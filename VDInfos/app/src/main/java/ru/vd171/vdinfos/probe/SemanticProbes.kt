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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.zip.GZIPInputStream

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
            add(nm("property_get $k", compare = cmp, tag = "JNI 92B") { NativeBridge.syspropClassic(k) })
            add(sm("getprop $k", "getprop $k", compare = cmp))
        }
        attestField?.let { f -> add(am("KeyStore attestation record", compare = attestCompare) { f() }) }
    }, sensitive, solution = solution)

    private fun readSysctlKernel(name: String): String? =
        runCatching { File("/proc/sys/kernel/$name").readText().trim() }.getOrNull()?.ifEmpty { null }

    private fun relOf(procVersion: String?): String? {
        val m = procVersion?.let { Regex("""Linux version (\S+)""").find(it) } ?: return null
        return m.groupValues[1]
    }

    private fun verOf(procVersion: String?): String? {
        val m = procVersion?.let { Regex("""#\d+.*""").find(it.trim()) } ?: return null
        return m.value.trim()
    }

    private fun kernelMajorMinor(s: String): String? {
        val m = Regex("""^(\d+)\.(\d+)""").find(s.trim()) ?: return null
        return "${m.groupValues[1]}.${m.groupValues[2]}"
    }

    private val kernelDateRegex = Regex("""[A-Z][a-z]{2} [A-Z][a-z]{2}\s+\d+ \d{2}:\d{2}:\d{2} \S+ \d{4}""")

    private fun kernelBuildDateStr(unameVersion: String?): String? =
        unameVersion?.let { kernelDateRegex.find(it)?.value }

    private fun kernelBuildEpoch(unameVersion: String?): Long? {
        val date = kernelBuildDateStr(unameVersion) ?: return null
        return runCatching {
            SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy", Locale.US).parse(date)?.time?.div(1000)
        }.getOrNull()
    }

    private fun configMarkers(cfg: String): String {
        val hits = buildList {
            if (Regex("""(?m)^CONFIG_KSU=y""").containsMatchIn(cfg)) add("KSU")
            if (cfg.contains("SUSFS", ignoreCase = true)) add("SUSFS")
            Regex("""(?m)^CONFIG_LOCALVERSION="([^"]+)"""").find(cfg)?.let { add("localversion=${it.groupValues[1]}") }
            if (Regex("""(?m)^CONFIG_MODULE_SIG=y""").containsMatchIn(cfg) &&
                !Regex("""(?m)^CONFIG_MODULE_SIG_FORCE=y""").containsMatchIn(cfg)) add("module-sig-unenforced")
            if (Regex("""(?m)^CONFIG_KALLSYMS_ALL=y""").containsMatchIn(cfg)) add("kallsyms-all")
        }
        return if (hits.isEmpty()) "clean" else hits.joinToString(",")
    }

    private fun moduleHits(): String {
        val d = File("/sys/module").listFiles() ?: return Sentinels.EACCES
        val hits = d.map { it.name }
            .filter { Regex("ksu|susfs|magisk|kernelsu|zygisk", RegexOption.IGNORE_CASE).containsMatchIn(it) }
        return if (hits.isEmpty()) "clean" else hits.sorted().joinToString(",")
    }

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
            am("attestation serial") { Attestation.record?.serial },
        ) + propTrio("ril.serialnumber") + propTrio("ro.serialno") + propTrio("ro.boot.serialno") + listOf(
            sm("cat /proc/cmdline 2>/dev/null | tr '\\0' '\\n' | sed -n 's/^androidboot.serialno=//p'", "cmdline androidboot.serialno"),
        ), sensitive = true))

        add(probe("kernel:release", ctx.getString(R.string.t_kernel_release), Category.BOOT, listOf(
            jm("System.getProperty(os.version)") { System.getProperty("os.version")?.ifEmpty { null } },
            nm("uname(2).release") { NativeBridge.uname()?.release },
            sm("uname -r", "uname -r"),
            jm("read /proc/sys/kernel/osrelease") { readSysctlKernel("osrelease") },
            nm("native read /proc/sys/kernel/osrelease") { NativeBridge.readFile("/proc/sys/kernel/osrelease", 256)?.trim()?.ifEmpty { null } },
            jm("release field of /proc/version") { relOf(runCatching { File("/proc/version").readText() }.getOrNull()) },
        )))
        add(probe("kernel:version", ctx.getString(R.string.t_kernel_version), Category.BOOT, listOf(
            jm("read /proc/version") { File("/proc/version").readText().trim() },
            nm("open/read /proc/version") { NativeBridge.readFileOrReason("/proc/version", 512)?.trim() },
            smr("cat /proc/version", "cat /proc/version"),
        )))
        add(probe("kernel:version_string", ctx.getString(R.string.t_kernel_version_string), Category.BOOT, listOf(
            nm("uname(2).version") { NativeBridge.uname()?.version },
            sm("uname -v", "uname -v"),
            jm("read /proc/sys/kernel/version") { readSysctlKernel("version") },
            nm("native read /proc/sys/kernel/version") { NativeBridge.readFile("/proc/sys/kernel/version", 256)?.trim()?.ifEmpty { null } },
            jm("version field of /proc/version") { verOf(runCatching { File("/proc/version").readText() }.getOrNull()) },
        )))
        add(probe("kernel:version_claim", ctx.getString(R.string.t_kernel_version_claim), Category.INTEGRITY, listOf(
            jm("ro.kernel.version prop consistent with real uname release (expected)") { "consistent" },
            jm("ro.kernel.version (prop) vs uname(2).release, major.minor") {
                val prop = SystemProps.get("ro.kernel.version")?.ifEmpty { null } ?: return@jm "consistent"
                val rel = NativeBridge.uname()?.release?.ifEmpty { null } ?: return@jm "consistent"
                val pm = kernelMajorMinor(prop); val rm = kernelMajorMinor(rel)
                if (pm == null || rm == null || pm == rm) "consistent" else "MISMATCH prop=$pm uname=$rm"
            },
            jm("values", compare = false) {
                "ro.kernel.version=${SystemProps.get("ro.kernel.version")?.ifEmpty { null } ?: Sentinels.ABSENT}" +
                    " uname.release=${NativeBridge.uname()?.release ?: Sentinels.NONE}"
            },
        ), note = ctx.getString(R.string.note_kernel_version_claim)))
        add(probe("kernel:build_date", ctx.getString(R.string.t_kernel_build_date), Category.BOOT, listOf(
            jm("kernel build not newer than the ROM (expected)") { "ok" },
            jm("kernel build (uname) vs ro.build.date.utc") {
                val kv = kernelBuildEpoch(NativeBridge.uname()?.version) ?: return@jm "ok"
                val rom = SystemProps.get("ro.build.date.utc")?.trim()?.toLongOrNull() ?: return@jm "ok"
                val days = ((kv - rom) / 86400.0).toInt()
                if (days > 30) "kernel NEWER than ROM by ${days}d (rebuilt/flashed after ROM)" else "ok"
            },
            jm("dates", compare = false) {
                "kernel=${kernelBuildDateStr(NativeBridge.uname()?.version) ?: Sentinels.NONE}" +
                    " rom.utc=${SystemProps.get("ro.build.date.utc")}" +
                    " vendor.utc=${SystemProps.get("ro.vendor.build.date.utc")}"
            },
        ), note = ctx.getString(R.string.note_kernel_build_date)))
        add(probe("kernel:config", ctx.getString(R.string.t_kernel_config), Category.INTEGRITY, listOf(
            jm("markers /proc/config.gz (KSU/SUSFS/localversion)") {
                val raw = runCatching {
                    GZIPInputStream(File("/proc/config.gz").inputStream()).bufferedReader().use { it.readText() }
                }.getOrNull() ?: return@jm Sentinels.EACCES
                configMarkers(raw)
            },
            smr("zcat /proc/config.gz 2>/dev/null | grep -iE 'CONFIG_KSU|SUSFS|CONFIG_LOCALVERSION=' | head " +
                "|| echo ${Sentinels.EACCES}", "zcat config.gz | grep", compare = false),
        ), note = ctx.getString(R.string.note_kernel_config)))
        add(probe("kernel:modules", ctx.getString(R.string.t_kernel_module_scan), Category.INTEGRITY, listOf(
            jm("scan /sys/module for ksu/susfs/magisk") { moduleHits() },
            smr("ls /sys/module 2>/dev/null | grep -iE 'ksu|susfs|magisk|zygisk' || echo clean",
                "ls /sys/module | grep", compare = false),
            smr("grep -iE ' (ksu|susfs|magisk)[a-z_]*\$' /proc/kallsyms 2>/dev/null | head " +
                "|| echo ${Sentinels.EACCES}", "kallsyms symbol names", compare = false),
        ), note = ctx.getString(R.string.note_kernel_module_scan)))
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
