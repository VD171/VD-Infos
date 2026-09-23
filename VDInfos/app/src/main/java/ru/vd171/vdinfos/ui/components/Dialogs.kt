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

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Android
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ru.vd171.vdinfos.BuildConfig
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.data.LocaleManager

@Composable
private fun AboutRow(icon: ImageVector, label: String?, value: String, url: String? = null) {
    val ctx = LocalContext.current
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .then(
                if (url == null) Modifier
                else Modifier.clickable {
                    runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
                }
            )
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon, null,
            modifier = Modifier.size(18.dp),
            tint = if (url == null) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            if (label != null) Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (url == null) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun AboutCard(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(4.dp),
        content = content,
    )
}

@Composable
private fun AboutSectionLabel(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 6.dp),
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val targetSdk = LocalContext.current.applicationInfo.targetSdkVersion.toString()
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) }
        },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Outlined.Search, null,
                        modifier = Modifier.size(26.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "v" + BuildConfig.VERSION_NAME + "  \u00b7  SDK " + targetSdk,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 6.dp, vertical = 1.dp),
                        )
                    }
                    Text(
                        stringResource(R.string.app_tagline),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                AboutSectionLabel(stringResource(R.string.about_project))
                AboutCard {
                    AboutRow(Icons.Outlined.Person, stringResource(R.string.about_developer), "VD171", "https://github.com/VD171")
                    AboutRow(Icons.Outlined.Code, stringResource(R.string.about_source), "github.com/VD171/VD-Infos", "https://github.com/VD171/VD-Infos")
                    AboutRow(Icons.Outlined.Gavel, stringResource(R.string.about_license), "GNU AGPL-3.0-or-later")
                }

                AboutSectionLabel(stringResource(R.string.about_contacts))
                AboutCard {
                    AboutRow(Icons.Outlined.Language, null, "https://vd171.ru", "https://vd171.ru")
                    AboutRow(Icons.Outlined.Language, null, "https://vd.priv8.ru", "https://vd.priv8.ru")
                    AboutRow(Icons.AutoMirrored.Outlined.Send, "Telegram", "@VD_Priv8", "https://t.me/VD_Priv8")
                    AboutRow(Icons.Outlined.Chat, "Discord", "@VD.Priv8", "https://discord.com/users/1296831918989639721")
                    AboutRow(Icons.Outlined.Email, "E-mail", "vd.priv8@pm.me", "mailto:vd.priv8@pm.me")
                    AboutRow(Icons.Outlined.Forum, "XDA", "@VD171", "https://xdaforums.com/m/vd171.4699873/")
                    AboutRow(Icons.Outlined.Link, "GitHub", "@VD171", "https://github.com/VD171")
                }

                AboutSectionLabel(stringResource(R.string.about_support))
                AboutCard {
                    AboutRow(Icons.Outlined.OpenInNew, null, "github.com/VD171/VD-Infos/releases", "https://github.com/VD171/VD-Infos/releases")
                    AboutRow(Icons.Outlined.OpenInNew, null, "xdaforums.com/t/VD-Infos.4097379", "https://xdaforums.com/t/VD-Infos.4097379/")
                    AboutRow(Icons.Outlined.OpenInNew, null, "@RootDetected on Telegram", "https://t.me/RootDetected")
                    AboutRow(Icons.Outlined.OpenInNew, null, "@BlankAssistance on Telegram", "https://t.me/BlankAssistance")
                }

                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f))
                        .padding(12.dp),
                ) {
                    Icon(
                        Icons.Outlined.Shield, null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        stringResource(R.string.about_privacy),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                }
            }
        },
    )
}

@Composable
fun LanguageDialog(current: String, onCommit: (String) -> Unit) {
    var selected by remember { mutableStateOf(current) }
    var expanded by remember { mutableStateOf(false) }
    val systemLabel = stringResource(R.string.lang_system_default)
    val selectedLabel = if (selected.isBlank()) systemLabel
        else LocaleManager.LANGUAGES.firstOrNull { it.first == selected }?.second ?: selected

    AlertDialog(
        onDismissRequest = { onCommit(selected) },
        confirmButton = {
            TextButton(onClick = { onCommit(selected) }) { Text(stringResource(R.string.lang_apply)) }
        },
        title = { Text(stringResource(R.string.lang_dialog_title), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    stringResource(R.string.lang_dialog_body),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(Modifier.padding(top = 14.dp)) {
                    OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                        Text("Language", modifier = Modifier.weight(1f))
                        Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(systemLabel) },
                            leadingIcon = { if (selected.isBlank()) Icon(Icons.Filled.Check, null) },
                            onClick = { selected = ""; expanded = false },
                        )
                        LocaleManager.LANGUAGES.forEach { (tag, name) ->
                            DropdownMenuItem(
                                text = { Text(name) },
                                leadingIcon = { if (selected == tag) Icon(Icons.Filled.Check, null) },
                                onClick = { selected = tag; expanded = false },
                            )
                        }
                    }
                }
                Text(
                    selectedLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp, start = 4.dp),
                )
            }
        },
    )
}

@Composable
fun CrashDialog(report: String, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    val clipboard = LocalClipboardManager.current
    var showAbout by remember { mutableStateOf(false) }

    if (showAbout) AboutDialog { showAbout = false }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Row(horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { showAbout = true }) {
                    Text(stringResource(R.string.action_about))
                }
                TextButton(onClick = {
                    clipboard.setText(AnnotatedString(report))
                    Toast.makeText(ctx, ctx.getString(R.string.copied), Toast.LENGTH_SHORT).show()
                }) { Text(stringResource(R.string.action_copy)) }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.action_close)) }
        },
        title = { Text(stringResource(R.string.crash_title), fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(
                    stringResource(R.string.crash_body),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    report,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .verticalScroll(rememberScrollState())
                        .padding(8.dp),
                )
            }
        },
    )
}
