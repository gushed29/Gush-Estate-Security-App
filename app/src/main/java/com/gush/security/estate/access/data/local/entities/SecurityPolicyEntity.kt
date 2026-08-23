package com.gush.security.estate.access.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "security_policies")
data class SecurityPolicyEntity(
    @PrimaryKey
    val policyKey: String,
    val name: String,
    val description: String,
    val isEnabled: Boolean = true,
    val category: String = "GATE_CONTROL" // GATE_CONTROL, EVIDENCE, CONTRACTOR, PROPERTY_PROTECTION
)
