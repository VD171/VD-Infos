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

import android.app.ActivityManager
import android.content.Context
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES20
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask

object GpuJvm {

    private val cache: List<String>? by lazy { read() }

    fun get(): List<String>? = cache

    private fun read(): List<String>? = runCatching {
        val dpy = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
        if (dpy == EGL14.EGL_NO_DISPLAY) return null
        val ver = IntArray(2)
        if (!EGL14.eglInitialize(dpy, ver, 0, ver, 1)) return null
        try {
            val cfgAttr = intArrayOf(
                EGL14.EGL_SURFACE_TYPE, EGL14.EGL_PBUFFER_BIT,
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_RED_SIZE, 8, EGL14.EGL_GREEN_SIZE, 8, EGL14.EGL_BLUE_SIZE, 8,
                EGL14.EGL_NONE,
            )
            val cfgs = arrayOfNulls<EGLConfig>(1)
            val num = IntArray(1)
            if (!EGL14.eglChooseConfig(dpy, cfgAttr, 0, cfgs, 0, 1, num, 0) || num[0] < 1) return null
            val surf = EGL14.eglCreatePbufferSurface(
                dpy, cfgs[0], intArrayOf(EGL14.EGL_WIDTH, 1, EGL14.EGL_HEIGHT, 1, EGL14.EGL_NONE), 0,
            )
            val ctx = EGL14.eglCreateContext(
                dpy, cfgs[0], EGL14.EGL_NO_CONTEXT,
                intArrayOf(EGL14.EGL_CONTEXT_CLIENT_VERSION, 2, EGL14.EGL_NONE), 0,
            )
            if (ctx == EGL14.EGL_NO_CONTEXT) {
                EGL14.eglDestroySurface(dpy, surf)
                return null
            }
            if (!EGL14.eglMakeCurrent(dpy, surf, surf, ctx)) {
                EGL14.eglDestroyContext(dpy, ctx)
                EGL14.eglDestroySurface(dpy, surf)
                return null
            }
            val out = intArrayOf(
                GLES20.GL_VENDOR, GLES20.GL_RENDERER, GLES20.GL_VERSION, GLES20.GL_SHADING_LANGUAGE_VERSION,
            ).map { GLES20.glGetString(it) ?: "" }
            EGL14.eglMakeCurrent(dpy, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            EGL14.eglDestroyContext(dpy, ctx)
            EGL14.eglDestroySurface(dpy, surf)
            out
        } finally {
            EGL14.eglTerminate(dpy)
        }
    }.getOrNull()
}

object GpuProbes {

    private fun gpuItem(id: String, title: String, idx: Int, extra: List<Method> = emptyList()) =
        probe("gpu:$id", title, Category.HARDWARE, buildList {
            add(jm("EGL14/GLES20 glGetString") { GpuJvm.get()?.getOrNull(idx) })
            add(nm("native EGL glGetString") { NativeBridge.gpuInfo()?.getOrNull(idx) })
            addAll(extra)
        })

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        add(gpuItem("vendor", ctx.getString(R.string.t_gpu_vendor), 0))
        add(gpuItem("renderer", ctx.getString(R.string.t_gpu_renderer), 1))
        add(
            gpuItem(
                "version", ctx.getString(R.string.t_gpu_version), 2,
                listOf(
                    jm("ActivityManager.deviceConfigurationInfo.glEsVersion", compare = false) { c ->
                        (c.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
                            ?.deviceConfigurationInfo?.glEsVersion
                    },
                ),
            ),
        )
        add(gpuItem("glsl", ctx.getString(R.string.t_gpu_glsl), 3))
    }
}
