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
