package com.gush.security.estate.access.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gate_events")
data class GateEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val passId: String,
    val visitorName: String,
    val hostResident: String,
    val gateName: String,
    val guardName: String,
    val eventType: String, // CHECK_IN, CHECK_OUT, ACCESS_DENIED, EMERGENCY_OVERRIDE, REVOCATION
    val timestamp: Long = System.currentTimeMillis(),
    val vehiclePlate: String = "",
    val occupantCount: Int = 1,
    val decisionNote: String = "",
    val isDiscrepancy: Boolean = false
)
