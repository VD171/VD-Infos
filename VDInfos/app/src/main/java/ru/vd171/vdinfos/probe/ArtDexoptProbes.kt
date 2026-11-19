/*
 * ======================================================================
 *  VD INFOS  ::  method debugger
 *  Read every device info by every method, then compare.
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
 *  XDA thread     : https://xdaforums.com/t/VD-Infos.4097379/
 *  Telegram       : @VD_Priv8 https://t.me/VD_Priv8
 *  Channel        : @Priv8_VD https://t.me/Priv8_VD
 *  Discord        : @VD.Priv8 https://discord.com/users/1296831918989639721
 *  E-mail         : vd.priv8@pm.me
 * ======================================================================
 */

package ru.vd171.vdinfos.probe

import android.content.Context
import android.os.Build
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask
import java.io.File

object ArtDexoptProbes {
    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        add(
            probe(
                "art.dexopt_self", "ART dexopt (self)", Category.SYSTEM,
                listOf(jm("self base.odex x /proc/self/maps", compare = false) { selfState(it) }),
                note = ctx.getString(R.string.note_art_dexopt_self),
                solution = ctx.getString(R.string.sol_art_dexopt_self),
            )
        )
    }

    private fun isaDir(): String {
        val abi = Build.SUPPORTED_ABIS.firstOrNull() ?: "arm64-v8a"
        return when {
            abi.startsWith("arm64") -> "arm64"
            abi.startsWith("armeabi") -> "arm"
            abi == "x86_64" -> "x86_64"
            abi.startsWith("x86") -> "x86"
            else -> "arm64"
        }
    }

    private fun selfOdex(ctx: Context): File =
        File(File(ctx.applicationInfo.sourceDir).parentFile, "oat/" + isaDir() + "/base.odex")

    private fun odexExecuting(ctx: Context): Boolean = try {
        val pkg = ctx.packageName
        File("/proc/self/maps").bufferedReader().useLines { lines ->
            lines.any { l ->
                l.contains("base.odex") && l.contains(pkg) &&
                    l.indexOf(' ').let { sp -> sp >= 0 && sp + 4 <= l.length && l[sp + 3] == 'x' }
            }
        }
    } catch (_: Throwable) {
        false
    }

    private fun bootImageMapped(): Boolean = try {
        File("/proc/self/maps").bufferedReader().useLines { it.any { l -> l.contains("/boot.art") } }
    } catch (_: Throwable) {
        false
    }

    private fun oatCompilerFilter(odex: File): String? = try {
        val cap = minOf(odex.length(), 64L * 1024).toInt()
        val buf = ByteArray(cap)
        var n = 0
        odex.inputStream().use { input ->
            while (n < cap) {
                val rd = input.read(buf, n, cap - n)
                if (rd <= 0) break
                n += rd
            }
        }
        val key = "compiler-filter".toByteArray(Charsets.US_ASCII)
        var result: String? = null
        var i = 1
        while (i + key.size + 1 < n) {
            if (buf[i - 1].toInt() == 0 && buf[i + key.size].toInt() == 0 &&
                key.indices.all { buf[i + it] == key[it] }
            ) {
                val vs = i + key.size + 1
                var ve = vs
                while (ve < n && buf[ve].toInt() != 0) ve++
                result = if (ve > vs) String(buf, vs, ve - vs, Charsets.US_ASCII) else null
                break
            }
            i++
        }
        result
    } catch (_: Throwable) {
        null
    }

    private fun selfState(ctx: Context): String {
        val odex = selfOdex(ctx)
        val onDisk = odex.exists()
        val size = if (onDisk) odex.length() else 0L
        val filter = if (onDisk) oatCompilerFilter(odex) else null
        val aotFilter = filter != null && (filter.startsWith("speed") || filter.startsWith("everything"))
        val verifyFilter = filter != null && !aotFilter
        val boot = bootImageMapped()
        val exec = odexExecuting(ctx)
        val verdict = when {
            onDisk && exec -> "aot_ok"
            verifyFilter -> "verify_filter"
            aotFilter && boot -> "interp_despite_odex"
            aotFilter -> "no_boot_image"
            !onDisk -> "no_odex"
            else -> "unknown"
        }
        return verdict + " [filter=" + (filter ?: "unknown") +
            " odex=" + (if (onDisk) "yes(" + size + ")" else "no") +
            " exec=" + (if (exec) "yes" else "no") + " boot=" + (if (boot) "yes" else "no") + "]"
    }
}
