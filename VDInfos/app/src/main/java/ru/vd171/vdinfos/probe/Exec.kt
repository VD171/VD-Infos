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

import ru.vd171.vdinfos.core.model.Sentinels
import java.util.concurrent.TimeUnit

object Exec {

    private val REFUSALS = listOf(
        "Permission Denial", "SecurityException", "Error: ",
        "Exception occurred",
        "Error while accessing provider",
    )

    private val SHELL_ERROR =
        Regex("""^[\w./+-]+: .*: (No such file or directory|Permission denied|not found)$""")

    fun run(cmd: String, timeoutMs: Long = 3000, cap: Int = 8000): String? = runCatching {
        val p = ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start()
        p.outputStream.close()
        val finished = p.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
        if (!finished) p.destroyForcibly()
        val out = p.inputStream.bufferedReader().use { r ->
            val sb = StringBuilder()
            val buf = CharArray(4096)
            var n = r.read(buf)
            while (n >= 0 && sb.length < cap) { sb.append(buf, 0, n); n = r.read(buf) }
            sb.toString()
        }
        if (!finished || p.exitValue() != 0) return@runCatching null
        val text = out.trim()
        val firstLine = primeiraUtil(text)
        if (REFUSALS.any { firstLine.startsWith(it) }) return@runCatching null
        if (reasonOf(firstLine) != null) return@runCatching null
        text.ifEmpty { null }
    }.getOrNull()

    private fun primeiraUtil(out: String): String =
        out.lineSequence().firstOrNull() { it.isNotBlank() }.orEmpty().trim()

    fun reasonOf(first: String): String? = SHELL_ERROR.find(first)?.groupValues?.get(1)

    fun nullIfRefusal(out: String?): String? {
        if (out.isNullOrEmpty()) return out
        val first = primeiraUtil(out)
        if (out == Sentinels.NO_STATUS || out.startsWith("EXIT:") || out.startsWith("SIGNAL:")) return null
        val falhou = reasonOf(first) != null ||
            first.contains("Permission Denial", true) ||
            first.contains("SecurityException") ||
            first.startsWith("Exception occurred") ||
            first.startsWith("Error while accessing provider")
        return if (falhou) null else out
    }

    fun normalise(out: String?): String? {
        if (out.isNullOrEmpty()) return out
        val first = primeiraUtil(out)
        val r = reasonOf(first) ?: return when {
            first.contains("Permission Denial", true) -> Sentinels.EACCES
            first.contains("SecurityException") -> Sentinels.DENIED
            first.startsWith("Exception occurred") -> Sentinels.DENIED
            first.startsWith("Error while accessing provider") -> Sentinels.DENIED
            first.contains("can't open file", true) -> "ENOENT"
            else -> out
        }
        return when {
            r.contains("Permission denied", true) -> Sentinels.EACCES
            r.contains("No such file", true) -> "ENOENT"
            else -> "NOT_FOUND"
        }
    }

    fun runOrReason(cmd: String, timeoutMs: Long = 3000): String = runCatching {
        val p = ProcessBuilder("sh", "-c", cmd).redirectErrorStream(true).start()
        p.outputStream.close()
        val finished = p.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
        if (!finished) { p.destroyForcibly(); return@runCatching Sentinels.TIMEOUT }
        val out = p.inputStream.bufferedReader().use { it.readText() }.trim()
        val first = primeiraUtil(out)
        val shellError = reasonOf(first)
        when {
            shellError != null -> when {
                shellError.contains("Permission denied", true) -> Sentinels.EACCES
                shellError.contains("No such file", true) -> "ENOENT"
                else -> "NOT_FOUND"
            }
            p.exitValue() == 0 && out.isNotEmpty() &&
                REFUSALS.none { first.startsWith(it) } -> out
            p.exitValue() == 0 && out.isEmpty() -> "(empty)"
            first.contains("Permission Denial", true) -> Sentinels.EACCES
            first.contains("SecurityException") -> Sentinels.DENIED
            first.startsWith("Exception occurred") -> Sentinels.DENIED
            first.startsWith("Error while accessing provider") -> Sentinels.DENIED
            first.contains("can't open file", true) -> "ENOENT"
            first.isNotEmpty() -> "ERR(" + first.take(60) + ")"
            else -> "EXIT:" + p.exitValue()
        }
    }.getOrElse { "ERR(" + it.javaClass.simpleName + ")" }
}
