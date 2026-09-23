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

package ru.vd171.vdinfos.data

import android.content.Context
import ru.vd171.vdinfos.core.model.ProbeResult
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class Snapshot(
    val takenAt: Long,
    val appVersion: String,
    val results: List<ProbeResult>,
) {
    fun primaryMap(): Map<String, String?> =
        results.associate { it.spec.id to it.primary }
}

data class Change(val id: String, val title: String, val before: String?, val after: String?)

class SnapshotStore(context: Context) {

    private val file = File(context.filesDir, "last_snapshot.json")
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    private companion object { val writeLock = Any() }

    fun save(snapshot: Snapshot) {
        runCatching {
            synchronized(writeLock) {
                val tmp = File(file.parentFile, "${file.name}.tmp")
                tmp.writeText(json.encodeToString(Snapshot.serializer(), snapshot))
                if (!tmp.renameTo(file)) {
                    file.writeText(tmp.readText())
                    tmp.delete()
                }
            }
        }
    }

    fun load(): Snapshot? = runCatching {
        if (!file.exists()) null
        else json.decodeFromString(Snapshot.serializer(), file.readText())
    }.getOrNull()

    fun diff(old: Snapshot, new: Snapshot): List<Change> {
        val before = old.primaryMap()
        val byId = new.results.associateBy { it.spec.id }
        val changes = ArrayList<Change>()
        for ((id, result) in byId) {
            val a = before[id]
            val b = result.primary
            if (before.containsKey(id) && a != b) {
                changes += Change(id, result.spec.title, a, b)
            }
        }
        return changes.sortedBy { it.title }
    }
}
