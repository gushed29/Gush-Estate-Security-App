package com.gush.security.estate.access.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gush.security.estate.access.data.local.entities.EstateMessageEntity
import com.gush.security.estate.access.data.local.entities.MessageAttachmentType
import com.gush.security.estate.access.data.local.entities.MessageChannelType
import com.gush.security.estate.access.security.SecurityUtils
import com.gush.security.estate.access.ui.theme.FrostedGlassBorder
import com.gush.security.estate.access.ui.theme.FrostedGlassSurface
import com.gush.security.estate.access.ui.theme.GushedCobalt
import com.gush.security.estate.access.ui.theme.GushedEmeraldApproved
import com.gush.security.estate.access.ui.theme.GushedTextPrimary
import com.gush.security.estate.access.ui.theme.GushedTextSecondary

@Composable
fun EstateMessagesScreen(
    currentUserId: String,
    currentUserName: String,
    currentUserRole: String,
    messages: List<EstateMessageEntity>,
    onSendMessage: (channelType: MessageChannelType, conversationId: String, text: String, attachmentType: MessageAttachmentType, url: String, filename: String) -> Unit,
    onStartCall: (receiverName: String, receiverRole: String, receiverUnit: String, isVideo: Boolean) -> Unit
) {
    var selectedChannel by remember { mutableStateOf(MessageChannelType.DIRECT_GATE) }
    var inputText by remember { mutableStateOf("") }
    var showAttachmentPicker by remember { mutableStateOf(false) }

    val filteredMessages = messages.filter {
        when (selectedChannel) {
            MessageChannelType.DIRECT_GATE -> it.channelType == MessageChannelType.DIRECT_GATE.name
            MessageChannelType.DIRECT_ADMIN -> it.channelType == MessageChannelType.DIRECT_ADMIN.name
            MessageChannelType.COMMUNITY_GROUP -> it.channelType == MessageChannelType.COMMUNITY_GROUP.name
            MessageChannelType.SECURITY_WATCH -> it.channelType == MessageChannelType.SECURITY_WATCH.name
        }
    }

    val listState = rememberLazyListState()
    LaunchedEffect(filteredMessages.size) {
        if (filteredMessages.isNotEmpty()) {
            listState.animateScrollToItem(filteredMessages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Channel Selector Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, FrostedGlassBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedChannel == MessageChannelType.DIRECT_GATE,
                            onClick = { selectedChannel = MessageChannelType.DIRECT_GATE },
                            label = { Text("🛡️ Gatehouse Direct", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GushedCobalt,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedChannel == MessageChannelType.DIRECT_ADMIN,
                            onClick = { selectedChannel = MessageChannelType.DIRECT_ADMIN },
                            label = { Text("🏛️ Estate Helpdesk", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GushedCobalt,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedChannel == MessageChannelType.COMMUNITY_GROUP,
                            onClick = { selectedChannel = MessageChannelType.COMMUNITY_GROUP },
                            label = { Text("👥 Residents Forum", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GushedCobalt,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    item {
                        FilterChip(
                            selected = selectedChannel == MessageChannelType.SECURITY_WATCH,
                            onClick = { selectedChannel = MessageChannelType.SECURITY_WATCH },
                            label = { Text("🚨 Security Watch", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GushedCobalt,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Call Action Bar for direct channels
                if (selectedChannel == MessageChannelType.DIRECT_GATE || selectedChannel == MessageChannelType.DIRECT_ADMIN) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedChannel == MessageChannelType.DIRECT_GATE) "Direct Guard Intercom Channel" else "Estate Operations Helpdesk",
                            style = MaterialTheme.typography.labelSmall,
                            color = GushedTextSecondary
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GushedEmeraldApproved.copy(alpha = 0.12f),
                                modifier = Modifier.clickable {
                                    onStartCall(
                                        if (selectedChannel == MessageChannelType.DIRECT_GATE) "Gatehouse Duty Officer" else "Estate Admin Directorate",
                                        if (selectedChannel == MessageChannelType.DIRECT_GATE) "GUARD" else "ADMIN",
                                        if (selectedChannel == MessageChannelType.DIRECT_GATE) "Gate 1 Post" else "Command Centre",
                                        false
                                    )
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Call, contentDescription = null, tint = GushedEmeraldApproved, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Audio Call", color = GushedEmeraldApproved, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GushedCobalt.copy(alpha = 0.12f),
                                modifier = Modifier.clickable {
                                    onStartCall(
                                        if (selectedChannel == MessageChannelType.DIRECT_GATE) "Gatehouse Duty Officer" else "Estate Admin Directorate",
                                        if (selectedChannel == MessageChannelType.DIRECT_GATE) "GUARD" else "ADMIN",
                                        if (selectedChannel == MessageChannelType.DIRECT_GATE) "Gate 1 Post" else "Command Centre",
                                        true
                                    )
                                }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Videocam, contentDescription = null, tint = GushedCobalt, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Video Intercom", color = GushedCobalt, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Messages Stream
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            if (filteredMessages.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Forum,
                            contentDescription = null,
                            tint = GushedCobalt.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No messages yet in this channel", color = GushedTextSecondary, style = MaterialTheme.typography.bodyMedium)
                        Text("Send a text or attach image/video to start conversation", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredMessages) { msg ->
                        val isMe = msg.senderName == currentUserName || msg.senderRole == currentUserRole

                        MessageBubbleItem(message = msg, isMe = isMe)
                    }
                }
            }
        }

        // Quick Responses for security speed
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val quickNotes = listOf(
                "Visitor at the gate?",
                "Please grant clearance",
                "Delivering express package",
                "I am expecting this guest",
                "Security team acknowledged"
            )
            items(quickNotes) { note ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF1F5F9),
                    modifier = Modifier.clickable {
                        onSendMessage(
                            selectedChannel,
                            "conv_${selectedChannel.name}",
                            note,
                            MessageAttachmentType.NONE,
                            "",
                            ""
                        )
                    }
                ) {
                    Text(
                        text = note,
                        color = GushedTextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Attachment Sheet Picker if active
        if (showAttachmentPicker) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, FrostedGlassBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Image photo
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onSendMessage(
                                selectedChannel,
                                "conv_${selectedChannel.name}",
                                "📸 Photo evidence attached: Gate inspection & visitor ID",
                                MessageAttachmentType.IMAGE,
                                "https://images.unsplash.com/photo-1557804506-669a67965ba0?w=600",
                                "visitor_id_card.jpg"
                            )
                            showAttachmentPicker = false
                        }
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFDBEAFE), modifier = Modifier.size(44.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = GushedCobalt)
                            }
                        }
                        Text("Photo / ID", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                    }

                    // Video clip
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onSendMessage(
                                selectedChannel,
                                "conv_${selectedChannel.name}",
                                "🎥 Video recording: Gate transit & security inspection",
                                MessageAttachmentType.VIDEO,
                                "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                                "security_gate_clip.mp4"
                            )
                            showAttachmentPicker = false
                        }
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFFEE2E2), modifier = Modifier.size(44.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color(0xFFDC2626))
                            }
                        }
                        Text("Video", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                    }

                    // Document PDF
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onSendMessage(
                                selectedChannel,
                                "conv_${selectedChannel.name}",
                                "📄 Waybill & Material Entry Clearance Pass",
                                MessageAttachmentType.FILE,
                                "estate_clearance_manifest.pdf",
                                "manifest_slip.pdf"
                            )
                            showAttachmentPicker = false
                        }
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFFEF3C7), modifier = Modifier.size(44.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Description, contentDescription = null, tint = Color(0xFFD97706))
                            }
                        }
                        Text("Document", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                    }

                    // Audio Note
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            onSendMessage(
                                selectedChannel,
                                "conv_${selectedChannel.name}",
                                "🎙️ Audio Intercom Dispatch (0:18)",
                                MessageAttachmentType.AUDIO,
                                "audio_note_01.aac",
                                "voice_dispatch.aac"
                            )
                            showAttachmentPicker = false
                        }
                    ) {
                        Surface(shape = CircleShape, color = Color(0xFFD1FAE5), modifier = Modifier.size(44.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Mic, contentDescription = null, tint = GushedEmeraldApproved)
                            }
                        }
                        Text("Audio Note", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                    }
                }
            }
        }

        // Bottom Input Row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(20.dp))
                .border(1.2.dp, FrostedGlassBorder, RoundedCornerShape(20.dp)),
            colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showAttachmentPicker = !showAttachmentPicker }) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach file",
                        tint = if (showAttachmentPicker) GushedCobalt else GushedTextSecondary
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Write message or security note...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            onSendMessage(
                                selectedChannel,
                                "conv_${selectedChannel.name}",
                                inputText.trim(),
                                MessageAttachmentType.NONE,
                                "",
                                ""
                            )
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) GushedCobalt else Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubbleItem(message: EstateMessageEntity, isMe: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 4.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = if (isMe) GushedCobalt else Color(0xFFF1F5F9)
            ),
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Sender tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isMe) "You" else message.senderName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isMe) Color(0xFFC7D2FE) else GushedTextSecondary
                    )
                    Text(
                        text = SecurityUtils.formatTimestamp(message.timestamp).takeLast(8),
                        fontSize = 10.sp,
                        color = if (isMe) Color(0x99FFFFFF) else Color(0xFF94A3B8)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Text Content
                Text(
                    text = message.content,
                    fontSize = 13.sp,
                    color = if (isMe) Color.White else GushedTextPrimary
                )

                // Attachment Preview
                if (message.attachmentType != MessageAttachmentType.NONE.name && message.attachmentType.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isMe) Color(0x33FFFFFF) else Color(0xFFE2E8F0),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (message.attachmentType) {
                                    MessageAttachmentType.IMAGE.name -> Icons.Default.Image
                                    MessageAttachmentType.VIDEO.name -> Icons.Default.PlayArrow
                                    MessageAttachmentType.AUDIO.name -> Icons.Default.Audiotrack
                                    else -> Icons.Default.Description
                                },
                                contentDescription = null,
                                tint = if (isMe) Color.White else GushedCobalt,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = message.attachmentName.ifEmpty { "attachment_${message.attachmentType.lowercase()}" },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isMe) Color.White else GushedTextPrimary
                                )
                                Text(
                                    text = "Tap to view • ${message.attachmentType}",
                                    fontSize = 9.sp,
                                    color = if (isMe) Color(0xCCFFFFFF) else GushedTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
