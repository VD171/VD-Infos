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
import android.provider.Settings
import android.telephony.TelephonyManager
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask

@SuppressLint("HardwareIds")
object IdentifierProbes {

    private fun tm(ctx: Context) = ctx.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

    private fun adsKeys(c: Context): List<String> = AssetData.packages(c, "ads_keys")

    private fun sweepAdsApi(c: Context): String {
        val found = sortedSetOf<String>()
        for (k in adsKeys(c)) {
            runCatching { Settings.Secure.getString(c.contentResolver, k) }
                .getOrNull()?.takeIf { it.isNotEmpty() }?.let { found += "secure/$k=$it" }
            @Suppress("DEPRECATION")
            runCatching { Settings.Global.getString(c.contentResolver, k) }
                .getOrNull()?.takeIf { it.isNotEmpty() }?.let { found += "global/$k=$it" }
            runCatching { Settings.System.getString(c.contentResolver, k) }
                .getOrNull()?.takeIf { it.isNotEmpty() }?.let { found += "system/$k=$it" }
        }
        return found.joinToString("\n").ifEmpty { Sentinels.NONE }
    }

    private fun sweepAdsProvider(c: Context): String {
        val found = sortedSetOf<String>()
        for (store in listOf("secure", "global", "system")) {
            for (k in adsKeys(c)) {
                runCatching {
                    c.contentResolver.query(
                        android.net.Uri.parse("content://settings/$store"),
                        arrayOf("name", "value"), "name=?", arrayOf(k), null,
                    )?.use { cur ->
                        if (cur.moveToFirst()) {
                            val v = cur.getString(cur.getColumnIndexOrThrow("value"))
                            if (!v.isNullOrEmpty()) found += "$store/$k=$v"
                        }
                    }
                }
            }
        }
        return found.joinToString("\n").ifEmpty { Sentinels.NONE }
    }

    private fun sweepAdsAppended(c: Context): String {
        val found = sortedSetOf<String>()
        for (store in listOf("secure", "global", "system")) for (k in adsKeys(c)) runCatching {
            c.contentResolver.query(
                android.net.Uri.parse("content://settings/$store/$k"),
                arrayOf("name", "value"), null, null, null,
            )?.use { cur ->
                if (cur.moveToFirst()) {
                    val v = cur.getString(cur.getColumnIndexOrThrow("value"))
                    if (!v.isNullOrEmpty()) found += "$store/$k=$v"
                }
            }
        }
        return found.joinToString("\n").ifEmpty { Sentinels.NONE }
    }

    private fun sweepAdsBulk(c: Context): String {
        val keySet = adsKeys(c).toHashSet()
        val found = sortedSetOf<String>()
        for (store in listOf("secure", "global", "system")) runCatching {
            c.contentResolver.query(
                android.net.Uri.parse("content://settings/$store"),
                arrayOf("name", "value"), null, null, null,
            )?.use { cur ->
                val ni = cur.getColumnIndex("name"); val vi = cur.getColumnIndex("value")
                while (cur.moveToNext()) {
                    val n = if (ni >= 0) cur.getString(ni) else null
                    if (n != null && n in keySet) {
                        val v = if (vi >= 0) cur.getString(vi) else null
                        if (!v.isNullOrEmpty()) found += "$store/$n=$v"
                    }
                }
            }
        }
        return found.joinToString("\n").ifEmpty { Sentinels.NONE }
    }

    private fun sweepAdsCall(c: Context): String {
        val found = sortedSetOf<String>()
        for (store in listOf("global", "secure", "system")) for (k in adsKeys(c)) runCatching {
            val v = c.contentResolver.call(
                android.net.Uri.parse("content://settings"), "GET_$store", k, null,
            )?.getString("value")
            if (!v.isNullOrEmpty()) found += "$store/$k=$v"
        }
        return found.joinToString("\n").ifEmpty { Sentinels.NONE }
    }

    private const val GSF_AUTHORITY = "com.google.android.gsf.gservices"
    private const val GMS_AUTHORITY = "com.google.android.gms.gservices.provider.do.not.use"

    private fun gservices(c: Context, authority: String, key: String): String? {
        val uri = android.net.Uri.parse("content://$authority")
        val shapes: List<Pair<String?, Array<String>?>> = listOf(
            null to arrayOf(key),
            "name=?" to arrayOf(key),
        )
        for ((selection, args) in shapes) {
            val v = runCatching {
                c.contentResolver.query(uri, null, selection, args, null)?.use { cur ->
                    if (!cur.moveToFirst()) return@use null
                    val byName = cur.getColumnIndex("value").takeIf { it >= 0 }
                    val idx = byName ?: (1.takeIf { cur.columnCount >= 2 } ?: return@use null)
                    cur.getString(idx)
                }
            }.getOrNull()?.takeIf { !it.isNullOrEmpty() }
            if (v != null) return v
        }
        return null
    }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        add(probe("id:android_id", ctx.getString(R.string.t_android_id), Category.IDENTITY,
            settingBlock("android_id") + listOf(
                smr("content query --uri content://settings/secure/android_id --projection value " +
                    "| sed 's/^Row: 0 value=//'", "content query settings/secure/android_id",
                    compare = false, timeoutMs = 8000),
            ), sensitive = true))

        add(probe("id:gsf", ctx.getString(R.string.t_gsf_id_hex), Category.IDENTITY, listOf(
            jm("gsf authority android_id") { c ->
                gservices(c, GSF_AUTHORITY, "android_id")?.toLongOrNull()
                    ?.let { java.lang.Long.toHexString(it).uppercase() }
            },
            jm("gms authority android_id") { c ->
                gservices(c, GMS_AUTHORITY, "android_id")?.toLongOrNull()
                    ?.let { java.lang.Long.toHexString(it).uppercase() }
            },
        ), sensitive = true, note = ctx.getString(ru.vd171.vdinfos.R.string.note_gsf)))
        add(probe("id:gsf_raw", ctx.getString(R.string.t_gsf_id_raw), Category.IDENTITY, listOf(
            jm("gsf authority android_id") { c -> gservices(c, GSF_AUTHORITY, "android_id") },
            jm("gms authority android_id") { c -> gservices(c, GMS_AUTHORITY, "android_id") },
        ), sensitive = true, note = ctx.getString(ru.vd171.vdinfos.R.string.note_gsf)))

        add(probe("id:ads", ctx.getString(R.string.t_advertising_id), Category.IDENTITY, listOf(
            jm("GMS IAdvertisingIdService.getId (bind)") { c -> AdvertisingId.id(c) },
        ) + settingBlock("advertising_id"), sensitive = true))
        add(probe("id:ads_keys", ctx.getString(R.string.t_advertising_id_keys_settings_stores), Category.IDENTITY, listOf(
            jm("Settings.{Secure,Global,System}.getString sweep") { c -> sweepAdsApi(c) },
            jm("ContentResolver query selection name=? sweep") { c -> sweepAdsProvider(c) },
            jm("ContentResolver query appended-path sweep") { c -> sweepAdsAppended(c) },
            jm("ContentResolver query bulk-all sweep") { c -> sweepAdsBulk(c) },
            jm("provider call GET_<store> sweep") { c -> sweepAdsCall(c) },
        ), sensitive = true))
        add(probe("id:limit_ad", ctx.getString(R.string.t_limit_ad_tracking), Category.IDENTITY, listOf(
            jm("GMS IAdvertisingIdService.isLimitAdTrackingEnabled") { c -> AdvertisingId.limitAdTracking(c) },
        ) + settingBlock("limit_ad_tracking")))

        add(probe("id:imei", ctx.getString(R.string.t_imei), Category.IDENTITY, listOf(
            @Suppress("DEPRECATION") jm("TelephonyManager.getDeviceId()") { c -> tm(c)?.deviceId },
            jm("TelephonyManager.getImei()") { c -> if (android.os.Build.VERSION.SDK_INT >= 26) tm(c)?.imei else null },
            jm("TelephonyManager.getImei(0)") { c -> if (android.os.Build.VERSION.SDK_INT >= 26) tm(c)?.getImei(0) else null },
        ) + propTrio("persist.radio.imei") + propTrio("ril.imei") + listOf(
        ), sensitive = true))
        add(probe("id:meid", ctx.getString(R.string.t_meid), Category.IDENTITY, listOf(
            jm("TelephonyManager.getMeid()") { c -> if (android.os.Build.VERSION.SDK_INT >= 26) tm(c)?.meid else null },
            @Suppress("DEPRECATION") jm("TelephonyManager.getDeviceId()") { c -> tm(c)?.deviceId },
        ), sensitive = true))

        add(probe("id:imsi", ctx.getString(R.string.t_imsi_subscriber), Category.IDENTITY, listOf(
            @Suppress("DEPRECATION") jm("TelephonyManager.getSubscriberId()") { c -> tm(c)?.subscriberId },
        ), sensitive = true))
        add(probe("id:iccid", ctx.getString(R.string.t_sim_serial_iccid), Category.IDENTITY, listOf(
            @Suppress("DEPRECATION") jm("TelephonyManager.getSimSerialNumber()") { c -> tm(c)?.simSerialNumber },
        ) + propTrio("persist.vendor.radio.cfu.iccid.0") + listOf(
        ), sensitive = true))
    }
}
