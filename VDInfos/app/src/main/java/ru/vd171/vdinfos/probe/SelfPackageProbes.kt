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
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@SuppressLint("QueryPermissionsNeeded")
object SelfPackageProbes {

    private fun pm(c: Context) = c.packageManager
    private fun self(c: Context) = c.packageName

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        val meuUid = Process.myUid()

        fun s(
            id: String, titleRes: Int, api: Int = 0,
            shell: String? = null, shellSource: String? = null, compare: Boolean = true,
            shellCompare: Boolean = compare,
            native: (() -> String?)? = null,
            os: (() -> String?)? = null,
            apk: ((Context) -> String?)? = null,
            get: (Context) -> String?,
        ) = add(probe("self:$id", ctx.getString(titleRes), Category.PACKAGES, buildList {
            add(jm("PackageManager.$id") { c -> if (Build.VERSION.SDK_INT < api) null else get(c) })
            if (os != null) add(jm("Os.$id", compare) { os() })
            if (apk != null) add(jm("APK on disk: $id") { c -> apk(c) })
            if (native != null) add(nm("native $id", compare) { native() })
            if (shell != null) add(smr(shell, shellSource ?: "sh: $shell", shellCompare))
        }))

        s("packageName", R.string.t_self_packageName, shell = "cat /proc/${Process.myPid()}/cmdline | tr -d '\\0'", shellSource = "/proc/<pid>/cmdline", shellCompare = false, native = { NativeBridge.readFileOrReason("/proc/self/cmdline", 256)?.trim { c -> c <= ' ' } }) { self(it) }
        s("uid", R.string.t_self_uid, os = { android.system.Os.getuid().toString() }, shell = "id -u", shellSource = "id -u", native = { NativeBridge.ids()?.uid?.toString() }) { Process.myUid().toString() }
        s("packagesForUid", R.string.t_self_packagesForUid, shell = "pm list packages --uid $meuUid | cut -d: -f2 | cut -d' ' -f1 | paste -sd, -", shellSource = "pm list packages --uid $meuUid") { c ->
            pm(c).getPackagesForUid(Process.myUid())?.joinToString(", ")
        }
        s("nameForUid", R.string.t_self_nameForUid, shell = "pm list packages --uid $meuUid | cut -d: -f2 | cut -d' ' -f1 | head -1", shellSource = "pm list packages --uid $meuUid") { c -> pm(c).getNameForUid(Process.myUid()) }
        s("packageGids", R.string.t_self_packageGids, shell = "id -G", shellSource = "id -G (grupos do kernel)", compare = false) { c ->
            pm(c).getPackageGids(self(c)).joinToString(", ")
        }
        s("packageUid", R.string.t_self_packageUid, os = { android.system.Os.getuid().toString() }, shell = "id -u", shellSource = "id -u", native = { NativeBridge.ids()?.uid?.toString() }, api = 24) { c ->
            pm(c).getPackageUid(self(c), 0).toString()
        }
        s("installer", R.string.t_self_installer, shell = "pm list packages -i ru.vd171.vdinfos", shellSource = "pm list packages -i", compare = false) { c ->
            @Suppress("DEPRECATION") pm(c).getInstallerPackageName(self(c))
        }
        s("installSource", R.string.t_self_installSource, shell = "pm list packages -i ru.vd171.vdinfos", shellSource = "pm list packages -i", compare = false, api = 30) { c ->
            val i = pm(c).getInstallSourceInfo(self(c))
            val upd = if (Build.VERSION.SDK_INT >= 34) i.updateOwnerPackageName else null
            "installing=${i.installingPackageName} initiating=${i.initiatingPackageName} originating=${i.originatingPackageName} updateOwner=$upd"
        }
        s("times", R.string.t_self_times) { c ->
            val pi = pm(c).getPackageInfo(self(c), 0)
            val f = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
            "first=${f.format(Date(pi.firstInstallTime))} lastUpdate=${f.format(Date(pi.lastUpdateTime))}"
        }
        s("versionCode", R.string.t_self_versionCode, apk = { c ->
            val f = c.applicationInfo.sourceDir
            val pi = c.packageManager.getPackageArchiveInfo(f, 0)
            pi?.let { if (Build.VERSION.SDK_INT >= 28) it.longVersionCode.toString()
                      else @Suppress("DEPRECATION") it.versionCode.toString() }
        }, shell = "dumpsys package ru.vd171.vdinfos | grep -m1 versionCode", shellSource = "dumpsys package | versionCode", compare = false) { c ->
            val pi = pm(c).getPackageInfo(self(c), 0)
            (if (Build.VERSION.SDK_INT >= 28) pi.longVersionCode else @Suppress("DEPRECATION") pi.versionCode.toLong()).toString()
        }
        s("targetSdk", R.string.t_self_targetSdk, apk = { c ->
            c.packageManager.getPackageArchiveInfo(c.applicationInfo.sourceDir, 0)
                ?.applicationInfo?.targetSdkVersion?.toString()
        }, shell = "dumpsys package ru.vd171.vdinfos | grep -m1 targetSdk", shellSource = "dumpsys package | targetSdk", compare = false, api = 31) { c -> pm(c).getTargetSdkVersion(self(c)).toString() }
        s("enabledSetting", R.string.t_self_enabledSetting, shell = "pm list packages -e ru.vd171.vdinfos", shellSource = "pm list packages -e", compare = false) { c -> pm(c).getApplicationEnabledSetting(self(c)).toString() }
        s("isInstantApp", R.string.t_self_isInstantApp, api = 26) { c -> pm(c).isInstantApp(self(c)).toString() }
        s("isPackageSuspended", R.string.t_self_isPackageSuspended, api = 29) { c ->
            @Suppress("NewApi") pm(c).isPackageSuspended(self(c)).toString()
        }
        s("autoRevokeWhitelisted", R.string.t_self_autoRevokeWhitelisted, api = 30) { c ->
            pm(c).isAutoRevokeWhitelisted(self(c)).toString()
        }
        s("syntheticDetails", R.string.t_self_syntheticDetails, api = 29) { c ->
            pm(c).getSyntheticAppDetailsActivityEnabled(self(c)).toString()
        }
        s("whitelistedRestricted", R.string.t_self_whitelistedRestricted, api = 29) { c ->
            pm(c).getWhitelistedRestrictedPermissions(self(c), PackageManager.FLAG_PERMISSION_WHITELIST_INSTALLER)
                .joinToString(", ").ifEmpty { Sentinels.NONE }
        }
        s("moduleInfo", R.string.t_self_moduleInfo, api = 30) { c ->
            runCatching { pm(c).getModuleInfo(self(c), 0).name?.toString() }.getOrNull() ?: "(not a module)"
        }
        s("checkAdId", R.string.t_self_checkAdId) { c ->
            pm(c).checkPermission("com.google.android.gms.permission.AD_ID", self(c)).toString()
        }
        s("launchIntent", R.string.t_self_launchIntent) { c ->
            (pm(c).getLaunchIntentForPackage(self(c)) != null).toString()
        }
        s("leanbackIntent", R.string.t_self_leanbackIntent) { c ->
            (pm(c).getLeanbackLaunchIntentForPackage(self(c)) != null).toString()
        }
        s("xposedMeta", R.string.t_self_xposedMeta) { c ->
            val md = pm(c).getApplicationInfo(self(c), PackageManager.GET_META_DATA).metaData
                ?: return@s "(no metaData)"
            AssetData.packages(c, "xposed_manifest_meta_keys")
                .filter { md.containsKey(it) }
                .joinToString("\n") { "$it=${md.get(it)}" }.ifEmpty { Sentinels.NONE }
        }
        s("resourcesForApp", R.string.t_self_resourcesForApp) { c ->
            (pm(c).getResourcesForApplication(self(c)) != null).toString()
        }
    }
}
