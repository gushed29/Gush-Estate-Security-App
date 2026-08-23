package com.example.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.VisitorPassEntity
import com.example.security.SecurityUtils
import com.example.ui.theme.GushedBorder
import com.example.ui.theme.GushedCobalt
import com.example.ui.theme.GushedCrimsonDark
import com.example.ui.theme.GushedCrimsonDenied
import com.example.ui.theme.GushedCyanAccent
import com.example.ui.theme.GushedEmeraldApproved
import com.example.ui.theme.GushedEmeraldDark
import com.example.ui.theme.GushedPrimaryNavy
import com.example.ui.theme.GushedSurfaceContainer
import com.example.ui.theme.GushedSurfaceDark
import com.example.ui.theme.GushedSurfaceElevated
import com.example.ui.theme.GushedTextMuted
import com.example.ui.theme.GushedTextPrimary
import com.example.ui.theme.GushedTextSecondary

@Composable
fun VisitorPassViewScreen(
    pass: VisitorPassEntity?,
    allPasses: List<VisitorPassEntity>,
    onSelectPass: (VisitorPassEntity) -> Unit,
    onBack: () -> Unit
) {
    var copiedFeedback by remember { mutableStateOf(false) }

    val activePass = pass ?: allPasses.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top selector bar if multiple passes
        if (allPasses.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                allPasses.take(4).forEach { p ->
                    val isSelected = p.id == activePass?.id
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color.White else Color.White.copy(alpha = 0.5f))
                            .border(
                                1.dp,
                                if (isSelected) GushedCobalt else Color.White.copy(alpha = 0.85f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { onSelectPass(p) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = p.visitorName.split(" ").firstOrNull() ?: p.visitorName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) GushedCobalt else GushedTextSecondary
                        )
                    }
                }
            }
        }

        if (activePass == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No visitor pass selected. Issue a pass from the Resident Portal.",
                    fontSize = 13.sp,
                    color = GushedTextSecondary
                )
            }
            return
        }

        val isExpired = SecurityUtils.isExpired(activePass.validUntilEpoch)
        val isRevoked = activePass.isRevoked

        // Pass Presentation Card (White-labeled Gushed Systems Digital Pass)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f)),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                when {
                    isRevoked -> Color(0xFFDC2626)
                    isExpired -> Color(0xFFCBD5E1)
                    else -> Color.White.copy(alpha = 0.95f)
                }
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Brand on Pass
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Gushed Shield",
                            tint = GushedCobalt,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GUSHED SECURE PASS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = GushedCobalt
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                when {
                                    isRevoked -> Color(0xFFFEF2F2)
                                    isExpired -> Color(0xFFF1F5F9)
                                    else -> Color(0xFFECFDF5)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = when {
                                isRevoked -> "REVOKED"
                                isExpired -> "EXPIRED"
                                else -> "ACTIVE PASS"
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isRevoked -> Color(0xFFDC2626)
                                isExpired -> GushedTextMuted
                                else -> Color(0xFF059669)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom High-Tech QR Code Canvas Simulation
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val gridSize = 9
                        val cellSize = size.width / gridSize

                        // Generate deterministic geometric matrix from pass qrToken hash
                        val seed = activePass.qrToken.hashCode()
                        for (row in 0 until gridSize) {
                            for (col in 0 until gridSize) {
                                // Keep corner finder patterns solid
                                val isCorner = (row < 3 && col < 3) ||
                                        (row < 3 && col >= gridSize - 3) ||
                                        (row >= gridSize - 3 && col < 3)

                                val isFilled = if (isCorner) {
                                    (row == 0 || row == 2 || col == 0 || col == 2) ||
                                            (row == 0 || row == 2 || col == gridSize - 1 || col == gridSize - 3) ||
                                            (row == gridSize - 1 || row == gridSize - 3 || col == 0 || col == 2) ||
                                            (row == 1 && col == 1) ||
                                            (row == 1 && col == gridSize - 2) ||
                                            (row == gridSize - 2 && col == 1)
                                } else {
                                    val bit = (seed shr (row * 3 + col)) and 1
                                    bit == 1 || (row + col) % 3 == 0
                                }

                                if (isFilled) {
                                    drawRect(
                                        color = Color(0xFF0F172A),
                                        topLeft = Offset(col * cellSize, row * cellSize),
                                        size = Size(cellSize - 1f, cellSize - 1f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Big 6-Digit PIN Display
                Text(
                    text = "6-DIGIT GATE PIN CODE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GushedTextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = activePass.pinCode.chunked(3).joinToString(" "),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp,
                        color = GushedCobalt
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "TOKEN: ${activePass.qrToken}",
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = GushedTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Detail Card on Pass
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Visitor", tint = GushedCobalt, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Visitor: ${activePass.visitorName}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Home, contentDescription = "Host", tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Host: ${activePass.hostResidentName} (${activePass.propertyUnit})", fontSize = 11.sp, color = GushedTextSecondary)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Schedule, contentDescription = "Validity", tint = GushedTextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Valid Until: ${SecurityUtils.formatTimestamp(activePass.validUntilEpoch)}", fontSize = 11.sp, color = GushedTextSecondary)
                        }

                        if (activePass.vehiclePlate.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = "Car", tint = GushedTextMuted, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Vehicle: ${activePass.vehiclePlate} (${activePass.vehicleMakeModel})", fontSize = 11.sp, color = GushedTextSecondary)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Gate", tint = GushedCobalt, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Allowed Gate: ${activePass.allowedGate}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = GushedCobalt)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Present this digital pass or 6-digit PIN at the gatehouse officer on arrival.",
                    fontSize = 11.sp,
                    color = GushedTextMuted,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
