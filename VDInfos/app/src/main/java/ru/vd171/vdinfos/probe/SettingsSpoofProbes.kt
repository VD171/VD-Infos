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
import android.net.Uri
import android.provider.Settings
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask

object SettingsSpoofProbes {

    private val STORES = listOf("global", "secure", "system")

    private val STOCK_BASELINE: List<Triple<String, String, String?>> = listOf(
        Triple("secure", "ota_disable_automatic_update", null),
        Triple("secure", "install_non_market_apps", null),
        Triple("secure", "unknown_sources_default_reversed", null),
        Triple("secure", "verifier_verify_adb_installs", null),
        Triple("secure", "package_verifier_user_consent", null),
        Triple("secure", "adb_enabled", null),
        Triple("secure", "adb_wifi_enabled", null),
        Triple("secure", "development_settings_enabled", null),
        Triple("system", "ota_disable_automatic_update", null),
        Triple("system", "install_non_market_apps", null),
        Triple("system", "unknown_sources_default_reversed", null),
        Triple("system", "verifier_verify_adb_installs", null),
        Triple("system", "package_verifier_user_consent", null),
        Triple("system", "adb_enabled", null),
        Triple("system", "adb_wifi_enabled", null),
        Triple("system", "development_settings_enabled", null),
        Triple("global", "ota_disable_automatic_update", null),
        Triple("global", "install_non_market_apps", null),
        Triple("global", "unknown_sources_default_reversed", null),
        Triple("global", "verifier_verify_adb_installs", null),
        Triple("global", "package_verifier_user_consent", null),
        Triple("global", "adb_enabled", "0"),
        Triple("global", "adb_wifi_enabled", "0"),
        Triple("global", "development_settings_enabled", "0"),
    )

    private fun checkBaseline(c: Context, read: (Context, String, String) -> String?): String {
        val bad = STOCK_BASELINE.mapNotNull { (ns, k, want) ->
            val got = read(c, ns, k)?.takeIf { it.isNotEmpty() }
            if (got == want) null else "$ns.$k=${got ?: "null"}(want=${want ?: "null"})"
        }
        return if (bad.isEmpty()) "baseline-ok" else "DIVERGE: " + bad.joinToString(" | ")
    }

    private fun uriOf(store: String): Uri = when (store) {
        "global" -> Settings.Global.CONTENT_URI
        "secure" -> Settings.Secure.CONTENT_URI
        else -> Settings.System.CONTENT_URI
    }

    @Suppress("DEPRECATION")
    private fun pGetString(c: Context, store: String, key: String): String? = runCatching {
        when (store) {
            "global" -> Settings.Global.getString(c.contentResolver, key)
            "secure" -> Settings.Secure.getString(c.contentResolver, key)
            else -> Settings.System.getString(c.contentResolver, key)
        }
    }.getOrNull()

    @Suppress("DEPRECATION")
    private fun pGetInt(c: Context, store: String, key: String): String? = runCatching {
        when (store) {
            "global" -> Settings.Global.getInt(c.contentResolver, key)
            "secure" -> Settings.Secure.getInt(c.contentResolver, key)
            else -> Settings.System.getInt(c.contentResolver, key)
        }.toString()
    }.getOrNull()

    private fun pAppended(c: Context, store: String, key: String): String? = runCatching {
        c.contentResolver.query(Uri.withAppendedPath(uriOf(store), key), arrayOf("name", "value"), null, null, null)
            ?.use { if (it.moveToFirst()) it.getString(it.getColumnIndexOrThrow("value")) else null }
    }.getOrNull()

    private fun pSelection(c: Context, store: String, key: String): String? = runCatching {
        c.contentResolver.query(Uri.parse("content://settings/$store"), arrayOf("name", "value"), "name=?", arrayOf(key), null)
            ?.use { if (it.moveToFirst()) it.getString(it.getColumnIndexOrThrow("value")) else null }
    }.getOrNull()

    private fun pBulk(c: Context, store: String, key: String): String? = runCatching {
        c.contentResolver.query(uriOf(store), arrayOf("name", "value"), null, null, null)?.use { cur ->
            val ni = cur.getColumnIndex("name"); val vi = cur.getColumnIndex("value")
            var out: String? = null
            while (cur.moveToNext()) if (ni >= 0 && cur.getString(ni) == key) { out = if (vi >= 0) cur.getString(vi) else null; break }
            out
        }
    }.getOrNull()

    private fun pCall(c: Context, store: String, key: String): String? = runCatching {
        c.contentResolver.call(uriOf(store), "GET_$store", key, null)?.getString("value")
    }.getOrNull()

    private fun sweep(c: Context, key: String, read: (Context, String, String) -> String?): String? {
        val values = STORES.mapNotNull { s -> read(c, s, key)?.takeIf { it.isNotEmpty() } }.distinct()
        val filtered = if (values.any { it != "0" }) values.filter { it != "0" } else values
        return if (filtered.isEmpty()) null else filtered.sorted().joinToString(",")
    }

    private fun bulkMap(c: Context, store: String): Map<String, String>? = runCatching {
        val m = LinkedHashMap<String, String>()
        c.contentResolver.query(uriOf(store), arrayOf("name", "value"), null, null, null)?.use { cur ->
            val ni = cur.getColumnIndex("name"); val vi = cur.getColumnIndex("value")
            while (cur.moveToNext()) {
                val k = if (ni >= 0) cur.getString(ni) else null
                if (k != null) m[k] = if (vi >= 0) (cur.getString(vi) ?: "") else ""
            }
        }
        m
    }.getOrNull()

    private fun sweepAll(c: Context, injectionCandidates: List<String>): String {
        val maps = STORES.associateWith { bulkMap(c, it) }
        val providerKeys = maps.values.filterNotNull().flatMap { it.keys }.toSet()
        val out = ArrayList<String>()
        for (s in STORES) {
            val bulk = maps[s]
            if (bulk == null) { out.add("$s: denied/empty"); continue }
            val diffs = ArrayList<String>()
            for ((k, bv) in bulk) {
                val gs = pGetString(c, s, k) ?: continue
                if (gs != bv) diffs.add("$k{prov=$bv,api=$gs}")
            }
            out.add("$s: " + if (diffs.isEmpty()) "consistent(${bulk.size})"
            else "MISMATCH ${diffs.size}/${bulk.size}: " + diffs.take(15).joinToString(" | "))
        }
        val injected = ArrayList<String>()
        for (k in injectionCandidates) {
            if (k in providerKeys) continue
            val serve = STORES.firstNotNullOfOrNull { s -> pGetString(c, s, k)?.let { "$s=$it" } }
            if (serve != null) injected.add("$k{INJECTED api($serve), prov=absent-in-all}")
        }
        out.add("injection: " + if (injected.isEmpty()) "none" else injected.joinToString(" | "))
        return out.joinToString("\n")
    }

    fun matrixJson(ctx: Context): String {
        val root = org.json.JSONObject()
        root.put("takenAt", System.currentTimeMillis())
        val results = org.json.JSONObject()
        for ((k, _) in AssetData.spoofKeys(ctx)) {
            val m = org.json.JSONObject()
            m.put("getString", sweep(ctx, k, ::pGetString) ?: org.json.JSONObject.NULL)
            m.put("query_appended", sweep(ctx, k, ::pAppended) ?: org.json.JSONObject.NULL)
            m.put("query_selection", sweep(ctx, k, ::pSelection) ?: org.json.JSONObject.NULL)
            m.put("query_bulk", sweep(ctx, k, ::pBulk) ?: org.json.JSONObject.NULL)
            m.put("call", sweep(ctx, k, ::pCall) ?: org.json.JSONObject.NULL)
            results.put(k, m)
        }
        root.put("results", results)
        return root.toString()
    }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        val keys = AssetData.spoofKeys(ctx)

        add(probe("settings:stock_baseline", ctx.getString(R.string.t_settings_stock_baseline), Category.INTEGRITY, listOf(
            jm("app-view (getString, what a RASP reads) vs baseline: INFO; raw DB is root-only", compare = false) { c -> checkBaseline(c, ::pGetString) },
            jm("app-side call GET_<ns> per-key vs baseline: INFO; @hide return code default/null, not raw", compare = false) { c -> checkBaseline(c, ::pCall) },
        ), sensitive = false, solution = ctx.getString(R.string.sol_spoof)))

        add(probe("spoof:sweep", ctx.getString(R.string.t_spoof_sweep), Category.INTEGRITY, listOf(
            jm("provider bulk vs API getString, all keys (3 stores)", compare = false) { c ->
                sweepAll(c, AssetData.spoofKeys(c).map { it.first })
            },
        ), sensitive = false, solution = ctx.getString(R.string.sol_spoof)))
        for ((k, type) in keys) {
            add(
                probe(
                    "spoof:$k", ctx.getString(R.string.t_spoof_consistency, k), Category.INTEGRITY,
                    buildList {
                        add(jm("getString sweep 3-stores") { c -> sweep(c, k, ::pGetString) })
                        if (type == "INT") add(jm("getInt sweep 3-stores") { c -> sweep(c, k, ::pGetInt) })
                        addAll(listOf(
                        jm("query appended-path sweep 3-stores") { c -> sweep(c, k, ::pAppended) },
                        jm("query selection name=? sweep 3-stores") { c -> sweep(c, k, ::pSelection) },
                        jm("query bulk-all sweep 3-stores") { c -> sweep(c, k, ::pBulk) },
                        jm("provider call GET_<store> sweep 3-stores") { c -> sweep(c, k, ::pCall) },
                        sm(
                            "for s in global secure system; do v=\$(settings get \$s $k 2>/dev/null); " +
                                "[ \"\$v\" != null ] && [ -n \"\$v\" ] && echo \$s=\$v; done",
                            "sh: settings get 3-stores $k", compare = false,
                        ),
                    ))
                    },
                    sensitive = false,
                    solution = ctx.getString(R.string.sol_spoof),
                )
            )
        }
    }
}
