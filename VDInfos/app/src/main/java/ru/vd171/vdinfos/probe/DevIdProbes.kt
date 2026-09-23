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
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.os.UserManager
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask

@SuppressLint("HardwareIds", "MissingPermission")
object DevIdProbes {

    private fun tm(c: Context) = c.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

    private fun msaReflect(method: String, c: Context): String? = runCatching {
        val cls = Class.forName("com.android.id.impl.IdProviderImpl")
        val impl = cls.getDeclaredConstructor().newInstance()
        (cls.getMethod(method, Context::class.java).invoke(impl, c) as? String)?.takeIf { it.isNotEmpty() }
    }.getOrNull()

    private fun miuiIdProvider(c: Context, path: String): String? = runCatching {
        c.contentResolver.query(Uri.parse("content://com.miui.idprovider/$path"), null, null, null, null)?.use {
            if (it.moveToFirst() && it.columnCount > 0) it.getString(it.columnCount - 1)?.takeIf { v -> v.isNotEmpty() } else null
        }
    }.getOrNull()

    private fun msaProbe(id: String, title: String, reflectMethod: String, providerPath: String) =
        probe("id:$id", title, Category.IDENTITY, listOf(
            jm("IdProviderImpl.$reflectMethod() [reflection]") { c -> msaReflect(reflectMethod, c) },
            jm("query content://com.miui.idprovider/$providerPath") { c -> miuiIdProvider(c, providerPath) },
        ), sensitive = true)

    private fun setting(id: String, title: String, key: String, cat: Category, sensitive: Boolean = false, solution: String? = null) =
        probe("set:$id", title, cat, settingBlock(key), sensitive = sensitive, solution = solution)

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        add(jprobe("sub:active", ctx.getString(R.string.t_active_subscriptions), Category.TELEPHONY, sensitive = true,
            source = "SubscriptionManager.getActiveSubscriptionInfoList()") { c ->
            val sm = c.getSystemService(SubscriptionManager::class.java) ?: return@jprobe null
            sm.activeSubscriptionInfoList?.joinToString("\n") { s ->
                "slot=${s.simSlotIndex} subId=${s.subscriptionId} iccid=${s.iccId} " +
                    "num=${s.number} carrier=${s.carrierName} mcc=${s.mccString} mnc=${s.mncString} " +
                    "country=${s.countryIso}"
            }
        })

        add(probe("id:primary_imei", ctx.getString(R.string.t_primary_imei), Category.IDENTITY, listOf(
            jm("TelephonyManager.getImei()") { c -> if (Build.VERSION.SDK_INT >= 26) tm(c)?.imei else null },
            jm("TelephonyManager.getPrimaryImei() [reflection]") { c ->
                tm(c)?.let { t -> TelephonyManager::class.java.getMethod("getPrimaryImei").invoke(t) as? String }
            },
        ), sensitive = true))

        add(jprobe("id:user_serial", ctx.getString(R.string.t_user_serial_number), Category.IDENTITY,
            source = "UserManager.getSerialNumberForUser(myUserHandle)") { c ->
            (c.getSystemService(Context.USER_SERVICE) as? UserManager)
                ?.getSerialNumberForUser(Process.myUserHandle())?.toString()
        })

        add(jprobe("tel:cellinfo", ctx.getString(R.string.t_cell_info), Category.TELEPHONY, sensitive = true,
            source = "TelephonyManager.getAllCellInfo()") { c ->
            tm(c)?.allCellInfo?.joinToString("\n") { it.toString().take(160) }
        })

        add(jprobe("hw:cameras", ctx.getString(R.string.t_cameras), Category.HARDWARE,
            source = "CameraManager.cameraIdList + LENS_FACING") { c ->
            val cm = c.getSystemService(Context.CAMERA_SERVICE) as? CameraManager ?: return@jprobe null
            cm.cameraIdList.joinToString("\n") { id ->
                val facing = cm.getCameraCharacteristics(id).get(CameraCharacteristics.LENS_FACING)
                "id=$id facing=$facing"
            }
        })

        add(probe("hw:features", ctx.getString(R.string.t_system_features_full_set), Category.HARDWARE, listOf(
            jm("PackageManager.getSystemAvailableFeatures") { c ->
                c.packageManager.systemAvailableFeatures
                    .mapNotNull { it.name }.distinct().sorted().joinToString("\n")
            },
            sm("pm list features 2>/dev/null | sed -n 's/^feature://p' | " +
                "grep -v '^reqGlEsVersion' | sed 's/=.*//' | sort -u",
                "pm list features"),
            sm("cat /system/etc/permissions/*.xml /vendor/etc/permissions/*.xml " +
                "/product/etc/permissions/*.xml /system_ext/etc/permissions/*.xml 2>/dev/null | " +
                "grep -o '<feature name=\"[^\"]*\"' | sed 's/.*name=\"//;s/\"//' | sort -u",
                "declarations in etc/permissions/*.xml", compare = false),
        )))
        add(probe("hw:features_count", ctx.getString(R.string.t_system_features_count), Category.HARDWARE, listOf(
            jm("getSystemAvailableFeatures().size") { c ->
                c.packageManager.systemAvailableFeatures.mapNotNull { it.name }.distinct().size.toString()
            },
            sm("pm list features 2>/dev/null | sed -n 's/^feature://p' | " +
                "grep -v '^reqGlEsVersion' | sed 's/=.*//' | sort -u | wc -l",
                "pm list features | wc -l"),
        )))
        for (f in AssetData.packages(ctx, "system_features")) {
            add(probe("feature:${f.substringAfterLast('.')}", f, Category.HARDWARE, listOf(
                jm("hasSystemFeature") { c -> c.packageManager.hasSystemFeature(f).toString() },
                jm("getSystemAvailableFeatures contains") { c ->
                    c.packageManager.systemAvailableFeatures.any { it.name == f }.toString()
                },
                sm("pm list features 2>/dev/null | sed 's/=.*//' | grep -qx 'feature:$f' " +
                    "&& echo true || echo false", "pm list features | grep"),
            )))
        }

        add(setting("wifi_mac", ctx.getString(R.string.t_wi_fi_mac_settings), "wifi_mac", Category.NETWORK, sensitive = true))
        add(setting("device_name", ctx.getString(R.string.t_device_name), "device_name", Category.SYSTEM))
        add(probe("set:boot_count", ctx.getString(R.string.t_boot_count), Category.BOOT,
            settingBlock("boot_count") + settingBlock("Phenotype_boot_count")))
        add(setting("adb_enabled", ctx.getString(R.string.t_adb_enabled), "adb_enabled", Category.INTEGRITY, solution = ctx.getString(R.string.sol_spoof)))
        add(setting("dev_settings", ctx.getString(R.string.t_developer_settings_enabled), "development_settings_enabled", Category.INTEGRITY, solution = ctx.getString(R.string.sol_spoof)))
        add(setting("data_roaming", ctx.getString(R.string.t_data_roaming), "data_roaming", Category.TELEPHONY))
        add(setting("http_proxy", ctx.getString(R.string.t_http_proxy), "http_proxy", Category.NETWORK))
        add(setting("time_zone", ctx.getString(R.string.t_time_zone_settings), "time_zone", Category.LOCALE))
        add(setting("hidden_api_policy", ctx.getString(R.string.t_hidden_api_policy), "hidden_api_policy", Category.INTEGRITY))
        add(setting("hidden_api_policy_p", ctx.getString(R.string.t_hidden_api_policy_p_apps), "hidden_api_policy_p_apps", Category.INTEGRITY))
        add(setting("hidden_api_policy_pre_p", ctx.getString(R.string.t_hidden_api_policy_pre_p_apps), "hidden_api_policy_pre_p_apps", Category.INTEGRITY))
        add(setting("block_untrusted_touches", ctx.getString(R.string.t_block_untrusted_touches), "block_untrusted_touches", Category.SECURITY))
        add(setting("accessibility_enabled", ctx.getString(R.string.t_accessibility_enabled), "accessibility_enabled", Category.SECURITY))
        add(setting("a11y_services", ctx.getString(R.string.t_enabled_accessibility_services), "enabled_accessibility_services", Category.SECURITY))
        add(setting("touch_exploration", ctx.getString(R.string.t_touch_exploration_enabled), "touch_exploration_enabled", Category.SECURITY))
        add(setting("touch_exploration_granted", ctx.getString(R.string.t_touch_exploration_granted_services), "touch_exploration_granted_accessibility_services", Category.SECURITY))
        add(setting("default_input_method", ctx.getString(R.string.t_default_input_method), "default_input_method", Category.SYSTEM))
        add(setting("bluetooth_name", ctx.getString(R.string.t_bluetooth_name_settings), "bluetooth_name", Category.NETWORK))
        add(setting("bt_name", ctx.getString(R.string.t_bluetooth_name_bt_name), "bt_name", Category.NETWORK))
        add(setting("btname", ctx.getString(R.string.t_bluetooth_name_btname), "btname", Category.NETWORK))
        add(setting("tether_dun_required", ctx.getString(R.string.t_tether_dun_required), "tether_dun_required", Category.NETWORK))

        add(setting("extm_uuid", ctx.getString(R.string.t_extm_uuid), "extm_uuid", Category.IDENTITY, sensitive = true))
        add(setting("op_security_uuid", ctx.getString(R.string.t_op_security_uuid), "op_security_uuid", Category.IDENTITY, sensitive = true))

        add(msaProbe("oaid", ctx.getString(R.string.t_oaid), "getOAID", "oaid"))
        add(msaProbe("vaid", ctx.getString(R.string.t_vaid), "getVAID", "vaid"))
        add(msaProbe("aaid_msa", ctx.getString(R.string.t_aaid_msa), "getAAID", "aaid"))
        add(msaProbe("udid", ctx.getString(R.string.t_udid), "getUDID", "uuid"))

        add(jprobe("id:user_creation_time", ctx.getString(R.string.t_user_creation_time), Category.IDENTITY,
            source = "UserManager.getUserCreationTime(myUserHandle)") { c ->
            (c.getSystemService(Context.USER_SERVICE) as? UserManager)
                ?.getUserCreationTime(Process.myUserHandle())?.toString()
        })
    }
}
