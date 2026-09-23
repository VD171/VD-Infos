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

package ru.vd171.vdinfos.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val Teal = Color(0xFF2DD4BF)
private val TealDark = Color(0xFF0D9488)
private val Slate = Color(0xFF0B1220)

private val DarkColors = darkColorScheme(
    primary = Teal,
    onPrimary = Color(0xFF00201B),
    secondary = Color(0xFF7DD3FC),
    background = Slate,
    surface = Color(0xFF111826),
    surfaceVariant = Color(0xFF1B2536),
    onSurfaceVariant = Color(0xFFB6C2D6),
    error = Color(0xFFFF6B6B),
)

private val LightColors = lightColorScheme(
    primary = TealDark,
    onPrimary = Color.White,
    secondary = Color(0xFF0369A1),
    background = Color(0xFFF6F8FB),
    surface = Color.White,
    surfaceVariant = Color(0xFFE7ECF3),
    onSurfaceVariant = Color(0xFF44506A),
    error = Color(0xFFD32F2F),
)

@Composable
fun VdInfosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val scheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(
        colorScheme = scheme,
        typography = VdTypography,
        content = content,
    )
}
