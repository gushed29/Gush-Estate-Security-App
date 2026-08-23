package com.gush.security.estate.access.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gush.security.estate.access.data.local.entities.EstateBroadcastEntity
import com.gush.security.estate.access.security.SecurityUtils
import com.gush.security.estate.access.ui.theme.FrostedGlassBorder
import com.gush.security.estate.access.ui.theme.FrostedGlassSurface
import com.gush.security.estate.access.ui.theme.GushedCobalt
import com.gush.security.estate.access.ui.theme.GushedCrimsonDenied
import com.gush.security.estate.access.ui.theme.GushedEmeraldApproved
import com.gush.security.estate.access.ui.theme.GushedTextPrimary
import com.gush.security.estate.access.ui.theme.GushedTextSecondary

@Composable
fun EstateBulletinBoardScreen(
    isAdmin: Boolean,
    broadcasts: List<EstateBroadcastEntity>,
    onPublishBroadcast: (title: String, category: String, priority: String, content: String, targetAudience: String, isPinned: Boolean) -> Unit,
    onAcknowledge: (String) -> Unit
) {
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var showCreateBroadcastDialog by remember { mutableStateOf(false) }

    val filteredBroadcasts = broadcasts.filter {
        when (selectedCategoryFilter) {
            "ALL" -> true
            "SECURITY_ALERT" -> it.category == "SECURITY_ALERT"
            "MAINTENANCE" -> it.category == "MAINTENANCE"
            "NOTICE" -> it.category == "NOTICE"
            else -> true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf(
                    "ALL" to "All Notifications",
                    "SECURITY_ALERT" to "🚨 Security Advisories",
                    "MAINTENANCE" to "🛠️ Facility Maintenance",
                    "NOTICE" to "📢 Community Notices"
                ).forEach { (key, label) ->
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == key,
                            onClick = { selectedCategoryFilter = key },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GushedCobalt,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredBroadcasts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = GushedCobalt.copy(alpha = 0.4f),
                            modifier = Modifier.size(52.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active broadcasts in this category",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GushedTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredBroadcasts) { broadcast ->
                        BroadcastCardItem(
                            broadcast = broadcast,
                            onAcknowledge = { onAcknowledge(broadcast.id) }
                        )
                    }
                }
            }
        }

        // FAB to create broadcast for Admin
        if (isAdmin) {
            FloatingActionButton(
                onClick = { showCreateBroadcastDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = GushedCobalt,
                contentColor = Color.White
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = "New Broadcast")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Broadcast Alert", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showCreateBroadcastDialog) {
        CreateBroadcastDialog(
            onDismiss = { showCreateBroadcastDialog = false },
            onPublish = { title, category, priority, content, audience, pinned ->
                onPublishBroadcast(title, category, priority, content, audience, pinned)
                showCreateBroadcastDialog = false
            }
        )
    }
}

@Composable
fun BroadcastCardItem(
    broadcast: EstateBroadcastEntity,
    onAcknowledge: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val priorityColor = when (broadcast.priority) {
        "URGENT" -> GushedCrimsonDenied
        "HIGH" -> Color(0xFFEA580C)
        else -> GushedCobalt
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.2.dp, if (broadcast.isPinned) Color(0xFFF59E0B) else FrostedGlassBorder, RoundedCornerShape(18.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (broadcast.isPinned) 6.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = priorityColor.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = when (broadcast.category) {
                                    "SECURITY_ALERT" -> Icons.Default.Security
                                    "MAINTENANCE" -> Icons.Default.Construction
                                    else -> Icons.Default.Campaign
                                },
                                contentDescription = null,
                                tint = priorityColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (broadcast.isPinned) {
                                Icon(Icons.Default.PushPin, contentDescription = "Pinned", tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = broadcast.category.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = priorityColor
                            )
                        }
                        Text(
                            text = SecurityUtils.formatTimestamp(broadcast.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = priorityColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = broadcast.priority,
                        color = priorityColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = broadcast.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GushedTextPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = broadcast.content,
                style = MaterialTheme.typography.bodyMedium,
                color = GushedTextSecondary,
                maxLines = if (expanded) Int.MAX_VALUE else 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Dispatched by: ${broadcast.authorName}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )

                if (!broadcast.isAcknowledged) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GushedEmeraldApproved.copy(alpha = 0.12f),
                        modifier = Modifier.clickable { onAcknowledge() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GushedEmeraldApproved, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Mark Read", color = GushedEmeraldApproved, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateBroadcastDialog(
    onDismiss: () -> Unit,
    onPublish: (title: String, category: String, priority: String, content: String, audience: String, pinned: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("SECURITY_ALERT") }
    var priority by remember { mutableStateOf("HIGH") }
    var content by remember { mutableStateOf("") }
    var audience by remember { mutableStateOf("All Registered Residents") }
    var isPinned by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.5.dp, FrostedGlassBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = GushedCobalt)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Publish Estate Broadcast", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Broadcast Title") },
                        placeholder = { Text("e.g. Mandatory Perimeter Night Patrol Review") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("SECURITY_ALERT", "MAINTENANCE", "NOTICE").forEach { cat ->
                            FilterChip(
                                selected = category == cat,
                                onClick = { category = cat },
                                label = { Text(cat.replace("_", " "), fontSize = 10.sp) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Full Bulletin Text / Security Instructions") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && content.isNotBlank()) {
                                onPublish(title.trim(), category, priority, content.trim(), audience, isPinned)
                            }
                        },
                        enabled = title.isNotBlank() && content.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Broadcast to All Residents", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
