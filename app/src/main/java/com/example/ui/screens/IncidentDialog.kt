package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.ui.theme.GushedAmberDark
import com.example.ui.theme.GushedAmberWarning
import com.example.ui.theme.GushedBorder
import com.example.ui.theme.GushedCrimsonDenied
import com.example.ui.theme.GushedSurfaceDark
import com.example.ui.theme.GushedTextPrimary
import com.example.ui.theme.GushedTextSecondary

@Composable
fun IncidentDialog(
    gateName: String,
    onDismiss: () -> Unit,
    onSubmitIncident: (title: String, category: String, severity: String, visitorName: String, vehiclePlate: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Vehicle Mismatch") }
    var severity by remember { mutableStateOf("MEDIUM") }
    var visitorName by remember { mutableStateOf("") }
    var vehiclePlate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var categoryDropdownOpen by remember { mutableStateOf(false) }
    var severityDropdownOpen by remember { mutableStateOf(false) }

    val categories = listOf(
        "Vehicle Mismatch",
        "Fake Credential / Spoof Attempt",
        "Unauthorized Item Removal",
        "Attempted Forced Entry",
        "Aggressive Visitor",
        "Suspicious Loitering",
        "Property Discrepancy"
    )

    val severities = listOf("LOW", "MEDIUM", "HIGH", "CRITICAL")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
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
                                .background(Color(0xFFFFFBEB)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReportProblem,
                                contentDescription = "Incident",
                                tint = Color(0xFFD97706),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "LOG SECURITY INCIDENT",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushedTextPrimary
                            )
                            Text(
                                text = "Gatehouse Ops • $gateName",
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

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Incident Title *", fontSize = 11.sp) },
                    placeholder = { Text("e.g. Unregistered vehicle attempted tailgate entry", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD97706),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedTextColor = GushedTextPrimary,
                        unfocusedTextColor = GushedTextPrimary
                    ),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category
                    Box(modifier = Modifier.weight(1.2f)) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category", fontSize = 11.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { categoryDropdownOpen = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD97706),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedTextColor = GushedTextPrimary,
                                unfocusedTextColor = GushedTextPrimary
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )
                        DropdownMenu(
                            expanded = categoryDropdownOpen,
                            onDismissRequest = { categoryDropdownOpen = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat, color = GushedTextPrimary) },
                                    onClick = {
                                        category = cat
                                        categoryDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }

                    // Severity
                    Box(modifier = Modifier.weight(0.8f)) {
                        OutlinedTextField(
                            value = severity,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Severity", fontSize = 11.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { severityDropdownOpen = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD97706),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedTextColor = GushedTextPrimary,
                                unfocusedTextColor = GushedTextPrimary
                            ),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )
                        DropdownMenu(
                            expanded = severityDropdownOpen,
                            onDismissRequest = { severityDropdownOpen = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            severities.forEach { sev ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            sev,
                                            color = if (sev == "CRITICAL" || sev == "HIGH") Color(0xFFDC2626) else GushedTextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    },
                                    onClick = {
                                        severity = sev
                                        severityDropdownOpen = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = visitorName,
                        onValueChange = { visitorName = it },
                        label = { Text("Individual Involved", fontSize = 11.sp) },
                        placeholder = { Text("e.g. Unknown Driver", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD97706),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedTextColor = GushedTextPrimary,
                            unfocusedTextColor = GushedTextPrimary
                        ),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                    )

                    OutlinedTextField(
                        value = vehiclePlate,
                        onValueChange = { vehiclePlate = it },
                        label = { Text("Vehicle Plate", fontSize = 11.sp) },
                        placeholder = { Text("e.g. KJA-992-ZZ", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD97706),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedTextColor = GushedTextPrimary,
                            unfocusedTextColor = GushedTextPrimary
                        ),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Incident Description & Evidence Notes *", fontSize = 11.sp) },
                    placeholder = { Text("Detailed account of events observed at gate...", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD97706),
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
                            if (title.isNotBlank() && description.isNotBlank()) {
                                onSubmitIncident(
                                    title.trim(),
                                    category,
                                    severity,
                                    visitorName.trim(),
                                    vehiclePlate.trim(),
                                    description.trim()
                                )
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank() && description.isNotBlank(),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706))
                    ) {
                        Text(
                            text = "Submit Incident",
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
