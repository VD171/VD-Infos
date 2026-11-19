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
import android.os.Build
import org.json.JSONObject
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask

object PixelCorpusProbes {

    private const val DIR = "data/pixel"
    private const val MAX_TELLS = 16

    @Volatile private var indexCache: JSONObject? = null

    private fun index(ctx: Context): JSONObject? {
        indexCache?.let { return it }
        return runCatching {
            ctx.assets.open("$DIR/_index.json").bufferedReader().use { JSONObject(it.readText()) }
        }.getOrNull()?.optJSONObject("devices")?.also { indexCache = it }
    }

    private fun deviceBlob(ctx: Context, codename: String): JSONObject? = runCatching {
        ctx.assets.open("$DIR/$codename.json").bufferedReader().use { JSONObject(it.readText()) }
    }.getOrNull()

    private fun firstOf(props: Map<String, String>, keys: List<String>): String? =
        keys.firstNotNullOfOrNull { props[it]?.takeIf { v -> v.isNotEmpty() } }

    private fun deviceSignals(props: Map<String, String>): Map<String, String?> = mapOf(
        "brand" to firstOf(props, listOf("ro.product.brand", "ro.product.product.brand", "ro.product.vendor.brand")),
        "manufacturer" to firstOf(props, listOf("ro.product.manufacturer", "ro.product.product.manufacturer", "ro.product.vendor.manufacturer")),
        "model" to firstOf(props, listOf("ro.product.model", "ro.product.product.model", "ro.product.vendor.model")),
        "device" to firstOf(props, listOf("ro.product.device", "ro.product.product.device", "ro.product.vendor.device")),
        "board" to props["ro.product.board"],
        "soc_manufacturer" to props["ro.soc.manufacturer"],
        "soc_model" to props["ro.soc.model"],
        "first_api_level" to firstOf(props, listOf("ro.product.first_api_level", "ro.board.first_api_level")),
    )

    private data class Perception(val claimsPixel: Boolean, val codename: String?, val score: Int, val incoherences: List<String>)

    private val VOTE_KEYS = listOf("board", "device", "model", "soc_model", "first_api_level")
    private val COHERENCE_KEYS = listOf("brand", "manufacturer", "model", "device", "board", "soc_manufacturer", "soc_model", "first_api_level")

    private fun perceive(ctx: Context, props: Map<String, String>): Perception {
        val idx = index(ctx) ?: return Perception(false, null, 0, emptyList())
        val dev = deviceSignals(props)
        val brand = (dev["brand"] ?: "").lowercase()
        val mfr = (dev["manufacturer"] ?: "").lowercase()
        val model = dev["model"] ?: ""
        val soc = (dev["soc_manufacturer"] ?: "").lowercase()
        val claims = brand == "google" || mfr == "google" || model.startsWith("Pixel") || soc == "google"
        if (!claims) return Perception(false, null, 0, emptyList())
        var best: String? = null
        var bestScore = -1
        val it = idx.keys()
        while (it.hasNext()) {
            val cn = it.next()
            val sig = idx.getJSONObject(cn).getJSONObject("signals")
            var s = 0
            for (k in VOTE_KEYS) {
                val dv = dev[k]
                if (!dv.isNullOrEmpty() && dv.equals(sig.optString(k, ""), ignoreCase = true)) s++
            }
            if (s > bestScore) { bestScore = s; best = cn }
        }
        if (best == null || bestScore == 0)
            return Perception(true, null, 0, listOf("claims Pixel but no corpus codename matches any identity signal"))
        val sig = idx.getJSONObject(best).getJSONObject("signals")
        val inc = ArrayList<String>()
        for (k in COHERENCE_KEYS) {
            val dv = dev[k]
            val cv = sig.optString(k, "")
            if (!dv.isNullOrEmpty() && cv.isNotEmpty() && !dv.equals(cv, ignoreCase = true))
                inc.add("$k(dev=$dv real=$cv)")
        }
        return Perception(true, best, bestScore, inc)
    }

    private fun append(sb: StringBuilder, n: Int, s: String) {
        if (n <= MAX_TELLS) { if (sb.isNotEmpty()) sb.append("; "); sb.append(s) }
    }

    fun tasks(ctx: Context): List<ProbeTask> = listOf(
        jprobe("integrity:pixel_perception",
            "Claimed-Pixel identity coherence (multi-signal perception)", Category.FINGERPRINT) { c ->
            val props = NativeBridge.propList() ?: return@jprobe "n/a (props unavailable)"
            val p = perceive(c, props)
            when {
                !p.claimsPixel -> "n/a (not a claimed Pixel)"
                p.codename == null -> "MISMATCH -> " + p.incoherences.joinToString("; ")
                p.incoherences.isEmpty() -> "coherent Pixel: ${p.codename} (score ${p.score}/${VOTE_KEYS.size})"
                else -> "MISMATCH:${p.incoherences.size} (perceived ${p.codename}) -> " + p.incoherences.joinToString("; ")
            }
        },
        jprobe("integrity:pixel_corpus_match",
            "Every canonical build.prop vs device (exhaustive, real Google build)", Category.FINGERPRINT) { c ->
            val props = NativeBridge.propList() ?: return@jprobe "n/a (props unavailable)"
            val p = perceive(c, props)
            if (!p.claimsPixel || p.codename == null) return@jprobe "n/a (not a resolved Pixel)"
            val blob = deviceBlob(c, p.codename) ?: return@jprobe "n/a (corpus missing for ${p.codename})"
            val builds = blob.getJSONObject("builds")
            val devBuildId = props["ro.build.id"] ?: props["ro.system.build.id"] ?: ""
            val inc = props["ro.build.version.incremental"] ?: Build.VERSION.INCREMENTAL
            var buildKey: String? = null
            if (devBuildId.isNotEmpty()) {
                val bk = builds.keys()
                while (bk.hasNext()) { val k = bk.next(); if (builds.getJSONObject(k).optString("build_id") == devBuildId) { buildKey = k; break } }
            }
            if (buildKey == null) {
                val bk = builds.keys()
                while (bk.hasNext()) { val k = bk.next(); if (k.split("|").getOrNull(1) == inc) { buildKey = k; break } }
            }
            if (buildKey == null)
                return@jprobe "n/a (build $inc/$devBuildId outside the STRONG-capable corpus window for ${p.codename})"
            val canon = builds.getJSONObject(buildKey).getJSONObject("props")
            var n = 0
            var checked = 0
            val tells = StringBuilder()
            val ck = canon.keys()
            while (ck.hasNext()) {
                val key = ck.next()
                val cv = canon.getString(key)
                if (cv.isEmpty()) continue
                checked++
                val dv = props[key]
                if (dv == null) { n++; append(tells, n, "$key(missing real=$cv)") }
                else if (dv != cv) { n++; append(tells, n, "$key(dev=$dv real=$cv)") }
            }
            val more = if (n > MAX_TELLS) " (+${n - MAX_TELLS} more)" else ""
            if (n == 0) "MATCH ($checked props vs real $buildKey)"
            else "MISMATCH:$n/$checked ($buildKey) -> $tells$more"
        })
}
