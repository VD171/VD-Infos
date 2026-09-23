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
            am("attestation osPatchLevel") { ym(Attestation.record?.osPatchLevel) },
        ) + propTrio("ro.build.version.security_patch", map = ::ym), solution = ctx.getString(R.string.sol_attest_patch)))
        add(probe("attest:vendor_patch", ctx.getString(R.string.t_vendor_patch_level), Category.BUILD, listOf(
            am("attestation vendorPatchLevel") { ym(Attestation.record?.vendorPatchLevel) },
        ) + propTrio("ro.vendor.build.security_patch", map = ::ym), solution = ctx.getString(R.string.sol_attest_patch)))
        add(probe("attest:boot_patch", ctx.getString(R.string.t_boot_patch_level), Category.BOOT, listOf(
            am("attestation bootPatchLevel") { Attestation.record?.bootPatchLevel },
        ), solution = ctx.getString(R.string.sol_attest_patch)))

        add(probe("attest:verified_boot_state", ctx.getString(R.string.t_verified_boot_state), Category.INTEGRITY, listOf(
            am("attestation verifiedBootState") { Attestation.record?.verifiedBootState },
        ) + propTrio("ro.boot.verifiedbootstate", map = ::bootNorm), solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:device_locked", ctx.getString(R.string.t_device_locked), Category.INTEGRITY, listOf(
            am("attestation deviceLocked") { Attestation.record?.deviceLocked },
        ) + propTrio("ro.boot.flash.locked", map = ::lockNorm), solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:vbmeta", ctx.getString(R.string.t_verified_boot_hash_vbmeta_digest), Category.INTEGRITY, listOf(
            am("attestation verifiedBootHash") { Attestation.record?.verifiedBootHashHex },
        ) + propTrio("ro.boot.vbmeta.digest"), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:os_version", ctx.getString(R.string.t_os_version_attested), Category.BUILD, listOf(
            am("attestation osVersion") { osVer(Attestation.record?.osVersion) },
            jm("Build.VERSION.RELEASE") { android.os.Build.VERSION.RELEASE },
        ), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:security_level", ctx.getString(R.string.t_attestation_security_level), Category.INTEGRITY, listOf(
            am("attestation securityLevel") { Attestation.record?.securityLevel },
        ), solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:versions", ctx.getString(R.string.t_attestation_keymaster_version), Category.INTEGRITY, listOf(
            am("attestation record") {
                Attestation.record?.let { "att=${it.attestationVersion} km=${it.keymasterVersion}" }
            },
        ), solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:verified_boot_key", ctx.getString(R.string.t_verified_boot_key), Category.INTEGRITY, listOf(
            am("attestation rootOfTrust.verifiedBootKey") { Attestation.record?.verifiedBootKeyHex },
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
