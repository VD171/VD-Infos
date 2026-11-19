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

package ru.vd171.vdinfos.probe

import android.content.Context
import android.os.Build
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.core.model.Lens
import ru.vd171.vdinfos.engine.ProbeTask
import java.io.File

object IntegrityProbes {

    private val PROBE_COMPONENT = Regex("Probe_[0-9a-fA-F]{8,}")

    private fun managerDoors(c: Context): String {
        val pm = c.packageManager
        @Suppress("QueryPermissionsNeeded")
        val listed = runCatching { pm.getInstalledPackages(0).map { it.packageName }.toSet() }
            .getOrDefault(emptySet())
        val byPerm = runCatching {
            @Suppress("QueryPermissionsNeeded")
            pm.getPackagesHoldingPermissions(AssetData.packages(c, "detector_holding_perms").toTypedArray(), 0).map { it.packageName }.toSet()
        }.getOrDefault(emptySet())
        val compFlags = android.content.pm.PackageManager.GET_ACTIVITIES or
            android.content.pm.PackageManager.GET_RECEIVERS or
            android.content.pm.PackageManager.GET_SERVICES
        val out = ArrayList<String>()
        for (pkg in AssetData.packages(c, "root_manager_apps") + AssetData.packages(c, "xposed_manager_apps")) {
            val inList = pkg in listed
            val info = runCatching { pm.getPackageInfo(pkg, 0) }.isSuccess
            val appInfo = runCatching { pm.getApplicationInfo(pkg, 0) }.isSuccess
            val launch = runCatching { pm.getLaunchIntentForPackage(pkg) != null }.getOrDefault(false)
            val pi = runCatching { pm.getPackageInfo(pkg, compFlags) }.getOrNull()
            val names = buildList {
                pi?.activities?.forEach { add(it.name) }
                pi?.receivers?.forEach { add(it.name) }
                pi?.services?.forEach { add(it.name) }
            }
            val perm = pkg in byPerm
            val doors = listOf(inList, info, appInfo, launch, names.isNotEmpty(), perm)
            if (doors.none { it }) continue
            val probe = names.firstOrNull { PROBE_COMPONENT.containsMatchIn(it) }
            out.add(
                "$pkg list=${yn(inList)} info=${yn(info)} app=${yn(appInfo)} launch=${yn(launch)} " +
                    "perm=${yn(perm)} comps=${names.size}" +
                    (probe?.let { " probe=${it.substringAfterLast('.')}" } ?: "") +
                    if (doors.distinct().size > 1) "  DIVERGE" else ""
            )
        }
        return if (out.isEmpty()) Sentinels.NONE else out.joinToString("\n")
    }

    private fun yn(b: Boolean) = if (b) "y" else "n"

    private fun groupsCsv(status: String?): String? {
        val line = status?.lineSequence()?.firstOrNull { it.startsWith("Groups:") } ?: return null
        val gids = line.substringAfter(":").trim().split(Regex("\\s+"))
            .mapNotNull { it.trim().toIntOrNull() }.sorted()
        return if (gids.isEmpty()) "none" else gids.joinToString(",")
    }

    private fun pathItem(id: String, title: String, paths: List<String>) = probe(
        "integrity:$id", title, Category.INTEGRITY, listOf(
            nm("access() any of ${paths.size} paths") {
                paths.filter { NativeBridge.exists(it) }.joinToString("\n").ifEmpty { Sentinels.ABSENT }
            },
            nm("raw faccessat(2) any of ${paths.size} paths") {
                paths.filter { NativeBridge.rawExists(it) }.joinToString("\n").ifEmpty { Sentinels.ABSENT }
            },
            jm("Os.stat() any of ${paths.size} paths") {
                paths.filter {
                    runCatching { android.system.Os.stat(it) }.isSuccess
                }.joinToString("\n").ifEmpty { Sentinels.ABSENT }
            },
            sm("ls -d ${paths.joinToString(" ")} 2>/dev/null | grep . || echo ${Sentinels.ABSENT}", "ls the paths"),
            nsm("ls -d ${paths.joinToString(" ")} 2>/dev/null | grep . || echo ${Sentinels.ABSENT}", "popen: ls the paths"),
        )
    )

    private fun pkgItem(id: String, title: String, packages: List<String>, solution: String? = null) = probe(
        "integrity:$id", title, Category.INTEGRITY, solution = solution, methods = listOf(
            jm("getPackageInfo of ${packages.size} packages") { c ->
                packages.filter {
                    runCatching { c.packageManager.getPackageInfo(it, 0) }.isSuccess
                }.joinToString("\n").ifEmpty { Sentinels.ABSENT }
            },
            sm("pm list packages 2>/dev/null | sed 's/^package://' | grep -Fx \"" +
                packages.joinToString("\n") + "\" || echo ${Sentinels.ABSENT}",
                "pm list packages | grep the ${packages.size}"),
        )
    )

    private const val S_ISUID = 0x800
    private const val S_ISGID = 0x400

    private fun suidSgidRoot(uid: Int, gid: Int, mode: Int): Boolean =
        (uid == 0 && (mode and S_ISUID) != 0) || (gid == 0 && (mode and S_ISGID) != 0)

    private fun setuidCandidates(ctx: Context): List<String> =
        (AssetData.packages(ctx, "su_binary_paths") +
            AssetData.packages(ctx, "su_path_dirs").map { "$it/su" }).distinct()

    private fun suidSgidOsStat(path: String): Boolean = runCatching {
        val st = android.system.Os.stat(path)
        suidSgidRoot(st.st_uid, st.st_gid, st.st_mode)
    }.getOrDefault(false)

    private fun suidSgidNio(path: String): Boolean = runCatching {
        val p = java.io.File(path).toPath()
        val uid = java.nio.file.Files.getAttribute(p, "unix:uid") as Int
        val gid = java.nio.file.Files.getAttribute(p, "unix:gid") as Int
        val mode = java.nio.file.Files.getAttribute(p, "unix:mode") as Int
        suidSgidRoot(uid, gid, mode)
    }.getOrDefault(false)

    private fun suidSgidNative(path: String): Boolean {
        val m = NativeBridge.statMode(path) ?: return false
        return suidSgidRoot(m.first, m.second, m.third)
    }

    private fun nioUnixSupported(): Boolean = runCatching {
        java.nio.file.Files.getAttribute(java.io.File("/system").toPath(), "unix:uid"); true
    }.getOrDefault(false)

    private fun setuidRootScan(ctx: Context, test: (String) -> Boolean): String {
        val hits = setuidCandidates(ctx).filter(test).sorted()
        return if (hits.isEmpty()) "clean" else hits.joinToString(",")
    }

    private fun hookedList(scan: String?): String? {
        scan ?: return null
        val hooked = scan.substringAfter('\t', "").split(",")
            .filter { it.endsWith(":patched") || it.endsWith(":trampoline") }
            .map { it.substringBefore(':') }
        return if (hooked.isEmpty()) "clean" else hooked.joinToString(",")
    }

    private fun modList(r: ru.vd171.vdinfos.iso.MapsScan.Result?): String? {
        r ?: return null
        return if (r.modPaths.isEmpty()) "clean" else r.modPaths.joinToString(",")
    }

    private fun isoExtra(c: Context, cls: Class<out ru.vd171.vdinfos.iso.IsoMapsService>): Map<String, String> =
        ru.vd171.vdinfos.iso.IsoChecks.parse(ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, cls)?.extra)

    private val ROOT_PRESENCE_PATHS = listOf(
        "/data/adb", "/data/adb/modules", "/data/adb/rezygisk", "/data/adb/rezygisk/cp64.sock",
        "/data/adb/magisk", "/data/adb/ksu", "/data/adb/tricky_store", "/data/adb/lspd",
        "/sbin/.magisk", "/dev/.magisk", "/cache/.magisk", "/system/bin/su",
    )

    private val ROOT_PRESENCE_CONTAINERS = ROOT_PRESENCE_PATHS.filter { d ->
        ROOT_PRESENCE_PATHS.any { it != d && it.startsWith("$d/") } &&
            ROOT_PRESENCE_PATHS.none { it != d && d.startsWith("$it/") }
    }

    private fun pathOpaque(dir: String): Boolean = try {
        !android.system.Os.access(dir, android.system.OsConstants.X_OK)
    } catch (e: android.system.ErrnoException) {
        e.errno == android.system.OsConstants.EACCES || e.errno == android.system.OsConstants.EPERM
    }

    private fun classifyErrno(errno: Int): String = when (errno) {
        android.system.OsConstants.ENOENT, android.system.OsConstants.ENOTDIR -> "absent"
        android.system.OsConstants.EACCES, android.system.OsConstants.EPERM -> "present(denied)"
        else -> "errno$errno"
    }

    private fun osStatPresence(p: String): String = try {
        android.system.Os.stat(p); "present"
    } catch (e: android.system.ErrnoException) { classifyErrno(e.errno) }

    private fun osLstatPresence(p: String): String = try {
        android.system.Os.lstat(p); "present"
    } catch (e: android.system.ErrnoException) { classifyErrno(e.errno) }

    private fun osAccessPresence(p: String): String = try {
        if (android.system.Os.access(p, android.system.OsConstants.F_OK)) "present" else "present(denied)"
    } catch (e: android.system.ErrnoException) { classifyErrno(e.errno) }

    private fun usbAdbTell(v: String?): String =
        if (!v.isNullOrEmpty() && v.contains("adb")) v else "clean"

    private fun ffsReadyTell(v: String?): String = if (v == "1") "ffs.ready=1" else "clean"

    private fun adbSecureTell(v: String?): String =
        if (Build.VERSION.SDK_INT >= 29 && v == "0") "ro.adb.secure=0" else "clean"

    private fun adbdTell(v: String?): String = if (v == "running") "adbd=running" else "clean"

    private fun snTell(v: String?): String = if (!v.isNullOrEmpty()) "init.svc.sn=$v" else "clean"

    private fun verifierAdbTell(c: Context): String {
        val k = "verifier_verify_adb_installs"
        val v = settingViaCall(c, k) ?: settingViaProvider(c, k) ?: settingViaBulk(c, k)
        return if (!v.isNullOrEmpty()) "$k=$v" else "clean"
    }

    private fun rootPresenceScan(test: (String) -> String): String {
        val opaque = ROOT_PRESENCE_CONTAINERS.filter { pathOpaque(it) }
        val hidden = linkedSetOf<String>()
        val hits = ROOT_PRESENCE_PATHS.mapNotNull { p ->
            val cover = opaque.firstOrNull { p.startsWith("$it/") }
            if (cover != null) { hidden.add(cover); return@mapNotNull null }
            val s = test(p)
            if (s == "absent" || s == "blocked") return@mapNotNull null
            if (s == "present(denied)") {
                val parent = p.substringBeforeLast('/', "")
                if (parent.isNotEmpty() && parent != "/" && pathOpaque(parent)) return@mapNotNull null
            }
            "$p=$s"
        }
        val all = hits + hidden.map { "$it/[*]=indeterminate(parent-opaque)" }
        return if (all.isEmpty()) "clean" else all.joinToString(",")
    }

    private fun lexForms(p: String): List<String> {
        val s = p.lastIndexOf('/')
        val dir = if (s > 0) p.substring(0, s) else ""
        val name = p.substring(s + 1)
        return listOf(p, "$dir/./$name", "$dir//$name", "$dir/..$p", "/.$p", "$p/", "$p/.", "$dir/././$name", "$dir////$name")
    }

    private fun readlinkatRootLeak(): String {
        val opaque = ROOT_PRESENCE_CONTAINERS.filter { pathOpaque(it) }
        fun present(p: String, m: Int) = NativeBridge.statPresence(p, m).let { it == "present" || it == "present(denied)" }
        fun absent(p: String, m: Int) = NativeBridge.statPresence(p, m) == "absent"
        val leak = ROOT_PRESENCE_PATHS.flatMap { base ->
            if (opaque.any { base.startsWith("$it/") }) emptyList()
            else lexForms(base).filter { f -> (present(f, 4) || present(f, 25)) && absent(f, 0) && absent(f, 1) && absent(f, 2) }
        }
        return if (leak.isEmpty()) "clean" else "readlinkat-only presence: ${leak.joinToString(",")}"
    }

    private fun installedPkgSet(c: Context): Set<String> = runCatching {
        @Suppress("QueryPermissionsNeeded")
        c.packageManager.getInstalledPackages(0).map { it.packageName }.toSet()
    }.getOrDefault(emptySet())

    private fun usageAccessGranted(c: Context): Boolean = ru.vd171.vdinfos.data.UsageAccess.granted(c)

    private val USAGE_WINDOW_MS = 7L * 24 * 60 * 60 * 1000

    private fun usageSeenPkgs(c: Context, useEvents: Boolean): Set<String> {
        val usm = c.getSystemService(Context.USAGE_STATS_SERVICE) as? android.app.usage.UsageStatsManager
            ?: return emptySet()
        val end = System.currentTimeMillis()
        val begin = end - USAGE_WINDOW_MS
        val seen = LinkedHashSet<String>()
        runCatching {
            if (useEvents) {
                val ev = usm.queryEvents(begin, end)
                val e = android.app.usage.UsageEvents.Event()
                while (ev.hasNextEvent()) {
                    ev.getNextEvent(e)
                    if (e.eventType == android.app.usage.UsageEvents.Event.MOVE_TO_FOREGROUND)
                        e.packageName?.let { seen.add(it) }
                }
            } else {
                usm.queryUsageStats(android.app.usage.UsageStatsManager.INTERVAL_BEST, begin, end)
                    ?.forEach { st -> if (st.lastTimeUsed >= begin) st.packageName?.let { seen.add(it) } }
            }
        }
        return seen
    }

    private fun usageHiddenLeak(c: Context, useEvents: Boolean): String? {
        if (!usageAccessGranted(c)) return null
        val installed = installedPkgSet(c)
        val leaked = usageSeenPkgs(c, useEvents)
            .asSequence()
            .filter { it != c.packageName && it !in installed }
            .sorted().toList()
        return if (leaked.isEmpty()) "clean" else "hidden-from-pm: ${leaked.joinToString(", ")}"
    }

    private fun usageAccessSummary(c: Context): String {
        if (!usageAccessGranted(c)) return "usage access: denied (grant Usage access to measure)"
        val installed = installedPkgSet(c)
        val seen = usageSeenPkgs(c, false)
        val notInPm = seen.count { it != c.packageName && it !in installed }
        return "usage access: granted | installed-list=${installed.size}" +
            " | usageStats-pkgs=${seen.size} | events-pkgs=${usageSeenPkgs(c, true).size}" +
            " | usage-not-in-pm=$notInPm (only data-dir-present reported as tells)"
    }

    private fun phantomTell(v: String?): String =
        if (v.isNullOrBlank() || v == "null") "clean" else "present=$v"

    @Suppress("NewApi")
    private fun launchSender(c: Context, pkg: String): android.content.IntentSender? =
        runCatching { c.packageManager.getLaunchIntentSenderForPackage(pkg) }.getOrNull()

    private fun launchSenderChannelState(c: Context): String {
        if (Build.VERSION.SDK_INT < 35) return "n/a (requires Android 15+)"
        val absent = "vd.absent." + java.util.UUID.randomUUID().toString().take(12)
        val a = launchSender(c, absent) ?: return "n/a (null for absent packages)"
        val b = launchSender(c, absent)
        return if (a != b) "closed" else "open"
    }

    @Suppress("NewApi")
    private fun lsCreatorSig(c: Context, pkg: String): String {
        val s = launchSender(c, pkg) ?: return "null"
        val cp = runCatching { s.creatorPackage }.getOrNull()
        val cu = runCatching { s.creatorUid }.getOrNull()
        val uh = runCatching { s.creatorUserHandle }.getOrNull()
        return "$cp|$cu|$uh"
    }

    private fun launchSenderPkgLeak(c: Context): String? {
        if (Build.VERSION.SDK_INT < 35) return null
        val absent = "vd.absent." + java.util.UUID.randomUUID().toString().take(12)
        val baseA = launchSender(c, absent) ?: return "clean"
        if (launchSender(c, absent) != baseA) return "clean"
        val sigVisible = lsCreatorSig(c, c.packageName)
        val sigAbsent = lsCreatorSig(c, absent)
        return if (sigVisible == sigAbsent) "clean" else "channel-distinguishes:$sigVisible/$sigAbsent"
    }

    private fun otacertsSubject(): String? {
        val paths = listOf(
            "/system/etc/security/otacerts.zip",
            "/product/etc/security/otacerts.zip",
            "/system_ext/etc/security/otacerts.zip",
            "/vendor/etc/security/otacerts.zip",
        )
        for (p in paths) {
            val f = File(p)
            if (!f.canRead()) continue
            val subj = runCatching {
                java.util.zip.ZipFile(f).use { zip ->
                    val cf = java.security.cert.CertificateFactory.getInstance("X.509")
                    val e = zip.entries().asSequence().firstOrNull { !it.isDirectory &&
                        (it.name.endsWith(".x509.pem") || it.name.endsWith(".pem") || it.name.endsWith(".der") || it.name.endsWith(".crt")) }
                    e?.let {
                        zip.getInputStream(it).use { ins ->
                            (cf.generateCertificate(ins) as? java.security.cert.X509Certificate)?.subjectX500Principal?.name
                        }
                    }
                }
            }.getOrNull()
            if (subj != null) return subj
        }
        return null
    }

    private fun otacertsIsAospTestKey(subject: String): Boolean {
        val s = subject.uppercase()
        return s.contains("ANDROID@ANDROID.COM") ||
            (s.contains("CN=ANDROID") && s.contains("O=ANDROID") && s.contains("OU=ANDROID"))
    }

    private fun otacertsSignerClass(): String? =
        otacertsSubject()?.let { if (otacertsIsAospTestKey(it)) "aosp-test-key" else "release" }

    @Suppress("DEPRECATION", "PackageManagerGetSignatures")
    private fun platformCertSubject(c: Context): String? = runCatching {
        val pm = c.packageManager
        val cert: ByteArray? = if (Build.VERSION.SDK_INT >= 28) {
            val pi = pm.getPackageInfo("android", android.content.pm.PackageManager.GET_SIGNING_CERTIFICATES)
            pi.signingInfo?.apkContentsSigners?.firstOrNull()?.toByteArray()
        } else {
            val pi = pm.getPackageInfo("android", android.content.pm.PackageManager.GET_SIGNATURES)
            pi.signatures?.firstOrNull()?.toByteArray()
        }
        cert ?: return null
        val x = java.security.cert.CertificateFactory.getInstance("X.509")
            .generateCertificate(cert.inputStream()) as? java.security.cert.X509Certificate
        x?.subjectX500Principal?.name
    }.getOrNull()

    private fun orgToken(s: String?): String =
        s.orEmpty().lowercase().replace(Regex("[^a-z0-9]"), "")

    private fun platformSignerVerdict(c: Context): String? {
        val subject = platformCertSubject(c) ?: return null
        if (otacertsIsAospTestKey(subject)) return "aosp-test-key platform cert"
        val certOrg = orgToken(Regex("O=([^,]+)").find(subject)?.groupValues?.get(1))
        val claimed = orgToken(Build.MANUFACTURER)
        if (certOrg.isEmpty() || claimed.isEmpty()) return "indeterminate (O=${certOrg.ifEmpty { "?" }} manufacturer=${claimed.ifEmpty { "?" }})"
        val coherent = certOrg.contains(claimed) || claimed.contains(certOrg)
        return if (coherent) "coherent" else "mismatch (platform O=$certOrg vs claimed manufacturer=$claimed)"
    }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        val suDirs = AssetData.packages(ctx, "su_path_dirs")
        add(pathItem("su", ctx.getString(R.string.t_su_binary), AssetData.packages(ctx, "su_binary_paths")))
        add(probe("integrity:which_su", ctx.getString(R.string.t_su_on_path), Category.INTEGRITY, listOf(
            nm("access() over ${suDirs.size} PATH dirs") {
                suDirs.map { "$it/su" }.firstOrNull { NativeBridge.exists(it) } ?: Sentinels.ABSENT
            },
            sm("{ " + suDirs.joinToString("; ") { "[ -e $it/su ] && echo $it/su" } +
                "; echo ${Sentinels.ABSENT}; } | head -1",
                "test -e over the same dirs"),
            sm("which su 2>/dev/null || echo ${Sentinels.ABSENT}", "which su (walks PATH)", compare = false),
            nsm("which su 2>/dev/null || echo ${Sentinels.ABSENT}", "popen: which su", compare = false),
        )))
        val suidFind = "x=$(find -L " + setuidCandidates(ctx).joinToString(" ") { "'$it'" } +
            " \\( -uid 0 -perm -4000 \\) -o \\( -gid 0 -perm -2000 \\) 2>/dev/null | sort | paste -sd, -); echo \"\${x:-clean}\""
        add(probe("integrity:setuid_root", ctx.getString(R.string.t_setuid_root), Category.INTEGRITY, listOf(
            jm("no setuid/setgid-root su reachable (expected)") { "clean" },
            jm("Os.stat uid/gid/mode (libcore)") { setuidRootScan(ctx) { p -> suidSgidOsStat(p) } },
            jm("Files unix:uid/gid/mode (nio)") { if (nioUnixSupported()) setuidRootScan(ctx) { p -> suidSgidNio(p) } else null },
            nm("stat(2) uid/gid/mode (native libc)") { setuidRootScan(ctx) { p -> suidSgidNative(p) } },
            sm(suidFind, "find -L setuid/setgid root over su paths"),
        )))

        add(probe("integrity:root_dirs_present", ctx.getString(R.string.t_root_dirs_present), Category.INTEGRITY, listOf(
            jm("expected: every root dir reads absent") { "clean" },
            jm("Os.stat errno (libcore)") { rootPresenceScan { osStatPresence(it) } },
            jm("Os.lstat errno (libcore)") { rootPresenceScan { osLstatPresence(it) } },
            jm("Os.access F_OK errno (libcore)") { rootPresenceScan { osAccessPresence(it) } },
            nm("statx(AT_FDCWD) direct (native libc)") { rootPresenceScan { NativeBridge.statPresence(it, 0) } },
            nm("fstatat(AT_FDCWD) direct (native libc)") { rootPresenceScan { NativeBridge.statPresence(it, 1) } },
            nm("faccessat(AT_FDCWD) F_OK direct (native libc)") { rootPresenceScan { NativeBridge.statPresence(it, 2) } },
            nm("openat2(AT_FDCWD) O_PATH direct", compare = false) { rootPresenceScan { NativeBridge.statPresence(it, 3) } },
            nm("readlinkat(AT_FDCWD) direct", compare = false) { rootPresenceScan { NativeBridge.statPresence(it, 4) } },
            jm("discriminator: readlinkat resolves a root dir the stat family reports ENOENT (any lexical form or raw syscall)") { readlinkatRootLeak() },
            nm("statvfs direct (native libc)", compare = false) { rootPresenceScan { NativeBridge.statPresence(it, 5) } },
            nm("stat64 direct (native libc)", compare = false) { rootPresenceScan { NativeBridge.statPresence(it, 6) } },
            nm("faccessat2(AT_FDCWD) F_OK direct", compare = false) { rootPresenceScan { NativeBridge.statPresence(it, 7) } },
            nm("raw newfstatat(2) syscall") { rootPresenceScan { NativeBridge.statPresence(it, 19) } },
            nm("raw faccessat(2) syscall") { rootPresenceScan { NativeBridge.statPresence(it, 21) } },
            nm("raw statx(2) syscall") { rootPresenceScan { NativeBridge.statPresence(it, 23) } },
            sm("op=\"\"; for d in " + ROOT_PRESENCE_CONTAINERS.joinToString(" ") +
                "; do if ls -d \"\$d\" >/dev/null 2>&1; then :; " +
                "elif ls -d \"\$d\" 2>&1 | grep -qiE 'denied|permission'; then op=\"\$op \$d\"; fi; done; " +
                "{ for p in " + ROOT_PRESENCE_PATHS.joinToString(" ") +
                "; do sk=0; for d in \$op; do case \"\$p\" in \"\$d\"/?*) sk=1;; esac; done; " +
                "[ \"\$sk\" = 1 ] && continue; " +
                "if ls -d \"\$p\" >/dev/null 2>&1; then echo \"\$p=present\"; " +
                "elif ls -d \"\$p\" 2>&1 | grep -qiE 'denied|permission'; then " +
                "par=\${p%/*}; " +
                "if ls -d \"\$par/.vdnope\" 2>&1 | grep -qiE 'denied|permission'; then :; " +
                "else echo \"\$p=denied\"; fi; fi; done; " +
                "for d in \$op; do echo \"\$d/[*]=indeterminate(parent-opaque)\"; done; } " +
                "| paste -sd, - | grep . || echo clean",
                "ls -d errno per root dir (unhooked subprocess)"),
            jm("discriminator: coherent-absent is hook-produced when su-path libc fns are inline-hooked (see inline_hooks)", compare = false) {
                val h = NativeBridge.inlineHookScan()?.substringAfter('\t')?.takeIf { it.isNotBlank() && it != "-" }
                if (h.isNullOrBlank()) "no inline hook on su-path fns: absent is hook-free (a coherent native hook would make every method agree absent; this does not)"
                else "inline-hooked: $h -> every-method-absent above is produced by the native hook (expected on a hooked device), not a hook-free clean read"
            },
        )))

        add(probe("integrity:adb_usb_exposure", ctx.getString(R.string.t_adb_usb_exposure), Category.INTEGRITY, listOf(
            jm("expected: no adb/usb debugging exposure") { "clean" },
            jm("sys.usb.config (SystemProperties)") { usbAdbTell(SystemProps.get("sys.usb.config")) },
            jm("sys.usb.state (SystemProperties)") { usbAdbTell(SystemProps.get("sys.usb.state")) },
            jm("persist.sys.usb.config (SystemProperties)") { usbAdbTell(SystemProps.get("persist.sys.usb.config")) },
            jm("sys.usb.ffs.ready (SystemProperties)") { ffsReadyTell(SystemProps.get("sys.usb.ffs.ready")) },
            jm("ro.adb.secure=0 on SDK>=29 (SystemProperties)") { adbSecureTell(SystemProps.get("ro.adb.secure")) },
            jm("init.svc.adbd (SystemProperties)") { adbdTell(SystemProps.get("init.svc.adbd")) },
            jm("init.svc.sn (SystemProperties)") { snTell(SystemProps.get("init.svc.sn")) },
        )))

        add(probe("integrity:verifier_adb_installs", ctx.getString(R.string.t_verifier_adb_installs), Category.INTEGRITY, listOf(
            jm("expected: verify-apps-over-USB setting absent (null)") { "clean" },
            jm("verifier_verify_adb_installs (settings 3-store)") { c -> verifierAdbTell(c) },
        )))

        add(probe("integrity:hidden_app_usage_leak", ctx.getString(R.string.t_hidden_app_usage_leak), Category.INTEGRITY, listOf(
            jm("expected: usage-stats APIs reveal no package hidden from the installed-package list") { "clean" },
            jm("UsageStatsManager.queryUsageStats vs PackageManager.getInstalledPackages") { c -> usageHiddenLeak(c, useEvents = false) },
            jm("UsageStatsManager.queryEvents vs PackageManager.getInstalledPackages") { c -> usageHiddenLeak(c, useEvents = true) },
            jm("usage access state + enumerated counts", compare = false) { c -> usageAccessSummary(c) },
        ), solution = ctx.getString(R.string.sol_hidden_app_usage_leak)))

        add(probe("integrity:launch_sender_pkg_leak", ctx.getString(R.string.t_launch_sender_pkg_leak), Category.INTEGRITY, listOf(
            jm("expected: launch IntentSender does not expose an installed package") { "clean" },
            jm("launch IntentSender creator-signature: visible installed vs absent") { c -> launchSenderPkgLeak(c) },
            jm("launch IntentSender channel state", compare = false) { c -> launchSenderChannelState(c) },
        ), solution = ctx.getString(R.string.sol_launch_sender_pkg_leak)))

        add(probe("integrity:otacerts_signing_key", ctx.getString(R.string.t_otacerts_signing_key), Category.INTEGRITY, listOf(
            jm("expected: the OTA signing certificate is a release key") { "release" },
            jm("otacerts.zip signing certificate class") { otacertsSignerClass() },
            jm("OTA cert subject and ro.build.tags", compare = false) {
                otacertsSubject()?.let { "subject=$it | ro.build.tags=${Build.TAGS}" } ?: "otacerts unreadable or absent"
            },
        ), solution = ctx.getString(R.string.sol_otacerts_signing_key)))

        add(probe("integrity:platform_signer", ctx.getString(R.string.t_platform_signer), Category.INTEGRITY, listOf(
            jm("expected: the platform package signer belongs to the claimed device OEM") { "coherent" },
            jm("android package signer vs claimed manufacturer") { c -> platformSignerVerdict(c) },
            jm("platform cert subject and claimed identity", compare = false) { c ->
                val subj = platformCertSubject(c) ?: return@jm "platform cert unreadable"
                "subject=$subj | manufacturer=${Build.MANUFACTURER} brand=${Build.BRAND}"
            },
        ), solution = ctx.getString(R.string.sol_platform_signer)))

        add(probe("integrity:libc_identity", ctx.getString(R.string.t_libc_identity), Category.INTEGRITY, listOf(
            nm("expected: the loaded libc mapping matches the on-disk file") { "clean" },
            nm("loaded libc mapping (dev/inode) vs on-disk libc.so") { NativeBridge.libcIdentity() },
        ), solution = ctx.getString(R.string.sol_libc_identity)))

        add(probe("integrity:statfs_consistency", ctx.getString(R.string.t_statfs_consistency), Category.INTEGRITY, listOf(
            jm("android.os.StatFs(/data) totalBytes") { runCatching { android.os.StatFs("/data").totalBytes.toString() }.getOrNull() },
            nm("native statvfs(/data) total") { NativeBridge.statfs("/data")?.total?.toString() },
        ), solution = ctx.getString(R.string.sol_statfs_consistency)))

        add(probe("integrity:feature_spoof", ctx.getString(R.string.t_feature_spoof), Category.INTEGRITY, listOf(
            jm("expected: feature-query paths and hardware agree") { "clean" },
            jm("hasSystemFeature vs getSystemAvailableFeatures (PM self-coherence)") { c ->
                val pm = c.packageManager
                val avail = runCatching { pm.systemAvailableFeatures.mapNotNull { it.name }.toSet() }.getOrDefault(emptySet())
                val feats = listOf(
                    "android.hardware.wifi", "android.hardware.bluetooth", "android.hardware.camera.any",
                    "android.hardware.nfc", "android.hardware.telephony", "android.hardware.fingerprint",
                    "android.hardware.location.gps", "android.hardware.sensor.accelerometer",
                )
                val bad = feats.mapNotNull { feat ->
                    val has = runCatching { pm.hasSystemFeature(feat) }.getOrDefault(false)
                    val inAvail = feat in avail
                    if (has != inAvail) "$feat: hasSystemFeature=$has getSystemAvailableFeatures=$inAvail" else null
                }
                if (bad.isEmpty()) "clean" else bad.joinToString(" | ")
            },
            jm("/sys hardware node present vs hasSystemFeature hidden") { c ->
                val pm = c.packageManager
                val checks = listOf(
                    "android.hardware.wifi" to listOf("/sys/class/net/wlan0"),
                    "android.hardware.bluetooth" to listOf("/sys/class/bluetooth"),
                    "android.hardware.camera.any" to listOf("/sys/class/video4linux/video0", "/dev/video0"),
                    "android.hardware.nfc" to listOf("/sys/class/nfc"),
                )
                val bad = checks.mapNotNull { (feat, nodes) ->
                    val has = runCatching { pm.hasSystemFeature(feat) }.getOrDefault(false)
                    val hw = nodes.any { NativeBridge.exists(it) }
                    if (hw && !has) "$feat: hardware present but feature hidden" else null
                }
                if (bad.isEmpty()) "clean" else bad.joinToString(" | ")
            },
        ), solution = ctx.getString(R.string.sol_feature_spoof)))

        add(probe("integrity:phantom_monitor_setting", ctx.getString(R.string.t_phantom_monitor_setting), Category.INTEGRITY, listOf(
            jm("expected: app reads stock (setting absent)") { "clean" },
            jm("Settings.Global settings_enable_monitor_phantom_procs") { c ->
                phantomTell(android.provider.Settings.Global.getString(c.contentResolver, "settings_enable_monitor_phantom_procs"))
            },
            jm("provider settings/global (selection name=?)") { c ->
                phantomTell(settingViaProvider(c, "settings_enable_monitor_phantom_procs"))
            },
            jm("provider call GET_global") { c ->
                phantomTell(settingViaCall(c, "settings_enable_monitor_phantom_procs"))
            },
            jm("control: settings get global", compare = false) {
                Exec.run("settings get global settings_enable_monitor_phantom_procs")
            },
        ), solution = ctx.getString(R.string.sol_phantom_monitor)))

        add(probe("integrity:phantom_monitor_prop", ctx.getString(R.string.t_phantom_monitor_prop), Category.INTEGRITY, listOf(
            jm("expected: feature-flag override prop absent") { "clean" },
            jm("SystemProperties persist.sys.fflag.override.settings_enable_monitor_phantom_procs") {
                phantomTell(SystemProps.get("persist.sys.fflag.override.settings_enable_monitor_phantom_procs"))
            },
            nm("__system_property_read_callback (native)") {
                phantomTell(NativeBridge.sysprop("persist.sys.fflag.override.settings_enable_monitor_phantom_procs"))
            },
            jm("control: getprop", compare = false) {
                Exec.run("getprop persist.sys.fflag.override.settings_enable_monitor_phantom_procs")
            },
        ), solution = ctx.getString(R.string.sol_phantom_monitor)))

        add(probe("integrity:maps_isolated", ctx.getString(R.string.t_maps_isolated), Category.INTEGRITY, listOf(
            jm("root-module paths in maps (main process)") {
                modList(ru.vd171.vdinfos.iso.MapsScan.scan())
            },
            jm("root-module paths in maps (isolated app-zygote child)") { c ->
                modList(ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)?.maps)
            },
            jm("root-module paths in maps (isolated system-zygote child)") { c ->
                modList(ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)?.maps)
            },
            jm("maps: main vs isolated children", compare = false) { c ->
                fun fmt(r: ru.vd171.vdinfos.iso.MapsScan.Result?, label: String) =
                    if (r == null) "$label: unavailable" else
                        "$label: modules=${r.modCount} rx-anon=${r.rxAnon} deleted=${r.deleted}" +
                            (if (r.modPaths.isEmpty()) "" else "\n  paths: ${r.modPaths.joinToString(", ")}")
                val m = ru.vd171.vdinfos.iso.MapsScan.scan()
                val az = ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)
                val sy = ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)
                "${fmt(m, "main")}\n${fmt(az?.maps, "iso app-zygote")}\n${fmt(sy?.maps, "iso system-zygote")}"
            },
        )))

        add(probe("integrity:inline_hooks", ctx.getString(R.string.t_inline_hooks), Category.INTEGRITY, listOf(
            jm("expected: no inline hook on the libc su-path functions") { "clean" },
            jm("inline-hooked libc functions (main process)") {
                hookedList(NativeBridge.inlineHookScan())
            },
            jm("inline-hooked libc functions (isolated app-zygote child)") { c ->
                hookedList(ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)?.hooks)
            },
            jm("inline-hooked libc functions (isolated system-zygote child)") { c ->
                hookedList(ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)?.hooks)
            },
            jm("per-function: main vs isolated", compare = false) { c ->
                val m = NativeBridge.inlineHookScan()?.substringAfter('\t') ?: "unavailable"
                val az = ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)?.hooks?.substringAfter('\t') ?: "unavailable"
                val sy = ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)?.hooks?.substringAfter('\t') ?: "unavailable"
                "main:              $m\niso app-zygote:    $az\niso system-zygote: $sy"
            },
        )))

        add(probe("integrity:linker_maps_gap", ctx.getString(R.string.t_linker_maps_gap), Category.INTEGRITY, listOf(
            jm("expected: every linker-loaded lib shows its path in maps") { "clean" },
            jm("libs in the linker but hidden from maps (main process)") {
                NativeBridge.linkerMapsGap()?.substringAfter('\t')
            },
            jm("libs in the linker but hidden from maps (isolated app-zygote child)") { c ->
                ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)?.linker?.substringAfter('\t')
            },
            jm("libs in the linker but hidden from maps (isolated system-zygote child)") { c ->
                ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)?.linker?.substringAfter('\t')
            },
        )))

        add(probe("integrity:tracerpid_isolated", ctx.getString(R.string.t_tracerpid_iso), Category.INTEGRITY, listOf(
            jm("TracerPid (main process)") { ru.vd171.vdinfos.iso.IsoChecks.tracerPid() },
            jm("TracerPid (isolated app-zygote child)") { c ->
                ru.vd171.vdinfos.iso.IsoChecks.parse(ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)?.extra)["tracerpid"]
            },
            jm("TracerPid (isolated system-zygote child)") { c ->
                ru.vd171.vdinfos.iso.IsoChecks.parse(ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)?.extra)["tracerpid"]
            },
        )))

        add(probe("integrity:fd_isolated", ctx.getString(R.string.t_fd_iso), Category.INTEGRITY, listOf(
            jm("open fds to root paths/daemons (main process)") { ru.vd171.vdinfos.iso.IsoChecks.fdScan() },
            jm("open fds to root paths/daemons (isolated app-zygote child)") { c ->
                ru.vd171.vdinfos.iso.IsoChecks.parse(ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)?.extra)["fd"]
            },
            jm("open fds to root paths/daemons (isolated system-zygote child)") { c ->
                ru.vd171.vdinfos.iso.IsoChecks.parse(ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)?.extra)["fd"]
            },
        )))

        val isoSuPaths = AssetData.packages(ctx, "su_binary_paths") +
            AssetData.packages(ctx, "su_path_dirs").map { "$it/su" }
        val isoPropKeys = AssetData.packages(ctx, "prop_dual_keys")

        add(probe("integrity:selinux_isolated", ctx.getString(R.string.t_selinux_iso), Category.INTEGRITY, listOf(
            jm("expected: an app SELinux domain in every process") { "ok" },
            jm("domain not a root/su domain (main process)") { ru.vd171.vdinfos.iso.IsoChecks.seLinuxContextBad() },
            jm("domain not a root/su domain (isolated app-zygote child)") { c ->
                isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)["sectxbad"]
            },
            jm("domain not a root/su domain (isolated system-zygote child)") { c ->
                isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)["sectxbad"]
            },
            jm("enforce (where readable) + context per process", compare = false) { c ->
                val az = isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)
                val sy = isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)
                "main:              enforce=${ru.vd171.vdinfos.iso.IsoChecks.seLinuxEnforce()} ${ru.vd171.vdinfos.iso.IsoChecks.seLinuxContext()}\n" +
                    "iso app-zygote:    enforce=${az["seenforce"] ?: "n/a"} ${az["sectx"] ?: "unavailable"}\n" +
                    "iso system-zygote: enforce=${sy["seenforce"] ?: "n/a"} ${sy["sectx"] ?: "unavailable"}"
            },
        )))

        add(probe("integrity:su_isolated", ctx.getString(R.string.t_su_iso), Category.INTEGRITY, listOf(
            jm("expected: no su visible to file/access/stat/native in any process") { "clean" },
            jm("su by file/access/stat/native (main process)") { ru.vd171.vdinfos.iso.IsoChecks.suScan(isoSuPaths) },
            jm("su by file/access/stat/native (isolated app-zygote child)") { c ->
                isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)["su"]
            },
            jm("su by file/access/stat/native (isolated system-zygote child)") { c ->
                isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)["su"]
            },
        )))

        add(probe("integrity:prop_dual_isolated", ctx.getString(R.string.t_prop_dual_iso), Category.INTEGRITY, listOf(
            jm("expected: native and JVM agree on every sensitive prop") { "clean" },
            jm("props where native != JVM (main process)") { ru.vd171.vdinfos.iso.IsoChecks.propDual(isoPropKeys) },
            jm("props where native != JVM (isolated app-zygote child)") { c ->
                isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)["propnj"]
            },
            jm("props where native != JVM (isolated system-zygote child)") { c ->
                isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)["propnj"]
            },
        )))

        add(probe("integrity:smaps_isolated", ctx.getString(R.string.t_smaps_iso), Category.INTEGRITY, listOf(
            jm("expected: no anonymous write+exec mapping in any process") { "clean" },
            jm("anon rwx regions in smaps (main process)") { ru.vd171.vdinfos.iso.IsoChecks.smapsRwx() },
            jm("anon rwx regions in smaps (isolated app-zygote child)") { c ->
                isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)["smaprwx"]
            },
            jm("anon rwx regions in smaps (isolated system-zygote child)") { c ->
                isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)["smaprwx"]
            },
        )))

        add(probe("integrity:mountinfo_isolated", ctx.getString(R.string.t_mountinfo_iso), Category.INTEGRITY, listOf(
            jm("expected: no root-module mount in any process") { "clean" },
            jm("root mounts in /proc/self/mountinfo (main process)") { ru.vd171.vdinfos.iso.IsoChecks.mountMarkers() },
            jm("root mounts in mountinfo (isolated app-zygote child)") { c ->
                isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsService::class.java)["mnt"]
            },
            jm("root mounts in mountinfo (isolated system-zygote child)") { c ->
                isoExtra(c, ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)["mnt"]
            },
        )))

        add(pathItem("magisk", ctx.getString(R.string.t_magisk), AssetData.packages(ctx, "magisk_paths")))
        add(pathItem("kernelsu", ctx.getString(R.string.t_kernelsu), AssetData.packages(ctx, "kernelsu_paths")))
        add(pathItem("apatch", ctx.getString(R.string.t_apatch), AssetData.packages(ctx, "apatch_paths")))
        add(pathItem("xposed", ctx.getString(R.string.t_xposed_lsposed_riru), AssetData.packages(ctx, "xposed_paths")))

        val mapsNeedles = AssetData.packages(ctx, "injection_maps_needles")
        val mapsGrep = mapsNeedles.joinToString("|")
        add(probe("integrity:maps", ctx.getString(R.string.t_injected_libs_proc_self_maps), Category.INTEGRITY, listOf(
            nm("scan /proc/self/maps") {
                val maps = NativeBridge.readFile("/proc/self/maps", 1 shl 20) ?: return@nm null
                mapsNeedles.filter {
                    maps.contains(it, ignoreCase = true)
                }.joinToString(", ").ifEmpty { "clean" }
            },
            sm("grep -iE '$mapsGrep' /proc/self/maps 2>/dev/null | head || echo ${Sentinels.ABSENT}", "grep maps"),
            nsm("grep -iE '$mapsGrep' /proc/self/maps 2>/dev/null | head || echo ${Sentinels.ABSENT}", "popen: grep maps"),
        )))

        add(probe("integrity:dlphdr", ctx.getString(R.string.t_dlphdr), Category.INTEGRITY, listOf(
            jm("no injected/outside module (expected)") { "0/0" },
            jm("dl_iterate_phdr hits/outside") {
                NativeBridge.dlPhdr()?.let { "${it.hitCount}/${it.outsideCount}" } ?: Sentinels.NONE
            },
            jm("modules", compare = false) {
                NativeBridge.dlPhdr()?.let { "hits=${it.hits}\noutside=${it.outside}" } ?: Sentinels.NONE
            },
        )))

        add(probe("integrity:local_tmp_list", ctx.getString(R.string.t_local_tmp_list), Category.INTEGRITY, listOf(
            jm("no app-visible entry in /data/local/tmp (expected)") { "clean" },
            jm("getdents64 /data/local/tmp") {
                when (val r = NativeBridge.dirList("/data/local/tmp") ?: Sentinels.NONE) {
                    "EACCES", "ENOENT", "empty" -> "clean"
                    else -> "VISIBLE: $r"
                }
            },
        )))

        add(probe("integrity:maps_exec", ctx.getString(R.string.t_maps_exec), Category.INTEGRITY, listOf(
            jm("no injected exec map (expected)") { "0/0" },
            jm("deleted-exec ELF/RWX and outside-allowlist count") {
                NativeBridge.mapsDeletedExec()?.let { "${it.delCount}/${it.outCount}" } ?: Sentinels.NONE
            },
            jm("detail", compare = false) {
                NativeBridge.mapsDeletedExec()?.let { "del=${it.delHits}\noutside=${it.outHits}\nbenign(ART)=${it.benign}" } ?: Sentinels.NONE
            },
        )))

        add(probe("integrity:elf_in_anon", ctx.getString(R.string.t_elf_in_anon), Category.INTEGRITY, listOf(
            jm("expected: no ELF header inside a nameless anonymous mapping") { "clean" },
            jm("ELF header at the start of a nameless anonymous mapping (a file-backed .so morphed to anon leaves its ELF header; a nameless executable .so is an injection tell)") {
                val a = NativeBridge.anonExecElf() ?: return@jm Sentinels.NONE
                if (a.elfAnonCount == 0) "clean"
                else "ELF-in-anon x${a.elfAnonCount}: ${a.elfHits?.takeIf { it != "-" } ?: "?"}"
            },
            jm("addresses", compare = false) {
                NativeBridge.anonExecElf()?.elfHits?.takeIf { it != "-" } ?: Sentinels.NONE
            },
            jm("anon-injection residual: main vs isolated children (elf,exec | maps del/out)", compare = false) { c ->
                fun iso(cls: Class<out ru.vd171.vdinfos.iso.IsoMapsService>): String {
                    val x = ru.vd171.vdinfos.iso.IsoChecks.parse(ru.vd171.vdinfos.iso.IsoMapsClient.scan(c, cls)?.extra)
                    return "anonelf=${x["anonelf"] ?: "unavailable"} mapsexec=${x["mapsexec"] ?: "unavailable"}"
                }
                val m = NativeBridge.anonExecElf()?.let { "elf${it.elfAnonCount},exec${it.anonExecCount}" } ?: "unavailable"
                val me = NativeBridge.mapsDeletedExec()?.let { "${it.delCount}/${it.outCount}" } ?: "unavailable"
                "main:              anonelf=$m mapsexec=$me\n" +
                    "iso app-zygote:    ${iso(ru.vd171.vdinfos.iso.IsoMapsService::class.java)}\n" +
                    "iso system-zygote: ${iso(ru.vd171.vdinfos.iso.IsoMapsServiceSys::class.java)}"
            },
        )))

        add(probe("integrity:anon_exec_orphan", ctx.getString(R.string.t_anon_exec_orphan), Category.INTEGRITY, listOf(
            jm("expected: no executable mapping without a backing file (all code is file- or memfd-backed)") { "clean" },
            jm("nameless executable mappings (a .so morphed file-backed -> anon, or injected code, keeps execute permission but loses its path; memfd and bracketed regions are excluded)") {
                val a = NativeBridge.anonExecElf() ?: return@jm Sentinels.NONE
                if (a.anonExecCount == 0) "clean"
                else "nameless-exec x${a.anonExecCount}: ${a.anonExecHits.takeIf { it != "-" } ?: "?"}"
            },
            jm("addresses/sizes", compare = false) {
                NativeBridge.anonExecElf()?.anonExecHits?.takeIf { it != "-" } ?: Sentinels.NONE
            },
        )))

        add(probe("integrity:named_anon_exec", ctx.getString(R.string.t_named_anon_exec), Category.INTEGRITY, listOf(
            jm("expected: no ELF mapped under a [anon:...] executable region (ART named-anon regions never carry ELF magic)") { "clean" },
            nm("[anon:...] executable regions carrying ELF magic (a .so injected under a forged anon name, escaping the nameless-exec scan)") {
                val m = NativeBridge.namedAnonElf() ?: return@nm Sentinels.NONE
                if (m.elfNamedCount == 0) "clean"
                else "NAMED_ANON_ELF x${m.elfNamedCount}: ${m.elfHits.takeIf { it != "-" } ?: "?"}"
            },
            nm("all [anon:...] executable region names", compare = false) {
                val m = NativeBridge.namedAnonElf() ?: return@nm Sentinels.NONE
                "exec_named=${m.execNamedCount} names=${m.names}"
            },
        )))

        add(probe("integrity:anon_elf_struct", ctx.getString(R.string.t_anon_elf_struct), Category.INTEGRITY, listOf(
            jm("expected: no [anon:] executable region is a structural ELF image with its magic scrubbed") { "clean" },
            nm("an [anon:...] / nameless executable region (or its r-- header neighbor) whose \\x7fELF magic is GONE yet whose ELF64-AArch64 header fields (e_machine/e_ehsize/e_phentsize) stay consistent: a .so whose magic was zeroed and remapped under a JIT-like anon name to pass a magic-only scan; name-independent, survives the dalvik-jit-code-cache disguise") {
                val x = NativeBridge.anonElfStruct() ?: return@nm Sentinels.NONE
                if (x.scrubbedCount == 0) "clean" else "ANON_ELF_SCRUBBED x${x.scrubbedCount}: ${x.hits}"
            },
            nm("scrubbed-ELF anon regions / total anon executable regions", compare = false) {
                val x = NativeBridge.anonElfStruct() ?: return@nm Sentinels.NONE
                "scrubbed=${x.scrubbedCount} exec_anon=${x.execAnonCount}"
            },
        )))

        add(probe("integrity:zygisk_loader_footprint", ctx.getString(R.string.t_zygisk_loader_footprint), Category.INTEGRITY, listOf(
            jm("expected: no zygisk 2nd-stage loader residue (no non-ART memfd exec, no libzygisk mapping)") { "clean" },
            nm("memfd-backed executable regions not named as the ART jit cache (an injected loader agent kept in a memfd survives name-morphing, since memfd is not a /data path)") {
                val f = NativeBridge.zygiskFootprint() ?: return@nm Sentinels.NONE
                if (f.memfdExecSusp == 0) "clean" else "MEMFD_EXEC x${f.memfdExecSusp}"
            },
            nm("mappings referencing a zygisk loader (file-backed libzygisk.so or a named region)") {
                val f = NativeBridge.zygiskFootprint() ?: return@nm Sentinels.NONE
                if (f.zygiskHits == 0) "clean" else "ZYGISK_MAP x${f.zygiskHits}: ${f.zygiskPaths}"
            },
            nm("all memfd exec region names (ART jit vs suspect) + total", compare = false) {
                val f = NativeBridge.zygiskFootprint() ?: return@nm Sentinels.NONE
                "memfd_exec_total=${f.memfdExecTotal} names=${f.memfdNames}"
            },
        )))

        add(probe("integrity:heap_hook_xref", ctx.getString(R.string.t_heap_hook_xref), Category.INTEGRITY, listOf(
            jm("expected: no heap pointer references injected executable memory (no dangling hook table)") { "clean" },
            nm("writable-heap words pointing inside a non-system executable region (a hook/trampoline table inherited from zygote by fork keeps pointing into the injected agent: the structural tell, name-independent)") {
                val x = NativeBridge.heapHookXref() ?: return@nm Sentinels.NONE
                if (x.suspectExecCount == 0 || x.xrefs == 0) "clean"
                else "HEAP_HOOK_XREF x${x.xrefs} -> ${x.hitRegions}"
            },
            nm("suspect executable region count / referenced regions", compare = false) {
                val x = NativeBridge.heapHookXref() ?: return@nm Sentinels.NONE
                "suspect_exec=${x.suspectExecCount} refs=${x.hitRegions}"
            },
        )))

        add(probe("integrity:hidden_region_xref", ctx.getString(R.string.t_hidden_region_xref), Category.INTEGRITY, listOf(
            jm("expected: no heap pointer targets readable memory absent from /proc/self/maps") { "clean" },
            nm("heap words pointing into a READABLE address that no /proc/self/maps entry covers (a mapping hidden by maps-morph/sus_map is still mapped, so heap references into the morphed injected .so land in a maps gap: maps- AND name-independent, defeats the comprehensive morph)") {
                val x = NativeBridge.hiddenRegionXref() ?: return@nm Sentinels.NONE
                if (x.hiddenPtrs < 4) "clean"
                else "HIDDEN_REGION x${x.hiddenPtrs} in ${x.hiddenPages} page(s): ${x.pages}"
            },
            nm("hidden-region pointer count / target pages", compare = false) {
                val x = NativeBridge.hiddenRegionXref() ?: return@nm Sentinels.NONE
                "hidden_ptrs=${x.hiddenPtrs} pages=${x.hiddenPages} ${x.pages}"
            },
        )))

        add(probe("integrity:gap_mapped", ctx.getString(R.string.t_gap_mapped), Category.INTEGRITY, listOf(
            jm("expected: no ELF image lives in a /proc/self/maps gap") { "clean" },
            nm("an ELF header found at the start of a run that a syscall oracle (mincore) reports MAPPED yet no /proc/self/maps entry lists: the maps text line was deleted but the VMA stays mapped, the hiding paradox a text-layer morph cannot cover (mincore walks a separate kernel path; header read via pipe since /proc/self/mem is denied). Confirmed absent from a second maps snapshot to drop managed-runtime race allocations") {
                val x = NativeBridge.gapMapped() ?: return@nm Sentinels.NONE
                if (x.gapElf > 0) "GAP_HIDDEN_ELF x${x.gapElf} (mapped-but-unlisted ELF image): ${x.addrs}" else "clean"
            },
            nm("mincore-mapped gap runs (noisy, includes managed-runtime churn) / ELF-confirmed / gaps scanned", compare = false) {
                val x = NativeBridge.gapMapped() ?: return@nm Sentinels.NONE
                "gap_mapped=${x.gapMapped} gap_elf=${x.gapElf} runs=${x.pages} gaps=${x.gapsScanned}"
            },
        )))

        add(probe("integrity:map_files_leak", ctx.getString(R.string.t_map_files_leak), Category.INTEGRITY, listOf(
            jm("expected: no /data/adb-backed mapping leaks through /proc/self/map_files") { "clean" },
            nm("readlink of every /proc/self/map_files entry: a root-module .so stays file-backed under /data/adb here even when sus_map scrubs its /proc/self/maps text line (an interface the maps morph often misses)") {
                val names = java.io.File("/proc/self/map_files").list() ?: return@nm Sentinels.RESTRICTED
                val hits = names.mapNotNull { n ->
                    runCatching { android.system.Os.readlink("/proc/self/map_files/$n") }.getOrNull()
                }.filter { it.contains("/data/adb") }.distinct()
                if (hits.isEmpty()) "clean" else "DATA_ADB_MAPPED x${hits.size}: ${hits.joinToString(" | ")}"
            },
        )))

        add(probe("integrity:anon_minor", ctx.getString(R.string.t_anon_minor), Category.INTEGRITY, listOf(
            jm("no hidden anon-bdev superblock (expected)", compare = false) { "0" },
            jm("anon-minor holes (short runs only)", compare = false) {
                NativeBridge.anonHoles()?.holeCount?.toString() ?: Sentinels.NONE
            },
            jm("detail", compare = false) {
                NativeBridge.anonHoles()?.let { "holes=${it.holes}\nchurn=runs${it.churnRuns}/minors${it.churnMinors}\nwindow=${it.window}" } ?: Sentinels.NONE
            },
        )))

        add(probe("integrity:frida_port", ctx.getString(R.string.t_frida_port), Category.INTEGRITY, listOf(
            jm("frida ports closed (expected)") { "closed" },
            jm("connect 127.0.0.1:27042/27043") {
                val ports = NativeBridge.fridaPorts() ?: return@jm Sentinels.NONE
                ports.filter { it.second }.takeIf { it.isNotEmpty() }
                    ?.joinToString(",") { it.first.toString() }?.plus(" open") ?: "closed"
            },
        )))

        add(probe("integrity:procnet_frida", ctx.getString(R.string.t_procnet_frida), Category.INTEGRITY, listOf(
            nm("/proc/net/tcp+tcp6 LISTEN frida ports (native read)") {
                NativeBridge.procNetScan()?.fridaPorts
            },
            sm("if ! head -c1 /proc/net/tcp >/dev/null 2>&1; then echo EACCES; else cat /proc/net/tcp /proc/net/tcp6 2>/dev/null | awk '\$4==\"0A\"{if(\$2 ~ /:69A2\$/)print 27042; else if(\$2 ~ /:69A3\$/)print 27043}' | sort -un | paste -sd, - | grep . || echo none; fi",
                "frida ports via cat /proc/net/tcp*"),
        )))

        add(probe("integrity:procnet_tun", ctx.getString(R.string.t_procnet_tun), Category.INTEGRITY, listOf(
            nm("vpn/tun iface in /proc/net/{if_inet6,route,dev} (native read)") {
                NativeBridge.procNetScan()?.tunIfaces
            },
            sm("if ! head -c1 /proc/net/dev >/dev/null 2>&1; then echo EACCES; else { cat /proc/net/if_inet6 2>/dev/null; awk 'NR>1{print \$1}' /proc/net/route 2>/dev/null; sed 's/:.*//' /proc/net/dev 2>/dev/null; } | grep -oE '(tun|ppp|tap|pptp)[0-9]*' | sort -u | paste -sd, - | grep . || echo none; fi",
                "tun iface via /proc/net (cat)"),
            sm("if ! ls /sys/class/net >/dev/null 2>&1; then echo EACCES; else ls /sys/class/net | grep -oE '^(tun|ppp|tap|pptp)[0-9]*' | sort -u | paste -sd, - | grep . || echo none; fi",
                "tun iface via /sys/class/net (independent)"),
        )))

        add(pkgItem("root_apps", ctx.getString(R.string.t_root_manager_apps), AssetData.packages(ctx, "root_manager_apps"), ctx.getString(R.string.sol_root_detector)))
        add(pkgItem("xposed_apps", ctx.getString(R.string.t_xposed_manager_apps), AssetData.packages(ctx, "xposed_manager_apps"), ctx.getString(R.string.sol_root_detector)))
        add(jprobe("integrity:manager_doors", ctx.getString(R.string.t_manager_doors), Category.INTEGRITY,
            source = "PackageManager: list / getPackageInfo / getApplicationInfo / launchIntent / components") { c -> managerDoors(c) })
        add(pkgItem("hide_apps", ctx.getString(R.string.t_hiding_cloaking_apps), AssetData.packages(ctx, "hiding_apps"), ctx.getString(R.string.sol_root_detector)))
        add(pkgItem("root_tools", ctx.getString(R.string.t_root_tools_apps), AssetData.packages(ctx, "root_tools_apps"), ctx.getString(R.string.sol_root_detector)))
        add(pkgItem("rooted_apps", ctx.getString(R.string.t_rooted_apps), AssetData.packages(ctx, "rooted_apps"), ctx.getString(R.string.sol_root_detector)))
        add(pkgItem("detector_apps", ctx.getString(R.string.t_root_detector_apps), AssetData.packages(ctx, "root_detector_apps"), ctx.getString(R.string.sol_root_detector)))
        add(pkgItem("suspicious_apps", ctx.getString(R.string.t_suspicious_apps), AssetData.packages(ctx, "suspicious_apps"), ctx.getString(R.string.sol_root_detector)))
        add(pkgItem("analysis_apps", ctx.getString(R.string.t_analysis_emulator_apps), AssetData.packages(ctx, "analysis_emulator_apps"), ctx.getString(R.string.sol_root_detector)))

        add(pathItem("emu_files", ctx.getString(R.string.t_emulator_files), AssetData.packages(ctx, "emulator_file_paths")))

        add(probe("integrity:verifiedboot", ctx.getString(R.string.t_verified_boot_state), Category.INTEGRITY, propTrio("ro.boot.verifiedbootstate")))

        add(probe("integrity:prop_area_holes", ctx.getString(R.string.t_prop_area_tamper), Category.INTEGRITY, listOf(
            nm("scan_prop_area_holes /dev/__properties__") { NativeBridge.propAreaHoles() },
        )))

        add(probe("integrity:readproc_gid", ctx.getString(R.string.t_supplementary_groups), Category.INTEGRITY, listOf(
            jm("read /proc/self/status Groups") { groupsCsv(runCatching { File("/proc/self/status").readText() }.getOrNull()) },
            nm("native read /proc/self/status Groups") { groupsCsv(NativeBridge.readFileOrReason("/proc/self/status")) },
            sm("grep '^Groups:' /proc/self/status | cut -f2- | tr ' \t' '\n' | grep . | sort -n | paste -sd, -",
                "sh: /proc/self/status Groups"),
        )))
    }
}
