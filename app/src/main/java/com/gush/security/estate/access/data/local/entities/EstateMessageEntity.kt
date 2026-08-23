package com.gush.security.estate.access.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

enum class MessageChannelType {
    DIRECT_GATE,
    DIRECT_ADMIN,
    COMMUNITY_GROUP,
    SECURITY_WATCH
}

enum class MessageAttachmentType {
    NONE,
    IMAGE,
    VIDEO,
    FILE,
    AUDIO
}

@Entity(tableName = "estate_messages")
data class EstateMessageEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val channelType: String, // DIRECT_GATE, DIRECT_ADMIN, COMMUNITY_GROUP, SECURITY_WATCH
    val conversationId: String, // e.g. "gate_res-001" or "admin_res-001" or "COMMUNITY_FORUM"
    val senderId: String,
    val senderName: String,
    val senderRole: String, // RESIDENT, GUARD, ADMIN
    val senderUnit: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val content: String,
    val attachmentType: String = MessageAttachmentType.NONE.name,
    val attachmentUrl: String = "",
    val attachmentName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
