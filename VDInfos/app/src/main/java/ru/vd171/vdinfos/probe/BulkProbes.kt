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
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.security.MessageDigest

@SuppressLint("QueryPermissionsNeeded")
object BulkProbes {

    private fun sha256(bytes: ByteArray) =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        add(jprobe("pkg:list", ctx.getString(R.string.t_installed_packages_list), Category.PACKAGES,
            source = "PackageManager.getInstalledPackages()") { c ->
            c.packageManager.getInstalledPackages(0)
                .map { it.packageName }.sorted().joinToString("\n")
        })

        add(jprobe("pkg:signatures", ctx.getString(R.string.t_installed_app_signing_digests), Category.PACKAGES,
            source = "getPackageInfo(GET_SIGNING_CERTIFICATES) + SHA-256 per app") { c ->
            val pm = c.packageManager
            val flag = if (Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES
            else @Suppress("DEPRECATION") PackageManager.GET_SIGNATURES
            pm.getInstalledPackages(flag).mapNotNull { pi ->
                val sig = if (Build.VERSION.SDK_INT >= 28)
                    pi.signingInfo?.apkContentsSigners?.firstOrNull()
                else @Suppress("DEPRECATION") pi.signatures?.firstOrNull()
                sig?.let { "${pi.packageName} ${AssetData.certTag(c, sha256(it.toByteArray()))}" }
            }.sorted().joinToString("\n")
        })

        add(jprobe("pkg:xposed_scan", ctx.getString(R.string.t_xposed_scan_all), Category.PACKAGES,
            source = "getInstalledPackages(GET_META_DATA): scan all apps for Xposed meta-data keys") { c ->
            val metaKeys = AssetData.packages(c, "xposed_manifest_meta_keys")
            c.packageManager.getInstalledPackages(PackageManager.GET_META_DATA).mapNotNull { pi ->
                val md = pi.applicationInfo?.metaData ?: return@mapNotNull null
                val found = metaKeys.filter { md.containsKey(it) }
                if (found.isEmpty()) return@mapNotNull null
                val tag = if ((pi.applicationInfo?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM != 0) "sys" else "usr"
                "${pi.packageName}[$tag]: ${found.joinToString(",") { "$it=${md.get(it)}" }}"
            }.sorted().joinToString("\n").ifEmpty { Sentinels.NONE }
        })

        add(jprobe("pkg:debuggable_scan", ctx.getString(R.string.t_debuggable_scan_all), Category.PACKAGES,
            source = "getInstalledApplications(): FLAG_DEBUGGABLE / FLAG_TEST_ONLY on user apps") { c ->
            c.packageManager.getInstalledApplications(0)
                .filter { ai ->
                    (ai.flags and ApplicationInfo.FLAG_SYSTEM) == 0 &&
                        ((ai.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0 ||
                            (ai.flags and ApplicationInfo.FLAG_TEST_ONLY) != 0)
                }
                .map { ai ->
                    val tags = buildList {
                        if ((ai.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) add("debuggable")
                        if ((ai.flags and ApplicationInfo.FLAG_TEST_ONLY) != 0) add("test_only")
                    }
                    "${ai.packageName}: ${tags.joinToString(",")}"
                }
                .sorted().joinToString("\n").ifEmpty { Sentinels.NONE }
        })

        add(jprobe("pkg:uid_groups", ctx.getString(R.string.t_uid_groups), Category.PACKAGES,
            source = "getInstalledApplications(): group by uid, highlight system+user UID sharing") { c ->
            val apps = c.packageManager.getInstalledApplications(0)
            val groups = apps.groupBy { it.uid }.filter { (_, ais) -> ais.size > 1 }
            val mixed = groups.filter { (_, ais) ->
                ais.any { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 } &&
                    ais.any { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            }
            val out = ArrayList<String>()
            if (mixed.isNotEmpty()) {
                out.add("MIXED uid+system:")
                mixed.entries.sortedBy { it.key }.forEach { (uid, ais) ->
                    val sys = ais.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 }.map { it.packageName }.sorted()
                    val usr = ais.filter { (it.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }.map { it.packageName }.sorted()
                    out.add("uid=$uid sys=[${sys.joinToString(",")}] usr=[${usr.joinToString(",")}]")
                }
            }
            val pureUsr = groups.filter { (uid, _) -> uid !in mixed }.filter { (_, ais) ->
                ais.none { (it.flags and ApplicationInfo.FLAG_SYSTEM) != 0 }
            }
            if (pureUsr.isNotEmpty()) {
                out.add("shared user-only:")
                pureUsr.entries.sortedBy { it.key }.forEach { (uid, ais) ->
                    out.add("uid=$uid: ${ais.map { it.packageName }.sorted().joinToString(",")}")
                }
            }
            if (out.isEmpty()) Sentinels.NONE else out.joinToString("\n")
        })

        add(jprobe("pkg:updated_system", ctx.getString(R.string.t_updated_system), Category.PACKAGES,
            source = "getInstalledApplications(): FLAG_UPDATED_SYSTEM_APP - system apps overridden by user install") { c ->
            c.packageManager.getInstalledApplications(0)
                .filter { ai -> (ai.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0 }
                .map { it.packageName }
                .sorted().joinToString("\n").ifEmpty { Sentinels.NONE }
        })

        add(jprobe("proc:services", ctx.getString(R.string.t_running_services), Category.PROCESS,
            source = "ActivityManager.getRunningServices(1000)") { c ->
            @Suppress("DEPRECATION")
            (c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .getRunningServices(1000).joinToString("\n") { it.service.flattenToShortString() }
        })

        add(jprobe("proc:processes", ctx.getString(R.string.t_running_app_processes), Category.PROCESS,
            source = "ActivityManager.getRunningAppProcesses()") { c ->
            (c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .runningAppProcesses?.joinToString("\n") { "${it.pid} ${it.processName}" }
        })
    }
}
