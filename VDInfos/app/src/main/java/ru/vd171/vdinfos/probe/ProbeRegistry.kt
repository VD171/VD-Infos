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

import android.content.Context
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask

object ProbeRegistry {

    fun build(context: Context): List<ProbeTask> {
        val all = buildList {
            PropCatalog.specs(context).forEach { spec ->
                add(propItem(spec.id.removePrefix("prop:"), spec.category, spec.sensitive))
            }
            addAll(SemanticProbes.tasks(context))
            addAll(BuildProbes.tasks(context))
            addAll(IdentifierProbes.tasks(context))
            addAll(TelephonyProbes.tasks(context))
            addAll(NetworkProbes.tasks(context))
            addAll(SystemExtraProbes.tasks(context))
            addAll(DevIdProbes.tasks(context))
            addAll(BulkProbes.tasks(context))
            addAll(SelfPackageProbes.tasks(context))
            addAll(InstallSourceProbes.tasks(context))
            addAll(QueryProbes.tasks(context))
            addAll(AttestProbes.tasks(context))
            addAll(AttestExtraProbes.tasks(context))
            addAll(SandboxProbes.tasks(context))
            addAll(DeepProbes.tasks(context))
            addAll(IntegrityProbes.tasks(context))
            addAll(IntegrityExtraProbes.tasks(context))
            addAll(ServiceProbes.tasks(context))
            addAll(ParanoidProbes.tasks(context))
            addAll(FrontierProbes.tasks(context))
            addAll(SettingsSpoofProbes.tasks(context))
        }
        return all.associateBy { it.spec.id }.values.toList()
    }

}
