package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val eventId: String, // UUID
    val timestamp: Long = System.currentTimeMillis(),
    val actor: String,
    val role: String, // SECURITY_GUARD, RESIDENT, ESTATE_ADMIN, SYSTEM
    val action: String, // PASS_CREATED, ACCESS_VERIFIED, ENTRY_APPROVED, ENTRY_DENIED, EXIT_RECORDED, PASS_REVOKED, ITEM_DISCREPANCY_FLAGGED, EMERGENCY_OVERRIDE
    val resource: String,
    val result: String, // SUCCESS, FAILURE, WARNING, OVERRIDE
    val previousHash: String,
    val currentHash: String, // SHA256(previousHash + canonical_data)
    val details: String
)
