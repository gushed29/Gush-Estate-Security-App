package com.example.security

import java.security.MessageDigest
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object SecurityUtils {
    private val secureRandom = SecureRandom()
    private val tokenChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // Exclude confusing chars 0/O, 1/I

    /**
     * Generates a cryptographically secure 6-digit OTP/PIN code.
     */
    fun generateSecurePin(): String {
        val number = 100000 + secureRandom.nextInt(900000)
        return number.toString()
    }

    /**
     * Generates an opaque random token for QR code passes.
     */
    fun generateQrToken(prefix: String = "GSH"): String {
        val sb = StringBuilder(prefix)
        sb.append("-")
        for (i in 0 until 4) {
            sb.append(tokenChars[secureRandom.nextInt(tokenChars.length)])
        }
        sb.append("-")
        for (i in 0 until 4) {
            sb.append(tokenChars[secureRandom.nextInt(tokenChars.length)])
        }
        sb.append("-")
        for (i in 0 until 4) {
            sb.append(tokenChars[secureRandom.nextInt(tokenChars.length)])
        }
        return sb.toString()
    }

    /**
     * Calculates SHA-256 hash for Tamper-Evident Audit Event Chaining.
     * event_hash = SHA256(previous_hash + canonical_event_data)
     */
    fun calculateEventHash(previousHash: String, canonicalData: String): String {
        val input = "$previousHash|$canonicalData"
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(input.toByteArray(Charsets.UTF_8))
        return digest.joinToString("") { "%02x".format(it) }
    }

    /**
     * Returns true if current trusted UTC time has exceeded the expiration timestamp.
     */
    fun isExpired(validUntilEpochMs: Long): Boolean {
        return System.currentTimeMillis() > validUntilEpochMs
    }

    /**
     * Formats timestamp into human-readable local time with UTC indicator.
     */
    fun formatTimestamp(epochMs: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(epochMs))
    }

    fun formatTimeOnly(epochMs: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(Date(epochMs))
    }

    fun formatDateOnly(epochMs: Long): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(Date(epochMs))
    }
}
