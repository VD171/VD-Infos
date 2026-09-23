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
import ru.vd171.vdinfos.core.model.Category

object AssetData {

    private val cache = HashMap<String, List<String>>()

    private fun lines(ctx: Context, path: String): List<String> = synchronized(cache) {
        cache.getOrPut(path) {
            runCatching {
                ctx.assets.open(path).bufferedReader().use { r ->
                    r.readLines().map { it.substringBefore('#').trim() }.filter { it.isNotEmpty() }
                }
            }.getOrDefault(emptyList())
        }
    }

    fun packages(ctx: Context, name: String): List<String> = lines(ctx, "data/$name.txt")

    fun spoofKeys(ctx: Context): List<Pair<String, String>> = lines(ctx, "data/spoof_keys.txt").map {
        val name = it.substringBefore(':').trim()
        val type = it.substringAfter(':', "STR").trim().uppercase().ifEmpty { "STR" }
        name to type
    }

    data class PropEntry(val category: Category, val key: String, val sensitive: Boolean)

    fun propEntries(ctx: Context): List<PropEntry> = lines(ctx, "data/props.txt").mapNotNull { line ->
        val parts = line.split(Regex("\\s+"))
        if (parts.size < 2) return@mapNotNull null
        val category = runCatching { Category.valueOf(parts[0]) }.getOrNull() ?: return@mapNotNull null
        PropEntry(category, parts[1], parts.size >= 3 && parts[2].equals("S", ignoreCase = true))
    }

    fun policyTypes(ctx: Context): List<Pair<String, String>> =
        lines(ctx, "data/selinux_policy_types.txt").mapNotNull {
            val name = it.substringBefore(':').trim()
            val context = it.substringAfter(':', "").trim()
            if (name.isEmpty() || context.isEmpty()) null else name to context
        }

    fun avQuestions(ctx: Context): List<Array<String>> =
        lines(ctx, "data/selinux_av_questions.txt").mapNotNull {
            val parts = it.split(Regex("\\s+")).filter { p -> p.isNotEmpty() }
            if (parts.size >= 4) arrayOf(parts[0], parts[1], parts[2], parts[3]) else null
        }

    fun invisibleRanges(ctx: Context): IntArray {
        val out = ArrayList<Int>()
        for (line in lines(ctx, "data/invisible_char_ranges.txt")) {
            val p = line.split(Regex("\\s+"))
            val lo = p.getOrNull(0)?.toIntOrNull(16) ?: continue
            val hi = p.getOrNull(1)?.toIntOrNull(16) ?: lo
            out.add(lo); out.add(hi)
        }
        return out.toIntArray()
    }

    fun buildFieldProps(ctx: Context): Map<String, List<String>> {
        val canonical = LinkedHashMap<String, String>()
        val mirrors = LinkedHashMap<String, MutableList<String>>()
        for (line in lines(ctx, "data/props.txt")) {
            val parts = line.split(Regex("\\s+"))
            if (parts.size < 2) continue
            val tag = parts.drop(2).firstOrNull { it.startsWith("@") } ?: continue
            val field = tag.removePrefix("@").removeSuffix("*")
            if (field.isEmpty()) continue
            if (tag.endsWith("*")) canonical[field] = parts[1]
            else mirrors.getOrPut(field) { mutableListOf() }.add(parts[1])
        }
        val out = LinkedHashMap<String, List<String>>()
        for (field in canonical.keys + mirrors.keys) {
            val list = ArrayList<String>()
            canonical[field]?.let { list.add(it) }
            mirrors[field]?.let { list.addAll(it) }
            out[field] = list
        }
        return out
    }
}
