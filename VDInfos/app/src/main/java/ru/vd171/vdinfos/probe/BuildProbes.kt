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

import android.os.Build
import android.content.Context
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask

object BuildProbes {
    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        fun b(id: String, cat: Category, prop: String? = null, compare: Boolean = true,
              solution: String? = null, v: () -> String?) =
            add(probe("build.field:$id", id, cat, buildList {
                add(jm("Build.$id") { v() })
                if (prop != null) {
                    add(nm("read_callback $prop", compare) { NativeBridge.sysprop(prop) })
                    add(nm("property_get $prop (92B)", compare) { NativeBridge.syspropClassic(prop) })
                    add(sm("getprop $prop", "getprop $prop", compare))
                }
            }, solution = solution))

        b("BOARD", Category.BUILD, "ro.product.board") { Build.BOARD }
        b("BOOTLOADER", Category.BOOT, "ro.bootloader") { Build.BOOTLOADER }
        @Suppress("DEPRECATION") b("CPU_ABI", Category.HARDWARE, "ro.product.cpu.abi") { Build.CPU_ABI }
        @Suppress("DEPRECATION") b("CPU_ABI2", Category.HARDWARE, "ro.product.cpu.abi2") { Build.CPU_ABI2 }
        b("SUPPORTED_ABIS", Category.HARDWARE, "ro.product.cpu.abilist") { Build.SUPPORTED_ABIS.joinToString(",") }
        b("SUPPORTED_32_BIT_ABIS", Category.HARDWARE, "ro.product.cpu.abilist32") { Build.SUPPORTED_32_BIT_ABIS.joinToString(",") }
        b("SUPPORTED_64_BIT_ABIS", Category.HARDWARE, "ro.product.cpu.abilist64") { Build.SUPPORTED_64_BIT_ABIS.joinToString(",") }
        b("DISPLAY", Category.BUILD, "ro.build.display.id") { Build.DISPLAY }
        b("HOST", Category.BUILD, "ro.build.host") { Build.HOST }
        b("USER", Category.BUILD, "ro.build.user") { Build.USER }
        b("TIME", Category.BUILD, "ro.build.date.utc", compare = false) { java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US).format(java.util.Date(Build.TIME)) }
        b("RADIO", Category.TELEPHONY, "gsm.version.baseband") { Build.getRadioVersion() }
        b("ODM_SKU", Category.BUILD, "ro.boot.product.hardware.sku") { if (Build.VERSION.SDK_INT >= 31) Build.ODM_SKU else null }
        b("SKU", Category.BUILD, "ro.boot.hardware.sku") { if (Build.VERSION.SDK_INT >= 31) Build.SKU else null }
        b("SOC_MANUFACTURER", Category.HARDWARE, "ro.soc.manufacturer") { if (Build.VERSION.SDK_INT >= 31) Build.SOC_MANUFACTURER else null }
        b("SOC_MODEL", Category.HARDWARE, "ro.soc.model") { if (Build.VERSION.SDK_INT >= 31) Build.SOC_MODEL else null }
        b("FINGERPRINT.raw", Category.FINGERPRINT, "ro.build.fingerprint") { Build.FINGERPRINT }

        b("VERSION.RELEASE", Category.BUILD, "ro.build.version.release") { Build.VERSION.RELEASE }
        b("VERSION.SDK_INT", Category.BUILD, "ro.build.version.sdk") { Build.VERSION.SDK_INT.toString() }
        b("VERSION.CODENAME", Category.BUILD, "ro.build.version.codename") { Build.VERSION.CODENAME }
        b("VERSION.INCREMENTAL", Category.BUILD, "ro.build.version.incremental") { Build.VERSION.INCREMENTAL }
        b("VERSION.BASE_OS", Category.BUILD, "ro.build.version.base_os") { Build.VERSION.BASE_OS }
        b("VERSION.PREVIEW_SDK_INT", Category.BUILD, "ro.build.version.preview_sdk") { Build.VERSION.PREVIEW_SDK_INT.toString() }
        b("VERSION.SECURITY_PATCH", Category.BUILD, "ro.build.version.security_patch",
          solution = ctx.getString(R.string.sol_patch_prop)) { Build.VERSION.SECURITY_PATCH }
        b("VERSION.MEDIA_PERFORMANCE_CLASS", Category.BUILD) { if (Build.VERSION.SDK_INT >= 31) Build.VERSION.MEDIA_PERFORMANCE_CLASS.toString() else null }
        b("SUPPORTED_ABIS.is64bit", Category.HARDWARE) { android.os.Process.is64Bit().toString() }
    }
}
