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
import android.provider.Settings
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Lens
import ru.vd171.vdinfos.core.model.ProbeSpec
import ru.vd171.vdinfos.engine.ProbeTask
import ru.vd171.vdinfos.engine.measure

class Method(
    val lens: Lens, val source: String, val compare: Boolean = true,
    val cmd: String? = null,
    val refusalIsValue: Boolean = false,
    val read: (Context) -> String?,
)

fun jm(source: String, compare: Boolean = true, read: (Context) -> String?) =
    Method(Lens.JAVA, source, compare, read = read)

fun nm(source: String, compare: Boolean = true, read: () -> String?) =
    Method(Lens.NATIVE, source, compare) { read() }

fun sm(cmd: String, source: String = "sh: $cmd", compare: Boolean = true) =
    Method(Lens.SHELL, source, compare, cmd, refusalIsValue = false) { Exec.run(cmd) }

fun smr(cmd: String, source: String = "sh: $cmd", compare: Boolean = true, timeoutMs: Long = 3000) =
    Method(Lens.SHELL, source, compare, cmd, refusalIsValue = true) { Exec.runOrReason(cmd, timeoutMs) }

fun nsm(cmd: String, source: String = "popen: $cmd", compare: Boolean = true, refusalIsValue: Boolean = true) =
    Method(Lens.SHELL_NATIVE, source, compare, cmd, refusalIsValue) {
        if (refusalIsValue) Exec.normalise(NativeBridge.exec(cmd))
        else Exec.nullIfRefusal(NativeBridge.exec(cmd))
    }

fun probe(
    id: String, title: String, cat: Category,
    methods: List<Method>,
    sensitive: Boolean = false, note: String? = null, solution: String? = null,
): ProbeTask {
    val jaTem = methods.filter { it.lens == Lens.SHELL_NATIVE }.mapNotNull { it.cmd }.toSet()
    val full = methods.flatMap { m ->
        if (m.lens == Lens.SHELL && m.cmd != null && m.cmd !in jaTem) {
            listOf(m, nsm(m.cmd, "popen: ${m.source}", m.compare, m.refusalIsValue))
        } else listOf(m)
    }
    return ProbeTask(ProbeSpec(id, title, cat, full.map { it.lens }.toSet(), note, sensitive, solution)) { ctx ->
        full.map { m -> measure(m.lens, m.source, m.compare) { m.read(ctx) } }
    }
}

fun jprobe(
    id: String, title: String, cat: Category,
    sensitive: Boolean = false, source: String = title, note: String? = null,
    block: (Context) -> String?,
) = probe(id, title, cat, listOf(jm(source, read = block)), sensitive, note)

fun propItem(key: String, cat: Category, sensitive: Boolean = false) = probe(
    id = "prop:$key", title = key, cat = cat,
    methods = listOf(
        jm("SystemProperties.get") { SystemProps.get(key) },
        nm("__system_property_read_callback") { NativeBridge.sysprop(key) },
        nm("__system_property_get (92-byte API)") { NativeBridge.syspropClassic(key) },
        sm("getprop $key", "getprop"),
        nsm("getprop $key", "popen: getprop"),
    ),
    sensitive = sensitive,
)

fun propTrio(
    key: String, compare: Boolean = true,
    map: ((String?) -> String?)? = null,
): List<Method> {
    val f: (String?) -> String? = map ?: { it }
    val cmd = "getprop $key"
    return listOf(
        jm("SystemProperties $key", compare) { f(SystemProps.get(key)) },
        nm("read_callback $key", compare) { f(NativeBridge.sysprop(key)) },
        nm("property_get $key (92B)", compare) { f(NativeBridge.syspropClassic(key)) },
        Method(Lens.SHELL, "getprop $key", compare, cmd) { f(Exec.run(cmd)) },
        Method(Lens.SHELL_NATIVE, "popen: getprop $key", compare, cmd) {
            f(Exec.normalise(NativeBridge.exec(cmd)))
        },
    )
}

fun settingViaProvider(c: Context, key: String): String? {
    for (store in listOf("secure", "global", "system")) {
        val v = runCatching {
            c.contentResolver.query(
                android.net.Uri.parse("content://settings/$store"),
                arrayOf("name", "value"), "name=?", arrayOf(key), null,
            )?.use { cur ->
                if (cur.moveToFirst()) cur.getString(cur.getColumnIndexOrThrow("value")) else null
            }
        }.getOrNull()
        if (!v.isNullOrEmpty()) return v
    }
    return null
}

fun settingViaAppended(c: Context, key: String): String? {
    for (store in listOf("secure", "global", "system")) {
        val v = runCatching {
            c.contentResolver.query(
                android.net.Uri.parse("content://settings/$store/$key"),
                arrayOf("name", "value"), null, null, null,
            )?.use { cur -> if (cur.moveToFirst()) cur.getString(cur.getColumnIndexOrThrow("value")) else null }
        }.getOrNull()
        if (!v.isNullOrEmpty()) return v
    }
    return null
}

fun settingViaBulk(c: Context, key: String): String? {
    for (store in listOf("secure", "global", "system")) {
        val v = runCatching {
            c.contentResolver.query(
                android.net.Uri.parse("content://settings/$store"),
                arrayOf("name", "value"), null, null, null,
            )?.use { cur ->
                val ni = cur.getColumnIndex("name"); val vi = cur.getColumnIndex("value")
                var out: String? = null
                while (cur.moveToNext()) if (ni >= 0 && cur.getString(ni) == key) {
                    out = if (vi >= 0) cur.getString(vi) else null; break
                }
                out
            }
        }.getOrNull()
        if (!v.isNullOrEmpty()) return v
    }
    return null
}

fun settingViaCall(c: Context, key: String): String? {
    for (store in listOf("global", "secure", "system")) {
        val v = runCatching {
            c.contentResolver.call(android.net.Uri.parse("content://settings"), "GET_$store", key, null)
                ?.getString("value")
        }.getOrNull()
        if (!v.isNullOrEmpty()) return v
    }
    return null
}

fun settingBlock(key: String, compare: Boolean = true) = listOf(
    jm("Settings.Secure $key", compare) { c -> Settings.Secure.getString(c.contentResolver, key) },
    @Suppress("DEPRECATION")
    jm("Settings.Global $key", compare) { c -> Settings.Global.getString(c.contentResolver, key) },
    @Suppress("DEPRECATION")
    jm("Settings.System $key", compare) { c -> Settings.System.getString(c.contentResolver, key) },
    jm("provider settings/* $key (selection name=?)", compare) { c -> settingViaProvider(c, key) },
    jm("query appended-path $key", compare) { c -> settingViaAppended(c, key) },
    jm("query bulk-all $key", compare) { c -> settingViaBulk(c, key) },
    jm("provider call GET_<store> $key", compare) { c -> settingViaCall(c, key) },
    smr("{ settings get global $key; settings get secure $key; settings get system $key; } " +
        "| grep -v '^null$' | head -1", "settings get 3-stores $key", compare = false),
)

fun am(source: String, compare: Boolean = true, read: () -> String?) =
    Method(Lens.ATTEST, source, compare) { read() }
