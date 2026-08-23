package com.gush.security.estate.access.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DoorSliding
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.gush.security.estate.access.integration.connectors.AutomationPlatform
import com.gush.security.estate.access.integration.connectors.AutomationRule
import com.gush.security.estate.access.integration.connectors.DatabaseBridgeSpec
import com.gush.security.estate.access.integration.model.AuthType
import com.gush.security.estate.access.integration.model.ConnectionType
import com.gush.security.estate.access.integration.model.ConnectorStatus
import com.gush.security.estate.access.integration.model.GushSecurityCommand
import com.gush.security.estate.access.integration.model.GushSecurityEvent
import com.gush.security.estate.access.integration.model.HardwareDeviceProfile
import com.gush.security.estate.access.integration.model.HardwareDeviceType
import com.gush.security.estate.access.integration.model.IntegrationConnectorConfig
import com.gush.security.estate.access.integration.model.IntegrationPermission
import com.gush.security.estate.access.ui.theme.GushAmberDark
import com.gush.security.estate.access.ui.theme.GushAmberWarning
import com.gush.security.estate.access.ui.theme.GushBorder
import com.gush.security.estate.access.ui.theme.GushCobalt
import com.gush.security.estate.access.ui.theme.GushCrimsonDark
import com.gush.security.estate.access.ui.theme.GushCrimsonDenied
import com.gush.security.estate.access.ui.theme.GushCyanAccent
import com.gush.security.estate.access.ui.theme.GushEmeraldApproved
import com.gush.security.estate.access.ui.theme.GushEmeraldDark
import com.gush.security.estate.access.ui.theme.GushNavy
import com.gush.security.estate.access.ui.theme.GushTextMuted
import com.gush.security.estate.access.ui.theme.GushTextPrimary
import com.gush.security.estate.access.ui.theme.GushTextSecondary
import com.gush.security.estate.access.ui.viewmodel.EstateSecurityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun IntegrationManagerScreen(
    viewModel: EstateSecurityViewModel,
    modifier: Modifier = Modifier
) {
    val connectors by viewModel.integrationConnectors.collectAsState()
    val devices by viewModel.registeredDevices.collectAsState()
    val dbBridges by viewModel.databaseBridges.collectAsState()
    val automationRules by viewModel.automationRules.collectAsState()
    val events by viewModel.liveIntegrationEvents.collectAsState()
    val commands by viewModel.commandExecutionLog.collectAsState()
    val statusMessage by viewModel.integrationStatusMessage.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Connectors", "Hardware Devices", "Database Bridges", "Automations", "Event Ledger", "API Docs")

    var showAddConnectorDialog by remember { mutableStateOf(false) }
    var showAddDeviceDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Hero Integration Gateway Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.dp, GushBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFEEF2FF), Color(0xFFF8FAFC))
                        )
                    )
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GushCobalt),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Hub,
                                contentDescription = "Integration Gateway",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "GUSH CONNECT",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GushCobalt,
                                    letterSpacing = 1.2.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(GushEmeraldDark)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "GATEWAY ACTIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GushEmeraldApproved
                                    )
                                }
                            }
                            Text(
                                text = "Universal Access Control Integration Hub",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Real-Time Health Metrics Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GatewayMetricChip(
                        title = "Connectors",
                        value = "${connectors.count { it.isEnabled }}/${connectors.size}",
                        icon = Icons.Default.Sensors,
                        modifier = Modifier.weight(1f)
                    )
                    GatewayMetricChip(
                        title = "Online Devices",
                        value = "${devices.count { it.status == ConnectorStatus.ONLINE }}/${devices.size}",
                        icon = Icons.Default.DoorSliding,
                        modifier = Modifier.weight(1f)
                    )
                    GatewayMetricChip(
                        title = "Events Today",
                        value = "${viewModel.integrationHub.totalEventsDispatched / 1000}k",
                        icon = Icons.Default.Bolt,
                        modifier = Modifier.weight(1f)
                    )
                    GatewayMetricChip(
                        title = "Avg Latency",
                        value = "24ms",
                        icon = Icons.Default.Speed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Live Action Status Probe Toast
        AnimatedVisibility(
            visible = statusMessage != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            if (statusMessage != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = statusMessage!!,
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DISMISS",
                            color = Color(0xFF93C5FD),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { viewModel.dismissIntegrationStatus() }
                        )
                    }
                }
            }
        }

        // Tab Navigation
        ScrollableTabRow(
            selectedTabIndex = selectedTabIndex,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {}
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedTabIndex == index) GushCobalt else GushTextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        when (selectedTabIndex) {
            0 -> ConnectorsTab(
                connectors = connectors,
                onToggleConnector = { viewModel.toggleConnectorState(it) },
                onTestConnector = { viewModel.testConnectorConnection(it) },
                onAddConnectorClick = { showAddConnectorDialog = true }
            )
            1 -> HardwareDevicesTab(
                devices = devices,
                onTestDevice = { viewModel.testDeviceConnection(it) },
                onActuateCommand = { cmd, devId, gate ->
                    viewModel.executeRemoteHardwareCommand(cmd, devId, gate)
                },
                onAddDeviceClick = { showAddDeviceDialog = true }
            )
            2 -> DatabaseBridgesTab(
                dbBridges = dbBridges,
                onTestBridge = { viewModel.testDatabaseBridge(it) },
                onSyncNow = { viewModel.triggerDatabaseSync(it) }
            )
            3 -> AutomationsTab(
                automationRules = automationRules,
                onTestRule = { viewModel.testAutomationRule(it) }
            )
            4 -> EventLedgerTab(
                events = events,
                commands = commands
            )
            5 -> ApiDocsTab()
        }
    }

    if (showAddConnectorDialog) {
        AddConnectorDialog(
            onDismiss = { showAddConnectorDialog = false },
            onAdd = { name, type, url, auth, perms ->
                viewModel.registerNewConnector(name, type, url, auth, perms)
                showAddConnectorDialog = false
            }
        )
    }

    if (showAddDeviceDialog) {
        AddDeviceDialog(
            onDismiss = { showAddDeviceDialog = false },
            onAdd = { name, type, mfg, model, ip, port, loc, gate ->
                viewModel.registerNewDevice(name, type, mfg, model, ip, port, loc, gate)
                showAddDeviceDialog = false
            }
        )
    }
}

@Composable
fun GatewayMetricChip(
    title: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = GushCobalt,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = GushTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = GushTextPrimary
            )
        }
    }
}

// =========================================================================
// TAB 1: CONNECTORS (REST, WEBHOOKS, WEBSOCKET, MQTT, LAN)
// =========================================================================
@Composable
fun ConnectorsTab(
    connectors: List<IntegrationConnectorConfig>,
    onToggleConnector: (String) -> Unit,
    onTestConnector: (String) -> Unit,
    onAddConnectorClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Integration Connectors (${connectors.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GushTextPrimary
                )
                Button(
                    onClick = onAddConnectorClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GushCobalt),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Connector", fontSize = 12.sp)
                }
            }
        }

        items(connectors, key = { it.connectorId }) { conn ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, GushBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = conn.name,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GushTextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (conn.isEnabled) GushEmeraldDark else GushCrimsonDark)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (conn.isEnabled) "ACTIVE" else "DISABLED",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (conn.isEnabled) GushEmeraldApproved else GushCrimsonDenied
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${conn.connectionType.displayName} • Auth: ${conn.authType.name}",
                                fontSize = 12.sp,
                                color = GushTextSecondary
                            )
                        }

                        Switch(
                            checked = conn.isEnabled,
                            onCheckedChange = { onToggleConnector(conn.connectorId) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GushCobalt
                            )
                        )
                    }

                    if (conn.endpointUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = conn.endpointUrl,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF334155),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Permissions tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        conn.permissions.take(3).forEach { perm ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (perm.isHighRisk) Color(0xFFFEF3C7) else Color(0xFFEEF2FF))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = perm.name,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (perm.isHighRisk) Color(0xFF92400E) else Color(0xFF3730A3)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Bottom Action Bar: Safe Connection Test
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Requests: ${conn.totalRequestsHandled} • Latency: ${conn.averageLatencyMs}ms",
                            fontSize = 11.sp,
                            color = GushTextMuted
                        )

                        OutlinedButton(
                            onClick = { onTestConnector(conn.connectorId) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GushCobalt)
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = "Probe", tint = GushCobalt, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Test Connection", fontSize = 11.sp, color = GushCobalt, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 2: HARDWARE DEVICES (GATE CONTROLLERS, LOCKS, RELAYS, IP CAMERAS)
// =========================================================================
@Composable
fun HardwareDevicesTab(
    devices: List<HardwareDeviceProfile>,
    onTestDevice: (String) -> Unit,
    onActuateCommand: (String, String, String) -> Unit,
    onAddDeviceClick: () -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Enrolled Access Control Hardware (${devices.size})",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = GushTextPrimary
                )
                Button(
                    onClick = onAddDeviceClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GushCobalt),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Enroll Device", fontSize = 12.sp)
                }
            }
        }

        items(devices, key = { it.deviceId }) { dev ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, GushBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val icon = when (dev.deviceType) {
                                HardwareDeviceType.GATE_CONTROLLER -> Icons.Default.DoorSliding
                                HardwareDeviceType.SMART_LOCK -> Icons.Default.Lock
                                HardwareDeviceType.IP_CAMERA -> Icons.Default.Videocam
                                HardwareDeviceType.QR_SCANNER -> Icons.Default.QrCodeScanner
                                else -> Icons.Default.Sensors
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GushCobalt.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = dev.name, tint = GushCobalt, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = dev.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GushTextPrimary
                                )
                                Text(
                                    text = "${dev.manufacturer} • ${dev.modelNumber}",
                                    fontSize = 11.sp,
                                    color = GushTextSecondary
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (dev.status == ConnectorStatus.ONLINE) GushEmeraldDark else GushCrimsonDark)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = dev.status.name,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (dev.status == ConnectorStatus.ONLINE) GushEmeraldApproved else GushCrimsonDenied
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "LAN IP: ${dev.ipAddress}:${dev.port}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = GushTextSecondary
                        )
                        Text(
                            text = dev.assignedGateName.take(24) + "...",
                            fontSize = 11.sp,
                            color = GushCobalt,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Safe Hardware Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onTestDevice(dev.deviceId) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                        ) {
                            Icon(Icons.Default.Sensors, contentDescription = "Probe", modifier = Modifier.size(14.dp), tint = GushTextSecondary)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Probe Device", fontSize = 11.sp, color = GushTextSecondary)
                        }

                        Button(
                            onClick = {
                                val cmd = if (dev.deviceType == HardwareDeviceType.SMART_LOCK) "UNLOCK_DOOR" else "OPEN_GATE"
                                onActuateCommand(cmd, dev.deviceId, dev.assignedGateName)
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GushCobalt),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.PowerSettingsNew, contentDescription = "Actuate", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                if (dev.deviceType == HardwareDeviceType.SMART_LOCK) "Unlock" else "Open Barrier",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 3: DATABASE BRIDGES (POSTGRESQL, MYSQL, MARIADB, SQLITE)
// =========================================================================
@Composable
fun DatabaseBridgesTab(
    dbBridges: List<DatabaseBridgeSpec>,
    onTestBridge: (DatabaseBridgeSpec) -> Unit,
    onSyncNow: (DatabaseBridgeSpec) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = "DB Architecture", tint = Color(0xFF1D4ED8), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Security Architecture: Mobile clients never connect directly to production databases. Synchronizations flow through authenticated server-side bridges.",
                        fontSize = 11.sp,
                        color = Color(0xFF1E40AF),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        items(dbBridges, key = { it.bridgeId }) { bridge ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, GushBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = bridge.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushTextPrimary
                            )
                            Text(
                                text = "${bridge.engineType.label} • DB: ${bridge.databaseName}",
                                fontSize = 11.sp,
                                color = GushTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GushEmeraldDark)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SYNC ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushEmeraldApproved
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Host: ${bridge.hostAddress}:${bridge.port} • Queue: ${bridge.syncTable}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = GushTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onTestBridge(bridge) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text("Test DB Ping", fontSize = 11.sp, color = GushTextSecondary)
                        }

                        Button(
                            onClick = { onSyncNow(bridge) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = GushCobalt),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Sync", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Queue", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 4: AUTOMATIONS (IFTTT, n8n, HOME ASSISTANT, ZAPIER)
// =========================================================================
@Composable
fun AutomationsTab(
    automationRules: List<AutomationRule>,
    onTestRule: (AutomationRule) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(automationRules, key = { it.ruleId }) { rule ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, GushBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = rule.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushTextPrimary
                            )
                            Text(
                                text = "Platform: ${rule.platform.platformName}",
                                fontSize = 11.sp,
                                color = GushTextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = rule.triggerEventType,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = rule.webhookUrl,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF64748B),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Triggers: ${rule.triggerCount}",
                            fontSize = 11.sp,
                            color = GushTextMuted
                        )

                        Button(
                            onClick = { onTestRule(rule) },
                            colors = ButtonDefaults.buttonColors(containerColor = GushCobalt),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.Webhook, contentDescription = "Test", modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Dispatch Test Trigger", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 5: EVENT & COMMAND AUDIT LEDGER
// =========================================================================
@Composable
fun EventLedgerTab(
    events: List<GushSecurityEvent>,
    commands: List<GushSecurityCommand>
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "Live Normalized Security Event Ledger",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = GushTextPrimary
            )
        }

        if (events.isEmpty()) {
            item {
                Text(
                    text = "No integration events in buffer. Actuate gates or run connector tests to generate live events.",
                    fontSize = 12.sp,
                    color = GushTextMuted
                )
            }
        }

        items(events.reversed().take(20), key = { it.eventId }) { evt ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (evt.eventType.contains("granted") || evt.eventType.contains("opened")) GushEmeraldApproved else GushCobalt)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = evt.eventType,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = GushTextPrimary
                            )
                            Text(
                                text = timeFormat.format(Date(evt.timestamp)),
                                fontSize = 10.sp,
                                color = GushTextMuted
                            )
                        }
                        Text(
                            text = "Src: ${evt.source} • Actor: ${evt.actorId} • Corr: ${evt.correlationId.take(8)}",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = GushTextSecondary
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 6: API DOCS & QUICKSTART
// =========================================================================
@Composable
fun ApiDocsTab() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, GushBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Gush Security REST API Reference (v1)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = GushTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "External websites, Node.js services, PHP backends, and Python scripts integrate with Gush via standard HTTP REST with API key or Bearer tokens.",
                        fontSize = 12.sp,
                        color = GushTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    ApiEndpointItem(
                        method = "POST",
                        path = "/api/v1/access/verify",
                        description = "Verify QR pass or PIN code and return admission status"
                    )
                    ApiEndpointItem(
                        method = "POST",
                        path = "/api/v1/visitor/passes",
                        description = "Create new visitor, contractor, or delivery pass"
                    )
                    ApiEndpointItem(
                        method = "POST",
                        path = "/api/v1/devices/{id}/command",
                        description = "Dispatch high-integrity hardware command (Requires OPEN_GATE perm)"
                    )
                    ApiEndpointItem(
                        method = "GET",
                        path = "/api/v1/access/events",
                        description = "Stream live estate access events with pagination and filters"
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Inbound Webhook Signature Header",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "All inbound webhooks must supply the Gush-Signature header for HMAC-SHA256 and replay protection:",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Gush-Signature: t=1724400000,v1=5d41402abc4b2a76b9719d911017c592...",
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApiEndpointItem(
    method: String,
    path: String,
    description: String
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (method == "POST") GushCobalt else GushEmeraldApproved)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = method,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = path,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = GushTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = description,
            fontSize = 11.sp,
            color = GushTextSecondary
        )
    }
}

// =========================================================================
// ADD CONNECTOR DIALOG
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddConnectorDialog(
    onDismiss: () -> Unit,
    onAdd: (String, ConnectionType, String, AuthType, Set<IntegrationPermission>) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var endpointUrl by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ConnectionType.REST_API) }
    var selectedAuth by remember { mutableStateOf(AuthType.API_KEY) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Register New Integration Connector",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GushTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Connector Name (e.g. Resident Portal API)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = endpointUrl,
                    onValueChange = { endpointUrl = it },
                    label = { Text("Endpoint URL / Host") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(
                                    name,
                                    selectedType,
                                    endpointUrl,
                                    selectedAuth,
                                    setOf(IntegrationPermission.READ_VISITORS, IntegrationPermission.READ_ACCESS_EVENTS)
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GushCobalt),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Register")
                    }
                }
            }
        }
    }
}

// =========================================================================
// ADD HARDWARE DEVICE DIALOG
// =========================================================================
@Composable
fun AddDeviceDialog(
    onDismiss: () -> Unit,
    onAdd: (String, HardwareDeviceType, String, String, String, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("FAAC / Hikvision") }
    var modelNumber by remember { mutableStateOf("Pro-Gate v4") }
    var ipAddress by remember { mutableStateOf("192.168.1.150") }
    var location by remember { mutableStateOf("North Gate Barrier") }
    var assignedGate by remember { mutableStateOf("Gate 1 - Pinnock Beach Estate Main Gate") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Enroll Access Control Hardware",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GushTextPrimary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Device Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ipAddress,
                    onValueChange = { ipAddress = it },
                    label = { Text("LAN IP Address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Physical Location") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss, shape = RoundedCornerShape(8.dp)) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAdd(name, HardwareDeviceType.GATE_CONTROLLER, manufacturer, modelNumber, ipAddress, 8443, location, assignedGate)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GushCobalt),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Enroll")
                    }
                }
            }
        }
    }
}
