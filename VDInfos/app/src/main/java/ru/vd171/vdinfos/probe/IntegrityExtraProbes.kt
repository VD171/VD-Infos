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
import android.os.Build
import android.os.Process
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.io.File

object IntegrityExtraProbes {

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
    }
}
