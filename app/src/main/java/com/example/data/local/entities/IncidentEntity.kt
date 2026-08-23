package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val incidentCode: String, // INC-2026-081
    val title: String,
    val category: String, // Suspicious Visitor, Vehicle Mismatch, Unauthorized Item, Fake Credential, Attempted Forced Entry
    val severity: String, // LOW, MEDIUM, HIGH, CRITICAL
    val guardName: String,
    val gateName: String,
    val visitorName: String = "",
    val vehiclePlate: String = "",
    val description: String,
    val status: String = "OPEN", // OPEN, INVESTIGATING, RESOLVED
    val timestamp: Long = System.currentTimeMillis(),
    val resolutionNotes: String = ""
)
