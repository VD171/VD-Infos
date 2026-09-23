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
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.io.File

object SandboxProbes {

    private fun javaReadOrReason(path: String): String = try {
        File(path).readText().trim().ifEmpty { "(empty)" }
    } catch (t: Throwable) {
        val m = t.message.orEmpty()
        when {
            m.contains(Sentinels.EACCES) -> Sentinels.EACCES
            m.contains("ENOENT") -> "ENOENT"
            m.contains(Sentinels.EPERM) -> Sentinels.EPERM
            m.contains("EISDIR") -> "EISDIR"
            else -> t.javaClass.simpleName
        }
    }

    private fun catOrReason(path: String) =
        "cat $path 2>&1 | sed -e 's/.*Permission denied.*/${Sentinels.EACCES}/' " +
            "-e 's/.*No such file or directory.*/ENOENT/' " +
            "-e 's/.*Not a directory.*/ENOTDIR/' | head -1"

    private fun closedItem(id: String, title: String, path: String, cat: Category) =
        probe(id, title, cat, listOf(
            jm("read $path") { javaReadOrReason(path) },
            nm("open/read $path (errno)") { NativeBridge.readFileOrReason(path, 4096)?.trim() },
            sm(catOrReason(path), "cat $path"),
        ), sensitive = cat == Category.IDENTITY)

    private fun dumpsysItem(service: String, title: String, cat: Category = Category.INTEGRITY) =
        probe("dumpsys:$service", title, cat, listOf(
            sm("dumpsys $service 2>&1 | sed -e 's/^Permission Denial.*/${Sentinels.DENIED}/' " +
                "-e 's/^.*requires android.permission.*/${Sentinels.DENIED}/' " +
                "-e \"s/^Can't find service.*/NO_SERVICE/\" | head -3",
                "dumpsys $service"),
        ))

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        add(closedItem("sandbox:emmc_cid", ctx.getString(R.string.t_emmc_cid), "/sys/block/mmcblk0/device/cid", Category.IDENTITY))
        add(closedItem("sandbox:emmc_serial", ctx.getString(R.string.t_emmc_serial), "/sys/block/mmcblk0/device/serial", Category.IDENTITY))
        add(closedItem("sandbox:emmc_name", ctx.getString(R.string.t_emmc_name), "/sys/block/mmcblk0/device/name", Category.HARDWARE))
        add(closedItem("sandbox:emmc_manfid", ctx.getString(R.string.t_emmc_manufacturer_id), "/sys/block/mmcblk0/device/manfid", Category.HARDWARE))
        add(closedItem("sandbox:emmc_oemid", ctx.getString(R.string.t_emmc_oem_id), "/sys/block/mmcblk0/device/oemid", Category.HARDWARE))
        add(closedItem("sandbox:emmc_date", ctx.getString(R.string.t_emmc_manufacture_date), "/sys/block/mmcblk0/device/date", Category.HARDWARE))

        add(closedItem("sandbox:ufs_vendor", ctx.getString(R.string.t_ufs_vendor), "/sys/block/sda/device/vendor", Category.HARDWARE))
        add(closedItem("sandbox:ufs_model", ctx.getString(R.string.t_ufs_model), "/sys/block/sda/device/model", Category.HARDWARE))
        add(closedItem("sandbox:ufs_rev", ctx.getString(R.string.t_ufs_revision), "/sys/block/sda/device/rev", Category.HARDWARE))

        add(closedItem("sandbox:soc_id", ctx.getString(R.string.t_soc_id), "/sys/devices/soc0/soc_id", Category.HARDWARE))
        add(closedItem("sandbox:soc_family", ctx.getString(R.string.t_soc_family), "/sys/devices/soc0/family", Category.HARDWARE))
        add(closedItem("sandbox:soc_machine", ctx.getString(R.string.t_soc_machine), "/sys/devices/soc0/machine", Category.HARDWARE))
        add(closedItem("sandbox:soc_serial", ctx.getString(R.string.t_soc_serial_number), "/sys/devices/soc0/serial_number", Category.IDENTITY))

        add(closedItem("sandbox:cmdline", ctx.getString(R.string.t_kernel_cmdline), "/proc/cmdline", Category.BOOT))
        add(closedItem("sandbox:stat", ctx.getString(R.string.t_kernel_stat), "/proc/stat", Category.SYSTEM))
        add(closedItem("sandbox:other_maps", ctx.getString(R.string.t_another_process_maps_init), "/proc/1/maps", Category.INTEGRITY))
        add(closedItem("sandbox:other_cmdline", ctx.getString(R.string.t_another_process_cmdline_init), "/proc/1/cmdline", Category.INTEGRITY))
        add(closedItem("sandbox:kmsg", ctx.getString(R.string.t_kernel_log), "/dev/kmsg", Category.INTEGRITY))
        add(closedItem("sandbox:build_prop", ctx.getString(R.string.t_system_build_prop), "/system/build.prop", Category.SYSTEM))

        add(probe("sandbox:content_cmd", ctx.getString(R.string.t_content_command), Category.INTEGRITY, listOf(
            smr("content query --uri content://settings/global/adb_enabled --projection value " +
                "| sed 's/^Row: 0 value=//'", "content query settings/global", timeoutMs = 8000),
        )))

        for ((svc, titulo) in listOf(
            "package" to "dumpsys package", "battery" to "dumpsys battery",
            "wifi" to "dumpsys wifi", "telephony.registry" to "dumpsys telephony.registry",
            "iphonesubinfo" to "dumpsys iphonesubinfo", "display" to "dumpsys display",
            "sensorservice" to "dumpsys sensorservice", "meminfo" to "dumpsys meminfo",
            "cpuinfo" to "dumpsys cpuinfo", "netstats" to "dumpsys netstats",
            "activity" to "dumpsys activity", "user" to "dumpsys user",
            "settings" to "dumpsys settings", "mount" to "dumpsys mount",
            "deviceidle" to "dumpsys deviceidle", "SurfaceFlinger" to "dumpsys SurfaceFlinger",
            "media.audio_flinger" to "dumpsys media.audio_flinger", "gfxinfo" to "dumpsys gfxinfo",
            "procstats" to "dumpsys procstats", "location" to "dumpsys location",
            "connectivity" to "dumpsys connectivity", "appops" to "dumpsys appops",
        )) {
            add(dumpsysItem(svc, titulo))
        }
    }
}
