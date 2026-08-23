package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "estate_broadcasts")
data class EstateBroadcastEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val category: String, // SECURITY_ALERT, MAINTENANCE, NOTICE, EMERGENCY, DUES
    val priority: String = "NORMAL", // URGENT, HIGH, NORMAL
    val authorName: String = "Estate Security Directorate",
    val authorRole: String = "ESTATE_ADMIN",
    val content: String,
    val targetAudience: String = "All Residents",
    val attachmentUrl: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isPinned: Boolean = false,
    val isAcknowledged: Boolean = false
)
