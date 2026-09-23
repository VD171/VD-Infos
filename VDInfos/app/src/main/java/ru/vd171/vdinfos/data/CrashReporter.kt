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

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import ru.vd171.vdinfos.BuildConfig
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.exitProcess

object CrashReporter {

    private const val FILE = "last_crash.txt"
    private const val PREFS = "crash"
    private const val KEY_LAST_RELAUNCH = "last_relaunch"

    private const val RELAUNCH_GUARD_MS = 10_000L

    private fun file(ctx: Context) = File(ctx.filesDir, FILE)

    fun install(app: Application) {
        val previous = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, error ->
            runCatching { file(app).writeText(report(thread, error)) }

            val prefs = runCatching { app.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }.getOrNull()
            val now = System.currentTimeMillis()
            val lastRelaunch = prefs?.getLong(KEY_LAST_RELAUNCH, 0L) ?: 0L
            val looping = now - lastRelaunch < RELAUNCH_GUARD_MS

            if (looping) {
                previous?.uncaughtException(thread, error)
                exitProcess(2)
            }

            runCatching { prefs?.edit()?.putLong(KEY_LAST_RELAUNCH, now)?.commit() }
            runCatching {
                val intent = app.packageManager.getLaunchIntentForPackage(app.packageName)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    app.startActivity(intent)
                }
            }
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(2)
        }
    }

    private fun report(thread: Thread, error: Throwable): String {
        val stack = StringWriter().also { error.printStackTrace(PrintWriter(it)) }.toString()
        val stamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        return buildString {
            appendLine("VD Infos ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine(stamp)
            appendLine("Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            appendLine("${Build.MANUFACTURER} ${Build.MODEL} (${Build.DEVICE})")
            appendLine("Thread: ${thread.name}")
            appendLine()
            append(stack)
        }
    }

    fun consume(ctx: Context): String? = runCatching {
        val f = file(ctx)
        if (!f.exists()) return null
        val text = f.readText()
        f.delete()
        text.ifBlank { null }
    }.getOrNull()
}
