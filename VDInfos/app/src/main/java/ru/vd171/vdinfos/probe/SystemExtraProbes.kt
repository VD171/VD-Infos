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

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import android.hardware.SensorManager
import android.media.MediaDrm
import android.os.Build
import android.provider.CallLog
import android.webkit.WebSettings
import android.webkit.WebView
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.io.File
import java.util.UUID

@SuppressLint("HardwareIds", "MissingPermission")
object SystemExtraProbes {

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        add(probe("sys:selinux", ctx.getString(R.string.t_selinux_enforce), Category.SECURITY, listOf(
            jm("read /sys/fs/selinux/enforce") { File("/sys/fs/selinux/enforce").readText().trim() },
            nm("open/read /sys/fs/selinux/enforce") { NativeBridge.readFileOrReason("/sys/fs/selinux/enforce", 8)?.trim() },
            smr("cat /sys/fs/selinux/enforce", "cat /sys/fs/selinux/enforce"),
            smr("getenforce | sed -e 's/^Enforcing$/1/' -e 's/^Permissive$/0/'", "getenforce"),
        ) + propTrio("ro.boot.selinux", compare = false)))

        add(jprobe("sys:accounts", ctx.getString(R.string.t_accounts), Category.ACCOUNTS, sensitive = true,
            source = "AccountManager.getAccounts()") { c ->
            AccountManager.get(c).accounts.joinToString("\n") { "${it.type}: ${it.name}" }.ifEmpty { null }
        })

        add(jprobe("sys:calllog", ctx.getString(R.string.t_call_log_entries), Category.ACCOUNTS, sensitive = true,
            source = "CallLog.Calls query count") { c ->
            c.contentResolver.query(CallLog.Calls.CONTENT_URI, null, null, null, null)?.use { it.count.toString() }
        })

        val meuUser = android.os.Process.myUid() / 100000
        val contaPacotes = "p=\$(/system/bin/pm list packages --user $meuUser 2>&1); " +
            "case \"\$p\" in *package:*) printf '%s\\n' \"\$p\" | grep -c '^package:';; " +
            "*) printf '%s\\n' \"\$p\" | head -1;; esac"
        val fontePacotes = "pm list packages --user $meuUser | count"
        add(probe("pkg:installed_count", ctx.getString(R.string.t_installed_packages), Category.PACKAGES, listOf(
            jm("PackageManager.getInstalledPackages().size") { c ->
                c.packageManager.getInstalledPackages(0).size.toString()
            },
            smr(contaPacotes, fontePacotes),
        )))
        add(probe("pkg:apps_count", ctx.getString(R.string.t_installed_applications), Category.PACKAGES, listOf(
            jm("PackageManager.getInstalledApplications().size") { c ->
                c.packageManager.getInstalledApplications(0).size.toString()
            },
            smr(contaPacotes, fontePacotes),
        )))
        val nomesPm = "/system/bin/pm list packages --user $meuUser 2>/dev/null | sed 's/^package://'"
        add(jprobe("pkg:installed_diff", ctx.getString(R.string.t_installed_packages_diff), Category.PACKAGES,
            source = "getInstalledPackages() ⊖ pm list packages") { c ->
            diffLenses(c.packageManager.getInstalledPackages(0).map { it.packageName }, nomesPm)
        })
        add(jprobe("pkg:apps_diff", ctx.getString(R.string.t_installed_applications_diff), Category.PACKAGES,
            source = "getInstalledApplications() ⊖ pm list packages") { c ->
            diffLenses(c.packageManager.getInstalledApplications(0).map { it.packageName }, nomesPm)
        })
        add(jprobe("pkg:installer", ctx.getString(R.string.t_this_app_installer), Category.PACKAGES,
            source = "PackageManager.getInstallerPackageName") { c ->
            @Suppress("DEPRECATION") c.packageManager.getInstallerPackageName(c.packageName)
        })
        add(jprobe("pkg:self_signature", ctx.getString(R.string.t_this_app_signing_sha_256), Category.PACKAGES,
            source = "PackageManager signatures + SHA-256") { c ->
            @Suppress("PackageManagerGetSignatures", "DEPRECATION")
            val pi = c.packageManager.getPackageInfo(c.packageName, android.content.pm.PackageManager.GET_SIGNATURES)
            val sig = pi.signatures?.firstOrNull()?.toByteArray() ?: return@jprobe null
            java.security.MessageDigest.getInstance("SHA-256").digest(sig)
                .joinToString(":") { b -> "%02X".format(b) }
        })

        add(jprobe("hw:sensor_count", ctx.getString(R.string.t_sensor_count), Category.SENSORS,
            source = "SensorManager.getSensorList(TYPE_ALL).size") { c ->
            (c.getSystemService(Context.SENSOR_SERVICE) as SensorManager).getSensorList(-1).size.toString()
        })
        add(jprobe("hw:sensors", ctx.getString(R.string.t_sensors), Category.SENSORS,
            source = "SensorManager.getSensorList(TYPE_ALL)") { c ->
            (c.getSystemService(Context.SENSOR_SERVICE) as SensorManager).getSensorList(-1)
                .joinToString("\n") { "${it.name} (${it.vendor})" }
        })

        add(jprobe("hw:display", ctx.getString(R.string.t_display_metrics), Category.DISPLAY,
            source = "Resources.displayMetrics") { c ->
            val m = c.resources.displayMetrics
            "${m.widthPixels}x${m.heightPixels} ${m.densityDpi}dpi density=${m.density}"
        })

        add(jprobe("media:drm_id", ctx.getString(R.string.t_widevine_device_id), Category.MEDIA, sensitive = true,
            source = "MediaDrm(WIDEVINE).getPropertyByteArray(deviceUniqueId)") {
            val widevine = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
            val drm = MediaDrm(widevine)
            try {
                val id = drm.getPropertyByteArray(MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
                android.util.Base64.encodeToString(id, android.util.Base64.NO_WRAP)
            } finally { if (Build.VERSION.SDK_INT >= 28) drm.close() else @Suppress("DEPRECATION") drm.release() }
        })
        add(jprobe("media:drm_vendor", ctx.getString(R.string.t_widevine_vendor_version), Category.MEDIA,
            source = "MediaDrm(WIDEVINE) vendor+version") {
            val widevine = UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
            val drm = MediaDrm(widevine)
            try {
                drm.getPropertyString("vendor") + " / " + drm.getPropertyString("version")
            } finally { if (Build.VERSION.SDK_INT >= 28) drm.close() else @Suppress("DEPRECATION") drm.release() }
        })

        add(jprobe("web:ua_webview", ctx.getString(R.string.t_webview_user_agent), Category.WEBVIEW,
            source = "WebSettings.getDefaultUserAgent") { c -> WebSettings.getDefaultUserAgent(c) })
        add(jprobe("web:ua_http", ctx.getString(R.string.t_http_agent), Category.WEBVIEW,
            source = "System.getProperty(http.agent)") { System.getProperty("http.agent") })
        add(jprobe("web:package", ctx.getString(R.string.t_webview_provider), Category.WEBVIEW,
            source = "WebView.getCurrentWebViewPackage()") {
            WebView.getCurrentWebViewPackage()?.let { "${it.packageName} ${it.versionName}" }
        })


        add(probe("hw:cpu_cores", ctx.getString(R.string.t_cpu_cores), Category.HARDWARE, listOf(
            jm("Runtime.availableProcessors()") { Runtime.getRuntime().availableProcessors().toString() },
            jm("Os.sysconf(_SC_NPROCESSORS_ONLN)") {
                android.system.Os.sysconf(android.system.OsConstants._SC_NPROCESSORS_ONLN).toString()
            },
            sm("awk -F- '{ print $2 + 1 }' /sys/devices/system/cpu/present", "/sys/devices/system/cpu/present"),
            sm("nproc", "nproc (affinity of this process)", compare = false),
        )))
        add(probe("hw:cpuinfo", ctx.getString(R.string.t_cpuinfo), Category.HARDWARE, listOf(
            jm("read /proc/cpuinfo") {
                File("/proc/cpuinfo").readText().lineSequence().filterNot { it.startsWith("Processor") }
                    .joinToString("\n").trim()
            },
            sm("cat /proc/cpuinfo | grep -v '^Processor'", "cat /proc/cpuinfo"),
        )))

        add(jprobe("hw:mem_total", ctx.getString(R.string.t_total_ram_bytes), Category.HARDWARE,
            source = "ActivityManager.MemoryInfo.totalMem") { c ->
            val mi = ActivityManager.MemoryInfo()
            (c.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).getMemoryInfo(mi)
            mi.totalMem.toString()
        })
        add(probe("hw:meminfo", ctx.getString(R.string.t_meminfo), Category.HARDWARE, listOf(
            jm("read /proc/meminfo", compare = false) { File("/proc/meminfo").readText().trim() },
            nm("open/read /proc/meminfo", compare = false) { NativeBridge.readFileOrReason("/proc/meminfo")?.trim() },
            sm("cat /proc/meminfo", "cat /proc/meminfo", compare = false),
        )))
        add(probe("hw:mem_total_kb", ctx.getString(R.string.t_mem_total_kb), Category.HARDWARE, listOf(
            jm("MemoryInfo.totalMem / 1024") { c ->
                val am = c.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
                val mi = android.app.ActivityManager.MemoryInfo(); am.getMemoryInfo(mi)
                (mi.totalMem / 1024).toString()
            },
            jm("/proc/meminfo MemTotal") {
                File("/proc/meminfo").readLines().firstOrNull { it.startsWith("MemTotal") }
                    ?.filter { ch -> ch.isDigit() }
            },
            nm("open/read /proc/meminfo MemTotal") {
                NativeBridge.readFileOrReason("/proc/meminfo")
                    ?.lineSequence()?.firstOrNull { it.startsWith("MemTotal") }
                    ?.filter { ch -> ch.isDigit() }
            },
            sm("awk '/^MemTotal/{print $2}' /proc/meminfo", "MemTotal"),
        ), note = null))

        add(probe("emu:test_harness", ctx.getString(R.string.t_test_harness), Category.INTEGRITY, listOf(
            jm("ActivityManager.isRunningInTestHarness()") {
                @Suppress("DEPRECATION") android.app.ActivityManager.isRunningInTestHarness().toString()
            },
        ) + propTrio("ro.test_harness", map = { it ?: "false" })))
        add(probe("emu:user_test_harness", ctx.getString(R.string.t_user_test_harness), Category.INTEGRITY, listOf(
            jm("ActivityManager.isRunningInUserTestHarness()") {
                if (Build.VERSION.SDK_INT >= 33) android.app.ActivityManager.isRunningInUserTestHarness().toString() else null
            },
        )))
        add(probe("emu:monkey", ctx.getString(R.string.t_user_a_monkey), Category.INTEGRITY, listOf(
            jm("ActivityManager.isUserAMonkey()") { android.app.ActivityManager.isUserAMonkey().toString() },
        )))
        add(probe("hw:low_ram", ctx.getString(R.string.t_low_ram_device), Category.HARDWARE, listOf(
            jm("ActivityManager.isLowRamDevice()") { c ->
                (c.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager).isLowRamDevice.toString()
            },
        ) + propTrio("ro.config.low_ram")))

        for (v in AssetData.packages(ctx, "env_vars")) {
            add(probe("env:$v", ctx.getString(R.string.t_env, v), Category.SYSTEM, listOf(
                jm("System.getenv($v)") { System.getenv(v) },
                jm("Os.getenv($v)") { android.system.Os.getenv(v) },
                sm("echo \$$v", "echo \$$v"),
                nsm("echo \$$v", "popen: echo \$$v"),
            )))
        }

        add(probe("sys:locale", ctx.getString(R.string.t_locale), Category.LOCALE, listOf(
            jm("Resources.getSystem() configuration locale") {
                Resources.getSystem().configuration.locales
                    .takeIf { !it.isEmpty }?.get(0)?.toLanguageTag()
            },
        ) + propTrio("persist.sys.locale")))
    }

    private fun diffLenses(jvm: List<String>, pmCmd: String): String {
        val jvmSet = jvm.toSortedSet()
        val out = Exec.run(pmCmd, timeoutMs = 8000, cap = 400000)
            ?: return Sentinels.RESTRICTED
        val pmSet = out.lineSequence().map { it.trim() }.filter { it.isNotEmpty() }.toSortedSet()
        val onlyJvm = (jvmSet - pmSet)
        val onlyPm = (pmSet - jvmSet)
        if (onlyJvm.isEmpty() && onlyPm.isEmpty()) return Sentinels.NONE
        return buildString {
            if (onlyJvm.isNotEmpty()) append("JVM → pm (${onlyJvm.size}):\n")
                .append(onlyJvm.joinToString("\n"))
            if (onlyPm.isNotEmpty()) {
                if (isNotEmpty()) append("\n")
                append("pm → JVM (${onlyPm.size}):\n").append(onlyPm.joinToString("\n"))
            }
        }
    }
}
