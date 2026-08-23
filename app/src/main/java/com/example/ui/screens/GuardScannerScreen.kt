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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.entities.DeclaredItemEntity
import com.example.data.local.entities.PassStatus
import com.example.data.local.entities.VisitorPassEntity
import com.example.data.repository.VerificationResult
import com.example.security.SecurityUtils
import com.example.ui.theme.GushedAmberDark
import com.example.ui.theme.GushedAmberWarning
import com.example.ui.theme.GushedBorder
import com.example.ui.theme.GushedBorderBright
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
fun GuardScannerScreen(
    scannerInput: String,
    verificationResult: VerificationResult,
    selectedGate: String,
    onInputChange: (String) -> Unit,
    onAppendKeypad: (Char) -> Unit,
    onBackspaceKeypad: () -> Unit,
    onClearInput: () -> Unit,
    onVerifyCode: (String) -> Unit,
    onApproveEntry: (pass: VisitorPassEntity, actualOccupants: Int, notes: String, signature: String?) -> Unit,
    onRecordExit: (pass: VisitorPassEntity, items: List<DeclaredItemEntity>, notes: String, signature: String?) -> Unit,
    onDenyAccess: (String) -> Unit,
    onOpenEmergencyDialog: () -> Unit,
    onOpenIncidentDialog: () -> Unit,
    onStartCall: (receiverName: String, receiverRole: String, receiverUnit: String, isVideo: Boolean, gatePost: String) -> Unit
) {
    var showItemInspector by remember { mutableStateOf(false) }
    var itemInspectorIsExit by remember { mutableStateOf(false) }
    var currentInspectingPass by remember { mutableStateOf<VisitorPassEntity?>(null) }
    var currentInspectingItems by remember { mutableStateOf<List<DeclaredItemEntity>>(emptyList()) }

    var actualOccupantsCount by remember { mutableIntStateOf(1) }
    var entryNotes by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Scanner Input Bar (Frosted Glass Panel)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.65f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.8f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEEF2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan",
                                tint = GushedCobalt,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "FAST CREDENTIAL VERIFICATION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.2.sp,
                            color = GushedCobalt
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.7f))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = selectedGate,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GushedTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input field + Clear & Verify buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = scannerInput,
                        onValueChange = { onInputChange(it) },
                        placeholder = {
                            Text(
                                "Enter PIN / QR Token / Plate #",
                                fontSize = 13.sp,
                                color = GushedTextMuted
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GushedCobalt,
                            unfocusedBorderColor = Color(0xFFCBD5E1),
                            focusedTextColor = GushedTextPrimary,
                            unfocusedTextColor = GushedTextPrimary,
                            focusedContainerColor = Color.White.copy(alpha = 0.8f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.6f)
                        ),
                        singleLine = true,
                        trailingIcon = {
                            if (scannerInput.isNotEmpty()) {
                                IconButton(onClick = onClearInput) {
                                    Icon(
                                        imageVector = Icons.Default.Backspace,
                                        contentDescription = "Clear",
                                        tint = GushedTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    )

                    Button(
                        onClick = { onVerifyCode(scannerInput) },
                        colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(54.dp)
                    ) {
                        Text(
                            text = "VERIFY",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Demo Scenario Chips (Frosted Pills)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "849201" to "David (VIP)",
                        "319458" to "Tunde (AC Tech)",
                        "572190" to "Samuel (DHL)",
                        "999999" to "Test Invalid"
                    ).forEach { (code, label) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.5f))
                                .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                                .clickable {
                                    onInputChange(code)
                                    onVerifyCode(code)
                                }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GushedTextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Verification Result Section
        when (verificationResult) {
            is VerificationResult.Idle -> {
                // Keypad & Waiting state (Frosted Glass Container)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.55f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "GATEHOUSE NUMERIC KEYPAD",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = GushedTextMuted
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        // 3x4 Numeric Keypad
                        val keys = listOf(
                            listOf('1', '2', '3'),
                            listOf('4', '5', '6'),
                            listOf('7', '8', '9'),
                            listOf('C', '0', '⌫')
                        )

                        keys.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                row.forEach { key ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(52.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                when (key) {
                                                    'C' -> Color(0xFFFEE2E2)
                                                    '⌫' -> Color.White.copy(alpha = 0.8f)
                                                    else -> Color.White.copy(alpha = 0.75f)
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                when (key) {
                                                    'C' -> Color(0xFFFECACA)
                                                    else -> Color.White.copy(alpha = 0.9f)
                                                },
                                                RoundedCornerShape(14.dp)
                                            )
                                            .clickable {
                                                when (key) {
                                                    'C' -> onClearInput()
                                                    '⌫' -> onBackspaceKeypad()
                                                    else -> onAppendKeypad(key)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$key",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (key) {
                                                'C' -> GushedCrimsonDenied
                                                else -> GushedTextPrimary
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            is VerificationResult.Approved -> {
                val pass = verificationResult.pass
                val items = verificationResult.declaredItems
                val isInside = pass.status == PassStatus.ACTIVE_INSIDE.name

                LaunchedEffect(pass.id) {
                    actualOccupantsCount = pass.expectedOccupants
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isInside) Color(0xFFFEF3C7).copy(alpha = 0.85f) else Color(0xFFD1FAE5).copy(alpha = 0.85f)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.5.dp,
                        if (isInside) Color(0xFFFBBF24) else Color(0xFF34D399)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        // Big Status Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(if (isInside) GushedAmberWarning else GushedEmeraldApproved),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Approved",
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = if (isInside) "ACTIVE INSIDE • READY FOR EXIT" else "APPROVED FOR ENTRY",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp,
                                        color = if (isInside) Color(0xFF92400E) else Color(0xFF065F46)
                                    )
                                    Text(
                                        text = "PIN: ${pass.pinCode} • Token: ${pass.qrToken}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GushedTextSecondary
                                    )
                                }
                            }

                            // Pass Type Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GushedCobalt)
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = pass.visitorType,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Visitor and Resident Information Card (Frosted inner card)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Visitor Name & Phone
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = "Visitor",
                                        tint = GushedCobalt,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = pass.visitorName,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GushedTextPrimary
                                    )
                                    if (pass.phone.isNotEmpty()) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "(${pass.phone})",
                                            fontSize = 12.sp,
                                            color = GushedTextSecondary
                                        )
                                    }
                                }

                                // Resident Host & Property
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Home,
                                        contentDescription = "Host",
                                        tint = GushedEmeraldApproved,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Host: ${pass.hostResidentName}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = GushedTextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${pass.propertyUnit}",
                                        fontSize = 12.sp,
                                        color = GushedTextSecondary
                                    )
                                }

                                // Purpose & Gate
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.LocationOn,
                                        contentDescription = "Gate",
                                        tint = GushedTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Purpose: ${pass.visitPurpose}",
                                        fontSize = 12.sp,
                                        color = GushedTextSecondary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "[Gate: ${pass.allowedGate}]",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GushedCobalt
                                    )
                                }

                                // Validity Window
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.Schedule,
                                        contentDescription = "Time",
                                        tint = GushedTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Valid Until: ${SecurityUtils.formatTimestamp(pass.validUntilEpoch)}",
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = GushedTextSecondary
                                    )
                                }

                                // 2-Way Call / Intercom to Host Resident
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Intercom Host:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GushedTextSecondary)
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = { onStartCall(pass.hostResidentName, "RESIDENT", pass.propertyUnit, false, selectedGate) },
                                            colors = ButtonDefaults.buttonColors(containerColor = GushedEmeraldApproved),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Audio Call", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = { onStartCall(pass.hostResidentName, "RESIDENT", pass.propertyUnit, true, selectedGate) },
                                            colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Video Intercom", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Vehicle Check Section
                        if (pass.vehiclePlate.isNotEmpty() || pass.vehicleMakeModel.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.85f))
                                    .border(1.dp, Color.White, RoundedCornerShape(14.dp))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.DirectionsCar,
                                        contentDescription = "Vehicle",
                                        tint = GushedCobalt,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "PLATE: ${pass.vehiclePlate}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Black,
                                            fontFamily = FontFamily.Monospace,
                                            color = GushedTextPrimary
                                        )
                                        Text(
                                            text = "${pass.vehicleColor} ${pass.vehicleMakeModel} • Driver: ${if (pass.driverName.isNotEmpty()) pass.driverName else pass.visitorName}",
                                            fontSize = 11.sp,
                                            color = GushedTextSecondary
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFD1FAE5))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "VERIFIED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF065F46)
                                    )
                                }
                            }
                        }

                        // Occupant Control & Declared Items
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Occupant Counter with Discrepancy Alert
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.85f))
                                    .border(
                                        1.dp,
                                        if (actualOccupantsCount != pass.expectedOccupants) GushedAmberWarning else Color.White,
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "OCCUPANTS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GushedTextSecondary
                                    )
                                    Text(
                                        text = "Exp: ${pass.expectedOccupants}",
                                        fontSize = 10.sp,
                                        color = GushedTextMuted
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconButton(
                                        onClick = { if (actualOccupantsCount > 1) actualOccupantsCount-- },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                                    }
                                    Text(
                                        text = "$actualOccupantsCount",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (actualOccupantsCount != pass.expectedOccupants) GushedAmberWarning else GushedCobalt
                                    )
                                    IconButton(
                                        onClick = { if (actualOccupantsCount < 12) actualOccupantsCount++ },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                                    }
                                }
                            }

                            // Declared Items Quick Box
                            Column(
                                modifier = Modifier
                                    .weight(1.2f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color.White.copy(alpha = 0.85f))
                                    .border(1.dp, Color.White, RoundedCornerShape(14.dp))
                                    .clickable {
                                        currentInspectingPass = pass
                                        currentInspectingItems = items
                                        itemInspectorIsExit = isInside
                                        showItemInspector = true
                                    }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "PROPERTY CUSTODY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GushedTextSecondary
                                    )
                                    Icon(
                                        Icons.Default.Inventory,
                                        contentDescription = "Items",
                                        tint = GushedCobalt,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (items.isEmpty()) "0 Items Declared" else "${items.size} Declared (Inspect)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (items.isNotEmpty()) GushedCobalt else GushedTextMuted
                                )
                            }
                        }

                        // Guard Notes Field
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = entryNotes,
                            onValueChange = { entryNotes = it },
                            placeholder = { Text("Guard notes / Physical verification details", fontSize = 11.sp, color = GushedTextMuted) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GushedCobalt,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedTextColor = GushedTextPrimary,
                                unfocusedTextColor = GushedTextPrimary,
                                focusedContainerColor = Color.White.copy(alpha = 0.85f),
                                unfocusedContainerColor = Color.White.copy(alpha = 0.7f)
                            ),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Main Action Buttons
                        if (!isInside) {
                            // Entry Mode Action
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onApproveEntry(pass, actualOccupantsCount, entryNotes, "GUARD_VERIFIED")
                                    },
                                    modifier = Modifier
                                        .weight(2f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GushedEmeraldApproved)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Allow", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "ALLOW ENTRY",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }

                                OutlinedButton(
                                    onClick = { onDenyAccess("Guard manual denial at gate") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GushedCrimsonDenied)
                                ) {
                                    Text("DENY", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            // Exit Mode Action
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        currentInspectingPass = pass
                                        currentInspectingItems = items
                                        itemInspectorIsExit = true
                                        showItemInspector = true
                                    },
                                    modifier = Modifier
                                        .weight(2f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GushedAmberWarning)
                                ) {
                                    Icon(Icons.Default.Inventory, contentDescription = "Exit", modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "INSPECT & RECORD EXIT",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                }

                                OutlinedButton(
                                    onClick = onOpenIncidentDialog,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = GushedAmberWarning)
                                ) {
                                    Text("FLAG", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            is VerificationResult.Denied -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2).copy(alpha = 0.9f)),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFCA5A5)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GushedCrimsonDenied),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Denied",
                                tint = Color.White,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "ACCESS DENIED",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = GushedCrimsonDenied
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = verificationResult.reason,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF991B1B),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = onClearInput,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = GushedTextSecondary)
                            ) {
                                Text("Re-Scan", fontSize = 12.sp)
                            }

                            Button(
                                onClick = onOpenIncidentDialog,
                                modifier = Modifier
                                    .weight(1.5f)
                                    .height(44.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GushedCrimsonDenied)
                            ) {
                                Icon(Icons.Default.ReportProblem, contentDescription = "Incident", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Log Security Incident", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Emergency Gate Override Quick Trigger Bar (Frosted Emergency Glass)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenEmergencyDialog() },
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2).copy(alpha = 0.8f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFECACA)),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFECACA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = "Emergency",
                            tint = GushedCrimsonDenied,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "EMERGENCY VEHICLE OVERRIDE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = GushedCrimsonDenied
                        )
                        Text(
                            text = "Instant 1-tap gate release for Fire, Police, Ambulance",
                            fontSize = 11.sp,
                            color = GushedTextSecondary
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Go",
                    tint = GushedCrimsonDenied,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }

    // Modal Inspection Dialog
    if (showItemInspector && currentInspectingPass != null) {
        ItemInspectionDialog(
            pass = currentInspectingPass!!,
            initialItems = currentInspectingItems,
            isExitMode = itemInspectorIsExit,
            onDismiss = { showItemInspector = false },
            onConfirm = { updatedItems, notes, sig ->
                showItemInspector = false
                if (itemInspectorIsExit) {
                    onRecordExit(currentInspectingPass!!, updatedItems, notes, sig)
                } else {
                    onApproveEntry(currentInspectingPass!!, actualOccupantsCount, notes, sig)
                }
            }
        )
    }
}
