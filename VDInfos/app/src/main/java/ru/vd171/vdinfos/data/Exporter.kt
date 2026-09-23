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

package ru.vd171.vdinfos.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import ru.vd171.vdinfos.core.model.Verdict
import kotlinx.serialization.json.Json
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Exporter {

    private val json = Json { prettyPrint = true; encodeDefaults = true }

    fun toJson(snapshot: Snapshot): String =
        json.encodeToString(Snapshot.serializer(), snapshot)

    fun toText(snapshot: Snapshot): String = buildString {
        val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date(snapshot.takenAt))
        appendLine("VD Infos ${snapshot.appVersion} - $stamp")
        val mismatches = snapshot.results.filter { it.verdict == Verdict.MISMATCH }
        appendLine("Probes: ${snapshot.results.size} | Divergences: ${mismatches.size}")
        appendLine("=".repeat(48))
        if (mismatches.isNotEmpty()) {
            appendLine("\n## DIVERGENCES (Java × Native)")
            mismatches.forEach { r ->
                appendLine("• ${r.spec.title}")
                r.values.forEach { v ->
                    appendLine("    ${v.lens.short}: ${v.value ?: " - "}")
                    v.detail?.takeIf { it.isNotBlank() }?.let { appendLine("        ($it)") }
                }
            }
        }
    }

    fun shareIntent(context: Context, snapshot: Snapshot): Intent {
        val dir = File(context.cacheDir, "reports").apply { mkdirs() }
        val f = File(dir, "vdinfos-report.json")
        val tmp = File(dir, "vdinfos-report.json.tmp")
        tmp.writeText(toJson(snapshot))
        if (!tmp.renameTo(f)) {
            f.writeText(tmp.readText())
            tmp.delete()
        }
        val uri: Uri = FileProvider.getUriForFile(
            context, "${context.packageName}.fileprovider", f
        )
        return Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, toText(snapshot))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
