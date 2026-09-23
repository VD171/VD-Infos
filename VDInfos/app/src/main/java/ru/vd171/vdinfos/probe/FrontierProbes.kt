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

object FrontierProbes {

    private val PID = android.os.Process.myPid()

    private fun peerDigest(text: String?): String? {
        if (text == null) return null
        val ids = Regex("""\b(?:shared|master):(\d+)""")
            .findAll(text).map { it.groupValues[1].toInt() }.toSortedSet()
        if (ids.isEmpty()) return "peers=0"
        val span = ids.last() - ids.first() + 1
        return "peers=${ids.size} span=$span"
    }

    private fun anonExecDigest(text: String?): String? {
        if (text == null) return null
        var n = 0
        for (line in text.lineSequence()) {
            val cols = line.split(Regex("\\s+"), limit = 6)
            if (cols.size < 2) continue
            val perms = cols[1]
            if (perms.length < 3 || perms[2] != 'x') continue
            val path = cols.getOrNull(5)?.trim().orEmpty()
            if (path.isEmpty() || path.startsWith("[anon:") || path == "(deleted)" ||
                path.startsWith("memfd:") || path.endsWith("(deleted)") ||
                path.startsWith("/dev/ashmem") || path.startsWith("/data/")
            ) n++
        }
        return "anon_x=$n"
    }

    private fun kernelMarkers(text: String?): String? {
        if (text == null) return null
        val low = text.lowercase()
        val hits = buildList {
            if (Regex("""-dirty\b""").containsMatchIn(low)) add("dirty")
            if (low.contains("susfs")) add("susfs")
            if (low.contains("kernelsu") || low.contains("-ksu")) add("ksu")
        }.sorted()
        return if (hits.isEmpty()) "clean" else hits.joinToString(",")
    }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        add(probe("net:sock_diag", ctx.getString(R.string.t_net_sock_diag), Category.NETWORK, listOf(
            nm("NETLINK_SOCK_DIAG create+dump") { NativeBridge.sockDiag() },
        ), note = ctx.getString(R.string.n_net_sock_diag)))

        add(probe("mount:peer_gap", ctx.getString(R.string.t_mount_peer_gap), Category.INTEGRITY, listOf(
            jm("peer-groups /proc/self/mountinfo") {
                peerDigest(runCatching { File("/proc/self/mountinfo").readText() }.getOrNull())
            },
            nm("peer-groups (native read)") {
                peerDigest(NativeBridge.readFile("/proc/self/mountinfo", 1 shl 20))
            },
            smr("grep -oE '(shared|master):[0-9]+' /proc/$PID/mountinfo | sort -u | " +
                "wc -l | awk '{print \"peers=\" \$1}'",
                "peer-groups (sh)", compare = false),
        ), note = ctx.getString(R.string.n_mount_peer_gap)))

        add(probe("mount:fdinfo_mnt", ctx.getString(R.string.t_mount_fdinfo_mnt), Category.INTEGRITY, listOf(
            jm("fdinfo mnt_id vs mountinfo") { fdinfoOrphan() },
        ), note = ctx.getString(R.string.n_mount_fdinfo_mnt)))

        add(probe("proc:readproc_gid", ctx.getString(R.string.t_proc_readproc_gid), Category.PROCESS, listOf(
            jm("Groups line /proc/self/status") { gid3009(runCatching { File("/proc/self/status").readText() }.getOrNull()) },
            nm("Groups line (native read)") { gid3009(NativeBridge.readFileOrReason("/proc/self/status")) },
            smr("awk -F: '/^Groups:/{print (\$2 ~ /(^| )3009( |\$)/)?\"present\":\"missing\"}' /proc/$PID/status",
                "Groups 3009 (sh)", compare = false),
        ), note = ctx.getString(R.string.n_proc_readproc_gid)))

        add(probe("proc:fd_graph", ctx.getString(R.string.t_proc_fd_graph), Category.PROCESS, listOf(
            jm("census /proc/self/fd", compare = false) { fdCensus() },
        )))

        add(probe("mem:anon_exec", ctx.getString(R.string.t_mem_anon_exec), Category.INTEGRITY, listOf(
            jm("scan /proc/self/maps") {
                anonExecDigest(runCatching { File("/proc/self/maps").readText() }.getOrNull())
            },
            nm("scan maps (native read)") {
                anonExecDigest(NativeBridge.readFile("/proc/self/maps", 1 shl 20))
            },
            smr("""awk '${'$'}2 ~ /..x./ { p=""; for(i=6;i<=NF;i++)p=p (i>6?" ":"") ${'$'}i; """ +
                """if(p=="" || p ~ /^\[anon:/ || p ~ /\(deleted\)/ || p ~ /^memfd:/ || """ +
                """p ~ /^\/dev\/ashmem/ || p ~ /^\/data\//) n++ } END{print "anon_x=" n+0}' /proc/$PID/maps""",
                "scan maps (sh)", compare = false),
        ), note = ctx.getString(R.string.n_mem_anon_exec)))

        add(probe("kernel:selfbuild", ctx.getString(R.string.t_kernel_selfbuild), Category.SYSTEM, listOf(
            jm("markers /proc/version") { kernelMarkers(runCatching { File("/proc/version").readText() }.getOrNull()) },
            nm("markers uname release+version") {
                NativeBridge.uname()?.let { kernelMarkers(it.release + " " + it.version) }
            },
            smr("uname -a", "uname -a (raw)", compare = false),
        ), note = ctx.getString(R.string.n_kernel_selfbuild)))

        add(probe("tee:soter", ctx.getString(R.string.t_tee_soter), Category.SECURITY, listOf(
            smr("if [ -e /system/bin/soterserver ] || service list 2>/dev/null | grep -qi soter; " +
                "then echo present; else echo absent; fi", "soter program", compare = false),
        ), note = ctx.getString(R.string.n_tee_soter)))

        add(probe("proc:cgroup_format", ctx.getString(R.string.t_proc_cgroup_format), Category.INTEGRITY, listOf(
            jm("validate /proc/self/cgroup") { cgroupFormat(runCatching { File("/proc/self/cgroup").readText() }.getOrNull()) },
        ), note = ctx.getString(R.string.n_proc_cgroup_format)))
    }

    private fun gid3009(status: String?): String? {
        val line = status?.lineSequence()?.firstOrNull { it.startsWith("Groups:") } ?: return null
        val groups = line.substringAfter(":").trim().split(Regex("\\s+"))
        return if ("3009" in groups) Sentinels.PRESENT else "missing"
    }

    private fun fdinfoOrphan(): String? {
        val mounts = runCatching {
            File("/proc/self/mountinfo").readLines()
                .mapNotNull { it.substringBefore(' ').toIntOrNull() }.toHashSet()
        }.getOrNull() ?: return Sentinels.EACCES
        val fds = File("/proc/self/fd").listFiles() ?: return Sentinels.EACCES
        for (fd in fds) {
            val info = runCatching { File("/proc/self/fdinfo/${fd.name}").readText() }.getOrNull() ?: continue
            val mnt = info.lineSequence().firstOrNull { it.startsWith("mnt_id:") }
                ?.substringAfter(":")?.trim()?.toIntOrNull() ?: continue
            if (mnt !in mounts) return "orphan:$mnt"
        }
        return "consistent"
    }

    private fun fdCensus(): String? {
        val fds = File("/proc/self/fd").listFiles() ?: return Sentinels.EACCES
        var sock = 0; var pipe = 0; var anon = 0; var reg = 0
        for (fd in fds) {
            val t = runCatching { android.system.Os.readlink(fd.absolutePath) }.getOrNull() ?: continue
            when {
                t.startsWith("socket:") -> sock++
                t.startsWith("pipe:") -> pipe++
                t.startsWith("anon_inode:") -> anon++
                else -> reg++
            }
        }
        return "n=${fds.size} sock=$sock pipe=$pipe anon=$anon file=$reg"
    }

    private val CGROUP_CANON = Regex("""^0::/(apps/)?uid_\d+/pid_\d+$""")

    private fun cgroupFormat(text: String?): String? {
        val t = text?.trim() ?: return null
        val lines = t.lines().filter { it.isNotBlank() }
        val unified = lines.singleOrNull { it.startsWith("0::") } ?: return "cgroup-v1"
        return if (CGROUP_CANON.matches(unified)) "canonical" else "nonstandard"
    }
}
