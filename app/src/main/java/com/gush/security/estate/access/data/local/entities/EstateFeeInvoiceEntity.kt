package com.gush.security.estate.access.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class FeeStatus {
    PENDING,
    PAID,
    OVERDUE
}

@Entity(tableName = "estate_fee_invoices")
data class EstateFeeInvoiceEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val residentId: String,
    val residentName: String,
    val unitNumber: String,
    val feeTitle: String,
    val category: String, // SERVICE_CHARGE, SECURITY_LEVY, WASTE_MANAGEMENT, INFRASTRUCTURE, POWER
    val amount: Double,
    val period: String, // e.g. "Q3 2026", "August 2026", "Annual 2026"
    val dueDateEpoch: Long,
    val paidDateEpoch: Long? = null,
    val status: String = FeeStatus.PENDING.name,
    val paymentReference: String = "",
    val paymentMethod: String = "", // CARD, BANK_TRANSFER, USSD, WALLET
    val receiptNumber: String = "",
    val createdTimestamp: Long = System.currentTimeMillis()
)
