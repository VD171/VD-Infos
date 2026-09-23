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

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        add(probe("attest:key_secure_hw", ctx.getString(R.string.t_key_inside_secure_hw), Category.INTEGRITY, listOf(
            @Suppress("DEPRECATION")
            jm("KeyInfo.isInsideSecureHardware", compare = false) { keyInfo()?.isInsideSecureHardware?.toString() },
            jm("KeyInfo.getSecurityLevel (API31+)", compare = false) {
                if (Build.VERSION.SDK_INT >= 31) keyInfo()?.let { secLevelName(it.securityLevel) } else null
            },
            am("attestation record securityLevel", compare = false) { Attestation.record?.securityLevel },
        ), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:keybox_ec_vs_rsa", ctx.getString(R.string.t_keybox_ec_vs_rsa), Category.INTEGRITY, listOf(
            am("EC attested securityLevel", compare = false) { Attestation.record?.securityLevel },
            am("RSA attested securityLevel", compare = false) { Attestation.rsaAttestSecurityLevel() },
            am("verdict: keybox covers both?", compare = false) {
                val ec = Attestation.record?.securityLevel
                val rsa = Attestation.rsaAttestSecurityLevel()
                val hw = setOf("tee", "strongbox")
                when {
                    ec == null -> "no-ec-attestation"
                    ec in hw && (rsa == null || rsa !in hw) -> "SUSPECT: EC=$ec RSA=${rsa ?: "none"} (keybox EC-only?)"
                    else -> "consistent (EC=$ec RSA=${rsa ?: "none"})"
                }
            },
        ), solution = ctx.getString(R.string.sol_boot)))

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
