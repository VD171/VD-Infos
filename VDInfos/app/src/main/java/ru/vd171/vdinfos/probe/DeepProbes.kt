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
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.io.File

@SuppressLint("MissingPermission")
object DeepProbes {

    private val REASON_TOKENS = setOf(Sentinels.EACCES, Sentinels.EPERM, "ENOENT", "EISDIR", "ENOTDIR", Sentinels.DENIED)

    private fun contextValid(c: String): Boolean = runCatching {
        java.io.RandomAccessFile("/sys/fs/selinux/context", "rw").use { it.write(c.toByteArray()) }
        true
    }.getOrDefault(false)

    private fun avQuery(scon: String, tcon: String, cls: String, perm: String): String = runCatching {
        val idx = File("/sys/fs/selinux/class/$cls/index").readText().trim().toInt()
        val bit = File("/sys/fs/selinux/class/$cls/perms/$perm").readText().trim().toLong()
        java.io.RandomAccessFile("/sys/fs/selinux/access", "rw").use { f ->
            f.write("$scon $tcon $idx ${java.lang.Long.toHexString(bit)}".toByteArray())
            f.seek(0)
            val buf = ByteArray(256)
            val n = f.read(buf)
            if (n <= 0) return@runCatching "?"
            val allowed = String(buf, 0, n).trim().substringBefore(' ').toLong(16)
            if (allowed and bit != 0L) "allow" else "deny"
        }
    }.getOrElse { "?" }

    private fun policyProbe(ctx: Context): String {
        val policyTypes = AssetData.policyTypes(ctx)
        val avQuestions = AssetData.avQuestions(ctx)
        if (policyTypes.isEmpty() || !contextValid(policyTypes[0].second)) return Sentinels.EACCES
        val types = policyTypes.joinToString(" ") { (n, c) -> "$n=${if (contextValid(c)) "yes" else "no"}" }
        val scon = runCatching { File("/proc/self/attr/current").readText().trim { it <= ' ' } }.getOrNull()
        val av = if (scon.isNullOrEmpty()) "?"
        else avQuestions.joinToString(" ") { q -> "${q[0]}=${avQuery(scon, q[1], q[2], q[3])}" }
        return "types: $types\nav: $av"
    }

    private fun selinuxStatus(): String = runCatching {
        val b = ByteArray(20)
        val n = java.io.FileInputStream("/sys/fs/selinux/status").use { it.read(b) }
        if (n < 20) return Sentinels.EACCES
        fun u32(o: Int): Long =
            ((b[o].toLong() and 0xff) or ((b[o + 1].toLong() and 0xff) shl 8) or
                ((b[o + 2].toLong() and 0xff) shl 16) or ((b[o + 3].toLong() and 0xff) shl 24))
        "version=${u32(0)} seq=${u32(4)} enforcing=${u32(8)} " +
            "policyload=${u32(12)} deny_unknown=${u32(16)}"
    }.getOrDefault(Sentinels.EACCES)


    private fun mountMarkers(text: String, systemPaths: List<String>): String {
        val found = sortedSetOf<String>()
        for (line in text.lineSequence()) {
            val sep = line.indexOf(" - ")
            if (sep < 0) continue
            val left = line.substring(0, sep).split(' ')
            val right = line.substring(sep + 3).split(' ')
            val mountPoint = left.getOrNull(4).orEmpty()
            val fsType = right.getOrNull(0).orEmpty()
            val source = right.getOrNull(1).orEmpty()
            val where = "$mountPoint $fsType $source".lowercase()
            if (where.contains("magisk")) found += "magisk"
            if (where.contains("ksu")) found += "ksu"
            if (where.contains("worker")) found += "worker"
            if (where.contains("/data/adb")) found += "data-adb"
            if (fsType == "overlay" && systemPaths.any { mountPoint.startsWith(it) }) {
                found += "overlay-on-system"
            }
        }
        return found.joinToString(", ").ifEmpty { "clean" }
    }

    private fun mountMarkersSh(systemPaths: List<String>): String = """awk '{ p=index($0," - "); if(!p) next; split(substr($0,1,p-1),L," "); split(substr($0,p+3),R," "); w=tolower(L[5] " " R[1] " " R[2]); if (index(w,"magisk")) m["magisk"]=1; if (index(w,"ksu")) m["ksu"]=1; if (index(w,"worker")) m["worker"]=1; if (index(w,"/data/adb")) m["data-adb"]=1; if (R[1]=="overlay" && L[5] ~ "^/(${systemPaths.joinToString("|") { it.trimStart('/') }})") m["overlay-on-system"]=1 } END { n=0; for(k in m) a[++n]=k; for(x=1;x<=n;x++) for(y=x+1;y<=n;y++) if(a[y]<a[x]){t=a[x];a[x]=a[y];a[y]=t} s=""; for(x=1;x<=n;x++) s=s (x>1?", ":"") a[x]; print (n? s : "clean") }' /proc/self/mountinfo 2>/dev/null"""

    private fun grepLine(text: String?, key: String): String? =
        text?.lineSequence()?.firstOrNull { it.startsWith(key) }?.substringAfter(":")?.trim()

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        add(probe("selinux:context", ctx.getString(R.string.t_selinux_context_self), Category.SECURITY, listOf(
            jm("read /proc/self/attr/current") { File("/proc/self/attr/current").readText().trim { it <= ' ' } },
            nm("open/read /proc/self/attr/current") { NativeBridge.readFile("/proc/self/attr/current", 256)?.trim { it <= ' ' } },
            sm("id -Z | sed 's/^context=//'", "id -Z"),
            nsm("id -Z | sed 's/^context=//'", "popen: id -Z"),
        )))
        add(probe("selinux:status", ctx.getString(R.string.t_selinux_status), Category.SECURITY, listOf(
            jm("read /sys/fs/selinux/status (5x u32)") { selinuxStatus() },
            smr(
                "o=\$(dd if=/sys/fs/selinux/status bs=20 count=1 2>/dev/null | od -An -tu4 2>/dev/null | " +
                    "tr -s ' \\n' ' ' | sed 's/^ *//;s/ *\$//'); " +
                    "case \"\$o\" in " +
                    "*[0-9]*' '*[0-9]*' '*[0-9]*' '*[0-9]*' '*[0-9]*) " +
                    "set -- \$o; echo \"version=\$1 seq=\$2 enforcing=\$3 policyload=\$4 deny_unknown=\$5\";; " +
                    "*) echo ${Sentinels.EACCES};; esac",
                "dd+od /sys/fs/selinux/status",
            ),
        )))
        add(jprobe("selinux:policy_probe", ctx.getString(R.string.t_selinux_policy_probe), Category.SECURITY,
            source = "selinuxfs: context validation + compute_av") { policyProbe(ctx) })
        add(probe("selinux:policyvers", ctx.getString(R.string.t_selinux_policy_version), Category.SECURITY, listOf(
            jm("read /sys/fs/selinux/policyvers") { File("/sys/fs/selinux/policyvers").readText().trim() },
            nm("open/read /sys/fs/selinux/policyvers") { NativeBridge.readFileOrReason("/sys/fs/selinux/policyvers", 16)?.trim() },
            smr("cat /sys/fs/selinux/policyvers", "cat policyvers"),
        )))
        add(probe("selinux:mls", ctx.getString(R.string.t_selinux_mls_enabled), Category.SECURITY, listOf(
            nm("open/read /sys/fs/selinux/mls") { NativeBridge.readFile("/sys/fs/selinux/mls", 8)?.trim() },
            sm("cat /sys/fs/selinux/mls", "cat mls"),
        )))

        add(probe("integrity:tracerpid", ctx.getString(R.string.t_tracerpid_debugger), Category.INTEGRITY, listOf(
            jm("grep TracerPid /proc/self/status") { grepLine(File("/proc/self/status").readText(), "TracerPid") },
            nm("open/read /proc/self/status") { grepLine(NativeBridge.readFile("/proc/self/status", 4096), "TracerPid") },
            sm("grep TracerPid /proc/self/status | awk '{print $2}'", "grep TracerPid"),
            nsm("grep TracerPid /proc/self/status | awk '{print $2}'", "popen: grep TracerPid"),
        )))
        add(probe("integrity:mount_markers", ctx.getString(R.string.t_mount_markers_magisk_overlay), Category.INTEGRITY, listOf(
            nm("parse /proc/self/mountinfo") {
                mountMarkers(NativeBridge.readFile("/proc/self/mountinfo", 1 shl 20) ?: return@nm null, AssetData.packages(ctx, "overlay_mount_paths"))
            },
            sm(mountMarkersSh(AssetData.packages(ctx, "overlay_mount_paths")), "parse mountinfo (sh)"),
            nsm(mountMarkersSh(AssetData.packages(ctx, "overlay_mount_paths")), "popen: parse mountinfo (sh)"),
        )))

        add(probe("ime:enabled", ctx.getString(R.string.t_enabled_input_methods), Category.SYSTEM,
            settingBlock("enabled_input_methods") + listOf(
            jm("InputMethodManager.getEnabledInputMethodList", compare = false) { c ->
                (c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .enabledInputMethodList.joinToString("\n") { it.id }
            },
        )))
        add(probe("ime:all", ctx.getString(R.string.t_installed_input_methods), Category.SYSTEM, listOf(
            jm("InputMethodManager.getInputMethodList") { c ->
                (c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .inputMethodList.joinToString("\n") { it.id }
            },
            sm("ime list -a -s", "ime list -a -s"),
        )))

        add(probe("boot:btime", ctx.getString(R.string.t_boot_time_epoch_proc_stat_btime), Category.BOOT, listOf(
            jm("grep btime /proc/stat") { grepLine(File("/proc/stat").readText().replace("btime ", "btime:"), "btime") },
            nm("open/read /proc/stat") {
                val raw = NativeBridge.readFileOrReason("/proc/stat", 65536)
                if (raw in REASON_TOKENS) raw
                else grepLine(raw?.replace("btime ", "btime:"), "btime")
            },
            smr("awk '/^btime/{print $2}' /proc/stat 2>/dev/null || cat /proc/stat", "btime"),
        )))
    }
}
