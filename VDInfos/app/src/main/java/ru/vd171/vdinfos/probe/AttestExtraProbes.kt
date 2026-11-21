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
import android.content.pm.PackageManager
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyInfo
import android.security.keystore.KeyProperties
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.MessageDigest

object AttestExtraProbes {

    private const val ALIAS = "vdinfos_keyinfo"
    private const val FEATURE_STRONGBOX = "android.hardware.strongbox_keystore"
    private const val OK = "ok"

    private fun serialFp(serial: String?): String? {
        if (serial.isNullOrEmpty()) return null
        return MessageDigest.getInstance("SHA-256")
            .digest(serial.toByteArray()).joinToString("") { "%02x".format(it) }.take(16)
    }

    private fun ownSigSha256(c: Context): String? = runCatching {
        val md = MessageDigest.getInstance("SHA-256")
        if (Build.VERSION.SDK_INT >= 28) {
            val pi = c.packageManager.getPackageInfo(c.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            val sig = pi.signingInfo?.apkContentsSigners?.firstOrNull() ?: return@runCatching null
            md.digest(sig.toByteArray()).joinToString("") { "%02x".format(it) }
        } else {
            @Suppress("DEPRECATION", "PackageManagerGetSignatures")
            val pi = c.packageManager.getPackageInfo(c.packageName, PackageManager.GET_SIGNATURES)
            @Suppress("DEPRECATION")
            val sig = pi.signatures?.firstOrNull() ?: return@runCatching null
            md.digest(sig.toByteArray()).joinToString("") { "%02x".format(it) }
        }
    }.getOrNull()

    private fun keyInfo(): KeyInfo? = runCatching {
        runCatching {
            val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            if (ks.containsAlias(ALIAS)) ks.deleteEntry(ALIAS)
        }
        val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
        kpg.initialize(
            KeyGenParameterSpec.Builder(ALIAS, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256).build()
        )
        val kp = kpg.generateKeyPair()
        val factory = KeyFactory.getInstance(kp.private.algorithm, "AndroidKeyStore")
        factory.getKeySpec(kp.private, KeyInfo::class.java) as KeyInfo
    }.getOrNull()

    private fun secLevelName(v: Int) = when (v) {
        0 -> "software"; 1 -> "tee"; 2 -> "strongbox"; -1 -> "unrestricted"; else -> "?$v"
    }

    private fun fmtAttest(r: Attestation.Record?): String {
        if (r == null) return "absent (no chain / attestation failed)"
        return "sec=${r.securityLevel} boot=${r.verifiedBootState ?: "-"} locked=${r.deviceLocked ?: "-"} " +
            "rot=${r.verifiedBootKeyHex?.take(12) ?: "-"} patch=${r.osPatchLevel ?: "-"} root=${r.rootName ?: "-"}"
    }

    private fun rotTuple(r: Attestation.Record?): String? {
        r ?: return null
        return "boot=${r.verifiedBootState ?: "-"} locked=${r.deviceLocked ?: "-"} " +
            "rot=${r.verifiedBootKeyHex?.take(12) ?: "-"} root=${r.rootName ?: "-"}"
    }

    private fun rsaLeaf(): Attestation.Record? =
        Attestation.recordRsa?.takeIf { it.leafKeyAlg?.contains("RSA", true) == true }
    private fun rsaLeafIsRkp(): Boolean =
        rsaLeaf()?.let { it.provisioningByStructure == "rkp" || it.provisioningInfo != null } == true
    private fun rsaLeafExpected(): String? = rsaLeaf()?.let { if (rsaLeafIsRkp()) "EC" else "RSA" }
    private fun rsaLeafSigFamily(): String? {
        if (Attestation.recordRsa == null) return "ABSENT (keybox has no RSA key)"
        val sig = rsaLeaf()?.leafSigAlg ?: return null
        return when {
            sig.contains("RSA", true) -> "RSA"
            sig.contains("EC", true) -> "EC"
            else -> sig
        }
    }
    private fun rsaLeafDetail(): String {
        val rsa = Attestation.recordRsa
        val ec = Attestation.recordPlain ?: Attestation.record
        val note = rsaLeaf()?.let {
            if (rsaLeafIsRkp()) "RKP chain (EC-only attestation key): an RSA leaf is ECDSA-signed by design"
            else "batch keybox (not RKP): an RSA leaf must be RSA-signed; EC here is a real divergence"
        }
        return "RSA leaf: key=${rsa?.leafKeyAlg ?: "-"} sig=${rsa?.leafSigAlg ?: "-"}\n" +
            "EC leaf:  key=${ec?.leafKeyAlg ?: "-"} sig=${ec?.leafSigAlg ?: "-"}" +
            (note?.let { "\n$it" } ?: "")
    }

    private fun batchKeyEc(): Attestation.Record? = Attestation.recordPlain ?: Attestation.record
    private fun batchKeyExpected(): String? = batchKeyActual()?.let { OK }
    private fun batchKeyActual(): String? {
        val ec = batchKeyEc() ?: return null
        val rsa = Attestation.recordRsa ?: return null
        ec.signerSpkiHex ?: ec.signerSerial ?: return null
        rsa.signerSpkiHex ?: rsa.signerSerial ?: return null
        val remoteProvisioned = ec.provisioning == "rkp" || rsa.provisioning == "rkp"
        val tells = buildList {
            val sharedSerial = ec.signerSerial != null && ec.signerSerial == rsa.signerSerial
            val sharedKey = ec.signerSpkiHex != null && ec.signerSpkiHex == rsa.signerSpkiHex
            if ((sharedSerial || sharedKey) && !remoteProvisioned)
                add("SHARED signer (one attestation key signs EC and RSA = keybox EC-only)")
            if (ec.signerSubjectSerialNo != null && rsa.signerSubjectSerialNo != null &&
                ec.signerSubjectSerialNo != rsa.signerSubjectSerialNo)
                add("divergent identity (subject serialNumber EC!=RSA = chains from different devices)")
        }
        return if (tells.isEmpty()) OK else tells.joinToString("; ")
    }
    private fun batchKeyDetail(fp: Boolean): String {
        val ec = batchKeyEc(); val rsa = Attestation.recordRsa
        fun s(x: String?) = (if (fp) serialFp(x) else x) ?: "-"
        return "provisioning: EC=${ec?.provisioning ?: "-"} RSA=${rsa?.provisioning ?: "-"}\n" +
            "EC chain:  serial=${s(ec?.signerSerial)} key=${ec?.signerSpkiHex ?: "-"} id=${s(ec?.signerSubjectSerialNo)}\n" +
            "RSA chain: serial=${s(rsa?.signerSerial)} key=${rsa?.signerSpkiHex ?: "-"} id=${s(rsa?.signerSubjectSerialNo)}"
    }

    private fun nativeRec(): Attestation.Record? = Attestation.recordNative
    private fun jvmRec(): Attestation.Record? = Attestation.recordPlain ?: Attestation.record

    private fun nativeVsJvmExpected(): String? {
        if (!NativeBridge.ks2Available) return null
        nativeRec() ?: return null
        jvmRec() ?: return null
        return OK
    }

    private fun nativeVsJvmVerdict(): String? {
        if (!NativeBridge.ks2Available) return null
        val nat = nativeRec() ?: return null
        val jvm = jvmRec() ?: return null
        val tells = buildList {
            if (nat.challengeHex != Attestation.CHALLENGE_NAT_HEX)
                add("native challenge echo != 92B request (binder path faked)")
            if (nat.securityLevel != jvm.securityLevel)
                add("securityLevel native=${nat.securityLevel} jvm=${jvm.securityLevel}")
            if (rotTuple(nat) != rotTuple(jvm))
                add("root of trust native!=jvm (one path rewrites boot state)")
            if (nat.signerSerial != null && jvm.signerSerial != null && nat.signerSerial != jvm.signerSerial)
                add("batch signer serial native!=jvm")
            if (nat.signerSpkiHex != null && jvm.signerSpkiHex != null && nat.signerSpkiHex != jvm.signerSpkiHex)
                add("batch signer key native!=jvm")
        }
        return if (tells.isEmpty()) OK else tells.joinToString("; ")
    }

    private fun nativeVsJvmDetail(fp: Boolean): String {
        val nat = nativeRec(); val jvm = jvmRec()
        fun s(x: String?) = (if (fp) serialFp(x) else x) ?: "-"
        return "native: chal=${nat?.challengeHex?.take(24) ?: "-"} sec=${nat?.securityLevel ?: "-"} ${rotTuple(nat) ?: "-"} signer=${s(nat?.signerSerial)}\n" +
            "jvm:    chal=${jvm?.challengeHex?.take(24) ?: "-"} sec=${jvm?.securityLevel ?: "-"} ${rotTuple(jvm) ?: "-"} signer=${s(jvm?.signerSerial)}"
    }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        add(probe("attest:sb_tee_signer", ctx.getString(R.string.t_sb_tee_signer), Category.INTEGRITY, listOf(
            jm("StrongBox keystore feature", compare = false) { c ->
                if (c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) "present" else "absent"
            },
            jm("StrongBox and TEE have distinct signers (expected)") { c ->
                if (!c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) return@jm null
                "distinct"
            },
            jm("StrongBox signer vs TEE signer") { c ->
                if (!c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) return@jm null
                val tee = Attestation.record?.signerSerial ?: return@jm null
                val sb = Attestation.recordStrongBox?.signerSerial ?: return@jm "STRONGBOX_MISSING"
                if (sb == tee) "SHARED" else "distinct"
            },
            jmReveal(
                "signers (serial fingerprint)",
                reveal = {
                    val sb = Attestation.recordStrongBox
                    val tee = Attestation.record
                    val none = ru.vd171.vdinfos.core.model.Sentinels.NONE
                    "strongbox=${sb?.signerSubject}/${sb?.signerSerial ?: none}\n" +
                        "tee=${tee?.signerSubject}/${tee?.signerSerial ?: none}"
                },
                read = {
                    val sb = Attestation.recordStrongBox
                    val tee = Attestation.record
                    val none = ru.vd171.vdinfos.core.model.Sentinels.NONE
                    "strongbox=${sb?.signerSubject}/${serialFp(sb?.signerSerial) ?: none}\n" +
                        "tee=${tee?.signerSubject}/${serialFp(tee?.signerSerial) ?: none}"
                },
            ),
        )))

        add(probe("attest:strongbox_level", ctx.getString(R.string.t_strongbox_level), Category.INTEGRITY, listOf(
            jm("StrongBox keystore feature", compare = false) { c ->
                if (c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) "present" else "absent"
            },
            jm("expected: a StrongBox-backed key attests at StrongBox level") { c ->
                if (!c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) return@jm null
                "strongbox"
            },
            jm("StrongBox-backed attestationSecurityLevel") { c ->
                if (!c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) return@jm null
                Attestation.recordStrongBox?.securityLevel ?: return@jm "STRONGBOX_MISSING"
            },
            jm("StrongBox-backed keymasterSecurityLevel") { c ->
                if (!c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) return@jm null
                Attestation.recordStrongBox?.keymasterSecurityLevel ?: return@jm "STRONGBOX_MISSING"
            },
        )))

        add(probe("attest:key_secure_hw", ctx.getString(R.string.t_key_inside_secure_hw), Category.INTEGRITY, listOf(
            @Suppress("DEPRECATION")
            jm("KeyInfo.isInsideSecureHardware", compare = false) { keyInfo()?.isInsideSecureHardware?.toString() },
            jm("KeyInfo.getSecurityLevel (API31+)", compare = false) {
                if (Build.VERSION.SDK_INT >= 31) keyInfo()?.let { secLevelName(it.securityLevel) } else null
            },
            am("attestation record securityLevel", compare = false) { Attestation.record?.securityLevel },
        ), solution = ctx.getString(R.string.sol_secure_hw)))

        add(probe("attest:rot_ec_vs_rsa", ctx.getString(R.string.t_rot_ec_vs_rsa), Category.INTEGRITY, listOf(
            am("EC chain (sec/boot/locked/rot/root)", compare = false) { fmtAttest(Attestation.record) },
            am("RSA chain (sec/boot/locked/rot/root)", compare = false) { fmtAttest(Attestation.recordRsa) },
            am("root of trust via EC request") { rotTuple(Attestation.record) },
            am("root of trust via RSA request") { rotTuple(Attestation.recordRsa) },
        ), solution = ctx.getString(R.string.sol_attest_consistency)))

        add(probe("attest:leaf_sig_alg", ctx.getString(R.string.t_leaf_sig_alg), Category.INTEGRITY, listOf(
            am("expected RSA-leaf signer (RSA on batch keybox, EC under RKP)") { rsaLeafExpected() },
            am("RSA attestation leaf signature family") { rsaLeafSigFamily() },
            am("leaf key vs signature algorithm", compare = false) { rsaLeafDetail() },
        ), solution = ctx.getString(R.string.sol_attest_consistency)))

        add(probe("attest:batch_key_ec_vs_rsa", ctx.getString(R.string.t_batch_key_ec_vs_rsa), Category.INTEGRITY, listOf(
            am("expected: distinct key, same device identity") { batchKeyExpected() },
            am("EC chain signer vs RSA chain signer") { batchKeyActual() },
            amReveal("batch cert: EC vs RSA (serial / key / id)",
                reveal = { batchKeyDetail(fp = false) },
                read = { batchKeyDetail(fp = true) }),
        ), solution = ctx.getString(R.string.sol_attest_consistency)))

        add(probe("attest:native_vs_jvm", ctx.getString(R.string.t_native_vs_jvm), Category.INTEGRITY, listOf(
            am("native keystore2 lens", compare = false) {
                when {
                    !NativeBridge.ks2Available -> "unavailable (API<33 or load failed)"
                    Attestation.recordNative == null -> "present, no chain (${Attestation.errorNative ?: "?"})"
                    else -> "present"
                }
            },
            am("expected: native keystore2 chain matches JVM chain", tag = "92B") { nativeVsJvmExpected() },
            am("native keystore2 vs JVM attestation", tag = "92B") { nativeVsJvmVerdict() },
            amReveal("native vs JVM (challenge / sec / rot / signer)",
                reveal = { nativeVsJvmDetail(fp = false) },
                read = { nativeVsJvmDetail(fp = true) }),
        ), solution = ctx.getString(R.string.sol_attest_consistency)))

        add(probe("attest:sb_capability", ctx.getString(R.string.t_sb_capability), Category.INTEGRITY, listOf(
            jm("StrongBox keystore feature", compare = false) { c ->
                if (c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) "present" else "absent"
            },
            jm("expected: a secure element rejects unsupported params") { c ->
                if (!c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) return@jm null
                "all-rejected"
            },
            jm("params a secure element must reject") { c ->
                if (!c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) return@jm null
                val cap = Attestation.sbCapability() ?: return@jm null
                if (cap.contains("accepted")) "accepts-unsupported" else "all-rejected"
            },
            jm("per-param result", compare = false) { c ->
                if (!c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) return@jm null
                Attestation.sbCapability()
            },
        ), solution = ctx.getString(R.string.sol_secure_hw)))

        add(probe("attest:sb_enforced", ctx.getString(R.string.t_sb_enforced), Category.INTEGRITY, listOf(
            jm("expected: rootOfTrust is hardware-enforced") { c ->
                if (!c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) return@jm null
                if (Attestation.recordStrongBox == null) return@jm null
                "hw"
            },
            am("StrongBox key rootOfTrust enforcement level") { Attestation.recordStrongBox?.rotLevel },
            am("StrongBox key origin/purpose enforcement", compare = false) {
                val r = Attestation.recordStrongBox ?: return@am null
                "origin=${r.originLevel ?: "-"} purpose=${r.purposeLevel ?: "-"}"
            },
        ), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:sb_version", ctx.getString(R.string.t_sb_version), Category.INTEGRITY, listOf(
            am("attestation/keymaster version (TEE)", compare = false) {
                Attestation.record?.let { "att=${it.attestationVersion} km=${it.keymasterVersion}" }
            },
            am("attestation/keymaster version (StrongBox)", compare = false) {
                Attestation.recordStrongBox?.let { "att=${it.attestationVersion} km=${it.keymasterVersion}" }
            },
            am("moduleHash present (keymaster>=300)", compare = false) {
                val r = Attestation.recordStrongBox ?: Attestation.record ?: return@am null
                if (r.keymasterVersion >= 300) (if (r.moduleHashHex != null) "present" else "absent") else "n/a"
            },
        ), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:sb_chain", ctx.getString(R.string.t_sb_chain), Category.INTEGRITY, listOf(
            am("certificate chain (TEE: len / root)", compare = false) {
                Attestation.record?.let { "len=${it.chainLen} root=${it.rootName ?: "-"}" }
            },
            am("certificate chain (StrongBox: len / root)", compare = false) {
                Attestation.recordStrongBox?.let { "len=${it.chainLen} root=${it.rootName ?: "-"}" }
            },
        ), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:sb_rkp", ctx.getString(R.string.t_sb_rkp), Category.INTEGRITY, listOf(
            am("provisioning (TEE)", compare = false) {
                Attestation.record?.let { "${it.provisioning ?: "-"} info=${if (it.provisioningInfo != null) "present" else "absent"}" }
            },
            am("provisioning (StrongBox)", compare = false) {
                Attestation.recordStrongBox?.let { "${it.provisioning ?: "-"} info=${if (it.provisioningInfo != null) "present" else "absent"}" }
            },
        ), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:sb_latency", ctx.getString(R.string.t_sb_latency), Category.INTEGRITY, listOf(
            jm("StrongBox keystore feature", compare = false) { c ->
                if (c.packageManager.hasSystemFeature(FEATURE_STRONGBOX)) "present" else "absent"
            },
            am("StrongBox vs TEE key-gen time", compare = false) { Attestation.sbVsTeeLatency() },
        )))

        add(probe("attest:app_id", ctx.getString(R.string.t_attestation_app_id), Category.INTEGRITY, listOf(
            am("attestation attestationApplicationId", compare = false) { Attestation.record?.attestationAppId },
            jm("own package + signing sha256", compare = false) { c -> "pkg=${c.packageName} sig=${ownSigSha256(c) ?: "?"}" },
            jm("verdict: attestation binds THIS app?", compare = false) { c ->
                val att = Attestation.record?.attestationAppId ?: return@jm "no-attestation"
                val pkgOk = att.contains(c.packageName)
                val mySig = ownSigSha256(c)
                val sigOk = mySig != null && att.contains(mySig, ignoreCase = true)
                if (pkgOk && sigOk) "MATCH" else "MISMATCH (pkg=$pkgOk sig=$sigOk)"
            },
        ), solution = ctx.getString(R.string.sol_boot)))
    }
}
