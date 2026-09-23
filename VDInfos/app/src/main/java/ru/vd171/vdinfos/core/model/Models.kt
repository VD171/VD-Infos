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

package ru.vd171.vdinfos.core.model

import androidx.annotation.StringRes
import ru.vd171.vdinfos.R
import kotlinx.serialization.Serializable

enum class Category(@StringRes val labelRes: Int) {
    IDENTITY(R.string.cat_identity),
    FINGERPRINT(R.string.cat_fingerprint),
    TELEPHONY(R.string.cat_telephony),
    NETWORK(R.string.cat_network),
    LOCALE(R.string.cat_locale),
    BOOT(R.string.cat_boot),
    BUILD(R.string.cat_build),
    HARDWARE(R.string.cat_hardware),
    DISPLAY(R.string.cat_display),
    SENSORS(R.string.cat_sensors),
    MEDIA(R.string.cat_media),
    STORAGE(R.string.cat_storage),
    PROCESS(R.string.cat_process),
    PACKAGES(R.string.cat_packages),
    ACCOUNTS(R.string.cat_accounts),
    WEBVIEW(R.string.cat_webview),
    SECURITY(R.string.cat_security),
    INTEGRITY(R.string.cat_integrity),
    EMULATOR(R.string.cat_emulator),
    SYSTEM(R.string.cat_system),
}

enum class Lens(val label: String, val short: String) {
    JAVA("Java / SDK", "JVM"),
    NATIVE("Native / JNI", "JNI"),
    SHELL("Shell via JVM", "JVM+SH"),
    SHELL_NATIVE("Shell via JNI", "JNI+SH"),
    ATTEST("TEE / Attestation", "TEE"),
}

@Serializable
data class LensValue(
    val lens: Lens,
    val source: String,
    val value: String?,
    val error: String? = null,
    val elapsedMicros: Long = 0L,
    val compare: Boolean = true,
    val detail: String? = null,
) {
    val ok: Boolean get() = error == null
    val present: Boolean get() = ok && !value.isNullOrEmpty()
}

enum class Verdict(@StringRes val labelRes: Int) {
    MATCH(R.string.verdict_match),
    MISMATCH(R.string.verdict_mismatch),
    SINGLE(R.string.verdict_single),
    EMPTY(R.string.verdict_empty),
    ERROR(R.string.verdict_error),
    INFO(R.string.verdict_info),
}

@Serializable
data class ProbeSpec(
    val id: String,
    val title: String,
    val category: Category,
    val lenses: Set<Lens>,
    val note: String? = null,
    val sensitive: Boolean = false,
    val solution: String? = null,
)

@Serializable
data class ProbeResult(
    val spec: ProbeSpec,
    val values: List<LensValue>,
    val verdict: Verdict,
    val detail: String? = null,
) {
    fun value(lens: Lens): String? = values.firstOrNull { it.lens == lens }?.value
    val primary: String? get() = values.firstOrNull { it.present }?.value
    val isDivergent: Boolean get() = verdict == Verdict.MISMATCH

    companion object {
        fun verdictOf(values: List<LensValue>): Verdict {
            val present = values.filter { it.present }
            val errored = values.filter { !it.ok }
            if (present.isEmpty()) return if (errored.isNotEmpty()) Verdict.ERROR else Verdict.EMPTY
            val voting = present.filter { it.compare && !isInstrumentFailure(it.value!!) }
            if (voting.isEmpty()) return Verdict.INFO
            val hasNonShellValue = voting.any { !it.lens.isShell && !isRefusal(it.value!!) }
            val comparable = if (hasNonShellValue)
                voting.filterNot { it.lens.isShell && isRefusal(it.value!!) } else voting
            if (comparable.size == 1) return Verdict.SINGLE
            val norm = comparable.map { normalise(it.value!!) }
            val polarity = norm.map { BOOLEAN_WORDS[it] }
            if (polarity.all { it != null }) {
                return if (polarity.toSet().size == 1) Verdict.MATCH else Verdict.MISMATCH
            }
            return if (norm.toSet().size == 1) Verdict.MATCH else Verdict.MISMATCH
        }

        private val BOOLEAN_WORDS = mapOf(
            "true" to true, "1" to true, "yes" to true, "on" to true, "enabled" to true,
            "false" to false, "0" to false, "no" to false, "off" to false, "disabled" to false,
        )

        private val Lens.isShell get() = this == Lens.SHELL || this == Lens.SHELL_NATIVE

        private fun isRefusal(v: String): Boolean = v.trim().uppercase() in Sentinels.REFUSALS

        fun isInstrumentFailure(v: String): Boolean {
            val t = v.trim()
            return t in Sentinels.FAILURES || Sentinels.FAILURE_PREFIXES.any { t.startsWith(it) }
        }

        private fun normalise(v: String): String =
            v.trim().trim('"').replace(Regex("\\s+"), " ").lowercase()
    }
}
