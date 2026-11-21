/*
 * ======================================================================
 *  VD INFOS  ::  method debugger
 *  Read every device info by every method, then compare.
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
 *  XDA thread     : https://xdaforums.com/t/VD-Infos.4097379/
 *  Telegram       : @VD_Priv8 https://t.me/VD_Priv8
 *  Channel        : @Priv8_VD https://t.me/Priv8_VD
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
#include <sys/statfs.h>
#include <sys/mman.h>
#include <sys/system_properties.h>
#include <sstream>
#include <cstdio>
#include <cstdint>
#include <cstdlib>
#include <vector>
#include <unordered_set>
#include <algorithm>
#include <csignal>
#include <csetjmp>
#include <pthread.h>
#include <sys/wait.h>
#include <sys/syscall.h>
#include <sys/resource.h>
#include <sys/stat.h>
#include <dlfcn.h>
#include <link.h>
#include <elf.h>
#include <set>
#include <sys/sysmacros.h>
#include <arpa/inet.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <poll.h>
#include <linux/netlink.h>
#include <linux/sock_diag.h>
#include <linux/inet_diag.h>
#include <EGL/egl.h>
#include <GLES2/gl2.h>

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

#ifndef __NR_openat2
#define __NR_openat2 437
#endif
#ifndef __NR_faccessat2
#define __NR_faccessat2 439
#endif
#ifndef __NR_statfs
#define __NR_statfs 43
#endif
#ifndef __NR_faccessat
#define __NR_faccessat 48
#endif
#ifndef __NR_openat
#define __NR_openat 56
#endif
#ifndef __NR_readlinkat
#define __NR_readlinkat 78
#endif
#ifndef __NR_newfstatat
#define __NR_newfstatat 79
#endif
#ifndef __NR_statx
#define __NR_statx 291
#endif

static __thread sigjmp_buf g_sysJmp;
static __thread volatile sig_atomic_t g_sysArmed = 0;
static pthread_once_t g_sysOnce = PTHREAD_ONCE_INIT;

static void sigsysTrap(int, siginfo_t*, void*) {
    if (g_sysArmed) { g_sysArmed = 0; siglongjmp(g_sysJmp, 1); }
}

static void installSigsys() {
    struct sigaction sa;
    memset(&sa, 0, sizeof(sa));
    sa.sa_sigaction = sigsysTrap;
    sa.sa_flags = SA_SIGINFO | SA_NODEFER;
    sigemptyset(&sa.sa_mask);
    sigaction(SIGSYS, &sa, nullptr);
}

static void onSigsys(int) { _exit(2); }

static int rawSyscallGuarded(long* out, long nr, long a0, long a1, long a2, long a3, long a4 = 0) {
    pthread_once(&g_sysOnce, installSigsys);
    if (sigsetjmp(g_sysJmp, 1) != 0) return 1;
    g_sysArmed = 1;
    errno = 0;
    long r = syscall(nr, a0, a1, a2, a3, a4);
    g_sysArmed = 0;
    *out = r;
    return 0;
}

static std::string presenceOf(int ret, int err) {
    if (ret == 0) return "present";
    switch (err) {
        case ENOENT: case ENOTDIR: return "absent";
        case EACCES: case EPERM:   return "present(denied)";
        case ENOSYS:               return "n/a";
        default: return "errno" + std::to_string(err);
    }
}

jstring nStatPresence(JNIEnv* env, jobject, jstring jpath, jint method) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    std::string result;
    int ret = -1, err = 0;
    switch (method) {
        case 0: {
            using statx_fn = int (*)(int, const char*, int, unsigned, void*);
            static statx_fn statxp = reinterpret_cast<statx_fn>(dlsym(RTLD_DEFAULT, "statx"));
            if (!statxp) { result = "nosym"; break; }
            unsigned char buf[256];
            errno = 0;
            ret = statxp(AT_FDCWD, path, 0, 0x000007ffU, buf);
            result = presenceOf(ret, errno);
            break;
        }
        case 1: {
            struct stat st {};
            errno = 0;
            ret = fstatat(AT_FDCWD, path, &st, 0);
            result = presenceOf(ret, errno);
            break;
        }
        case 2: {
            errno = 0;
            ret = faccessat(AT_FDCWD, path, F_OK, 0);
            result = presenceOf(ret, errno);
            break;
        }
        case 3: {
            struct open_how_t { uint64_t flags; uint64_t mode; uint64_t resolve; } how {};
            how.flags = static_cast<uint64_t>(O_PATH | O_CLOEXEC);
            long fd = -1;
            int blocked = rawSyscallGuarded(&fd, __NR_openat2, AT_FDCWD,
                                            reinterpret_cast<long>(path),
                                            reinterpret_cast<long>(&how), sizeof(how));
            if (blocked) { result = "blocked"; break; }
            err = errno;
            if (fd >= 0) { close(static_cast<int>(fd)); result = "present"; }
            else result = presenceOf(-1, err);
            break;
        }
        case 4: {
            char lb[64];
            errno = 0;
            ssize_t n = readlinkat(AT_FDCWD, path, lb, sizeof(lb));
            err = errno;
            if (n >= 0 || err == EINVAL) result = "present";
            else result = presenceOf(-1, err);
            break;
        }
        case 5: {
            struct statvfs vf {};
            errno = 0;
            ret = statvfs(path, &vf);
            result = presenceOf(ret, errno);
            break;
        }
        case 6: {
            using stat64_fn = int (*)(const char*, void*);
            static stat64_fn s64 = reinterpret_cast<stat64_fn>(dlsym(RTLD_DEFAULT, "stat64"));
            if (!s64) { result = "nosym"; break; }
            unsigned char buf[256];
            errno = 0;
            ret = s64(path, buf);
            result = presenceOf(ret, errno);
            break;
        }
        case 7: {
            long r = -1;
            int blocked = rawSyscallGuarded(&r, __NR_faccessat2, AT_FDCWD,
                                            reinterpret_cast<long>(path), F_OK, 0);
            if (blocked) { result = "blocked"; break; }
            result = presenceOf(static_cast<int>(r), errno);
            break;
        }
        case 8: {
            struct stat st {};
            errno = 0;
            ret = stat(path, &st);
            result = presenceOf(ret, errno);
            break;
        }
        case 9: {
            struct stat st {};
            errno = 0;
            ret = lstat(path, &st);
            result = presenceOf(ret, errno);
            break;
        }
        case 10: {
            errno = 0;
            ret = access(path, F_OK);
            result = presenceOf(ret, errno);
            break;
        }
        case 11: {
            struct statfs sf {};
            errno = 0;
            ret = statfs(path, &sf);
            result = presenceOf(ret, errno);
            break;
        }
        case 12: {
            using lstat64_fn = int (*)(const char*, void*);
            static lstat64_fn l64 = reinterpret_cast<lstat64_fn>(dlsym(RTLD_DEFAULT, "lstat64"));
            if (!l64) { result = "nosym"; break; }
            unsigned char buf[256];
            errno = 0;
            ret = l64(path, buf);
            result = presenceOf(ret, errno);
            break;
        }
        case 13: {
            struct stat st {};
            errno = 0;
            ret = fstatat(AT_FDCWD, path, &st, AT_SYMLINK_NOFOLLOW);
            result = presenceOf(ret, errno);
            break;
        }
        case 14: {
            errno = 0;
            int fd = open(path, O_PATH | O_CLOEXEC);
            err = errno;
            if (fd >= 0) {
                struct stat st {};
                int fr = fstat(fd, &st);
                int fe = errno;
                close(fd);
                result = (fr == 0) ? "present" : presenceOf(-1, fe);
            } else {
                result = presenceOf(-1, err);
            }
            break;
        }
        case 15: {
            using eaccess_fn = int (*)(const char*, int);
            static eaccess_fn ea = reinterpret_cast<eaccess_fn>(dlsym(RTLD_DEFAULT, "euidaccess"));
            if (!ea) ea = reinterpret_cast<eaccess_fn>(dlsym(RTLD_DEFAULT, "eaccess"));
            if (!ea) { result = "nosym"; break; }
            errno = 0;
            ret = ea(path, F_OK);
            result = presenceOf(ret, errno);
            break;
        }
        case 16: {
            errno = 0;
            ret = faccessat(AT_FDCWD, path, F_OK, AT_EACCESS);
            result = presenceOf(ret, errno);
            break;
        }
        case 17: {
            using statfs64_fn = int (*)(const char*, void*);
            static statfs64_fn sf64 = reinterpret_cast<statfs64_fn>(dlsym(RTLD_DEFAULT, "statfs64"));
            if (!sf64) { result = "nosym"; break; }
            unsigned char buf[128];
            errno = 0;
            ret = sf64(path, buf);
            result = presenceOf(ret, errno);
            break;
        }
        case 18: {
            struct file_handle_t { unsigned int handle_bytes; int handle_type; unsigned char f_handle[128]; };
            using nth_fn = int (*)(int, const char*, void*, int*, int);
            static nth_fn nth = reinterpret_cast<nth_fn>(dlsym(RTLD_DEFAULT, "name_to_handle_at"));
            if (!nth) { result = "nosym"; break; }
            file_handle_t fh {};
            fh.handle_bytes = sizeof(fh.f_handle);
            int mount_id = 0;
            errno = 0;
            ret = nth(AT_FDCWD, path, &fh, &mount_id, 0);
            err = errno;
            if (ret == 0 || err == EOVERFLOW) result = "present";
            else result = presenceOf(-1, err);
            break;
        }
        case 19: {
            struct stat st {};
            long r = -1;
            int blocked = rawSyscallGuarded(&r, __NR_newfstatat, AT_FDCWD,
                                            reinterpret_cast<long>(path),
                                            reinterpret_cast<long>(&st), 0);
            if (blocked) { result = "blocked"; break; }
            result = presenceOf(static_cast<int>(r), errno);
            break;
        }
        case 20: {
            struct stat st {};
            long r = -1;
            int blocked = rawSyscallGuarded(&r, __NR_newfstatat, AT_FDCWD,
                                            reinterpret_cast<long>(path),
                                            reinterpret_cast<long>(&st), AT_SYMLINK_NOFOLLOW);
            if (blocked) { result = "blocked"; break; }
            result = presenceOf(static_cast<int>(r), errno);
            break;
        }
        case 21: {
            long r = -1;
            int blocked = rawSyscallGuarded(&r, __NR_faccessat, AT_FDCWD,
                                            reinterpret_cast<long>(path), F_OK, 0);
            if (blocked) { result = "blocked"; break; }
            result = presenceOf(static_cast<int>(r), errno);
            break;
        }
        case 22: {
            struct statfs sf {};
            long r = -1;
            int blocked = rawSyscallGuarded(&r, __NR_statfs,
                                            reinterpret_cast<long>(path),
                                            reinterpret_cast<long>(&sf), 0, 0);
            if (blocked) { result = "blocked"; break; }
            result = presenceOf(static_cast<int>(r), errno);
            break;
        }
        case 23: {
            unsigned char buf[256];
            long r = -1;
            int blocked = rawSyscallGuarded(&r, __NR_statx, AT_FDCWD,
                                            reinterpret_cast<long>(path), 0, 0x000007ffU,
                                            reinterpret_cast<long>(buf));
            if (blocked) { result = "blocked"; break; }
            result = presenceOf(static_cast<int>(r), errno);
            break;
        }
        case 24: {
            long fd = -1;
            int blocked = rawSyscallGuarded(&fd, __NR_openat, AT_FDCWD,
                                            reinterpret_cast<long>(path),
                                            O_PATH | O_CLOEXEC, 0);
            if (blocked) { result = "blocked"; break; }
            err = errno;
            if (fd >= 0) { close(static_cast<int>(fd)); result = "present"; }
            else result = presenceOf(-1, err);
            break;
        }
        case 25: {
            char lb[64];
            long r = -1;
            int blocked = rawSyscallGuarded(&r, __NR_readlinkat, AT_FDCWD,
                                            reinterpret_cast<long>(path),
                                            reinterpret_cast<long>(lb), sizeof(lb));
            if (blocked) { result = "blocked"; break; }
            err = errno;
            if (r >= 0 || err == EINVAL) result = "present";
            else result = presenceOf(-1, err);
            break;
        }
        default: result = "n/a";
    }
    env->ReleaseStringUTFChars(jpath, path);
    return toJstring(env, result);
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
#ifndef IOPRIO_WHO_USER
#define IOPRIO_WHO_USER 3
#endif
#ifndef __NR_ioprio_get
#if defined(__aarch64__)
#define __NR_ioprio_get 31
#elif defined(__arm__)
#define __NR_ioprio_get 315
#elif defined(__x86_64__)
#define __NR_ioprio_get 252
#elif defined(__i386__)
#define __NR_ioprio_get 290
#endif
#endif
#ifndef __NR_ioprio_set
#if defined(__aarch64__)
#define __NR_ioprio_set 30
#elif defined(__arm__)
#define __NR_ioprio_set 314
#elif defined(__x86_64__)
#define __NR_ioprio_set 251
#elif defined(__i386__)
#define __NR_ioprio_set 289
#endif
#endif

static void onFatalProbe(int) { _exit(2); }

static int syscallPresent(long nr) {
    pid_t pid = fork();
    if (pid < 0) return -1;
    if (pid == 0) {
        struct sigaction sa {};
        sa.sa_handler = onFatalProbe;
        sigemptyset(&sa.sa_mask);
        const int fatal[] = { SIGSYS, SIGSEGV, SIGBUS, SIGILL, SIGABRT };
        for (int s : fatal) sigaction(s, &sa, nullptr);
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

jstring nGetGroups(JNIEnv* env, jobject) {
    int n = getgroups(0, nullptr);
    if (n < 0) return toJstring(env, "err");
    if (n == 0) return toJstring(env, "");
    std::vector<gid_t> g(static_cast<size_t>(n));
    int r = getgroups(n, g.data());
    if (r < 0) return toJstring(env, "err");
    std::sort(g.begin(), g.end());
    std::string out;
    for (gid_t x : g) { if (!out.empty()) out += ","; out += std::to_string(x); }
    return toJstring(env, out);
}

jstring nAtexitDrift(JNIEnv* env, jobject) {
    const char* syms[] = {"__cxa_atexit", "__cxa_finalize", "__cxa_thread_atexit_impl"};
    std::string out;
    for (const char* s : syms) {
        void* p = dlsym(RTLD_DEFAULT, s);
        if (p == nullptr) continue;
        Dl_info info {};
        if (dladdr(p, &info) == 0 || info.dli_fname == nullptr) continue;
        bool ok = false;
        std::string owner = safeReadCStr(info.dli_fname, &ok);
        if (!ok) continue;
        if (owner.find("libc.so") == std::string::npos) {
            if (!out.empty()) out += " ";
            out += std::string(s) + "@" + owner;
        }
    }
    return toJstring(env, out.empty() ? std::string("clean") : ("DRIFT " + out));
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

jstring nLibcIdentity(JNIEnv* env, jobject) {
    void* sym = dlsym(RTLD_DEFAULT, "__cxa_atexit");
    Dl_info info {};
    if (sym == nullptr || dladdr(sym, &info) == 0 || info.dli_fname == nullptr)
        return toJstring(env, "n/a (libc entry not resolvable)");
    std::string path(info.dli_fname);
    std::string base = path.substr(path.find_last_of('/') + 1);
    if (base != "libc.so")
        return toJstring(env, "n/a (entry not in libc.so)");
    struct stat st {};
    if (stat(path.c_str(), &st) != 0)
        return toJstring(env, "n/a (cannot stat libc)");
    unsigned long long fileIno = (unsigned long long) st.st_ino;
    unsigned fileMaj = major(st.st_dev), fileMin = minor(st.st_dev);
    unsigned long long addr = (unsigned long long) reinterpret_cast<uintptr_t>(sym);
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    std::string mapline;
    size_t pos = 0;
    while (pos < maps.size()) {
        size_t eol = maps.find('\n', pos);
        if (eol == std::string::npos) eol = maps.size();
        std::string line = maps.substr(pos, eol - pos);
        pos = eol + 1;
        unsigned long long s = 0, e = 0;
        if (sscanf(line.c_str(), "%llx-%llx", &s, &e) == 2 && addr >= s && addr < e) { mapline = line; break; }
    }
    if (mapline.empty())
        return toJstring(env, "n/a (no mapping for libc entry)");
    char perms[8] = {0}; unsigned long long off = 0, ino = 0; unsigned maj = 0, mn = 0;
    int n = sscanf(mapline.c_str(), "%*llx-%*llx %7s %llx %x:%x %llu", perms, &off, &maj, &mn, &ino);
    if (n < 5)
        return toJstring(env, "n/a (map parse failed)");
    if (ino == fileIno && maj == fileMaj && mn == fileMin)
        return toJstring(env, "clean");
    return toJstring(env, "mapping dev=" + std::to_string(maj) + ":" + std::to_string(mn) + " inode=" + std::to_string(ino) +
        " != file dev=" + std::to_string(fileMaj) + ":" + std::to_string(fileMin) + " inode=" + std::to_string(fileIno));
}

jstring nStatOwner(JNIEnv* env, jobject, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    struct stat st {};
    int r = stat(path, &st);
    env->ReleaseStringUTFChars(jpath, path);
    if (r != 0) return nullptr;
    return toJstring(env, std::to_string(st.st_uid) + "\t" + std::to_string(st.st_gid));
}

jstring nStatMode(JNIEnv* env, jobject, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    struct stat st {};
    int r = stat(path, &st);
    env->ReleaseStringUTFChars(jpath, path);
    if (r != 0) return nullptr;
    return toJstring(env, std::to_string(st.st_uid) + "\t" + std::to_string(st.st_gid) + "\t" +
                          std::to_string(static_cast<unsigned>(st.st_mode)));
}

static bool diskFuncBytes(const char* lib, const char* sym, uint8_t* out, size_t n, uint64_t* outVaddr = nullptr) {
    int fd = open(lib, O_RDONLY | O_CLOEXEC);
    if (fd < 0) return false;
    bool ok = false;
    do {
        Elf64_Ehdr eh{};
        if (pread(fd, &eh, sizeof(eh), 0) != (ssize_t) sizeof(eh)) break;
        if (memcmp(eh.e_ident, ELFMAG, SELFMAG) != 0 || eh.e_ident[EI_CLASS] != ELFCLASS64) break;
        std::vector<Elf64_Shdr> sh(eh.e_shnum);
        if (pread(fd, sh.data(), sizeof(Elf64_Shdr) * eh.e_shnum, eh.e_shoff) !=
            (ssize_t) (sizeof(Elf64_Shdr) * eh.e_shnum)) break;
        int dynsym = -1, dynstr = -1;
        for (int i = 0; i < eh.e_shnum; i++)
            if (sh[i].sh_type == SHT_DYNSYM) { dynsym = i; dynstr = (int) sh[i].sh_link; }
        if (dynsym < 0 || dynstr < 0 || dynstr >= eh.e_shnum) break;
        std::vector<Elf64_Phdr> ph(eh.e_phnum);
        if (pread(fd, ph.data(), sizeof(Elf64_Phdr) * eh.e_phnum, eh.e_phoff) !=
            (ssize_t) (sizeof(Elf64_Phdr) * eh.e_phnum)) break;
        std::string str(sh[dynstr].sh_size, '\0');
        if (pread(fd, &str[0], sh[dynstr].sh_size, sh[dynstr].sh_offset) != (ssize_t) sh[dynstr].sh_size) break;
        size_t cnt = sh[dynsym].sh_size / sizeof(Elf64_Sym);
        uint64_t vaddr = 0;
        for (size_t i = 0; i < cnt && !vaddr; i++) {
            Elf64_Sym s{};
            if (pread(fd, &s, sizeof(s), sh[dynsym].sh_offset + i * sizeof(s)) != (ssize_t) sizeof(s)) continue;
            if (s.st_value == 0 || s.st_name >= str.size()) continue;
            if (strcmp(str.c_str() + s.st_name, sym) == 0) vaddr = s.st_value;
        }
        if (!vaddr) break;
        if (outVaddr) *outVaddr = vaddr;
        for (auto& p : ph) {
            if (p.p_type == PT_LOAD && vaddr >= p.p_vaddr && vaddr < p.p_vaddr + p.p_filesz) {
                if (pread(fd, out, n, (off_t) (vaddr - p.p_vaddr + p.p_offset)) == (ssize_t) n) ok = true;
                break;
            }
        }
    } while (false);
    close(fd);
    return ok;
}

#if defined(__aarch64__)
static bool arm64Trampoline(const uint32_t* c) {
    uint32_t w = c[0];
    if ((w & 0xFC000000u) == 0x14000000u) return true;
    if ((w & 0xFF000000u) == 0x58000000u) { uint32_t rt = w & 0x1F; if (rt == 16 || rt == 17) return true; }
    if (w == 0xD61F0200u || w == 0xD61F0220u) return true;
    return false;
}
#endif

struct LibBaseCtx { const char* soname; uintptr_t base; };
static int libBaseCb(struct dl_phdr_info* info, size_t, void* data) {
    auto* c = reinterpret_cast<LibBaseCtx*>(data);
    if (info->dlpi_name && strstr(info->dlpi_name, c->soname)) { c->base = (uintptr_t) info->dlpi_addr; return 1; }
    return 0;
}
static uintptr_t libLoadBias(const char* soname) {
    LibBaseCtx c{soname, 0};
    dl_iterate_phdr(libBaseCb, &c);
    return c.base;
}

struct HookTarget { const char* display; const char* sym; const char* soname; const char* const* libs; };

jstring nInlineHookScan(JNIEnv* env, jobject) {
    static const char* kLibc[] = {"/apex/com.android.runtime/lib64/bionic/libc.so",
                                  "/apex/com.android.runtime/lib/bionic/libc.so", nullptr};
    static const char* kBinder[] = {"/system/lib64/libbinder.so", "/system/lib/libbinder.so", nullptr};
    static const char* kGui[] = {"/system/lib64/libgui.so", "/system/lib/libgui.so", nullptr};
    static const HookTarget targets[] = {
        {"access", "access", nullptr, kLibc},
        {"faccessat", "faccessat", nullptr, kLibc},
        {"stat", "stat", nullptr, kLibc},
        {"lstat", "lstat", nullptr, kLibc},
        {"open", "open", nullptr, kLibc},
        {"openat", "openat", nullptr, kLibc},
        {"syscall", "syscall", nullptr, kLibc},
        {"getifaddrs", "getifaddrs", nullptr, kLibc},
        {"newfstatat", "newfstatat", nullptr, kLibc},
        {"fstatat", "fstatat", nullptr, kLibc},
        {"statx", "statx", nullptr, kLibc},
        {"bpbinder_transact", "_ZN7android8BpBinder8transactEjRKNS_6ParcelEPS1_j", "libbinder.so", kBinder},
        {"bbinder_transact", "_ZN7android7BBinder8transactEjRKNS_6ParcelEPS1_j", "libbinder.so", kBinder},
        {"surface_setflags", "_ZN7android21SurfaceComposerClient11Transaction8setFlagsERKNS_2spINS_14SurfaceControlEEEjj", "libgui.so", kGui},
    };
    std::string detail;
    int hooked = 0;
    for (auto& t : targets) {
        uint8_t disk[16];
        uint64_t vaddr = 0;
        bool haveDisk = false;
        for (int l = 0; t.libs[l] && !haveDisk; l++) haveDisk = diskFuncBytes(t.libs[l], t.sym, disk, 16, &vaddr);
        void* addr = dlsym(RTLD_DEFAULT, t.sym);
        if (!addr && t.soname) {
            void* h = dlopen(t.soname, RTLD_NOLOAD | RTLD_NOW);
            if (h) addr = dlsym(h, t.sym);
        }
        if (!addr && t.soname && vaddr) {
            uintptr_t base = libLoadBias(t.soname);
            if (base) addr = reinterpret_cast<void*>(base + vaddr);
        }
        std::string st;
        if (!addr) {
            st = "nosym";
        } else {
            bool tramp = false;
#if defined(__aarch64__)
            tramp = arm64Trampoline(reinterpret_cast<const uint32_t*>(addr));
#endif
            bool diff = haveDisk && memcmp(addr, disk, 16) != 0;
            if (tramp) st = "trampoline";
            else if (diff) st = "patched";
            else if (!haveDisk) st = "clean?";
            else st = "clean";
            if (tramp || diff) hooked++;
        }
        if (!detail.empty()) detail += ",";
        detail += std::string(t.display) + ":" + st;
    }
    return toJstring(env, std::to_string(hooked) + "\t" + detail);
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

jstring nStatMeta(JNIEnv* env, jobject, jstring jpath) {
    const char* path = env->GetStringUTFChars(jpath, nullptr);
    struct stat st {};
    int r = stat(path, &st);
    env->ReleaseStringUTFChars(jpath, path);
    if (r != 0) return nullptr;
    return toJstring(env, std::to_string(static_cast<long long>(st.st_mtime)) + "\t" +
                              std::to_string(static_cast<long long>(st.st_size)));
}

jstring nGpuInfo(JNIEnv* env, jobject) {
    EGLDisplay dpy = eglGetDisplay(EGL_DEFAULT_DISPLAY);
    if (dpy == EGL_NO_DISPLAY) return nullptr;
    if (!eglInitialize(dpy, nullptr, nullptr)) return nullptr;
    const EGLint cfgAttr[] = {
        EGL_SURFACE_TYPE, EGL_PBUFFER_BIT,
        EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
        EGL_RED_SIZE, 8, EGL_GREEN_SIZE, 8, EGL_BLUE_SIZE, 8,
        EGL_NONE,
    };
    EGLConfig cfg;
    EGLint num = 0;
    if (!eglChooseConfig(dpy, cfgAttr, &cfg, 1, &num) || num < 1) { eglTerminate(dpy); return nullptr; }
    const EGLint pbAttr[] = { EGL_WIDTH, 1, EGL_HEIGHT, 1, EGL_NONE };
    EGLSurface surf = eglCreatePbufferSurface(dpy, cfg, pbAttr);
    if (surf == EGL_NO_SURFACE) { eglTerminate(dpy); return nullptr; }
    const EGLint ctxAttr[] = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL_NONE };
    EGLContext ctx = eglCreateContext(dpy, cfg, EGL_NO_CONTEXT, ctxAttr);
    if (ctx == EGL_NO_CONTEXT) { eglDestroySurface(dpy, surf); eglTerminate(dpy); return nullptr; }
    if (!eglMakeCurrent(dpy, surf, surf, ctx)) {
        eglDestroyContext(dpy, ctx); eglDestroySurface(dpy, surf); eglTerminate(dpy); return nullptr;
    }
    auto g = [](GLenum n) -> std::string {
        const GLubyte* s = glGetString(n);
        return s ? reinterpret_cast<const char*>(s) : std::string();
    };
    std::string out = g(GL_VENDOR) + "\t" + g(GL_RENDERER) + "\t" +
                      g(GL_VERSION) + "\t" + g(GL_SHADING_LANGUAGE_VERSION);
    eglMakeCurrent(dpy, EGL_NO_SURFACE, EGL_NO_SURFACE, EGL_NO_CONTEXT);
    eglDestroyContext(dpy, ctx);
    eglDestroySurface(dpy, surf);
    eglTerminate(dpy);
    return toJstring(env, out);
}

static void propListCb(void* cookie, const char* name, const char* value, uint32_t) {
    auto* out = static_cast<std::string*>(cookie);
    out->append(name ? name : "");
    out->push_back('\t');
    out->append(value ? value : "");
    out->push_back('\n');
}

static void propListEach(const prop_info* pi, void* cookie) {
    __system_property_read_callback(pi, propListCb, cookie);
}

jbyteArray nPropList(JNIEnv* env, jobject) {
    std::string out;
    __system_property_foreach(propListEach, &out);
    jbyteArray arr = env->NewByteArray(static_cast<jsize>(out.size()));
    if (arr == nullptr) return nullptr;
    env->SetByteArrayRegion(arr, 0, static_cast<jsize>(out.size()), reinterpret_cast<const jbyte*>(out.data()));
    return arr;
}

static bool pathAllowed(const char* p) {
    if (!p || !*p) return true;
    if (p[0] == '[') return true;
    if (std::strcmp(p, "linux-vdso.so.1") == 0) return true;
    static const char* ok[] = {"/system/", "/apex/", "/vendor/", "/product/", "/system_ext/",
                               "/data/app/", "/data/misc/apexdata/", "/data/dalvik-cache/", nullptr};
    for (int i = 0; ok[i]; i++) if (std::strncmp(p, ok[i], std::strlen(ok[i])) == 0) return true;
    if (std::strstr(p, "linker64") || std::strcmp(p, "libc.so") == 0 || std::strcmp(p, "libdl.so") == 0) return true;
    return false;
}

static const char* kDlNeedles[] = {"magisk", "zygisk", "lsposed", "riru", "frida",
                                   "substrate", "edxposed", "/data/adb", "memfd", "(deleted)", nullptr};

static size_t safeReadableLen(const char* p, size_t max) {
    if (p == nullptr || max == 0) return 0;
    int fds[2];
    if (pipe(fds) != 0) return 0;
    fcntl(fds[1], F_SETFL, O_NONBLOCK);
    const size_t pg = (size_t) sysconf(_SC_PAGESIZE);
    if (max > 4096) max = 4096;
    size_t total = 0;
    while (total < max) {
        const uintptr_t at = reinterpret_cast<uintptr_t>(p + total);
        size_t chunk = pg - (at % pg);
        if (chunk > max - total) chunk = max - total;
        ssize_t n = write(fds[1], p + total, chunk);
        if (n <= 0) break;
        const bool nul = memchr(p + total, 0, (size_t) n) != nullptr;
        total += (size_t) n;
        if (nul || (size_t) n < chunk) break;
    }
    close(fds[0]);
    close(fds[1]);
    return total;
}

struct DlAcc { int nhits = 0; int noutside = 0; std::string hits; std::string outside; };

static int dlCb(struct dl_phdr_info* info, size_t, void* data) {
    DlAcc* a = static_cast<DlAcc*>(data);
    const char* raw = info->dlpi_name;
    size_t len = safeReadableLen(raw, 4096);
    if (raw != nullptr && len == 0) {
        a->noutside++;
        if (a->outside.size() < 400) { if (!a->outside.empty()) a->outside += "|"; a->outside += "<unreadable-name>"; }
        return 0;
    }
    std::string name = (raw && len) ? std::string(raw, len) : std::string();
    size_t z = name.find('\0');
    if (z != std::string::npos) name.resize(z);
    for (int i = 0; kDlNeedles[i]; i++) {
        if (name.find(kDlNeedles[i]) != std::string::npos) {
            if (!a->hits.empty()) a->hits += "|";
            a->hits += name; a->nhits++; break;
        }
    }
    if (!pathAllowed(name.c_str())) {
        a->noutside++;
        if (a->outside.size() < 400) { if (!a->outside.empty()) a->outside += "|"; a->outside += name; }
    }
    return 0;
}

jstring nDlPhdr(JNIEnv* env, jobject) {
    DlAcc a;
    dl_iterate_phdr(dlCb, &a);
    std::string out = std::to_string(a.nhits) + "\t" + (a.hits.empty() ? "-" : a.hits) + "\t" +
                      std::to_string(a.noutside) + "\t" + (a.outside.empty() ? "-" : a.outside);
    return toJstring(env, out);
}

struct LibRange { std::string name; uintptr_t execAddr; };

static int linkerCb(struct dl_phdr_info* info, size_t, void* data) {
    auto* v = static_cast<std::vector<LibRange>*>(data);
    const char* raw = info->dlpi_name;
    size_t len = safeReadableLen(raw, 4096);
    std::string nm = (raw && len) ? std::string(raw, len) : std::string();
    size_t z = nm.find('\0');
    if (z != std::string::npos) nm.resize(z);
    for (int i = 0; i < info->dlpi_phnum; i++) {
        const ElfW(Phdr)* p = &info->dlpi_phdr[i];
        if (p->p_type == PT_LOAD && (p->p_flags & PF_X)) {
            v->push_back({nm, static_cast<uintptr_t>(info->dlpi_addr) + p->p_vaddr});
            break;
        }
    }
    return 0;
}

jstring nLinkerMapsGap(JNIEnv* env, jobject) {
    std::vector<LibRange> libs;
    dl_iterate_phdr(linkerCb, &libs);
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    struct M { uintptr_t s, e; bool path; };
    std::vector<M> ranges;
    size_t pos = 0;
    while (pos < maps.size()) {
        size_t eol = maps.find('\n', pos);
        std::string line = maps.substr(pos, (eol == std::string::npos ? maps.size() : eol) - pos);
        pos = (eol == std::string::npos) ? maps.size() : eol + 1;
        uintptr_t s = 0, e = 0;
        char perms[8] = {0};
        if (sscanf(line.c_str(), "%lx-%lx %7s", &s, &e, perms) < 3) continue;
        ranges.push_back({s, e, line.find('/') != std::string::npos});
    }
    std::string hidden;
    int n = 0;
    for (auto& L : libs) {
        if (L.name.empty() || L.name.find("vdso") != std::string::npos ||
            L.name.find("/linker") != std::string::npos) continue;
        bool found = false, backed = false;
        for (auto& r : ranges)
            if (L.execAddr >= r.s && L.execAddr < r.e) { found = true; backed = r.path; break; }
        if (found && !backed) {
            std::string base = L.name;
            size_t s = base.rfind('/');
            if (s != std::string::npos) base = base.substr(s + 1);
            if (base.empty()) base = "<anon-lib>";
            if (!hidden.empty()) hidden += ",";
            hidden += base;
            n++;
        }
    }
    return toJstring(env, std::to_string(n) + "\t" + (hidden.empty() ? "clean" : hidden));
}

static bool portOpen(const char* ip, int port) {
    int fd = socket(AF_INET, SOCK_STREAM, 0);
    if (fd < 0) return false;
    fcntl(fd, F_SETFL, O_NONBLOCK);
    struct sockaddr_in sa {};
    sa.sin_family = AF_INET;
    sa.sin_port = htons((uint16_t) port);
    sa.sin_addr.s_addr = inet_addr(ip);
    bool open = false;
    int r = connect(fd, reinterpret_cast<struct sockaddr*>(&sa), sizeof(sa));
    if (r == 0) open = true;
    else {
        struct pollfd pfd { fd, POLLOUT, 0 };
        if (poll(&pfd, 1, 250) > 0 && (pfd.revents & POLLOUT)) {
            int err = 0; socklen_t l = sizeof(err);
            getsockopt(fd, SOL_SOCKET, SO_ERROR, &err, &l);
            open = (err == 0);
        }
    }
    close(fd);
    return open;
}

jstring nFridaPorts(JNIEnv* env, jobject) {
    std::string out;
    int ports[] = {27042, 27043};
    for (int i = 0; i < 2; i++) {
        if (i) out += "\t";
        out += std::to_string(ports[i]) + ":" + (portOpen("127.0.0.1", ports[i]) ? "open" : "closed");
    }
    return toJstring(env, out);
}

static void collectVpnIface(const std::string& name, std::set<std::string>& out) {
    static const char* pfx[] = {"tun", "ppp", "tap", "pptp"};
    for (auto p : pfx) if (name.rfind(p, 0) == 0) { out.insert(name); return; }
}

static bool readNet(const char* path, std::string& out) {
    int fd = open(path, O_RDONLY | O_CLOEXEC);
    if (fd < 0) return false;
    out.clear();
    char buf[65536];
    ssize_t n;
    while ((n = read(fd, buf, sizeof(buf))) > 0) out.append(buf, (size_t) n);
    close(fd);
    return true;
}

jstring nProcNetScan(JNIEnv* env, jobject) {
    std::set<int> frida;
    const int fridaPorts[] = {27042, 27043};
    bool tcpReadable = false;
    for (const char* f : {"/proc/net/tcp", "/proc/net/tcp6"}) {
        std::string data;
        if (!readNet(f, data)) continue;
        tcpReadable = true;
        std::istringstream ls(data);
        std::string line;
        bool header = true;
        while (std::getline(ls, line)) {
            if (header) { header = false; continue; }
            std::istringstream is(line);
            std::string sl, local, rem, st;
            if (!(is >> sl >> local >> rem >> st)) continue;
            if (st != "0A") continue;
            size_t c = local.rfind(':');
            if (c == std::string::npos) continue;
            int port = (int) strtol(local.c_str() + c + 1, nullptr, 16);
            for (int fp : fridaPorts) if (port == fp) frida.insert(port);
        }
    }
    std::set<std::string> tun;
    bool ifReadable = false;
    std::string data;
    if (readNet("/proc/net/if_inet6", data)) {
        ifReadable = true;
        std::istringstream ls(data);
        std::string line;
        while (std::getline(ls, line)) {
            std::istringstream is(line);
            std::string a, b, cc, d, e, dev;
            if (is >> a >> b >> cc >> d >> e >> dev) collectVpnIface(dev, tun);
        }
    }
    if (readNet("/proc/net/route", data)) {
        ifReadable = true;
        std::istringstream ls(data);
        std::string line;
        bool header = true;
        while (std::getline(ls, line)) {
            if (header) { header = false; continue; }
            std::istringstream is(line);
            std::string ifc;
            if (is >> ifc) collectVpnIface(ifc, tun);
        }
    }
    if (readNet("/proc/net/dev", data)) {
        ifReadable = true;
        std::istringstream ls(data);
        std::string line;
        while (std::getline(ls, line)) {
            size_t c = line.find(':');
            if (c == std::string::npos) continue;
            std::string ifc = line.substr(0, c);
            size_t b = ifc.find_first_not_of(" \t");
            if (b == std::string::npos) continue;
            collectVpnIface(ifc.substr(b), tun);
        }
    }
    std::string out;
    if (!tcpReadable) out = "EACCES";
    else if (frida.empty()) out = "none";
    else { bool first = true; for (int p : frida) { if (!first) out += ","; out += std::to_string(p); first = false; } }
    out += "\t";
    if (!ifReadable) out += "EACCES";
    else if (tun.empty()) out += "none";
    else { bool first = true; for (auto& n : tun) { if (!first) out += ","; out += n; first = false; } }
    return toJstring(env, out);
}

static int pipePeek(unsigned long long addr, unsigned char* out, int n);

jstring nMapsDeletedExec(JNIEnv* env, jobject) {
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    int mdel = 0, mout = 0, mbenign = 0;
    std::string delhits, outhits;
    size_t pos = 0;
    while (pos < maps.size()) {
        size_t nl = maps.find('\n', pos);
        std::string line = maps.substr(pos, (nl == std::string::npos ? maps.size() : nl) - pos);
        pos = (nl == std::string::npos) ? maps.size() : nl + 1;
        size_t sp = line.find(' ');
        if (sp == std::string::npos || sp + 4 > line.size()) continue;
        if (line[sp + 3] != 'x') continue;
        bool wx = (line[sp + 2] == 'w');
        size_t slash = line.find('/');
        std::string pth = (slash != std::string::npos) ? line.substr(slash) : std::string();
        bool deleted = pth.find("(deleted)") != std::string::npos || pth.find("memfd:") != std::string::npos
                       || pth.find("/dev/ashmem") != std::string::npos;
        if (deleted) {
            bool isElf = false;
            if (line[sp + 1] == 'r') {
                unsigned long long start = std::strtoull(line.c_str(), nullptr, 16);
                if (start) {
                    unsigned char hdr[4];
                    if (pipePeek(start, hdr, 4) == 4 &&
                        hdr[0] == 0x7f && hdr[1] == 'E' && hdr[2] == 'L' && hdr[3] == 'F') isElf = true;
                }
            }
            if (wx || isElf) {
                mdel++;
                if (delhits.size() < 300) { if (!delhits.empty()) delhits += "|"; delhits += (isElf ? "ELF:" : "RWX:"); delhits += pth.empty() ? "<anon>" : pth; }
            } else {
                mbenign++;
            }
        } else if (!pth.empty() && pth[0] == '/' && !pathAllowed(pth.c_str())) {
            mout++;
            if (outhits.size() < 300) { if (!outhits.empty()) outhits += "|"; outhits += pth; }
        }
    }
    std::string out = std::to_string(mdel) + "\t" + (delhits.empty() ? "-" : delhits) + "\t" +
                      std::to_string(mout) + "\t" + (outhits.empty() ? "-" : outhits) + "\t" +
                      std::to_string(mbenign);
    return toJstring(env, out);
}

jstring nAnonExecElf(JNIEnv* env, jobject) {
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    int anonExec = 0, elfAnon = 0;
    std::string elfHits, anonExecHits;
    size_t pos = 0;
    while (pos < maps.size()) {
        size_t nl = maps.find('\n', pos);
        std::string line = maps.substr(pos, (nl == std::string::npos ? maps.size() : nl) - pos);
        pos = (nl == std::string::npos) ? maps.size() : nl + 1;
        size_t sp = line.find(' ');
        if (sp == std::string::npos || sp + 4 > line.size()) continue;
        char rp = line[sp + 1], xp = line[sp + 3];
        if (line.find('/', sp) != std::string::npos || line.find('[', sp) != std::string::npos) continue;
        unsigned long long start = std::strtoull(line.c_str(), nullptr, 16);
        unsigned long long end = 0;
        size_t dash = line.find('-');
        if (dash != std::string::npos) end = std::strtoull(line.c_str() + dash + 1, nullptr, 16);
        unsigned long long sz = (end > start) ? (end - start) : 0;
        bool isElf = false;
        if (rp == 'r' && start) {
            unsigned char hdr[4];
            if (pipePeek(start, hdr, 4) == 4 &&
                hdr[0] == 0x7f && hdr[1] == 'E' && hdr[2] == 'L' && hdr[3] == 'F') isElf = true;
        }
        char b[48];
        if (xp == 'x') {
            anonExec++;
            if (anonExecHits.size() < 220) { snprintf(b, sizeof(b), "0x%llx/%lluK", start, sz / 1024); if (!anonExecHits.empty()) anonExecHits += "|"; anonExecHits += b; }
        }
        if (isElf) {
            elfAnon++;
            if (elfHits.size() < 220) { snprintf(b, sizeof(b), "0x%llx/%lluK", start, sz / 1024); if (!elfHits.empty()) elfHits += "|"; elfHits += b; }
        }
    }
    std::string out = std::to_string(anonExec) + "\t" + std::to_string(elfAnon) + "\t" +
                      (elfHits.empty() ? "-" : elfHits) + "\t" + (anonExecHits.empty() ? "-" : anonExecHits);
    return toJstring(env, out);
}

static int pipePeek(unsigned long long addr, unsigned char* out, int n) {
    int pf[2];
    if (pipe(pf) != 0) return 0;
    fcntl(pf[1], F_SETFL, O_NONBLOCK);
    int got = 0;
    ssize_t w = write(pf[1], reinterpret_cast<const void*>(addr), (size_t) n);
    if (w > 0) { ssize_t r = read(pf[0], out, (size_t) w); if (r > 0) got = (int) r; }
    close(pf[0]); close(pf[1]);
    return got;
}

jstring nAnonElfStruct(JNIEnv* env, jobject) {
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    std::vector<std::pair<unsigned long long, std::string>> execAnon;
    std::vector<std::pair<unsigned long long, bool>> all;
    size_t pos = 0;
    while (pos < maps.size()) {
        size_t nl = maps.find('\n', pos);
        std::string line = maps.substr(pos, (nl == std::string::npos ? maps.size() : nl) - pos);
        pos = (nl == std::string::npos) ? maps.size() : nl + 1;
        size_t sp = line.find(' ');
        if (sp == std::string::npos || sp + 4 > line.size()) continue;
        unsigned long long start = std::strtoull(line.c_str(), nullptr, 16);
        if (!start) continue;
        char rp = line[sp + 1], xp = line[sp + 3];
        std::string tail = line.substr(sp);
        bool fileBacked = tail.find('/') != std::string::npos;
        bool anon = !fileBacked;
        all.push_back({start, rp == 'r'});
        if (xp == 'x' && anon) {
            std::string name = "(nameless)";
            size_t ap = line.find("[anon:", sp);
            if (ap != std::string::npos) { name = line.substr(ap); while (!name.empty() && (name.back()==' '||name.back()=='\r'||name.back()=='\t')) name.pop_back(); }
            execAnon.push_back({start, name});
        }
    }
    auto structElfNoMagic = [&](unsigned long long b) -> bool {
        unsigned char h[64] = {0};
        if (pipePeek(b, h, 64) < 64) return false;
        bool magic = (h[0] == 0x7f && h[1] == 'E' && h[2] == 'L' && h[3] == 'F');
        if (magic) return false;
        unsigned int e_machine = h[0x12] | (h[0x13] << 8);
        unsigned int e_ehsize = h[0x34] | (h[0x35] << 8);
        unsigned int e_phentsize = h[0x36] | (h[0x37] << 8);
        return e_machine == 0xB7 && e_ehsize == 64 && e_phentsize == 56;
    };
    int scrubbed = 0;
    std::string hits;
    for (auto& e : execAnon) {
        bool hit = structElfNoMagic(e.first);
        if (!hit) {
            auto it = std::lower_bound(all.begin(), all.end(), std::make_pair(e.first, false),
                                       [](const std::pair<unsigned long long,bool>&a, const std::pair<unsigned long long,bool>&b){ return a.first < b.first; });
            if (it != all.begin()) { --it; if (it->second) hit = structElfNoMagic(it->first); }
        }
        if (hit) { scrubbed++; if (hits.size() < 300) { char b[24]; snprintf(b, sizeof(b), "0x%llx", e.first); if (!hits.empty()) hits += "|"; hits += b; hits += e.second; } }
    }
    std::string out = std::to_string(scrubbed) + "\t" + std::to_string((int) execAnon.size()) + "\t" + (hits.empty() ? "-" : hits);
    return toJstring(env, out);
}

jstring nNamedAnonElf(JNIEnv* env, jobject) {
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    int elfNamed = 0, execNamed = 0;
    std::string elfHits, names;
    size_t pos = 0;
    while (pos < maps.size()) {
        size_t nl = maps.find('\n', pos);
        std::string line = maps.substr(pos, (nl == std::string::npos ? maps.size() : nl) - pos);
        pos = (nl == std::string::npos) ? maps.size() : nl + 1;
        size_t sp = line.find(' ');
        if (sp == std::string::npos || sp + 4 > line.size()) continue;
        char rp = line[sp + 1], xp = line[sp + 3];
        if (xp != 'x') continue;
        size_t namePos = line.find("[anon:", sp);
        if (namePos == std::string::npos) continue;
        std::string name = line.substr(namePos);
        while (!name.empty() && (name.back() == ' ' || name.back() == '\r' || name.back() == '\t')) name.pop_back();
        execNamed++;
        unsigned long long start = std::strtoull(line.c_str(), nullptr, 16);
        bool isElf = false;
        if (rp == 'r' && start) {
            unsigned char hdr[4];
            if (pipePeek(start, hdr, 4) == 4 &&
                hdr[0] == 0x7f && hdr[1] == 'E' && hdr[2] == 'L' && hdr[3] == 'F') isElf = true;
        }
        if (isElf) {
            elfNamed++;
            if (elfHits.size() < 300) { if (!elfHits.empty()) elfHits += "|"; elfHits += name; }
        }
        if (names.size() < 400) { if (!names.empty()) names += "|"; names += name; }
    }
    std::string out = std::to_string(elfNamed) + "\t" + std::to_string(execNamed) + "\t" +
                      (elfHits.empty() ? "-" : elfHits) + "\t" + (names.empty() ? "-" : names);
    return toJstring(env, out);
}

jstring nZygiskFootprint(JNIEnv* env, jobject) {
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    int memfdExecSusp = 0, memfdExecTotal = 0, zygiskHits = 0;
    std::string memfdNames, zygiskPaths;
    size_t pos = 0;
    while (pos < maps.size()) {
        size_t nl = maps.find('\n', pos);
        std::string line = maps.substr(pos, (nl == std::string::npos ? maps.size() : nl) - pos);
        pos = (nl == std::string::npos) ? maps.size() : nl + 1;
        size_t sp = line.find(' ');
        if (sp == std::string::npos || sp + 4 > line.size()) continue;
        char xp = line[sp + 3];
        std::string tail = line.substr(sp);
        size_t mp = tail.find("/memfd:");
        if (xp == 'x' && mp != std::string::npos) {
            memfdExecTotal++;
            std::string nm = tail.substr(mp);
            while (!nm.empty() && (nm.back() == ' ' || nm.back() == '\r' || nm.back() == '\t')) nm.pop_back();
            std::string after = nm.size() > 7 ? nm.substr(7) : "";
            bool named = !after.empty() && after[0] != ' ' && after[0] != '(';
            bool benign = !named || after.find("jit") != std::string::npos;
            if (!benign) memfdExecSusp++;
            if (memfdNames.size() < 350) { if (!memfdNames.empty()) memfdNames += "|"; memfdNames += nm + (benign ? (named ? "(jit)" : "(bare)") : "(SUSP)"); }
        }
        if (tail.find("zygisk") != std::string::npos) {
            zygiskHits++;
            size_t zp = tail.find('/');
            std::string nm = (zp != std::string::npos) ? tail.substr(zp) : tail;
            while (!nm.empty() && (nm.back() == ' ' || nm.back() == '\r' || nm.back() == '\t')) nm.pop_back();
            if (zygiskPaths.size() < 350) { if (!zygiskPaths.empty()) zygiskPaths += "|"; zygiskPaths += std::string(1, xp == 'x' ? 'x' : '-') + ":" + nm; }
        }
    }
    std::string out = std::to_string(memfdExecSusp) + "\t" + std::to_string(zygiskHits) + "\t" +
                      std::to_string(memfdExecTotal) + "\t" + (memfdNames.empty() ? "-" : memfdNames) +
                      "\t" + (zygiskPaths.empty() ? "-" : zygiskPaths);
    return toJstring(env, out);
}

static size_t peekPage(int wfd, int rfd, unsigned long long addr, char* dst, size_t len) {
    ssize_t w = write(wfd, reinterpret_cast<const void*>(addr), len);
    if (w <= 0) return 0;
    size_t total = 0;
    while (total < (size_t) w) {
        ssize_t r = read(rfd, dst + total, (size_t) w - total);
        if (r <= 0) break;
        total += (size_t) r;
    }
    return total;
}

jstring nHeapHookXref(JNIEnv* env, jobject) {
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    struct Range { unsigned long long lo, hi; };
    std::vector<Range> suspectExec, heaps;
    size_t pos = 0;
    while (pos < maps.size()) {
        size_t nl = maps.find('\n', pos);
        std::string line = maps.substr(pos, (nl == std::string::npos ? maps.size() : nl) - pos);
        pos = (nl == std::string::npos) ? maps.size() : nl + 1;
        size_t sp = line.find(' ');
        if (sp == std::string::npos || sp + 4 > line.size()) continue;
        char rp = line[sp + 1], wp = line[sp + 2], xp = line[sp + 3];
        unsigned long long start = std::strtoull(line.c_str(), nullptr, 16), end = 0;
        size_t dash = line.find('-');
        if (dash != std::string::npos) end = std::strtoull(line.c_str() + dash + 1, nullptr, 16);
        if (end <= start) continue;
        std::string tail = line.substr(sp);
        if (xp == 'x') {
            bool legit = tail.find("/system/") != std::string::npos || tail.find("/apex/") != std::string::npos ||
                         tail.find("/vendor/") != std::string::npos || tail.find("/product/") != std::string::npos ||
                         tail.find("/system_ext/") != std::string::npos || tail.find("/data/app/") != std::string::npos ||
                         tail.find("/memfd:jit") != std::string::npos || tail.find("[vdso]") != std::string::npos ||
                         tail.find("[vectors]") != std::string::npos || tail.find("/linkerconfig") != std::string::npos ||
                         tail.find("/dalvik-cache/") != std::string::npos || tail.find("[anon:dalvik-") != std::string::npos;
            if (!legit) suspectExec.push_back({start, end});
        }
        if (rp == 'r' && wp == 'w' && (tail.find("[anon:libc_malloc") != std::string::npos ||
            tail.find("[anon:scudo") != std::string::npos || tail.find("[anon:GWP-ASan") != std::string::npos ||
            tail.find("[anon:dalvik-") != std::string::npos))
            heaps.push_back({start, end});
    }
    if (suspectExec.empty()) return toJstring(env, "0\t0\t-");
    int pfd[2];
    if (pipe(pfd) != 0) return toJstring(env, "0\t0\t-");
    fcntl(pfd[1], F_SETFL, O_NONBLOCK);
    unsigned long long scanned = 0, cap = 48ull << 20;
    int xrefs = 0;
    std::vector<unsigned long long> hitRegions;
    char page[4096];
    for (auto& h : heaps) {
        for (unsigned long long p = h.lo; p + 8 <= h.hi && scanned < cap; p += sizeof(page)) {
            size_t want = (size_t) std::min<unsigned long long>(sizeof(page), h.hi - p);
            size_t got = peekPage(pfd[1], pfd[0], p, page, want);
            for (size_t off = 0; off + 8 <= got; off += 8) {
                scanned += 8;
                unsigned long long v;
                memcpy(&v, page + off, 8);
                for (auto& s : suspectExec) {
                    if (v >= s.lo && v < s.hi) {
                        xrefs++;
                        bool seen = false;
                        for (auto r : hitRegions) if (r == s.lo) { seen = true; break; }
                        if (!seen && hitRegions.size() < 16) hitRegions.push_back(s.lo);
                        break;
                    }
                }
            }
        }
        if (scanned >= cap) break;
    }
    close(pfd[0]);
    close(pfd[1]);
    std::string regs;
    for (auto r : hitRegions) { char b[24]; snprintf(b, sizeof(b), "0x%llx", r); if (!regs.empty()) regs += "|"; regs += b; }
    std::string out = std::to_string(xrefs) + "\t" + std::to_string((int) suspectExec.size()) + "\t" + (regs.empty() ? "-" : regs);
    return toJstring(env, out);
}

jstring nHiddenRegionXref(JNIEnv* env, jobject) {
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    std::vector<std::pair<unsigned long long, unsigned long long>> regions, heaps;
    size_t pos = 0;
    while (pos < maps.size()) {
        size_t nl = maps.find('\n', pos);
        std::string line = maps.substr(pos, (nl == std::string::npos ? maps.size() : nl) - pos);
        pos = (nl == std::string::npos ? maps.size() : nl + 1);
        size_t sp = line.find(' ');
        if (sp == std::string::npos || sp + 4 > line.size()) continue;
        unsigned long long lo = std::strtoull(line.c_str(), nullptr, 16), hi = 0;
        size_t dash = line.find('-');
        if (dash != std::string::npos) hi = std::strtoull(line.c_str() + dash + 1, nullptr, 16);
        if (hi <= lo) continue;
        regions.push_back({lo, hi});
        char rp = line[sp + 1], wp = line[sp + 2];
        std::string tail = line.substr(sp);
        if (rp == 'r' && wp == 'w' && (tail.find("[anon:libc_malloc") != std::string::npos ||
            tail.find("[anon:scudo") != std::string::npos || tail.find("[anon:GWP-ASan") != std::string::npos ||
            tail.find("[anon:dalvik-") != std::string::npos))
            heaps.push_back({lo, hi});
    }
    std::sort(regions.begin(), regions.end());
    auto inRegion = [&](unsigned long long v) -> bool {
        auto it = std::upper_bound(regions.begin(), regions.end(),
                                   std::make_pair(v, (unsigned long long) ~0ULL));
        if (it == regions.begin()) return false;
        --it;
        return v >= it->first && v < it->second;
    };
    int pfd[2];
    if (pipe(pfd) != 0) return toJstring(env, "0\t0\t-");
    fcntl(pfd[1], F_SETFL, O_NONBLOCK);
    auto readable = [&](unsigned long long v) -> bool {
        ssize_t n = write(pfd[1], reinterpret_cast<const void*>(v), 8);
        if (n > 0) { char b[8]; ssize_t r = read(pfd[0], b, (size_t) n); (void) r; return true; }
        return false;
    };
    std::set<unsigned long long> rdOk, rdBad, hiddenPages;
    int hiddenPtrs = 0;
    unsigned long long scanned = 0, cap = 48ull << 20;
    char page[4096];
    for (auto& h : heaps) {
        for (unsigned long long pp = h.first; pp + 8 <= h.second && scanned < cap; pp += sizeof(page)) {
            size_t want = (size_t) std::min<unsigned long long>(sizeof(page), h.second - pp);
            size_t got = peekPage(pfd[1], pfd[0], pp, page, want);
            for (size_t off = 0; off + 8 <= got; off += 8) {
                scanned += 8;
                unsigned long long v;
                memcpy(&v, page + off, 8);
                if (v < 0x10000ULL || v >= 0x800000000000ULL) continue;
                if (inRegion(v)) continue;
                unsigned long long pg = v & ~0xfffULL;
                bool ok;
                if (rdOk.count(pg)) ok = true;
                else if (rdBad.count(pg)) ok = false;
                else {
                    if (rdOk.size() + rdBad.size() >= 8192) continue;
                    ok = readable(v);
                    if (ok) rdOk.insert(pg); else rdBad.insert(pg);
                }
                if (ok) { hiddenPtrs++; if (hiddenPages.size() < 32) hiddenPages.insert(pg); }
            }
        }
        if (scanned >= cap) break;
    }
    close(pfd[0]);
    close(pfd[1]);
    std::string pgs;
    for (auto p : hiddenPages) { char b[24]; snprintf(b, sizeof(b), "0x%llx", p); if (!pgs.empty()) pgs += "|"; pgs += b; }
    std::string out = std::to_string(hiddenPtrs) + "\t" + std::to_string((int) hiddenPages.size()) + "\t" + (pgs.empty() ? "-" : pgs);
    return toJstring(env, out);
}

static std::vector<std::pair<unsigned long long, unsigned long long>> parseMapsRegions() {
    std::string maps = readFileRaw("/proc/self/maps", 8u << 20);
    std::vector<std::pair<unsigned long long, unsigned long long>> regions;
    size_t pos = 0;
    while (pos < maps.size()) {
        size_t nl = maps.find('\n', pos);
        std::string line = maps.substr(pos, (nl == std::string::npos ? maps.size() : nl) - pos);
        pos = (nl == std::string::npos ? maps.size() : nl + 1);
        unsigned long long lo = std::strtoull(line.c_str(), nullptr, 16), hi = 0;
        size_t dash = line.find('-');
        if (dash != std::string::npos) hi = std::strtoull(line.c_str() + dash + 1, nullptr, 16);
        if (hi <= lo) continue;
        regions.push_back({lo, hi});
    }
    std::sort(regions.begin(), regions.end());
    return regions;
}

jstring nGapMapped(JNIEnv* env, jobject) {
    auto regions = parseMapsRegions();
    long psz = sysconf(_SC_PAGESIZE);
    unsigned long long PG = (psz > 0) ? (unsigned long long) psz : 4096ULL;
    int gapMapped = 0, gapElf = 0, gapsScanned = 0;
    std::vector<unsigned long long> cand;
    auto mapped = [&](unsigned long long a) -> bool {
        unsigned char vec = 0;
        return mincore(reinterpret_cast<void*>(a), (size_t) PG, &vec) == 0;
    };
    auto elfAt = [&](unsigned long long a) -> bool {
        int pf[2];
        if (pipe(pf) != 0) return false;
        fcntl(pf[1], F_SETFL, O_NONBLOCK);
        unsigned char hdr[4] = {0};
        bool ok = (write(pf[1], reinterpret_cast<const void*>(a), 4) == 4 && read(pf[0], hdr, 4) == 4 &&
                   hdr[0] == 0x7f && hdr[1] == 'E' && hdr[2] == 'L' && hdr[3] == 'F');
        close(pf[0]); close(pf[1]);
        return ok;
    };
    for (size_t i = 0; i + 1 < regions.size(); i++) {
        unsigned long long gs = regions[i].second, ge = regions[i + 1].first;
        if (ge <= gs) continue;
        unsigned long long gap = ge - gs;
        gapsScanned++;
        unsigned long long npg = gap / PG, samples = (npg < 16) ? npg : 16;
        bool hitThisGap = false;
        for (unsigned long long k = 0; k <= samples && !hitThisGap; k++) {
            unsigned long long a = (k < samples) ? ((gs + (gap / samples) * k) & ~(PG - 1)) : (ge - PG);
            if (a < gs || a + PG > ge) continue;
            if (mapped(a)) { cand.push_back(a); hitThisGap = true; }
        }
    }
    auto regionsB = parseMapsRegions();
    auto inB = [&](unsigned long long v) -> bool {
        auto it = std::upper_bound(regionsB.begin(), regionsB.end(),
                                   std::make_pair(v, (unsigned long long) ~0ULL));
        if (it == regionsB.begin()) return false;
        --it; return v >= it->first && v < it->second;
    };
    std::set<unsigned long long> runStarts, elfStarts;
    for (unsigned long long a : cand) {
        if (inB(a)) continue;
        unsigned long long start = a;
        for (int step = 0; step < 4096; step++) {
            unsigned long long p = start - PG;
            if (p >= start || !mapped(p) || inB(p)) break;
            start = p;
        }
        gapMapped++;
        if (runStarts.insert(start).second && elfAt(start)) { gapElf++; elfStarts.insert(start); }
    }
    std::string pgs;
    int shown = 0;
    const std::set<unsigned long long>& show = elfStarts.empty() ? runStarts : elfStarts;
    for (auto p : show) { if (shown++ >= 32) break; char b[24]; snprintf(b, sizeof(b), "0x%llx", p); if (!pgs.empty()) pgs += "|"; pgs += b; }
    std::string out = std::to_string(gapMapped) + "\t" + std::to_string(gapElf) + "\t" +
                      std::to_string((int) runStarts.size()) + "\t" + std::to_string(gapsScanned) + "\t" +
                      (pgs.empty() ? "-" : pgs);
    return toJstring(env, out);
}

static bool statAnonMinor(const char* path, int& out) {
    struct stat st {};
    if (stat(path, &st) != 0) return false;
    if (major(st.st_dev) != 0) return false;
    out = static_cast<int>(minor(st.st_dev));
    return true;
}

static std::set<int> anonPresentSet(const std::string& pkg) {
    std::set<int> present;
    std::string mi = readFileRaw("/proc/self/mountinfo", 1u << 20);
    size_t pos = 0;
    while (pos < mi.size()) {
        size_t eol = mi.find('\n', pos);
        std::string line = mi.substr(pos, (eol == std::string::npos ? mi.size() : eol) - pos);
        pos = (eol == std::string::npos) ? mi.size() : eol + 1;
        int id, parent, mj, mn; char root[4096] = {}, tgt[4096] = {};
        if (sscanf(line.c_str(), "%d %d %d:%d %4095s %4095s", &id, &parent, &mj, &mn, root, tgt) < 6) continue;
        if (mj != 0) continue;
        std::string t = tgt, rt = root;
        bool appPriv = t.rfind("/data/data", 0) == 0 || t.rfind("/data/user", 0) == 0 ||
                       t.rfind("/data/misc/profiles", 0) == 0 ||
                       (pkg.find('.') != std::string::npos && rt.find(pkg) != std::string::npos);
        if (!appPriv) present.insert(mn);
    }
    return present;
}

static std::set<int> anonShortHoles(const std::set<int>& present, int flr, int ceil, int& churnRuns, int& churnMin) {
    std::set<int> holes; churnRuns = 0; churnMin = 0;
    int m = flr + 1;
    while (m < ceil) {
        if (present.find(m) != present.end()) { m++; continue; }
        int run = m; while (run < ceil && present.find(run) == present.end()) run++;
        int len = run - m;
        if (len > 10) { churnRuns++; churnMin += len; }
        else for (int k = m; k < run; k++) holes.insert(k);
        m = run;
    }
    return holes;
}

jstring nAnonHoles(JNIEnv* env, jobject) {
    int flr = -1;
    const char* floors[] = {"/tmp", "/dev/cpuset", "/sys/fs/cgroup", nullptr};
    for (int i = 0; floors[i]; i++) { int m; if (statAnonMinor(floors[i], m) && m > flr) flr = m; }
    std::string pkg = readFileRaw("/proc/self/cmdline", 512);
    size_t nul = pkg.find('\0'); if (nul != std::string::npos) pkg.resize(nul);
    size_t colon = pkg.find(':'); if (colon != std::string::npos) pkg.resize(colon);
    std::vector<std::string> ceils = {"/storage/emulated/0/Android/data/.nomedia",
                                      "/storage/emulated/0/Android/obb/.nomedia"};
    if (!pkg.empty()) ceils.push_back("/storage/emulated/0/Android/data/" + pkg);
    ceils.push_back("/storage/emulated/0"); ceils.push_back("/storage/emulated");
    int ceil = -1;
    for (auto& cp : ceils) { int m; if (statAnonMinor(cp.c_str(), m)) { ceil = m; break; } }
    if (flr < 0 || ceil <= flr) return toJstring(env, "0\tNENHUM(ok)\t0\t0\t0\t0");
    int churnRuns = 0, churnMin = 0, churnRuns2 = 0, churnMin2 = 0;
    std::set<int> firstHoles = anonShortHoles(anonPresentSet(pkg), flr, ceil, churnRuns, churnMin);
    usleep(25000);
    std::set<int> secondHoles = anonShortHoles(anonPresentSet(pkg), flr, ceil, churnRuns2, churnMin2);
    std::string holes; int stable = 0;
    for (int k : firstHoles) if (secondHoles.find(k) != secondHoles.end()) {
        if (!holes.empty()) holes += ","; holes += std::to_string(k); stable++;
    }
    std::string out = std::to_string(stable) + "\t" + (holes.empty() ? "NENHUM(ok)" : holes) + "\t" +
                      std::to_string(churnRuns) + "\t" + std::to_string(churnMin) + "\t" +
                      std::to_string(ceil - flr - 1) + "\t" + std::to_string(static_cast<int>(firstHoles.size()));
    return toJstring(env, out);
}

jstring nDirList(JNIEnv* env, jobject, jstring jdir) {
    const char* dir = env->GetStringUTFChars(jdir, nullptr);
    int fd = open(dir, O_RDONLY | O_DIRECTORY | O_CLOEXEC);
    env->ReleaseStringUTFChars(jdir, dir);
    if (fd < 0) return toJstring(env, errno == EACCES ? "EACCES"
                                    : (errno == ENOENT ? "ENOENT" : ("errno=" + std::to_string(errno))));
    struct linux_dirent64 { uint64_t d_ino; int64_t d_off; unsigned short d_reclen; unsigned char d_type; char d_name[]; };
    char buf[8192];
    std::string out;
    int n = 0;
    for (;;) {
        long r = syscall(SYS_getdents64, fd, buf, sizeof(buf));
        if (r <= 0) break;
        for (long off = 0; off < r;) {
            auto* d = reinterpret_cast<linux_dirent64*>(buf + off);
            std::string nm = d->d_name;
            if (nm != "." && nm != ".." && n++ < 50) { if (!out.empty()) out += "|"; out += nm; }
            off += d->d_reclen;
        }
    }
    close(fd);
    return toJstring(env, out.empty() ? "empty" : out);
}

jstring nUidPriorityScan(JNIEnv* env, jobject, jintArray juids) {
    jsize n = env->GetArrayLength(juids);
    jint* uids = env->GetIntArrayElements(juids, nullptr);
    if (uids == nullptr) return toJstring(env, "clean");

    int pipefd[2];
    if (pipe(pipefd) != 0) {
        env->ReleaseIntArrayElements(juids, uids, JNI_ABORT);
        return toJstring(env, "clean");
    }
    pid_t pid = fork();
    if (pid < 0) {
        close(pipefd[0]); close(pipefd[1]);
        env->ReleaseIntArrayElements(juids, uids, JNI_ABORT);
        return toJstring(env, "clean");
    }
    if (pid == 0) {
        struct sigaction sa {};
        sa.sa_handler = onSigsys;
        sigemptyset(&sa.sa_mask);
        sigaction(SIGSYS, &sa, nullptr);
        close(pipefd[0]);
        for (jsize i = 0; i < n; i++) {
            long uid = (long) uids[i];
            errno = 0;
            int p = getpriority(PRIO_USER, (id_t) uid);
            int32_t gpEsrch = (p == -1 && errno == ESRCH) ? 1 : 0;
            errno = 0;
            long r = syscall(__NR_ioprio_get, IOPRIO_WHO_USER, uid);
            if (r < 0 && errno == EINVAL) {
                int32_t rec[2] = { (int32_t) uid, gpEsrch };
                write(pipefd[1], rec, sizeof(rec));
            }
        }
        close(pipefd[1]);
        _exit(0);
    }
    close(pipefd[1]);
    std::string raw;
    char rb[4096];
    ssize_t got;
    while ((got = read(pipefd[0], rb, sizeof(rb))) > 0) raw.append(rb, (size_t) got);
    close(pipefd[0]);
    int st = 0;
    waitpid(pid, &st, 0);
    env->ReleaseIntArrayElements(juids, uids, JNI_ABORT);

    if (WIFEXITED(st) && WEXITSTATUS(st) == 2) return toJstring(env, "clean");
    std::string leak;
    for (size_t off = 0; off + 8 <= raw.size(); off += 8) {
        int32_t ruid, rgp;
        memcpy(&ruid, raw.data() + off, 4);
        memcpy(&rgp, raw.data() + off + 4, 4);
        if (!leak.empty()) leak += ",";
        leak += "uid=" + std::to_string(ruid) + (rgp ? "(getpri=ESRCH)" : "");
    }
    if (leak.empty()) return toJstring(env, "clean");
    return toJstring(env, "UID_FAKE ioprio_get(WHO_USER)=EINVAL " + leak);
}

static bool liveScanIoprioForked(int start, int end, int sel, std::vector<int>& out) {
#if !defined(__NR_ioprio_get) || !defined(__NR_ioprio_set)
    (void) start; (void) end; (void) sel; (void) out;
    return false;
#else
    int pfd[2];
    if (pipe(pfd) != 0) return false;
    pid_t pid = fork();
    if (pid < 0) { close(pfd[0]); close(pfd[1]); return false; }
    if (pid == 0) {
        struct sigaction sa {};
        sa.sa_handler = onSigsys;
        sigemptyset(&sa.sa_mask);
        sigaction(SIGSYS, &sa, nullptr);
        close(pfd[0]);
        uid_t self = getuid();
        for (int uid = start; uid <= end; uid++) {
            bool alive = false;
            if (sel == 2) {
                errno = 0;
                long r = syscall(__NR_ioprio_get, IOPRIO_WHO_USER, (long) uid);
                alive = !(r == -1 && (errno == ESRCH || errno == EINVAL));
            } else {
                if ((uid_t) uid == self) {
                    alive = true;
                } else {
                    errno = 0;
                    syscall(__NR_ioprio_set, IOPRIO_WHO_USER, (long) uid, (long) ((2 << 13) | 4));
                    alive = !(errno == ESRCH || errno == EINVAL);
                }
            }
            if (alive) { int u = uid; write(pfd[1], &u, sizeof(u)); }
        }
        close(pfd[1]);
        _exit(0);
    }
    close(pfd[1]);
    std::string acc;
    char rb[4096];
    ssize_t got;
    while ((got = read(pfd[0], rb, sizeof(rb))) > 0) acc.append(rb, (size_t) got);
    close(pfd[0]);
    int st = 0;
    waitpid(pid, &st, 0);
    if (WIFEXITED(st) && WEXITSTATUS(st) == 2) return false;
    for (size_t i = 0; i + sizeof(int) <= acc.size(); i += sizeof(int)) {
        int v;
        memcpy(&v, acc.data() + i, sizeof(int));
        out.push_back(v);
    }
    return true;
#endif
}

jintArray nUidLiveScan(JNIEnv* env, jobject, jint start, jint end) {
    std::vector<int> live;
    int pfd[2];
    if (pipe(pfd) != 0) return env->NewIntArray(0);
    pid_t pid = fork();
    if (pid < 0) { close(pfd[0]); close(pfd[1]); return env->NewIntArray(0); }
    if (pid == 0) {
        struct sigaction sa {};
        sa.sa_handler = onSigsys;
        sigemptyset(&sa.sa_mask);
        sigaction(SIGSYS, &sa, nullptr);
        close(pfd[0]);
        uid_t self = getuid();
        for (int uid = start; uid <= end; uid++) {
            errno = 0;
            int p = getpriority(PRIO_USER, (id_t) uid);
            bool alive = !(p == -1 && errno == ESRCH);
#ifdef __NR_ioprio_get
            if (!alive) {
                errno = 0;
                long r = syscall(__NR_ioprio_get, IOPRIO_WHO_USER, (long) uid);
                alive = !(r == -1 && (errno == ESRCH || errno == EINVAL));
            }
#endif
            if (!alive) {
                if ((uid_t) uid == self) {
                    alive = true;
                } else {
                    errno = 0;
                    setpriority(PRIO_USER, (id_t) uid, 0);
                    alive = (errno != ESRCH);
                }
            }
#ifdef __NR_ioprio_set
            if (!alive && (uid_t) uid != self) {
                errno = 0;
                syscall(__NR_ioprio_set, IOPRIO_WHO_USER, (long) uid, (long) ((2 << 13) | 4));
                alive = !(errno == ESRCH || errno == EINVAL);
            }
#endif
            if (alive) { int u = uid; write(pfd[1], &u, sizeof(u)); }
        }
        close(pfd[1]);
        _exit(0);
    }
    close(pfd[1]);
    std::string acc;
    char rb[4096];
    ssize_t got;
    while ((got = read(pfd[0], rb, sizeof(rb))) > 0) acc.append(rb, (size_t) got);
    close(pfd[0]);
    int st = 0;
    waitpid(pid, &st, 0);
    if (!(WIFEXITED(st) && WEXITSTATUS(st) == 2))
        for (size_t i = 0; i + sizeof(int) <= acc.size(); i += sizeof(int)) {
            int v;
            memcpy(&v, acc.data() + i, sizeof(int));
            live.push_back(v);
        }
    jintArray arr = env->NewIntArray((jsize) live.size());
    if (arr && !live.empty()) env->SetIntArrayRegion(arr, 0, (jsize) live.size(), (const jint*) live.data());
    return arr;
}

jintArray nUidLiveScanSel(JNIEnv* env, jobject, jint start, jint end, jint sel) {
    std::vector<int> live;
    if (sel == 2 || sel == 3) {
        liveScanIoprioForked(start, end, sel, live);
    } else {
        uid_t self = getuid();
        for (int uid = start; uid <= end; uid++) {
            bool alive = false;
            errno = 0;
            switch (sel) {
                case 0: {
                    int p = getpriority(PRIO_USER, (id_t) uid);
                    alive = !(p == -1 && errno == ESRCH);
                    break;
                }
                case 1: {
                    if ((uid_t) uid == self) { alive = true; break; }
                    setpriority(PRIO_USER, (id_t) uid, 0);
                    alive = (errno != ESRCH);
                    break;
                }
            }
            if (alive) live.push_back(uid);
        }
    }
    jintArray arr = env->NewIntArray((jsize) live.size());
    if (arr && !live.empty()) env->SetIntArrayRegion(arr, 0, (jsize) live.size(), (const jint*) live.data());
    return arr;
}

jstring nUidPriorityScanSel(JNIEnv* env, jobject, jintArray juids, jint sel) {
    jsize n = env->GetArrayLength(juids);
    jint* uids = env->GetIntArrayElements(juids, nullptr);
    if (uids == nullptr) return toJstring(env, "clean");

    int pipefd[2];
    if (pipe(pipefd) != 0) {
        env->ReleaseIntArrayElements(juids, uids, JNI_ABORT);
        return toJstring(env, "clean");
    }
    pid_t pid = fork();
    if (pid < 0) {
        close(pipefd[0]); close(pipefd[1]);
        env->ReleaseIntArrayElements(juids, uids, JNI_ABORT);
        return toJstring(env, "clean");
    }
    if (pid == 0) {
        struct sigaction sa {};
        sa.sa_handler = onSigsys;
        sigemptyset(&sa.sa_mask);
        sigaction(SIGSYS, &sa, nullptr);
        close(pipefd[0]);
        uid_t self = getuid();
        for (jsize i = 0; i < n; i++) {
            long uid = (long) uids[i];
            bool einval = false;
            switch (sel) {
                case 0:
                    errno = 0;
                    getpriority(PRIO_USER, (id_t) uid);
                    einval = (errno == EINVAL);
                    break;
                case 1:
                    if ((uid_t) uid == self) break;
                    errno = 0;
                    setpriority(PRIO_USER, (id_t) uid, 0);
                    einval = (errno == EINVAL);
                    break;
                case 2:
                    errno = 0;
                    syscall(__NR_ioprio_get, IOPRIO_WHO_USER, uid);
                    einval = (errno == EINVAL);
                    break;
                case 3:
                    if ((uid_t) uid == self) break;
                    errno = 0;
                    syscall(__NR_ioprio_set, IOPRIO_WHO_USER, uid, (long) ((2 << 13) | 4));
                    einval = (errno == EINVAL);
                    break;
            }
            if (einval) {
                int32_t ruid = (int32_t) uid;
                write(pipefd[1], &ruid, sizeof(ruid));
            }
        }
        close(pipefd[1]);
        _exit(0);
    }
    close(pipefd[1]);
    std::string raw;
    char rb[4096];
    ssize_t got;
    while ((got = read(pipefd[0], rb, sizeof(rb))) > 0) raw.append(rb, (size_t) got);
    close(pipefd[0]);
    int st = 0;
    waitpid(pid, &st, 0);
    env->ReleaseIntArrayElements(juids, uids, JNI_ABORT);

    if (WIFEXITED(st) && WEXITSTATUS(st) == 2) return toJstring(env, "clean");
    std::string leak;
    for (size_t off = 0; off + 4 <= raw.size(); off += 4) {
        int32_t ruid;
        memcpy(&ruid, raw.data() + off, 4);
        if (!leak.empty()) leak += ",";
        leak += "uid=" + std::to_string(ruid);
    }
    if (leak.empty()) return toJstring(env, "clean");
    return toJstring(env, "UID_FAKE EINVAL(valid selector) " + leak);
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
    {"nStatPresence", "(Ljava/lang/String;I)Ljava/lang/String;", reinterpret_cast<void*>(nStatPresence)},
    {"nReadlink", "(Ljava/lang/String;)Ljava/lang/String;",   reinterpret_cast<void*>(nReadlink)},
    {"nIds",      "()Ljava/lang/String;",                     reinterpret_cast<void*>(nIds)},
    {"nStatfs",   "(Ljava/lang/String;)Ljava/lang/String;",   reinterpret_cast<void*>(nStatfs)},
    {"nArch",     "()Ljava/lang/String;",                     reinterpret_cast<void*>(nArch)},
    {"nPropAreaHoles", "()Ljava/lang/String;",              reinterpret_cast<void*>(nPropAreaHoles)},
    {"nRawExists",   "(Ljava/lang/String;)Z",               reinterpret_cast<void*>(nRawExists)},
    {"nKernelSpoof", "()Ljava/lang/String;",                reinterpret_cast<void*>(nKernelSpoof)},
    {"nGetGroups",   "()Ljava/lang/String;",                reinterpret_cast<void*>(nGetGroups)},
    {"nAtexitDrift", "()Ljava/lang/String;",                reinterpret_cast<void*>(nAtexitDrift)},
    {"nSelfPath",    "()Ljava/lang/String;",                reinterpret_cast<void*>(nSelfPath)},
    {"nLibcIdentity", "()Ljava/lang/String;",               reinterpret_cast<void*>(nLibcIdentity)},
    {"nStatOwner",   "(Ljava/lang/String;)Ljava/lang/String;", reinterpret_cast<void*>(nStatOwner)},
    {"nStatMode",    "(Ljava/lang/String;)Ljava/lang/String;", reinterpret_cast<void*>(nStatMode)},
    {"nInlineHookScan", "()Ljava/lang/String;", reinterpret_cast<void*>(nInlineHookScan)},
    {"nLinkerMapsGap", "()Ljava/lang/String;", reinterpret_cast<void*>(nLinkerMapsGap)},
    {"nDirZeroWidth","(Ljava/lang/String;[I)Ljava/lang/String;", reinterpret_cast<void*>(nDirZeroWidth)},
    {"nSockDiag",   "()Ljava/lang/String;",                   reinterpret_cast<void*>(nSockDiag)},
    {"nStatMeta",   "(Ljava/lang/String;)Ljava/lang/String;", reinterpret_cast<void*>(nStatMeta)},
    {"nGpuInfo",    "()Ljava/lang/String;",                   reinterpret_cast<void*>(nGpuInfo)},
    {"nDlPhdr",     "()Ljava/lang/String;",                   reinterpret_cast<void*>(nDlPhdr)},
    {"nFridaPorts", "()Ljava/lang/String;",                   reinterpret_cast<void*>(nFridaPorts)},
    {"nProcNetScan", "()Ljava/lang/String;",                  reinterpret_cast<void*>(nProcNetScan)},
    {"nMapsDeletedExec", "()Ljava/lang/String;",              reinterpret_cast<void*>(nMapsDeletedExec)},
    {"nAnonHoles",  "()Ljava/lang/String;",                   reinterpret_cast<void*>(nAnonHoles)},
    {"nAnonExecElf", "()Ljava/lang/String;",                  reinterpret_cast<void*>(nAnonExecElf)},
    {"nNamedAnonElf", "()Ljava/lang/String;",                 reinterpret_cast<void*>(nNamedAnonElf)},
    {"nAnonElfStruct", "()Ljava/lang/String;",                reinterpret_cast<void*>(nAnonElfStruct)},
    {"nZygiskFootprint", "()Ljava/lang/String;",              reinterpret_cast<void*>(nZygiskFootprint)},
    {"nHeapHookXref", "()Ljava/lang/String;",                 reinterpret_cast<void*>(nHeapHookXref)},
    {"nHiddenRegionXref", "()Ljava/lang/String;",             reinterpret_cast<void*>(nHiddenRegionXref)},
    {"nGapMapped", "()Ljava/lang/String;",                    reinterpret_cast<void*>(nGapMapped)},
    {"nDirList",    "(Ljava/lang/String;)Ljava/lang/String;", reinterpret_cast<void*>(nDirList)},
    {"nUidPriorityScan", "([I)Ljava/lang/String;",            reinterpret_cast<void*>(nUidPriorityScan)},
    {"nUidLiveScan", "(II)[I",                                reinterpret_cast<void*>(nUidLiveScan)},
    {"nUidLiveScanSel", "(III)[I",                            reinterpret_cast<void*>(nUidLiveScanSel)},
    {"nUidPriorityScanSel", "([II)Ljava/lang/String;",        reinterpret_cast<void*>(nUidPriorityScanSel)},
    {"nPropList",   "()[B",                                   reinterpret_cast<void*>(nPropList)},
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
