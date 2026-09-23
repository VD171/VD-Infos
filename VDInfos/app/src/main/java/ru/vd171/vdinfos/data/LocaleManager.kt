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
import android.content.res.Configuration
import java.util.Locale

object LocaleManager {

    private const val PREFS = "vdinfos_prefs"
    private const val KEY_CHOSEN = "locale_chosen"
    private const val KEY_TAG = "locale_tag"

    fun tag(ctx: Context): String =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY_TAG, "") ?: ""

    fun chosen(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getBoolean(KEY_CHOSEN, false)

    fun save(ctx: Context, tag: String) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_TAG, tag)
            .putBoolean(KEY_CHOSEN, true)
            .apply()
    }

    fun wrap(base: Context): Context {
        val tag = tag(base)
        if (tag.isBlank()) return base
        val locale = Locale.forLanguageTag(tag)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        return base.createConfigurationContext(config)
    }

    val LANGUAGES: List<Pair<String, String>> = listOf(
        "en" to "English",
        "pt" to "Português",
        "es" to "Español",
        "it" to "Italiano",
        "de" to "Deutsch",
        "fr" to "Français",
        "ru" to "Русский",
        "in" to "Bahasa Indonesia",
        "tr" to "Türkçe",
        "pl" to "Polski",
        "nl" to "Nederlands",
        "sv" to "Svenska",
        "cs" to "Čeština",
        "vi" to "Tiếng Việt",
        "zh" to "中文",
        "ja" to "日本語",
        "ko" to "한국어",
        "fa" to "فارسی",
        "hi" to "हिन्दी",
        "ar" to "العربية",
        "th" to "ไทย",
    )
}
