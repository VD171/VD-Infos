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

import androidx.compose.ui.graphics.Color
import ru.vd171.vdinfos.core.model.Verdict

fun verdictColor(verdict: Verdict): Color = when (verdict) {
    Verdict.MISMATCH -> Color(0xFFFF5A5A)
    Verdict.MATCH -> Color(0xFF34D399)
    Verdict.SINGLE -> Color(0xFF94A3B8)
    Verdict.INFO -> Color(0xFF60A5FA)
    Verdict.EMPTY -> Color(0xFF64748B)
    Verdict.ERROR -> Color(0xFFF59E0B)
}
