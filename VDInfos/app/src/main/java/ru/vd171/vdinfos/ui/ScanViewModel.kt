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

package ru.vd171.vdinfos.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ru.vd171.vdinfos.BuildConfig
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.ProbeResult
import ru.vd171.vdinfos.core.model.Verdict
import ru.vd171.vdinfos.data.LocaleManager
import ru.vd171.vdinfos.data.Snapshot
import ru.vd171.vdinfos.data.SnapshotStore
import ru.vd171.vdinfos.engine.ProbeEngine
import ru.vd171.vdinfos.probe.NativeBridge
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ScanUiState(
    val results: List<ProbeResult> = emptyList(),
    val scanning: Boolean = false,
    val total: Int = 0,
    val nativeAvailable: Boolean = NativeBridge.available,
    val query: String = "",
    val onlyDivergent: Boolean = false,
    val category: Category? = null,
    val reveal: Boolean = false,
) {
    val done: Int get() = results.size
    val progress: Float get() = if (total == 0) 0f else done.toFloat() / total

    val mismatches: Int get() = results.count { it.verdict == Verdict.MISMATCH }
    val matches: Int get() = results.count { it.verdict == Verdict.MATCH }

    val categories: List<Category>
        get() = Category.entries.filter { c -> results.any { it.spec.category == c } }

    val filtered: List<ProbeResult>
        get() = results.asSequence()
            .filter { !onlyDivergent || it.verdict == Verdict.MISMATCH }
            .filter { category == null || it.spec.category == category }
            .filter {
                query.isBlank() ||
                    it.spec.title.contains(query, true) ||
                    it.spec.id.contains(query, true) ||
                    it.values.any { v -> v.value?.contains(query, true) == true }
            }
            .sortedWith(compareBy<ProbeResult> { it.spec.category.ordinal }
                .thenByDescending { it.verdict == Verdict.MISMATCH }
                .thenBy { it.spec.title })
            .toList()
}

class ScanViewModel(app: Application) : AndroidViewModel(app) {

    private val engine = ProbeEngine(LocaleManager.wrap(app))
    private val store = SnapshotStore(app)

    private val _state = MutableStateFlow(ScanUiState(total = engine.count))
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    init { scan() }

    fun scan() {
        if (_state.value.scanning) return
        _state.update { it.copy(scanning = true, results = emptyList()) }
        viewModelScope.launch {
            val acc = ArrayList<ProbeResult>(engine.count)
            var i = 0
            engine.scan().collect { r ->
                acc.add(r); i++
                if (i % 25 == 0) _state.update { it.copy(results = ArrayList(acc)) }
            }
            _state.update { it.copy(results = ArrayList(acc), scanning = false) }
            persist(acc)
        }
    }

    private fun persist(results: List<ProbeResult>) {
        store.save(Snapshot(System.currentTimeMillis(), BuildConfig.VERSION_NAME, results))
    }

    fun setQuery(q: String) = _state.update { it.copy(query = q) }
    fun toggleDivergent() = _state.update { it.copy(onlyDivergent = !it.onlyDivergent) }
    fun setCategory(c: Category?) = _state.update { it.copy(category = c) }
    fun toggleReveal() = _state.update { it.copy(reveal = !it.reveal) }

    fun currentSnapshot(): Snapshot =
        Snapshot(System.currentTimeMillis(), BuildConfig.VERSION_NAME, _state.value.results)

    fun reportJson(): String =
        ru.vd171.vdinfos.data.Exporter.toJson(currentSnapshot())

    fun suggestedFileName(): String {
        val ts = java.text.SimpleDateFormat("yyyyMMdd-HHmm", java.util.Locale.US)
            .format(java.util.Date())
        return "vdinfos-$ts.json"
    }
}
