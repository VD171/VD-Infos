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

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.SigningInfo
import android.os.Build
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.security.MessageDigest

@SuppressLint("QueryPermissionsNeeded")
object InstallSourceProbes {

    private const val PLAY = "com.android.vending"
    private const val SHELL = "com.android.shell"

    private fun sha256(bytes: ByteArray) =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    private fun signingCertSet(si: SigningInfo?): Set<String> {
        if (si == null) return emptySet()
        val out = LinkedHashSet<String>()
        (if (si.hasMultipleSigners()) null else si.signingCertificateHistory)
            ?.forEach { out.add(sha256(it.toByteArray())) }
        si.apkContentsSigners?.forEach { out.add(sha256(it.toByteArray())) }
        return out
    }

    private fun certSetOf(pi: PackageInfo?): Set<String> {
        if (pi == null) return emptySet()
        return if (Build.VERSION.SDK_INT >= 28) signingCertSet(pi.signingInfo)
        else @Suppress("DEPRECATION") pi.signatures?.map { sha256(it.toByteArray()) }?.toSet() ?: emptySet()
    }

    private fun dumpsysField(self: String, field: String) =
        "l=\$(/system/bin/dumpsys package $self 2>/dev/null | grep -m1 '$field='); " +
        "case \"\$l\" in *$field=*) printf '%s' \"\${l#*$field=}\" | sed 's/ .*//';; " +
        "*) echo ${Sentinels.RESTRICTED};; esac"

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        val self = ctx.packageName

        add(probe("isrc:self_installer", ctx.getString(R.string.t_isrc_self_installer), Category.PACKAGES, listOf(
            jm("getInstallSourceInfo().installingPackageName") { c ->
                if (Build.VERSION.SDK_INT < 30) null
                else c.packageManager.getInstallSourceInfo(self).installingPackageName ?: "null"
            },
            jm("getInstallerPackageName (legacy)") { c ->
                @Suppress("DEPRECATION") c.packageManager.getInstallerPackageName(self) ?: "null"
            },
            smr(dumpsysField(self, "installerPackageName"), "dumpsys package | installerPackageName"),
            smr("pm list packages -i $self 2>&1 | sed -n 's/.*installer=//p' | head -1",
                "pm list packages -i"),
        )))

        add(probe("isrc:self_initiating", ctx.getString(R.string.t_isrc_self_initiating), Category.PACKAGES, listOf(
            jm("getInstallSourceInfo().initiatingPackageName") { c ->
                if (Build.VERSION.SDK_INT < 30) null
                else c.packageManager.getInstallSourceInfo(self).initiatingPackageName ?: "null"
            },
            smr(dumpsysField(self, "initiatingPackageName"), "dumpsys package | initiatingPackageName"),
        )))

        add(probe("isrc:self_originating", ctx.getString(R.string.t_isrc_self_originating), Category.PACKAGES, listOf(
            jm("getInstallSourceInfo().originatingPackageName") { c ->
                if (Build.VERSION.SDK_INT < 30) null
                else c.packageManager.getInstallSourceInfo(self).originatingPackageName ?: "null"
            },
            smr(dumpsysField(self, "originatingPackageName"), "dumpsys package | originatingPackageName"),
        )))

        add(probe("isrc:self_pkgsource", ctx.getString(R.string.t_isrc_self_pkgsource), Category.PACKAGES, listOf(
            jm("getInstallSourceInfo().packageSource") { c ->
                if (Build.VERSION.SDK_INT < 33) null
                else c.packageManager.getInstallSourceInfo(self).packageSource.toString()
            },
            smr(dumpsysField(self, "packageSource"), "dumpsys package | packageSource"),
        )))

        add(probe("isrc:self_initiator_sig", ctx.getString(R.string.t_isrc_self_initiator_sig), Category.PACKAGES, listOf(
            jm("getInitiatingPackageSigningInfo() SHA-256") { c ->
                if (Build.VERSION.SDK_INT < 30) return@jm null
                signingCertSet(c.packageManager.getInstallSourceInfo(self).initiatingPackageSigningInfo)
                    .minOrNull()?.take(16) ?: Sentinels.NONE
            },
            jm("initiating package lineage, as installed") { c ->
                if (Build.VERSION.SDK_INT < 30) return@jm null
                val si = c.packageManager.getInstallSourceInfo(self)
                val rec = signingCertSet(si.initiatingPackageSigningInfo)
                val init = si.initiatingPackageName ?: return@jm Sentinels.NONE
                val flag = if (Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES
                else @Suppress("DEPRECATION") PackageManager.GET_SIGNATURES
                val inst = runCatching { certSetOf(c.packageManager.getPackageInfo(init, flag)) }
                    .getOrDefault(emptySet())
                if (rec.isEmpty() || inst.isEmpty()) return@jm Sentinels.NONE
                (if (rec.any { it in inst }) rec.minOrNull() else inst.minOrNull())?.take(16) ?: Sentinels.NONE
            },
        )))

        addAll(gapProbes(ctx))
    }

    private class Row(
        val pkg: String, val sys: Boolean, val inst: String?, val init: String?,
        val orig: String?, val initSig: String?, val updateOwner: String?,
        val installedInitSig: String?,
        val sigShared: Boolean,
        val pmInst: String?,
    )

    private fun gapProbes(ctx: Context): List<ProbeTask> {
        val cache = lazy { scan(ctx) }

        fun gap(id: String, title: String, detail: ((Row) -> String)? = null, pick: (Row) -> Boolean) =
            jprobe("isrc:gap_$id", title, Category.PACKAGES, source = "install-source cross-check over all visible apps") {
                val hits = cache.value.filter(pick).sortedBy { it.pkg }
                if (hits.isEmpty()) Sentinels.NONE
                else "${hits.size} app(s)\n" +
                    hits.take(60).joinToString("\n") { r -> r.pkg + (detail?.let { " (${it(r)})" } ?: "") } +
                    (if (hits.size > 60) "\n... +${hits.size - 60}" else "")
            }

        val stores = AssetData.packages(ctx, "install_source_stores").toSet()
        val sideloaders = AssetData.packages(ctx, "install_source_sideloaders").toSet()

        return listOf(
            gap("jvm_store_pm_denies", ctx.getString(R.string.t_isrc_gap_jvm_store_pm_denies),
                detail = { r -> "jvm=${r.inst ?: "null"} pm=${r.pmInst ?: "null"}" }) { r ->
                r.pmInst != null && isStore(r.inst, stores) && !isStore(r.pmInst, stores)
            },
            gap("jvm_pm_installer_other", ctx.getString(R.string.t_isrc_gap_jvm_pm_other),
                detail = { r -> "jvm=${r.inst ?: "null"} pm=${r.pmInst ?: "null"}" }) { r ->
                r.pmInst != null && normPm(r.inst) != normPm(r.pmInst) &&
                    !(isStore(r.inst, stores) && !isStore(r.pmInst, stores))
            },
            gap("installer_ne_initiating", ctx.getString(R.string.t_isrc_gap_installer_ne_initiating)) { r ->
                r.inst != null && r.init != null && r.inst != r.init
            },
            gap("store_installer_sideload_initiator", ctx.getString(R.string.t_isrc_gap_store_installer_sideload)) { r ->
                r.inst in stores && r.init in sideloaders
            },
            gap("installer_set_no_initiator", ctx.getString(R.string.t_isrc_gap_installer_set_no_initiator)) { r ->
                r.inst != null && r.init == null
            },
            gap("initiator_store_no_installer", ctx.getString(R.string.t_isrc_gap_initiator_store_no_installer)) { r ->
                r.init in stores && r.inst == null
            },
            gap("initiator_sig_mismatch", ctx.getString(R.string.t_isrc_gap_initiator_sig_mismatch)) { r ->
                r.init != null && r.init !in sideloaders &&
                    r.initSig != null && r.installedInitSig != null && !r.sigShared
            },
            gap("initiator_orphan", ctx.getString(R.string.t_isrc_gap_initiator_orphan)) { r ->
                r.init != null && r.init !in sideloaders && !isInstalled(ctx, r.init)
            },
            gap("installer_orphan", ctx.getString(R.string.t_isrc_gap_installer_orphan)) { r ->
                r.inst != null && r.inst !in sideloaders && !isInstalled(ctx, r.inst)
            },
            gap("originating_ne_initiating", ctx.getString(R.string.t_isrc_gap_originating_ne_initiating)) { r ->
                r.orig != null && r.orig != r.init
            },
            gap("ghost_user_install", ctx.getString(R.string.t_isrc_gap_ghost_user_install)) { r ->
                !r.sys && r.inst == null && r.init == null
            },
            gap("system_shell_installed", ctx.getString(R.string.t_isrc_gap_system_shell_installed)) { r ->
                r.sys && r.init == SHELL
            },
            gap("update_owner_drift", ctx.getString(R.string.t_isrc_gap_update_owner_drift)) { r ->
                r.updateOwner != null && r.inst != null && r.updateOwner != r.inst
            },
            gap("self_declared_installer", ctx.getString(R.string.t_isrc_gap_self_declared_installer)) { r ->
                r.inst == r.pkg && !isStore(r.pkg, stores)
            },
        )
    }

    private fun isInstalled(ctx: Context, pkg: String): Boolean = runCatching {
        ctx.packageManager.getPackageInfo(pkg, 0); true
    }.getOrDefault(false)

    private fun normPm(v: String?): String =
        v?.trim()?.lowercase().let { if (it.isNullOrEmpty() || it == "null") "" else it }

    private fun isStore(v: String?, stores: Set<String>): Boolean = normPm(v) in stores

    private fun pmInstallers(): Map<String, String> {
        val out = Exec.run("pm list packages -i 2>/dev/null", timeoutMs = 8000, cap = 400000)
            ?: return emptyMap()
        val map = HashMap<String, String>()
        for (line in out.lineSequence()) {
            val pk = Regex("package:(\\S+)").find(line)?.groupValues?.get(1) ?: continue
            val inst = Regex("installer=(\\S+)").find(line)?.groupValues?.get(1) ?: "null"
            map[pk] = inst
        }
        return map
    }

    private fun scan(ctx: Context): List<Row> {
        val pm = ctx.packageManager
        val pmMap = pmInstallers()
        val sigFlag = if (Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES
        else @Suppress("DEPRECATION") PackageManager.GET_SIGNATURES
        val certByPkg = HashMap<String, Set<String>>()
        pm.getInstalledPackages(sigFlag).forEach { pi ->
            certSetOf(pi).takeIf { it.isNotEmpty() }?.let { certByPkg[pi.packageName] = it }
        }
        return pm.getInstalledPackages(0).mapNotNull { pi ->
            val ai: ApplicationInfo = pi.applicationInfo ?: return@mapNotNull null
            val sys = (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                (ai.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
            var inst: String? = null; var init: String? = null; var orig: String? = null
            var recSet: Set<String> = emptySet(); var upd: String? = null
            if (Build.VERSION.SDK_INT >= 30) runCatching {
                val si = pm.getInstallSourceInfo(pi.packageName)
                inst = si.installingPackageName
                init = si.initiatingPackageName
                orig = si.originatingPackageName
                recSet = signingCertSet(si.initiatingPackageSigningInfo)
                if (Build.VERSION.SDK_INT >= 34) upd = si.updateOwnerPackageName
            } else runCatching {
                @Suppress("DEPRECATION")
                val v = pm.getInstallerPackageName(pi.packageName)
                inst = v
            }
            val instSet = init?.let { certByPkg[it] } ?: emptySet()
            val shared = recSet.isNotEmpty() && instSet.isNotEmpty() && recSet.any { it in instSet }
            Row(pi.packageName, sys, inst, init, orig, recSet.minOrNull(), upd,
                instSet.minOrNull(), shared, pmMap[pi.packageName])
        }
    }
}
