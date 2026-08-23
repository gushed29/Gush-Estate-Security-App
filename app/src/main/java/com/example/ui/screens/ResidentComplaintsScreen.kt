package com.example.ui.screens

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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SupportAgent
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.ResidentComplaintEntity
import com.example.security.SecurityUtils
import com.example.ui.theme.FrostedGlassBorder
import com.example.ui.theme.FrostedGlassSurface
import com.example.ui.theme.GushedCobalt
import com.example.ui.theme.GushedCrimsonDenied
import com.example.ui.theme.GushedEmeraldApproved
import com.example.ui.theme.GushedTextPrimary
import com.example.ui.theme.GushedTextSecondary

@Composable
fun ResidentComplaintsScreen(
    isAdmin: Boolean,
    residentUnit: String,
    complaints: List<ResidentComplaintEntity>,
    onSubmitComplaint: (title: String, category: String, severity: String, description: String, imageUrl: String) -> Unit,
    onResolveComplaint: (complaintId: String, status: String, response: String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var selectedComplaintToReview by remember { mutableStateOf<ResidentComplaintEntity?>(null) }

    val filteredComplaints = if (isAdmin) {
        complaints
    } else {
        complaints.filter { it.unitNumber == residentUnit || it.unitNumber.isEmpty() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isAdmin) "ESTATE INCIDENT & SERVICE TICKETS" else "REPORTED ISSUES & COMPLAINTS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GushedTextSecondary
                    )
                    Text(
                        text = "${filteredComplaints.size} Active & Resolved Tickets",
                        style = MaterialTheme.typography.bodySmall,
                        color = GushedCobalt
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (filteredComplaints.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = GushedCobalt.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No open complaints or issues reported",
                            style = MaterialTheme.typography.bodyMedium,
                            color = GushedTextSecondary
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredComplaints) { item ->
                        ComplaintItemCard(
                            complaint = item,
                            isAdmin = isAdmin,
                            onReview = { selectedComplaintToReview = item }
                        )
                    }
                }
            }
        }

        // FAB to submit new issue
        FloatingActionButton(
            onClick = { showCreateDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = GushedCobalt,
            contentColor = Color.White
        ) {
            Row(modifier = Modifier.padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("File Issue / Complain", fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showCreateDialog) {
        CreateComplaintDialog(
            onDismiss = { showCreateDialog = false },
            onSubmit = { title, cat, sev, desc, img ->
                onSubmitComplaint(title, cat, sev, desc, img)
                showCreateDialog = false
            }
        )
    }

    selectedComplaintToReview?.let { comp ->
        ReviewComplaintDialog(
            complaint = comp,
            isAdmin = isAdmin,
            onDismiss = { selectedComplaintToReview = null },
            onUpdate = { status, response ->
                onResolveComplaint(comp.id, status, response)
                selectedComplaintToReview = null
            }
        )
    }
}

@Composable
fun ComplaintItemCard(
    complaint: ResidentComplaintEntity,
    isAdmin: Boolean,
    onReview: () -> Unit
) {
    val isResolved = complaint.status == "RESOLVED"
    val statusColor = when (complaint.status) {
        "RESOLVED" -> GushedEmeraldApproved
        "INVESTIGATING" -> Color(0xFFD97706)
        else -> GushedCrimsonDenied
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.2.dp, FrostedGlassBorder, RoundedCornerShape(16.dp))
            .clickable { onReview() },
        colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = complaint.ticketCode,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = GushedCobalt,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "• ${complaint.category.replace("_", " ")}",
                        style = MaterialTheme.typography.labelSmall,
                        color = GushedTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = complaint.status,
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = complaint.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = GushedTextPrimary
            )

            Text(
                text = complaint.description,
                style = MaterialTheme.typography.bodySmall,
                color = GushedTextSecondary,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${complaint.residentName} (${complaint.unitNumber})",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = SecurityUtils.formatTimestamp(complaint.createdTimestamp).takeLast(8),
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
fun CreateComplaintDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, category: String, severity: String, description: String, imageUrl: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("SECURITY") }
    var severity by remember { mutableStateOf("HIGH") }
    var description by remember { mutableStateOf("") }
    var hasPhotoEvidence by remember { mutableStateOf(false) }

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
                    Text("File Complain / Ticket", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Issue Summary") },
                    placeholder = { Text("e.g. Unregistered vehicle parked in reserved slot") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("SECURITY", "NOISE", "FACILITY", "PARKING").forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat, fontSize = 10.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detailed Description") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Photo attachment toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { hasPhotoEvidence = !hasPhotoEvidence }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = if (hasPhotoEvidence) GushedEmeraldApproved else GushedTextSecondary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasPhotoEvidence) "📸 Photo Evidence Attached (complaint_photo.jpg)" else "Attach Photo Proof",
                        fontSize = 12.sp,
                        fontWeight = if (hasPhotoEvidence) FontWeight.Bold else FontWeight.Normal,
                        color = if (hasPhotoEvidence) GushedEmeraldApproved else GushedTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank() && description.isNotBlank()) {
                            val imgUrl = if (hasPhotoEvidence) "https://images.unsplash.com/photo-1557804506-669a67965ba0?w=600" else ""
                            onSubmit(title.trim(), category, severity, description.trim(), imgUrl)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submit to Security & Admin", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }
}

@Composable
fun ReviewComplaintDialog(
    complaint: ResidentComplaintEntity,
    isAdmin: Boolean,
    onDismiss: () -> Unit,
    onUpdate: (status: String, response: String) -> Unit
) {
    var responseText by remember { mutableStateOf(complaint.adminResponse) }

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
                    Text("Ticket #${complaint.ticketCode}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(complaint.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = GushedTextPrimary)
                Text("Reported by ${complaint.residentName} (${complaint.unitNumber})", fontSize = 12.sp, color = GushedTextSecondary)

                Spacer(modifier = Modifier.height(8.dp))
                Text(complaint.description, fontSize = 13.sp, color = GushedTextPrimary)

                if (complaint.imageAttachmentUrl.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFDBEAFE)) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = GushedCobalt, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Photo Evidence Attached", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GushedCobalt)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isAdmin) {
                    OutlinedTextField(
                        value = responseText,
                        onValueChange = { responseText = it },
                        label = { Text("Officer / Admin Response Note") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { onUpdate("INVESTIGATING", responseText) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                        ) {
                            Text("Investigating", fontSize = 11.sp, color = Color.White)
                        }
                        Button(
                            onClick = { onUpdate("RESOLVED", responseText) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GushedEmeraldApproved)
                        ) {
                            Text("Resolve Ticket", fontSize = 11.sp, color = Color.White)
                        }
                    }
                } else {
                    if (complaint.adminResponse.isNotBlank()) {
                        Text("ESTATE RESPONSE:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GushedTextSecondary)
                        Text(complaint.adminResponse, fontSize = 12.sp, color = GushedEmeraldApproved, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
