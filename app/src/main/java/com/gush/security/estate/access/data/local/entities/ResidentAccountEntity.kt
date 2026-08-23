package com.gush.security.estate.access.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "resident_accounts")
data class ResidentAccountEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val unitNumber: String,
    val estateName: String = "Pinnock Beach Estate",
    val primaryGate: String = "Gate 1 - Pinnock Beach Estate Main Gate",
    val phone: String,
    val email: String = "",
    val passcode: String = "1234",
    val status: String = "ACTIVE", // ACTIVE, SUSPENDED
    val registeredVehicles: String = "",
    val emergencyContact: String = "",
    val createdEpoch: Long = System.currentTimeMillis()
)
