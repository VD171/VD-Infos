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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.HandlerThread
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

object IsoMapsClient {

    data class IsoScan(val maps: MapsScan.Result?, val hooks: String?, val linker: String?, val extra: String?)

    private const val TTL_MS = 20_000L
    private const val NEG_TTL_MS = 5_000L
    private const val SCAN_TIMEOUT_MS = 5_000L
    private const val PREWARM_TIMEOUT_MS = 7_000L
    private const val PREWARM_JOIN_MS = 8_000L
    private val cache = HashMap<String, Pair<Long, IsoScan>>()
    private val negCache = HashMap<String, Long>()
    private val cacheLock = Any()
    private val locks = HashMap<String, Any>()

    private fun lockFor(name: String): Any = synchronized(cacheLock) { locks.getOrPut(name) { Any() } }

    fun prewarm(ctx: Context) {
        val app = ctx.applicationContext
        val threads = listOf(IsoMapsService::class.java, IsoMapsServiceSys::class.java).map { cls ->
            Thread({ runCatching { scan(app, cls, PREWARM_TIMEOUT_MS) } }, "vdz-iso-warm").apply {
                isDaemon = true
                runCatching { priority = Thread.MIN_PRIORITY }
            }
        }
        threads.forEach { it.start() }
        threads.forEach { runCatching { it.join(PREWARM_JOIN_MS) } }
    }

    fun scan(ctx: Context, cls: Class<out IsoMapsService>, timeoutMs: Long = SCAN_TIMEOUT_MS): IsoScan? {
        val key = cls.name
        synchronized(cacheLock) {
            cache[key]?.let { if (System.currentTimeMillis() - it.first < TTL_MS) return it.second }
        }
        synchronized(lockFor(key)) {
            val now = System.currentTimeMillis()
            synchronized(cacheLock) {
                cache[key]?.let { if (now - it.first < TTL_MS) return it.second }
                negCache[key]?.let { if (now < it) return null }
            }
            val r = bindAndScan(ctx.applicationContext, cls, timeoutMs)
            synchronized(cacheLock) {
                if (r != null) { cache[key] = now to r; negCache.remove(key) }
                else negCache[key] = now + NEG_TTL_MS
            }
            return r
        }
    }

    private fun bindAndScan(ctx: Context, cls: Class<out IsoMapsService>, timeoutMs: Long): IsoScan? {
        val latch = CountDownLatch(1)
        val maps = AtomicReference<String?>(null)
        val hooks = AtomicReference<String?>(null)
        val linker = AtomicReference<String?>(null)
        val extra = AtomicReference<String?>(null)
        val replyThread = HandlerThread("vdz-iso-reply").apply { start() }
        val replyHandler = object : Handler(replyThread.looper) {
            override fun handleMessage(msg: Message) {
                maps.set(msg.data?.getString(IsoMapsService.KEY))
                hooks.set(msg.data?.getString(IsoMapsService.KEY_HOOKS))
                linker.set(msg.data?.getString(IsoMapsService.KEY_LINKER))
                extra.set(msg.data?.getString(IsoMapsService.KEY_EXTRA))
                latch.countDown()
            }
        }
        val replyMessenger = Messenger(replyHandler)
        var bound = false
        val conn = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val m = Messenger(service)
                val req = Message.obtain(null, IsoMapsService.MSG_SCAN)
                req.replyTo = replyMessenger
                runCatching { m.send(req) }.onFailure { latch.countDown() }
            }
            override fun onServiceDisconnected(name: ComponentName?) { latch.countDown() }
            override fun onBindingDied(name: ComponentName?) { latch.countDown() }
            override fun onNullBinding(name: ComponentName?) { latch.countDown() }
        }
        return try {
            bound = ctx.bindService(Intent(ctx, cls), conn, Context.BIND_AUTO_CREATE)
            if (!bound) return null
            latch.await(timeoutMs, TimeUnit.MILLISECONDS)
            val m = MapsScan.deserialize(maps.get())
            val h = hooks.get()
            val l = linker.get()
            val x = extra.get()
            if (m == null && h == null && l == null && x == null) null else IsoScan(m, h, l, x)
        } catch (t: Throwable) {
            null
        } finally {
            if (bound) runCatching { ctx.unbindService(conn) }
            replyThread.quitSafely()
        }
    }
}
