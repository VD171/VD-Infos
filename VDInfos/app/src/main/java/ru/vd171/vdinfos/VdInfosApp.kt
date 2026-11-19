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

package ru.vd171.vdinfos

import android.app.Application
import android.content.Context
import ru.vd171.vdinfos.data.CrashReporter
import ru.vd171.vdinfos.data.LocaleManager

class VdInfosApp : Application() {

    private fun isolatedProc(): Boolean {
        val appId = android.os.Process.myUid() % 100000
        return appId in 90000..99999
    }

    override fun attachBaseContext(newBase: Context) {
        if (isolatedProc()) { super.attachBaseContext(newBase); return }
        super.attachBaseContext(LocaleManager.wrap(newBase))
    }

    override fun onCreate() {
        super.onCreate()
        if (isolatedProc()) return
        CrashReporter.install(this)
    }
}
