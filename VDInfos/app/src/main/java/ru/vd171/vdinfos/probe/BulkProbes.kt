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
import android.content.pm.PackageManager
import android.os.Build
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
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
                sig?.let { "${pi.packageName} ${sha256(it.toByteArray()).take(16)}" }
            }.sorted().joinToString("\n")
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
