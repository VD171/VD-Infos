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
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.net.wifi.WifiManager
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask
import java.net.NetworkInterface

@SuppressLint("HardwareIds", "MissingPermission")
object NetworkProbes {

    private fun macItem(ctx: Context, iface: String) = probe("net:mac:$iface", ctx.getString(R.string.t_mac, iface), Category.NETWORK, listOf(
        jm("NetworkInterface.getHardwareAddress") {
            NetworkInterface.getByName(iface)?.hardwareAddress?.joinToString(":") { b -> "%02x".format(b) }
        },
        nm("read /sys/class/net/$iface/address") { NativeBridge.ifaceMac(iface) },
        sm("cat /sys/class/net/$iface/address", "cat /sys/class/net/$iface/address"),
        smr("ip link show $iface | awk '$1 ~ /^link/{print $2}'", "ip link show $iface", compare = false),
    ), sensitive = true)

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        for (iface in AssetData.packages(ctx, "network_mac_ifaces")) add(macItem(ctx, iface))

        add(jprobe("net:interfaces", ctx.getString(R.string.t_network_interfaces), Category.NETWORK,
            source = "NetworkInterface.getNetworkInterfaces()") {
            NetworkInterface.getNetworkInterfaces()?.toList()?.joinToString("\n") { ni ->
                val mac = ni.hardwareAddress?.joinToString(":") { b -> "%02x".format(b) } ?: "-"
                val addrs = ni.inetAddresses.toList().joinToString(",") { it.hostAddress ?: "" }
                "${ni.name} mac=$mac up=${runCatching { ni.isUp }.getOrNull()} [$addrs]"
            }
        })


        @Suppress("DEPRECATION")
        fun wifi(id: String, title: String, sensitive: Boolean = false, get: (WifiManager) -> String?) =
            jprobe("wifi:$id", title, Category.NETWORK, sensitive, "WifiInfo.$id") { c ->
                (c.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager)?.let(get)
            }
        add(wifi("ssid", ctx.getString(R.string.t_wi_fi_ssid), sensitive = true) { it.connectionInfo.ssid })
        add(wifi("bssid", ctx.getString(R.string.t_wi_fi_bssid), sensitive = true) { it.connectionInfo.bssid })
        add(wifi("mac", ctx.getString(R.string.t_wi_fi_mac_wifiinfo), sensitive = true) { it.connectionInfo.macAddress })
        add(wifi("ip", ctx.getString(R.string.t_wi_fi_ip)) {
            val ip = it.connectionInfo.ipAddress
            "%d.%d.%d.%d".format(ip and 0xff, ip shr 8 and 0xff, ip shr 16 and 0xff, ip shr 24 and 0xff)
        })
        add(wifi("linkSpeed", ctx.getString(R.string.t_wi_fi_link_speed_mbps)) { it.connectionInfo.linkSpeed.toString() })
        add(wifi("frequency", ctx.getString(R.string.t_wi_fi_frequency_mhz)) { it.connectionInfo.frequency.toString() })
        add(wifi("rssi", ctx.getString(R.string.t_wi_fi_rssi)) { it.connectionInfo.rssi.toString() })
        add(wifi("networkId", ctx.getString(R.string.t_wi_fi_network_id)) { it.connectionInfo.networkId.toString() })

        fun bt(id: String, title: String, sensitive: Boolean = false, get: (BluetoothAdapter) -> String?) =
            jprobe("bt:$id", title, Category.NETWORK, sensitive, "BluetoothAdapter.$id") { c ->
                val a = (c.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter
                    ?: @Suppress("DEPRECATION") BluetoothAdapter.getDefaultAdapter()
                a?.let(get)
            }
        add(bt("name", ctx.getString(R.string.t_bluetooth_name)) { it.name })
        add(bt("address", ctx.getString(R.string.t_bluetooth_mac), sensitive = true) { @Suppress("DEPRECATION", "HardwareIds") it.address })
        add(bt("state", ctx.getString(R.string.t_bluetooth_state)) { it.state.toString() })
        add(bt("scanMode", ctx.getString(R.string.t_bluetooth_scan_mode)) { it.scanMode.toString() })
        add(bt("bonded", ctx.getString(R.string.t_bluetooth_bonded_devices), sensitive = true) { a ->
            a.bondedDevices?.joinToString("\n") { d -> "${d.name} ${@Suppress("HardwareIds") d.address}" }
        })
    }
}
