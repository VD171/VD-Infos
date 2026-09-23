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
import android.content.Intent
import android.net.Uri
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask

@SuppressLint("QueryPermissionsNeeded")
object QueryProbes {

    private val mainLauncher = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
    private val call = Intent(Intent.ACTION_CALL, Uri.parse("tel:0"))
    private val view = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com"))

    private fun q(id: String, title: String, run: (Context) -> List<Any>) =
        jprobe("query:$id", title, Category.PACKAGES, source = "PackageManager.$id") { c ->
            val l = run(c)
            "count=${l.size}\n" + l.take(10).joinToString("\n") { it.toString().take(120) }
        }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        add(q("activities_launcher", ctx.getString(R.string.t_queryintentactivities_main_launcher)) {
            it.packageManager.queryIntentActivities(mainLauncher, 0)
        })
        add(q("activities_view", ctx.getString(R.string.t_queryintentactivities_view_http)) {
            it.packageManager.queryIntentActivities(view, 0)
        })
        add(q("services", ctx.getString(R.string.t_queryintentservices_main)) {
            it.packageManager.queryIntentServices(Intent(Intent.ACTION_MAIN), 0)
        })
        add(q("receivers", ctx.getString(R.string.t_querybroadcastreceivers_boot_completed)) {
            it.packageManager.queryBroadcastReceivers(Intent(Intent.ACTION_BOOT_COMPLETED), 0)
        })
        add(q("providers", ctx.getString(R.string.t_queryintentcontentproviders_view)) {
            it.packageManager.queryIntentContentProviders(view, 0)
        })
        add(q("call_activities", ctx.getString(R.string.t_queryintentactivities_call_tel)) {
            it.packageManager.queryIntentActivities(call, 0)
        })
        add(jprobe("query:resolve_launcher", ctx.getString(R.string.t_resolveactivity_main_launcher), Category.PACKAGES,
            source = "PackageManager.resolveActivity") { c ->
            @Suppress("DEPRECATION") c.packageManager.resolveActivity(mainLauncher, 0)?.toString()
        })
        add(jprobe("query:instrumentation", ctx.getString(R.string.t_queryinstrumentation_all), Category.PACKAGES,
            source = "PackageManager.queryInstrumentation") { c ->
            c.packageManager.queryInstrumentation("", 0).joinToString("\n") { it.toString().take(120) }
                .ifEmpty { Sentinels.NONE }
        })

        add(probe("file:ptmx", ctx.getString(R.string.t_dev_ptmx_present), Category.PROCESS, listOf(
            jm("File.exists(/dev/ptmx)") { if (java.io.File("/dev/ptmx").exists()) "present" else Sentinels.ABSENT },
            nm("access(/dev/ptmx)") { if (NativeBridge.exists("/dev/ptmx")) "present" else Sentinels.ABSENT },
            sm("[ -e /dev/ptmx ] && echo ${Sentinels.PRESENT} || echo ${Sentinels.ABSENT}", "test -e /dev/ptmx"),
        )))
        add(probe("file:buildprop", ctx.getString(R.string.t_system_build_prop_present), Category.SYSTEM, listOf(
            nm("access(/system/build.prop)") { if (NativeBridge.exists("/system/build.prop")) "present" else Sentinels.ABSENT },
            sm("[ -e /system/build.prop ] && echo ${Sentinels.PRESENT} || echo ${Sentinels.ABSENT}", "test -e /system/build.prop"),
        )))
    }
}
