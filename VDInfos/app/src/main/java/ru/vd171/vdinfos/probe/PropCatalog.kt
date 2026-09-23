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

import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Lens
import ru.vd171.vdinfos.core.model.ProbeSpec
import android.content.Context

object PropCatalog {

    private fun prop(
        key: String,
        category: Category,
        sensitive: Boolean = category == Category.IDENTITY,
    ) = ProbeSpec(
        id = "prop:$key",
        title = key,
        category = category,
        lenses = setOf(Lens.JAVA, Lens.NATIVE),
        sensitive = sensitive,
    )

    fun specs(ctx: Context): List<ProbeSpec> =
        AssetData.propEntries(ctx).map { prop(it.key, it.category, it.sensitive || it.category == Category.IDENTITY) }
}
