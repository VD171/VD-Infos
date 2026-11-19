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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate

class AttestGoldenTest {

    private fun load(res: String): X509Certificate {
        val stream = javaClass.getResourceAsStream(res)
        assertNotNull("fixture ausente: $res", stream)
        return stream!!.use {
            CertificateFactory.getInstance("X.509").generateCertificate(it) as X509Certificate
        }
    }

    @Test
    fun ecTeeLeafMatchesGolden() {
        val r = Attestation.parseLeafForTest(load("/attest_golden/ec_tee_leaf.pem"))
        assertEquals(3, r.attestationVersion)
        assertEquals("tee", r.securityLevel)
        assertEquals(4, r.keymasterVersion)
        assertEquals("unverified", r.verifiedBootState)
        assertEquals("no", r.deviceLocked)
        assertEquals("201907", r.osPatchLevel)
    }
}
