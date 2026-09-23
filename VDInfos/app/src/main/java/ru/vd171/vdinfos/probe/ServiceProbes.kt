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
import android.app.AppOpsManager
import android.app.KeyguardManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.hardware.input.InputManager
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Debug
import android.os.Process
import android.provider.Settings
import android.view.InputDevice
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.net.NetworkInterface

@SuppressLint("MissingPermission")
object ServiceProbes {

    private const val VPN_IFACES = "tun0 tun1 ppp0 pptp0 tap0"

    private val OPS = listOf(
        AppOpsManager.OPSTR_MOCK_LOCATION,
        AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW,
        AppOpsManager.OPSTR_WRITE_SETTINGS,
        "android:request_install_packages",
        AppOpsManager.OPSTR_GET_USAGE_STATS,
        AppOpsManager.OPSTR_READ_PHONE_STATE,
        AppOpsManager.OPSTR_CAMERA,
        AppOpsManager.OPSTR_RECORD_AUDIO,
        AppOpsManager.OPSTR_FINE_LOCATION,
    )

    private fun opMode(m: Int) = when (m) {
        AppOpsManager.MODE_ALLOWED -> "allowed"
        AppOpsManager.MODE_IGNORED -> "ignored"
        AppOpsManager.MODE_ERRORED -> "errored"
        AppOpsManager.MODE_DEFAULT -> "default"
        else -> "mode=$m"
    }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        val vpnPrefixes = AssetData.packages(ctx, "vpn_iface_prefixes")

        add(probe("net:vpn", ctx.getString(R.string.t_vpn_active), Category.NETWORK, listOf(
            jm("NetworkCapabilities.TRANSPORT_VPN") { c ->
                val cm = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val any = cm.allNetworks.any { n ->
                    cm.getNetworkCapabilities(n)?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true
                }
                any.toString()
            },
            jm("NetworkInterface: tun/ppp/tap present") {
                val ifaces = NetworkInterface.getNetworkInterfaces()?.toList().orEmpty()
                if (ifaces.isEmpty()) Sentinels.RESTRICTED
                else ifaces.any { i ->
                    vpnPrefixes.any { p -> i.name.startsWith(p) }
                }.toString()
            },
            sm("ls /sys/class/net >/dev/null 2>&1 || { echo ${Sentinels.RESTRICTED}; exit 0; }; " +
                "ls -d " + vpnPrefixes.joinToString(" ") { "/sys/class/net/${it}0" } + " 2>/dev/null " +
                "| grep -q . && echo true || echo false", "ls /sys/class/net"),
            nsm("ls /sys/class/net >/dev/null 2>&1 || { echo ${Sentinels.RESTRICTED}; exit 0; }; " +
                "ls -d " + vpnPrefixes.joinToString(" ") { "/sys/class/net/${it}0" } + " 2>/dev/null " +
                "| grep -q . && echo true || echo false", "popen: ls /sys/class/net"),
        )))
        add(probe("net:vpn_ifaces", ctx.getString(R.string.t_vpn_interfaces), Category.NETWORK, listOf(
            jm("NetworkInterface names") {
                val ifaces = NetworkInterface.getNetworkInterfaces()?.toList().orEmpty()
                if (ifaces.isEmpty()) Sentinels.RESTRICTED
                else ifaces.map { it.name }.filter {
                    vpnPrefixes.any { p -> it.startsWith(p) }
                }.joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
            sm("ls /sys/class/net >/dev/null 2>&1 || { echo ${Sentinels.RESTRICTED}; exit 0; }; " +
                "ls /sys/class/net | grep -E '^(${vpnPrefixes.joinToString("|")})' || echo ${Sentinels.ABSENT}", "ls /sys/class/net"),
        )))

        add(probe("net:proxy", ctx.getString(R.string.t_http_proxy), Category.NETWORK, listOf(
            jm("System.getProperty(http.proxyHost)") {
                val h = System.getProperty("http.proxyHost")
                val p = System.getProperty("http.proxyPort")
                if (h.isNullOrEmpty()) Sentinels.ABSENT else "$h:${p.orEmpty()}"
            },

            jm("ConnectivityManager.defaultProxy") { c ->
                val cm = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                cm.defaultProxy?.let { "${it.host}:${it.port}" } ?: Sentinels.ABSENT
            },
        ) + settingBlock("http_proxy", compare = false)))

        add(probe("loc:providers", ctx.getString(R.string.t_location_providers), Category.SYSTEM, listOf(
            jm("LocationManager.getAllProviders()") { c ->
                val lm = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                lm.allProviders.sorted().joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
        )))
        add(probe("loc:test_providers", ctx.getString(R.string.t_mock_location), Category.INTEGRITY, listOf(
            jm("providers beyond the platform set") { c ->
                val lm = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val known = setOf("gps", "network", "passive", "fused")
                lm.allProviders.filter { it !in known }.joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
            jm("AppOps: this app may mock location", compare = false) { c ->
                val ops = c.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                opMode(
                    if (Build.VERSION.SDK_INT >= 29)
                        ops.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_MOCK_LOCATION, Process.myUid(), c.packageName)
                    else @Suppress("DEPRECATION")
                    ops.checkOpNoThrow(AppOpsManager.OPSTR_MOCK_LOCATION, Process.myUid(), c.packageName)
                )
            },
        ) + settingBlock("mock_location", compare = false)))

        add(probe("integrity:debugger", ctx.getString(R.string.t_debugger_attached), Category.INTEGRITY, listOf(
            jm("Debug.isDebuggerConnected()") { Debug.isDebuggerConnected().toString() },
            jm("Debug.waitingForDebugger()") { Debug.waitingForDebugger().toString() },
            sm("grep TracerPid /proc/self/status | awk '{print ($2 != 0)}'", "TracerPid != 0", compare = false),
        )))

        add(probe("sec:device_secure", ctx.getString(R.string.t_device_secure), Category.SECURITY, listOf(
            jm("KeyguardManager.isDeviceSecure()") { c ->
                (c.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isDeviceSecure.toString()
            },
            jm("KeyguardManager.isKeyguardSecure()") { c ->
                (c.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).isKeyguardSecure.toString()
            },
        )))

        add(probe("hw:sensor_vendors", ctx.getString(R.string.t_sensor_vendors), Category.HARDWARE, listOf(
            jm("SensorManager.getSensorList(TYPE_ALL) vendors") { c ->
                val sm2 = c.getSystemService(Context.SENSOR_SERVICE) as SensorManager
                sm2.getSensorList(Sensor.TYPE_ALL).map { it.vendor }.distinct().sorted()
                    .joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
        )))
        add(probe("hw:input_devices", ctx.getString(R.string.t_input_devices), Category.HARDWARE, listOf(
            jm("InputDevice.getDeviceIds() names") { c ->
                val im = c.getSystemService(Context.INPUT_SERVICE) as InputManager
                im.inputDeviceIds.toList()
                    .mapNotNull { id -> InputDevice.getDevice(id)?.name }
                    .sorted().joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
        )))

        for (op in OPS) {
            val nome = op.removePrefix("android:")
            add(probe("appop:$nome", ctx.getString(R.string.t_appop, nome), Category.PACKAGES, listOf(
                jm("AppOpsManager.checkOpNoThrow($nome)") { c ->
                    val ops = c.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
                    opMode(
                        if (Build.VERSION.SDK_INT >= 29)
                            ops.unsafeCheckOpNoThrow(op, Process.myUid(), c.packageName)
                        else @Suppress("DEPRECATION")
                        ops.checkOpNoThrow(op, Process.myUid(), c.packageName)
                    )
                },
            )))
        }
    }
}
