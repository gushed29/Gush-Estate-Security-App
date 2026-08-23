package com.gush.security.estate.access.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "resident_complaints")
data class ResidentComplaintEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val ticketCode: String,
    val residentId: String,
    val residentName: String,
    val unitNumber: String,
    val phone: String = "",
    val title: String,
    val category: String, // SECURITY, NOISE, FACILITY, SANITATION, PARKING, OTHER
    val severity: String = "MEDIUM", // LOW, MEDIUM, HIGH, EMERGENCY
    val description: String,
    val imageAttachmentUrl: String = "",
    val status: String = "OPEN", // OPEN, INVESTIGATING, IN_PROGRESS, RESOLVED
    val createdTimestamp: Long = System.currentTimeMillis(),
    val resolvedTimestamp: Long? = null,
    val adminResponse: String = "",
    val assignedOfficer: String = ""
)
