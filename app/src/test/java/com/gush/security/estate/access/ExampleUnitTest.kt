package com.gush.security.estate.access

import com.gush.security.estate.access.data.local.entities.PassStatus
import com.gush.security.estate.access.data.local.entities.PassType
import com.gush.security.estate.access.security.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GushSecurityUnitTest {

    @Test
    fun testSecurePinGeneration_FormatAndLength() {
        val pin = SecurityUtils.generateSecurePin()
        assertNotNull(pin)
        assertEquals(6, pin.length)
        assertTrue(pin.all { it.isDigit() })
    }

    @Test
    fun testQrTokenGeneration_PrefixAndFormat() {
        val token = SecurityUtils.generateQrToken("GSH")
        assertNotNull(token)
        assertTrue(token.startsWith("GSH-"))
        assertEquals(4, token.split("-").size) // GSH-XXXX-XXXX-XXXX
    }

    @Test
    fun testSha256EventHashChaining_DeterminismAndTamperEvident() {
        val initialGenesisHash = "GENESIS_BLOCK_0000000000000000"
        val canonicalEvent1 = "EVENT_PASS_CREATED|PASS-101|HOST_JANE|2026-08-23"
        val hash1 = SecurityUtils.calculateEventHash(initialGenesisHash, canonicalEvent1)

        val canonicalEvent2 = "EVENT_ENTRY_ALLOWED|PASS-101|GUARD_MARK|GATE_1"
        val hash2 = SecurityUtils.calculateEventHash(hash1, canonicalEvent2)

        assertNotNull(hash1)
        assertNotNull(hash2)
        assertEquals(64, hash1.length) // 256 bits in hex
        assertEquals(64, hash2.length)
        assertNotEquals(hash1, hash2)

        // Verify tampering is immediately detected
        val tamperedEvent1 = "EVENT_PASS_CREATED|PASS-101|HOST_HACKER|2026-08-23"
        val tamperedHash1 = SecurityUtils.calculateEventHash(initialGenesisHash, tamperedEvent1)
        assertNotEquals(hash1, tamperedHash1)
    }

    @Test
    fun testExpirationCheck_ValidAndExpiredTimestamps() {
        val futureTime = System.currentTimeMillis() + 3600_000L // 1 hr in future
        val pastTime = System.currentTimeMillis() - 3600_000L   // 1 hr in past

        assertFalse(SecurityUtils.isExpired(futureTime))
        assertTrue(SecurityUtils.isExpired(pastTime))
    }

    @Test
    fun testPassTypeAndStatusEnums() {
        assertEquals("SCHEDULED", PassStatus.SCHEDULED.name)
        assertEquals("ACTIVE_INSIDE", PassStatus.ACTIVE_INSIDE.name)
        assertEquals("COMPLETED_EXIT", PassStatus.COMPLETED_EXIT.name)
        assertEquals("EXPIRED", PassStatus.EXPIRED.name)
        assertEquals("REVOKED", PassStatus.REVOKED.name)
        assertEquals("DENIED", PassStatus.DENIED.name)

        assertEquals("GUEST", PassType.GUEST.name)
        assertEquals("DELIVERY", PassType.DELIVERY.name)
        assertEquals("CONTRACTOR", PassType.CONTRACTOR.name)
        assertEquals("DOMESTIC_STAFF", PassType.DOMESTIC_STAFF.name)
        assertEquals("EMERGENCY", PassType.EMERGENCY.name)
    }
}

