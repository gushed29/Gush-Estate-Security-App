package com.gush.security.estate.access.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PassType {
    GUEST,
    DELIVERY,
    CONTRACTOR,
    DOMESTIC_STAFF,
    EMERGENCY
}

enum class PassStatus {
    SCHEDULED,
    ACTIVE_INSIDE,
    COMPLETED_EXIT,
    EXPIRED,
    REVOKED,
    DENIED
}

@Entity(tableName = "visitor_passes")
data class VisitorPassEntity(
    @PrimaryKey
    val id: String, // UUID
    val pinCode: String, // 6-digit high entropy PIN
    val qrToken: String, // Opaque security token
    val visitorName: String,
    val phone: String,
    val hostResidentName: String,
    val propertyUnit: String, // e.g. Villa 14B, Royal Crest
    val visitorType: String = PassType.GUEST.name,
    val visitPurpose: String,
    val expectedOccupants: Int = 1,
    val actualOccupants: Int = 1,
    val vehiclePlate: String = "",
    val vehicleMakeModel: String = "",
    val vehicleColor: String = "",
    val driverName: String = "",
    val allowedGate: String = "All Gates",
    val validFromEpoch: Long,
    val validUntilEpoch: Long,
    val status: String = PassStatus.SCHEDULED.name,
    val entryTimeEpoch: Long? = null,
    val exitTimeEpoch: Long? = null,
    val isRevoked: Boolean = false,
    val revokedReason: String? = null,
    val specialInstructions: String = "",
    val emergencyOverride: Boolean = false,
    val guardNotes: String = "",
    val entrySignature: String? = null,
    val exitSignature: String? = null,
    val createdTimestamp: Long = System.currentTimeMillis()
)
