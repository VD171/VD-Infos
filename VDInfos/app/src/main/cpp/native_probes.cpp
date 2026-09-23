/*
 * ======================================================================
 *  VD INFOS  ::  method debugger
 *  Read every device info by every method, then compare. A divergence is a hook.
 *
 *  Copyright (C) 2026  VD171
 *  SPDX-License-Identifier: AGPL-3.0-or-later
 *
 *  Free software under the GNU AGPL v3 or later. NETWORK COPYLEFT: run a
 *  modified version, even as a service, and you MUST offer its source.
 *
 *  Site           : https://vd171.ru
 *  Site           : https://vd.priv8.ru
 *  Source         : https://github.com/VD171/VD-Infos
 *  GitHub         : @VD171 https://github.com/VD171
 *  XDA-Developers : @VD171 https://xdaforums.com/m/vd171.4699873/
 *  Telegram       : @VD_Priv8 https://t.me/VD_Priv8
 *  Discord        : @VD.Priv8 https://discord.com/users/1296831918989639721
 *  E-mail         : vd.priv8@pm.me
 * ======================================================================
 */

#include <jni.h>
#include <string>
#include <cstring>
#include <cerrno>
#include <unistd.h>
#include <fcntl.h>
#include <sys/utsname.h>
#include <sys/statvfs.h>
#include <sys/system_properties.h>
#include <sstream>
#include <cstdio>
#include <cstdint>
#include <cstdlib>
#include <vector>
#include <unordered_set>
#include <algorithm>
#include <csignal>
#include <sys/wait.h>
#include <sys/syscall.h>
#include <sys/stat.h>
#include <dlfcn.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <poll.h>
#include <linux/netlink.h>
#include <linux/sock_diag.h>
#include <linux/inet_diag.h>

namespace {

jstring toJstring(JNIEnv* env, const std::string& s) {
    return env->NewStringUTF(s.c_str());
}

std::string readFileRaw(const char* path, size_t cap = 65536) {
    int fd = open(path, O_RDONLY | O_CLOEXEC);
    if (fd < 0) return std::string();
    std::string out;
    out.reserve(4096);
    char buf[4096];
    ssize_t n;
    while ((n = read(fd, buf, sizeof(buf))) > 0) {
        out.append(buf, static_cast<size_t>(n));
        if (out.size() >= cap) break;
    }
    close(fd);
    return out;
}

std::string trimNul(std::string s) {
    while (!s.empty() && (s.back() == '\n' || s.back() == '\0')) s.pop_back();
    return s;
}

static std::string safeReadCStr(const char* p, bool* terminated, size_t cap = 4096) {
    if (terminated) *terminated = false;
    if (p == nullptr) return std::string();
    int fds[2];
    if (pipe2(fds, O_CLOEXEC) != 0) return std::string();
    std::string out;
    size_t off = 0;
    while (off < cap) {
        size_t want = cap - off;
        if (want > 512) want = 512;
        ssize_t n = write(fds[1], p + off, want);
        if (n <= 0) break;
        char tmp[512];
        ssize_t rd = read(fds[0], tmp, static_cast<size_t>(n));
        if (rd <= 0) break;
        for (ssize_t i = 0; i < rd; ++i) {
            if (tmp[i] == '\0') {
                if (terminated) *terminated = true;
                close(fds[0]); close(fds[1]);
                return out;
            }
            out.push_back(tmp[i]);
        }
        off += static_cast<size_t>(rd);
        if (rd < n) break;
    }
    close(fds[0]); close(fds[1]);
    return out;
}

static void propCallback(void* cookie, const char* , const char* value,
                         uint32_t ) {
    if (value != nullptr) static_cast<std::string*>(cookie)->assign(value);
}

jstring nSysProp(JNIEnv* env, jobject, jstring jkey) {
    const char* key = env->GetStringUTFChars(jkey, nullptr);
    std::string value;
    const prop_info* pi = (key != nullptr) ? __system_property_find(key) : nullptr;
    if (pi != nullptr) __system_property_read_callback(pi, propCallback, &value);
    env->ReleaseStringUTFChars(jkey, key);
    if (value.empty()) return nullptr;
    return toJstring(env, value);
}

jstring nUname(JNIEnv* env, jobject) {
    struct utsname u {};
    if (uname(&u) != 0) return nullptr;
    std::string s = std::string(u.sysname) + "\t" + u.nodename + "\t" +
                    u.release + "\t" + u.version + "\t" + u.machine;
    return toJstring(env, s);
}

jstring nHostname(JNIEnv* env, jobject) {
    char host[256] = {0};
    if (gethostname(host, sizeof(host) - 1) != 0) return nullptr;
    return toJstring(env, std::string(host));
}

jstring nIfaceMac(JNIEnv* env, jobject, jstring jname) {
    const char* name = env->GetStringUTFChars(jname, nullptr);
    std::string path = std::string("/sys/class/net/") + name + "/address";
    env->ReleaseStringUTFChars(jname, name);
    std::string mac = trimNul(readFileRaw(path.c_str(), 64));
    if (mac.empty()) return nullptr;
    return toJstring(env, mac);
}

jstring nSysPropClassic(JNIEnv* env, jobject, jstring jkey) {
    const char* key = env->GetStringUTFChars(jkey, nullptr);
    char buf[PROP_VALUE_MAX] = {0};
    int n = __system_property_get(key, buf);
    env->ReleaseStringUTFChars(jkey, key);
    if (n <= 0) return nullptr;
    if (strstr(buf, "__system_property_read_callback") != nullptr) {
        return env->NewStringUTF("TOO_LONG_FOR_92B_API");
    }
    return env->NewStringUTF(buf);
}

jstring nExec(JNIEnv* env, jobject, jstring jcmd, jint cap) {
    const char* cmd = env->GetStringUTFChars(jcmd, nullptr);
    size_t limit = cap > 0 ? static_cast<size_t>(cap) : 8192;
    std::string full = "{ " + std::string(cmd) + " ; } 2>&1";
    env->ReleaseStringUTFChars(jcmd, cmd);

    FILE* f = popen(full.c_str(), "r");
    if (f == nullptr) return env->NewStringUTF("POPEN_FAILED");

    std::string data;
    char buf[4096];
    size_t n;
    while (data.size() < limit && (n = fread(buf, 1, sizeof(buf), f)) > 0) {
        data.append(buf, n);
    }
    int rc = pclose(f);

    while (!data.empty() && (data.back() == '\n' || data.back() == '\r' ||
                             data.back() == ' '  || data.back() == '\t')) {
        data.pop_back();
    }
    if (!data.empty()) return env->NewStringUTF(data.c_str());
    if (rc == -1)              return env->NewStringUTF("NO_STATUS");
    if (WIFSIGNALED(rc))       return env->NewStringUTF(
                                   ("SIGNAL:" + std::to_string(WTERMSIG(rc))).c_str());
    if (WIFEXITED(rc) && WEXITSTATUS(rc) != 0)
        return env->NewStringUTF(("EXIT:" + std::to_string(WEXITSTATUS(rc))).c_str());
    return env->NewStringUTF("");
}

jstring nReadFile(JNIEnv* env, jobject, jstring jpath, jint cap) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    std::string data = readFileRaw(path, cap > 0 ? static_cast<size_t>(cap) : 65536);
    env->ReleaseStringUTFChars(jpath, path);
    if (data.empty()) return nullptr;
    return toJstring(env, data);
}

jstring nReadFileOrReason(JNIEnv* env, jobject, jstring jpath, jint cap) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    size_t limit = cap > 0 ? static_cast<size_t>(cap) : 65536;
    int fd = open(path, O_RDONLY | O_CLOEXEC);
    std::string out;
    if (fd < 0) {
        switch (errno) {
            case EACCES:  out = "EACCES";  break;
            case ENOENT:  out = "ENOENT";  break;
            case EPERM:   out = "EPERM";   break;
            case EISDIR:  out = "EISDIR";  break;
            case ENOTDIR: out = "ENOTDIR"; break;
            case ELOOP:   out = "ELOOP";   break;
            case ENXIO:   out = "ENXIO";   break;
            case EINVAL:  out = "EINVAL";  break;
            default:      out = "errno=" + std::to_string(errno); break;
        }
    } else {
        std::string data;
        char buf[4096];
        ssize_t n;
        while (data.size() < limit && (n = read(fd, buf, sizeof(buf))) > 0) {
            data.append(buf, static_cast<size_t>(n));
        }
        close(fd);
        out = data.empty() ? "(empty)" : data;
    }
    env->ReleaseStringUTFChars(jpath, path);
    return toJstring(env, out);
}

jboolean nExists(JNIEnv* env, jobject, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    int r = access(path, F_OK);
    env->ReleaseStringUTFChars(jpath, path);
    return r == 0 ? JNI_TRUE : JNI_FALSE;
}

jstring nReadlink(JNIEnv* env, jobject, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    char buf[512] = {0};
    ssize_t n = readlink(path, buf, sizeof(buf) - 1);
    env->ReleaseStringUTFChars(jpath, path);
    if (n <= 0) return nullptr;
    return toJstring(env, std::string(buf, static_cast<size_t>(n)));
}

jstring nIds(JNIEnv* env, jobject) {
    std::string s = std::to_string(getuid()) + "\t" + std::to_string(geteuid()) + "\t" +
                    std::to_string(getgid()) + "\t" + std::to_string(getegid()) + "\t" +
                    std::to_string(getpid()) + "\t" + std::to_string(getppid());
    return toJstring(env, s);
}

jstring nStatfs(JNIEnv* env, jobject, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    struct statvfs st {};
    int r = statvfs(path, &st);
    env->ReleaseStringUTFChars(jpath, path);
    if (r != 0) return nullptr;
    unsigned long long total = static_cast<unsigned long long>(st.f_blocks) * st.f_frsize;
    unsigned long long free_ = static_cast<unsigned long long>(st.f_bfree) * st.f_frsize;
    unsigned long long avail = static_cast<unsigned long long>(st.f_bavail) * st.f_frsize;
    std::string s = std::to_string(total) + "\t" + std::to_string(free_) + "\t" + std::to_string(avail);
    return toJstring(env, s);
}

jstring nArch(JNIEnv* env, jobject) {
#if defined(__aarch64__)
    return toJstring(env, "arm64-v8a");
#elif defined(__arm__)
    return toJstring(env, "armeabi-v7a");
#elif defined(__x86_64__)
    return toJstring(env, "x86_64");
#elif defined(__i386__)
    return toJstring(env, "x86");
#else
    return toJstring(env, "unknown");
#endif
}

namespace propscan {

constexpr const char* kDirTag  = "/dev/__properties__/";
constexpr uint32_t    kMagic   = 0x504f5250u;
constexpr uint32_t    kVersion = 0xfc6ed0abu;
constexpr uint32_t    kLongFlag = 1u << 16;
constexpr size_t      kAlign    = 4;
constexpr size_t      kBackup   = PROP_VALUE_MAX;

struct AreaHeader { uint32_t bytes_used, serial, magic, version, reserved[28]; };
struct TrieNode   { uint32_t namelen, prop, left, right, children; };
struct PropRecord { uint32_t serial; char value[PROP_VALUE_MAX]; };
constexpr size_t kLongOffsetPos = 56;

inline size_t alignUp(size_t v) { return (v + (kAlign - 1)) & ~(kAlign - 1); }
inline bool fits(size_t off, size_t need, size_t used) { return off <= used && used - off >= need; }

long holesInArea(const uint8_t* data, size_t used) {
    std::vector<uint8_t> live(used, 0);
    auto mark = [&](uint32_t off, size_t size) -> bool {
        if (size == 0) return true;
        if (off % kAlign || size % kAlign) return false;
        if (off > used || size > used - off) return false;
        std::fill(live.begin() + off, live.begin() + off + size, 1);
        return true;
    };
    auto cstrlen = [&](uint32_t off) -> long {
        if (off >= used) return -1;
        const void* z = memchr(data + off, 0, used - off);
        return z ? static_cast<long>(static_cast<const uint8_t*>(z) - (data + off)) : -1;
    };

    if (!fits(0, sizeof(TrieNode), used)) return -1;
    const auto* root = reinterpret_cast<const TrieNode*>(data);
    if (root->namelen != 0) return -1;
    if (!mark(0, sizeof(TrieNode)) || !mark(sizeof(TrieNode), kBackup)) return -1;

    std::unordered_set<uint32_t> seenNode, seenProp;
    std::vector<uint32_t> nstack, pstack;
    auto pushNode = [&](uint32_t o) { if (o && seenNode.insert(o).second) nstack.push_back(o); };
    auto pushProp = [&](uint32_t o) { if (o && seenProp.insert(o).second) pstack.push_back(o); };
    pushNode(root->left); pushNode(root->right); pushNode(root->children); pushProp(root->prop);

    while (!nstack.empty()) {
        uint32_t off = nstack.back(); nstack.pop_back();
        if (!fits(off, sizeof(TrieNode), used)) return -1;
        const auto* n = reinterpret_cast<const TrieNode*>(data + off);
        long nl = cstrlen(off + sizeof(TrieNode));
        if (nl < 0 || static_cast<uint32_t>(nl) != n->namelen) return -1;
        if (!mark(off, alignUp(sizeof(TrieNode) + n->namelen + 1))) return -1;
        pushNode(n->left); pushNode(n->right); pushNode(n->children); pushProp(n->prop);
    }
    while (!pstack.empty()) {
        uint32_t off = pstack.back(); pstack.pop_back();
        if (!fits(off, sizeof(PropRecord), used)) return -1;
        const auto* pr = reinterpret_cast<const PropRecord*>(data + off);
        long nl = cstrlen(off + sizeof(PropRecord));
        if (nl < 0) return -1;
        if (!mark(off, alignUp(sizeof(PropRecord) + nl + 1))) return -1;
        if (pr->serial & kLongFlag) {
            uint32_t rel; memcpy(&rel, pr->value + kLongOffsetPos, sizeof(rel));
            if (rel < sizeof(PropRecord)) return -1;
            uint32_t lo = off + rel;
            if (lo >= used) return -1;
            long vl = cstrlen(lo);
            if (vl < 0 || !mark(lo, alignUp(vl + 1))) return -1;
        }
    }

    long holes = 0;
    for (size_t i = 0; i < used; ) {
        if (live[i]) { ++i; continue; }
        size_t start = i;
        while (i < used && !live[i]) ++i;
        size_t len = i - start;
        if (start % kAlign || len < kAlign || len % kAlign) return -1;
        ++holes;
    }
    return holes;
}

std::string scan() {
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    if (maps.empty()) return "AVAILABLE=0";

    int contexts = 0;
    long totalHoles = 0;
    std::vector<std::string> perCtx;
    std::unordered_set<std::string> seenCtx;

    size_t pos = 0;
    while (pos < maps.size()) {
        size_t eol = maps.find('\n', pos);
        if (eol == std::string::npos) eol = maps.size();
        std::string line = maps.substr(pos, eol - pos);
        pos = eol + 1;

        size_t tag = line.find(kDirTag);
        if (tag == std::string::npos) continue;
        std::string ctx = line.substr(tag + strlen(kDirTag));
        if (ctx == "properties_serial" || ctx == "property_info") continue;
        size_t sp = line.find(' ');
        if (sp == std::string::npos || sp + 1 >= line.size() || line[sp + 1] != 'r') continue;
        if (!seenCtx.insert(ctx).second) continue;

        char* dash = nullptr;
        unsigned long start = strtoul(line.c_str(), &dash, 16);
        if (!dash || *dash != '-') continue;
        unsigned long end = strtoul(dash + 1, nullptr, 16);
        if (end <= start) continue;
        size_t region = static_cast<size_t>(end - start);
        if (region < sizeof(AreaHeader)) continue;

        const auto* h = reinterpret_cast<const AreaHeader*>(static_cast<uintptr_t>(start));
        size_t dataMax = region - sizeof(AreaHeader);
        if (h->magic != kMagic || h->version != kVersion ||
            h->bytes_used > dataMax || h->bytes_used < sizeof(TrieNode) + kBackup) {
            continue;
        }
        long holes = holesInArea(reinterpret_cast<const uint8_t*>(h) + sizeof(AreaHeader), h->bytes_used);
        if (holes < 0) continue;
        ++contexts;
        if (holes > 0) {
            totalHoles += holes;
            perCtx.push_back(ctx + ": holes=" + std::to_string(holes));
        }
    }

    if (contexts == 0) return "AVAILABLE=0";
    std::ostringstream o;
    o << "AVAILABLE=1 CONTEXTS=" << contexts << " HOLES=" << totalHoles;
    for (const auto& c : perCtx) o << "\n" << c;
    return o.str();
}

}

jstring nPropAreaHoles(JNIEnv* env, jobject) {
    return toJstring(env, propscan::scan());
}

jboolean nRawExists(JNIEnv* env, jobject, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
#if defined(SYS_faccessat)
    long r = syscall(SYS_faccessat, AT_FDCWD, path, F_OK, 0);
#else
    long r = syscall(SYS_access, path, F_OK);
#endif
    env->ReleaseStringUTFChars(jpath, path);
    return r == 0 ? JNI_TRUE : JNI_FALSE;
}

#ifndef __NR_pidfd_open
#define __NR_pidfd_open 434
#endif
#ifndef __NR_close_range
#define __NR_close_range 436
#endif
#ifndef __NR_openat2
#define __NR_openat2 437
#endif
#ifndef __NR_faccessat2
#define __NR_faccessat2 439
#endif

static void onSigsys(int) { _exit(2); }

static int syscallPresent(long nr) {
    pid_t pid = fork();
    if (pid < 0) return -1;
    if (pid == 0) {
        struct sigaction sa {};
        sa.sa_handler = onSigsys;
        sigemptyset(&sa.sa_mask);
        sigaction(SIGSYS, &sa, nullptr);
        errno = 0;
        long r = syscall(nr, -1, -1, -1, -1, -1, -1);
        _exit((r >= 0 || errno != ENOSYS) ? 1 : 0);
    }
    int st = 0;
    if (waitpid(pid, &st, 0) < 0) return -1;
    if (!WIFEXITED(st)) return -1;
    int code = WEXITSTATUS(st);
    return (code == 2) ? -1 : (code ? 1 : 0);
}

jstring nKernelSpoof(JNIEnv* env, jobject) {
    struct utsname u {};
    int maj = -1, min = -1;
    if (uname(&u) == 0) sscanf(u.release, "%d.%d", &maj, &min);
    struct Probe { const char* name; int major; int minor; long nr; };
    const Probe probes[] = {
        {"pidfd_open",  5, 3, __NR_pidfd_open},
        {"openat2",     5, 6, __NR_openat2},
        {"faccessat2",  5, 8, __NR_faccessat2},
        {"close_range", 5, 9, __NR_close_range},
    };
    std::string claimed = (maj >= 0) ? (std::to_string(maj) + "." + std::to_string(min)) : "unknown";
    std::string contradict;
    int refused = 0;
    for (const auto& p : probes) {
        int present = syscallPresent(p.nr);
        if (present < 0) { refused++; continue; }
        if (present == 0) continue;
        bool older = (maj >= 0) && (maj < p.major || (maj == p.major && min < p.minor));
        if (older) {
            if (!contradict.empty()) contradict += ",";
            contradict += std::string(p.name) + "(>=" + std::to_string(p.major) + "." +
                          std::to_string(p.minor) + ")";
        }
    }
    const int total = (int) (sizeof(probes) / sizeof(probes[0]));
    std::string verdict;
    if (!contradict.empty()) verdict = "SPOOF present_too_new=" + contradict;
    else if (refused == total) verdict = "no_answer";
    else verdict = "coherent";
    if (refused > 0 && refused < total) verdict += " refused=" + std::to_string(refused);
    return toJstring(env, "uname=" + claimed + " " + verdict);
}

jstring nSelfPath(JNIEnv* env, jobject) {
    Dl_info info {};
    if (dladdr(reinterpret_cast<void*>(&nSelfPath), &info) == 0 || info.dli_fname == nullptr)
        return env->NewStringUTF("");
    bool ok = false;
    std::string name = safeReadCStr(info.dli_fname, &ok);
    if (!ok) return env->NewStringUTF("<unreadable-name>");
    return toJstring(env, name);
}

jstring nStatOwner(JNIEnv* env, jobject, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    struct stat st {};
    int r = stat(path, &st);
    env->ReleaseStringUTFChars(jpath, path);
    if (r != 0) return nullptr;
    return toJstring(env, std::to_string(st.st_uid) + "\t" + std::to_string(st.st_gid));
}

static int utf8Next(const unsigned char** pp, const unsigned char* end) {
    const unsigned char* p = *pp;
    unsigned char c = p[0];
    if (c < 0x80) { *pp = p + 1; return c; }
    if ((c & 0xE0) == 0xC0 && p + 1 < end && (p[1] & 0xC0) == 0x80) {
        *pp = p + 2; return ((c & 0x1F) << 6) | (p[1] & 0x3F);
    }
    if ((c & 0xF0) == 0xE0 && p + 2 < end && (p[1] & 0xC0) == 0x80 && (p[2] & 0xC0) == 0x80) {
        *pp = p + 3; return ((c & 0x0F) << 12) | ((p[1] & 0x3F) << 6) | (p[2] & 0x3F);
    }
    if ((c & 0xF8) == 0xF0 && p + 3 < end && (p[1] & 0xC0) == 0x80 && (p[2] & 0xC0) == 0x80 && (p[3] & 0xC0) == 0x80) {
        *pp = p + 4; return ((c & 0x07) << 18) | ((p[1] & 0x3F) << 12) | ((p[2] & 0x3F) << 6) | (p[3] & 0x3F);
    }
    *pp = p + 1; return -1;
}
static bool inRanges(int cp, const int* r, int n) {
    for (int i = 0; i + 1 < n; i += 2) if (cp >= r[i] && cp <= r[i + 1]) return true;
    return false;
}
static bool hasInvisible(const char* name, const int* ranges, int nr) {
    const unsigned char* p = reinterpret_cast<const unsigned char*>(name);
    const unsigned char* end = p + strlen(name);
    while (p < end) {
        int cp = utf8Next(&p, end);
        if (cp > 0 && inRanges(cp, ranges, nr)) return true;
    }
    return false;
}
jstring nDirZeroWidth(JNIEnv* env, jobject, jstring jdir, jintArray jranges) {
    const char* dir = env->GetStringUTFChars(jdir, nullptr);
    int fd = open(dir, O_RDONLY | O_DIRECTORY | O_CLOEXEC);
    env->ReleaseStringUTFChars(jdir, dir);
    if (fd < 0) {
        std::string t = (errno == EACCES) ? "EACCES"
                      : (errno == ENOENT) ? "ENOENT"
                      : ("errno=" + std::to_string(errno));
        return toJstring(env, t);
    }
    struct linux_dirent64 {
        uint64_t d_ino; int64_t d_off; unsigned short d_reclen; unsigned char d_type; char d_name[];
    };
    jsize nr = env->GetArrayLength(jranges);
    jint* ranges = env->GetIntArrayElements(jranges, nullptr);
    char buf[8192];
    bool found = false;
    for (;;) {
        long n = syscall(SYS_getdents64, fd, buf, sizeof(buf));
        if (n <= 0) break;
        for (long off = 0; off < n;) {
            auto* d = reinterpret_cast<linux_dirent64*>(buf + off);
            if (hasInvisible(d->d_name, reinterpret_cast<const int*>(ranges), static_cast<int>(nr))) { found = true; break; }
            off += d->d_reclen;
        }
        if (found) break;
    }
    env->ReleaseIntArrayElements(jranges, ranges, JNI_ABORT);
    close(fd);
    return toJstring(env, found ? "1" : "0");
}

jstring nSockDiag(JNIEnv* env, jobject) {
    int fd = socket(AF_NETLINK, SOCK_RAW | SOCK_CLOEXEC, NETLINK_SOCK_DIAG);
    if (fd < 0) {
        if (errno == EACCES || errno == EPERM) return toJstring(env, "denied");
        return toJstring(env, std::string("ERR(socket:") + std::to_string(errno) + ")");
    }
    struct Req { struct nlmsghdr nlh; struct inet_diag_req_v2 req; } r{};
    r.nlh.nlmsg_len = sizeof(r);
    r.nlh.nlmsg_type = SOCK_DIAG_BY_FAMILY;
    r.nlh.nlmsg_flags = NLM_F_REQUEST | NLM_F_DUMP;
    r.nlh.nlmsg_seq = 1;
    r.req.sdiag_family = AF_INET;
    r.req.sdiag_protocol = IPPROTO_TCP;
    r.req.idiag_states = 0xffffffffu;
    struct sockaddr_nl sa{};
    sa.nl_family = AF_NETLINK;
    ssize_t s = sendto(fd, &r, sizeof(r), 0,
                       reinterpret_cast<struct sockaddr*>(&sa), sizeof(sa));
    if (s < 0) {
        int e = errno; close(fd);
        if (e == EACCES || e == EPERM) return toJstring(env, "denied");
        return toJstring(env, std::string("ERR(send:") + std::to_string(e) + ")");
    }
    struct pollfd pfd { fd, POLLIN, 0 };
    if (poll(&pfd, 1, 400) <= 0) { close(fd); return toJstring(env, "no-reply"); }
    char buf[8192];
    ssize_t got = recv(fd, buf, sizeof(buf), 0);
    close(fd);
    if (got < 0) {
        if (errno == EACCES || errno == EPERM) return toJstring(env, "denied");
        return toJstring(env, std::string("ERR(recv:") + std::to_string(errno) + ")");
    }
    int len = static_cast<int>(got);
    for (auto* h = reinterpret_cast<struct nlmsghdr*>(buf);
         NLMSG_OK(h, len); h = NLMSG_NEXT(h, len)) {
        if (h->nlmsg_type == NLMSG_ERROR) {
            auto* err = static_cast<struct nlmsgerr*>(NLMSG_DATA(h));
            if (err->error == -EACCES || err->error == -EPERM)
                return toJstring(env, "denied");
            return toJstring(env, std::string("nlerr:") + std::to_string(err->error));
        }
        if (h->nlmsg_type == SOCK_DIAG_BY_FAMILY) return toJstring(env, "leak");
        if (h->nlmsg_type == NLMSG_DONE) return toJstring(env, "leak-empty");
    }
    return toJstring(env, "leak-empty");
}

const JNINativeMethod kMethods[] = {
    {"nSysProp",  "(Ljava/lang/String;)Ljava/lang/String;",  reinterpret_cast<void*>(nSysProp)},
    {"nUname",    "()Ljava/lang/String;",                     reinterpret_cast<void*>(nUname)},
    {"nHostname", "()Ljava/lang/String;",                     reinterpret_cast<void*>(nHostname)},
    {"nIfaceMac", "(Ljava/lang/String;)Ljava/lang/String;",   reinterpret_cast<void*>(nIfaceMac)},
    {"nExec", "(Ljava/lang/String;I)Ljava/lang/String;", reinterpret_cast<void*>(nExec)},
    {"nSysPropClassic", "(Ljava/lang/String;)Ljava/lang/String;", reinterpret_cast<void*>(nSysPropClassic)},
    {"nReadFile", "(Ljava/lang/String;I)Ljava/lang/String;",  reinterpret_cast<void*>(nReadFile)},
    {"nReadFileOrReason", "(Ljava/lang/String;I)Ljava/lang/String;", reinterpret_cast<void*>(nReadFileOrReason)},
    {"nExists",   "(Ljava/lang/String;)Z",                    reinterpret_cast<void*>(nExists)},
    {"nReadlink", "(Ljava/lang/String;)Ljava/lang/String;",   reinterpret_cast<void*>(nReadlink)},
    {"nIds",      "()Ljava/lang/String;",                     reinterpret_cast<void*>(nIds)},
    {"nStatfs",   "(Ljava/lang/String;)Ljava/lang/String;",   reinterpret_cast<void*>(nStatfs)},
    {"nArch",     "()Ljava/lang/String;",                     reinterpret_cast<void*>(nArch)},
    {"nPropAreaHoles", "()Ljava/lang/String;",              reinterpret_cast<void*>(nPropAreaHoles)},
    {"nRawExists",   "(Ljava/lang/String;)Z",               reinterpret_cast<void*>(nRawExists)},
    {"nKernelSpoof", "()Ljava/lang/String;",                reinterpret_cast<void*>(nKernelSpoof)},
    {"nSelfPath",    "()Ljava/lang/String;",                reinterpret_cast<void*>(nSelfPath)},
    {"nStatOwner",   "(Ljava/lang/String;)Ljava/lang/String;", reinterpret_cast<void*>(nStatOwner)},
    {"nDirZeroWidth","(Ljava/lang/String;[I)Ljava/lang/String;", reinterpret_cast<void*>(nDirZeroWidth)},
    {"nSockDiag",   "()Ljava/lang/String;",                   reinterpret_cast<void*>(nSockDiag)},
};

}

extern "C" JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    jclass cls = env->FindClass("ru/vd171/vdinfos/probe/NativeBridge");
    if (cls == nullptr) return JNI_ERR;
    if (env->RegisterNatives(cls, kMethods,
                             sizeof(kMethods) / sizeof(kMethods[0])) != JNI_OK) {
        return JNI_ERR;
    }
    return JNI_VERSION_1_6;
}
