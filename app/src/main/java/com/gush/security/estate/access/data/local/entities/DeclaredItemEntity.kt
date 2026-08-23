package com.gush.security.estate.access.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "declared_items")
data class DeclaredItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val passId: String,
    val itemName: String,
    val category: String, // Electronics, Tools, Bags, Furniture, Documents, Equipment
    val serialNumber: String = "",
    val quantity: Int = 1,
    val photoEvidenceTag: String = "IMG_VERIFIED",
    val exitInspectionStatus: String = "MATCHED", // MATCHED, ADDED, REMOVED, MISSING, UNDECLARED
    val residentApprovedExit: Boolean = true,
    val createdTimestamp: Long = System.currentTimeMillis()
)
