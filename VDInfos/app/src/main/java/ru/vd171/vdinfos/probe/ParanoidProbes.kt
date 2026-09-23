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

object ParanoidProbes {

    private val PID = android.os.Process.myPid()

    private fun linkItem(id: String, title: String, path: String, cat: Category) =
        probe(id, title, cat, listOf(
            jm("Os.readlink $path") {
                runCatching { android.system.Os.readlink(path) }.getOrElse { Sentinels.EACCES }
            },
            nm("readlink(2) $path") { NativeBridge.readlink(path) ?: Sentinels.EACCES },
            smr("readlink ${path.replace("/proc/self/", "/proc/$PID/")}",
                "readlink ${path.replace("/proc/self/", "/proc/<pid>/")}", compare = false),
        ))

    private fun statusItem(id: String, title: String, campo: String, cat: Category, compare: Boolean = true) =
        probe(id, title, cat, listOf(
            jm("read /proc/self/status $campo", compare) {
                File("/proc/self/status").readLines()
                    .firstOrNull { it.startsWith("$campo:") }?.substringAfter(":")?.trim()
            },
            nm("open/read /proc/self/status $campo", compare) {
                NativeBridge.readFileOrReason("/proc/self/status")
                    ?.lineSequence()?.firstOrNull { it.startsWith("$campo:") }
                    ?.substringAfter(":")?.trim()
            },
            smr("awk -F: '/^$campo:/{gsub(/^[ \\t]+/,\"\",\$2); print \$2}' /proc/$PID/status",
                "$campo (/proc/<pid>/status)", compare = false),
        ))

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        for ((ns, res) in listOf(
            "mnt" to R.string.t_ns_mnt, "net" to R.string.t_ns_net,
            "pid" to R.string.t_ns_pid, "uts" to R.string.t_ns_uts,
            "ipc" to R.string.t_ns_ipc, "user" to R.string.t_ns_user,
            "cgroup" to R.string.t_ns_cgroup, "time" to R.string.t_ns_time,
        )) {
            add(linkItem("ns:$ns", ctx.getString(res), "/proc/self/ns/$ns", Category.INTEGRITY))
        }

        add(linkItem("proc:exe", ctx.getString(R.string.t_proc_exe), "/proc/self/exe", Category.PROCESS))
        add(linkItem("proc:cwd", ctx.getString(R.string.t_proc_cwd), "/proc/self/cwd", Category.PROCESS))
        add(linkItem("proc:root", ctx.getString(R.string.t_proc_root), "/proc/self/root", Category.INTEGRITY))

        add(statusItem("proc:seccomp", ctx.getString(R.string.t_seccomp), "Seccomp", Category.SECURITY))
        add(statusItem("proc:nonewprivs", ctx.getString(R.string.t_no_new_privs), "NoNewPrivs", Category.SECURITY))
        add(statusItem("proc:capeff", ctx.getString(R.string.t_cap_effective), "CapEff", Category.SECURITY))
        add(statusItem("proc:capbnd", ctx.getString(R.string.t_cap_bounding), "CapBnd", Category.SECURITY))
        add(statusItem("proc:uid_line", ctx.getString(R.string.t_status_uid), "Uid", Category.PROCESS))
        add(statusItem("proc:gid_line", ctx.getString(R.string.t_status_gid), "Gid", Category.PROCESS))
        add(statusItem("proc:groups", ctx.getString(R.string.t_status_groups), "Groups", Category.PROCESS))
        add(statusItem("proc:threads", ctx.getString(R.string.t_status_threads), "Threads", Category.PROCESS, compare = false))

        add(probe("selinux:attr_current", ctx.getString(R.string.t_selinux_attr), Category.SECURITY, listOf(
            jm("read /proc/self/attr/current") {
                runCatching { File("/proc/self/attr/current").readText().trim().trimEnd('\u0000') }
                    .getOrElse { Sentinels.EACCES }
            },
            nm("open/read /proc/self/attr/current") {
                NativeBridge.readFileOrReason("/proc/self/attr/current")?.trim()?.trimEnd('\u0000')
            },
            smr("cat /proc/$PID/attr/current | tr -d '\\000'", "cat <pid>/attr/current", compare = false),
        )))

        add(probe("proc:cgroup", ctx.getString(R.string.t_cgroup), Category.PROCESS, listOf(
            jm("read /proc/self/cgroup") { File("/proc/self/cgroup").readText().trim() },
            nm("open/read /proc/self/cgroup") { NativeBridge.readFileOrReason("/proc/self/cgroup")?.trim() },
            smr("cat /proc/$PID/cgroup", "cat /proc/<pid>/cgroup", compare = false),
        )))

        add(probe("cpu:arch", ctx.getString(R.string.t_architecture), Category.HARDWARE, listOf(
            jm("System.getProperty(os.arch)") { System.getProperty("os.arch") },
            nm("uname().machine") { NativeBridge.uname()?.machine },
            nm("compiled-in arch", compare = false) { NativeBridge.arch() },
            sm("uname -m", "uname -m"),
        )))
    }
}
