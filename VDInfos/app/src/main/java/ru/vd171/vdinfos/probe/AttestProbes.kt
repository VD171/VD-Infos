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

    private fun pixelCanonicalFor(ctx: Context, fingerprint: String?, field: String): String? {
        val parts = (fingerprint ?: return null).split("/")
        if (parts.size < 5) return null
        val device = parts[2].substringBefore(":")
        val incremental = parts[4].substringBefore(":")
        if (device.isEmpty() || incremental.isEmpty()) return null
        val json = runCatching { ctx.assets.open("data/pixel_index.json").bufferedReader().use { it.readText() } }.getOrNull() ?: return null
        val dev = runCatching { org.json.JSONObject(json).optJSONObject("devices")?.optJSONObject(device) }.getOrNull() ?: return null
        val channels = dev.optJSONObject("channels") ?: return null
        val keys = channels.keys()
        while (keys.hasNext()) {
            val c = channels.optJSONObject(keys.next()) ?: continue
            if (c.optString("incremental") == incremental) return c.optString(field).takeIf { s -> s.isNotEmpty() }
        }
        return null
    }

    private fun pixelCanonical(ctx: Context, field: String): String? =
        pixelCanonicalFor(ctx, android.os.Build.FINGERPRINT, field)

    private fun hexDigest(s: String?): String? =
        s?.lowercase()?.filter { it in "0123456789abcdef" }?.takeIf { it.length >= 32 }

    private fun canonicalVbmeta(ctx: Context): String? = hexDigest(pixelCanonical(ctx, "vbmeta_digest"))

    fun tasks(ctx: Context): List<ProbeTask> = buildList {

        add(probe("attest:os_patch", ctx.getString(R.string.t_os_security_patch_level), Category.BUILD, listOf(
            am("attestation osPatchLevel", tag = "TEE DP") { ym(Attestation.record?.osPatchLevel) },
            am("attestation osPatchLevel", tag = "TEE Plain") { ym(Attestation.recordPlain?.osPatchLevel) },
            am("attestation osPatchLevel", tag = "TEE Alt") { ym(Attestation.recordAltChallenge?.osPatchLevel) },
            am("attestation osPatchLevel", tag = "TEE RSA") { ym(Attestation.recordRsa?.osPatchLevel) },
            am("attestation osPatchLevel", compare = false, tag = "TEE StrongBox") { ym(Attestation.recordStrongBox?.osPatchLevel) },
            jm("canonical security patch (Pixel DB, exact claimed build)") { ym(pixelCanonical(it, "security_patch")) },
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
            jm("canonical vbmeta digest (Pixel DB, exact claimed build)") { canonicalVbmeta(it) },
        ) + propTrio("ro.boot.vbmeta.digest"), solution = ctx.getString(R.string.sol_boot)))

        add(probe("attest:prov_vbmeta_reuse", ctx.getString(R.string.t_prov_vbmeta_reuse), Category.INTEGRITY, listOf(
            am("RKP vbmeta_digest (.30)") { hexDigest(Attestation.record?.provVbmetaDigest) },
            am("runtime verifiedBootHash (attestation)") { hexDigest(Attestation.record?.verifiedBootHashHex) },
            jm("canonical vbmeta of the keybox's own claimed build (Pixel DB)") { pixelCanonicalFor(it, Attestation.record?.provFingerprint, "vbmeta_digest").let(::hexDigest) },
        ), solution = ctx.getString(R.string.sol_prov_vbmeta_reuse)))

        add(probe("attest:prov_fingerprint", ctx.getString(R.string.t_prov_fingerprint), Category.INTEGRITY, listOf(
            am("RKP fingerprint (.30)") { Attestation.record?.provFingerprint },
            jm("device fingerprint (Build.FINGERPRINT)") { android.os.Build.FINGERPRINT },
        ), solution = ctx.getString(R.string.sol_prov_fingerprint)))

        add(probe("attest:device_id", ctx.getString(R.string.t_device_id), Category.INTEGRITY, listOf(
            am("attestation identity (brand/device/manufacturer)") {
                Attestation.record?.let { r ->
                    if (r.brand == null && r.device == null && r.manufacturer == null) null
                    else "${r.brand}/${r.device}/${r.manufacturer}"
                }
            },
            jm("device identity (Build brand/device/manufacturer)") {
                "${android.os.Build.BRAND}/${android.os.Build.DEVICE}/${android.os.Build.MANUFACTURER}"
            },
            am("attestation product/model (codename, may differ from marketing)", compare = false) {
                Attestation.record?.let { r -> "${r.product}/${r.model}" }
            },
            jm("Build product/model", compare = false) {
                "${android.os.Build.PRODUCT}/${android.os.Build.MODEL}"
            },
        ), solution = ctx.getString(R.string.sol_device_id)))

        add(probe("attest:prov_manufacturer", ctx.getString(R.string.t_prov_manufacturer), Category.INTEGRITY, listOf(
            am("RKP manufacturer (.30)") { Attestation.record?.provManufacturer?.lowercase() },
            jm("device manufacturer (Build.MANUFACTURER)") { android.os.Build.MANUFACTURER.lowercase() },
        ), solution = ctx.getString(R.string.sol_prov_manufacturer)))

        add(probe("attest:prov_verdict", ctx.getString(R.string.t_prov_verdict), Category.INTEGRITY, listOf(
            jm("expected: RKP verified_boot is approved") { Attestation.record?.provBootVerdict?.let { "APPROVED" } },
            am("RKP verified_boot (.30)") { Attestation.record?.provBootVerdict },
        ), solution = ctx.getString(R.string.sol_prov_verdict)))

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
            am("verifiedBootKey all-zero? (zeroed rootOfTrust = keybox/spoof tell)", compare = false) {
                Attestation.record?.verifiedBootKeyHex?.let {
                    if (it.isNotEmpty() && it.all { ch -> ch == '0' }) "ALL-ZERO (tell)" else "non-zero"
                }
            },
        ), sensitive = true, solution = ctx.getString(R.string.sol_boot)))
        add(probe("attest:synth_paradox", ctx.getString(R.string.t_attest_synth_paradox), Category.INTEGRITY, listOf(
            jm("expected: hardware attestation integrity agrees with on-device root state") { "coherent" },
            jm("attestation locked+verified yet a root dir is visible user-side") {
                val r = Attestation.record ?: return@jm null
                val locked = r.deviceLocked?.lowercase() in listOf("yes", "true", "locked")
                val verified = r.verifiedBootState?.lowercase() in listOf("verified", "green")
                val adb = NativeBridge.statPresence("/data/adb", 0)
                val rootVisible = adb == "present" || adb == "present(denied)"
                if (locked && verified && rootVisible)
                    "PARADOX: attestation says locked+verified but /data/adb is visible (fake-relock / keybox spoof)"
                else "coherent"
            },
            jm("detail", compare = false) {
                val r = Attestation.record ?: return@jm null
                "locked=${r.deviceLocked} bootstate=${r.verifiedBootState} /data/adb=${NativeBridge.statPresence("/data/adb", 0)}"
            },
        ), solution = ctx.getString(R.string.sol_boot)))
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
