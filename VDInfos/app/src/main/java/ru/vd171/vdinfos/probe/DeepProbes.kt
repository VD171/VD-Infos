/*
 * ======================================================================
 *  VD INFOS  ::  method debugger
 *  Read every device info by every method, then compare.
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
 *  XDA thread     : https://xdaforums.com/t/VD-Infos.4097379/
 *  Telegram       : @VD_Priv8 https://t.me/VD_Priv8
 *  Channel        : @Priv8_VD https://t.me/Priv8_VD
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
            val root = left.getOrNull(3).orEmpty()
            val mountPoint = left.getOrNull(4).orEmpty()
            val fsType = right.getOrNull(0).orEmpty()
            val source = right.getOrNull(1).orEmpty()
            val where = "$root $mountPoint $fsType $source".lowercase()
            if (where.contains("magisk")) found += "magisk"
            if (where.contains("ksu")) found += "ksu"
            if (where.contains("worker")) found += "worker"
            if (where.contains("/data/adb") || where.contains("/adb/modules")) found += "data-adb"
            if (fsType == "overlay" && systemPaths.any { mountPoint.startsWith(it) }) {
                found += "overlay-on-system"
            }
            val rootLow = root.lowercase()
            if ((rootLow.contains("/adb/modules") || rootLow.contains("/data/adb")) && !mountPoint.startsWith("/data")) {
                found += "root-mount-exposure"
            }
        }
        return found.joinToString(", ").ifEmpty { "clean" }
    }

    private fun mountMarkersSh(systemPaths: List<String>): String = """awk '{ p=index($0," - "); if(!p) next; split(substr($0,1,p-1),L," "); split(substr($0,p+3),R," "); rt=tolower(L[4]); w=tolower(L[4] " " L[5] " " R[1] " " R[2]); if (index(w,"magisk")) m["magisk"]=1; if (index(w,"ksu")) m["ksu"]=1; if (index(w,"worker")) m["worker"]=1; if (index(w,"/data/adb") || index(w,"/adb/modules")) m["data-adb"]=1; if (R[1]=="overlay" && L[5] ~ "^/(${systemPaths.joinToString("|") { it.trimStart('/') }})") m["overlay-on-system"]=1; if ((index(rt,"/adb/modules") || index(rt,"/data/adb")) && L[5] !~ "^/data") m["root-mount-exposure"]=1 } END { n=0; for(k in m) a[++n]=k; for(x=1;x<=n;x++) for(y=x+1;y<=n;y++) if(a[y]<a[x]){t=a[x];a[x]=a[y];a[y]=t} s=""; for(x=1;x<=n;x++) s=s (x>1?", ":"") a[x]; print (n? s : "clean") }' /proc/self/mountinfo 2>/dev/null"""

    private fun grepLine(text: String?, key: String): String? =
        text?.lineSequence()?.firstOrNull { it.startsWith(key) }?.substringAfter(":")?.trim()

    private val LOOP_RE = Regex("^loop\\d+$")
    private val MOUNT_LOOP_RE = Regex("/dev/block/(loop\\d+)")
    private val SYS_LOOP_PREFIXES =
        listOf("/apex", "/bootstrap-apex", "/system", "/vendor", "/product", "/system_ext", "/odm")
    private fun ext4Loops(): Set<String>? {
        val listed = runCatching { File("/proc/fs/ext4").listFiles() }.getOrNull() ?: return null
        return listed.map { it.name }.filter { LOOP_RE.matches(it) }.toSortedSet()
    }
    private fun loopMountPoints(): Map<String, String> {
        val m = HashMap<String, String>()
        runCatching {
            File("/proc/self/mountinfo").forEachLine { line ->
                val loop = MOUNT_LOOP_RE.find(line)?.groupValues?.get(1) ?: return@forEachLine
                val mp = line.substringBefore(" - ").split(' ').getOrNull(4) ?: ""
                if (!m.containsKey(loop)) m[loop] = mp
            }
        }
        return m
    }
    private fun loopOrphans(): String {
        val ext4 = ext4Loops() ?: return "clean"
        if (ext4.isEmpty()) return "clean"
        val mp = loopMountPoints()
        val suspicious = ext4.filter { loop ->
            val point = mp[loop]
            point == null || SYS_LOOP_PREFIXES.none { point.startsWith(it) }
        }
        return if (suspicious.isEmpty()) "clean" else "non-system-loop: " + suspicious.joinToString(",")
    }
    private fun loopOrphanDetail(): String {
        val ext4 = ext4Loops() ?: return "cannot read /proc/fs/ext4 (not app-readable here)"
        val mp = loopMountPoints()
        return "ext4-loops[" + ext4.size + "]: " +
            ext4.joinToString(" ") { it + "@" + (mp[it] ?: "UNMOUNTED") }
    }
    private fun loopGrepOptic(): String {
        val ext4 = ext4Loops() ?: return "unavailable"
        if (ext4.isEmpty()) return "clean"
        val androidLoops = sortedSetOf<String>()
        runCatching {
            File("/proc/self/mountinfo").forEachLine { line ->
                if (line.contains("android", ignoreCase = true))
                    MOUNT_LOOP_RE.find(line)?.let { androidLoops += it.groupValues[1] }
            }
        }
        val flagged = ext4.filter { it !in androidLoops }
        return if (flagged.isEmpty()) "clean" else "naive-grep-would-flag: " + flagged.joinToString(",")
    }

    private fun debugRamdiskContent(): String {
        val list = runCatching { File("/debug_ramdisk").listFiles() }.getOrNull()
        return if (list != null && list.isNotEmpty())
            "content: " + list.map { it.name }.sorted().joinToString(",")
        else "clean"
    }
    private fun debugRamdiskDetail(): String {
        val f = File("/debug_ramdisk")
        val exists = runCatching { f.exists() }.getOrDefault(false)
        val list = runCatching { f.listFiles() }.getOrNull()
        val state = when {
            list != null && list.isNotEmpty() -> "present, " + list.size + " entries"
            list != null -> "present, empty"
            exists -> "present, unreadable (denied)"
            else -> "absent"
        }
        return "stat=" + NativeBridge.statPresence("/debug_ramdisk", 0) + " | listing=" + state
    }

    private val STOCK_PATHS = listOf(
        "/debug_ramdisk", "/dev/__properties__", "/metadata", "/postinstall",
        "/apex", "/mnt", "/config", "/linkerconfig"
    )
    private fun stockPresent(p: String): Boolean =
        (0..7).any { m -> NativeBridge.statPresence(p, m).let { it == "present" || it == "present(denied)" } }
    private fun stockPathHidden(): String {
        val hidden = STOCK_PATHS.filterNot { stockPresent(it) }
        return if (hidden.isEmpty()) "present" else "HIDDEN: " + hidden.joinToString(",")
    }
    private fun stockPathDetail(): String =
        STOCK_PATHS.joinToString(" ") { it + "=" + if (stockPresent(it)) "present" else "absent" }

    private val MTK_CHIP = Regex("(?i)\\bMT[0-9]{4}")
    private fun socManufacturer(props: Map<String, String>): String {
        props["ro.soc.manufacturer"]?.takeIf { it.isNotEmpty() }?.let { return it + " " + (props["ro.hardware"] ?: "") }
        if (android.os.Build.VERSION.SDK_INT >= 31) runCatching { android.os.Build.SOC_MANUFACTURER }.getOrNull()?.let { return it }
        return props["ro.hardware"] ?: ""
    }
    private fun romIdentityLeaks(ctx: Context): List<String> {
        val props = NativeBridge.propList() ?: return emptyList()
        val brand = (android.os.Build.BRAND + " " + android.os.Build.MANUFACTURER).lowercase()
        val isXiaomi = listOf("xiaomi", "redmi", "poco").any { brand.contains(it) }
        val soc = socManufacturer(props).lowercase()
        val claimsMtk = soc.contains("mediatek") || soc.contains("mtk")
        val leaks = ArrayList<String>()
        for (pat in AssetData.packages(ctx, "rom_identity_patterns")) {
            val re = runCatching { Regex(pat, RegexOption.IGNORE_CASE) }.getOrNull() ?: continue
            val mtkFam = pat.contains("mtk", true) || pat.contains("mediatek", true)
            val miui = pat.contains("miui", true)
            if ((mtkFam && claimsMtk) || (miui && isXiaomi)) continue
            val hits = props.keys.filter { re.containsMatchIn(it) }.sorted()
            if (hits.isNotEmpty()) leaks.add("prop:" + hits.take(3).joinToString(","))
        }
        if (!claimsMtk) {
            android.os.Build.getRadioVersion()?.let { if (it.contains("MOLY", true)) leaks.add("baseband=MOLY") }
            props.keys.filter { it.startsWith("init.svc.") && (it.contains("mtk", true) || it.contains("mediatek", true)) }
                .sorted().let { if (it.isNotEmpty()) leaks.add("init.svc:" + it.take(2).joinToString(",")) }
            NativeBridge.readFile("/proc/cpuinfo", 1 shl 16)?.let { ci ->
                Regex("Hardware\\s*:\\s*(\\S.*)").find(ci)?.groupValues?.get(1)?.trim()?.let {
                    if (it.contains("mediatek", true) || MTK_CHIP.containsMatchIn(it)) leaks.add("cpuinfo.Hardware=$it")
                }
            }
            NativeBridge.readFile("/proc/device-tree/compatible", 4096)?.let {
                if (it.contains("mediatek", true)) leaks.add("device-tree=mediatek")
            }
        }
        if ((props["ro.bootloader"] ?: "").equals("unknown", true)) leaks.add("ro.bootloader=unknown")
        return leaks
    }
    private fun romIdentityVerdict(ctx: Context): String {
        val leaks = romIdentityLeaks(ctx)
        return if (leaks.isEmpty()) "clean" else "LEAK: " + leaks.joinToString(" | ")
    }
    private fun romIdentityDetail(ctx: Context): String {
        val props = NativeBridge.propList() ?: emptyMap()
        val soc = socManufacturer(props).ifEmpty { "?" }
        return "brand=" + android.os.Build.BRAND + "/" + android.os.Build.MANUFACTURER +
            " soc=" + soc +
            " baseband=" + (android.os.Build.getRadioVersion() ?: "?") +
            " bootloader=" + (props["ro.bootloader"] ?: "?")
    }

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
            jm("expected: no root-module marker or module-over-system bind in mountinfo") { "clean" },
            nm("parse /proc/self/mountinfo") {
                mountMarkers(NativeBridge.readFile("/proc/self/mountinfo", 1 shl 20) ?: return@nm null, AssetData.packages(ctx, "overlay_mount_paths"))
            },
            sm(mountMarkersSh(AssetData.packages(ctx, "overlay_mount_paths")), "parse mountinfo (sh)"),
            nsm(mountMarkersSh(AssetData.packages(ctx, "overlay_mount_paths")), "popen: parse mountinfo (sh)"),
        )))
        add(probe("integrity:loop_orphan", ctx.getString(R.string.t_loop_orphan), Category.INTEGRITY, listOf(
            jm("expected: every global ext4 loop is backed by a system mount in this namespace") { "clean" },
            jm("ext4 loops (/proc/fs/ext4, global) not mounted under a system path") { loopOrphans() },
            jm("detail: each ext4 loop and its mount point", compare = false) { loopOrphanDetail() },
            jm("adversary optic: what a naive path-grep root heuristic would flag (may be its false positive on non-Google apex)", compare = false) { loopGrepOptic() },
        ), solution = ctx.getString(R.string.sol_loop_orphan)))
        add(probe("integrity:debug_ramdisk", ctx.getString(R.string.t_debug_ramdisk), Category.INTEGRITY, listOf(
            jm("expected: /debug_ramdisk holds no staged content (empty or absent)") { "clean" },
            jm("/debug_ramdisk entries") { debugRamdiskContent() },
            jm("detail: presence and listing", compare = false) { debugRamdiskDetail() },
        ), solution = ctx.getString(R.string.sol_debug_ramdisk)))
        add(probe("integrity:stock_path_hidden", ctx.getString(R.string.t_stock_path_hidden), Category.INTEGRITY, listOf(
            jm("expected: standard Android paths present (over-hiding a stock path is itself a tell)") { "present" },
            jm("stock paths read absent (= being hidden)") { stockPathHidden() },
            jm("per-path stat", compare = false) { stockPathDetail() },
        ), solution = ctx.getString(R.string.sol_stock_path_hidden)))
        add(probe("integrity:rom_identity", ctx.getString(R.string.t_rom_identity), Category.INTEGRITY, listOf(
            jm("expected: no ROM/HW identity prop inconsistent with the claimed device") { "clean" },
            jm("ROM/HW identity leaks (prop/baseband/init.svc/cpuinfo vs claimed brand/SOC)") { c -> romIdentityVerdict(c) },
            jm("detail: claimed identity", compare = false) { c -> romIdentityDetail(c) },
        ), solution = ctx.getString(R.string.sol_rom_identity)))

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
