package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "guard_accounts")
data class GuardAccountEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val badgeId: String,
    val assignedGate: String = "Gate 1 - Pinnock Beach Estate Main Gate",
    val shift: String = "Day Shift (06:00 - 18:00)", // Day Shift, Night Shift
    val phone: String = "",
    val passcode: String = "0000",
    val status: String = "ON_DUTY", // ON_DUTY, OFF_DUTY
    val supervisorName: String = "Chief Security Officer",
    val createdEpoch: Long = System.currentTimeMillis()
)
