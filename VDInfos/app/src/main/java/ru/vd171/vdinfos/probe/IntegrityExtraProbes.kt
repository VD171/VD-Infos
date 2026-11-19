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
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Process
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.core.model.Sentinels
import ru.vd171.vdinfos.engine.ProbeTask
import java.io.File

object IntegrityExtraProbes {

    private class ConcealDoors(
        val pkg: String, val listed: Boolean, val pi: Boolean,
        val ai: Boolean, val ctx: Boolean, val apk: Boolean,
        val launch: Boolean, val resolve: Boolean,
    ) {
        val hiddenButReachable get() = !listed && (pi || ai || ctx || apk || launch || resolve)
        val matrix get() = "$pkg L${b(listed)}P${b(pi)}A${b(ai)}C${b(ctx)}Z${b(apk)}I${b(launch)}R${b(resolve)}" +
            if (hiddenButReachable) "  LEAK" else ""
        private fun b(v: Boolean) = if (v) 1 else 0
    }

    private fun concealScan(ctx: Context): List<ConcealDoors> {
        val pm = ctx.packageManager
        val listed = runCatching { pm.getInstalledPackages(0).map { it.packageName }.toHashSet() }
            .getOrDefault(hashSetOf())
        return AssetData.packages(ctx, "conceal_targets").mapNotNull { pkg ->
            val isListed = pkg in listed
            val pi = runCatching { pm.getPackageInfo(pkg, 0); true }.getOrDefault(false)
            val ai = runCatching { pm.getApplicationInfo(pkg, 0); true }.getOrDefault(false)
            var ctxDoor = false
            var src: String? = null
            runCatching {
                val pc = ctx.createPackageContext(pkg, 0)
                ctxDoor = true
                src = pc.applicationInfo?.sourceDir
            }
            val apk = src?.let { runCatching { java.util.zip.ZipFile(it).close(); true }.getOrDefault(false) } ?: false
            val launch = runCatching { pm.getLaunchIntentForPackage(pkg) != null }.getOrDefault(false)
            val resolve = runCatching {
                pm.queryIntentActivities(android.content.Intent(android.content.Intent.ACTION_MAIN).setPackage(pkg), 0).isNotEmpty()
            }.getOrDefault(false)
            val d = ConcealDoors(pkg, isListed, pi, ai, ctxDoor, apk, launch, resolve)
            if (isListed || pi || ai || ctxDoor || apk || launch || resolve) d else null
        }
    }

    private val DATADIR_ROOTS = listOf("/data/data", "/data/user/0", "/data/user_de/0")

    private val STAT_FAMILY: List<Pair<Int, String>> = listOf(
        0 to "statx", 1 to "fstatat", 2 to "faccessat", 3 to "openat2", 4 to "readlinkat",
        5 to "statvfs", 6 to "stat64", 7 to "faccessat2", 8 to "stat", 9 to "lstat",
        10 to "access", 11 to "statfs", 12 to "lstat64", 13 to "fstatat_nofollow",
        14 to "open_O_PATH+fstat", 15 to "euidaccess", 16 to "faccessat_AT_EACCESS",
        17 to "statfs64", 18 to "name_to_handle_at", 19 to "raw_newfstatat",
        20 to "raw_newfstatat_nofollow", 21 to "raw_faccessat", 22 to "raw_statfs",
        23 to "raw_statx", 24 to "raw_openat", 25 to "raw_readlinkat",
    )

    private val TARGET_PKG_LISTS = listOf(
        "conceal_targets", "rooted_apps", "root_manager_apps", "root_tools_apps",
        "root_detector_apps", "root_backup_tools_apps", "xposed_manager_apps", "hiding_apps",
        "suspicious_apps", "analysis_emulator_apps", "install_source_sideloaders", "install_source_stores",
    )

    private fun targetPackages(c: Context): List<String> =
        TARGET_PKG_LISTS.flatMap { AssetData.packages(c, it) }.distinct()

    private fun fsPresent(pkg: String): Boolean =
        DATADIR_ROOTS.any { root ->
            STAT_FAMILY.any { (m, _) ->
                val s = NativeBridge.statPresence("$root/$pkg", m)
                s == "present" || s == "present(denied)"
            }
        }

    private fun pmVisible(c: Context, pkg: String, listed: Set<String>): Boolean {
        if (pkg in listed) return true
        val pm = c.packageManager
        if (runCatching { pm.getPackageInfo(pkg, 0); true }.getOrDefault(false)) return true
        if (runCatching { pm.getApplicationInfo(pkg, 0); true }.getOrDefault(false)) return true
        if (runCatching { c.createPackageContext(pkg, 0); true }.getOrDefault(false)) return true
        if (runCatching { pm.getLaunchIntentForPackage(pkg) != null }.getOrDefault(false)) return true
        return false
    }

    private fun installedSet(c: Context): Set<String> =
        runCatching { c.packageManager.getInstalledPackages(0).map { it.packageName }.toHashSet() }
            .getOrDefault(hashSetOf())

    private class OracleScan(
        val present: Set<String>, val leak: List<String>, val divergent: List<String>,
        val perMethod: Array<out Set<String>>, val absentMethod: Array<out Set<String>>,
    )

    private const val IDX_READLINKAT = 4
    private val STAT_CORE_IDX = listOf(0, 1, 2, 3)

    private fun readlinkatIncompleteHide(s: OracleScan): List<String> =
        s.perMethod[IDX_READLINKAT].filter { pkg -> STAT_CORE_IDX.all { pkg in s.absentMethod[it] } }.sorted()

    private val COMMON_USER_IDS = listOf(0, 1) + (10..20).toList() + listOf(100, 150, 999)

    private fun userManagerKnownIds(c: Context): Set<Int> = runCatching {
        val um = c.getSystemService(Context.USER_SERVICE) as? android.os.UserManager
        um?.userProfiles?.mapNotNull { Regex("""\d+""").find(it.toString())?.value?.toIntOrNull() }?.toSet()
    }.getOrNull() ?: setOf(0)

    private fun scanOracle(c: Context): OracleScan {
        val targets = targetPackages(c)
        val listed = installedSet(c)
        val present = sortedSetOf<String>()
        val avail = BooleanArray(STAT_FAMILY.size)
        val perMethod = Array(STAT_FAMILY.size) { sortedSetOf<String>() }
        val absentMethod = Array(STAT_FAMILY.size) { sortedSetOf<String>() }
        for (pkg in targets) {
            for (root in DATADIR_ROOTS) {
                STAT_FAMILY.forEachIndexed { idx, (m, _) ->
                    when (NativeBridge.statPresence("$root/$pkg", m)) {
                        "present", "present(denied)" -> { avail[idx] = true; perMethod[idx].add(pkg); present.add(pkg) }
                        "absent" -> { avail[idx] = true; absentMethod[idx].add(pkg) }
                    }
                }
            }
        }
        val divergent = STAT_FAMILY.mapIndexedNotNull { idx, (_, label) ->
            if (avail[idx] && !perMethod[idx].containsAll(present)) label else null
        }
        val leak = targets.filter { !pmVisible(c, it, listed) && it in present }.sorted()
        return OracleScan(present, leak, divergent, perMethod, absentMethod)
    }

    private val CONTROL_APPS = listOf(
        "com.google.android.gms", "com.android.vending", "com.android.settings",
    )

    private fun controlAppsPresence(): String =
        CONTROL_APPS.joinToString("  ") { pkg ->
            val s = DATADIR_ROOTS.firstNotNullOfOrNull { root ->
                NativeBridge.statPresence("$root/$pkg", 8).takeIf { it == "present" || it == "present(denied)" || it == "absent" }
            } ?: "n/a"
            "$pkg=$s"
        }

    private fun oracleLiveness(): String {
        val bogus = "nonexistent." + System.nanoTime().toString(16)
        var sawAbsent = false
        var deniedCount = 0
        var ran = 0
        for ((m, _) in STAT_FAMILY) {
            when (NativeBridge.statPresence("/data/data/$bogus", m)) {
                "absent" -> { sawAbsent = true; ran++ }
                "present", "present(denied)" -> { deniedCount++; ran++ }
            }
        }
        return if (sawAbsent) "live" else "dead ($deniedCount/$ran denied)"
    }


    private fun readGateApplies(ctx: Context): Boolean =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ctx.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.S

    private val pkgNameRegex = Regex("^[A-Za-z][A-Za-z0-9_]*(\\.[A-Za-z0-9_]+)+$")
    private val nonPkgAsset = Regex("prop|setting|cert|selinux|_path|_dir|needle|iface|char_range|feature|env_var|partition|policy|question|keys|mac_")

    private fun knownPackageUids(ctx: Context): Map<Int, String> {
        val pm = ctx.packageManager
        val names = LinkedHashSet<String>()
        val files = runCatching { ctx.assets.list("data") }.getOrNull().orEmpty()
        for (f in files) {
            if (!f.endsWith(".txt") || nonPkgAsset.containsMatchIn(f)) continue
            for (entry in AssetData.packages(ctx, f.removeSuffix(".txt"))) {
                val tok = entry.substringBefore(' ').trim()
                if (pkgNameRegex.matches(tok)) names += tok
            }
        }
        val out = HashMap<Int, String>()
        for (name in names) {
            val uid = runCatching { pm.getApplicationInfo(name, 0).uid }.getOrNull()
                ?: runCatching { pm.getPackageInfo(name, 0).applicationInfo?.uid }.getOrNull()
                ?: runCatching { ctx.createPackageContext(name, 0).applicationInfo?.uid }.getOrNull()
            if (uid != null) out.putIfAbsent(uid, name)
        }
        return out
    }

    private fun userBase(): Int = (android.os.Process.myUid() / 100000) * 100000

    private fun hiddenRunningByLens(c: Context, sel: Int): String? {
        val base = userBase()
        val live = NativeBridge.uidLiveScanSel(base + 10000, base + 19999, sel) ?: return null
        val pm = c.packageManager
        val hidden = live.filter { uid ->
            val pkgs = runCatching { pm.getPackagesForUid(uid) }.getOrNull()
            val name = runCatching { pm.getNameForUid(uid) }.getOrNull()
            (pkgs == null || pkgs.isEmpty()) && name.isNullOrEmpty()
        }
        if (hidden.isEmpty()) return "clean"
        val known = knownPackageUids(c)
        return hidden.joinToString(" ") { uid -> known[uid]?.let { "$it(uid=$uid)" } ?: "uid=$uid" } +
            "  (${hidden.size} hidden of ${live.size} live)"
    }

    private fun liveCountRange(classStart: Int, classEnd: Int): String? {
        val base = userBase()
        val live = NativeBridge.uidLiveScanSel(base + classStart, base + classEnd, 0) ?: return null
        return if (live.isEmpty()) "none" else "${live.size} live"
    }

    private fun liveCountOtherProfiles(c: Context): String? {
        val um = c.getSystemService(Context.USER_SERVICE) as? android.os.UserManager ?: return null
        val self = android.os.Process.myUid() / 100000
        val others = runCatching { um.userProfiles }.getOrNull().orEmpty()
            .map { it.hashCode() }.filter { it != self }.distinct()
        if (others.isEmpty()) return "none visible"
        return others.joinToString(" ") { uid ->
            val b = uid * 100000
            val n = NativeBridge.uidLiveScanSel(b + 10000, b + 19999, 0)?.size ?: -1
            "user$uid=${if (n < 0) "n/a" else n.toString()}"
        }
    }

    private fun priorityFakeByLens(ctx: Context, sel: Int): String? {
        val uids = runCatching {
            ctx.packageManager.getInstalledApplications(0).map { it.uid }.toSortedSet().toIntArray()
        }.getOrDefault(IntArray(0))
        if (uids.isEmpty()) return "clean"
        return NativeBridge.uidPriorityScanSel(uids, sel) ?: "clean"
    }

    private fun flagMods(text: String?, needles: List<String>): String? {
        if (text == null) return null
        val hits = text.lineSequence().mapNotNull { line ->
            val name = line.substringBefore(' ').trim()
            if (name.isNotEmpty() && needles.any { name.contains(it, ignoreCase = true) }) name else null
        }.toList()
        return if (hits.isEmpty()) Sentinels.ABSENT else hits.joinToString(", ")
    }

    private fun pathTampered(p: String?): Boolean {
        if (p.isNullOrEmpty()) return false
        return p.contains("(deleted)") || p.startsWith("/memfd:") || p.contains("memfd:") ||
            p.startsWith("/data/adb") || p.startsWith("/data/local/tmp")
    }

    private fun uidVerdict(uid: Int, owner: Int?): String {
        val system = uid in 0 until 10000
        val clone = uid / 100000 >= 1
        val mismatch = owner != null && owner >= 0 && owner != uid
        val bad = system || mismatch
        return "uid=$uid owner=${owner ?: "?"} clone=${if (clone) "yes" else "no"} " +
            "system=${if (system) "yes" else "no"} => ${if (bad) "MISMATCH" else "OK"}"
    }

    private fun hasInvisibleName(name: String, ranges: IntArray): Boolean {
        var i = 0
        while (i < name.length) {
            val cp = name.codePointAt(i)
            var j = 0
            while (j + 1 < ranges.size) {
                if (cp in ranges[j]..ranges[j + 1]) return true
                j += 2
            }
            i += Character.charCount(cp)
        }
        return false
    }

    private fun jvmInvisible(path: String, ranges: IntArray): String {
        val d = File(path)
        if (!d.exists()) return "ENOENT"
        val names = d.list() ?: return Sentinels.EACCES
        return if (names.any { hasInvisibleName(it, ranges) }) "1" else "0"
    }

    private fun aggInvisible(dirs: List<String>, scan: (String) -> String?): String {
        var sawClean = false
        for (dir in dirs) {
            when (scan(dir)) {
                "1" -> return "1"
                "0" -> sawClean = true
                else -> {}
            }
        }
        return if (sawClean) "0" else Sentinels.EACCES
    }

    private val SIGNING_KEY_PROPS = listOf(
        "ro.build.tags", "ro.build.keys", "ro.bootimage.keys", "ro.product.keys",
        "ro.system.keys", "ro.system_ext.keys", "ro.vendor.keys", "ro.odm.keys",
        "ro.vendor_dlkm.keys", "ro.odm_dlkm.keys", "ro.oem.keys",
    )

    private fun signingKeysScan(): String {
        val bad = linkedSetOf<String>()
        for (k in SIGNING_KEY_PROPS) {
            val reads = listOf(
                "prop" to SystemProps.get(k),
                "native" to NativeBridge.sysprop(k),
                "getprop" to Exec.run("getprop $k"),
            )
            for ((src, raw) in reads) {
                val v = raw?.trim()?.lowercase() ?: continue
                if (v.isEmpty()) continue
                if (v != "release-keys") bad.add("$k=$v($src)")
            }
        }
        return if (bad.isEmpty()) "release-keys" else "NON-RELEASE ${bad.joinToString(" ")}"
    }

    private fun signingKeysRaw(): String {
        val present = SIGNING_KEY_PROPS.mapNotNull { k ->
            SystemProps.get(k)?.trim()?.takeIf { it.isNotEmpty() }?.let { "$k=$it" }
        }
        return if (present.isEmpty()) "none set" else present.joinToString(" ")
    }

    private val LOCK_PROPS = listOf(
        "ro.boot.flash.locked", "ro.boot.verifiedbootstate", "ro.boot.vbmeta.device_state",
        "ro.secureboot.lockstate", "ro.secureboot.devicelock", "ro.boot.secureboot",
        "ro.boot.realme.lockstate", "ro.boot.realmebootstate",
        "ro.boot.warranty_bit", "ro.warranty_bit", "ro.vendor.warranty_bit",
        "ro.vendor.boot.warranty_bit", "ro.is_ever_orange",
    )

    private fun lpRead(k: String): String? =
        (SystemProps.get(k)?.trim()?.ifEmpty { null }) ?: (NativeBridge.sysprop(k)?.trim()?.ifEmpty { null })

    private fun lockNorm(key: String, raw: String): String? = when (val v = raw.trim().lowercase()) {
        "" -> null
        else -> when (key) {
            "ro.boot.flash.locked", "ro.boot.secureboot" ->
                when (v) { "1" -> "locked"; "0" -> "unlocked"; else -> null }
            "ro.boot.verifiedbootstate", "ro.boot.realmebootstate" ->
                when (v) { "green" -> "locked"; "orange", "yellow", "red" -> "unlocked"; else -> null }
            "ro.boot.vbmeta.device_state", "ro.secureboot.lockstate", "ro.secureboot.devicelock" ->
                when (v) { "locked", "1" -> "locked"; "unlocked", "0" -> "unlocked"; else -> null }
            "ro.boot.realme.lockstate" ->
                when (v) { "1" -> "locked"; "0" -> "unlocked"; else -> null }
            "ro.boot.warranty_bit", "ro.warranty_bit", "ro.vendor.warranty_bit",
            "ro.vendor.boot.warranty_bit", "ro.is_ever_orange" ->
                when (v) { "0" -> "locked"; "1" -> "unlocked"; else -> null }
            else -> null
        }
    }

    private fun lockStateScan(): String {
        val states = LOCK_PROPS.mapNotNull { k -> lpRead(k)?.let { lockNorm(k, it) }?.let { k to it } }
        if (states.size <= 1) return "coherent"
        val set = states.map { it.second }.toSet()
        return if (set.size <= 1) "coherent"
        else "INCOHERENT " + states.joinToString(" ") { "${it.first}=${it.second}" }
    }

    private fun lockStateRaw(): String {
        val present = (LOCK_PROPS + "ro.boot.veritymode").mapNotNull { k ->
            lpRead(k)?.let { raw -> "$k=$raw" + (lockNorm(k, raw)?.let { "($it)" } ?: "") }
        }
        return if (present.isEmpty()) "none set" else present.joinToString(" ")
    }

    private fun sha256Hex(b: ByteArray): String =
        java.security.MessageDigest.getInstance("SHA-256").digest(b).joinToString("") { "%02x".format(it) }

    @Suppress("DEPRECATION", "PackageManagerGetSignatures")
    private fun certSet(pi: PackageInfo?): String {
        if (pi == null) return ""
        val set = sortedSetOf<String>()
        if (Build.VERSION.SDK_INT >= 28)
            pi.signingInfo?.apkContentsSigners?.forEach { set.add(sha256Hex(it.toByteArray())) }
        else
            pi.signatures?.forEach { set.add(sha256Hex(it.toByteArray())) }
        return set.joinToString(",")
    }

    private fun selfSigFlag(): Int =
        if (Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES
        else @Suppress("DEPRECATION") PackageManager.GET_SIGNATURES

    private fun selfSigPms(c: Context): String? = runCatching {
        certSet(c.packageManager.getPackageInfo(c.packageName, selfSigFlag())).ifEmpty { null }
    }.getOrNull()

    private fun selfSigApk(c: Context): String? = runCatching {
        certSet(c.packageManager.getPackageArchiveInfo(c.applicationInfo.sourceDir, selfSigFlag())).ifEmpty { null }
    }.getOrNull()

    private fun selfCheckSig(c: Context): String {
        val r = runCatching { c.packageManager.checkSignatures(c.packageName, c.packageName) }.getOrNull()
        return if (r == PackageManager.SIGNATURE_MATCH) "match" else "result=$r"
    }

    @Suppress("DEPRECATION", "PackageManagerGetSignatures")
    private fun certSetOf(pi: PackageInfo?): Set<String> {
        if (pi == null) return emptySet()
        val set = sortedSetOf<String>()
        if (Build.VERSION.SDK_INT >= 28)
            pi.signingInfo?.apkContentsSigners?.forEach { set.add(sha256Hex(it.toByteArray())) }
        else
            pi.signatures?.forEach { set.add(sha256Hex(it.toByteArray())) }
        return set
    }

    @Suppress("DEPRECATION", "PackageManagerGetSignatures")
    private fun firstCertSha256(pi: PackageInfo?): ByteArray? {
        val sig = if (Build.VERSION.SDK_INT >= 28) pi?.signingInfo?.apkContentsSigners?.firstOrNull()
        else pi?.signatures?.firstOrNull()
        return sig?.let { java.security.MessageDigest.getInstance("SHA-256").digest(it.toByteArray()) }
    }

    private fun selfSigScan(c: Context): String {
        val pm = c.packageManager
        val pms = runCatching { certSetOf(pm.getPackageInfo(c.packageName, selfSigFlag())) }.getOrDefault(emptySet())
        val apkPi = runCatching { pm.getPackageArchiveInfo(c.applicationInfo.sourceDir, selfSigFlag()) }.getOrNull()
        val apk = certSetOf(apkPi)
        val issues = mutableListOf<String>()
        if (pms.isNotEmpty() && apk.isNotEmpty() && pms.intersect(apk).isEmpty()) issues.add("PMS-cert!=APK-cert")
        if (Build.VERSION.SDK_INT >= 28) firstCertSha256(apkPi)?.let { apkCert ->
            val ok = runCatching { pm.hasSigningCertificate(c.packageName, apkCert, PackageManager.CERT_INPUT_SHA256) }.getOrDefault(true)
            if (!ok) issues.add("PMS-rejects-APK-cert")
        }
        runCatching { pm.checkSignatures(c.packageName, c.packageName) }.getOrNull()?.let {
            if (it != PackageManager.SIGNATURE_MATCH) issues.add("checkSignatures=$it")
        }
        return if (issues.isEmpty()) "coherent" else "INCOHERENT ${issues.joinToString(" ")}"
    }

    private fun sigSweep(c: Context): String {
        val pm = c.packageManager
        val flag = selfSigFlag()
        val pkgs = runCatching { pm.getInstalledPackages(flag) }.getOrDefault(emptyList())
        val divergent = mutableListOf<String>()
        var checked = 0
        for (p in pkgs) {
            val src = p.applicationInfo?.sourceDir ?: continue
            val pms = certSetOf(p)
            if (pms.isEmpty()) continue
            checked++
            val fast = firstCertSha256FromFile(src)
            if (fast != null && pms.contains(fast)) continue
            val apk = certSetOf(runCatching { pm.getPackageArchiveInfo(src, flag) }.getOrNull())
            if (apk.isEmpty()) continue
            if (pms.intersect(apk).isEmpty()) divergent.add(p.packageName)
        }
        return if (divergent.isEmpty()) "coherent" else "DIVERGENT(checked=$checked) ${divergent.joinToString(" ")}"
    }

    private val SCHEME_V3 = 0xf05368c0.toInt()
    private val SCHEME_V2 = 0x7109871a

    private fun firstCertSha256FromFile(path: String): String? {
        try {
            java.io.RandomAccessFile(path, "r").use { raf ->
                val fileLen = raf.length()
                if (fileLen < 22) return null
                val maxBack = minOf(fileLen, 22L + 65535L).toInt()
                val tail = ByteArray(maxBack)
                raf.seek(fileLen - maxBack); raf.readFully(tail)
                var eocd = -1
                var i = tail.size - 22
                while (i >= 0) {
                    if ((tail[i].toInt() and 0xff) == 0x50 && (tail[i + 1].toInt() and 0xff) == 0x4b &&
                        (tail[i + 2].toInt() and 0xff) == 0x05 && (tail[i + 3].toInt() and 0xff) == 0x06) { eocd = i; break }
                    i--
                }
                if (eocd < 0) return null
                val cdOffset = u32le(tail, eocd + 16)
                if (cdOffset < 32 || cdOffset > fileLen) return null
                val magic = ByteArray(16); raf.seek(cdOffset - 16); raf.readFully(magic)
                if (String(magic, Charsets.US_ASCII) != "APK Sig Block 42") return null
                val sz = ByteArray(8); raf.seek(cdOffset - 24); raf.readFully(sz)
                val blockStart = cdOffset - 8 - u64le(sz, 0)
                val blockLen = cdOffset - blockStart
                if (blockStart < 0 || blockLen < 32 || blockLen > 64L * 1024 * 1024) return null
                val block = ByteArray(blockLen.toInt()); raf.seek(blockStart); raf.readFully(block)
                val bb = java.nio.ByteBuffer.wrap(block).order(java.nio.ByteOrder.LITTLE_ENDIAN)
                bb.position(8)
                val pairsEnd = blockLen.toInt() - 24
                var v3: ByteArray? = null; var v2: ByteArray? = null
                while (bb.position() + 12 <= pairsEnd) {
                    val pairLen = bb.long
                    if (pairLen < 4 || bb.position() + pairLen > blockLen) break
                    val id = bb.int
                    val valLen = (pairLen - 4).toInt()
                    val valStart = bb.position()
                    if (id == SCHEME_V3 && v3 == null) v3 = block.copyOfRange(valStart, valStart + valLen)
                    else if (id == SCHEME_V2 && v2 == null) v2 = block.copyOfRange(valStart, valStart + valLen)
                    bb.position(valStart + valLen)
                }
                val scheme = v3 ?: v2 ?: return null
                val cert = firstCertFromScheme(scheme) ?: return null
                return if (cert.isEmpty()) null else sha256Hex(cert)
            }
        } catch (t: Throwable) { return null }
    }

    private fun firstCertFromScheme(scheme: ByteArray): ByteArray? {
        val root = java.nio.ByteBuffer.wrap(scheme).order(java.nio.ByteOrder.LITTLE_ENDIAN)
        val signers = lenPrefixed(root) ?: return null
        val signer = lenPrefixed(signers) ?: return null
        val signedData = lenPrefixed(signer) ?: return null
        lenPrefixed(signedData) ?: return null
        val certs = lenPrefixed(signedData) ?: return null
        val firstCert = lenPrefixed(certs) ?: return null
        val der = ByteArray(firstCert.remaining()); firstCert.get(der)
        return if (der.isEmpty()) null else der
    }

    private fun lenPrefixed(bb: java.nio.ByteBuffer): java.nio.ByteBuffer? {
        if (bb.remaining() < 4) return null
        val len = bb.int.toLong() and 0xffffffffL
        if (len < 0 || len > bb.remaining()) return null
        val b = ByteArray(len.toInt()); bb.get(b)
        return java.nio.ByteBuffer.wrap(b).order(java.nio.ByteOrder.LITTLE_ENDIAN)
    }

    private fun u32le(b: ByteArray, off: Int): Long =
        (b[off].toLong() and 0xff) or ((b[off + 1].toLong() and 0xff) shl 8) or
            ((b[off + 2].toLong() and 0xff) shl 16) or ((b[off + 3].toLong() and 0xff) shl 24)

    private fun u64le(b: ByteArray, off: Int): Long {
        var v = 0L; for (i in 7 downTo 0) v = (v shl 8) or (b[off + i].toLong() and 0xff); return v
    }

    private fun fingerprintRebuild(): String {
        val release = if (Build.VERSION.SDK_INT >= 30) Build.VERSION.RELEASE_OR_CODENAME else Build.VERSION.RELEASE
        return "${Build.BRAND}/${Build.PRODUCT}/${Build.DEVICE}:$release/" +
            "${Build.ID}/${Build.VERSION.INCREMENTAL}:${Build.TYPE}/${Build.TAGS}"
    }

    private fun verIsRelease(token: String?): Boolean? {
        val t = token?.trim()?.ifEmpty { null } ?: return null
        return t.all { it.isDigit() || it == '.' }
    }
    private fun fpReleaseToken(): String = Build.FINGERPRINT.substringAfter(':', "").substringBefore('/', "")
    private fun versionClusterSignals(): List<Pair<String, String>> = buildList {
        Build.VERSION.CODENAME.trim().ifEmpty { null }?.let {
            add("codename" to if (it.equals("REL", true)) "release" else "preview")
        }
        if (Build.VERSION.SDK_INT >= 30) verIsRelease(Build.VERSION.RELEASE_OR_CODENAME)?.let {
            add("release_or_codename" to if (it) "release" else "preview")
        }
        add("preview_sdk" to if (Build.VERSION.PREVIEW_SDK_INT == 0) "release" else "preview")
        verIsRelease(fpReleaseToken())?.let { add("fp_token" to if (it) "release" else "preview") }
    }
    private fun versionClusterVerdict(): String {
        val sig = versionClusterSignals()
        if (sig.size <= 1) return "coherent"
        return if (sig.map { it.second }.toSet().size <= 1) "coherent"
        else "INCOHERENT " + sig.joinToString(" ") { "${it.first}=${it.second}" }
    }
    private fun versionClusterDetail(): String {
        val rel = if (Build.VERSION.SDK_INT >= 30) Build.VERSION.RELEASE_OR_CODENAME else Build.VERSION.RELEASE
        return "codename=${Build.VERSION.CODENAME} release_or_codename=$rel " +
            "preview_sdk=${Build.VERSION.PREVIEW_SDK_INT} fp_token=${fpReleaseToken()}\n" +
            versionClusterSignals().joinToString(" ") { "${it.first}:${it.second}" }
    }

    private fun bootTriadLegs(): List<Pair<String, String>> = buildList {
        Attestation.record?.deviceLocked?.trim()?.lowercase()?.let { v ->
            when (v) { "yes", "true", "locked" -> "locked"; "no", "false", "unlocked" -> "unlocked"; else -> null }
                ?.let { add("attest.deviceLocked" to it) }
        }
        for (k in listOf("ro.boot.flash.locked", "ro.boot.vbmeta.device_state",
                "ro.secureboot.lockstate", "ro.secureboot.devicelock")) {
            val s = lpRead(k)?.let { lockNorm(k, it) } ?: continue
            add("prop.bootloader($k)" to s); break
        }
        lpRead("ro.boot.verifiedbootstate")?.let { raw ->
            lockNorm("ro.boot.verifiedbootstate", raw)?.let { add("prop.verifiedbootstate" to it) }
        }
    }
    private fun bootTriadVerdict(): String {
        val legs = bootTriadLegs()
        if (legs.size <= 1) return "coherent"
        return if (legs.map { it.second }.toSet().size <= 1) "coherent"
        else "INCOHERENT " + legs.joinToString(" ") { "${it.first}=${it.second}" }
    }
    private fun bootTriadDetail(): String {
        val legs = bootTriadLegs()
        return if (legs.isEmpty()) "no boot-lock signal readable" else legs.joinToString("\n") { "${it.first} = ${it.second}" }
    }

    private val PERM_GID_RULES: List<Pair<String, List<Pair<Int, String>>>> = listOf(
        "android.permission.INTERNET" to listOf(3003 to "INET"),
        "android.permission.BLUETOOTH_ADMIN" to listOf(3001 to "NET_BT_ADMIN"),
        "android.permission.BLUETOOTH" to listOf(3002 to "NET_BT"),
        "android.permission.NET_TUNNELING" to listOf(1016 to "VPN"),
        "android.permission.READ_LOGS" to listOf(1007 to "LOG"),
        "android.permission.ACCESS_MTP" to listOf(1024 to "MTP"),
        "android.permission.NET_ADMIN" to listOf(3005 to "NET_ADMIN"),
        "android.permission.MAINLINE_NETWORK_STACK" to listOf(3005 to "NET_ADMIN", 3004 to "NET_RAW"),
        "android.permission.ACCESS_CACHE_FILESYSTEM" to listOf(2001 to "CACHE"),
        "android.permission.DIAGNOSTIC" to listOf(1004 to "INPUT", 2002 to "DIAG"),
        "android.permission.READ_NETWORK_USAGE_HISTORY" to listOf(3006 to "NET_BW_STATS"),
        "android.permission.UPDATE_DEVICE_STATS" to listOf(3007 to "NET_BW_ACCT"),
        "android.permission.LOOP_RADIO" to listOf(1030 to "LOOP_RADIO"),
        "android.permission.MANAGE_VOICE_KEYPHRASES" to listOf(1005 to "AUDIO"),
        "android.permission.ACCESS_BROADCAST_RADIO" to listOf(1013 to "MEDIA"),
        "android.permission.USE_RESERVED_DISK" to listOf(1065 to "RESERVED_DISK"),
        "android.permission.WRITE_SECURITY_LOG" to listOf(1091 to "SECURITY_LOG_WRITER"),
        "android.permission.VIRTUAL_INPUT_DEVICE" to listOf(3011 to "UHID"),
        "android.permission.MANAGE_VIRTUAL_MACHINE" to listOf(3013 to "VIRTUAL_MACHINE"),
    )

    private fun grantedPerms(c: Context): Set<String> = runCatching {
        @Suppress("DEPRECATION")
        val pi = c.packageManager.getPackageInfo(c.packageName, PackageManager.GET_PERMISSIONS)
        val perms = pi.requestedPermissions ?: return emptySet()
        val flags = pi.requestedPermissionsFlags ?: IntArray(0)
        perms.filterIndexed { i, _ -> (flags.getOrElse(i) { 0 } and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0 }.toSet()
    }.getOrDefault(emptySet())

    private fun parseGroupsLine(status: String?): Set<Int>? =
        status?.lineSequence()?.firstOrNull { it.startsWith("Groups:") }
            ?.removePrefix("Groups:")?.trim()?.split(Regex("\\s+"))
            ?.mapNotNull { it.toIntOrNull() }?.toSet()

    private fun expectedZygoteGids(c: Context): LinkedHashMap<Int, String> {
        val granted = grantedPerms(c)
        val exp = LinkedHashMap<Int, String>()
        for ((perm, gids) in PERM_GID_RULES) if (perm in granted) for ((g, label) in gids) exp[g] = label
        runCatching { c.packageManager.getPackageGids(c.packageName) }.getOrNull()?.forEach {
            if (it in 1..9996 && it !in exp) exp[it] = "pm:$it"
        }
        return exp
    }

    private fun zygoteGidStripped(c: Context): String {
        val expected = expectedZygoteGids(c)
        val actual = NativeBridge.getGroups()?.split(",")?.mapNotNull { it.toIntOrNull() }?.toSet()
            ?: return "n/a (getgroups unavailable)"
        val missing = expected.filterKeys { it !in actual }
        return if (missing.isNotEmpty())
            "STRIPPED " + missing.entries.joinToString(" ") { "${it.value}(${it.key})" }
        else "coherent"
    }

    private fun zygoteGidProcSpoof(c: Context): String {
        val actual = NativeBridge.getGroups()?.split(",")?.mapNotNull { it.toIntOrNull() }?.toSet()
            ?: return "n/a (getgroups unavailable)"
        val procJava = parseGroupsLine(runCatching { File("/proc/self/status").readText() }.getOrNull())
        return if (procJava != null && procJava != actual)
            "GROUPS-LINE-SPOOF proc=${procJava.sorted()} getgroups=${actual.sorted()}"
        else "coherent"
    }

    private fun zygoteGidRaw(c: Context): String {
        val expected = expectedZygoteGids(c)
        val gg = NativeBridge.getGroups()
        val procJava = parseGroupsLine(runCatching { File("/proc/self/status").readText() }.getOrNull())
        val procNat = parseGroupsLine(NativeBridge.readFile("/proc/self/status"))
        val expStr = if (expected.isEmpty()) "none" else expected.entries.joinToString(",") { "${it.value}:${it.key}" }
        return "expected=[$expStr] getgroups(2)=[$gg] proc(java)=${procJava?.sorted()} proc(native)=${procNat?.sorted()}"
    }

    private val permPriv = listOf(
        "android.permission.START_ANY_ACTIVITY", "android.permission.INSTALL_PACKAGES",
        "android.permission.MANAGE_USERS", "android.permission.GRANT_RUNTIME_PERMISSIONS",
        "android.permission.INTERACT_ACROSS_USERS_FULL",
    )
    private val permAgree = listOf(
        "android.permission.INTERNET", "android.permission.QUERY_ALL_PACKAGES",
        "android.permission.ACCESS_NETWORK_STATE",
    )

    private fun permVerdicts(c: Context, perm: String): LinkedHashMap<String, Int?> {
        val pm = c.packageManager
        val uid = android.os.Process.myUid(); val pid = android.os.Process.myPid()
        val v = LinkedHashMap<String, Int?>()
        v["pm.checkPermission"] = runCatching { pm.checkPermission(perm, c.packageName) }.getOrNull()
        v["ctx.checkPermission"] = runCatching { c.checkPermission(perm, pid, uid) }.getOrNull()
        v["pkgInfo.flags"] = runCatching {
            val pi = pm.getPackageInfo(c.packageName, android.content.pm.PackageManager.GET_PERMISSIONS)
            val idx = pi.requestedPermissions?.indexOf(perm) ?: -1
            if (idx < 0) null
            else if ((pi.requestedPermissionsFlags!![idx] and android.content.pm.PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) 0 else -1
        }.getOrNull()
        v["IPackageManager.checkUidPermission"] = runCatching {
            val binder = Class.forName("android.os.ServiceManager")
                .getMethod("getService", String::class.java).invoke(null, "package") as android.os.IBinder
            val ipm = Class.forName("android.content.pm.IPackageManager\$Stub")
                .getMethod("asInterface", android.os.IBinder::class.java).invoke(null, binder)
            ipm.javaClass.getMethod("checkUidPermission", String::class.java, Int::class.javaPrimitiveType)
                .invoke(ipm, perm, uid) as Int
        }.getOrNull()
        return v
    }

    private fun permPathCoherence(c: Context): String {
        val issues = mutableListOf<String>()
        for (perm in permPriv + permAgree) {
            val reachable = permVerdicts(c, perm).filterValues { it != null }
            if (reachable.isEmpty()) continue
            val short = perm.substringAfterLast(".")
            val vals = reachable.values.toSet()
            if (vals.size > 1)
                issues.add("$short DISAGREE[" + reachable.entries.joinToString(",") { "${it.key.substringAfterLast(".")}=${if (it.value == 0) "G" else "D"}" } + "]")
            else if (perm in permPriv && vals.contains(0))
                issues.add("$short GRANTED(privileged, should be denied)")
        }
        return if (issues.isEmpty()) "coherent" else "PERM-DIVERGENCE " + issues.joinToString(" | ")
    }

    private fun permPathRaw(c: Context): String =
        (permPriv + permAgree).joinToString("\n") { p ->
            p.substringAfterLast(".") + ": " + permVerdicts(c, p).entries.joinToString(" ") {
                "${it.key.substringAfterLast(".")}=${it.value?.let { r -> if (r == 0) "G" else "D" } ?: "n/a"}"
            }
        }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {
        val suDirs = AssetData.packages(ctx, "su_path_dirs")
        val invisRanges = AssetData.invisibleRanges(ctx)
        val zwDirs = listOf("/storage/emulated/0/Android/data", "/storage/emulated/0/Android/obb")

        val kmodNeedles = AssetData.packages(ctx, "root_kernel_module_needles")
        val backupPkgs = AssetData.packages(ctx, "root_backup_tools_apps")
        val localTmpArtifacts = AssetData.packages(ctx, "root_local_tmp_artifacts")
            .map { "/data/local/tmp/$it" }

        add(probe("integrity:kmods", ctx.getString(R.string.t_kernel_modules), Category.INTEGRITY, listOf(
            jm("read /proc/modules") { flagMods(runCatching { File("/proc/modules").readText() }.getOrNull(), kmodNeedles) },
            nm("native read /proc/modules") { flagMods(NativeBridge.readFile("/proc/modules"), kmodNeedles) },
            sm("lsmod 2>/dev/null | grep -iE '${kmodNeedles.joinToString("|")}' | awk '{print \$1}' | paste -sd, - || echo ${Sentinels.ABSENT}",
                "lsmod | grep root modules"),
        )))

        add(probe("integrity:signing_keys", ctx.getString(R.string.t_signing_keys), Category.INTEGRITY, listOf(
            jm("expected: build tags and every signing-key prop are release-keys") { "release-keys" },
            jm("measured: signing-key props by SystemProperties, native and getprop") { signingKeysScan() },
            jm("present signing-key props (raw)", compare = false) { signingKeysRaw() },
        ), solution = ctx.getString(R.string.sol_signing_keys)))

        add(probe("integrity:lock_state_coherence", ctx.getString(R.string.t_lock_coherence), Category.INTEGRITY, listOf(
            jm("expected: every present bootloader-lock / verified-boot signal agrees") { "coherent" },
            jm("measured: lock signals normalized and cross-checked") { lockStateScan() },
            jm("per-signal map (raw -> normalized; veritymode shown, not voted)", compare = false) { lockStateRaw() },
        ), solution = ctx.getString(R.string.sol_lock_coherence)))

        add(probe("integrity:boot_triad_coherence", ctx.getString(R.string.t_boot_triad_coherence), Category.INTEGRITY, listOf(
            jm("expected: deviceLocked (attestation), bootloader-locked (prop) and verifiedbootstate (prop) all agree") { "coherent" },
            jm("measured: the three boot-lock legs cross-voted") { bootTriadVerdict() },
            jm("per-leg", compare = false) { bootTriadDetail() },
        ), solution = ctx.getString(R.string.sol_lock_coherence)))

        add(probe("dev:fingerprint_rebuild", ctx.getString(R.string.t_fingerprint_rebuild), Category.FINGERPRINT, listOf(
            jm("reconstructed: BRAND/PRODUCT/DEVICE:RELEASE/ID/INCREMENTAL:TYPE/TAGS") { fingerprintRebuild() },
            jm("Build.FINGERPRINT (actual)") { Build.FINGERPRINT },
        ), solution = ctx.getString(R.string.sol_fingerprint_rebuild)))

        add(probe("dev:version_cluster", ctx.getString(R.string.t_version_cluster), Category.BUILD, listOf(
            jm("expected: codename, release_or_codename, preview_sdk and fingerprint token all agree (all release or all preview)") { "coherent" },
            jm("measured: version-state signals cross-voted") { versionClusterVerdict() },
            jm("per-signal", compare = false) { versionClusterDetail() },
        ), solution = ctx.getString(R.string.sol_version_cluster)))

        add(probe("integrity:signature_source_coherence", ctx.getString(R.string.t_signature_source), Category.INTEGRITY, listOf(
            jm("expected: PMS record, APK-file parse, hasSigningCertificate and checkSignatures all agree") { "coherent" },
            jm("measured: cross-check of this app's signing-cert sources") { c -> selfSigScan(c) },
            jm("getPackageInfo cert set [PMS, info]", compare = false) { c -> selfSigPms(c) },
            jm("getPackageArchiveInfo cert set [APK file, info]", compare = false) { c -> selfSigApk(c) },
            jm("checkSignatures(self,self) [info]", compare = false) { c -> selfCheckSig(c) },
        ), solution = ctx.getString(R.string.sol_signature_source)))

        add(probe("integrity:signature_sweep", ctx.getString(R.string.t_signature_sweep), Category.INTEGRITY, listOf(
            jm("expected: every readable app's PMS cert and APK-file cert share a signer") { "coherent" },
            jm("measured: installed apps whose PMS cert set is disjoint from the APK-file cert set") { c -> sigSweep(c) },
        ), solution = ctx.getString(R.string.sol_signature_sweep)))

        add(probe("tel:baseband_coherence", ctx.getString(R.string.t_baseband_coherence), Category.TELEPHONY, buildList {
            add(jm("Build.getRadioVersion") { Build.getRadioVersion() })
            addAll(propTrio("gsm.version.baseband"))
            addAll(propTrio("gsm.version.baseband1"))
            addAll(propTrio("vendor.ril.gsm.version.baseband"))
            add(jm("gsm.version.baseband2 (per-slot, info)", compare = false) { SystemProps.get("gsm.version.baseband2") })
            add(jm("vendor.gsm.project.baseband (project string, info)", compare = false) { SystemProps.get("vendor.gsm.project.baseband") })
            add(jm("ro.baseband (modem platform, info)", compare = false) { SystemProps.get("ro.baseband") })
        }, solution = ctx.getString(R.string.sol_baseband_coherence)))

        add(probe("integrity:kernel_syscall_age", ctx.getString(R.string.t_kernel_syscall_fingerprint), Category.INTEGRITY, listOf(
            nm("syscall availability vs uname") { NativeBridge.kernelSpoof() },
        )))

        add(probe("integrity:su_libc_hook", ctx.getString(R.string.t_su_libc_inline_hook), Category.INTEGRITY, listOf(
            nm("raw faccessat() over ${suDirs.size} su paths") {
                suDirs.map { "$it/su" }.firstOrNull { NativeBridge.rawExists(it) } ?: Sentinels.ABSENT
            },
            nm("libc access() over the same paths") {
                suDirs.map { "$it/su" }.firstOrNull { NativeBridge.exists(it) } ?: Sentinels.ABSENT
            },
            jm("Os.stat()/File over the same paths") {
                suDirs.map { "$it/su" }.firstOrNull { runCatching { android.system.Os.stat(it) }.isSuccess } ?: Sentinels.ABSENT
            },
        )))

        add(probe("integrity:atexit_drift", ctx.getString(R.string.t_atexit_drift), Category.INTEGRITY, listOf(
            jm("expected: libc atexit functions resolve inside libc") { "clean" },
            nm("measured: __cxa_atexit/__cxa_finalize owning library") { NativeBridge.atexitDrift() },
        ), solution = ctx.getString(R.string.sol_atexit_drift)))

        add(probe("integrity:zygote_gid_coherence", ctx.getString(R.string.t_zygote_gid), Category.INTEGRITY, listOf(
            jm("expected: every permission-implied supplementary GID is present in the process") { "coherent" },
            jm("permission-implied GIDs vs real getgroups(2)") { c -> zygoteGidStripped(c) },
            jm("/proc/self/status Groups vs getgroups(2)") { c -> zygoteGidProcSpoof(c) },
            jm("expected GIDs / getgroups(2) / proc reads (raw)", compare = false) { c -> zygoteGidRaw(c) },
        ), solution = ctx.getString(R.string.sol_zygote_gid)))

        add(probe("integrity:perm_path_coherence", ctx.getString(R.string.t_perm_path_coherence), Category.INTEGRITY, listOf(
            jm("expected: a permission's grant is identical across every reachable check path, and privileged permissions are denied on all of them") { "coherent" },
            jm("permission verdict across checkPermission / Context.checkPermission(uid) / getPackageInfo flags / IPackageManager.checkUidPermission (a hiding/forge hook that spoofs one path leaks here)") { c -> permPathCoherence(c) },
            jm("raw per-permission per-path verdicts (G/D/n-a; n-a on IPackageManager means the low-level PMS binder is not reflection-reachable)", compare = false) { c -> permPathRaw(c) },
        ), solution = ctx.getString(R.string.sol_perm_path_coherence)))

        add(probe("integrity:self_lib_origin", ctx.getString(R.string.t_native_lib_origin), Category.INTEGRITY, listOf(
            nm("dladdr(self) origin path") {
                NativeBridge.selfPath()?.let { if (pathTampered(it)) "TAMPERED: $it" else it }
            },
        )))

        add(probe("integrity:uid_coherence", ctx.getString(R.string.t_uid_coherence), Category.INTEGRITY, listOf(
            jm("Process.myUid vs stat(dataDir) owner") { c ->
                uidVerdict(Process.myUid(), NativeBridge.statOwner(c.dataDir.path)?.first)
            },
            nm("getuid() vs stat(dataDir) owner") {
                val owner = NativeBridge.statOwner(ctx.dataDir.path)?.first
                NativeBridge.ids()?.uid?.let { uidVerdict(it, owner) }
            },
        )))

        add(probe("integrity:uid_priority_fake", ctx.getString(R.string.t_uid_priority_fake), Category.INTEGRITY, listOf(
            jm("expected: no priority syscall returns EINVAL for a valid selector") { "clean" },
            nm("getpriority(PRIO_USER) EINVAL = forged response") { priorityFakeByLens(ctx, 0) },
            nm("setpriority(PRIO_USER) EINVAL = forged response") { priorityFakeByLens(ctx, 1) },
            nm("ioprio_get(IOPRIO_WHO_USER) EINVAL = forged response") { priorityFakeByLens(ctx, 2) },
            nm("ioprio_set(IOPRIO_WHO_USER) EINVAL = forged response") { priorityFakeByLens(ctx, 3) },
        )))

        add(probe("integrity:uid_hidden_running", ctx.getString(R.string.t_uid_hidden_running), Category.INTEGRITY, listOf(
            jm("expected: no live UID hidden from the package manager") { "clean" },
            jm("getpriority(PRIO_USER) live UID with no visible package [own user]") { c -> hiddenRunningByLens(c, 0) },
            jm("setpriority(PRIO_USER) live UID with no visible package [own user]") { c -> hiddenRunningByLens(c, 1) },
            jm("ioprio_get(IOPRIO_WHO_USER) live UID with no visible package [own user]") { c -> hiddenRunningByLens(c, 2) },
            jm("ioprio_set(IOPRIO_WHO_USER) live UID with no visible package [own user]") { c -> hiddenRunningByLens(c, 3) },
            jm("INFO: getpriority liveness over SDK-sandbox range (own user)", compare = false) { liveCountRange(20000, 29999) },
            jm("INFO: getpriority liveness over isolated range (own user)", compare = false) { liveCountRange(90000, 99999) },
            jm("INFO: getpriority liveness across other visible user profiles", compare = false) { c -> liveCountOtherProfiles(c) },
        )))

        add(probe("integrity:settings_read_gate", ctx.getString(R.string.t_settings_read_gate), Category.INTEGRITY, listOf(
            jm("expected: a restricted setting key stays gated for a normal app") { c ->
                if (!readGateApplies(c)) return@jm null
                "gated"
            },
            jm("Settings.Secure.getString(bluetooth_name)") { c ->
                if (!readGateApplies(c)) return@jm null
                try {
                    val v = Settings.Secure.getString(c.contentResolver, "bluetooth_name")
                    if (v.isNullOrEmpty()) "gated" else "LEAKED_VALUE"
                } catch (e: SecurityException) {
                    "gated"
                } catch (e: Throwable) {
                    "gated"
                }
            },
        )))

        add(probe("integrity:backup_tools", ctx.getString(R.string.t_root_backup_tools), Category.INTEGRITY, listOf(
            nm("access() /data/data & /data/user/0 for ${backupPkgs.size} tools") {
                backupPkgs.filter { NativeBridge.exists("/data/data/$it") || NativeBridge.exists("/data/user/0/$it") }
                    .joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
            jm("getPackageInfo of the same tools") { c ->
                backupPkgs.filter { runCatching { c.packageManager.getPackageInfo(it, 0) }.isSuccess }
                    .joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
        )))

        add(probe("integrity:local_tmp", ctx.getString(R.string.t_local_tmp_contents), Category.INTEGRITY, listOf(
            nm("access() ${localTmpArtifacts.size} known artifacts") {
                localTmpArtifacts.filter { NativeBridge.exists(it) }.joinToString(", ").ifEmpty { Sentinels.ABSENT }
            },
            sm("ls -a /data/local/tmp 2>/dev/null | grep -vE '^\\.\\.?$' | paste -sd, - || echo ${Sentinels.ABSENT}",
                "ls /data/local/tmp"),
        )))

        add(probe("integrity:documents_providers", ctx.getString(R.string.t_documents_providers), Category.PACKAGES, listOf(
            jm("queryIntentContentProviders DOCUMENTS_PROVIDER") { c ->
                val intent = android.content.Intent("android.content.action.DOCUMENTS_PROVIDER")
                runCatching {
                    c.packageManager.queryIntentContentProviders(intent, 0)
                        .mapNotNull { it.providerInfo?.let { p -> "${p.packageName}/${p.authority}" } }
                        .distinct().sorted().joinToString("\n")
                }.getOrNull()?.ifEmpty { Sentinels.NONE }
            },
            sm("pm query-content-providers 2>/dev/null | grep -i document | head || echo ${Sentinels.ABSENT}",
                "pm query-content-providers | grep document", compare = false),
        )))

        add(probe("integrity:zero_width_names", ctx.getString(R.string.t_zero_width_names), Category.INTEGRITY, listOf(
            jm("File.list Android/data+obb") { aggInvisible(zwDirs) { p -> jvmInvisible(p, invisRanges) } },
            nm("getdents64 Android/data+obb") { aggInvisible(zwDirs) { p -> NativeBridge.dirZeroWidth(p, invisRanges) } },
        )))

        add(probe("net:resolv_conf", ctx.getString(R.string.t_resolv_conf), Category.NETWORK, listOf(
            jm("read /etc/resolv.conf") {
                runCatching { File("/etc/resolv.conf").readText().trim() }.getOrNull()?.ifEmpty { Sentinels.ABSENT }
            },
            nm("native read /etc/resolv.conf") { NativeBridge.readFileOrReason("/etc/resolv.conf", 4096)?.trim() },
            nm("native read /system/etc/resolv.conf") { NativeBridge.readFileOrReason("/system/etc/resolv.conf", 4096)?.trim() },
            sm("getprop | grep -iE 'net\\.dns[0-9]' | paste -sd' ' - || echo ${Sentinels.ABSENT}", "getprop net.dns*", compare = false),
        )))

        add(probe("pkg:store_version", ctx.getString(R.string.t_play_store_version), Category.PACKAGES, listOf(
            jm("versionName com.android.vending / gms") { c ->
                AssetData.packages(c, "store_version_packages").joinToString("\n") { p ->
                    val v = runCatching {
                        val pi = c.packageManager.getPackageInfo(p, 0)
                        val code = if (Build.VERSION.SDK_INT >= 28) pi.longVersionCode.toString()
                        else @Suppress("DEPRECATION") pi.versionCode.toString()
                        "${pi.versionName} ($code)"
                    }.getOrNull() ?: Sentinels.ABSENT
                    "$p=$v"
                }
            },
            sm("for p in com.android.vending com.google.android.gms; do echo \$p=\$(dumpsys package \$p 2>/dev/null | grep -m1 versionName | cut -d= -f2); done",
                "dumpsys package versionName", compare = false),
        )))

        add(probe("self:debuggable", ctx.getString(R.string.t_own_debuggable), Category.INTEGRITY, listOf(
            jm("ApplicationInfo.FLAG_DEBUGGABLE") { c ->
                val dbg = (c.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
                val vn = runCatching { c.packageManager.getPackageInfo(c.packageName, 0).versionName }.getOrNull().orEmpty()
                val tainted = vn.contains("debug", true) || vn.contains("BUILD", true)
                "debuggable=$dbg versionName=$vn${if (dbg || tainted) " => TAINTED" else " => OK"}"
            },
            nm("ro.debuggable (global)", compare = false) { NativeBridge.sysprop("ro.debuggable") },
        )))

        add(probe("integrity:ime_verdict", ctx.getString(R.string.t_ime_verdict), Category.SECURITY, listOf(
            jm("enabled IMEs are system or from a store (expected)") { "SAFE" },
            jm("non-system enabled IME with non-store installer") { c ->
                val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    ?: return@jm Sentinels.NONE
                val stores = AssetData.packages(c, "install_source_stores").toHashSet()
                val bad = imm.enabledInputMethodList.mapNotNull { imi ->
                    val pkg = imi.packageName
                    val sys = runCatching {
                        (imi.serviceInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    }.getOrDefault(true)
                    if (sys) return@mapNotNull null
                    val inst = runCatching { c.packageManager.getInstallSourceInfo(pkg).installingPackageName }.getOrNull()
                    if (inst != null && inst in stores) null else "$pkg(installer=${inst ?: "null"})"
                }
                if (bad.isEmpty()) "SAFE" else "UNSAFE: ${bad.joinToString("; ")}"
            },
            jm("no synthetic IME entry (serviceInfo.applicationInfo.flags==0 = injected dummy)") { c ->
                val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    ?: return@jm Sentinels.NONE
                val all = (imm.inputMethodList + imm.enabledInputMethodList).distinctBy { it.id }
                val synth = all.filter { runCatching { it.serviceInfo.applicationInfo.flags == 0 }.getOrDefault(false) }
                    .map { it.packageName }.distinct()
                if (synth.isEmpty()) "SAFE" else "UNSAFE: SYNTHETIC ${synth.joinToString(",")}"
            },
            jm("IME subtype surface (current present + enabled-subtype count)", compare = false) { c ->
                val imm = c.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    ?: return@jm Sentinels.NONE
                val cur = runCatching { imm.currentInputMethodSubtype }.getOrNull()
                val n = runCatching {
                    imm.enabledInputMethodList.sumOf { imi -> imm.getEnabledInputMethodSubtypeList(imi, true).size }
                }.getOrDefault(-1)
                "current=${if (cur == null) "null" else "present"} enabled_subtypes=$n"
            },
        )))

        add(probe("integrity:store_authenticity", ctx.getString(R.string.t_store_authenticity), Category.PACKAGES, listOf(
            jm("Google stores are Google-signed (expected)") { "ok" },
            jm("store/GMS package present but not Google-signed") { c ->
                val pm = c.packageManager
                val flag = if (Build.VERSION.SDK_INT >= 28) PackageManager.GET_SIGNING_CERTIFICATES
                else @Suppress("DEPRECATION") PackageManager.GET_SIGNATURES
                val bad = listOf("com.android.vending", "com.google.android.gms", "com.google.android.gsf", "com.android.vending.billing").mapNotNull { pkg ->
                    val sig = runCatching {
                        val pi = pm.getPackageInfo(pkg, flag)
                        val s = if (Build.VERSION.SDK_INT >= 28) pi.signingInfo?.apkContentsSigners?.firstOrNull()
                        else @Suppress("DEPRECATION") pi.signatures?.firstOrNull()
                        s?.let { java.security.MessageDigest.getInstance("SHA-256").digest(it.toByteArray()).joinToString("") { b -> "%02x".format(b) } }
                    }.getOrNull() ?: return@mapNotNull null
                    if (AssetData.certLabel(c, sig)?.contains("Google", true) == true) null
                    else "$pkg(${AssetData.certTag(c, sig)})"
                }
                if (bad.isEmpty()) "ok" else "FAKE: ${bad.joinToString("; ")}"
            },
        )))

        add(probe("integrity:devgate", ctx.getString(R.string.t_devgate), Category.INTEGRITY, listOf(
            jm("no dev-gated setting on while dev-options off (expected)") { "ok" },
            jm("dev-gated setting non-default while development_settings_enabled=0") { c ->
                @Suppress("DEPRECATION")
                val devOn = (Settings.Global.getString(c.contentResolver, "development_settings_enabled") ?: "0") != "0"
                if (devOn) return@jm "dev-options ON"
                val bad = AssetData.devGated(c).mapNotNull { (k, def) ->
                    @Suppress("DEPRECATION")
                    val v = Settings.Global.getString(c.contentResolver, k)
                        ?: Settings.Secure.getString(c.contentResolver, k)
                    if (!v.isNullOrEmpty() && v != def) "$k=$v" else null
                }
                if (bad.isEmpty()) "ok" else "INCONSISTENT: ${bad.joinToString(",")}"
            },
        )))

        add(probe("integrity:concealment", ctx.getString(R.string.t_concealment), Category.INTEGRITY, listOf(
            jm("no target hidden-but-reachable (expected)") { "0" },
            jm("hidden-but-reachable count") { c -> concealScan(c).count { it.hiddenButReachable }.toString() },
            jm("door matrix (list/getPackageInfo/getApplicationInfo/createPackageContext/openApk/launchIntent/queryIntentActivities)", compare = false) { c ->
                concealScan(c).takeIf { it.isNotEmpty() }?.joinToString("\n") { it.matrix } ?: Sentinels.NONE
            },
        )))

        add(probe("integrity:datadir_errno_oracle", ctx.getString(R.string.t_datadir_errno_oracle), Category.INTEGRITY, buildList {
            val cache = arrayOfNulls<OracleScan>(1)
            fun scan(c: Context): OracleScan {
                if (cache[0] == null) cache[0] = scanOracle(c)
                return cache[0]!!
            }
            add(jm("expected: nothing hidden from every package door yet present via errno, and every stat variant agrees") { "clean" })
            add(jm("measured: errno-channel leak and stat-variant divergence") { c ->
                val s = scan(c)
                when {
                    oracleLiveness() != "live" -> "clean"
                    s.leak.isNotEmpty() -> "leak: ${s.leak.joinToString(",")}"
                    s.divergent.isNotEmpty() -> "stat-variant divergence: ${s.divergent.joinToString(",")}"
                    else -> "clean"
                }
            })
            add(jm("measured: readlinkat resolves a path the stat family reports ENOENT (incomplete hide)") { c ->
                if (oracleLiveness() != "live") "clean"
                else scan(c).let { readlinkatIncompleteHide(it) }
                    .let { if (it.isEmpty()) "clean" else "readlinkat-only presence: ${it.joinToString(",")}" }
            })
            add(jm("channel liveness (control: a nonexistent name)", compare = false) { oracleLiveness() })
            add(jm("control: data-dir errno of known-installed non-target apps", compare = false) { controlAppsPresence() })
            add(jm("present via the errno channel across ${STAT_FAMILY.size} stat variants", compare = false) { c ->
                val s = scan(c)
                (if (s.present.isEmpty()) Sentinels.NONE else s.present.joinToString(",")) +
                    (if (s.divergent.isEmpty()) "" else "\ndivergent variants: ${s.divergent.joinToString(",")}")
            })
            add(jm("per-target map (pm=package-visible / fs=data-dir-present)", compare = false) { c ->
                val listed = installedSet(c)
                val rows = targetPackages(c).mapNotNull { pkg ->
                    val pm = pmVisible(c, pkg, listed)
                    val fs = fsPresent(pkg)
                    if (!pm && !fs) null
                    else "$pkg pm=${if (pm) 1 else 0} fs=${if (fs) 1 else 0}" + if (!pm && fs) "  LEAK" else ""
                }
                rows.takeIf { it.isNotEmpty() }?.joinToString("\n") ?: Sentinels.NONE
            })
        }, solution = ctx.getString(R.string.sol_datadir_errno_oracle)))

        add(probe("integrity:multiuser_oracle", ctx.getString(R.string.t_multiuser_oracle), Category.INTEGRITY, listOf(
            jm("expected: no secondary user / private-space dir visible to the app") { "clean" },
            jm("user dir present via errno but invisible to UserManager (hidden user / private space)") { c ->
                fun present(n: Int): Boolean = NativeBridge.statPresence("/data/user/$n", 0).let { it == "present" || it == "present(denied)" }
                if (present(77)) return@jm "clean"
                val known = userManagerKnownIds(c)
                val hidden = COMMON_USER_IDS.filter { present(it) && it !in known }
                if (hidden.isEmpty()) "clean" else "HIDDEN USER DIR(S): /data/user/{${hidden.joinToString(",")}}"
            },
            jm("raw-syscall twin", compare = false) { c ->
                fun present(n: Int): Boolean = NativeBridge.statPresence("/data/user/$n", 19).let { it == "present" || it == "present(denied)" }
                if (present(77)) return@jm "channel opaque"
                val known = userManagerKnownIds(c)
                val hidden = COMMON_USER_IDS.filter { present(it) && it !in known }
                if (hidden.isEmpty()) "clean" else "HIDDEN USER DIR(S): /data/user/{${hidden.joinToString(",")}}"
            },
            jm("UserManager profiles vs errno-present user dirs", compare = false) { c ->
                val um = c.getSystemService(Context.USER_SERVICE) as? android.os.UserManager
                val profiles = runCatching { um?.userProfiles?.size }.getOrNull() ?: -1
                val present = COMMON_USER_IDS.filter { NativeBridge.statPresence("/data/user/$it", 0).let { s -> s == "present" || s == "present(denied)" } }
                "userManager.profiles=$profiles errno-present=/data/user/{${present.joinToString(",")}}"
            },
        ), solution = ctx.getString(R.string.sol_multiuser_oracle)))

        val kcPubkey = "/data/misc/keychain/pubkey_blacklist.txt"
        val kcSerial = "/data/misc/keychain/serial_blacklist.txt"
        fun meta(mtime: Long, size: Long) = "mtime=$mtime size=$size"
        fun kcJava(p: String): String { val f = File(p); return if (!f.exists()) Sentinels.ABSENT else meta(f.lastModified() / 1000, f.length()) }
        fun kcNat(p: String): String = NativeBridge.statMeta(p)?.let { meta(it.first, it.second) } ?: Sentinels.ABSENT
        add(probe("integrity:keychain_blacklist", ctx.getString(R.string.t_keychain_blacklist), Category.INTEGRITY, listOf(
            jm("File.lastModified/length (pubkey | serial)") { "pubkey=${kcJava(kcPubkey)} serial=${kcJava(kcSerial)}" },
            nm("stat (pubkey | serial)") { "pubkey=${kcNat(kcPubkey)} serial=${kcNat(kcSerial)}" },
            sm("echo \"pubkey=\$(stat -c 'mtime=%Y size=%s' $kcPubkey 2>/dev/null || echo ${Sentinels.ABSENT}) " +
                "serial=\$(stat -c 'mtime=%Y size=%s' $kcSerial 2>/dev/null || echo ${Sentinels.ABSENT})\"",
                "stat pubkey+serial"),
            jm("same setup/reset-date via unmodified world-traversable dirs (secondary surface)", compare = false) {
                listOf(
                    "/data/vendor/hardware", "/data/vendor/shading",
                    "/data/vendor/shading_otp", "/data/vendor/cam_ulog",
                ).joinToString(" | ") { p -> "$p=" + (NativeBridge.statMeta(p)?.first?.toString() ?: Sentinels.ABSENT) }
            },
        )))

        add(probe("integrity:buildprop_vs_runtime", ctx.getString(R.string.t_buildprop_vs_runtime), Category.INTEGRITY, listOf(
            jm("build.prop files vs runtime getprop") { c ->
                val files = AssetData.partitions(c).flatMap { it.propPaths }
                val keys = listOf(
                    "ro.product.model", "ro.product.device", "ro.build.fingerprint",
                    "ro.build.version.security_patch",
                )
                val fileVals = HashMap<String, MutableSet<String>>()
                for (path in files) {
                    val txt = NativeBridge.readFile(path) ?: continue
                    for (line in txt.lineSequence()) {
                        val t = line.trim()
                        if (t.startsWith("#")) continue
                        val i = t.indexOf('=')
                        if (i <= 0) continue
                        val k = t.substring(0, i).trim()
                        if (k in keys) fileVals.getOrPut(k) { HashSet() }.add(t.substring(i + 1).trim())
                    }
                }
                if (fileVals.isEmpty()) return@jm Sentinels.ABSENT
                keys.mapNotNull { k ->
                    val fv = fileVals[k] ?: return@mapNotNull null
                    val rt = SystemProps.get(k)
                    val ok = rt != null && fv.contains(rt)
                    "$k: file=${fv.joinToString("|")} runtime=${rt ?: "-"} ${if (ok) "OK" else "DIVERGE"}"
                }.joinToString("\n").ifEmpty { Sentinels.ABSENT }
            },
        )))

        add(probe("integrity:flagsecure_honored", ctx.getString(R.string.t_flagsecure_honored), Category.INTEGRITY, listOf(
            jm("expected: our own FLAG_SECURE request is honored") {
                val r = FlagSecureState.result ?: return@jm null
                if (!r.startsWith("honored") && !r.startsWith("STRIPPED")) return@jm null
                "honored"
            },
            jm("FLAG_SECURE set on own window, read back") {
                val r = FlagSecureState.result ?: return@jm null
                if (!r.startsWith("honored") && !r.startsWith("STRIPPED")) return@jm null
                r
            },
        )))

        add(probe("net:nettype_consistency", ctx.getString(R.string.t_nettype_consistency), Category.NETWORK, listOf(
            jm("getType/hasTransport") { c -> claimedNetType(c) },
            jm("active iface -> type (LinkProperties, independent)") { c -> ifaceNetType(c) },
            jm("underlying transport when VPN", compare = false) { c -> vpnUnderlying(c) },
            nm("native /sys/class/net wlan operstate", compare = false) { sysfsWlan() },
            sm("cat /proc/net/wireless 2>/dev/null | awk 'NR>2{print \$1}' | tr -d ':' | paste -sd, - || echo ${Sentinels.ABSENT}",
                "proc/net/wireless ifaces", compare = false),
        )))
    }

    private fun cmgr(c: Context) =
        c.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager

    private fun claimedNetType(c: Context): String? {
        val cm = cmgr(c)
        val n = cm.activeNetwork ?: return Sentinels.ABSENT
        val caps = cm.getNetworkCapabilities(n) ?: return Sentinels.ABSENT
        if (caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_VPN)) return "vpn"
        return when {
            caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
            caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR) -> "mobile"
            caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
            else -> "other"
        }
    }

    private fun ifaceNetType(c: Context): String? {
        val cm = cmgr(c)
        val n = cm.activeNetwork ?: return Sentinels.ABSENT
        val ifn = cm.getLinkProperties(n)?.interfaceName ?: return Sentinels.ABSENT
        return when {
            ifn.startsWith("wlan") -> "wifi"
            ifn.startsWith("rmnet") || ifn.startsWith("ccmni") || ifn.startsWith("ccinet") || ifn.startsWith("pdp") -> "mobile"
            ifn.startsWith("eth") -> "ethernet"
            ifn.startsWith("tun") || ifn.startsWith("ppp") || ifn.startsWith("tap") -> "vpn"
            else -> "other($ifn)"
        }
    }

    private fun vpnUnderlying(c: Context): String? {
        val cm = cmgr(c)
        val n = cm.activeNetwork ?: return Sentinels.ABSENT
        val caps = cm.getNetworkCapabilities(n) ?: return Sentinels.ABSENT
        if (!caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_VPN)) return Sentinels.ABSENT
        val u = buildList {
            if (caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_WIFI)) add("wifi")
            if (caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)) add("mobile")
            if (caps.hasTransport(android.net.NetworkCapabilities.TRANSPORT_ETHERNET)) add("ethernet")
        }
        return if (u.isEmpty()) "vpn (underlying not exposed)" else "vpn over ${u.joinToString("+")}"
    }

    private fun sysfsWlan(): String? {
        val names = NativeBridge.dirList("/sys/class/net")?.lineSequence()
            ?.map { it.trim() }?.filter { it.startsWith("wlan") }?.toList() ?: return null
        if (names.isEmpty()) return "no-wlan"
        return names.joinToString(",") { w ->
            "$w=${NativeBridge.readFile("/sys/class/net/$w/operstate")?.trim() ?: "?"}"
        }
    }
}
