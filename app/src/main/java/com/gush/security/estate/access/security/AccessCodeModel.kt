package com.gush.security.estate.access.security

import java.security.MessageDigest
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

data class EstateProfile(
    val estateId: String = "GST-EST-84K7P2",
    val name: String = "Pinnock Beach Estate",
    val address: String = "Pinnock Beach Road, Lekki Phase 1, Lagos, Nigeria",
    val country: String = "Nigeria",
    val state: String = "Lagos",
    val city: String = "Lekki",
    val tier: String = "ENTERPRISE",
    val subscriptionStatus: String = "ACTIVE",
    val gracePeriodDaysRemaining: Int = 0,
    val subscriptionExpiresEpochMs: Long = System.currentTimeMillis() + (365L * 24 * 3600 * 1000)
)

data class UserProfile(
    val userId: String,
    val fullName: String,
    val phone: String,
    val email: String,
    val unitOrGate: String,
    val role: String, // "ADMIN", "SECURITY", "RESIDENT"
    val avatarUrl: String = ""
)

data class DeviceProfile(
    val deviceId: String,
    val deviceModel: String,
    val registeredAt: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE" // "ACTIVE", "REVOKED", "PENDING"
)

data class EstateEnrollmentResponse(
    val authorized: Boolean,
    val role: String, // "ADMIN", "SECURITY", "RESIDENT"
    val estate: EstateProfile,
    val user: UserProfile,
    val device: DeviceProfile,
    val sessionToken: String,
    val permissions: List<String>,
    val signedCache: String,
    val cacheExpiresAt: Long,
    val message: String? = null
)

data class AccessInvitationCode(
    val code: String,
    val role: String, // "ADMIN", "SECURITY", "RESIDENT", "TENANT", "FAMILY"
    val targetName: String,
    val targetPhone: String,
    val unitOrGate: String,
    val category: String,
    val createdBy: String,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 3600 * 1000),
    val status: String = "ACTIVE", // "ACTIVE", "REVOKED", "USED"
    val usageCount: Int = 0,
    val maxUses: Int = 1
)

data class RegisteredDeviceItem(
    val deviceId: String,
    val deviceModel: String,
    val assignedTo: String,
    val role: String,
    val registeredAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE"
)

object GushAuthEngine {

    private const val HMAC_SECRET = "GUSH_ESTATE_SECURITY_AUTHORIZATION_SECRET_v2_2026"
    const val CACHE_VALIDITY_HOURS = 42L

    /**
     * Compute HMAC-SHA256 cryptographic signature for caching authorization payloads safely.
     */
    fun signPayload(payload: String): String {
        return try {
            val sha256Hmac = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(HMAC_SECRET.toByteArray(Charsets.UTF_8), "HmacSHA256")
            sha256Hmac.init(secretKey)
            val signedBytes = sha256Hmac.doFinal(payload.toByteArray(Charsets.UTF_8))
            signedBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest((payload + HMAC_SECRET).toByteArray(Charsets.UTF_8))
            digest.joinToString("") { "%02x".format(it) }
        }
    }

    /**
     * Verify if the cached signed authorization payload has been tampered with.
     */
    fun verifySignature(payload: String, signature: String): Boolean {
        val expected = signPayload(payload)
        return expected.equals(signature, ignoreCase = true)
    }

    /**
     * Generate standard Join Codes with role prefixes for estate provisioning.
     */
    fun generateJoinCode(role: String): String {
        val prefix = when (role.uppercase()) {
            "ADMIN", "ESTATE_ADMIN" -> "GST-ADM"
            "SECURITY", "GUARD" -> "GST-SEC"
            "RESIDENT" -> "GST-RES"
            "TENANT" -> "GST-TNT"
            "FAMILY" -> "GST-FAM"
            else -> "GST-ACC"
        }
        val randomPart = UUID.randomUUID().toString().replace("-", "").take(6).uppercase()
        return "$prefix-$randomPart"
    }

    /**
     * Generates a unique Device ID for hardware enrollment.
     */
    fun generateDeviceId(): String {
        val randomPart = UUID.randomUUID().toString().replace("-", "").take(6).uppercase()
        return "DEV-$randomPart"
    }
}
