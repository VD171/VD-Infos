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

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import org.bouncycastle.asn1.ASN1Boolean
import org.bouncycastle.asn1.ASN1Encodable
import org.bouncycastle.asn1.ASN1Enumerated
import org.bouncycastle.asn1.ASN1Integer
import org.bouncycastle.asn1.ASN1OctetString
import org.bouncycastle.asn1.ASN1Primitive
import org.bouncycastle.asn1.ASN1Sequence
import org.bouncycastle.asn1.ASN1Set
import org.bouncycastle.asn1.ASN1TaggedObject
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.security.spec.ECGenParameterSpec

object Attestation {

    private const val EXT_OID = "1.3.6.1.4.1.11129.2.1.17"
    private const val ALIAS = "vdinfos_attest"
    private val CHALLENGE = "vdinfos".toByteArray()

    data class Record(
        val securityLevel: String,
        val attestationVersion: Int,
        val keymasterVersion: Int,
        val verifiedBootState: String?,
        val deviceLocked: String?,
        val verifiedBootKeyHex: String?,
        val verifiedBootHashHex: String?,
        val osVersion: String?,
        val osPatchLevel: String?,
        val vendorPatchLevel: String?,
        val bootPatchLevel: String?,
        val brand: String?,
        val device: String?,
        val product: String?,
        val manufacturer: String?,
        val model: String?,
        val serial: String?,
        val signerValidityDays: Long?,
        val provisioning: String?,
        val provisioningByStructure: String?,
        val rootName: String?,
        val provisioningInfo: String?,
        val attestationAppId: String?,
    )

    val record: Record? by lazy { runCatching { build() }.getOrNull() }
    val error: String? by lazy { runCatching { build(); null }.exceptionOrNull()?.toString() }

    private fun build(): Record {
        var chain = gen(strongbox = false, deviceProps = true)
            ?: gen(strongbox = false, deviceProps = false)
            ?: gen(strongbox = true, deviceProps = false)
            ?: throw IllegalStateException("attestation unavailable")
        try {
            return parse(chain[0] as X509Certificate, chain)
        } finally {
            deleteKey()
        }
    }

    private fun gen(strongbox: Boolean, deviceProps: Boolean): Array<java.security.cert.Certificate>? =
        runCatching {
            deleteKey()
            val b = KeyGenParameterSpec.Builder(
                ALIAS, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setAttestationChallenge(CHALLENGE)
            if (strongbox) b.setIsStrongBoxBacked(true)
            if (deviceProps && Build.VERSION.SDK_INT >= 31) b.setDevicePropertiesAttestationIncluded(true)
            val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
            kpg.initialize(b.build())
            kpg.generateKeyPair()
            val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            ks.getCertificateChain(ALIAS)?.takeIf { it.isNotEmpty() }
        }.getOrNull()

    private const val PROV_OID = "1.3.6.1.4.1.11129.2.1.30"
    private val PROV_NAMES = mapOf(1L to "certs_issued", 3L to "manufacturer", 4L to "attested_entity", 6L to "lost_device")

    private fun rdn(dn: String, key: String): String? =
        dn.split(",").map { it.trim() }.firstOrNull { it.startsWith("$key=") }?.substringAfter("=")?.trim('"')

    private class Cbor(val b: ByteArray) {
        var i = 0
        fun read(): Any? {
            val ib = b[i++].toInt() and 0xFF; val mt = ib ushr 5; val ai = ib and 31
            var n = ai.toLong()
            if (ai in 24..27) { n = 0; repeat(1 shl (ai - 24)) { n = (n shl 8) or (b[i++].toLong() and 0xFF) } }
            else if (ai > 27) error("CBOR ai=$ai")
            return when (mt) {
                0 -> n
                1 -> -1 - n
                2 -> b.copyOfRange(i, i + n.toInt()).also { i += n.toInt() }.joinToString("") { "%02x".format(it) }
                3 -> String(b, i, n.toInt(), Charsets.UTF_8).also { i += n.toInt() }
                4 -> List(n.toInt()) { read() }
                5 -> (0 until n).associate { read() to read() }
                7 -> when (ai) { 20 -> false; 21 -> true; 22 -> null; else -> n }
                else -> error("CBOR mt=$mt")
            }
        }
    }

    private fun kd(leaf: X509Certificate): ASN1Sequence? {
        val ext = leaf.getExtensionValue(EXT_OID) ?: return null
        val outer = ASN1Primitive.fromByteArray(ext) as ASN1OctetString
        return ASN1Primitive.fromByteArray(outer.octets) as ASN1Sequence
    }

    private fun parse(leaf: X509Certificate, chain: Array<java.security.cert.Certificate>): Record {
        val kd = kd(leaf) ?: throw IllegalStateException("no attestation extension")
        val attVersion = intOf(kd.getObjectAt(0))
        val attSec = intOf(kd.getObjectAt(1))
        val kmVersion = intOf(kd.getObjectAt(2))

        var rot: ASN1Sequence? = null
        var osPatch: String? = null; var osVer: String? = null
        var vendorPatch: String? = null; var bootPatch: String? = null
        var brand: String? = null; var device: String? = null; var product: String? = null
        var manufacturer: String? = null; var model: String? = null; var serial: String? = null
        var attAppId: String? = null
        for (idx in intArrayOf(7, 6)) {
            val al = kd.getObjectAt(idx) as? ASN1Sequence ?: continue
            for (e in al) {
                val t = e as? ASN1TaggedObject ?: continue
                val base = t.baseObject.toASN1Primitive()
                when (t.tagNo) {
                    704 -> if (rot == null) rot = ASN1Sequence.getInstance(base)
                    705 -> if (osVer == null) osVer = intOf(base).toString()
                    706 -> if (osPatch == null) osPatch = intOf(base).toString()
                    709 -> if (attAppId == null) attAppId = parseAppId(base)
                    710 -> if (brand == null) brand = octStr(base)
                    711 -> if (device == null) device = octStr(base)
                    712 -> if (product == null) product = octStr(base)
                    713 -> if (serial == null) serial = octStr(base)
                    716 -> if (manufacturer == null) manufacturer = octStr(base)
                    717 -> if (model == null) model = octStr(base)
                    718 -> if (vendorPatch == null) vendorPatch = intOf(base).toString()
                    719 -> if (bootPatch == null) bootPatch = intOf(base).toString()
                }
            }
        }

        var vbState: String? = null; var locked: String? = null
        var vbKey: String? = null; var vbHash: String? = null
        rot?.let { r ->
            runCatching { vbKey = hex((r.getObjectAt(0) as ASN1OctetString).octets) }
            locked = if ((r.getObjectAt(1) as ASN1Boolean).isTrue) "yes" else "no"
            vbState = bootState(intOf(r.getObjectAt(2)))
            if (r.size() >= 4) runCatching { vbHash = hex((r.getObjectAt(3) as ASN1OctetString).octets) }
        }

        var signerDays: Long? = null; var provisioning: String? = null
        if (chain.size >= 2) {
            val signer = chain[1] as X509Certificate
            signerDays = (signer.notAfter.time - signer.notBefore.time) / 86400000L
            provisioning = if (signerDays > 730) "batch_keybox" else "rkp"
        }
        val x509 = chain.map { it as X509Certificate }
        val provisioningByStructure = if (x509.size >= 2) {
            val n = x509[x509.size - 2].subjectX500Principal.getName(javax.security.auth.x500.X500Principal.RFC1779)
            if (rdn(n, "CN") == "Droid CA2" && rdn(n, "O") == "Google LLC") "rkp" else "batch_keybox"
        } else null
        val rootName = x509.lastOrNull()?.let {
            rdn(it.subjectX500Principal.getName(javax.security.auth.x500.X500Principal.RFC1779), "CN")
        }
        val provisioningInfo = x509.asReversed().firstNotNullOfOrNull { c ->
            c.getExtensionValue(PROV_OID)?.let { raw ->
                runCatching {
                    val cbor = ASN1OctetString.getInstance(ASN1OctetString.getInstance(raw).octets).octets
                    Cbor(cbor).read().let { v -> if (v is Map<*, *>) v.entries.joinToString(", ") { (k, x) -> "${PROV_NAMES[k] ?: k}=$x" } else v.toString() }
                }.getOrElse { "unparsable: $it" }
            }
        }

        return Record(
            securityLevel = secLevel(attSec), attestationVersion = attVersion, keymasterVersion = kmVersion,
            verifiedBootState = vbState, deviceLocked = locked,
            verifiedBootKeyHex = vbKey, verifiedBootHashHex = vbHash,
            osVersion = osVer, osPatchLevel = osPatch, vendorPatchLevel = vendorPatch, bootPatchLevel = bootPatch,
            brand = brand, device = device, product = product, manufacturer = manufacturer, model = model,
            serial = serial, signerValidityDays = signerDays, provisioning = provisioning,
            provisioningByStructure = provisioningByStructure, rootName = rootName, provisioningInfo = provisioningInfo,
            attestationAppId = attAppId,
        )
    }

    private fun parseAppId(base: ASN1Primitive): String? = runCatching {
        val inner = ASN1OctetString.getInstance(base).octets
        val seq = ASN1Primitive.fromByteArray(inner) as ASN1Sequence
        val pkgs = (seq.getObjectAt(0) as ASN1Set).map {
            String(((it as ASN1Sequence).getObjectAt(0) as ASN1OctetString).octets)
        }
        val sigs = (seq.getObjectAt(1) as ASN1Set).map { hex((it as ASN1OctetString).octets) }
        "pkgs=${pkgs.joinToString("|")} sigs=${sigs.joinToString("|")}"
    }.getOrNull()

    fun rsaAttestSecurityLevel(): String? = runCatching {
        val alias = "vdinfos_attest_rsa"
        runCatching {
            val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            if (ks.containsAlias(alias)) ks.deleteEntry(alias)
        }
        val b = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
            .setDigests(KeyProperties.DIGEST_SHA256)
            .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            .setAttestationChallenge(CHALLENGE)
        val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
        kpg.initialize(b.build())
        kpg.generateKeyPair()
        try {
            val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            val chain = ks.getCertificateChain(alias)?.takeIf { it.isNotEmpty() }
                ?: return@runCatching "no-chain"
            val kd = kd(chain[0] as X509Certificate) ?: return@runCatching "no-extension"
            secLevel(intOf(kd.getObjectAt(1)))
        } finally {
            runCatching {
                val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
                if (ks.containsAlias(alias)) ks.deleteEntry(alias)
            }
        }
    }.getOrNull()

    private fun deleteKey() = runCatching {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (ks.containsAlias(ALIAS)) ks.deleteEntry(ALIAS)
    }

    private fun intOf(e: ASN1Encodable?): Int = when (e) {
        is ASN1Integer -> e.value.toInt()
        is ASN1Enumerated -> e.value.toInt()
        else -> -1
    }

    private fun octStr(e: ASN1Primitive): String? =
        runCatching { String((ASN1OctetString.getInstance(e)).octets) }.getOrNull()

    private fun secLevel(v: Int) = when (v) { 0 -> "software"; 1 -> "tee"; 2 -> "strongbox"; else -> "?$v" }
    private fun bootState(v: Int) = when (v) { 0 -> "verified"; 1 -> "self-signed"; 2 -> "unverified"; 3 -> "failed"; else -> "?$v" }
    private fun hex(b: ByteArray) = b.joinToString("") { "%02x".format(it) }
}
