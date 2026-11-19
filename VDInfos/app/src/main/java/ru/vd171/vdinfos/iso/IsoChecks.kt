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

package ru.vd171.vdinfos.iso

import android.system.Os
import android.system.OsConstants
import ru.vd171.vdinfos.probe.NativeBridge
import java.io.File

object IsoChecks {

    private val FD_NEEDLES = listOf(
        "/data/adb", "magisk", "kernelsu", "/ksu", "zygisk", "rezygisk", "lsposed", "/hma",
    )

    fun tracerPid(): String = runCatching {
        File("/proc/self/status").readLines()
            .firstOrNull { it.startsWith("TracerPid:") }
            ?.substringAfter(':')?.trim() ?: "?"
    }.getOrDefault("?")

    fun fdScan(): String {
        val hits = (File("/proc/self/fd").listFiles() ?: emptyArray()).mapNotNull { fd ->
            runCatching { android.system.Os.readlink(fd.path) }.getOrNull()
        }.filter { t -> FD_NEEDLES.any { t.contains(it) } }.distinct()
        return if (hits.isEmpty()) "clean" else hits.joinToString(",")
    }

    fun seLinuxEnforce(): String = runCatching {
        File("/sys/fs/selinux/enforce").readText().trim()
    }.getOrDefault("?")

    private fun rawContext(): String = runCatching {
        File("/proc/self/attr/current").readText().trim().trimEnd('\u0000').trim()
    }.getOrDefault("?")

    fun seLinuxContext(): String = rawContext()

    fun seLinuxContextBad(): String {
        val ctx = rawContext()
        if (ctx == "?") return "unknown"
        val type = ctx.split(':').getOrNull(2) ?: return "ok"
        return if (type.contains("app")) "ok" else "suspicious:$type"
    }

    private fun jvmProp(key: String): String? = runCatching {
        val c = Class.forName("android.os.SystemProperties")
        (c.getMethod("get", String::class.java).invoke(null, key) as? String)?.takeIf { it.isNotEmpty() }
    }.getOrNull()

    fun propDual(keys: List<String>): String {
        val diverge = keys.filter { k ->
            val native = runCatching { NativeBridge.sysprop(k) }.getOrNull()?.takeIf { it.isNotEmpty() }
            native != jvmProp(k)
        }
        return if (diverge.isEmpty()) "clean" else diverge.joinToString(",")
    }

    fun suScan(paths: List<String>): String {
        val out = ArrayList<String>()
        for (p in paths.distinct()) {
            val methods = ArrayList<String>()
            if (runCatching { File(p).exists() }.getOrDefault(false)) methods.add("file")
            if (runCatching { Os.access(p, OsConstants.F_OK) }.getOrDefault(false)) methods.add("access")
            if (runCatching { Os.stat(p); true }.getOrDefault(false)) methods.add("stat")
            if (runCatching { NativeBridge.exists(p) }.getOrDefault(false)) methods.add("naccess")
            if (methods.isNotEmpty()) out.add("$p(${methods.joinToString("/")})")
        }
        return if (out.isEmpty()) "clean" else out.joinToString(",")
    }

    private val MAPS_HEADER = Regex("^([0-9a-f]+)-[0-9a-f]+\\s+(\\S{4})\\s+\\S+\\s+\\S+\\s+\\S+\\s*(.*)$")

    fun smapsRwx(): String {
        val file = File("/proc/self/smaps")
        if (!runCatching { file.canRead() }.getOrDefault(false)) return "unavailable"
        val hits = ArrayList<String>()
        val read = runCatching {
            file.forEachLine { line ->
                val m = MAPS_HEADER.matchEntire(line) ?: return@forEachLine
                val perms = m.groupValues[2]
                val path = m.groupValues[3].trim()
                if (perms.length >= 3 && perms[1] == 'w' && perms[2] == 'x' && path.isEmpty()) hits.add(m.groupValues[1])
            }
        }.isSuccess
        if (!read && hits.isEmpty()) return "unavailable"
        return if (hits.isEmpty()) "clean" else "rwx:" + hits.joinToString(",")
    }

    private val MNT_NEEDLES = listOf("magisk", "kernelsu", "/ksu", "worker", "/data/adb")

    fun mountMarkers(): String {
        val file = File("/proc/self/mountinfo")
        if (!runCatching { file.canRead() }.getOrDefault(false)) return "unavailable"
        val found = LinkedHashSet<String>()
        val read = runCatching {
            file.forEachLine { line ->
                val low = line.lowercase()
                for (n in MNT_NEEDLES) if (low.contains(n)) found.add(n.trim('/'))
            }
        }.isSuccess
        if (!read && found.isEmpty()) return "unavailable"
        return if (found.isEmpty()) "clean" else found.joinToString(",")
    }

    fun anonElf(): String = runCatching {
        val a = NativeBridge.anonExecElf() ?: return "na"
        "elf${a.elfAnonCount},exec${a.anonExecCount}"
    }.getOrDefault("na")

    fun mapsExec(): String = runCatching {
        NativeBridge.mapsDeletedExec()?.let { "${it.delCount}/${it.outCount}" } ?: "na"
    }.getOrDefault("na")

    fun extras(suPaths: List<String>, propKeys: List<String>): String =
        "tracerpid=${tracerPid()}|fd=${fdScan()}|seenforce=${seLinuxEnforce()}|sectx=${seLinuxContext()}" +
            "|sectxbad=${seLinuxContextBad()}|su=${suScan(suPaths)}|smaprwx=${smapsRwx()}|propnj=${propDual(propKeys)}" +
            "|mnt=${mountMarkers()}|anonelf=${anonElf()}|mapsexec=${mapsExec()}"

    fun parse(blob: String?): Map<String, String> =
        blob?.split('|')?.mapNotNull {
            val i = it.indexOf('=')
            if (i <= 0) null else it.substring(0, i) to it.substring(i + 1)
        }?.toMap() ?: emptyMap()
}
