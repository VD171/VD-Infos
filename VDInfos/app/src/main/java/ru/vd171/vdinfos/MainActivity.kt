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

package ru.vd171.vdinfos

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import ru.vd171.vdinfos.data.CrashReporter
import ru.vd171.vdinfos.data.LocaleManager
import ru.vd171.vdinfos.ui.HomeScreen
import ru.vd171.vdinfos.ui.components.CrashDialog
import ru.vd171.vdinfos.ui.components.LanguageDialog
import ru.vd171.vdinfos.ui.theme.VdInfosTheme

class MainActivity : ComponentActivity() {

    private val idleFinish = Runnable { finishAndRemoveTask() }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.wrap(newBase))
    }

    override fun onStart() {
        super.onStart()
        idleHandler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) idleHandler.postDelayed(idleFinish, IDLE_FINISH_MS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VdInfosTheme {
                HomeScreen()
                var showLang by remember { mutableStateOf(!LocaleManager.chosen(this)) }
                if (showLang) {
                    LanguageDialog(current = LocaleManager.tag(this)) { tag ->
                        val changed = tag != LocaleManager.tag(this)
                        LocaleManager.save(this, tag)
                        showLang = false
                        if (changed) {
                            this@MainActivity.viewModelStore.clear()
                            recreate()
                        }
                    }
                }
                var crash by remember { mutableStateOf(CrashReporter.consume(this)) }
                crash?.let { report ->
                    CrashDialog(report) { crash = null }
                }
            }
        }
    }

    companion object {
        private const val IDLE_FINISH_MS = 3 * 60 * 1000L
        private val idleHandler = Handler(Looper.getMainLooper())
    }
}
