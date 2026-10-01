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

package ru.vd171.vdinfos.probe

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.io.File

object IntegrityExtraProbes {

    private class ConcealDoors(
        val pkg: String, val listed: Boolean, val pi: Boolean,
        val ai: Boolean, val ctx: Boolean, val apk: Boolean,
        val launch: Boolean, val resolve: Boolean,
    ) {
        val hiddenButReachable get() = !listed && (pi || ai || ctx || apk || launch || resolve)
        val matrix get() = "$pkg L${b(listed)}P${b(pi)}A${b(ai)}C${b(ctx)}Z${b(apk)}I${b(launch)}R${b(resolve)}" +
            if (hiddenButReachable) "  LEAK" else ""
        private fun b(v: Boolean) = if (v) 1 else 0
    }

    private fun concealScan(ctx: Context): List<ConcealDoors> {
        val pm = ctx.packageManager
        val listed = runCatching { pm.getInstalledPackages(0).map { it.packageName }.toHashSet() }
            .getOrDefault(hashSetOf())
        return AssetData.packages(ctx, "conceal_targets").mapNotNull { pkg ->
            val isListed = pkg in listed
            val pi = runCatching { pm.getPackageInfo(pkg, 0); true }.getOrDefault(false)
            val ai = runCatching { pm.getApplicationInfo(pkg, 0); true }.getOrDefault(false)
            var ctxDoor = false
            var src: String? = null
            runCatching {
                val pc = ctx.createPackageContext(pkg, 0)
                ctxDoor = true
                src = pc.applicationInfo?.sourceDir
            }
            val apk = src?.let { runCatching { java.util.zip.ZipFile(it).close(); true }.getOrDefault(false) } ?: false
            val launch = runCatching { pm.getLaunchIntentForPackage(pkg) != null }.getOrDefault(false)
            val resolve = runCatching {
                pm.queryIntentActivities(android.content.Intent(android.content.Intent.ACTION_MAIN).setPackage(pkg), 0).isNotEmpty()
            }.getOrDefault(false)
            val d = ConcealDoors(pkg, isListed, pi, ai, ctxDoor, apk, launch, resolve)
            if (isListed || pi || ai || ctxDoor || apk || launch || resolve) d else null
        }
    }


    private fun readGateApplies(ctx: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ctx.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.S

    private fun flagMods(text: String?, needles: List<String>): String? {
        if (text == null) return null
        val hits = text.lineSequence().mapNotNull { line ->
            val name = line.substringBefore(' ').trim()
            if (name.isNotEmpty() && needles.any { name.contains(it, ignoreCase = true) }) name else null
        }.toList()
        return if (hits.isEmpty()) Sentinels.ABSENT else hits.joinToString(", ")
    }

    private fun pathTampered(p: String?): Boolean {
        if (p.isNullOrEmpty()) return false
        return p.contains("(deleted)") || p.startsWith("/memfd:") || p.contains("memfd:") ||
            p.startsWith("/data/adb") || p.startsWith("/data/local/tmp")
    }

    private fun uidVerdict(uid: Int, owner: Int?): String {
        val system = uid in 0 until 10000
        val clone = uid / 100000 >= 1
        val mismatch = owner != null && owner >= 0 && owner != uid
        val bad = system || mismatch
        return "uid=$uid owner=${owner ?: "?"} clone=${if (clone) "yes" else "no"} " +
            "system=${if (system) "yes" else "no"} => ${if (bad) "MISMATCH" else "OK"}"
    }

    private fun hasInvisibleName(name: String, ranges: IntArray): Boolean {
        var i = 0
        while (i < name.length) {
            val cp = name.codePointAt(i)
            var j = 0
            while (j + 1 < ranges.size) {
                if (cp in ranges[j]..ranges[j + 1]) return true
                j += 2
            }
            i += Character.charCount(cp)
        }
        return false
    }

    private fun jvmInvisible(path: String, ranges: IntArray): String {
        val d = File(path)
        if (!d.exists()) return "ENOENT"
        val names = d.list() ?: return Sentinels.EACCES
        return if (names.any { hasInvisibleName(it, ranges) }) "1" else "0"
    }

    private fun aggInvisible(dirs: List<String>, scan: (String) -> String?): String {
        var sawClean = false
        for (dir in dirs) {
            when (scan(dir)) {
                "1" -> return "1"
                "0" -> sawClean = true
                else -> {}
            }
        }
        return if (sawClean) "0" else Sentinels.EACCES
    }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        val suDirs = AssetData.packages(ctx, "su_path_dirs")
        val invisRanges = AssetData.invisibleRanges(ctx)
        val zwDirs = listOf("/storage/emulated/0/Android/data", "/storage/emulated/0/Android/obb")

        val kmodNeedles = AssetData.packages(ctx, "root_kernel_module_needles")
        val backupPkgs = AssetData.packages(ctx, "root_backup_tools_apps")
        val localTmpArtifacts = AssetData.packages(ctx, "root_local_tmp_artifacts")
            .map { "/data/local/tmp/$it" }

        add(probe("integrity:kmods", ctx.getString(R.string.t_kernel_modules), Category.INTEGRITY, listOf(
            jm("read /proc/modules") { flagMods(runCatching { File("/proc/modules").readText() }.getOrNull(), kmodNeedles) },
            nm("native read /proc/modules") { flagMods(NativeBridge.readFile("/proc/modules"), kmodNeedles) },
            sm("lsmod 2>/dev/null | grep -iE '${kmodNeedles.joinToString("|")}' | awk '{print \$1}' | paste -sd, - || echo ${Sentinels.ABSENT}",
                "lsmod | grep root modules"),
        )))

        add(probe("integrity:kernel_syscall_age", ctx.getString(R.string.t_kernel_syscall_fingerprint), Category.INTEGRITY, listOf(
            nm("syscall availability vs uname") { NativeBridge.kernelSpoof() },
        )))

        add(probe("integrity:su_libc_hook", ctx.getString(R.string.t_su_libc_inline_hook), Category.INTEGRITY, listOf(
            nm("raw faccessat() over ${suDirs.size} su paths") {
                suDirs.map { "$it/su" }.firstOrNull { NativeBridge.rawExists(it) } ?: Sentinels.ABSENT
            },
            nm("libc access() over the same paths") {
                suDirs.map { "$it/su" }.firstOrNull { NativeBridge.exists(it) } ?: Sentinels.ABSENT
            },
            jm("Os.stat()/File over the same paths") {
                suDirs.map { "$it/su" }.firstOrNull { runCatching { android.system.Os.stat(it) }.isSuccess } ?: Sentinels.ABSENT
            },
        )))

        add(probe("integrity:self_lib_origin", ctx.getString(R.string.t_native_lib_origin), Category.INTEGRITY, listOf(
            nm("dladdr(self) origin path") {
                NativeBridge.selfPath()?.let { if (pathTampered(it)) "TAMPERED: $it" else it }
            },
        )))

        add(probe("integrity:uid_coherence", ctx.getString(R.string.t_uid_coherence), Category.INTEGRITY, listOf(
            jm("Process.myUid vs stat(dataDir) owner") { c ->
                uidVerdict(Process.myUid(), NativeBridge.statOwner(c.dataDir.path)?.first)
            },
            nm("getuid() vs stat(dataDir) owner") {
                val owner = NativeBridge.statOwner(ctx.dataDir.path)?.first
                NativeBridge.ids()?.uid?.let { uidVerdict(it, owner) }
            },
        )))

        add(probe("integrity:settings_read_gate", ctx.getString(R.string.t_settings_read_gate), Category.INTEGRITY, listOf(
            jm("expected: a restricted setting key stays gated for a normal app") { c ->
                if (!readGateApplies(c)) return@jm null
                "gated"
            },
            jm("Settings.Secure.getString(bluetooth_name)") { c ->
                if (!readGateApplies(c)) return@jm null
                try {
                    val v = Settings.Secure.getString(c.contentResolver, "bluetooth_name")
                    if (v.isNullOrEmpty()) "gated" else "LEAKED_VALUE"
                } catch (e: SecurityException) {
                    "gated"
                } catch (e: Throwable) {
                    "gated"
                }
            },
        )))

        add(probe("integrity:backup_tools", ctx.getString(R.string.t_root_backup_tools), Category.INTEGRITY, listOf(
            nm("access() /data/data & /data/user/0 for ${backupPkgs.size} tools") {
                backupPkgs.filter { NativeBridge.exists("/data/data/$it") || NativeBridge.exists("/data/user/0/$it") }
                    .joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
            jm("getPackageInfo of the same tools") { c ->
                backupPkgs.filter { runCatching { c.packageManager.getPackageInfo(it, 0) }.isSuccess }
                    .joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
        )))

        add(probe("integrity:local_tmp", ctx.getString(R.string.t_local_tmp_contents), Category.INTEGRITY, listOf(
            nm("access() ${localTmpArtifacts.size} known artifacts") {
                localTmpArtifacts.filter { NativeBridge.exists(it) }.joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
            sm("ls -a /data/local/tmp 2>/dev/null | grep -vE '^\\.\\.?$' | paste -sd, - || echo ${Sentinels.ABSENT}",
                "ls /data/local/tmp"),
        )))

        add(probe("integrity:documents_providers", ctx.getString(R.string.t_documents_providers), Category.PACKAGES, listOf(
            jm("queryIntentContentProviders DOCUMENTS_PROVIDER") { c ->
                val intent = android.content.Intent("android.content.action.DOCUMENTS_PROVIDER")
                runCatching {
                    c.packageManager.queryIntentContentProviders(intent, 0)
                        .mapNotNull { it.providerInfo?.let { p -> "${p.packageName}/${p.authority}" } }
                        .distinct().sorted().joinToString("\n")
                }.getOrNull()?.ifEmpty { Sentinels.NONE }
            },
            sm("pm query-content-providers 2>/dev/null | grep -i document | head || echo ${Sentinels.ABSENT}",
                "pm query-content-providers | grep document", compare = false),
        )))

        add(probe("integrity:zero_width_names", ctx.getString(R.string.t_zero_width_names), Category.INTEGRITY, listOf(
            jm("File.list Android/data+obb") { aggInvisible(zwDirs) { p -> jvmInvisible(p, invisRanges) } },
            nm("getdents64 Android/data+obb") { aggInvisible(zwDirs) { p -> NativeBridge.dirZeroWidth(p, invisRanges) } },
        )))

        add(probe("net:resolv_conf", ctx.getString(R.string.t_resolv_conf), Category.NETWORK, listOf(
            jm("read /etc/resolv.conf") {
                runCatching { File("/etc/resolv.conf").readText().trim() }.getOrNull()?.ifEmpty { Sentinels.ABSENT }
            },
            nm("native read /etc/resolv.conf") { NativeBridge.readFileOrReason("/etc/resolv.conf", 4096)?.trim() },
            nm("native read /system/etc/resolv.conf") { NativeBridge.readFileOrReason("/system/etc/resolv.conf", 4096)?.trim() },
            sm("getprop | grep -iE 'net\\.dns[0-9]' | paste -sd' ' - || echo ${Sentinels.ABSENT}", "getprop net.dns*", compare = false),
        )))

        add(probe("pkg:store_version", ctx.getString(R.string.t_play_store_version), Category.PACKAGES, listOf(
            jm("versionName com.android.vending / gms") { c ->
                AssetData.packages(c, "store_version_packages").joinToString("\n") { p ->
                    val v = runCatching {
                        val pi = c.packageManager.getPackageInfo(p, 0)
                        val code = if (Build.VERSION.SDK_INT >= 28) pi.longVersionCode.toString()
                        else @Suppress("DEPRECATION") pi.versionCode.toString()
                        "${pi.versionName} ($code)"
                    }.getOrNull() ?: Sentinels.ABSENT
                    "$p=$v"
                }
            },
            sm("for p in com.android.vending com.google.android.gms; do echo \$p=\$(dumpsys package \$p 2>/dev/null | grep -m1 versionName | cut -d= -f2); done",
                "dumpsys package versionName", compare = false),
        )))

        add(probe("self:debuggable", ctx.getString(R.string.t_own_debuggable), Category.INTEGRITY, listOf(
            jm("ApplicationInfo.FLAG_DEBUGGABLE") { c ->
                val dbg = (c.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
                val vn = runCatching { c.packageManager.getPackageInfo(c.packageName, 0).versionName }.getOrNull().orEmpty()
                val tainted = vn.contains("debug", true) || vn.contains("BUILD", true)
                "debuggable=$dbg versionName=$vn${if (dbg || tainted) " => TAINTED" else " => OK"}"
            },
            nm("ro.debuggable (global)", compare = false) { NativeBridge.sysprop("ro.debuggable") },
        )))

        add(probe("integrity:ime_verdict", ctx.getString(R.string.t_ime_verdict), Category.SECURITY, listOf(
            jm("enabled IMEs are system or from a store (expected)") { "SAFE" },
            jm("non-system enabled IME with non-store installer") { c ->
                val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    ?: return@jm Sentinels.NONE
                val stores = AssetData.packages(c, "install_source_stores").toHashSet()
                val bad = imm.enabledInputMethodList.mapNotNull { imi ->
                    val pkg = imi.packageName
                    val sys = runCatching {
                        (imi.serviceInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    }.getOrDefault(true)
                    if (sys) return@mapNotNull null
                    val inst = runCatching { c.packageManager.getInstallSourceInfo(pkg).installingPackageName }.getOrNull()
                    if (inst != null && inst in stores) null else "$pkg(installer=${inst ?: "null"})"
                }
                if (bad.isEmpty()) "SAFE" else "UNSAFE: ${bad.joinToString("; ")}"
            },
        ), note = ctx.getString(R.string.note_ime_verdict)))

        add(probe("integrity:store_authenticity", ctx.getString(R.string.t_store_authenticity), Category.PACKAGES, listOf(
            jm("Google stores are Google-signed (expected)") { "ok" },
            jm("store/GMS package present but not Google-signed") { c ->
                val pm = c.packageManager
                val flag = if (Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES
                else @Suppress("DEPRECATION") PackageManager.GET_SIGNATURES
                val bad = listOf("com.android.vending", "com.google.android.gms", "com.google.android.gsf", "com.android.vending.billing").mapNotNull { pkg ->
                    val sig = runCatching {
                        val pi = pm.getPackageInfo(pkg, flag)
                        val s = if (Build.VERSION.SDK_INT >= 28) pi.signingInfo?.apkContentsSigners?.firstOrNull()
                        else @Suppress("DEPRECATION") pi.signatures?.firstOrNull()
                        s?.let { java.security.MessageDigest.getInstance("SHA-256").digest(it.toByteArray()).joinToString("") { b -> "%02x".format(b) } }
                    }.getOrNull() ?: return@mapNotNull null
                    if (AssetData.certLabel(c, sig)?.contains("Google", true) == true) null
                    else "$pkg(${AssetData.certTag(c, sig)})"
                }
                if (bad.isEmpty()) "ok" else "FAKE: ${bad.joinToString("; ")}"
            },
        ), note = ctx.getString(R.string.note_store_authenticity)))

        add(probe("integrity:devgate", ctx.getString(R.string.t_devgate), Category.INTEGRITY, listOf(
            jm("no dev-gated setting on while dev-options off (expected)") { "ok" },
            jm("dev-gated setting non-default while development_settings_enabled=0") { c ->
                @Suppress("DEPRECATION")
                val devOn = (Settings.Global.getString(c.contentResolver, "development_settings_enabled") ?: "0") != "0"
                if (devOn) return@jm "dev-options ON"
                val bad = AssetData.devGated(c).mapNotNull { (k, def) ->
                    @Suppress("DEPRECATION")
                    val v = Settings.Global.getString(c.contentResolver, k)
                        ?: Settings.Secure.getString(c.contentResolver, k)
                    if (!v.isNullOrEmpty() && v != def) "$k=$v" else null
                }
                if (bad.isEmpty()) "ok" else "INCONSISTENT: ${bad.joinToString(",")}"
            },
        ), note = ctx.getString(R.string.note_devgate)))

        add(probe("integrity:concealment", ctx.getString(R.string.t_concealment), Category.INTEGRITY, listOf(
            jm("no target hidden-but-reachable (expected)") { "0" },
            jm("hidden-but-reachable count") { c -> concealScan(c).count { it.hiddenButReachable }.toString() },
            jm("door matrix (list/getPackageInfo/getApplicationInfo/createPackageContext/openApk/launchIntent/queryIntentActivities)", compare = false) { c ->
                concealScan(c).takeIf { it.isNotEmpty() }?.joinToString("\n") { it.matrix } ?: Sentinels.NONE
            },
        ), note = ctx.getString(R.string.note_concealment)))

        val keychain = "/data/misc/keychain/pubkey_blacklist.txt"
        fun meta(mtime: Long, size: Long) = "mtime=$mtime size=$size"
        add(probe("integrity:keychain_blacklist", ctx.getString(R.string.t_keychain_blacklist), Category.INTEGRITY, listOf(
            jm("File.lastModified/length $keychain") {
                val f = File(keychain)
                if (!f.exists()) Sentinels.ABSENT else meta(f.lastModified() / 1000, f.length())
            },
            nm("stat $keychain") { NativeBridge.statMeta(keychain)?.let { meta(it.first, it.second) } ?: Sentinels.ABSENT },
            sm("stat -c 'mtime=%Y size=%s' $keychain 2>/dev/null || echo ${Sentinels.ABSENT}", "stat $keychain"),
        )))

        add(probe("integrity:buildprop_vs_runtime", ctx.getString(R.string.t_buildprop_vs_runtime), Category.INTEGRITY, listOf(
            jm("build.prop files vs runtime getprop") { c ->
                val files = AssetData.partitions(c).flatMap { it.propPaths }
                val keys = listOf(
                    "ro.product.model", "ro.product.device", "ro.build.fingerprint",
                    "ro.build.version.security_patch",
                )
                val fileVals = HashMap<String, MutableSet<String>>()
                for (path in files) {
                    val txt = NativeBridge.readFile(path) ?: continue
                    for (line in txt.lineSequence()) {
                        val t = line.trim()
                        if (t.startsWith("#")) continue
                        val i = t.indexOf('=')
                        if (i <= 0) continue
                        val k = t.substring(0, i).trim()
                        if (k in keys) fileVals.getOrPut(k) { HashSet() }.add(t.substring(i + 1).trim())
                    }
                }
                if (fileVals.isEmpty()) return@jm Sentinels.ABSENT
                keys.mapNotNull { k ->
                    val fv = fileVals[k] ?: return@mapNotNull null
                    val rt = SystemProps.get(k)
                    val ok = rt != null && fv.contains(rt)
                    "$k: file=${fv.joinToString("|")} runtime=${rt ?: "-"} ${if (ok) "OK" else "DIVERGE"}"
                }.joinToString("\n").ifEmpty { Sentinels.ABSENT }
            },
        )))

        add(probe("integrity:flagsecure_honored", ctx.getString(R.string.t_flagsecure_honored), Category.INTEGRITY, listOf(
            jm("expected: our own FLAG_SECURE request is honored") {
                val r = FlagSecureState.result ?: return@jm null
                if (!r.startsWith("honored") && !r.startsWith("STRIPPED")) return@jm null
                "honored"
            },
            jm("FLAG_SECURE set on own window, read back") {
                val r = FlagSecureState.result ?: return@jm null
                if (!r.startsWith("honored") && !r.startsWith("STRIPPED")) return@jm null
                r
            },
        )))

        add(probe("net:nettype_consistency", ctx.getString(R.string.t_nettype_consistency), Category.NETWORK, listOf(
            jm("getType/hasTransport (hookable)") { c -> claimedNetType(c) },
            jm("active iface -> type (LinkProperties, independent)") { c -> ifaceNetType(c) },
            nm("native /sys/class/net wlan operstate", compare = false) { sysfsWlan() },
            sm("cat /proc/net/wireless 2>/dev/null | awk 'NR>2{print \$1}' | tr -d ':' | paste -sd, - || echo ${Sentinels.ABSENT}",
                "proc/net/wireless ifaces", compare = false),
        )))
    }

    private fun cmgr(c: Context) =
        c.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager

    private fun claimedNetType(c: Context): String? {
        val cm = cmgr(c)
        val n = cm.activeNetwork ?: return Sentinels.ABSENT
        val caps = cm.getNetworkCapabilities(n) ?: return Sentinels.ABSENT
        return when {
            caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
            caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> "mobile"
            caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
            else -> "other"
        }
    }

    private fun ifaceNetType(c: Context): String? {
        val cm = cmgr(c)
        val n = cm.activeNetwork ?: return Sentinels.ABSENT
        val ifn = cm.getLinkProperties(n)?.interfaceName ?: return Sentinels.ABSENT
        return when {
            ifn.startsWith("wlan") -> "wifi"
            ifn.startsWith("rmnet") || ifn.startsWith("ccmni") || ifn.startsWith("ccinet") || ifn.startsWith("pdp") -> "mobile"
            ifn.startsWith("eth") -> "ethernet"
            else -> "other($ifn)"
        }
    }

    private fun sysfsWlan(): String? {
        val names = NativeBridge.dirList("/sys/class/net")?.lineSequence()
            ?.map { it.trim() }?.filter { it.startsWith("wlan") }?.toList() ?: return null
        if (names.isEmpty()) return "no-wlan"
        return names.joinToString(",") { w ->
            "$w=${NativeBridge.readFile("/sys/class/net/$w/operstate")?.trim() ?: "?"}"
        }
    }
}
