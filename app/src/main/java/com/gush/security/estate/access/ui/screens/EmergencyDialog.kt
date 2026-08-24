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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocalPolice
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.gush.security.estate.access.ui.theme.GushedBorder
import com.gush.security.estate.access.ui.theme.GushedCrimsonDark
import com.gush.security.estate.access.ui.theme.GushedCrimsonDenied
import com.gush.security.estate.access.ui.theme.GushedSurfaceContainer
import com.gush.security.estate.access.ui.theme.GushedSurfaceDark
import com.gush.security.estate.access.ui.theme.GushedTextMuted
import com.gush.security.estate.access.ui.theme.GushedTextPrimary
import com.gush.security.estate.access.ui.theme.GushedTextSecondary

@Composable
fun EmergencyDialog(
    gateName: String,
    onDismiss: () -> Unit,
    onExecuteOverride: (serviceType: String, vehiclePlate: String, occupants: Int, notes: String) -> Unit
) {
    var selectedService by remember { mutableStateOf("Fire Service") }
    var vehiclePlate by remember { mutableStateOf("") }
    var occupantCount by remember { mutableIntStateOf(3) }
    var notes by remember { mutableStateOf("") }

    val services = listOf(
        "Fire Service" to Icons.Default.LocalFireDepartment,
        "Police Response" to Icons.Default.LocalPolice,
        "Ambulance / EMT" to Icons.Default.LocalHospital,
        "Estate Security Emergency" to Icons.Default.Warning
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .imePadding()
                .padding(vertical = 16.dp),
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
                                .background(Color(0xFFFEF2F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Emergency",
                                tint = Color(0xFFDC2626),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "EMERGENCY GATE OVERRIDE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFDC2626)
                            )
                            Text(
                                text = "Authorized Security Action • $gateName",
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

                Text(
                    text = "SELECT FIRST RESPONDER SERVICE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GushedTextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                services.forEach { (name, icon) ->
                    val isSelected = selectedService == name
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFFFEF2F2) else Color(0xFFF8FAFC))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFDC2626) else Color(0xFFE2E8F0),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedService = name }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = name,
                            tint = if (isSelected) Color(0xFFDC2626) else GushedTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = name,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFFDC2626) else GushedTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = vehiclePlate,
                        onValueChange = { vehiclePlate = it },
                        label = { Text("Emergency Vehicle Plate", fontSize = 11.sp) },
                        placeholder = { Text("e.g. FFS-01-AB", fontSize = 11.sp) },
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFDC2626),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedTextColor = GushedTextPrimary,
                            unfocusedTextColor = GushedTextPrimary
                        ),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                    )

                    OutlinedTextField(
                        value = "$occupantCount Crew",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Crew Count", fontSize = 11.sp) },
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFDC2626),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedTextColor = GushedTextPrimary,
                            unfocusedTextColor = GushedTextPrimary
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Emergency Event Description", fontSize = 11.sp) },
                    placeholder = { Text("e.g. Responding to smoke alarm reported at Villa 09", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFDC2626),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedTextColor = GushedTextPrimary,
                        unfocusedTextColor = GushedTextPrimary
                    ),
                    maxLines = 2,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                            onExecuteOverride(selectedService, vehiclePlate, occupantCount, notes)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Text(
                            text = "Execute Gate Override",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
