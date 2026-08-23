package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BackHand
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.EstateMeetingEntity
import com.example.data.local.entities.MeetingContributionEntity
import com.example.data.local.entities.MeetingPollEntity
import com.example.security.SecurityUtils
import com.example.ui.theme.FrostedGlassBorder
import com.example.ui.theme.FrostedGlassSurface
import com.example.ui.theme.GushedCobalt
import com.example.ui.theme.GushedEmeraldApproved
import com.example.ui.theme.GushedTextPrimary
import com.example.ui.theme.GushedTextSecondary

@Composable
fun CommunityMeetingsScreen(
    isAdmin: Boolean,
    currentUserName: String,
    meetings: List<EstateMeetingEntity>,
    onScheduleMeeting: (title: String, desc: String, category: String, epoch: Long, duration: Int, agenda: String) -> Unit,
    onPostContribution: (meetingId: String, msg: String, isHandRaised: Boolean, voteChoice: String) -> Unit,
    onVotePoll: (poll: MeetingPollEntity, optionIndex: Int) -> Unit
) {
    var selectedMeeting by remember { mutableStateOf<EstateMeetingEntity?>(meetings.firstOrNull()) }
    var showScheduleDialog by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }
    var isHandRaised by remember { mutableStateOf(false) }

    // Keep active meeting in sync
    val activeMeeting = selectedMeeting ?: meetings.firstOrNull()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Meeting Switcher Carousel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ESTATE TOWN HALLS & AGMs",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = GushedTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (meetings.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    meetings.forEach { m ->
                        val isSelected = m.id == activeMeeting?.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedMeeting = m },
                            label = {
                                Text(
                                    text = if (m.status == "LIVE_NOW") "🔴 ${m.title}" else m.title,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GushedCobalt,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (activeMeeting == null) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No scheduled estate townhall meetings.", color = GushedTextSecondary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Live Stage Box / Stream Simulation
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.2.dp, FrostedGlassBorder, RoundedCornerShape(20.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (activeMeeting.status == "LIVE_NOW") Color(0xFFDC2626) else GushedCobalt
                                    ) {
                                        Text(
                                            text = if (activeMeeting.status == "LIVE_NOW") "LIVE BROADCAST" else activeMeeting.status,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.People, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${activeMeeting.participantCount} Residents in attendance", color = Color(0xFFCBD5E1), fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Main Video Stage
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(160.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(
                                            Brush.verticalGradient(
                                                listOf(Color(0xFF1E293B), Color(0xFF020617))
                                            )
                                        )
                                        .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Surface(
                                            shape = CircleShape,
                                            color = GushedCobalt,
                                            modifier = Modifier.size(52.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(Icons.Default.Videocam, contentDescription = null, tint = Color.White)
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text("Host Speaker: ${activeMeeting.hostName}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("Virtual Townhall Audio & Screen Stage", color = Color(0xFF94A3B8), fontSize = 11.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = activeMeeting.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Text(
                                    text = activeMeeting.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Live Interaction Bar (Raise Hand / Speak)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            isHandRaised = !isHandRaised
                                            onPostContribution(
                                                activeMeeting.id,
                                                if (isHandRaised) "✋ Raised hand to speak on current agenda item" else "Lowered hand",
                                                isHandRaised,
                                                ""
                                            )
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isHandRaised) Color(0xFFD97706) else Color(0x33FFFFFF)
                                        )
                                    ) {
                                        Icon(Icons.Default.BackHand, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isHandRaised) "Hand Raised" else "Raise Hand", fontSize = 12.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                    }

                    // Meeting Agenda Section
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, FrostedGlassBorder, RoundedCornerShape(16.dp)),
                            colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("MEETING AGENDA", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GushedTextSecondary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = activeMeeting.agendaItems.ifEmpty { "1. Security report\n2. Power & lighting infrastructure\n3. Resident Q&A" },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GushedTextPrimary
                                )
                            }
                        }
                    }

                    // Live Resident Contributions Header
                    item {
                        Text(
                            text = "LIVE CONTRIBUTIONS & COMMENTS",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextSecondary
                        )
                    }

                    // Sample Contributions Stream
                    items(
                        listOf(
                            Triple("Engr. Babatunde Lawal", "Villa 12", "I strongly support upgrading the solar perimeter lighting at Gate 2."),
                            Triple("Dr. Amina Bello", "Block 4A", "Can we also discuss the speed humps near the children play zone?"),
                            Triple("Estate CSO", "Security HQ", "Perimeter patrols have been doubled starting 10:00 PM every night.")
                        )
                    ) { (author, unit, comment) ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(author, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GushedCobalt)
                                    Text(unit, fontSize = 10.sp, color = GushedTextSecondary)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(comment, fontSize = 12.sp, color = GushedTextPrimary)
                            }
                        }
                    }
                }

                // Contribution input box
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = chatInput,
                        onValueChange = { chatInput = it },
                        placeholder = { Text("Contribute to live townhall discussion...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (chatInput.isNotBlank() && activeMeeting != null) {
                                onPostContribution(activeMeeting.id, chatInput.trim(), false, "")
                                chatInput = ""
                            }
                        }
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = GushedCobalt)
                    }
                }
            }
        }

        if (isAdmin) {
            FloatingActionButton(
                onClick = { showScheduleDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 60.dp, end = 20.dp),
                containerColor = GushedCobalt,
                contentColor = Color.White
            ) {
                Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Schedule Townhall", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showScheduleDialog) {
        ScheduleMeetingDialog(
            onDismiss = { showScheduleDialog = false },
            onSchedule = { title, desc, cat, dur, agenda ->
                onScheduleMeeting(title, desc, cat, System.currentTimeMillis() + 86400000L, dur, agenda)
                showScheduleDialog = false
            }
        )
    }
}

@Composable
fun ScheduleMeetingDialog(
    onDismiss: () -> Unit,
    onSchedule: (title: String, desc: String, category: String, duration: Int, agenda: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var agenda by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
            modifier = Modifier.border(1.5.dp, FrostedGlassBorder, RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Schedule Community Meeting", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Meeting Title") },
                    placeholder = { Text("e.g. Q4 Security & Infrastructure Assembly") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Meeting Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = agenda,
                    onValueChange = { agenda = it },
                    label = { Text("Agenda Items (One per line)") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            onSchedule(title.trim(), desc.trim(), "AGM", 60, agenda.trim())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                ) {
                    Text("Schedule & Notify Residents", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}
