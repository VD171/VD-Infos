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

import android.content.Context
import android.os.Build
import android.os.Process
import android.os.SystemClock
import android.provider.Settings
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.io.File
import java.net.InetAddress
import java.util.TimeZone

object SemanticProbes {

    private fun buildItem(
        id: String, title: String, cat: Category, buildSource: String,
        build: (Context) -> String?, props: List<String>, sensitive: Boolean = false,
        attestField: (() -> String?)? = null, attestCompare: Boolean = true, solution: String? = null,
    ) = probe("dev:$id", title, cat, buildList {
        add(jm(buildSource, read = build))
        props.forEachIndexed { idx, k ->
            val cmp = idx == 0
            add(jm("SystemProperties $k", compare = cmp) { SystemProps.get(k) })
            add(nm("read_callback $k", compare = cmp) { NativeBridge.sysprop(k) })
            add(nm("property_get $k (92B)", compare = cmp) { NativeBridge.syspropClassic(k) })
            add(sm("getprop $k", "getprop $k", compare = cmp))
        }
        attestField?.let { f -> add(am("KeyStore attestation record", compare = attestCompare) { f() }) }
    }, sensitive, solution = solution)

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        val fieldProps = AssetData.buildFieldProps(ctx)

        add(buildItem("model", ctx.getString(R.string.t_model), Category.BUILD, "Build.MODEL", { Build.MODEL },
            (fieldProps["model"] ?: emptyList()),
            attestField = { Attestation.record?.model }, attestCompare = false))
        add(buildItem("manufacturer", ctx.getString(R.string.t_manufacturer), Category.BUILD, "Build.MANUFACTURER", { Build.MANUFACTURER },
            (fieldProps["manufacturer"] ?: emptyList()),
            attestField = { Attestation.record?.manufacturer }))
        add(buildItem("brand", ctx.getString(R.string.t_brand), Category.BUILD, "Build.BRAND", { Build.BRAND },
            (fieldProps["brand"] ?: emptyList()),
            attestField = { Attestation.record?.brand }))
        add(buildItem("device", ctx.getString(R.string.t_device), Category.BUILD, "Build.DEVICE", { Build.DEVICE },
            (fieldProps["device"] ?: emptyList()),
            attestField = { Attestation.record?.device }))
        add(buildItem("product", ctx.getString(R.string.t_product), Category.BUILD, "Build.PRODUCT", { Build.PRODUCT },
            (fieldProps["product"] ?: emptyList()),
            attestField = { Attestation.record?.product }, attestCompare = false))
        add(buildItem("board", ctx.getString(R.string.t_board), Category.BUILD, "Build.BOARD", { Build.BOARD },
            (fieldProps["board"] ?: emptyList())))
        add(buildItem("hardware", ctx.getString(R.string.t_hardware), Category.HARDWARE, "Build.HARDWARE", { Build.HARDWARE },
            (fieldProps["hardware"] ?: emptyList())))
        add(buildItem("fingerprint", ctx.getString(R.string.t_fingerprint), Category.FINGERPRINT, "Build.FINGERPRINT", { Build.FINGERPRINT },
            (fieldProps["fingerprint"] ?: emptyList())))
        add(buildItem("bootloader", ctx.getString(R.string.t_bootloader), Category.BOOT, "Build.BOOTLOADER", { Build.BOOTLOADER },
            (fieldProps["bootloader"] ?: emptyList())))
        add(buildItem("tags", ctx.getString(R.string.t_build_tags), Category.BUILD, "Build.TAGS", { Build.TAGS }, (fieldProps["tags"] ?: emptyList())))
        add(buildItem("type", ctx.getString(R.string.t_build_type), Category.BUILD, "Build.TYPE", { Build.TYPE }, (fieldProps["type"] ?: emptyList())))
        add(buildItem("buildid", ctx.getString(R.string.t_build_id), Category.BUILD, "Build.ID", { Build.ID }, (fieldProps["buildid"] ?: emptyList())))
        add(buildItem("host", ctx.getString(R.string.t_build_host), Category.BUILD, "Build.HOST", { Build.HOST }, (fieldProps["host"] ?: emptyList())))
        add(buildItem("displayid", ctx.getString(R.string.t_display_id), Category.BUILD, "Build.DISPLAY", { Build.DISPLAY }, (fieldProps["displayid"] ?: emptyList())))

        add(buildItem("version_release", ctx.getString(R.string.t_os_release), Category.BUILD, "Build.VERSION.RELEASE",
            { Build.VERSION.RELEASE }, (fieldProps["version_release"] ?: emptyList())))
        add(buildItem("version_sdk", ctx.getString(R.string.t_sdk_int), Category.BUILD, "Build.VERSION.SDK_INT",
            { Build.VERSION.SDK_INT.toString() }, (fieldProps["version_sdk"] ?: emptyList())))
        add(buildItem("version_codename", ctx.getString(R.string.t_version_codename), Category.BUILD, "Build.VERSION.CODENAME",
            { Build.VERSION.CODENAME }, (fieldProps["version_codename"] ?: emptyList())))
        add(buildItem("version_incremental", ctx.getString(R.string.t_incremental), Category.BUILD, "Build.VERSION.INCREMENTAL",
            { Build.VERSION.INCREMENTAL }, (fieldProps["version_incremental"] ?: emptyList())))
        add(buildItem("security_patch", ctx.getString(R.string.t_security_patch), Category.BUILD, "Build.VERSION.SECURITY_PATCH",
            { Build.VERSION.SECURITY_PATCH }, (fieldProps["security_patch"] ?: emptyList()),
            solution = ctx.getString(R.string.sol_patch_prop)))
        add(buildItem("build_time_utc", ctx.getString(R.string.t_build_time_utc_epoch), Category.BUILD, "Build.TIME/1000",
            { (Build.TIME / 1000).toString() }, (fieldProps["build_time_utc"] ?: emptyList())))

        add(probe("id:serial", ctx.getString(R.string.t_serial), Category.IDENTITY, listOf(
            @Suppress("DEPRECATION", "HardwareIds") jm("Build.SERIAL") {
                Build.SERIAL.takeIf { it != Build.UNKNOWN } ?: Sentinels.NOT_PERMITTED
            },
            jm("Build.getSerial()") {
                @Suppress("HardwareIds") Build.getSerial().takeIf { it != Build.UNKNOWN } ?: Sentinels.NOT_PERMITTED
            },
        ) + propTrio("ril.serialnumber") + propTrio("ro.serialno") + propTrio("ro.boot.serialno") + listOf(
            sm("cat /proc/cmdline 2>/dev/null | tr '\\0' '\\n' | sed -n 's/^androidboot.serialno=//p'", "cmdline androidboot.serialno"),
        ), sensitive = true))

        add(probe("kernel:release", ctx.getString(R.string.t_kernel_release), Category.BOOT, listOf(
            jm("System.getProperty(os.version)") { System.getProperty("os.version") },
            nm("uname(2).release") { NativeBridge.uname()?.release },
            sm("uname -r", "uname -r"),
        )))
        add(probe("kernel:version", ctx.getString(R.string.t_kernel_version), Category.BOOT, listOf(
            jm("read /proc/version") { File("/proc/version").readText().trim() },
            nm("open/read /proc/version") { NativeBridge.readFileOrReason("/proc/version", 512)?.trim() },
            smr("cat /proc/version", "cat /proc/version"),
        )))
        add(probe("cpu:abi", ctx.getString(R.string.t_primary_abi), Category.HARDWARE, listOf(
            jm("Build.SUPPORTED_ABIS[0]") { Build.SUPPORTED_ABIS.firstOrNull() },
        ) + propTrio("ro.product.cpu.abi") + listOf(
            nm(".so compile-time ABI") { NativeBridge.arch() },
        )))

        add(probe("net:hostname", ctx.getString(R.string.t_hostname), Category.NETWORK,
            propTrio("net.hostname", compare = false) + listOf(
            jm("InetAddress.getLocalHost().hostName") { InetAddress.getLocalHost().hostName },
            nm("gethostname(2)") { NativeBridge.hostname() },
            jm("Os.uname().nodename") { android.system.Os.uname().nodename },
            sm("uname -n", "uname -n"),
        )))
        add(probe("proc:uid", ctx.getString(R.string.t_uid), Category.PROCESS, listOf(
            jm("Process.myUid()") { Process.myUid().toString() },
            nm("getuid(2)") { NativeBridge.ids()?.uid?.toString() },
            sm("id -u", "id -u"),
        )))
        add(probe("proc:pid", ctx.getString(R.string.t_pid), Category.PROCESS, listOf(
            jm("Process.myPid()") { Process.myPid().toString() },
            nm("getpid(2)") { NativeBridge.ids()?.pid?.toString() },
            jm("Os.getpid()") { android.system.Os.getpid().toString() },
        )))
        add(probe("proc:boot_id", ctx.getString(R.string.t_boot_id), Category.BOOT, listOf(
            jm("read /proc/sys/kernel/random/boot_id") { File("/proc/sys/kernel/random/boot_id").readText().trim() },
            nm("open/read boot_id") { NativeBridge.readFile("/proc/sys/kernel/random/boot_id", 128)?.trim() },
            sm("cat /proc/sys/kernel/random/boot_id", "cat boot_id"),
        )))
        add(probe("proc:uptime", ctx.getString(R.string.t_uptime_s), Category.PROCESS, listOf(
            jm("SystemClock.elapsedRealtime()/1000") { (SystemClock.elapsedRealtime() / 1000).toString() },
            smr("cut -d. -f1 /proc/uptime", "/proc/uptime", compare = false),
        )))

        add(probe("time:timezone", ctx.getString(R.string.t_timezone), Category.LOCALE, listOf(
            jm("TimeZone.getDefault().id") { TimeZone.getDefault().id },
        ) + propTrio("persist.sys.timezone")))
        add(probe("storage:data", ctx.getString(R.string.t_storage_data_total_bytes), Category.STORAGE, listOf(
            jm("StatFs(/data)") { android.os.StatFs("/data").let { (it.blockCountLong * it.blockSizeLong).toString() } },
            nm("statvfs(/data)") { NativeBridge.statfs("/data")?.total?.toString() },
            jm("Os.statvfs(/data)") { c ->
                val st = android.system.Os.statvfs("/data")
                (st.f_blocks * st.f_frsize).toString()
            },
        )))

        add(probe("sec:screen_capture", ctx.getString(R.string.t_screen_capture_disabled_policy), Category.SECURITY, listOf(
            jm("DevicePolicyManager.getScreenCaptureDisabled(null)") { ctx ->
                ctx.getSystemService(android.app.admin.DevicePolicyManager::class.java)
                    ?.getScreenCaptureDisabled(null)?.toString()
            },
        )))
    }
}
