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

package ru.vd171.vdinfos.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.LensValue
import ru.vd171.vdinfos.core.model.ProbeResult
import ru.vd171.vdinfos.core.model.Verdict
import ru.vd171.vdinfos.ui.theme.verdictColor

@Composable
fun VerdictBadge(verdict: Verdict) {
    Text(
        text = stringResource(verdict.labelRes),
        color = Color.White,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(verdictColor(verdict))
            .padding(horizontal = 8.dp, vertical = 2.dp),
    )
}

private fun mask(value: String): String {
    if (value.length <= 4) return "••••"
    return value.take(2) + "•".repeat((value.length - 4).coerceAtMost(12)) + value.takeLast(2)
}

@Composable
private fun LensRow(v: LensValue, sensitive: Boolean, reveal: Boolean) {
    Column(Modifier.padding(top = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = v.lens.short,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 6.dp, vertical = 1.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = v.source,
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f, fill = false),
            )
            if (!v.compare) {
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.reading_context_only),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 5.dp, vertical = 1.dp),
                )
            }
        }
        val shown = when {
            v.error != null -> "! ${v.error}"
            v.value == null -> stringResource(R.string.reading_empty)
            sensitive && !reveal -> mask(v.value)
            else -> v.value
        }
        Text(
            text = shown,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = FontFamily.Monospace,
            color = if (v.error != null) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 4.dp, top = 1.dp),
        )
        v.detail?.takeIf { it.isNotBlank() }?.let { d ->
            Text(
                text = d,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 12.dp, top = 1.dp),
            )
        }
    }
}

@Composable
fun ProbeCard(result: ProbeResult, reveal: Boolean, modifier: Modifier = Modifier, startExpanded: Boolean = false) {
    var expanded by remember(startExpanded) { mutableStateOf(result.verdict == Verdict.MISMATCH || startExpanded) }
    var showSolution by remember { mutableStateOf(false) }
    val divergent = result.verdict == Verdict.MISMATCH
    val solution = result.spec.solution
    if (showSolution && solution != null) {
        val ctx = LocalContext.current
        AlertDialog(
            onDismissRequest = { showSolution = false },
            confirmButton = { TextButton(onClick = { showSolution = false }) { Text(stringResource(R.string.action_close)) } },
            icon = { Icon(Icons.Outlined.Lightbulb, null) },
            title = { Text(stringResource(R.string.sol_title)) },
            text = {
                Column {
                    Text(solution, style = MaterialTheme.typography.bodyMedium)
                    if (solution.contains("HMA-OSS")) {
                        Text(
                            HMA_OSS_URL.removePrefix("https://"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .clickable {
                                    runCatching {
                                        ctx.startActivity(
                                            android.content.Intent(
                                                android.content.Intent.ACTION_VIEW,
                                                android.net.Uri.parse(HMA_OSS_URL),
                                            )
                                        )
                                    }
                                },
                        )
                    }
                }
            },
        )
    }
    Card(
        modifier = modifier.fillMaxWidth().clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        border = if (divergent) BorderStroke(1.5.dp, verdictColor(result.verdict)) else null,
    ) {
        Column(Modifier.padding(12.dp).fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    if (result.spec.sensitive) {
                        Icon(
                            Icons.Filled.Lock, null,
                            modifier = Modifier.size(13.dp).padding(end = 4.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(result.spec.title, style = MaterialTheme.typography.titleMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (solution != null) {
                        OutlinedIconButton(
                            onClick = { showSolution = true },
                            modifier = Modifier.size(38.dp).padding(end = 6.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        ) {
                            Icon(
                                Icons.Outlined.Lightbulb,
                                contentDescription = stringResource(R.string.sol_title),
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    VerdictBadge(result.verdict)
                }
            }
            if (!expanded) {
                result.primary?.let {
                    Text(
                        if (result.spec.sensitive && !reveal) mask(it) else it,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
            }
            AnimatedVisibility(expanded) {
                Column {
                    result.values.forEach { LensRow(it, result.spec.sensitive, reveal) }
                    result.spec.note?.let {
                        Text(
                            it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Dot(color: Color) {
    Spacer(Modifier.size(10.dp).clip(CircleShape).background(color))
}

@Composable
fun CategoryHeader(
    title: String,
    total: Int,
    mismatches: Int,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            if (expanded) "▾" else "▸",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        if (mismatches > 0) {
            Text(
                "$mismatches",
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(styleMismatch)
                    .padding(horizontal = 7.dp, vertical = 2.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(
            "$total",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val styleMismatch = Color(0xFFFF5A5A)

private const val HMA_OSS_URL = "https://github.com/frknkrc44/HMA-OSS"
