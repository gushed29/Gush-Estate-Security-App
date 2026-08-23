package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.DoorSliding
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.EstateBroadcastEntity
import com.example.data.local.entities.EstateFeeInvoiceEntity
import com.example.data.local.entities.EstateMeetingEntity
import com.example.data.local.entities.EstateMessageEntity
import com.example.data.local.entities.FamilyMemberEntity
import com.example.data.local.entities.MeetingContributionEntity
import com.example.data.local.entities.MeetingPollEntity
import com.example.data.local.entities.MessageAttachmentType
import com.example.data.local.entities.MessageChannelType
import com.example.data.local.entities.PassStatus
import com.example.data.local.entities.PassType
import com.example.data.local.entities.ResidentAccountEntity
import com.example.data.local.entities.ResidentComplaintEntity
import com.example.data.local.entities.SecurityGateEntity
import com.example.data.local.entities.VisitorPassEntity
import com.example.security.SecurityUtils
import com.example.ui.theme.FrostedGlassBorder
import com.example.ui.theme.FrostedGlassSurface
import com.example.ui.theme.GushedAmberDark
import com.example.ui.theme.GushedAmberWarning
import com.example.ui.theme.GushedBorder
import com.example.ui.theme.GushedCobalt
import com.example.ui.theme.GushedCrimsonDark
import com.example.ui.theme.GushedCrimsonDenied
import com.example.ui.theme.GushedCyanAccent
import com.example.ui.theme.GushedEmeraldApproved
import com.example.ui.theme.GushedEmeraldDark
import com.example.ui.theme.GushedIndigoStaff
import com.example.ui.theme.GushedPurpleContractor
import com.example.ui.theme.GushedSurfaceContainer
import com.example.ui.theme.GushedSurfaceDark
import com.example.ui.theme.GushedSurfaceElevated
import com.example.ui.theme.GushedTextMuted
import com.example.ui.theme.GushedTextPrimary
import com.example.ui.theme.GushedTextSecondary

@Composable
fun ResidentDashboardScreen(
    activeResident: ResidentAccountEntity?,
    passes: List<VisitorPassEntity>,
    familyMembers: List<FamilyMemberEntity>,
    messages: List<EstateMessageEntity>,
    broadcasts: List<EstateBroadcastEntity>,
    invoices: List<EstateFeeInvoiceEntity>,
    meetings: List<EstateMeetingEntity>,
    complaints: List<ResidentComplaintEntity>,
    securityGates: List<SecurityGateEntity>,
    onSelectPassForDetail: (VisitorPassEntity) -> Unit,
    onRevokePass: (passId: String, reason: String) -> Unit,
    onOpenCreatePassDialog: () -> Unit,
    onAddFamilyMember: (fullName: String, rel: String, phone: String, email: String, access: String, plate: String) -> Unit,
    onDeleteFamilyMember: (FamilyMemberEntity) -> Unit,
    onStartCall: (receiverName: String, receiverRole: String, receiverUnit: String, isVideo: Boolean, gatePost: String) -> Unit,
    onSendMessage: (channelType: MessageChannelType, convId: String, text: String, attachType: MessageAttachmentType, url: String, filename: String) -> Unit,
    onAcknowledgeBroadcast: (String) -> Unit,
    onPayInvoice: (invoice: EstateFeeInvoiceEntity, method: String) -> Unit,
    onPostMeetingContribution: (meetingId: String, msg: String, isHand: Boolean, vote: String) -> Unit,
    onVoteMeetingPoll: (poll: MeetingPollEntity, opt: Int) -> Unit,
    onSubmitComplaint: (title: String, cat: String, sev: String, desc: String, img: String) -> Unit,
    onUpdateResidentProfile: (name: String, unit: String, phone: String, email: String, vehicles: String, emergency: String) -> Unit
) {
    var residentSubTab by remember { mutableStateOf("PASSES") }
    var selectedPassFilter by remember { mutableStateOf("ALL") }
    var passToRevoke by remember { mutableStateOf<VisitorPassEntity?>(null) }
    var showAddFamilyDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val resName = activeResident?.fullName ?: "Resident Host"
    val resUnit = activeResident?.unitNumber ?: "Villa 14B"

    data class SubTabItem(val key: String, val label: String, val icon: ImageVector)
    val subTabs = listOf(
        SubTabItem("PASSES", "Visitor Passes", Icons.Default.QrCode),
        SubTabItem("FAMILY", "Family Access (${familyMembers.size})", Icons.Default.FamilyRestroom),
        SubTabItem("INTERCOM", "2-Way Calls", Icons.Default.Call),
        SubTabItem("BULLETIN", "Estate Bulletin", Icons.Default.Campaign),
        SubTabItem("MESSAGES", "Comms & Chat", Icons.Default.Forum),
        SubTabItem("FINANCE", "Levies & Dues", Icons.Default.Payments),
        SubTabItem("MEETINGS", "Townhalls & AGM", Icons.Default.Groups),
        SubTabItem("COMPLAINTS", "Issues & Tickets", Icons.Default.SupportAgent),
        SubTabItem("MY_RESIDENCE", "Residence Profile", Icons.Default.Home)
    )

    val filteredPasses = when (selectedPassFilter) {
        "SCHEDULED" -> passes.filter { it.status == PassStatus.SCHEDULED.name && !it.isRevoked }
        "INSIDE" -> passes.filter { it.status == PassStatus.ACTIVE_INSIDE.name }
        "EXPIRED_REVOKED" -> passes.filter { it.status == PassStatus.COMPLETED_EXIT.name || it.isRevoked || it.status == PassStatus.EXPIRED.name }
        else -> passes
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Resident Info Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, FrostedGlassBorder),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GushedCobalt.copy(alpha = 0.12f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Home, contentDescription = null, tint = GushedCobalt, modifier = Modifier.size(24.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = resName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = GushedTextPrimary
                            )
                            Text(
                                text = "$resUnit • ${activeResident?.estateName ?: "Pinnock Beach Estate"}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GushedTextSecondary
                            )
                        }
                    }

                    // Direct Intercom Call to Gate Shortcut
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = GushedEmeraldApproved.copy(alpha = 0.12f),
                        modifier = Modifier.clickable {
                            onStartCall("Gate 1 Main Gate Duty Officer", "GUARD", "Gate 1 Post", true, "Gate 1 - Pinnock Beach Estate Main Gate")
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = GushedEmeraldApproved, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Call Gate", color = GushedEmeraldApproved, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Horizontally Scrollable Sub-tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                subTabs.forEach { tab ->
                    val isSelected = residentSubTab == tab.key
                    FilterChip(
                        selected = isSelected,
                        onClick = { residentSubTab = tab.key },
                        label = { Text(tab.label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        leadingIcon = {
                            Icon(tab.icon, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GushedCobalt,
                            selectedLabelColor = Color.White,
                            selectedLeadingIconColor = Color.White
                        )
                    )
                }
            }

            // Sub-tab Contents
            when (residentSubTab) {
                "PASSES" -> {
                    // Filter Chips & Passes List
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("ALL" to "All", "SCHEDULED" to "Scheduled", "INSIDE" to "Inside", "EXPIRED_REVOKED" to "History").forEach { (filter, label) ->
                                FilterChip(
                                    selected = selectedPassFilter == filter,
                                    onClick = { selectedPassFilter = filter },
                                    label = { Text(label, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = GushedCobalt,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }

                    if (filteredPasses.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.QrCode, contentDescription = null, tint = GushedCobalt.copy(alpha = 0.4f), modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No passes found under selected filter", color = GushedTextSecondary)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredPasses) { pass ->
                                VisitorPassCardItem(
                                    pass = pass,
                                    onSelect = { onSelectPassForDetail(pass) },
                                    onRevoke = { passToRevoke = pass }
                                )
                            }
                        }
                    }
                }

                "FAMILY" -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("HOUSEHOLD & FAMILY ACCESS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GushedTextSecondary)
                                Text("Permanent PINs & QR badges for your family & staff", fontSize = 11.sp, color = Color(0xFF64748B))
                            }
                            Button(
                                onClick = { showAddFamilyDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Member", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (familyMembers.isEmpty()) {
                            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text("No family members or household staff added yet.", color = GushedTextSecondary)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                items(familyMembers) { member ->
                                    FamilyMemberDigitalPassCard(
                                        member = member,
                                        onDelete = { onDeleteFamilyMember(member) }
                                    )
                                }
                            }
                        }
                    }
                }

                "INTERCOM" -> {
                    // Dedicated 2-Way Calling Hub
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(1.2.dp, FrostedGlassBorder, RoundedCornerShape(18.dp)),
                                colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("2-WAY GATEHOUSE INTERCOM", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GushedTextSecondary)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Instantly speak with on-duty security officers or verify unexpected visitors at gate barriers.", fontSize = 12.sp, color = GushedTextPrimary)

                                    Spacer(modifier = Modifier.height(14.dp))

                                    securityGates.forEach { gate ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFFF1F5F9))
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Surface(shape = CircleShape, color = GushedCobalt.copy(alpha = 0.12f), modifier = Modifier.size(34.dp)) {
                                                    Box(contentAlignment = Alignment.Center) {
                                                        Icon(Icons.Default.DoorSliding, contentDescription = null, tint = GushedCobalt, modifier = Modifier.size(18.dp))
                                                    }
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(gate.gateName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GushedTextPrimary)
                                                    Text("Status: ${gate.status} • Duty Shift", fontSize = 10.sp, color = Color(0xFF64748B))
                                                }
                                            }

                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                IconButton(
                                                    onClick = { onStartCall(gate.gateName, "GUARD", gate.location, false, gate.gateName) },
                                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFD1FAE5))
                                                ) {
                                                    Icon(Icons.Default.Call, contentDescription = "Audio Call", tint = GushedEmeraldApproved, modifier = Modifier.size(16.dp))
                                                }
                                                IconButton(
                                                    onClick = { onStartCall(gate.gateName, "GUARD", gate.location, true, gate.gateName) },
                                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFDBEAFE))
                                                ) {
                                                    Icon(Icons.Default.Videocam, contentDescription = "Video Intercom", tint = GushedCobalt, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(18.dp))
                                    .border(1.2.dp, FrostedGlassBorder, RoundedCornerShape(18.dp)),
                                colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("ADMIN DIRECTORATE & CSO CALL", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GushedTextSecondary)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF1F5F9))
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Chief Security Officer (Command)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("Col. Davies • 24/7 Emergency Line", fontSize = 11.sp, color = GushedTextSecondary)
                                        }

                                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Button(
                                                onClick = { onStartCall("Col. Davies (CSO Command)", "ADMIN", "Security HQ", false, "Command Centre") },
                                                colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt),
                                                shape = RoundedCornerShape(8.dp)
                                            ) {
                                                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Direct Call", fontSize = 11.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                "BULLETIN" -> {
                    EstateBulletinBoardScreen(
                        isAdmin = false,
                        broadcasts = broadcasts,
                        onPublishBroadcast = { _, _, _, _, _, _ -> },
                        onAcknowledge = onAcknowledgeBroadcast
                    )
                }

                "MESSAGES" -> {
                    EstateMessagesScreen(
                        currentUserId = activeResident?.id ?: "res_me",
                        currentUserName = resName,
                        currentUserRole = "RESIDENT",
                        messages = messages,
                        onSendMessage = onSendMessage,
                        onStartCall = { name, role, unit, isVideo ->
                            onStartCall(name, role, unit, isVideo, "Main Gate")
                        }
                    )
                }

                "FINANCE" -> {
                    EstateFeePaymentsScreen(
                        isAdmin = false,
                        residentUnit = resUnit,
                        allResidents = listOfNotNull(activeResident),
                        invoices = invoices,
                        onPayInvoice = onPayInvoice,
                        onCreateInvoice = { _, _, _, _, _, _ -> }
                    )
                }

                "MEETINGS" -> {
                    CommunityMeetingsScreen(
                        isAdmin = false,
                        currentUserName = resName,
                        meetings = meetings,
                        onScheduleMeeting = { _, _, _, _, _, _ -> },
                        onPostContribution = onPostMeetingContribution,
                        onVotePoll = onVoteMeetingPoll
                    )
                }

                "COMPLAINTS" -> {
                    ResidentComplaintsScreen(
                        isAdmin = false,
                        residentUnit = resUnit,
                        complaints = complaints,
                        onSubmitComplaint = onSubmitComplaint,
                        onResolveComplaint = { _, _, _ -> }
                    )
                }

                "MY_RESIDENCE" -> {
                    // Full Resident Registration Profile Overview
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .border(1.2.dp, FrostedGlassBorder, RoundedCornerShape(20.dp)),
                                colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface)
                            ) {
                                Column(modifier = Modifier.padding(18.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("OFFICIAL RESIDENCE PROFILE", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = GushedTextSecondary)
                                        Button(
                                            onClick = { showEditProfileDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9)),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Edit Profile", color = GushedCobalt, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    listOf(
                                        "Primary Resident" to resName,
                                        "Property Unit" to resUnit,
                                        "Estate Community" to (activeResident?.estateName ?: "Pinnock Beach Estate"),
                                        "Primary Gate Access" to (activeResident?.primaryGate ?: "Gate 1"),
                                        "Telephone" to (activeResident?.phone?.ifEmpty { "+234 803 123 4567" } ?: "N/A"),
                                        "Email" to (activeResident?.email?.ifEmpty { "resident@pinnock.estate" } ?: "N/A"),
                                        "Registered Vehicles" to (activeResident?.registeredVehicles?.ifEmpty { "KJA-992-ZZ, LAG-849-XY" } ?: "N/A"),
                                        "Emergency Contact" to (activeResident?.emergencyContact?.ifEmpty { "+234 800-EMERGENCY" } ?: "N/A")
                                    ).forEach { (label, value) ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(label, fontSize = 12.sp, color = GushedTextSecondary)
                                            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // FAB to Generate New Pass (Visible in PASSES subtab)
        if (residentSubTab == "PASSES") {
            FloatingActionButton(
                onClick = onOpenCreatePassDialog,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp),
                containerColor = GushedCobalt,
                contentColor = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Create Pass")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Generate Pass", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Revoke Pass Confirmation Dialog
    passToRevoke?.let { pass ->
        AlertDialog(
            onDismissRequest = { passToRevoke = null },
            title = { Text("Revoke Visitor Pass?") },
            text = { Text("Are you sure you want to revoke pass #${pass.pinCode} for ${pass.visitorName}? They will be denied entry immediately.") },
            confirmButton = {
                Button(
                    onClick = {
                        onRevokePass(pass.id, "Revoked by host resident")
                        passToRevoke = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GushedCrimsonDenied)
                ) {
                    Text("Yes, Revoke", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { passToRevoke = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Family Member Dialog
    if (showAddFamilyDialog) {
        AddFamilyMemberDialog(
            residentUnit = resUnit,
            onDismiss = { showAddFamilyDialog = false },
            onAddMember = { name, rel, phone, email, access, plate ->
                onAddFamilyMember(name, rel, phone, email, access, plate)
                showAddFamilyDialog = false
            }
        )
    }

    // Edit Resident Profile Dialog
    if (showEditProfileDialog && activeResident != null) {
        ResidentProfileDialog(
            resident = activeResident,
            onDismiss = { showEditProfileDialog = false },
            onSaveProfile = { name, unit, phone, email, vehicles, emergency ->
                onUpdateResidentProfile(name, unit, phone, email, vehicles, emergency)
                showEditProfileDialog = false
            }
        )
    }
}

@Composable
fun VisitorPassCardItem(
    pass: VisitorPassEntity,
    onSelect: () -> Unit,
    onRevoke: () -> Unit
) {
    val isExpired = pass.status == PassStatus.EXPIRED.name || (System.currentTimeMillis() > pass.validUntilEpoch && pass.status == PassStatus.SCHEDULED.name)
    val isInside = pass.status == PassStatus.ACTIVE_INSIDE.name
    val isRevoked = pass.isRevoked || pass.status == PassStatus.REVOKED.name

    val statusColor = when {
        isRevoked -> GushedCrimsonDenied
        isInside -> GushedEmeraldApproved
        isExpired -> GushedAmberWarning
        else -> GushedCobalt
    }

    val typeIcon = when (pass.visitorType) {
        PassType.GUEST.name -> Icons.Default.Person
        PassType.DELIVERY.name -> Icons.Default.LocalShipping
        "CAB_RIDE", PassType.DOMESTIC_STAFF.name -> Icons.Default.DirectionsCar
        PassType.CONTRACTOR.name -> Icons.Default.Inventory
        else -> Icons.Default.Person
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.2.dp, FrostedGlassBorder, RoundedCornerShape(18.dp))
            .clickable { onSelect() },
        colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
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
                    Surface(
                        shape = CircleShape,
                        color = statusColor.copy(alpha = 0.12f),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(typeIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = pass.visitorName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextPrimary
                        )
                        Text(
                            text = "${pass.visitorType} • Gate: ${pass.allowedGate.take(12)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = GushedTextSecondary
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isRevoked) "REVOKED" else if (isInside) "INSIDE ESTATE" else if (isExpired) "EXPIRED" else "SCHEDULED",
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // PIN & Validity Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ACCESS CODE: ", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    Text(
                        text = pass.pinCode,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = GushedCobalt,
                        fontSize = 14.sp
                    )
                }

                Text(
                    text = "Valid till: ${SecurityUtils.formatTimestamp(pass.validUntilEpoch).takeLast(11)}",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (pass.vehiclePlate.isNotBlank()) "🚗 Plate: ${pass.vehiclePlate}" else "🚶 Walk-in Guest",
                    fontSize = 11.sp,
                    color = GushedTextSecondary
                )

                if (!isRevoked && !isExpired && !isInside) {
                    Button(
                        onClick = onRevoke,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text("Revoke", color = Color(0xFFDC2626), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
