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
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Lens
import ru.vd171.vdinfos.core.model.LensValue
import ru.vd171.vdinfos.core.model.ProbeResult
import ru.vd171.vdinfos.core.model.Verdict
import ru.vd171.vdinfos.probe.ProbeRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeoutOrNull

class ProbeEngine(private val appContext: Context) {

    private val tasks = ProbeRegistry.build(appContext)

    val count: Int get() = tasks.size

    fun scan(concurrency: Int = DEFAULT_CONCURRENCY): Flow<ProbeResult> = channelFlow {
        val gate = Semaphore(concurrency)
        coroutineScope {
            for (task in tasks) {
                launch {
                    gate.withPermit {
                        val values = withTimeoutOrNull(TASK_TIMEOUT_MS) { task.run(appContext) }
                            ?: listOf(LensValue(Lens.SHELL, "timeout", null,
                                appContext.getString(ru.vd171.vdinfos.R.string.err_probe_timeout)))
                        var verdict = ProbeResult.verdictOf(values)
                        if (verdict == Verdict.SINGLE && task.spec.category == Category.INTEGRITY) {
                            verdict = Verdict.INFO
                        }
                        send(ProbeResult(task.spec, values, verdict))
                    }
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun scanAll(concurrency: Int = DEFAULT_CONCURRENCY): List<ProbeResult> =
        scan(concurrency).toList()

    companion object {
        const val DEFAULT_CONCURRENCY = 32
        const val TASK_TIMEOUT_MS = 8000L
    }
}
