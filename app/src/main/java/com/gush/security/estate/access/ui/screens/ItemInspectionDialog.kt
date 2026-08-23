package com.gush.security.estate.access.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gush.security.estate.access.data.local.entities.DeclaredItemEntity
import com.gush.security.estate.access.data.local.entities.VisitorPassEntity
import com.gush.security.estate.access.ui.theme.GushedAmberDark
import com.gush.security.estate.access.ui.theme.GushedAmberWarning
import com.gush.security.estate.access.ui.theme.GushedBorder
import com.gush.security.estate.access.ui.theme.GushedBorderBright
import com.gush.security.estate.access.ui.theme.GushedCobalt
import com.gush.security.estate.access.ui.theme.GushedCyanAccent
import com.gush.security.estate.access.ui.theme.GushedEmeraldApproved
import com.gush.security.estate.access.ui.theme.GushedEmeraldDark
import com.gush.security.estate.access.ui.theme.GushedPrimaryNavy
import com.gush.security.estate.access.ui.theme.GushedSurfaceContainer
import com.gush.security.estate.access.ui.theme.GushedSurfaceDark
import com.gush.security.estate.access.ui.theme.GushedSurfaceElevated
import com.gush.security.estate.access.ui.theme.GushedTextMuted
import com.gush.security.estate.access.ui.theme.GushedTextPrimary
import com.gush.security.estate.access.ui.theme.GushedTextSecondary

@Composable
fun ItemInspectionDialog(
    pass: VisitorPassEntity,
    initialItems: List<DeclaredItemEntity>,
    isExitMode: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (List<DeclaredItemEntity>, String, String?) -> Unit
) {
    val itemsState = remember { mutableStateListOf<DeclaredItemEntity>().apply { addAll(initialItems) } }
    var guardNotes by remember { mutableStateOf("") }
    var newItemName by remember { mutableStateOf("") }
    var newItemCategory by remember { mutableStateOf("Electronics") }

    // Digital Signature Path
    val signaturePaths = remember { mutableStateListOf<Path>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var hasSigned by remember { mutableStateOf(false) }

    val hasDiscrepancy = itemsState.any {
        it.exitInspectionStatus == "ADDED" || it.exitInspectionStatus == "MISSING" || it.exitInspectionStatus == "UNDECLARED"
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
            color = GushedSurfaceDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (hasDiscrepancy) GushedAmberWarning else GushedBorder)
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
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isExitMode) GushedAmberDark else GushedCobalt),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = "Items",
                                tint = if (isExitMode) GushedAmberWarning else GushedCyanAccent,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isExitMode) "EXIT PROPERTY INSPECTION" else "ENTRY PROPERTY DECLARATION",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushedTextPrimary
                            )
                            Text(
                                text = "Chain of Custody • ${pass.visitorName}",
                                fontSize = 11.sp,
                                color = GushedTextSecondary
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = GushedTextSecondary)
                    }
                }

                // Discrepancy Warning Notice
                if (isExitMode && hasDiscrepancy) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GushedAmberDark)
                            .border(1.dp, GushedAmberWarning, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = GushedAmberWarning,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ITEM DISCREPANCY DETECTED: Item count or status differs from entry record. Resident will be notified.",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFEF3C7)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (itemsState.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GushedSurfaceContainer)
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No property or tools declared for this visit.",
                                    fontSize = 12.sp,
                                    color = GushedTextMuted
                                )
                            }
                        }
                    }

                    itemsIndexed(itemsState) { index, item ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = GushedSurfaceContainer),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when (item.exitInspectionStatus) {
                                    "MATCHED" -> GushedEmeraldDark
                                    "ADDED", "MISSING", "UNDECLARED" -> GushedAmberDark
                                    else -> GushedBorder
                                }
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${item.quantity}x ${item.itemName}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GushedTextPrimary
                                        )
                                        Text(
                                            text = "Category: ${item.category} • S/N: ${if (item.serialNumber.isNotEmpty()) item.serialNumber else "N/A"}",
                                            fontSize = 11.sp,
                                            color = GushedTextSecondary
                                        )
                                    }
                                    if (isExitMode) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (item.exitInspectionStatus == "MATCHED") GushedEmeraldDark
                                                    else GushedAmberDark
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = item.exitInspectionStatus,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (item.exitInspectionStatus == "MATCHED") GushedEmeraldApproved else GushedAmberWarning
                                            )
                                        }
                                    }
                                }

                                if (isExitMode) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf("MATCHED", "ADDED", "REMOVED", "MISSING").forEach { status ->
                                            val isSelected = item.exitInspectionStatus == status
                                            FilterChip(
                                                selected = isSelected,
                                                onClick = {
                                                    itemsState[index] = item.copy(exitInspectionStatus = status)
                                                },
                                                label = {
                                                    Text(
                                                        text = status,
                                                        fontSize = 10.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = if (status == "MATCHED") GushedEmeraldDark else GushedAmberDark,
                                                    selectedLabelColor = Color.White,
                                                    containerColor = GushedSurfaceElevated,
                                                    labelColor = GushedTextSecondary
                                                ),
                                                modifier = Modifier.height(28.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Add Extra Item Option
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = newItemName,
                                onValueChange = { newItemName = it },
                                placeholder = { Text("+ Add undeclared item...", fontSize = 11.sp, color = GushedTextMuted) },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = GushedCyanAccent,
                                    unfocusedBorderColor = GushedBorder,
                                    focusedTextColor = GushedTextPrimary,
                                    unfocusedTextColor = GushedTextPrimary
                                ),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )
                            Button(
                                onClick = {
                                    if (newItemName.isNotBlank()) {
                                        itemsState.add(
                                            DeclaredItemEntity(
                                                passId = pass.id,
                                                itemName = newItemName.trim(),
                                                category = newItemCategory,
                                                quantity = 1,
                                                exitInspectionStatus = if (isExitMode) "ADDED" else "MATCHED"
                                            )
                                        )
                                        newItemName = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GushedCobalt),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Digital Touchscreen Signature Area
                Text(
                    text = "DIGITAL ACKNOWLEDGEMENT SIGNATURE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = GushedTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(85.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GushedPrimaryNavy)
                        .border(1.dp, if (hasSigned) GushedCyanAccent else GushedBorder, RoundedCornerShape(8.dp))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    val path = Path().apply { moveTo(offset.x, offset.y) }
                                    currentPath = path
                                    signaturePaths.add(path)
                                    hasSigned = true
                                },
                                onDrag = { change, _ ->
                                    currentPath?.lineTo(change.position.x, change.position.y)
                                }
                            )
                        }
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        signaturePaths.forEach { path ->
                            drawPath(
                                path = path,
                                color = GushedCyanAccent,
                                style = Stroke(
                                    width = 3.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round
                                )
                            )
                        }
                    }
                    if (!hasSigned) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Draw,
                                contentDescription = "Sign",
                                tint = GushedTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sign on screen to confirm property custody",
                                fontSize = 11.sp,
                                color = GushedTextMuted
                            )
                        }
                    } else {
                        Text(
                            text = "CLEAR",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = GushedTextMuted,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .clickable {
                                    signaturePaths.clear()
                                    currentPath = null
                                    hasSigned = false
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Guard Notes
                OutlinedTextField(
                    value = guardNotes,
                    onValueChange = { guardNotes = it },
                    label = { Text("Guard Inspection Notes", fontSize = 11.sp) },
                    placeholder = { Text("e.g. Serial numbers verified against physical chassis", fontSize = 11.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GushedCyanAccent,
                        unfocusedBorderColor = GushedBorder,
                        focusedTextColor = GushedTextPrimary,
                        unfocusedTextColor = GushedTextPrimary
                    ),
                    maxLines = 2,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = GushedTextSecondary)
                    ) {
                        Text("Cancel", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val sigStr = if (hasSigned) "SIG_VERIFIED_TOUCHSCREEN" else "NO_SIG"
                            onConfirm(itemsState.toList(), guardNotes, sigStr)
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(44.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isExitMode) GushedEmeraldApproved else GushedCobalt
                        )
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Confirm", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isExitMode) "Finalize Exit Record" else "Save Property Items",
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
