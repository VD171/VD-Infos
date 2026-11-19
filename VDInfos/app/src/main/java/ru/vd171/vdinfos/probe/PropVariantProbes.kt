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

import android.content.Context
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask

object PropVariantProbes {

    private fun keyOf(base: String, partition: String, field: String): String = when {
        base == "product" && partition == "(base)" -> "ro.product.$field"
        base == "product" -> "ro.product.$partition.$field"
        partition == "(base)" -> "ro.build.$field"
        else -> "ro.$partition.build.$field"
    }

    private fun patternOf(base: String, field: String): String =
        if (base == "product") "ro.product.*.$field" else "ro.*.build.$field"

    private fun genericMatchers(ctx: Context): List<(String) -> Boolean> =
        AssetData.packages(ctx, "prop_generic_identity").map { raw ->
            val p = raw.trim().lowercase()
            when {
                p.length > 1 && p.startsWith("*") && p.endsWith("*") -> { s -> s.contains(p.trim('*')) }
                p.startsWith("*") -> { s -> s.endsWith(p.trimStart('*')) }
                p.endsWith("*") -> { s -> s.startsWith(p.trimEnd('*')) }
                else -> { s -> s == p }
            }
        }

    private fun isGsiIdentity(value: String, matchers: List<(String) -> Boolean>): Boolean {
        val s = value.trim().lowercase()
        return matchers.any { it(s) }
    }

    private fun identityGroups(ctx: Context): Map<String, String> {
        val map = HashMap<String, String>()
        for (line in AssetData.packages(ctx, "prop_identity_groups")) {
            val tokens = line.split(',').map { it.trim().lowercase() }.filter { it.isNotEmpty() }
            val canon = tokens.firstOrNull() ?: continue
            tokens.forEach { map[it] = canon }
        }
        return map
    }

    private fun regionSuffixes(ctx: Context): Set<String> =
        AssetData.packages(ctx, "prop_region_suffixes").map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()

    private val regionRe = Regex("^(.+)[-_]([a-z0-9]{1,6})$")

    private fun stripRegion(s: String, suffixes: Set<String>): String {
        val m = regionRe.matchEntire(s) ?: return s
        val base = m.groupValues[1]
        return if (m.groupValues[2] in suffixes && base.length >= 2) base else s
    }

    private fun canonIdentity(value: String, groups: Map<String, String>, suffixes: Set<String>): String {
        val s = stripRegion(value.trim().lowercase(), suffixes)
        return groups[s] ?: s
    }

    private fun canonFingerprint(value: String, groups: Map<String, String>, suffixes: Set<String>): String {
        val segs = value.trim().split('/', ':').toMutableList()
        if (segs.size < 7) return value.trim().lowercase()
        segs[0] = canonIdentity(segs[0], groups, suffixes)
        segs[1] = canonIdentity(segs[1], groups, suffixes)
        segs[2] = canonIdentity(segs[2], groups, suffixes)
        segs[5] = ""
        return segs.joinToString("|") { it.lowercase() }
    }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        val parts = AssetData.partitions(ctx)
        val lineage = HashMap<String, String>()
        parts.forEach { lineage[it.name] = it.lineage }
        lineage["(base)"] = "base"
        val slots = listOf("(base)") + parts.map { it.name }
        val groups = identityGroups(ctx)
        val suffixes = regionSuffixes(ctx)
        val generic = genericMatchers(ctx)
        val selfCodename = slots.mapNotNull { slot ->
            SystemProps.get(keyOf("product", slot, "device"))
                ?.takeIf { it.isNotEmpty() && !isGsiIdentity(it, generic) }
                ?.let { canonIdentity(it, groups, suffixes) }
        }.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
        val codenameLeakFields = setOf("brand", "manufacturer", "model", "marketname")

        for (fam in AssetData.propFamilies(ctx)) {
            val present = slots.mapNotNull { slot ->
                val key = keyOf(fam.base, slot, fam.field)
                SystemProps.get(key)?.takeIf { it.isNotEmpty() }?.let { slot to it }
            }
            if (present.size < 2) continue

            val pattern = patternOf(fam.base, fam.field)
            val anchorVotes = fam.base == "product" && fam.field == "device"
            add(
                probe(
                    "prop:variants:$pattern", ctx.getString(R.string.t_prop_variants, pattern), Category.BUILD,
                    listOf(
                        jm("consistent within scope (expected)", compare = anchorVotes) { "ok" },
                        jm(if (fam.lineageScoped) "each lineage internally consistent" else "all partitions equal", compare = anchorVotes) {
                            val leak = fam.base == "product" && fam.field in codenameLeakFields
                            fun canonOf(v: String): String =
                                if (fam.field == "fingerprint") canonFingerprint(v, groups, suffixes)
                                else canonIdentity(v, groups, suffixes)
                            val real = present
                                .filterNot { isGsiIdentity(it.second, generic) }
                                .filterNot { leak && selfCodename != null && canonIdentity(it.second, groups, suffixes) == selfCodename }
                            val bad = if (fam.lineageScoped) {
                                real.groupBy { lineage[it.first] ?: it.first }
                                    .filterValues { g -> g.map { canonOf(it.second) }.distinct().size > 1 }
                                    .map { (lin, g) -> "$lin{${g.joinToString(",") { it.first }}}" }
                            } else {
                                if (real.map { canonOf(it.second) }.distinct().size > 1)
                                    listOf(real.joinToString(",") { it.first }) else emptyList()
                            }
                            if (bad.isEmpty()) "ok" else "SPLIT ${bad.joinToString(" ")}"
                        },
                        jm("partition matrix", compare = false) {
                            present.joinToString("\n") { (slot, v) -> "$slot=${v}" }
                        },
                    ),
                ),
            )
        }

        fun hiddenFromGetprop(): List<String>? {
            val native = NativeBridge.propList() ?: return null
            val shell = Exec.run("getprop", cap = 400000) ?: return null
            val shellKeys = Regex("^\\[(.*?)\\]:", RegexOption.MULTILINE)
                .findAll(shell).map { it.groupValues[1] }.toHashSet()
            if (shellKeys.isEmpty()) return null
            return native.keys.filterNot { it in shellKeys }.sorted()
        }
        add(
            probe(
                "prop:enum_vs_getprop", ctx.getString(R.string.t_prop_enum), Category.INTEGRITY,
                listOf(
                    jm("no prop hidden from getprop (expected)") { "0" },
                    jm("props in native enum but absent from getprop") {
                        hiddenFromGetprop()?.size?.toString()
                    },
                    jm("native enum count", compare = false) {
                        NativeBridge.propList()?.size?.toString() ?: Sentinels.NONE
                    },
                    jm("hidden keys", compare = false) {
                        hiddenFromGetprop()?.takeIf { it.isNotEmpty() }?.joinToString("\n") ?: Sentinels.NONE
                    },
                ),
            ),
        )
    }
}
