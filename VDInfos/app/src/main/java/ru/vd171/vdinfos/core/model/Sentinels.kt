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

object Sentinels {

    const val EACCES = "EACCES"
    const val DENIED = "DENIED"
    const val NOT_PERMITTED = "NOT_PERMITTED"
    const val EPERM = "EPERM"
    const val PERMISSION_DENIED = "PERMISSION_DENIED"

    val REFUSALS = setOf(EACCES, DENIED, NOT_PERMITTED, EPERM, PERMISSION_DENIED)

    const val TIMEOUT = "TIMEOUT"
    const val POPEN_FAILED = "POPEN_FAILED"
    const val NO_STATUS = "NO_STATUS"
    const val TOO_LONG_FOR_92B_API = "TOO_LONG_FOR_92B_API"

    const val RESTRICTED = "RESTRICTED"

    val FAILURES = setOf(TIMEOUT, POPEN_FAILED, NO_STATUS, TOO_LONG_FOR_92B_API, RESTRICTED)

    const val EXIT_PREFIX = "EXIT:"
    const val SIGNAL_PREFIX = "SIGNAL:"
    const val ERR_PREFIX = "ERR("

    val FAILURE_PREFIXES = listOf(EXIT_PREFIX, SIGNAL_PREFIX, ERR_PREFIX)

    const val ABSENT = "absent"
    const val NONE = "(none)"
    const val PRESENT = "present"
}
