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
    private const val ALIAS_PLAIN = "vdinfos_attest_plain"
    private const val ALIAS_SB = "vdinfos_attest_sb"
    private const val ALIAS_ALT = "vdinfos_attest_alt"
    private const val ALIAS_RSA = "vdinfos_attest_rsa2"
    private val PRIMARY = listOf(false to true, false to false, true to false)
    private val CHALLENGE = ByteArray(16).also { java.security.SecureRandom().nextBytes(it) }
    private val CHALLENGE_ALT = ByteArray(16).also { java.security.SecureRandom().nextBytes(it) }
    private val CHALLENGE_NAT = ByteArray(92).also { java.security.SecureRandom().nextBytes(it) }
    val CHALLENGE_HEX = hex(CHALLENGE)
    val CHALLENGE_ALT_HEX = hex(CHALLENGE_ALT)
    val CHALLENGE_NAT_HEX = hex(CHALLENGE_NAT)

    data class Record(
        val securityLevel: String,
        val keymasterSecurityLevel: String,
        val attestationVersion: Int,
        val keymasterVersion: Int,
        val challengeHex: String?,
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
        val signerSerial: String?,
        val signerSubject: String?,
        val signerSubjectSerialNo: String?,
        val signerSpkiHex: String?,
        val leafKeyAlg: String?,
        val leafSigAlg: String?,
        val provisioning: String?,
        val provisioningByStructure: String?,
        val rootName: String?,
        val provisioningInfo: String?,
        val provVbmetaDigest: String? = null,
        val provFingerprint: String? = null,
        val provBootVerdict: String? = null,
        val provManufacturer: String? = null,
        val attestationAppId: String?,
        val moduleHashHex: String? = null,
        val chainLen: Int = 0,
        val rotLevel: String? = null,
        val originLevel: String? = null,
        val purposeLevel: String? = null,
    )

    val record: Record? by lazy { runCatching { build(ALIAS, PRIMARY) }.getOrNull() }
    val error: String? by lazy { runCatching { build(ALIAS, PRIMARY); null }.exceptionOrNull()?.toString() }
    val recordPlain: Record? by lazy { runCatching { build(ALIAS_PLAIN, listOf(false to false)) }.getOrNull() }
    val recordStrongBox: Record? by lazy { runCatching { build(ALIAS_SB, listOf(true to false)) }.getOrNull() }
    val recordAltChallenge: Record? by lazy { runCatching { build(ALIAS_ALT, listOf(false to false), CHALLENGE_ALT) }.getOrNull() }
    val recordRsa: Record? by lazy { runCatching { buildRsa(ALIAS_RSA, CHALLENGE) }.getOrNull() }
    val recordNative: Record? by lazy { runCatching { buildNative(CHALLENGE_NAT) }.getOrNull() }
    val errorNative: String? by lazy { runCatching { buildNative(CHALLENGE_NAT); null }.exceptionOrNull()?.toString() }

    internal fun parseLeafForTest(leaf: X509Certificate): Record =
        parse(leaf, arrayOf<java.security.cert.Certificate>(leaf))

    private fun buildNative(challenge: ByteArray): Record {
        val der = NativeBridge.attestNative(challenge)
            ?: throw IllegalStateException("native keystore2 lens unavailable")
        val certs = java.security.cert.CertificateFactory.getInstance("X.509")
            .generateCertificates(java.io.ByteArrayInputStream(der))
            .filterIsInstance<X509Certificate>()
            .takeIf { it.isNotEmpty() } ?: throw IllegalStateException("native chain empty")
        val chain = certs.toTypedArray<java.security.cert.Certificate>()
        return parse(chain[0] as X509Certificate, chain)
    }

    private fun build(alias: String, attempts: List<Pair<Boolean, Boolean>>, challenge: ByteArray = CHALLENGE): Record {
        var chain: Array<java.security.cert.Certificate>? = null
        for ((strongbox, deviceProps) in attempts) {
            chain = gen(strongbox, deviceProps, alias, challenge)
            if (chain != null) break
        }
        chain ?: throw IllegalStateException("attestation unavailable")
        try {
            return parse(chain[0] as X509Certificate, chain)
        } finally {
            deleteKey(alias)
        }
    }

    private fun gen(strongbox: Boolean, deviceProps: Boolean, alias: String, challenge: ByteArray = CHALLENGE): Array<java.security.cert.Certificate>? =
        runCatching {
            deleteKey(alias)
            val b = KeyGenParameterSpec.Builder(
                alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
            )
                .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setAttestationChallenge(challenge)
            if (strongbox) b.setIsStrongBoxBacked(true)
            if (deviceProps && Build.VERSION.SDK_INT >= 31) b.setDevicePropertiesAttestationIncluded(true)
            val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
            kpg.initialize(b.build())
            kpg.generateKeyPair()
            val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            ks.getCertificateChain(alias)?.takeIf { it.isNotEmpty() }
        }.getOrNull()

    private fun buildRsa(alias: String, challenge: ByteArray): Record {
        val chain = genRsa(alias, challenge) ?: throw IllegalStateException("rsa attestation unavailable")
        try {
            return parse(chain[0] as X509Certificate, chain)
        } finally {
            deleteKey(alias)
        }
    }

    private fun genRsa(alias: String, challenge: ByteArray): Array<java.security.cert.Certificate>? =
        runCatching {
            deleteKey(alias)
            val b = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                .setAttestationChallenge(challenge)
            val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
            kpg.initialize(b.build())
            kpg.generateKeyPair()
            val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            ks.getCertificateChain(alias)?.takeIf { it.isNotEmpty() }
        }.getOrNull()

    private const val PROV_OID = "1.3.6.1.4.1.11129.2.1.30"
    private val PROV_NAMES = mapOf(1L to "certs_issued", 3L to "manufacturer", 4L to "attested_entity", 6L to "lost_device")

    private const val EAT_OID = "1.3.6.1.4.1.11129.2.1.25"
    private const val EAT_NONCE = -75008L
    private const val EAT_SECURITY_LEVEL = -76002L
    private const val EAT_BOOT_STATE = -76003L
    private const val EAT_SUBMODS = -76000L
    private const val EAT_VB_KEY = -82001L
    private const val EAT_DEVICE_LOCKED = -82002L
    private const val EAT_VB_HASH = -82003L
    private const val EAT_ATT_VERSION = -82004L
    private const val EAT_KM_VERSION = -82005L
    private const val EAT_OFFICIAL_BUILD = -82006L
    private const val EAT_OS_VERSION = -80705L
    private const val EAT_OS_PATCH = -80706L
    private const val EAT_ATT_APP_ID = -80709L
    private const val EAT_ID_BRAND = -80710L
    private const val EAT_ID_DEVICE = -80711L
    private const val EAT_ID_PRODUCT = -80712L
    private const val EAT_ID_SERIAL = -80713L
    private const val EAT_ID_MANUFACTURER = -80716L
    private const val EAT_ID_MODEL = -80717L
    private const val EAT_VENDOR_PATCH = -80718L
    private const val EAT_BOOT_PATCH = -80719L
    private const val EAT_ORIGIN = -80702L
    private const val EAT_PURPOSE = -80001L

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

    private fun eatMap(leaf: X509Certificate): Map<*, *>? = runCatching {
        val ext = leaf.getExtensionValue(EAT_OID) ?: return null
        val cbor = (ASN1Primitive.fromByteArray(ext) as ASN1OctetString).octets
        Cbor(cbor).read() as? Map<*, *>
    }.getOrNull()

    private fun eatSecToKm(v: Int?): Int = when (v) { 1 -> 0; 3 -> 1; 4 -> 2; else -> -1 }

    private fun hexToBytes(h: String): ByteArray =
        ByteArray(h.length / 2) { ((h[it * 2].digitToInt(16) shl 4) or h[it * 2 + 1].digitToInt(16)).toByte() }

    private fun hexToStr(h: String?): String? =
        h?.let { runCatching { String(hexToBytes(it), Charsets.UTF_8) }.getOrNull() }

    private fun parse(leaf: X509Certificate, chain: Array<java.security.cert.Certificate>): Record {
        var attVersion = 0; var attSec = 0; var kmVersion = 0; var kmSec = 0
        var challengeHex: String? = null
        var osPatch: String? = null; var osVer: String? = null
        var vendorPatch: String? = null; var bootPatch: String? = null
        var brand: String? = null; var device: String? = null; var product: String? = null
        var manufacturer: String? = null; var model: String? = null; var serial: String? = null
        var attAppId: String? = null
        var moduleHash: String? = null
        var rotLevel: String? = null; var originLevel: String? = null; var purposeLevel: String? = null
        var vbState: String? = null; var locked: String? = null
        var vbKey: String? = null; var vbHash: String? = null

        val kd = kd(leaf)
        if (kd != null) {
            attVersion = intOf(kd.getObjectAt(0)); attSec = intOf(kd.getObjectAt(1))
            kmVersion = intOf(kd.getObjectAt(2)); kmSec = intOf(kd.getObjectAt(3))
            challengeHex = runCatching { hex((kd.getObjectAt(4) as ASN1OctetString).octets) }.getOrNull()
            var rot: ASN1Sequence? = null
            for (idx in intArrayOf(7, 6)) {
                val lvl = if (idx == 7) "hw" else "sw"
                val al = kd.getObjectAt(idx) as? ASN1Sequence ?: continue
                for (e in al) {
                    val t = e as? ASN1TaggedObject ?: continue
                    val base = t.baseObject.toASN1Primitive()
                    when (t.tagNo) {
                        1 -> if (purposeLevel == null) purposeLevel = lvl
                        702 -> if (originLevel == null) originLevel = lvl
                        724 -> if (moduleHash == null) moduleHash = runCatching { hex(ASN1OctetString.getInstance(base).octets) }.getOrNull()
                        704 -> { if (rot == null) rot = ASN1Sequence.getInstance(base); if (rotLevel == null) rotLevel = lvl }
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
            rot?.let { r ->
                runCatching { vbKey = hex((r.getObjectAt(0) as ASN1OctetString).octets) }
                locked = if ((r.getObjectAt(1) as ASN1Boolean).isTrue) "yes" else "no"
                vbState = bootState(intOf(r.getObjectAt(2)))
                if (r.size() >= 4) runCatching { vbHash = hex((r.getObjectAt(3) as ASN1OctetString).octets) }
            }
        } else {
            val eat = eatMap(leaf) ?: throw IllegalStateException("no attestation extension (.17/.25)")
            fun i(k: Long) = eat[k] as? Long
            fun bytesHex(k: Long) = eat[k] as? String
            attVersion = i(EAT_ATT_VERSION)?.toInt() ?: 0
            kmVersion = i(EAT_KM_VERSION)?.toInt() ?: 0
            kmSec = eatSecToKm(i(EAT_SECURITY_LEVEL)?.toInt()); attSec = kmSec
            challengeHex = bytesHex(EAT_NONCE)
            vbKey = bytesHex(EAT_VB_KEY); vbHash = bytesHex(EAT_VB_HASH)
            locked = (eat[EAT_DEVICE_LOCKED] as? Boolean)?.let { if (it) "yes" else "no" }
            val officialBuild = eat[EAT_OFFICIAL_BUILD] as? Boolean ?: false
            (eat[EAT_BOOT_STATE] as? List<*>)?.let { bs ->
                val verifiedOrSelf = bs.getOrNull(0) as? Boolean ?: false
                vbState = bootState(if (officialBuild && verifiedOrSelf) 0 else if (verifiedOrSelf) 1 else 2)
            }
            val submods = eat[EAT_SUBMODS] as? Map<*, *>
            val tee = submods?.get("tee") as? Map<*, *>
            val al = tee ?: submods?.get("software") as? Map<*, *>
            if (al != null) {
                rotLevel = if (tee != null) "hw" else "sw"
                osVer = (al[EAT_OS_VERSION] as? Long)?.toString()
                osPatch = (al[EAT_OS_PATCH] as? Long)?.toString()
                vendorPatch = (al[EAT_VENDOR_PATCH] as? Long)?.toString()
                bootPatch = (al[EAT_BOOT_PATCH] as? Long)?.toString()
                brand = hexToStr(al[EAT_ID_BRAND] as? String)
                device = hexToStr(al[EAT_ID_DEVICE] as? String)
                product = hexToStr(al[EAT_ID_PRODUCT] as? String)
                serial = hexToStr(al[EAT_ID_SERIAL] as? String)
                manufacturer = hexToStr(al[EAT_ID_MANUFACTURER] as? String)
                model = hexToStr(al[EAT_ID_MODEL] as? String)
                attAppId = (al[EAT_ATT_APP_ID] as? String)?.let { parseAppIdBytes(hexToBytes(it)) }
                if (al[EAT_ORIGIN] != null) originLevel = rotLevel
                if (al[EAT_PURPOSE] != null) purposeLevel = rotLevel
            }
        }

        var signerDays: Long? = null; var provisioning: String? = null
        var signerSerial: String? = null; var signerSubject: String? = null; var signerSpki: String? = null
        var signerSubjSn: String? = null
        if (chain.size >= 2) {
            val signer = chain[1] as X509Certificate
            signerDays = (signer.notAfter.time - signer.notBefore.time) / 86400000L
            provisioning = if (signerDays > 730) "batch_keybox" else "rkp"
            signerSerial = signer.serialNumber?.toString(16)
            signerSubject = rdn(signer.subjectX500Principal.getName(javax.security.auth.x500.X500Principal.RFC1779), "CN")
            signerSpki = signer.publicKey?.encoded?.let {
                hex(java.security.MessageDigest.getInstance("SHA-256").digest(it)).take(16)
            }
            signerSubjSn = runCatching {
                val x500 = org.bouncycastle.asn1.x500.X500Name.getInstance(signer.subjectX500Principal.encoded)
                x500.getRDNs(org.bouncycastle.asn1.x500.style.BCStyle.SERIALNUMBER).firstOrNull()
                    ?.first?.value?.let { org.bouncycastle.asn1.x500.style.IETFUtils.valueToString(it) }
            }.getOrNull()
        }
        val x509 = chain.map { it as X509Certificate }
        val provisioningByStructure = if (x509.size >= 2) {
            val n = x509[x509.size - 2].subjectX500Principal.getName(javax.security.auth.x500.X500Principal.RFC1779)
            if (rdn(n, "CN") == "Droid CA2" && rdn(n, "O") == "Google LLC") "rkp" else "batch_keybox"
        } else null
        val rootName = x509.lastOrNull()?.let {
            rdn(it.subjectX500Principal.getName(javax.security.auth.x500.X500Principal.RFC1779), "CN")
        }
        var provVbmeta: String? = null; var provFp: String? = null; var provBootVerdict: String? = null; var provManuf: String? = null
        val provisioningInfo = x509.asReversed().firstNotNullOfOrNull { c ->
            c.getExtensionValue(PROV_OID)?.let { raw ->
                runCatching {
                    val once = ASN1OctetString.getInstance(raw).octets
                    val cbor = runCatching { ASN1OctetString.getInstance(once).octets }.getOrDefault(once)
                    Cbor(cbor).read().let { v ->
                        if (v is Map<*, *>) {
                            fun top(k: Long) = v.entries.firstOrNull { (it.key as? Long) == k }?.value?.toString()
                            val nested = v.entries.firstOrNull { (it.key as? Long) == 5L }?.value as? Map<*, *>
                            fun sec(k: Long) = nested?.entries?.firstOrNull { (it.key as? Long) == k }?.value?.toString()
                            provManuf = top(3L); provVbmeta = sec(2L)?.lowercase(); provFp = sec(4L); provBootVerdict = sec(1L)
                            v.entries.joinToString(", ") { (k, x) -> "${PROV_NAMES[k] ?: k}=$x" }
                        } else v.toString()
                    }
                }.getOrElse { "unparsable: $it | raw[${raw.size}]=" + raw.take(28).joinToString("") { b -> "%02x".format(b) } }
            }
        }

        return Record(
            securityLevel = secLevel(attSec), keymasterSecurityLevel = secLevel(kmSec),
            attestationVersion = attVersion, keymasterVersion = kmVersion,
            challengeHex = challengeHex,
            verifiedBootState = vbState, deviceLocked = locked,
            verifiedBootKeyHex = vbKey, verifiedBootHashHex = vbHash,
            osVersion = osVer, osPatchLevel = osPatch, vendorPatchLevel = vendorPatch, bootPatchLevel = bootPatch,
            brand = brand, device = device, product = product, manufacturer = manufacturer, model = model,
            serial = serial, signerValidityDays = signerDays,
            signerSerial = signerSerial, signerSubject = signerSubject,
            signerSubjectSerialNo = signerSubjSn, signerSpkiHex = signerSpki,
            leafKeyAlg = leaf.publicKey?.algorithm, leafSigAlg = leaf.sigAlgName,
            provisioning = provisioning,
            provisioningByStructure = provisioningByStructure, rootName = rootName, provisioningInfo = provisioningInfo,
            provVbmetaDigest = provVbmeta, provFingerprint = provFp, provBootVerdict = provBootVerdict, provManufacturer = provManuf,
            attestationAppId = attAppId,
            moduleHashHex = moduleHash, chainLen = chain.size,
            rotLevel = rotLevel, originLevel = originLevel, purposeLevel = purposeLevel,
        )
    }

    private fun parseAppIdBytes(seqBytes: ByteArray): String? = runCatching {
        val seq = ASN1Primitive.fromByteArray(seqBytes) as ASN1Sequence
        val pkgs = (seq.getObjectAt(0) as ASN1Set).map {
            String(((it as ASN1Sequence).getObjectAt(0) as ASN1OctetString).octets)
        }
        val sigs = (seq.getObjectAt(1) as ASN1Set).map { hex((it as ASN1OctetString).octets) }
        "pkgs=${pkgs.joinToString("|")} sigs=${sigs.joinToString("|")}"
    }.getOrNull()

    private fun parseAppId(base: ASN1Primitive): String? = runCatching {
        parseAppIdBytes(ASN1OctetString.getInstance(base).octets)
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

    fun sbCapability(): String? {
        if (Build.VERSION.SDK_INT < 28) return null
        val probes = listOf<Pair<String, () -> Unit>>(
            "ec_p521" to { genSbEc("secp521r1") },
            "ec_p384" to { genSbEc("secp384r1") },
            "rsa4096" to { genSbRsa(4096) },
        )
        return probes.joinToString(",") { (name, fn) ->
            val r = try { fn(); "accepted" } catch (e: Throwable) { "rejected" }
            "$name=$r"
        }
    }

    private fun genSbEc(curve: String) {
        val alias = "vdinfos_cap_ec"
        deleteKey(alias)
        try {
            val b = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setAlgorithmParameterSpec(ECGenParameterSpec(curve))
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            if (Build.VERSION.SDK_INT >= 28) b.setIsStrongBoxBacked(true)
            val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
            kpg.initialize(b.build())
            kpg.generateKeyPair()
        } finally { deleteKey(alias) }
    }

    private fun genSbRsa(bits: Int) {
        val alias = "vdinfos_cap_rsa"
        deleteKey(alias)
        try {
            val b = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                .setKeySize(bits)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
            if (Build.VERSION.SDK_INT >= 28) b.setIsStrongBoxBacked(true)
            val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore")
            kpg.initialize(b.build())
            kpg.generateKeyPair()
        } finally { deleteKey(alias) }
    }

    fun sbVsTeeLatency(): String? {
        if (Build.VERSION.SDK_INT < 28) return null
        val sb = timeGen(true) ?: return null
        val tee = timeGen(false) ?: return null
        val ratio = if (tee > 0) "%.1f".format(sb.toDouble() / tee) else "-"
        return "strongbox=${sb}ms tee=${tee}ms ratio=$ratio"
    }

    private fun timeGen(strongbox: Boolean): Long? = runCatching {
        val alias = "vdinfos_lat"
        deleteKey(alias)
        val t0 = System.nanoTime()
        val b = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
            .setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
            .setDigests(KeyProperties.DIGEST_SHA256)
        if (strongbox && Build.VERSION.SDK_INT >= 28) b.setIsStrongBoxBacked(true)
        val kpg = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_EC, "AndroidKeyStore")
        kpg.initialize(b.build())
        kpg.generateKeyPair()
        val ms = (System.nanoTime() - t0) / 1_000_000
        deleteKey(alias)
        ms
    }.getOrNull()

    private fun deleteKey(alias: String = ALIAS) = runCatching {
        val ks = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        if (ks.containsAlias(alias)) ks.deleteEntry(alias)
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
