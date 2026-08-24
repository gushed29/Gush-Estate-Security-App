package com.gush.security.estate.access.ui.screens

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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
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
import com.gush.security.estate.access.data.local.entities.PassType
import com.gush.security.estate.access.data.local.entities.SecurityGateEntity
import com.gush.security.estate.access.ui.theme.GushedCobalt
import com.gush.security.estate.access.ui.theme.GushedTextMuted
import com.gush.security.estate.access.ui.theme.GushedTextPrimary
import com.gush.security.estate.access.ui.theme.GushedTextSecondary

@Composable
fun CreatePassDialog(
    residentName: String,
    propertyUnit: String,
    availableGates: List<SecurityGateEntity>,
    onDismiss: () -> Unit,
    onCreatePass: (
        visitorName: String,
        phone: String,
        visitorType: PassType,
        visitPurpose: String,
        expectedOccupants: Int,
        vehiclePlate: String,
        vehicleMakeModel: String,
        vehicleColor: String,
        driverName: String,
        allowedGate: String,
        validDurationHours: Int,
        specialInstructions: String,
        declaredItems: List<Pair<String, String>>
    ) -> Unit
) {
    var visitorType by remember { mutableStateOf(PassType.GUEST) }
    var visitorName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var visitPurpose by remember { mutableStateOf("") }
    var expectedOccupants by remember { mutableIntStateOf(1) }
    var vehiclePlate by remember { mutableStateOf("") }
    var vehicleMakeModel by remember { mutableStateOf("") }
    var vehicleColor by remember { mutableStateOf("") }
    var driverName by remember { mutableStateOf("") }
    var allowedGate by remember {
        mutableStateOf(availableGates.firstOrNull()?.gateName ?: "All Gates")
    }
    var validDurationHours by remember { mutableIntStateOf(4) }
    var specialInstructions by remember { mutableStateOf("") }

    val declaredItems = remember { mutableStateListOf<Pair<String, String>>() }
    var itemNameInput by remember { mutableStateOf("") }
    var itemCategoryInput by remember { mutableStateOf("Electronics") }

    var gateDropdownOpen by remember { mutableStateOf(false) }
    var durationDropdownOpen by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .imePadding()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFEEF2FF)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Badge,
                                contentDescription = "Pass",
                                tint = GushedCobalt,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ISSUE VISITOR ACCESS PASS",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushedTextPrimary
                            )
                            Text(
                                text = "Host: $residentName • $propertyUnit",
                                fontSize = 11.sp,
                                color = GushedTextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Pass Type Selector Tabs
                    item {
                        Text(
                            text = "VISITOR CATEGORY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = GushedTextSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            data class PassTypeItem(
                                val type: PassType,
                                val label: String,
                                val icon: androidx.compose.ui.graphics.vector.ImageVector,
                                val color: Color
                            )
                            val passTypeItems = listOf(
                                PassTypeItem(PassType.GUEST, "Guest", Icons.Default.Person, GushedCobalt),
                                PassTypeItem(PassType.DELIVERY, "Delivery", Icons.Default.LocalShipping, Color(0xFF0369A1)),
                                PassTypeItem(PassType.CONTRACTOR, "Contractor", Icons.Default.Engineering, Color(0xFF7E22CE)),
                                PassTypeItem(PassType.DOMESTIC_STAFF, "Staff", Icons.Default.Shield, Color(0xFF334155))
                            )
                            passTypeItems.forEach { item ->
                                val isSelected = visitorType == item.type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) item.color.copy(alpha = 0.12f) else Color(0xFFF8FAFC))
                                        .border(
                                            1.dp,
                                            if (isSelected) item.color else Color(0xFFE2E8F0),
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { visitorType = item.type }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(
                                            imageVector = item.icon,
                                            contentDescription = item.label,
                                            tint = if (isSelected) item.color else GushedTextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = item.label,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) item.color else GushedTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Visitor Details
                    item {
                        OutlinedTextField(
                            value = visitorName,
                            onValueChange = { visitorName = it },
                            label = { Text("Visitor Full Name *", fontSize = 11.sp) },
                            placeholder = { Text("e.g. David Okonjo or DHL Express", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GushedCobalt,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedTextColor = GushedTextPrimary,
                                unfocusedTextColor = GushedTextPrimary
                            ),
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = phone,
                                onValueChange = { phone = it },
                                label = { Text("Phone Number", fontSize = 11.sp) },
                                placeholder = { Text("+234 80...", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GushedCobalt,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedTextColor = GushedTextPrimary,
                                    unfocusedTextColor = GushedTextPrimary
                                ),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )

                            OutlinedTextField(
                                value = visitPurpose,
                                onValueChange = { visitPurpose = it },
                                label = { Text("Visit Purpose *", fontSize = 11.sp) },
                                placeholder = { Text("e.g. Social, Repair", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GushedCobalt,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedTextColor = GushedTextPrimary,
                                    unfocusedTextColor = GushedTextPrimary
                                ),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )
                        }
                    }

                    // Vehicle & Occupants
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = vehiclePlate,
                                onValueChange = { vehiclePlate = it },
                                label = { Text("Vehicle Plate", fontSize = 11.sp) },
                                placeholder = { Text("LAG-123-XY", fontSize = 11.sp) },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GushedCobalt,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedTextColor = GushedTextPrimary,
                                    unfocusedTextColor = GushedTextPrimary
                                ),
                                singleLine = true,
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )

                            // Occupants Counter
                            Column(modifier = Modifier.weight(0.8f)) {
                                Text(
                                    text = "People Count",
                                    fontSize = 10.sp,
                                    color = GushedTextSecondary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFF8FAFC))
                                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    IconButton(
                                        onClick = { if (expectedOccupants > 1) expectedOccupants-- },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Text("-", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                                    }
                                    Text(
                                        text = "$expectedOccupants",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GushedCobalt
                                    )
                                    IconButton(
                                        onClick = { if (expectedOccupants < 10) expectedOccupants++ },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = GushedTextPrimary)
                                    }
                                }
                            }
                        }
                    }

                    // Gate Restriction & Validity Duration
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Gate (Dynamically loaded from Admin provisioned gates)
                            Box(modifier = Modifier.weight(1.2f)) {
                                OutlinedTextField(
                                    value = allowedGate,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Gate Authorized", fontSize = 11.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { gateDropdownOpen = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GushedCobalt,
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        focusedTextColor = GushedTextPrimary,
                                        unfocusedTextColor = GushedTextPrimary
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                                )
                                DropdownMenu(
                                    expanded = gateDropdownOpen,
                                    onDismissRequest = { gateDropdownOpen = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("All Gates (Unrestricted)", color = GushedTextPrimary) },
                                        onClick = {
                                            allowedGate = "All Gates"
                                            gateDropdownOpen = false
                                        }
                                    )
                                    availableGates.forEach { g ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(g.gateName, color = GushedTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                    Text("${g.gateCode} • ${g.operatingHours}", fontSize = 10.sp, color = GushedTextSecondary)
                                                }
                                            },
                                            onClick = {
                                                allowedGate = g.gateName
                                                gateDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }

                            // Validity Hours
                            Box(modifier = Modifier.weight(0.8f)) {
                                OutlinedTextField(
                                    value = "$validDurationHours Hours",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Validity", fontSize = 11.sp) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { durationDropdownOpen = true },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = GushedCobalt,
                                        unfocusedBorderColor = Color(0xFFE2E8F0),
                                        focusedTextColor = GushedTextPrimary,
                                        unfocusedTextColor = GushedTextPrimary
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                                )
                                DropdownMenu(
                                    expanded = durationDropdownOpen,
                                    onDismissRequest = { durationDropdownOpen = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    listOf(1, 2, 4, 8, 12, 24, 72).forEach { hrs ->
                                        DropdownMenuItem(
                                            text = { Text("$hrs Hours Validity", color = GushedTextPrimary) },
                                            onClick = {
                                                validDurationHours = hrs
                                                durationDropdownOpen = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Declared Items / Tools
                    item {
                        Text(
                            text = "PRE-DECLARED PROPERTY / TOOLS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = GushedTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        declaredItems.forEachIndexed { idx, (item, cat) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$item ($cat)",
                                    fontSize = 12.sp,
                                    color = GushedTextPrimary
                                )
                                IconButton(
                                    onClick = { declaredItems.removeAt(idx) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = itemNameInput,
                                onValueChange = { itemNameInput = it },
                                placeholder = { Text("e.g. Laptop, Toolbox", fontSize = 11.sp, color = GushedTextMuted) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GushedCobalt,
                                    unfocusedBorderColor = Color(0xFFE2E8F0),
                                    focusedTextColor = GushedTextPrimary,
                                    unfocusedTextColor = GushedTextPrimary
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )
                            Button(
                                onClick = {
                                    if (itemNameInput.isNotBlank()) {
                                        declaredItems.add(itemNameInput.trim() to itemCategoryInput)
                                        itemNameInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                        }
                    }

                    // Special Instructions
                    item {
                        OutlinedTextField(
                            value = specialInstructions,
                            onValueChange = { specialInstructions = it },
                            label = { Text("Gatehouse Special Instructions", fontSize = 11.sp) },
                            placeholder = { Text("e.g. Escort to block elevator / Hold package", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GushedCobalt,
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedTextColor = GushedTextPrimary,
                                unfocusedTextColor = GushedTextPrimary
                            ),
                            maxLines = 2,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GushedTextSecondary)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            if (visitorName.isNotBlank()) {
                                onCreatePass(
                                    visitorName.trim(),
                                    phone.trim(),
                                    visitorType,
                                    if (visitPurpose.isNotBlank()) visitPurpose.trim() else "Personal visit",
                                    expectedOccupants,
                                    vehiclePlate.trim(),
                                    vehicleMakeModel.trim(),
                                    vehicleColor.trim(),
                                    driverName.trim(),
                                    allowedGate,
                                    validDurationHours,
                                    specialInstructions.trim(),
                                    declaredItems.toList()
                                )
                                onDismiss()
                            }
                        },
                        enabled = visitorName.isNotBlank(),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                    ) {
                        Text(
                            text = "Generate Pass",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
