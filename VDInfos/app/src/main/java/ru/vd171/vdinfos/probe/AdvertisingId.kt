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

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Parcel
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

object AdvertisingId {

    private const val ACTION = "com.google.android.gms.ads.identifier.service.START"
    private const val GMS = "com.google.android.gms"
    private const val DESCRIPTOR = "com.google.android.gms.ads.identifier.internal.IAdvertisingIdService"
    private const val TX_GET_ID = 1
    private const val TX_LIMIT_AD_TRACKING = 2
    private const val BIND_TIMEOUT_MS = 3000L

    const val ZERO_ID = "00000000-0000-0000-0000-000000000000"

    private class Waiter : ServiceConnection {
        private val slot = LinkedBlockingQueue<IBinder>(1)
        override fun onServiceConnected(name: ComponentName?, service: IBinder) { slot.offer(service) }
        override fun onServiceDisconnected(name: ComponentName?) {}
        fun await(ms: Long): IBinder? = slot.poll(ms, TimeUnit.MILLISECONDS)
    }

    private fun <T> withService(ctx: Context, block: (IBinder) -> T?): T? {
        val app = ctx.applicationContext
        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) return null
        val hasVending = runCatching {
            app.packageManager.getPackageInfo("com.android.vending", 0) != null
        }.getOrDefault(false)
        if (!hasVending) return null
        val conn = Waiter()
        val bound = runCatching {
            app.bindService(Intent(ACTION).setPackage(GMS), conn, Context.BIND_AUTO_CREATE)
        }.getOrDefault(false)
        if (!bound) return null
        return try {
            conn.await(BIND_TIMEOUT_MS)?.let(block)
        } finally {
            runCatching { app.unbindService(conn) }
        }
    }

    private fun <T> transact(binder: IBinder, code: Int, write: (Parcel) -> Unit, read: (Parcel) -> T): T? {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()
        return try {
            data.writeInterfaceToken(DESCRIPTOR)
            write(data)
            binder.transact(code, data, reply, 0)
            reply.readException()
            read(reply)
        } catch (_: Throwable) {
            null
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    fun id(ctx: Context): String? = withService(ctx) { b ->
        transact(b, TX_GET_ID, {}, { it.readString() })
    }

    fun limitAdTracking(ctx: Context): String? = withService(ctx) { b ->
        transact(b, TX_LIMIT_AD_TRACKING, { it.writeInt(1) }, { (it.readInt() != 0).toString() })
    }
}
