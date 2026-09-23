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

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.ui.components.AboutDialog
import ru.vd171.vdinfos.ui.components.CategoryHeader
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Verdict
import ru.vd171.vdinfos.data.Exporter
import ru.vd171.vdinfos.ui.components.Dot
import ru.vd171.vdinfos.ui.components.ProbeCard
import ru.vd171.vdinfos.ui.theme.verdictColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(vm: ScanViewModel = viewModel()) {
    val state by vm.state.collectAsStateWithLifecycle()
    val ctx = LocalContext.current
    var showAbout by remember { mutableStateOf(false) }

    val saveLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            val ok = runCatching {
                ctx.contentResolver.openOutputStream(uri)?.use {
                    it.write(vm.reportJson().toByteArray())
                } != null
            }.getOrDefault(false)
            Toast.makeText(
                ctx,
                ctx.getString(if (ok) R.string.save_ok else R.string.save_fail),
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row {
                            Text(
                                stringResource(R.string.app_name),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.alignByBaseline(),
                            )
                            Text(
                                " v" + ru.vd171.vdinfos.BuildConfig.VERSION_NAME,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.alignByBaseline(),
                            )
                        }
                        Text(
                            stringResource(R.string.app_tagline) +
                                " · SDK " + ctx.applicationInfo.targetSdkVersion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { vm.toggleReveal() }) {
                        Icon(
                            if (state.reveal) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                            contentDescription = stringResource(R.string.action_reveal),
                        )
                    }
                    IconButton(onClick = { saveLauncher.launch(vm.suggestedFileName()) }) {
                        Icon(Icons.Filled.SaveAlt, contentDescription = stringResource(R.string.action_save))
                    }
                    IconButton(onClick = {
                        val intent = Exporter.shareIntent(ctx, vm.currentSnapshot())
                        ctx.startActivity(Intent.createChooser(intent, ctx.getString(R.string.action_export)))
                    }) { Icon(Icons.Filled.Share, contentDescription = stringResource(R.string.action_export)) }
                    IconButton(onClick = { showAbout = true }) {
                        Icon(Icons.Filled.Info, contentDescription = stringResource(R.string.action_about))
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { vm.scan() }) {
                Icon(Icons.Filled.Refresh, contentDescription = stringResource(R.string.action_rescan))
            }
        },
    ) { pad ->
        if (showAbout) AboutDialog { showAbout = false }
        Column(Modifier.fillMaxSize().padding(pad)) {
            SummaryHeader(state)
            SearchAndFilters(state, vm)
            val grouped = remember(state.filtered) { state.filtered.groupBy { it.spec.category } }
            val expanded = remember { mutableStateMapOf<Category, Boolean>() }
            LaunchedEffect(state.query) { expanded.clear() }
            val searching = state.query.isNotBlank()
            val singleCategory = grouped.size == 1
            val singleItem = state.filtered.size == 1
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 12.dp, end = 12.dp, bottom = 96.dp, top = 4.dp
                ),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                grouped.forEach { (cat, rows) ->
                    val mm = rows.count { it.isDivergent }
                    val isOpen = expanded[cat] ?: (searching || mm > 0 || singleCategory)
                    item(key = "hdr:${cat.name}") {
                        CategoryHeader(
                            title = stringResource(cat.labelRes),
                            total = rows.size, mismatches = mm, expanded = isOpen,
                        ) { expanded[cat] = !isOpen }
                    }
                    if (isOpen) {
                        items(rows, key = { it.spec.id }) { r ->
                            ProbeCard(r, reveal = state.reveal, startExpanded = singleItem)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryHeader(state: ScanUiState) {
    val allAgreed = !state.scanning && state.total > 0 &&
        state.done == state.total && state.mismatches == 0
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    (if (allAgreed) stringResource(R.string.summary_all_agreed_mark) else "") +
                        pluralStringResource(
                            R.plurals.summary_divergences, state.mismatches, state.mismatches,
                        ),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        state.mismatches > 0 -> verdictColor(Verdict.MISMATCH)
                        allAgreed -> verdictColor(Verdict.MATCH)
                        else -> MaterialTheme.colorScheme.onSurface
                    },
                )
                Text(
                    stringResource(R.string.summary_line, state.done, state.total, state.matches) +
                        if (!state.nativeAvailable) stringResource(R.string.summary_native_off) else "",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            if (state.scanning) CircularProgressIndicator(Modifier.width(24.dp))
        }
        if (state.scanning) {
            LinearProgressIndicator(
                progress = { state.progress },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            )
        }
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Legend(Verdict.MISMATCH); Legend(Verdict.MATCH); Legend(Verdict.SINGLE); Legend(Verdict.INFO)
        }
    }
}

@Composable
private fun Legend(v: Verdict) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Dot(verdictColor(v))
        Spacer(Modifier.width(4.dp))
        Text(stringResource(v.labelRes), style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchAndFilters(state: ScanUiState, vm: ScanViewModel) {
    Column {
        OutlinedTextField(
            value = state.query,
            onValueChange = { vm.setQuery(it) },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            trailingIcon = {
                if (state.query.isNotEmpty()) {
                    IconButton(onClick = { vm.setQuery("") }) {
                        Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.action_clear))
                    }
                }
            },
            placeholder = { Text(stringResource(R.string.search_hint)) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        )
        Row(
            Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                selected = state.onlyDivergent,
                onClick = { vm.toggleDivergent() },
                label = { Text(stringResource(R.string.filter_divergent)) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = verdictColor(Verdict.MISMATCH),
                ),
            )
            FilterChip(
                selected = state.category == null,
                onClick = { vm.setCategory(null) },
                label = { Text(stringResource(R.string.filter_all)) },
            )
            state.categories.forEach { c ->
                FilterChip(
                    selected = state.category == c,
                    onClick = { vm.setCategory(if (state.category == c) null else c) },
                    label = { Text(stringResource(c.labelRes)) },
                )
            }
        }
    }
}
