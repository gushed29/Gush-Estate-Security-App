package com.gush.security.estate.access.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gush.security.estate.access.data.local.entities.GuardAccountEntity
import com.gush.security.estate.access.data.local.entities.ResidentAccountEntity
import com.gush.security.estate.access.data.local.entities.SecurityGateEntity
import com.gush.security.estate.access.ui.theme.FrostedGlassBorder
import com.gush.security.estate.access.ui.theme.FrostedGlassBorderMuted
import com.gush.security.estate.access.ui.theme.FrostedGlassSurface
import com.gush.security.estate.access.ui.theme.FrostedGlassSurfaceElevated
import com.gush.security.estate.access.ui.theme.GushedCobalt
import com.gush.security.estate.access.ui.theme.GushedEmeraldApproved
import com.gush.security.estate.access.ui.theme.GushedEmeraldDark
import com.gush.security.estate.access.ui.theme.GushedIndigoStaff
import com.gush.security.estate.access.ui.theme.GushedTextMuted
import com.gush.security.estate.access.ui.theme.GushedTextPrimary
import com.gush.security.estate.access.ui.theme.GushedTextSecondary
enum class UserRole {
    ADMIN,
    RESIDENT,
    GUARD
}

@Composable
fun RoleSelectionScreen(
    residents: List<ResidentAccountEntity>,
    guards: List<GuardAccountEntity>,
    gates: List<SecurityGateEntity>,
    onLoginAdmin: () -> Unit,
    onLoginResident: (ResidentAccountEntity) -> Unit,
    onLoginGuard: (GuardAccountEntity, String) -> Unit
) {
    var activeModalRole by remember { mutableStateOf<UserRole?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Brand Hero Badge
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.9f),
                            Color.White.copy(alpha = 0.5f)
                        )
                    )
                )
                .border(1.5.dp, Color.White, CircleShape)
                .shadow(8.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GushedCobalt, Color(0xFF3730A3))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Gush Security Shield",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "GUSH SECURITY",
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
            color = GushedCobalt
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Estate Access Control & Security",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = GushedTextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Select your role to access your dedicated security operations terminal",
            fontSize = 13.sp,
            color = GushedTextSecondary,
            modifier = Modifier.padding(horizontal = 16.dp),
            lineHeight = 18.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Role Card 1: Admin
        RoleSelectionCard(
            title = "Estate Admin & Command",
            subtitle = "Security Directorate & Oversight",
            description = "Overview all estate activity, create & manage security gate posts, provision resident access, manage guard accounts, and inspect cryptographic audit logs.",
            icon = Icons.Default.AdminPanelSettings,
            badge = "COMMAND & CONTROL",
            accentColor = Color(0xFF4F46E5),
            buttonLabel = "Sign In as Admin",
            onClick = { activeModalRole = UserRole.ADMIN }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Role Card 2: Resident
        RoleSelectionCard(
            title = "Resident Host Portal",
            subtitle = "Property Owners & Residents",
            description = "Generate visitor access codes (PIN & QR), choose authorized estate entry gates (e.g. Pinnock Beach Estate Main Gate), track arrival alerts, and declare items.",
            icon = Icons.Default.Home,
            badge = "RESIDENT ACCESS",
            accentColor = Color(0xFF059669),
            buttonLabel = "Sign In as Resident",
            onClick = { activeModalRole = UserRole.RESIDENT }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Role Card 3: Security Guard
        RoleSelectionCard(
            title = "Security Gatehouse Terminal",
            subtitle = "Gate Duty & Inspection Officers",
            description = "Stationed at gate posts to verify visitor PIN/QR codes, inspect vehicle plates, check occupants, verify declared items, and log entry/exit clearances.",
            icon = Icons.Default.Security,
            badge = "DUTY OFFICERS",
            accentColor = Color(0xFF2563EB),
            buttonLabel = "Sign In as Guard",
            onClick = { activeModalRole = UserRole.GUARD }
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Estate Protocol Footer
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.5f))
                .border(1.dp, FrostedGlassBorderMuted, RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock",
                tint = GushedEmeraldApproved,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SHA-256 Tamper-Evident Access Chaining Active",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = GushedTextSecondary
            )
        }
    }

    // Role-specific login modal dialogs
    when (activeModalRole) {
        UserRole.ADMIN -> {
            AdminLoginDialog(
                onDismiss = { activeModalRole = null },
                onConfirmLogin = {
                    activeModalRole = null
                    onLoginAdmin()
                }
            )
        }

        UserRole.RESIDENT -> {
            ResidentSelectDialog(
                residents = residents,
                onDismiss = { activeModalRole = null },
                onSelectResident = { res ->
                    activeModalRole = null
                    onLoginResident(res)
                }
            )
        }

        UserRole.GUARD -> {
            GuardSelectDialog(
                guards = guards,
                gates = gates,
                onDismiss = { activeModalRole = null },
                onSelectGuard = { guard, gate ->
                    activeModalRole = null
                    onLoginGuard(guard, gate)
                }
            )
        }

        null -> {}
    }
}

@Composable
private fun RoleSelectionCard(
    title: String,
    subtitle: String,
    description: String,
    icon: ImageVector,
    badge: String,
    accentColor: Color,
    buttonLabel: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color.White.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(accentColor.copy(alpha = 0.12f))
                            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = accentColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextPrimary
                        )
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = GushedTextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = description,
                fontSize = 12.sp,
                color = GushedTextSecondary,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = buttonLabel,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Enter",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// --- Admin Login Dialog ---
@Composable
fun AdminLoginDialog(
    onDismiss: () -> Unit,
    onConfirmLogin: () -> Unit
) {
    var adminPin by remember { mutableStateOf("1234") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = GushedCobalt,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin Directorate Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Sign in with Admin PIN to access full estate security telemetry, gate management, and resident access controls.",
                    fontSize = 12.sp,
                    color = GushedTextSecondary,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = adminPin,
                    onValueChange = { adminPin = it },
                    label = { Text("Security Officer PIN") },
                    leadingIcon = {
                        Icon(Icons.Default.Key, contentDescription = "PIN", tint = GushedCobalt)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GushedCobalt,
                        unfocusedBorderColor = FrostedGlassBorderMuted,
                        focusedContainerColor = Color(0xFFF8FAFC),
                        unfocusedContainerColor = Color(0xFFF8FAFC)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onConfirmLogin,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Authenticate as Administrator", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- Resident Select / Login Dialog ---
@Composable
fun ResidentSelectDialog(
    residents: List<ResidentAccountEntity>,
    onDismiss: () -> Unit,
    onSelectResident: (ResidentAccountEntity) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = "Resident",
                            tint = Color(0xFF059669),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Select Resident Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Select your registered residence profile to generate visitor codes and manage host activities:",
                    fontSize = 12.sp,
                    color = GushedTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(residents) { res ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onSelectResident(res) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFECFDF5)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = "Resident",
                                            tint = Color(0xFF059669),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = res.fullName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GushedTextPrimary
                                        )
                                        Text(
                                            text = "${res.unitNumber} • ${res.primaryGate}",
                                            fontSize = 11.sp,
                                            color = GushedTextSecondary
                                        )
                                    }
                                }

                                Icon(
                                    Icons.Default.ArrowForward,
                                    contentDescription = "Select",
                                    tint = Color(0xFF059669),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- Guard Select / Login Dialog ---
@Composable
fun GuardSelectDialog(
    guards: List<GuardAccountEntity>,
    gates: List<SecurityGateEntity>,
    onDismiss: () -> Unit,
    onSelectGuard: (GuardAccountEntity, String) -> Unit
) {
    var selectedGuard by remember { mutableStateOf(guards.firstOrNull()) }
    var selectedGateName by remember {
        mutableStateOf(gates.firstOrNull()?.gateName ?: "Gate 1 - Pinnock Beach Estate Main Gate")
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.95f),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Security,
                            contentDescription = "Guard",
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guard Post Station Login",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextPrimary
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Select Duty Gatehouse Post:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GushedTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Gate Selector Chips
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    gates.forEach { gate ->
                        val isSelected = gate.gateName == selectedGateName
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedGateName = gate.gateName }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = "Gate",
                                    tint = if (isSelected) Color(0xFF2563EB) else GushedTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = gate.gateName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color(0xFF1E40AF) else GushedTextPrimary
                                    )
                                    Text(
                                        text = "${gate.gateCode} • ${gate.operatingHours}",
                                        fontSize = 10.sp,
                                        color = GushedTextSecondary
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Select Officer Profile on Duty:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GushedTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    guards.take(3).forEach { guard ->
                        val isSelected = guard.id == selectedGuard?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFFEEF2FF) else Color(0xFFF8FAFC))
                                .border(
                                    1.dp,
                                    if (isSelected) GushedCobalt else Color(0xFFE2E8F0),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedGuard = guard }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Badge,
                                    contentDescription = "Badge",
                                    tint = if (isSelected) GushedCobalt else GushedTextMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = guard.fullName,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) GushedCobalt else GushedTextPrimary
                                    )
                                    Text(
                                        text = "Badge: ${guard.badgeId} • ${guard.shift}",
                                        fontSize = 10.sp,
                                        color = GushedTextSecondary
                                    )
                                }
                            }
                            if (isSelected) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = "Selected",
                                    tint = GushedCobalt,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val g = selectedGuard ?: guards.first()
                        onSelectGuard(g, selectedGateName)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Open Gatehouse Terminal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
