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

object PropVariantProbes {

    private fun keyOf(base: String, partition: String, field: String): String = when {
        base == "product" && partition == "(base)" -> "ro.product.$field"
        base == "product" -> "ro.product.$partition.$field"
        partition == "(base)" -> "ro.build.$field"
        else -> "ro.$partition.build.$field"
    }

    private fun patternOf(base: String, field: String): String =
        if (base == "product") "ro.product.*.$field" else "ro.*.build.$field"

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        val parts = AssetData.partitions(ctx)
        val lineage = HashMap<String, String>()
        parts.forEach { lineage[it.name] = it.lineage }
        lineage["(base)"] = "base"
        val slots = listOf("(base)") + parts.map { it.name }

        for (fam in AssetData.propFamilies(ctx)) {
            val present = slots.mapNotNull { slot ->
                val key = keyOf(fam.base, slot, fam.field)
                SystemProps.get(key)?.takeIf { it.isNotEmpty() }?.let { slot to it }
            }
            if (present.size < 2) continue

            val pattern = patternOf(fam.base, fam.field)
            add(
                probe(
                    "prop:variants:$pattern", ctx.getString(R.string.t_prop_variants, pattern), Category.BUILD,
                    listOf(
                        jm("consistent within scope (expected)") { "ok" },
                        jm(if (fam.lineageScoped) "each lineage internally consistent" else "all partitions equal") {
                            val bad = if (fam.lineageScoped) {
                                present.groupBy { lineage[it.first] ?: it.first }
                                    .filterValues { g -> g.map { it.second }.distinct().size > 1 }
                                    .map { (lin, g) -> "$lin{${g.joinToString(",") { it.first }}}" }
                            } else {
                                if (present.map { it.second }.distinct().size > 1)
                                    listOf(present.joinToString(",") { it.first }) else emptyList()
                            }
                            if (bad.isEmpty()) "ok" else "SPLIT ${bad.joinToString(" ")}"
                        },
                        jm("partition matrix", compare = false) {
                            present.joinToString("\n") { (slot, v) -> "$slot=${v}" }
                        },
                    ),
                    note = ctx.getString(R.string.note_prop_variants),
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
                        hiddenFromGetprop()?.size?.toString() ?: Sentinels.NONE
                    },
                    jm("native enum count", compare = false) {
                        NativeBridge.propList()?.size?.toString() ?: Sentinels.NONE
                    },
                    jm("hidden keys", compare = false) {
                        hiddenFromGetprop()?.takeIf { it.isNotEmpty() }?.joinToString("\n") ?: Sentinels.NONE
                    },
                ),
                note = ctx.getString(R.string.note_prop_enum),
            ),
        )
    }
}
