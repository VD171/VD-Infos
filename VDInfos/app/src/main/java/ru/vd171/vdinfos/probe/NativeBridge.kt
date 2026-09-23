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

object NativeBridge {

    val available: Boolean

    init {
        available = runCatching { System.loadLibrary("vdinfos") }.isSuccess
    }

    private external fun nSysProp(key: String): String?
    private external fun nUname(): String?
    private external fun nHostname(): String?
    private external fun nIfaceMac(name: String): String?
    private external fun nExec(cmd: String, cap: Int): String?
    private external fun nSysPropClassic(key: String): String?
    private external fun nReadFile(path: String, cap: Int): String?
    private external fun nReadFileOrReason(path: String, cap: Int): String?
    private external fun nExists(path: String): Boolean
    private external fun nReadlink(path: String): String?
    private external fun nIds(): String?
    private external fun nStatfs(path: String): String?
    private external fun nArch(): String?
    private external fun nPropAreaHoles(): String?
    private external fun nRawExists(path: String): Boolean
    private external fun nKernelSpoof(): String?
    private external fun nSelfPath(): String?
    private external fun nStatOwner(path: String): String?
    private external fun nDirZeroWidth(dir: String, ranges: IntArray): String?
    private external fun nSockDiag(): String?

    private inline fun <T> guard(block: () -> T?): T? =
        if (!available) null else runCatching(block).getOrNull()

    fun sysprop(key: String): String? = guard { nSysProp(key)?.takeIf { it.isNotEmpty() } }

    fun syspropClassic(key: String): String? =
        guard { nSysPropClassic(key)?.takeIf { it.isNotEmpty() } }

    fun uname(): Uname? = guard {
        nUname()?.split('\t')?.takeIf { it.size == 5 }?.let {
            Uname(it[0], it[1], it[2], it[3], it[4])
        }
    }

    fun hostname(): String? = guard { nHostname() }

    fun ifaceMac(name: String): String? = guard { nIfaceMac(name) }

    fun readFileOrReason(path: String, cap: Int = 65536): String? =
        runCatching { nReadFileOrReason(path, cap) }.getOrNull()

    fun exec(cmd: String, cap: Int = 8192): String? =
        guard { nExec(cmd, cap)?.takeIf { it.isNotEmpty() } }

    fun readFile(path: String, cap: Int = 65536): String? = guard { nReadFile(path, cap) }

    fun exists(path: String): Boolean = guard { nExists(path) } ?: false

    fun readlink(path: String): String? = guard { nReadlink(path) }

    fun ids(): Ids? = guard {
        nIds()?.split('\t')?.takeIf { it.size == 6 }?.map { it.toInt() }?.let {
            Ids(it[0], it[1], it[2], it[3], it[4], it[5])
        }
    }

    fun statfs(path: String): Statfs? = guard {
        nStatfs(path)?.split('\t')?.takeIf { it.size == 3 }?.map { it.toLong() }?.let {
            Statfs(it[0], it[1], it[2])
        }
    }

    fun arch(): String? = guard { nArch() }

    fun propAreaHoles(): String? = guard { nPropAreaHoles()?.takeIf { it.isNotEmpty() } }

    fun rawExists(path: String): Boolean = guard { nRawExists(path) } ?: false

    fun kernelSpoof(): String? = guard { nKernelSpoof()?.takeIf { it.isNotEmpty() } }

    fun selfPath(): String? = guard { nSelfPath()?.takeIf { it.isNotEmpty() } }

    fun statOwner(path: String): Pair<Int, Int>? = guard {
        nStatOwner(path)?.split('\t')?.takeIf { it.size == 2 }?.let { it[0].toInt() to it[1].toInt() }
    }

    fun dirZeroWidth(dir: String, ranges: IntArray): String? = guard { nDirZeroWidth(dir, ranges) }

    fun sockDiag(): String? = guard { nSockDiag()?.takeIf { it.isNotEmpty() } }

    data class Uname(
        val sysname: String,
        val nodename: String,
        val release: String,
        val version: String,
        val machine: String,
    )

    data class Ids(
        val uid: Int, val euid: Int, val gid: Int,
        val egid: Int, val pid: Int, val ppid: Int,
    )

    data class Statfs(val total: Long, val free: Long, val avail: Long)
}
