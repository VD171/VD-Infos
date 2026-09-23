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

package ru.vd171.vdinfos.engine

import android.content.Context
import ru.vd171.vdinfos.core.model.Lens
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.core.model.LensValue
import ru.vd171.vdinfos.core.model.ProbeSpec

class ProbeTask(
    val spec: ProbeSpec,
    val run: suspend (Context) -> List<LensValue>,
)

fun refusalToken(t: Throwable): String? {
    val m = (t.message ?: "") + " " + t.javaClass.simpleName
    return when {
        m.contains(Sentinels.EACCES) || m.contains("Permission denied", true) -> Sentinels.EACCES
        m.contains(Sentinels.EPERM) -> Sentinels.EPERM
        m.contains("ENOENT") || m.contains("No such file", true) -> "ENOENT"
        m.contains("does not meet the requirements") -> Sentinels.NOT_PERMITTED
        m.contains("Not allowed to access") -> Sentinels.NOT_PERMITTED
        m.contains("is only readable to apps with") -> Sentinels.NOT_PERMITTED
        m.contains("SecurityException") -> Sentinels.DENIED
        m.contains("requires", true) && m.contains("permission", true) -> Sentinels.DENIED
        m.contains("must either hold", true) || m.contains("requires being inst", true) -> Sentinels.DENIED
        m.contains("Neither user", true) -> Sentinels.NOT_PERMITTED
        else -> null
    }
}

inline fun measure(lens: Lens, source: String, compare: Boolean = true, block: () -> String?): LensValue {
    val t0 = System.nanoTime()
    return try {
        val v = block()
        LensValue(lens, source, v?.takeIf { it.isNotEmpty() }, null, (System.nanoTime() - t0) / 1000, compare)
    } catch (t: Throwable) {
        val token = refusalToken(t)
        val msg = t.message ?: t.javaClass.simpleName
        LensValue(
            lens = lens, source = source, value = token,
            error = if (token != null) null else msg,
            elapsedMicros = (System.nanoTime() - t0) / 1000, compare = compare,
            detail = if (token != null) msg else null,
        )
    }
}
