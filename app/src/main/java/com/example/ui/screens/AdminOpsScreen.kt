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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DoorSliding
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.local.entities.AuditLogEntity
import com.example.data.local.entities.EstateBroadcastEntity
import com.example.data.local.entities.EstateFeeInvoiceEntity
import com.example.data.local.entities.EstateMeetingEntity
import com.example.data.local.entities.EstateMessageEntity
import com.example.data.local.entities.GateEventEntity
import com.example.data.local.entities.GuardAccountEntity
import com.example.data.local.entities.IncidentEntity
import com.example.data.local.entities.MeetingPollEntity
import com.example.data.local.entities.MessageAttachmentType
import com.example.data.local.entities.MessageChannelType
import com.example.data.local.entities.ResidentAccountEntity
import com.example.data.local.entities.ResidentComplaintEntity
import com.example.data.local.entities.SecurityGateEntity
import com.example.data.local.entities.SecurityPolicyEntity
import com.example.data.local.entities.VisitorPassEntity
import com.example.security.SecurityUtils
import com.example.ui.theme.FrostedGlassBorder
import com.example.ui.theme.FrostedGlassBorderMuted
import com.example.ui.theme.FrostedGlassSurface
import com.example.ui.theme.FrostedGlassSurfaceElevated
import com.example.ui.theme.GushedAmberWarning
import com.example.ui.theme.GushedCobalt
import com.example.ui.theme.GushedCrimsonDenied
import com.example.ui.theme.GushedEmeraldApproved
import com.example.ui.theme.GushedTextMuted
import com.example.ui.theme.GushedTextPrimary
import com.example.ui.theme.GushedTextSecondary
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SupportAgent

@Composable
fun AdminOpsScreen(
    gateEvents: List<GateEventEntity>,
    incidents: List<IncidentEntity>,
    auditLogs: List<AuditLogEntity>,
    policies: List<SecurityPolicyEntity>,
    allPasses: List<VisitorPassEntity>,
    securityGates: List<SecurityGateEntity>,
    residents: List<ResidentAccountEntity>,
    guards: List<GuardAccountEntity>,
    broadcasts: List<EstateBroadcastEntity>,
    invoices: List<EstateFeeInvoiceEntity>,
    meetings: List<EstateMeetingEntity>,
    complaints: List<ResidentComplaintEntity>,
    messages: List<EstateMessageEntity>,
    onTogglePolicy: (key: String, isEnabled: Boolean) -> Unit,
    onResolveIncident: (incident: IncidentEntity, notes: String) -> Unit,
    onOpenIncidentDialog: () -> Unit,
    onCreateGate: (gateName: String, gateCode: String, estateName: String, location: String, operatingHours: String, isPrimary: Boolean) -> Unit,
    onToggleGateStatus: (gateId: String, status: String) -> Unit,
    onCreateResident: (fullName: String, unitNumber: String, estateName: String, primaryGate: String, phone: String, email: String, passcode: String, registeredVehicles: String) -> Unit,
    onDeleteResident: (resident: ResidentAccountEntity) -> Unit,
    onCreateGuard: (fullName: String, badgeId: String, assignedGate: String, shift: String, phone: String) -> Unit,
    onDeleteGuard: (guard: GuardAccountEntity) -> Unit,
    onPublishBroadcast: (title: String, category: String, priority: String, content: String, audience: String, isPinned: Boolean) -> Unit,
    onAcknowledgeBroadcast: (String) -> Unit,
    onCreateInvoice: (ResidentAccountEntity, String, String, Double, String, Int) -> Unit,
    onPayInvoice: (EstateFeeInvoiceEntity, String) -> Unit,
    onScheduleMeeting: (String, String, String, Long, Int, String) -> Unit,
    onPostMeetingContribution: (String, String, Boolean, String) -> Unit,
    onVoteMeetingPoll: (MeetingPollEntity, Int) -> Unit,
    onSubmitComplaint: (String, String, String, String, String) -> Unit,
    onResolveComplaint: (String, String, String) -> Unit,
    onSendMessage: (MessageChannelType, String, String, MessageAttachmentType, String, String) -> Unit,
    onStartCall: (String, String, String, Boolean, String) -> Unit
) {
    var adminSubTab by remember { mutableStateOf("OVERVIEW") }
    var showAddGateDialog by remember { mutableStateOf(false) }
    var showAddResidentDialog by remember { mutableStateOf(false) }
    var showAddGuardDialog by remember { mutableStateOf(false) }
    var selectedIncidentForResolve by remember { mutableStateOf<IncidentEntity?>(null) }
    var resolveNotes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High Level Metrics (Frosted Metric Cards)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val activeInside = allPasses.count { it.status == "ACTIVE_INSIDE" }
            val totalGates = securityGates.size
            val totalRes = residents.size
            val openIncidents = incidents.count { it.status == "OPEN" }

            listOf(
                Triple("Gates", "$totalGates", GushedCobalt),
                Triple("Residents", "$totalRes", Color(0xFF059669)),
                Triple("Inside", "$activeInside", GushedEmeraldApproved),
                Triple("Incidents", "$openIncidents", if (openIncidents > 0) GushedAmberWarning else GushedTextSecondary)
            ).forEach { (label, count, color) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.75f))
                        .border(1.dp, Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 10.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text(text = count, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
                        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.SemiBold, color = GushedTextMuted)
                    }
                }
            }
        }

        // Sub-tabs (Horizontally Scrollable Frosted Glass Chips)
        data class AdminTabItem(
            val key: String,
            val label: String,
            val icon: androidx.compose.ui.graphics.vector.ImageVector
        )
        val tabItems = listOf(
            AdminTabItem("OVERVIEW", "Live Feed", Icons.Default.Timeline),
            AdminTabItem("GATES", "Gates (${securityGates.size})", Icons.Default.DoorSliding),
            AdminTabItem("RESIDENTS", "Residents (${residents.size})", Icons.Default.Home),
            AdminTabItem("GUARDS", "Guards (${guards.size})", Icons.Default.Security),
            AdminTabItem("BULLETIN", "Broadcasts (${broadcasts.size})", Icons.Default.Campaign),
            AdminTabItem("FINANCE", "Levies & Dues", Icons.Default.Payments),
            AdminTabItem("MEETINGS", "Townhalls (${meetings.size})", Icons.Default.Groups),
            AdminTabItem("COMPLAINTS", "Tickets (${complaints.size})", Icons.Default.SupportAgent),
            AdminTabItem("MESSAGES", "Comms & Chat", Icons.Default.Forum),
            AdminTabItem("AUDIT_CHAIN", "Audit Trail", Icons.Default.Fingerprint),
            AdminTabItem("POLICIES", "Policies", Icons.Default.Policy),
            AdminTabItem("INCIDENTS", "Incidents (${incidents.size})", Icons.Default.ReportProblem)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            tabItems.forEach { item ->
                val isSelected = adminSubTab == item.key
                FilterChip(
                    selected = isSelected,
                    onClick = { adminSubTab = item.key },
                    shape = RoundedCornerShape(14.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(14.dp),
                            tint = if (isSelected) Color.White else GushedTextSecondary
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GushedCobalt,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White.copy(alpha = 0.6f),
                        labelColor = GushedTextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) GushedCobalt else Color.White.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier.height(34.dp)
                )
            }
        }

        // Main Content Area based on Tab
        when (adminSubTab) {
            "OVERVIEW" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ESTATE ACCESS & TELEMETRY STREAM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushedTextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "${gateEvents.size} events logged",
                                fontSize = 10.sp,
                                color = GushedTextMuted
                            )
                        }
                    }

                    if (gateEvents.isEmpty()) {
                        item {
                            EmptyPlaceholder("No recent gate events recorded.")
                        }
                    }

                    items(gateEvents) { event ->
                        val isEntry = event.eventType == "CHECK_IN"
                        val isExit = event.eventType == "CHECK_OUT"
                        val isDenied = event.eventType == "ACCESS_DENIED"
                        val isEmergency = event.eventType == "EMERGENCY_OVERRIDE"

                        val (badgeBg, badgeColor, icon) = when {
                            isEmergency -> Triple(Color(0xFFFEE2E2), GushedCrimsonDenied, Icons.Default.FlashOn)
                            isDenied -> Triple(Color(0xFFFEE2E2), GushedCrimsonDenied, Icons.Default.Error)
                            event.isDiscrepancy -> Triple(Color(0xFFFEF3C7), GushedAmberWarning, Icons.Default.Warning)
                            isExit -> Triple(Color(0xFFEEF2FF), GushedCobalt, Icons.Default.SwapHoriz)
                            else -> Triple(Color(0xFFD1FAE5), GushedEmeraldApproved, Icons.Default.CheckCircle)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(badgeBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = icon, contentDescription = event.eventType, tint = badgeColor, modifier = Modifier.size(20.dp))
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${event.visitorName} • ${event.gateName}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GushedTextPrimary
                                        )
                                        Text(
                                            text = SecurityUtils.formatTimeOnly(event.timestamp),
                                            fontSize = 11.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = GushedTextMuted
                                        )
                                    }

                                    Text(
                                        text = event.decisionNote,
                                        fontSize = 12.sp,
                                        color = if (event.isDiscrepancy) Color(0xFFB45309) else GushedTextSecondary
                                    )

                                    if (event.vehiclePlate.isNotEmpty()) {
                                        Text(
                                            text = "Plate: ${event.vehiclePlate} • Host: ${event.hostResident} • Guard: ${event.guardName}",
                                            fontSize = 10.sp,
                                            color = GushedTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "GATES" -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SECURITY GATE POSTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextSecondary
                        )
                        Button(
                            onClick = { showAddGateDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Security Gate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(securityGates) { gate ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFFEEF2FF))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = gate.gateCode,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GushedCobalt
                                                )
                                            }
                                            if (gate.isPrimaryGate) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0xFFFEF3C7))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "PRIMARY GATE",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF92400E)
                                                    )
                                                }
                                            }
                                        }

                                        // Status Pill
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (gate.status == "OPERATIONAL") Color(0xFFECFDF5)
                                                    else Color(0xFFFEE2E2)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = gate.status,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (gate.status == "OPERATIONAL") GushedEmeraldApproved else GushedCrimsonDenied
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = gate.gateName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GushedTextPrimary
                                    )

                                    Text(
                                        text = "${gate.estateName} • ${gate.location}",
                                        fontSize = 11.sp,
                                        color = GushedTextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Hours: ${gate.operatingHours}",
                                            fontSize = 11.sp,
                                            color = GushedTextMuted
                                        )

                                        OutlinedButton(
                                            onClick = {
                                                val nextStatus = if (gate.status == "OPERATIONAL") "RESTRICTED" else "OPERATIONAL"
                                                onToggleGateStatus(gate.id, nextStatus)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text(
                                                text = if (gate.status == "OPERATIONAL") "Restrict Gate" else "Activate Gate",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GushedCobalt
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "RESIDENTS" -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REGISTERED RESIDENTS & PROPERTIES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextSecondary
                        )
                        Button(
                            onClick = { showAddResidentDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Resident Access", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(residents) { res ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFECFDF5)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = "Resident",
                                                tint = Color(0xFF059669),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = res.fullName,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GushedTextPrimary
                                            )
                                            Text(
                                                text = "${res.unitNumber} (${res.estateName})",
                                                fontSize = 11.sp,
                                                color = GushedTextSecondary
                                            )
                                            Text(
                                                text = "Primary: ${res.primaryGate} • ${res.phone}",
                                                fontSize = 10.sp,
                                                color = GushedTextMuted
                                            )
                                            if (res.registeredVehicles.isNotEmpty()) {
                                                Text(
                                                    text = "Vehicles: ${res.registeredVehicles}",
                                                    fontSize = 10.sp,
                                                    color = GushedCobalt
                                                )
                                            }
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteResident(res) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "GUARDS" -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SECURITY OFFICER ACCOUNTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextSecondary
                        )
                        Button(
                            onClick = { showAddGuardDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Guard Access", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(guards) { guard ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFEFF6FF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                Icons.Default.Badge,
                                                contentDescription = "Guard",
                                                tint = Color(0xFF2563EB),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = guard.fullName,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GushedTextPrimary
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(Color(0xFFEEF2FF))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = guard.badgeId,
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = GushedCobalt
                                                    )
                                                }
                                            }
                                            Text(
                                                text = "Assigned: ${guard.assignedGate}",
                                                fontSize = 11.sp,
                                                color = GushedTextSecondary
                                            )
                                            Text(
                                                text = "${guard.shift} • Phone: ${guard.phone}",
                                                fontSize = 10.sp,
                                                color = GushedTextMuted
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { onDeleteGuard(guard) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFDC2626),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "AUDIT_CHAIN" -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFEEF2FF).copy(alpha = 0.8f))
                            .border(1.dp, Color(0xFFC7D2FE), RoundedCornerShape(16.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "SHA256",
                                tint = GushedCobalt,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "TAMPER-EVIDENT SHA-256 AUDIT LOG",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    color = GushedCobalt
                                )
                                Text(
                                    text = "Every security event is cryptographically linked to previous hash.",
                                    fontSize = 11.sp,
                                    color = GushedTextSecondary
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(auditLogs) { log ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFFEEF2FF))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = log.role,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = GushedCobalt
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "${log.actor} • ${log.action}",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GushedTextPrimary
                                            )
                                        }

                                        Text(
                                            text = SecurityUtils.formatTimeOnly(log.timestamp),
                                            fontSize = 10.sp,
                                            color = GushedTextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = log.details,
                                        fontSize = 11.sp,
                                        color = GushedTextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "HASH: ${log.currentHash.take(24)}...",
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = GushedCobalt
                                    )
                                }
                            }
                        }
                    }
                }
            }

            "POLICIES" -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(policies) { policy ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(18.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = policy.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GushedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = policy.description,
                                        fontSize = 11.sp,
                                        color = GushedTextSecondary
                                    )
                                }

                                Switch(
                                    checked = policy.isEnabled,
                                    onCheckedChange = { onTogglePolicy(policy.policyKey, it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = GushedCobalt,
                                        uncheckedThumbColor = Color.White,
                                        uncheckedTrackColor = Color(0xFFCBD5E1)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            "INCIDENTS" -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SECURITY INCIDENT LOG",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextSecondary
                        )
                        Button(
                            onClick = onOpenIncidentDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Incident", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (incidents.isEmpty()) {
                            item {
                                EmptyPlaceholder("No incidents reported.")
                            }
                        }

                        items(incidents) { incident ->
                            val isOpen = incident.status == "OPEN"
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isOpen) Color(0xFFFEF2F2).copy(alpha = 0.9f) else Color.White.copy(alpha = 0.85f)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isOpen) Color(0xFFFECACA) else Color.White
                                ),
                                shape = RoundedCornerShape(18.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        if (incident.severity == "CRITICAL" || incident.severity == "HIGH") Color(0xFFFEE2E2)
                                                        else Color(0xFFFEF3C7)
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "${incident.severity} • ${incident.incidentCode}",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (incident.severity == "CRITICAL" || incident.severity == "HIGH") GushedCrimsonDenied else Color(0xFF92400E)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = incident.category,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = GushedTextSecondary
                                            )
                                        }

                                        Text(
                                            text = SecurityUtils.formatTimestamp(incident.timestamp),
                                            fontSize = 10.sp,
                                            color = GushedTextMuted
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(
                                        text = incident.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GushedTextPrimary
                                    )

                                    Text(
                                        text = incident.description,
                                        fontSize = 12.sp,
                                        color = GushedTextSecondary
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Logged by: ${incident.guardName} @ ${incident.gateName}",
                                            fontSize = 10.sp,
                                            color = GushedTextMuted
                                        )

                                        if (isOpen) {
                                            Button(
                                                onClick = {
                                                    selectedIncidentForResolve = incident
                                                    resolveNotes = ""
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.height(30.dp)
                                            ) {
                                                Text("Resolve", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                            }
                                        } else {
                                            Text(
                                                text = "✓ Resolved",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GushedEmeraldApproved
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            "BULLETIN" -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    EstateBulletinBoardScreen(
                        isAdmin = true,
                        broadcasts = broadcasts,
                        onPublishBroadcast = onPublishBroadcast,
                        onAcknowledge = onAcknowledgeBroadcast
                    )
                }
            }

            "FINANCE" -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    EstateFeePaymentsScreen(
                        isAdmin = true,
                        residentUnit = "HQ Management",
                        allResidents = residents,
                        invoices = invoices,
                        onPayInvoice = onPayInvoice,
                        onCreateInvoice = onCreateInvoice
                    )
                }
            }

            "MEETINGS" -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    CommunityMeetingsScreen(
                        isAdmin = true,
                        currentUserName = "Estate Executive Chairman",
                        meetings = meetings,
                        onScheduleMeeting = onScheduleMeeting,
                        onPostContribution = onPostMeetingContribution,
                        onVotePoll = onVoteMeetingPoll
                    )
                }
            }

            "COMPLAINTS" -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    ResidentComplaintsScreen(
                        isAdmin = true,
                        residentUnit = "HQ Directorate",
                        complaints = complaints,
                        onSubmitComplaint = onSubmitComplaint,
                        onResolveComplaint = onResolveComplaint
                    )
                }
            }

            "MESSAGES" -> {
                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    EstateMessagesScreen(
                        currentUserId = "admin_director",
                        currentUserName = "Chief Security Officer (HQ)",
                        currentUserRole = "ADMIN",
                        messages = messages,
                        onSendMessage = onSendMessage,
                        onStartCall = { name, role, unit, isVideo ->
                            onStartCall(name, role, unit, isVideo, "Admin Central Command")
                        }
                    )
                }
            }
        }
    }

    // --- Modal: Add Security Gate ---
    if (showAddGateDialog) {
        AddGateDialog(
            onDismiss = { showAddGateDialog = false },
            onConfirm = { name, code, estate, location, hours, isPrimary ->
                onCreateGate(name, code, estate, location, hours, isPrimary)
                showAddGateDialog = false
            }
        )
    }

    // --- Modal: Add Resident Access ---
    if (showAddResidentDialog) {
        AddResidentDialog(
            gates = securityGates,
            onDismiss = { showAddResidentDialog = false },
            onConfirm = { fullName, unitNumber, estate, primaryGate, phone, email, passcode, vehicles ->
                onCreateResident(fullName, unitNumber, estate, primaryGate, phone, email, passcode, vehicles)
                showAddResidentDialog = false
            }
        )
    }

    // --- Modal: Add Guard Access ---
    if (showAddGuardDialog) {
        AddGuardDialog(
            gates = securityGates,
            onDismiss = { showAddGuardDialog = false },
            onConfirm = { fullName, badgeId, gate, shift, phone ->
                onCreateGuard(fullName, badgeId, gate, shift, phone)
                showAddGuardDialog = false
            }
        )
    }

    // Resolve Incident Dialog
    selectedIncidentForResolve?.let { incident ->
        Dialog(onDismissRequest = { selectedIncidentForResolve = null }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "RESOLVE SECURITY INCIDENT",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GushedTextPrimary
                    )
                    Text(
                        text = "${incident.incidentCode} • ${incident.title}",
                        fontSize = 12.sp,
                        color = GushedTextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = resolveNotes,
                        onValueChange = { resolveNotes = it },
                        label = { Text("Resolution Findings & Actions Taken", fontSize = 11.sp) },
                        placeholder = { Text("e.g. Identity verified with host resident; vehicle allowed under warning.", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GushedCobalt,
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedTextColor = GushedTextPrimary,
                            unfocusedTextColor = GushedTextPrimary
                        ),
                        maxLines = 3,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { selectedIncidentForResolve = null },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel", color = GushedTextSecondary)
                        }

                        Button(
                            onClick = {
                                onResolveIncident(incident, resolveNotes)
                                selectedIncidentForResolve = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GushedEmeraldApproved),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Text("Mark Resolved", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// --- Dialog: Add Security Gate ---
@Composable
private fun AddGateDialog(
    onDismiss: () -> Unit,
    onConfirm: (gateName: String, gateCode: String, estateName: String, location: String, operatingHours: String, isPrimary: Boolean) -> Unit
) {
    var gateName by remember { mutableStateOf("Gate 5 - Pinnock East Perimeter Gate") }
    var gateCode by remember { mutableStateOf("PBE-GT05") }
    var estateName by remember { mutableStateOf("Pinnock Beach Estate") }
    var location by remember { mutableStateOf("East Perimeter Access Road, Lekki") }
    var operatingHours by remember { mutableStateOf("24 Hours / 7 Days") }
    var isPrimary by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Security Gate Post",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GushedTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = gateName,
                    onValueChange = { gateName = it },
                    label = { Text("Gate Name (e.g. Pinnock Estate Main Gate)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gateCode,
                        onValueChange = { gateCode = it },
                        label = { Text("Gate Code") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = estateName,
                        onValueChange = { estateName = it },
                        label = { Text("Estate Name") },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Physical Location / Landmark") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = operatingHours,
                    onValueChange = { operatingHours = it },
                    label = { Text("Operating Schedule") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { isPrimary = !isPrimary }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = isPrimary,
                        onCheckedChange = { isPrimary = it },
                        colors = CheckboxDefaults.colors(checkedColor = GushedCobalt)
                    )
                    Text("Set as Primary Access Gate", fontSize = 12.sp, color = GushedTextPrimary)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (gateName.isNotBlank()) {
                            onConfirm(gateName, gateCode, estateName, location, operatingHours, isPrimary)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Provision Security Gate Post", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- Dialog: Add Resident Access ---
@Composable
private fun AddResidentDialog(
    gates: List<SecurityGateEntity>,
    onDismiss: () -> Unit,
    onConfirm: (fullName: String, unitNumber: String, estate: String, primaryGate: String, phone: String, email: String, passcode: String, vehicles: String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var unitNumber by remember { mutableStateOf("Villa 22, Palm Grove") }
    var estateName by remember { mutableStateOf("Pinnock Beach Estate") }
    var selectedGate by remember { mutableStateOf(gates.firstOrNull()?.gateName ?: "Gate 1 - Pinnock Beach Estate Main Gate") }
    var phone by remember { mutableStateOf("+234 80") }
    var email by remember { mutableStateOf("") }
    var passcode by remember { mutableStateOf("1234") }
    var vehicles by remember { mutableStateOf("") }

    var gateDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Resident Access",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GushedTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Resident Full Name") },
                    placeholder = { Text("e.g. Dr. Olumide Johnson") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = unitNumber,
                        onValueChange = { unitNumber = it },
                        label = { Text("Property Unit / Villa") },
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = estateName,
                        onValueChange = { estateName = it },
                        label = { Text("Estate Name") },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gate Selector Dropdown
                Text("Assigned Primary Gate:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GushedTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                            .clickable { gateDropdownExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = selectedGate, fontSize = 12.sp, color = GushedTextPrimary)
                        Icon(Icons.Default.LocationOn, contentDescription = "Gate", tint = GushedCobalt, modifier = Modifier.size(18.dp))
                    }

                    DropdownMenu(
                        expanded = gateDropdownExpanded,
                        onDismissRequest = { gateDropdownExpanded = false }
                    ) {
                        gates.forEach { gate ->
                            DropdownMenuItem(
                                text = { Text(gate.gateName, fontSize = 12.sp) },
                                onClick = {
                                    selectedGate = gate.gateName
                                    gateDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Resident Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = vehicles,
                    onValueChange = { vehicles = it },
                    label = { Text("Registered Vehicle Plates (comma separated)") },
                    placeholder = { Text("e.g. LAG-102-AA, KJA-881-BC") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (fullName.isNotBlank()) {
                            onConfirm(fullName, unitNumber, estateName, selectedGate, phone, email, passcode, vehicles)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Provision Resident Account", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- Dialog: Add Guard Access ---
@Composable
private fun AddGuardDialog(
    gates: List<SecurityGateEntity>,
    onDismiss: () -> Unit,
    onConfirm: (fullName: String, badgeId: String, gate: String, shift: String, phone: String) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    val count = remember { (1000..9999).random() }
    var badgeId by remember { mutableStateOf("GSD-$count") }
    var selectedGate by remember { mutableStateOf(gates.firstOrNull()?.gateName ?: "Gate 1 - Pinnock Beach Estate Main Gate") }
    var shift by remember { mutableStateOf("Day Shift (06:00 - 18:00)") }
    var phone by remember { mutableStateOf("+234 80") }

    var gateDropdownExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Add Security Guard Access",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GushedTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Guard Full Name") },
                    placeholder = { Text("e.g. Officer Daniel Nnamdi") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = badgeId,
                    onValueChange = { badgeId = it },
                    label = { Text("Security Badge ID") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Assigned Gate Post:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GushedTextSecondary)
                Spacer(modifier = Modifier.height(4.dp))
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                            .clickable { gateDropdownExpanded = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = selectedGate, fontSize = 12.sp, color = GushedTextPrimary)
                        Icon(Icons.Default.DoorSliding, contentDescription = "Gate", tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                    }

                    DropdownMenu(
                        expanded = gateDropdownExpanded,
                        onDismissRequest = { gateDropdownExpanded = false }
                    ) {
                        gates.forEach { gate ->
                            DropdownMenuItem(
                                text = { Text(gate.gateName, fontSize = 12.sp) },
                                onClick = {
                                    selectedGate = gate.gateName
                                    gateDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = shift,
                    onValueChange = { shift = it },
                    label = { Text("Assigned Shift") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Guard Contact Phone") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (fullName.isNotBlank()) {
                            onConfirm(fullName, badgeId, selectedGate, shift, phone)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Provision Guard Profile", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun EmptyPlaceholder(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.5f))
            .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(18.dp))
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = GushedTextSecondary)
    }
}
