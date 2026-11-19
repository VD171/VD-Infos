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
import org.json.JSONObject
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask
import java.io.File

object PixelBuildProbes {

    private val FIELDS = listOf(
        "ANDROID_VERSION", "SDK_INT", "SDK_FULL", "CODENAME", "INCREMENTAL", "SECURITY_PATCH",
        "PREVIEW_SDK", "SDK_FINGERPRINT", "FIRST_API_LEVEL", "USER", "TAGS", "TYPE", "TIMESTAMP",
        "BOARD", "BOOTLOADER", "DISPLAY", "HARDWARE", "HOST", "ID", "BRAND", "MODEL", "PRODUCT",
        "DEVICE", "FINGERPRINT", "UUID", "MANUFACTURER")

    private val PROP = mapOf(
        "ANDROID_VERSION" to "ro.build.version.release", "SDK_INT" to "ro.build.version.sdk",
        "SDK_FULL" to "ro.build.version.sdk_full", "CODENAME" to "ro.build.version.codename",
        "INCREMENTAL" to "ro.build.version.incremental", "SECURITY_PATCH" to "ro.build.version.security_patch",
        "PREVIEW_SDK" to "ro.build.version.preview_sdk", "SDK_FINGERPRINT" to "ro.build.version.preview_sdk_fingerprint",
        "FIRST_API_LEVEL" to "ro.product.first_api_level", "USER" to "ro.build.user",
        "TAGS" to "ro.build.tags", "TYPE" to "ro.build.type", "TIMESTAMP" to "ro.build.date.utc",
        "BOARD" to "ro.product.board", "BOOTLOADER" to "ro.bootloader", "DISPLAY" to "ro.build.display.id",
        "HARDWARE" to "ro.hardware", "HOST" to "ro.build.host", "ID" to "ro.build.id",
        "BRAND" to "ro.product.brand", "MODEL" to "ro.product.model", "PRODUCT" to "ro.product.name",
        "DEVICE" to "ro.product.device", "FINGERPRINT" to "ro.build.fingerprint",
        "UUID" to "ro.build.uuid", "MANUFACTURER" to "ro.product.manufacturer")

    private val CODENAME = Regex("/([^/:]+):")

    private fun codename(fp: String?): String? =
        fp?.let { CODENAME.find(it)?.groupValues?.get(1)?.replace("_beta", "")?.replace("_canary", "") }

    private fun deviceValue(k: String): String? = when (k) {
        "SDK_INT" -> Build.VERSION.SDK_INT.toString()
        "ANDROID_VERSION" -> Build.VERSION.RELEASE
        "CODENAME" -> Build.VERSION.CODENAME
        "SECURITY_PATCH" -> Build.VERSION.SECURITY_PATCH
        "INCREMENTAL" -> Build.VERSION.INCREMENTAL
        "TIMESTAMP" -> (Build.TIME / 1000L).toString()
        else -> SystemProps.get(PROP[k] ?: "")
    }

    fun tasks(ctx: Context): List<ProbeTask> = listOf(
        jprobe("integrity:pixel_build", "Spoofed build vs real Google build (field-by-field)",
            Category.FINGERPRINT) { c ->
            val f = File(c.filesDir, "pixel_build_expected.json")
            if (!f.exists()) return@jprobe "n/a (no injected reference)"
            val exp = runCatching { JSONObject(f.readText()) }.getOrNull()
                ?: return@jprobe "n/a (bad reference json)"
            val devCode = codename(Build.FINGERPRINT)
            val expCode = codename(exp.optString("FINGERPRINT"))
            if (devCode == null || expCode == null || !devCode.equals(expCode, ignoreCase = true))
                return@jprobe "n/a out-of-scope (device presents $devCode, reference is $expCode)"
            val tells = StringBuilder()
            var n = 0
            var checked = 0
            for (k in FIELDS) {
                if (!exp.has(k)) continue
                val e = exp.optString(k)
                if (e.isEmpty()) continue
                checked++
                val dev = deviceValue(k)
                if (dev != e) {
                    n++
                    if (tells.isNotEmpty()) tells.append("; ")
                    tells.append("$k(dev=$dev real=$e)")
                }
            }
            if (n == 0) "MATCH ($checked fields vs real build)" else "MISMATCH:$n -> $tells"
        })
}
