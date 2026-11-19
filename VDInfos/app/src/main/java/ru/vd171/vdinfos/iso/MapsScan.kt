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

import java.io.File

object MapsScan {

    private val NEEDLES = listOf(
        "/data/adb", "/debug_ramdisk", "magisk", "kernelsu", "/ksu", "rezygisk",
        "lsposed", "zygisk", "treat_wheel", "/hma", "riru", "apatch",
    )

    data class Result(val modCount: Int, val modPaths: List<String>, val rxAnon: Int, val deleted: Int)

    fun scan(): Result {
        val file = File("/proc/self/maps")
        if (!runCatching { file.canRead() }.getOrDefault(false)) return Result(-1, emptyList(), -1, -1)
        val mods = LinkedHashSet<String>()
        var rxAnon = 0
        var deleted = 0
        val read = runCatching {
            file.forEachLine { line ->
                if (line.isBlank()) return@forEachLine
                val sp = line.split(Regex("\\s+"), limit = 6)
                if (sp.size < 5) return@forEachLine
                val perms = sp[1]
                val path = sp.getOrNull(5)?.trim().orEmpty()
                if (path.isEmpty()) {
                    if (perms.length >= 3 && perms[2] == 'x') rxAnon++
                    return@forEachLine
                }
                if (path.endsWith("(deleted)")) deleted++
                val low = path.lowercase()
                if (NEEDLES.any { low.contains(it) }) mods.add(path)
            }
        }.isSuccess
        if (!read && mods.isEmpty() && rxAnon == 0 && deleted == 0) return Result(-1, emptyList(), -1, -1)
        return Result(mods.size, mods.toList(), rxAnon, deleted)
    }

    fun serialize(r: Result): String =
        "${r.modCount}\t${r.modPaths.joinToString("|")}\t${r.rxAnon}\t${r.deleted}"

    fun deserialize(s: String?): Result? {
        val p = s?.split('\t') ?: return null
        if (p.size < 4) return null
        return Result(
            p[0].toIntOrNull() ?: -1,
            if (p[1].isEmpty()) emptyList() else p[1].split('|'),
            p[2].toIntOrNull() ?: -1,
            p[3].toIntOrNull() ?: -1,
        )
    }
}
