package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class FamilyRelationship {
    WIFE,
    HUSBAND,
    SON,
    DAUGHTER,
    PARENT,
    DOMESTIC_STAFF,
    DRIVER,
    RELATIVE
}

@Entity(tableName = "family_members")
data class FamilyMemberEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val residentId: String,
    val residentName: String,
    val unitNumber: String,
    val fullName: String,
    val relationship: String,
    val phone: String,
    val email: String = "",
    val accessLevel: String = "FULL_ACCESS", // FULL_ACCESS, GATE_ONLY, VEHICLE_ONLY
    val pinCode: String,
    val qrToken: String,
    val vehiclePlate: String = "",
    val isActive: Boolean = true,
    val createdTimestamp: Long = System.currentTimeMillis()
)
