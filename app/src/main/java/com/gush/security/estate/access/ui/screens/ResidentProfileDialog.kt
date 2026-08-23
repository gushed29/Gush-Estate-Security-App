package com.gush.security.estate.access.ui.screens

import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContactPhone
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.gush.security.estate.access.data.local.entities.ResidentAccountEntity
import com.gush.security.estate.access.ui.theme.FrostedGlassBorder
import com.gush.security.estate.access.ui.theme.FrostedGlassSurface
import com.gush.security.estate.access.ui.theme.GushedCobalt
import com.gush.security.estate.access.ui.theme.GushedTextPrimary
import com.gush.security.estate.access.ui.theme.GushedTextSecondary

@Composable
fun ResidentProfileDialog(
    resident: ResidentAccountEntity,
    onDismiss: () -> Unit,
    onSaveProfile: (fullName: String, unitNumber: String, phone: String, email: String, vehicles: String, emergencyContact: String) -> Unit
) {
    var fullName by remember { mutableStateOf(resident.fullName) }
    var unitNumber by remember { mutableStateOf(resident.unitNumber) }
    var phone by remember { mutableStateOf(resident.phone) }
    var email by remember { mutableStateOf(resident.email) }
    var registeredVehicles by remember { mutableStateOf(resident.registeredVehicles) }
    var emergencyContact by remember { mutableStateOf(resident.emergencyContact) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(1.5.dp, FrostedGlassBorder, RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = FrostedGlassSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = CircleShape,
                                color = GushedCobalt.copy(alpha = 0.12f),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = GushedCobalt,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Residence Registration",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = GushedTextPrimary
                                )
                                Text(
                                    text = "Property & Household Details",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = GushedTextSecondary
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Primary Resident Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GushedCobalt,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = unitNumber,
                        onValueChange = { unitNumber = it },
                        label = { Text("Residence Property Unit & Street Address") },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GushedCobalt,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Primary Telephone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GushedCobalt,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Official Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GushedCobalt,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = registeredVehicles,
                        onValueChange = { registeredVehicles = it },
                        label = { Text("Registered Vehicle License Plates (Comma separated)") },
                        placeholder = { Text("e.g. LAG-849-XY, KJA-992-ZZ") },
                        leadingIcon = { Icon(Icons.Default.DirectionsCar, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GushedCobalt,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = emergencyContact,
                        onValueChange = { emergencyContact = it },
                        label = { Text("Emergency Security Contact") },
                        leadingIcon = { Icon(Icons.Default.ContactPhone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GushedCobalt,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            if (fullName.isNotBlank()) {
                                onSaveProfile(
                                    fullName.trim(),
                                    unitNumber.trim(),
                                    phone.trim(),
                                    email.trim(),
                                    registeredVehicles.trim(),
                                    emergencyContact.trim()
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Update Residence Profile", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}
