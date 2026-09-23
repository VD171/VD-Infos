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
import android.os.Build
import android.telephony.TelephonyManager
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask

@SuppressLint("HardwareIds", "MissingPermission")
object TelephonyProbes {

    private fun tm(c: Context) = c.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

    private fun slot0(v: String?) = v?.substringBefore(',')?.takeIf { it.isNotEmpty() }

    private fun t(
        ctx: Context, id: String, titleRes: Int, sensitive: Boolean = false,
        prop: String? = null, compare: Boolean = true,
        get: (TelephonyManager) -> String?,
    ) = probe("tel:$id", ctx.getString(titleRes), Category.TELEPHONY, buildList {
        add(jm("TelephonyManager.$id") { c -> tm(c)?.let(get) })
        if (prop != null) {
            add(nm("read_callback $prop (slot 0)", compare) { slot0(NativeBridge.sysprop(prop)) })
            add(nm("property_get $prop (92B, slot 0)", compare) { slot0(NativeBridge.syspropClassic(prop)) })
            add(sm("getprop $prop | cut -d, -f1", "getprop $prop (slot 0)", compare))
        }
    }, sensitive = sensitive)

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        add(t(ctx, "networkOperatorName", R.string.t_tel_networkOperatorName, prop = "gsm.operator.alpha", compare = false) { it.networkOperatorName })
        add(t(ctx, "networkOperator", R.string.t_tel_networkOperator, prop = "gsm.operator.numeric", compare = false) { it.networkOperator })
        add(t(ctx, "networkCountryIso", R.string.t_tel_networkCountryIso, prop = "gsm.operator.iso-country", compare = false) { it.networkCountryIso })
        add(t(ctx, "simOperatorName", R.string.t_tel_simOperatorName, prop = "gsm.sim.operator.alpha", compare = false) { it.simOperatorName })
        add(t(ctx, "simOperator", R.string.t_tel_simOperator, prop = "gsm.sim.operator.numeric", compare = false) { it.simOperator })
        add(t(ctx, "simCountryIso", R.string.t_tel_simCountryIso, prop = "gsm.sim.operator.iso-country", compare = false) { it.simCountryIso })
        add(t(ctx, "simState", R.string.t_tel_simState, prop = "gsm.sim.state", compare = false) { it.simState.toString() })
        add(t(ctx, "phoneType", R.string.t_tel_phoneType) { it.phoneType.toString() })
        add(t(ctx, "networkType", R.string.t_tel_networkType, prop = "gsm.network.type", compare = false) { if (Build.VERSION.SDK_INT >= 30) it.dataNetworkType.toString() else null })
        add(t(ctx, "dataState", R.string.t_tel_dataState) { it.dataState.toString() })
        add(t(ctx, "callState", R.string.t_tel_callState) { it.callState.toString() })
        add(t(ctx, "isNetworkRoaming", R.string.t_tel_isNetworkRoaming, prop = "gsm.operator.isroaming") { it.isNetworkRoaming.toString() })
        add(t(ctx, "line1Number", R.string.t_tel_line1Number, sensitive = true) { @Suppress("DEPRECATION") it.line1Number })
        add(t(ctx, "voiceMailNumber", R.string.t_tel_voiceMailNumber, sensitive = true) { it.voiceMailNumber })
        add(t(ctx, "groupIdLevel1", R.string.t_tel_groupIdLevel1) { it.groupIdLevel1 })
        add(t(ctx, "mmsUserAgent", R.string.t_tel_mmsUserAgent) { it.mmsUserAgent })
        add(t(ctx, "mmsUAProfUrl", R.string.t_tel_mmsUAProfUrl) { it.mmsUAProfUrl })
        add(t(ctx, "phoneCount", R.string.t_tel_phoneCount, prop = "ro.telephony.sim.count") {
            if (Build.VERSION.SDK_INT >= 30) it.activeModemCount.toString() else @Suppress("DEPRECATION") it.phoneCount.toString()
        })
        add(t(ctx, "isDataEnabled", R.string.t_tel_isDataEnabled) { if (Build.VERSION.SDK_INT >= 26) it.isDataEnabled.toString() else null })
        add(t(ctx, "isVoiceCapable", R.string.t_tel_isVoiceCapable) { it.isVoiceCapable.toString() })
        add(t(ctx, "isSmsCapable", R.string.t_tel_isSmsCapable) { it.isSmsCapable.toString() })
        add(t(ctx, "subscriberId", R.string.t_tel_subscriberId, sensitive = true) { @Suppress("DEPRECATION") it.subscriberId })
        add(t(ctx, "deviceSoftwareVersion", R.string.t_tel_deviceSoftwareVersion, prop = "gsm.version.baseband", compare = false) { it.deviceSoftwareVersion })
    }
}
