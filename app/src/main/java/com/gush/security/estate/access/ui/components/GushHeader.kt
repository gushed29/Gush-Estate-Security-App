package com.gush.security.estate.access.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gush.security.estate.access.data.local.entities.GuardAccountEntity
import com.gush.security.estate.access.data.local.entities.ResidentAccountEntity
import com.gush.security.estate.access.data.local.entities.SecurityGateEntity
import com.gush.security.estate.access.security.SecurityUtils
import com.gush.security.estate.access.ui.theme.FrostedGlassBorder
import com.gush.security.estate.access.ui.theme.FrostedGlassBorderMuted
import com.gush.security.estate.access.ui.theme.FrostedGlassSurface
import com.gush.security.estate.access.ui.theme.FrostedGlassSurfaceElevated
import com.gush.security.estate.access.ui.theme.GushedCobalt
import com.gush.security.estate.access.ui.theme.GushedEmeraldApproved
import com.gush.security.estate.access.ui.theme.GushedEmeraldDark
import com.gush.security.estate.access.ui.theme.GushedTextMuted
import com.gush.security.estate.access.ui.theme.GushedTextPrimary
import com.gush.security.estate.access.ui.theme.GushedTextSecondary
import com.gush.security.estate.access.ui.viewmodel.CurrentPortal
import kotlinx.coroutines.delay

@Composable
fun GushHeader(
    currentPortal: CurrentPortal,
    selectedGate: String,
    activeResident: ResidentAccountEntity?,
    activeGuard: GuardAccountEntity?,
    availableGates: List<SecurityGateEntity>,
    onGateSelected: (String) -> Unit,
    onLogoutToSelection: () -> Unit,
    onBackFromVisitorPass: () -> Unit,
    bannerMessage: String?,
    onDismissBanner: () -> Unit
) {
    var gateMenuExpanded by remember { mutableStateOf(false) }
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMs = System.currentTimeMillis()
            delay(1000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(FrostedGlassSurface)
            .border(1.dp, FrostedGlassBorder)
            .shadow(4.dp)
    ) {
        // Banner notification if active
        AnimatedVisibility(visible = bannerMessage != null) {
            bannerMessage?.let { msg ->
                val isError = msg.contains("DENIED") || msg.contains("REVOKED") || msg.contains("⚠️")
                val isSuccess = msg.contains("✓") || msg.contains("APPROVED") || msg.contains("GENERATED")
                val isEmergency = msg.contains("🚨")

                val bg = when {
                    isEmergency -> Color(0xFFDC2626)
                    isError -> Color(0xFFD97706)
                    isSuccess -> Color(0xFF059669)
                    else -> GushedCobalt
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bg)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = msg,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = onDismissBanner,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Top Brand & Identity Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand identity with Frosted Glass Badge & Icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentPortal == CurrentPortal.VisitorPassPortal) {
                    IconButton(
                        onClick = onBackFromVisitorPass,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.8f))
                            .border(1.dp, FrostedGlassBorderMuted, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = GushedCobalt,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.6f))
                            .border(1.dp, FrostedGlassBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(GushedCobalt),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Gush Security Shield",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                }

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "GUSH SECURITY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.4.sp,
                            color = GushedCobalt
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(GushedEmeraldDark)
                                .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(12.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "ONLINE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushedEmeraldApproved
                            )
                        }
                    }

                    Text(
                        text = when (currentPortal) {
                            CurrentPortal.PortalSelection -> "Estate Security Suite"
                            CurrentPortal.AdminPortal -> "Admin Directorate Command"
                            CurrentPortal.ResidentPortal -> "${activeResident?.fullName ?: "Resident"} • ${activeResident?.unitNumber ?: "Unit"}"
                            CurrentPortal.GuardPortal -> "Gatehouse Terminal • ${activeGuard?.fullName ?: "Duty Guard"}"
                            CurrentPortal.VisitorPassPortal -> "Digital Gate Pass & QR Code"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = GushedTextPrimary
                    )
                }
            }

            // Right side Controls: Gate Selector / Switch Role / Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (currentPortal == CurrentPortal.GuardPortal) {
                    // Gate Switcher for Guard
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.8f))
                                .border(1.dp, FrostedGlassBorderMuted, RoundedCornerShape(16.dp))
                                .clickable { gateMenuExpanded = true }
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(GushedEmeraldApproved)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = selectedGate.take(14) + if (selectedGate.length > 14) "…" else "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushedCobalt
                            )
                        }

                        DropdownMenu(
                            expanded = gateMenuExpanded,
                            onDismissRequest = { gateMenuExpanded = false },
                            modifier = Modifier
                                .background(FrostedGlassSurfaceElevated)
                                .border(1.dp, FrostedGlassBorderMuted, RoundedCornerShape(12.dp))
                        ) {
                            availableGates.forEach { gate ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(
                                                text = gate.gateName,
                                                color = if (gate.gateName == selectedGate) GushedCobalt else GushedTextPrimary,
                                                fontWeight = if (gate.gateName == selectedGate) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "${gate.gateCode} • ${gate.operatingHours}",
                                                fontSize = 10.sp,
                                                color = GushedTextSecondary
                                            )
                                        }
                                    },
                                    onClick = {
                                        onGateSelected(gate.gateName)
                                        gateMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Switch Portal Button if authenticated
                if (currentPortal != CurrentPortal.PortalSelection) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.7f))
                            .border(1.dp, FrostedGlassBorderMuted, RoundedCornerShape(14.dp))
                            .clickable { onLogoutToSelection() }
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Switch Portal",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Switch",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GushedHeader(
    currentPortal: CurrentPortal,
    selectedGate: String,
    activeResident: ResidentAccountEntity?,
    activeGuard: GuardAccountEntity?,
    availableGates: List<SecurityGateEntity>,
    onGateSelected: (String) -> Unit,
    onLogoutToSelection: () -> Unit,
    onBackFromVisitorPass: () -> Unit,
    bannerMessage: String?,
    onDismissBanner: () -> Unit
) {
    GushHeader(
        currentPortal = currentPortal,
        selectedGate = selectedGate,
        activeResident = activeResident,
        activeGuard = activeGuard,
        availableGates = availableGates,
        onGateSelected = onGateSelected,
        onLogoutToSelection = onLogoutToSelection,
        onBackFromVisitorPass = onBackFromVisitorPass,
        bannerMessage = bannerMessage,
        onDismissBanner = onDismissBanner
    )
}
