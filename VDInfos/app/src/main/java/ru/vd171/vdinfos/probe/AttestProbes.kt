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
import ru.vd171.vdinfos.R
import ru.vd171.vdinfos.core.model.Category
import ru.vd171.vdinfos.engine.ProbeTask

object AttestProbes {

    private fun ym(s: String?): String? = s?.replace("-", "")?.take(6)?.takeIf { it.length == 6 }
    private fun bootNorm(s: String?) = s?.let { if (it == "green") "verified" else it }
    private fun lockNorm(s: String?) = s?.let { if (it == "1") "yes" else if (it == "0") "no" else it }
    private fun osVer(s: String?): String? {
        val v = s?.toIntOrNull() ?: return s
        if (v < 100) return "$v"
        val a = v / 10000; val b = v / 100 % 100; val c = v % 100
        return if (b == 0 && c == 0) "$a" else if (c == 0) "$a.$b" else "$a.$b.$c"
    }

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        add(probe("attest:os_patch", ctx.getString(R.string.t_os_security_patch_level), Category.BUILD, listOf(
            am("attestation osPatchLevel", tag = "TEE DP") { ym(Attestation.record?.osPatchLevel) },
            am("attestation osPatchLevel", tag = "TEE Plain") { ym(Attestation.recordPlain?.osPatchLevel) },
            am("attestation osPatchLevel", tag = "TEE Alt") { ym(Attestation.recordAltChallenge?.osPatchLevel) },
            am("attestation osPatchLevel", tag = "TEE RSA") { ym(Attestation.recordRsa?.osPatchLevel) },
            am("attestation osPatchLevel", compare = false, tag = "TEE StrongBox") { ym(Attestation.recordStrongBox?.osPatchLevel) },
        ) + propTrio("ro.build.version.security_patch", map = ::ym), solution = ctx.getString(R.string.sol_attest_patch)))
        add(probe("attest:vendor_patch", ctx.getString(R.string.t_vendor_patch_level), Category.BUILD, listOf(
            am("attestation vendorPatchLevel", tag = "TEE DP") { ym(Attestation.record?.vendorPatchLevel) },
            am("attestation vendorPatchLevel", tag = "TEE Plain") { ym(Attestation.recordPlain?.vendorPatchLevel) },
            am("attestation vendorPatchLevel", tag = "TEE Alt") { ym(Attestation.recordAltChallenge?.vendorPatchLevel) },
            am("attestation vendorPatchLevel", tag = "TEE RSA") { ym(Attestation.recordRsa?.vendorPatchLevel) },
            am("attestation vendorPatchLevel", compare = false, tag = "TEE StrongBox") { ym(Attestation.recordStrongBox?.vendorPatchLevel) },
        ) + propTrio("ro.vendor.build.security_patch", map = ::ym), solution = ctx.getString(R.string.sol_attest_patch)))
        add(probe("attest:boot_patch", ctx.getString(R.string.t_boot_patch_level), Category.BOOT, listOf(
            am("attestation bootPatchLevel", tag = "TEE DP") { Attestation.record?.bootPatchLevel },
            am("attestation bootPatchLevel", tag = "TEE Plain") { Attestation.recordPlain?.bootPatchLevel },
            am("attestation bootPatchLevel", tag = "TEE Alt") { Attestation.recordAltChallenge?.bootPatchLevel },
            am("attestation bootPatchLevel", tag = "TEE RSA") { Attestation.recordRsa?.bootPatchLevel },
            am("attestation bootPatchLevel", compare = false, tag = "TEE StrongBox") { Attestation.recordStrongBox?.bootPatchLevel },
        ), solution = ctx.getString(R.string.sol_attest_patch)))

        add(probe("attest:verified_boot_state", ctx.getString(R.string.t_verified_boot_state), Category.INTEGRITY, listOf(
            am("attestation verifiedBootState", tag = "TEE DP") { Attestation.record?.verifiedBootState },
            am("attestation verifiedBootState", tag = "TEE Plain") { Attestation.recordPlain?.verifiedBootState },
            am("attestation verifiedBootState", tag = "TEE Alt") { Attestation.recordAltChallenge?.verifiedBootState },
            am("attestation verifiedBootState", tag = "TEE RSA") { Attestation.recordRsa?.verifiedBootState },
            am("attestation verifiedBootState", compare = false, tag = "TEE StrongBox") { Attestation.recordStrongBox?.verifiedBootState },
        ) + propTrio("ro.boot.verifiedbootstate", map = ::bootNorm), solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:device_locked", ctx.getString(R.string.t_device_locked), Category.INTEGRITY, listOf(
            am("attestation deviceLocked", tag = "TEE DP") { Attestation.record?.deviceLocked },
            am("attestation deviceLocked", tag = "TEE Plain") { Attestation.recordPlain?.deviceLocked },
            am("attestation deviceLocked", tag = "TEE Alt") { Attestation.recordAltChallenge?.deviceLocked },
            am("attestation deviceLocked", tag = "TEE RSA") { Attestation.recordRsa?.deviceLocked },
            am("attestation deviceLocked", compare = false, tag = "TEE StrongBox") { Attestation.recordStrongBox?.deviceLocked },
        ) + propTrio("ro.boot.flash.locked", map = ::lockNorm), solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:vbmeta", ctx.getString(R.string.t_verified_boot_hash_vbmeta_digest), Category.INTEGRITY, listOf(
            am("attestation verifiedBootHash", tag = "TEE DP") { Attestation.record?.verifiedBootHashHex },
            am("attestation verifiedBootHash", tag = "TEE Plain") { Attestation.recordPlain?.verifiedBootHashHex },
            am("attestation verifiedBootHash", tag = "TEE Alt") { Attestation.recordAltChallenge?.verifiedBootHashHex },
            am("attestation verifiedBootHash", tag = "TEE RSA") { Attestation.recordRsa?.verifiedBootHashHex },
            am("attestation verifiedBootHash", compare = false, tag = "TEE StrongBox") { Attestation.recordStrongBox?.verifiedBootHashHex },
        ) + propTrio("ro.boot.vbmeta.digest"), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:challenge_echo", ctx.getString(R.string.t_attestation_challenge_echo), Category.INTEGRITY, listOf(
            jm("challenge we sent") { Attestation.CHALLENGE_HEX },
            am("challenge echoed in the attestation") { Attestation.record?.challengeHex },
        ), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:os_version", ctx.getString(R.string.t_os_version_attested), Category.BUILD, listOf(
            am("attestation osVersion", tag = "TEE DP") { osVer(Attestation.record?.osVersion) },
            am("attestation osVersion", tag = "TEE Plain") { osVer(Attestation.recordPlain?.osVersion) },
            am("attestation osVersion", tag = "TEE Alt") { osVer(Attestation.recordAltChallenge?.osVersion) },
            am("attestation osVersion", tag = "TEE RSA") { osVer(Attestation.recordRsa?.osVersion) },
            am("attestation osVersion", compare = false, tag = "TEE StrongBox") { osVer(Attestation.recordStrongBox?.osVersion) },
            jm("Build.VERSION.RELEASE") { android.os.Build.VERSION.RELEASE },
        ), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:security_level", ctx.getString(R.string.t_attestation_security_level), Category.INTEGRITY, listOf(
            am("attestation securityLevel", tag = "TEE DP") { Attestation.record?.securityLevel },
            am("attestation securityLevel", tag = "TEE Plain") { Attestation.recordPlain?.securityLevel },
            am("attestation securityLevel", tag = "TEE Alt") { Attestation.recordAltChallenge?.securityLevel },
            am("attestation securityLevel", tag = "TEE RSA") { Attestation.recordRsa?.securityLevel },
            am("attestation securityLevel", compare = false, tag = "TEE StrongBox") { Attestation.recordStrongBox?.securityLevel },
        ), solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:versions", ctx.getString(R.string.t_attestation_keymaster_version), Category.INTEGRITY, listOf(
            am("attestation record") {
                Attestation.record?.let { "att=${it.attestationVersion} km=${it.keymasterVersion}" }
            },
        ), solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:verified_boot_key", ctx.getString(R.string.t_verified_boot_key), Category.INTEGRITY, listOf(
            am("attestation rootOfTrust.verifiedBootKey", tag = "TEE DP") { Attestation.record?.verifiedBootKeyHex },
            am("attestation rootOfTrust.verifiedBootKey", tag = "TEE Plain") { Attestation.recordPlain?.verifiedBootKeyHex },
            am("attestation rootOfTrust.verifiedBootKey", tag = "TEE Alt") { Attestation.recordAltChallenge?.verifiedBootKeyHex },
            am("attestation rootOfTrust.verifiedBootKey", tag = "TEE RSA") { Attestation.recordRsa?.verifiedBootKeyHex },
            am("attestation rootOfTrust.verifiedBootKey", compare = false, tag = "TEE StrongBox") { Attestation.recordStrongBox?.verifiedBootKeyHex },
        ), sensitive = true, solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:provisioning", ctx.getString(R.string.t_attestation_provisioning_signer), Category.INTEGRITY, listOf(
            am("attestation signer validity (>730d = batch)") { Attestation.record?.provisioning },
            am("attestation chain shape (Droid CA2 under root)") { Attestation.record?.provisioningByStructure },
            am("attestation signer validity days", compare = false) {
                Attestation.record?.signerValidityDays?.let { "${it}d" }
            },
            am("attestation root CN", compare = false) { Attestation.record?.rootName },
        ), solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:provisioning_info", ctx.getString(R.string.t_attestation_provisioning_info), Category.INTEGRITY, listOf(
            am("ProvisioningInfo ext 1.3.6.1.4.1.11129.2.1.30", compare = false) {
                Attestation.record?.let { it.provisioningInfo ?: "absent" }
            },
        ), solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:available", ctx.getString(R.string.t_attestation_available), Category.INTEGRITY, listOf(
            am("KeyStore attestation") { if (Attestation.record != null) "yes" else "no (${Attestation.error ?: "unavailable"})" },
        )))
    }
}
