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

package ru.vd171.vdinfos.iso

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger

open class IsoMapsService : Service() {

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            val reply = msg.replyTo ?: return
            Thread({ runScan(reply) }, "vdz-iso-scan").apply {
                runCatching { priority = Thread.MIN_PRIORITY }
                isDaemon = true
            }.start()
        }
    }

    private fun runScan(reply: Messenger) {
        runCatching {
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND)
        }
        val svc = this
        val maps = runCatching { MapsScan.serialize(MapsScan.scan()) }.getOrNull()
        breathe()
        val hooks = runCatching { ru.vd171.vdinfos.probe.NativeBridge.inlineHookScan() }.getOrNull()
        breathe()
        val linker = runCatching { ru.vd171.vdinfos.probe.NativeBridge.linkerMapsGap() }.getOrNull()
        breathe()
        val suPaths = runCatching {
            ru.vd171.vdinfos.probe.AssetData.packages(svc, "su_binary_paths") +
                ru.vd171.vdinfos.probe.AssetData.packages(svc, "su_path_dirs").map { "$it/su" }
        }.getOrDefault(emptyList())
        val propKeys = runCatching {
            ru.vd171.vdinfos.probe.AssetData.packages(svc, "prop_dual_keys")
        }.getOrDefault(emptyList())
        val extra = runCatching { IsoChecks.extras(suPaths, propKeys) }.getOrNull()
        val out = Message.obtain(null, MSG_RESULT)
        out.data = Bundle().apply {
            putString(KEY, maps); putString(KEY_HOOKS, hooks)
            putString(KEY_LINKER, linker); putString(KEY_EXTRA, extra)
        }
        runCatching { reply.send(out) }
    }

    private fun breathe() {
        runCatching { Thread.sleep(SLICE_PAUSE_MS) }
    }

    private val messenger = Messenger(handler)

    override fun onBind(intent: Intent): IBinder = messenger.binder

    companion object {
        const val MSG_SCAN = 1
        const val MSG_RESULT = 2
        private const val SLICE_PAUSE_MS = 6L
        const val KEY = "r"
        const val KEY_HOOKS = "h"
        const val KEY_LINKER = "l"
        const val KEY_EXTRA = "x"
    }
}
