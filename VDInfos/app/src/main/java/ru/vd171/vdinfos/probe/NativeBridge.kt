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

import android.os.Build

object NativeBridge {

    val available: Boolean
    val ks2Available: Boolean

    init {
        available = runCatching { System.loadLibrary("vdinfos") }.isSuccess
        ks2Available = Build.VERSION.SDK_INT >= 33 &&
            runCatching { System.loadLibrary("vdks2") }.isSuccess
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
    private external fun nStatPresence(path: String, method: Int): String?
    private external fun nReadlink(path: String): String?
    private external fun nIds(): String?
    private external fun nStatfs(path: String): String?
    private external fun nArch(): String?
    private external fun nPropAreaHoles(): String?
    private external fun nRawExists(path: String): Boolean
    private external fun nKernelSpoof(): String?
    private external fun nGetGroups(): String?
    private external fun nAtexitDrift(): String?
    private external fun nSelfPath(): String?
    private external fun nLibcIdentity(): String?
    private external fun nStatOwner(path: String): String?
    private external fun nStatMode(path: String): String?
    private external fun nInlineHookScan(): String?
    private external fun nLinkerMapsGap(): String?
    private external fun nDirZeroWidth(dir: String, ranges: IntArray): String?
    private external fun nSockDiag(): String?
    private external fun nStatMeta(path: String): String?
    private external fun nGpuInfo(): String?
    private external fun nPropList(): ByteArray?
    private external fun nDlPhdr(): String?
    private external fun nFridaPorts(): String?
    private external fun nProcNetScan(): String?
    private external fun nMapsDeletedExec(): String?
    private external fun nAnonHoles(): String?
    private external fun nAnonExecElf(): String?
    private external fun nNamedAnonElf(): String?
    private external fun nAnonElfStruct(): String?
    private external fun nZygiskFootprint(): String?
    private external fun nHeapHookXref(): String?
    private external fun nHiddenRegionXref(): String?
    private external fun nGapMapped(): String?
    private external fun nDirList(dir: String): String?
    private external fun nAttestNative(challenge: ByteArray): ByteArray?
    private external fun nUidPriorityScan(uids: IntArray): String?
    private external fun nUidPriorityScanSel(uids: IntArray, sel: Int): String?
    private external fun nUidLiveScan(start: Int, end: Int): IntArray?
    private external fun nUidLiveScanSel(start: Int, end: Int, sel: Int): IntArray?

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

    fun statPresence(path: String, method: Int): String = guard { nStatPresence(path, method) } ?: "n/a"

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

    fun getGroups(): String? = guard { nGetGroups()?.takeIf { it.isNotEmpty() && it != "err" } }
    fun atexitDrift(): String? = guard { nAtexitDrift()?.takeIf { it.isNotEmpty() } }
    fun uidPriorityScan(uids: IntArray): String? = guard { nUidPriorityScan(uids)?.takeIf { it.isNotEmpty() } }
    fun uidPriorityScanSel(uids: IntArray, sel: Int): String? = guard { nUidPriorityScanSel(uids, sel) }
    fun uidLiveScan(start: Int, end: Int): IntArray? = guard { nUidLiveScan(start, end) }
    fun uidLiveScanSel(start: Int, end: Int, sel: Int): IntArray? = guard { nUidLiveScanSel(start, end, sel) }
    fun selfPath(): String? = guard { nSelfPath()?.takeIf { it.isNotEmpty() } }
    fun libcIdentity(): String? = guard { nLibcIdentity()?.takeIf { it.isNotEmpty() && !it.startsWith("n/a") } }

    fun statOwner(path: String): Pair<Int, Int>? = guard {
        nStatOwner(path)?.split('\t')?.takeIf { it.size == 2 }?.let { it[0].toInt() to it[1].toInt() }
    }

    fun statMode(path: String): Triple<Int, Int, Int>? = guard {
        nStatMode(path)?.split('\t')?.takeIf { it.size == 3 }?.let { Triple(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
    }

    fun inlineHookScan(): String? = guard { nInlineHookScan()?.takeIf { it.isNotEmpty() } }

    fun linkerMapsGap(): String? = guard { nLinkerMapsGap()?.takeIf { it.isNotEmpty() } }

    fun dirZeroWidth(dir: String, ranges: IntArray): String? = guard { nDirZeroWidth(dir, ranges) }

    fun sockDiag(): String? = guard { nSockDiag()?.takeIf { it.isNotEmpty() } }

    fun statMeta(path: String): Pair<Long, Long>? = guard {
        nStatMeta(path)?.split('\t')?.takeIf { it.size == 2 }?.let { it[0].toLong() to it[1].toLong() }
    }

    private val gpuCache: List<String>? by lazy {
        guard { nGpuInfo()?.split('\t')?.takeIf { it.size == 4 } }
    }
    fun gpuInfo(): List<String>? = gpuCache

    data class DlPhdr(val hitCount: Int, val hits: String, val outsideCount: Int, val outside: String)

    fun dlPhdr(): DlPhdr? = guard {
        nDlPhdr()?.split('\t')?.takeIf { it.size == 4 }?.let {
            DlPhdr(it[0].toIntOrNull() ?: 0, it[1], it[2].toIntOrNull() ?: 0, it[3])
        }
    }

    data class MapsExec(val delCount: Int, val delHits: String, val outCount: Int, val outHits: String, val benign: Int)

    fun mapsDeletedExec(): MapsExec? = guard {
        nMapsDeletedExec()?.split('\t')?.takeIf { it.size == 5 }?.let {
            MapsExec(it[0].toIntOrNull() ?: 0, it[1], it[2].toIntOrNull() ?: 0, it[3], it[4].toIntOrNull() ?: 0)
        }
    }

    data class AnonHoles(val stableCount: Int, val holes: String, val churnRuns: Int, val churnMinors: Int, val window: Int, val rawCount: Int)

    fun anonHoles(): AnonHoles? = guard {
        nAnonHoles()?.split('\t')?.takeIf { it.size == 6 }?.let {
            AnonHoles(it[0].toIntOrNull() ?: 0, it[1], it[2].toIntOrNull() ?: 0, it[3].toIntOrNull() ?: 0, it[4].toIntOrNull() ?: 0, it[5].toIntOrNull() ?: 0)
        }
    }

    fun dirList(dir: String): String? = guard { nDirList(dir) }

    data class AnonExecElf(val anonExecCount: Int, val elfAnonCount: Int, val elfHits: String, val anonExecHits: String)

    fun anonExecElf(): AnonExecElf? = guard {
        nAnonExecElf()?.split('\t')?.takeIf { it.size == 4 }?.let {
            AnonExecElf(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0, it[2], it[3])
        }
    }

    data class NamedAnonElf(val elfNamedCount: Int, val execNamedCount: Int, val elfHits: String, val names: String)

    data class AnonElfStruct(val scrubbedCount: Int, val execAnonCount: Int, val hits: String)

    fun anonElfStruct(): AnonElfStruct? = guard {
        nAnonElfStruct()?.split('\t')?.takeIf { it.size == 3 }?.let {
            AnonElfStruct(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0, it[2])
        }
    }

    fun namedAnonElf(): NamedAnonElf? = guard {
        nNamedAnonElf()?.split('\t')?.takeIf { it.size == 4 }?.let {
            NamedAnonElf(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0, it[2], it[3])
        }
    }

    data class ZygiskFootprint(val memfdExecSusp: Int, val zygiskHits: Int, val memfdExecTotal: Int, val memfdNames: String, val zygiskPaths: String)

    fun zygiskFootprint(): ZygiskFootprint? = guard {
        nZygiskFootprint()?.split('\t')?.takeIf { it.size == 5 }?.let {
            ZygiskFootprint(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0, it[2].toIntOrNull() ?: 0, it[3], it[4])
        }
    }

    data class HeapHookXref(val xrefs: Int, val suspectExecCount: Int, val hitRegions: String)

    fun heapHookXref(): HeapHookXref? = guard {
        nHeapHookXref()?.split('\t')?.takeIf { it.size == 3 }?.let {
            HeapHookXref(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0, it[2])
        }
    }

    data class HiddenRegionXref(val hiddenPtrs: Int, val hiddenPages: Int, val pages: String)

    fun hiddenRegionXref(): HiddenRegionXref? = guard {
        nHiddenRegionXref()?.split('\t')?.takeIf { it.size == 3 }?.let {
            HiddenRegionXref(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0, it[2])
        }
    }

    data class GapMapped(val gapMapped: Int, val gapElf: Int, val pages: Int, val gapsScanned: Int, val addrs: String)

    fun gapMapped(): GapMapped? = guard {
        nGapMapped()?.split('\t')?.takeIf { it.size == 5 }?.let {
            GapMapped(it[0].toIntOrNull() ?: 0, it[1].toIntOrNull() ?: 0, it[2].toIntOrNull() ?: 0,
                      it[3].toIntOrNull() ?: 0, it[4])
        }
    }

    fun attestNative(challenge: ByteArray): ByteArray? =
        if (!ks2Available) null else runCatching { nAttestNative(challenge) }.getOrNull()

    data class ProcNet(val fridaPorts: String, val tunIfaces: String)

    fun procNetScan(): ProcNet? = guard {
        nProcNetScan()?.split('\t')?.takeIf { it.size == 2 }?.let { ProcNet(it[0], it[1]) }
    }

    fun fridaPorts(): List<Pair<Int, Boolean>>? = guard {
        nFridaPorts()?.split('\t')?.mapNotNull { seg ->
            val p = seg.split(':')
            if (p.size == 2) (p[0].toIntOrNull() ?: return@mapNotNull null) to (p[1] == "open") else null
        }
    }

    fun propList(): Map<String, String>? = guard {
        nPropList()?.toString(Charsets.UTF_8)?.lineSequence()?.mapNotNull { l ->
            val i = l.indexOf('\t')
            if (i <= 0) null else l.substring(0, i) to l.substring(i + 1)
        }?.toMap()
    }

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
