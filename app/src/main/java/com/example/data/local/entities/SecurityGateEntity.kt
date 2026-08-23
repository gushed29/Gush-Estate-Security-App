package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "security_gates")
data class SecurityGateEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val gateName: String,
    val gateCode: String,
    val estateName: String = "Pinnock Beach Estate",
    val location: String,
    val operatingHours: String = "24 Hours / 7 Days",
    val status: String = "OPERATIONAL", // OPERATIONAL, RESTRICTED, LOCKED_DOWN
    val assignedGuardStaff: String = "Duty Officers",
    val isPrimaryGate: Boolean = false,
    val createdEpoch: Long = System.currentTimeMillis()
)
