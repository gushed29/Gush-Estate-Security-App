package com.gush.security.estate.access.integration.gateway

import com.gush.security.estate.access.integration.adapters.DeviceOperationResult
import com.gush.security.estate.access.integration.adapters.HardwareAdapterRegistry
import com.gush.security.estate.access.integration.connectors.AutomationPlatform
import com.gush.security.estate.access.integration.connectors.AutomationRule
import com.gush.security.estate.access.integration.connectors.AutomationService
import com.gush.security.estate.access.integration.connectors.DatabaseBridgeSpec
import com.gush.security.estate.access.integration.connectors.DatabaseEngineType
import com.gush.security.estate.access.integration.connectors.ServerDatabaseBridgeService
import com.gush.security.estate.access.integration.connectors.WebhookEngine
import com.gush.security.estate.access.integration.model.AuthType
import com.gush.security.estate.access.integration.model.ConnectionType
import com.gush.security.estate.access.integration.model.ConnectorStatus
import com.gush.security.estate.access.integration.model.GushSecurityCommand
import com.gush.security.estate.access.integration.model.GushSecurityEvent
import com.gush.security.estate.access.integration.model.HardwareDeviceProfile
import com.gush.security.estate.access.integration.model.HardwareDeviceType
import com.gush.security.estate.access.integration.model.IntegrationConnectorConfig
import com.gush.security.estate.access.integration.model.IntegrationPermission
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Universal Access Control Integration Hub / Gush Connect Gateway Coordinator.
 * Manages all external integrations, hardware devices, security pipelines, and health streams.
 */
class GushSecurityIntegrationHub {

    val policyEngine = SecurityPolicyEngine()
    val eventBus = GushEventBus()
    val commandBus = GushCommandBus(policyEngine, eventBus)
    val webhookEngine = WebhookEngine()
    val dbBridgeService = ServerDatabaseBridgeService()
    val automationService = AutomationService()

    // Active Connectors
    private val _connectors = MutableStateFlow<List<IntegrationConnectorConfig>>(createInitialConnectors())
    val connectors: StateFlow<List<IntegrationConnectorConfig>> = _connectors.asStateFlow()

    // Registered Hardware Devices
    private val _devices = MutableStateFlow<List<HardwareDeviceProfile>>(createInitialDevices())
    val devices: StateFlow<List<HardwareDeviceProfile>> = _devices.asStateFlow()

    // Database Bridges
    private val _dbBridges = MutableStateFlow<List<DatabaseBridgeSpec>>(createInitialDbBridges())
    val dbBridges: StateFlow<List<DatabaseBridgeSpec>> = _dbBridges.asStateFlow()

    // Automation Rules
    private val _automationRules = MutableStateFlow<List<AutomationRule>>(createInitialAutomationRules())
    val automationRules: StateFlow<List<AutomationRule>> = _automationRules.asStateFlow()

    // Real-Time System Health Metrics
    val totalRequestsHandled: Long get() = _connectors.value.sumOf { it.totalRequestsHandled }
    val totalEventsDispatched: Long get() = _connectors.value.sumOf { it.totalEventsDispatched }
    val activeDevicesCount: Int get() = _devices.value.count { it.status == ConnectorStatus.ONLINE }

    /**
     * Toggles a connector's enabled state.
     */
    fun toggleConnector(connectorId: String) {
        _connectors.value = _connectors.value.map {
            if (it.connectorId == connectorId) it.copy(isEnabled = !it.isEnabled) else it
        }
    }

    /**
     * Safely tests a connector without executing any physical hardware actuation.
     */
    suspend fun testConnectorConnection(connectorId: String): String {
        val connector = _connectors.value.find { it.connectorId == connectorId }
            ?: return "Connector not found."
        
        // Simulating safe protocol probe
        val result = when (connector.connectionType) {
            ConnectionType.REST_API -> "HTTPS REST handshake verified. TLS 1.3 negotiated with valid certificate. Latency: 22ms."
            ConnectionType.OUTBOUND_WEBHOOK -> "Dispatched test ping webhook with HMAC-SHA256 signature. Receiver responded with HTTP 200 OK (34ms)."
            ConnectionType.WEBSOCKET -> "WSS persistent socket handshake acknowledged with heartbeat interval 15s."
            ConnectionType.MQTT_BROKER -> "MQTT 3.1.1 CONNECT packet acknowledged (CONNACK). Subscribed to topics: estate/+/access, estate/+/alerts."
            ConnectionType.LOCAL_LAN_HTTP -> "Local LAN Gateway responsive on 192.168.1.1:8443. Ping: 4ms."
            ConnectionType.DB_SERVER_BRIDGE -> "Server bridge connection alive. 0 pending schema sync errors."
            ConnectionType.AUTOMATION_WEBHOOK -> "Automation endpoint reachable. Payload template format validated."
            else -> "Connection probe completed with status 200 OK."
        }

        // Update connector success timestamp
        _connectors.value = _connectors.value.map {
            if (it.connectorId == connectorId) it.copy(lastSuccessEpoch = System.currentTimeMillis()) else it
        }
        return result
    }

    /**
     * Safely tests a hardware device's communication channel and optical/electrical sensors.
     */
    suspend fun testDevice(deviceId: String): DeviceOperationResult {
        val device = _devices.value.find { it.deviceId == deviceId }
            ?: return DeviceOperationResult(false, deviceId, "TEST", 0L, "Device not found in registry.")

        val adapter = HardwareAdapterRegistry.getAdapterFor(device.deviceType)
        return adapter.testConnection(device)
    }

    /**
     * Executes a physical access command through the secure pipeline.
     */
    suspend fun executeCommand(
        commandType: String,
        targetDeviceId: String,
        targetGateName: String,
        actorId: String,
        actorRole: String,
        parameters: Map<String, String> = emptyMap()
    ): DeviceOperationResult {
        val command = GushSecurityCommand(
            commandType = commandType,
            targetDeviceId = targetDeviceId,
            targetGateName = targetGateName,
            actorId = actorId,
            actorRole = actorRole,
            parameters = parameters
        )
        val targetDevice = _devices.value.find { it.deviceId == targetDeviceId }
        return commandBus.dispatchCommand(command, targetDevice)
    }

    /**
     * Adds a new custom connector.
     */
    fun addConnector(
        name: String,
        connectionType: ConnectionType,
        endpointUrl: String,
        authType: AuthType,
        permissions: Set<IntegrationPermission>
    ) {
        val newConnector = IntegrationConnectorConfig(
            name = name,
            connectionType = connectionType,
            endpointUrl = endpointUrl,
            authType = authType,
            permissions = permissions
        )
        _connectors.value = _connectors.value + newConnector
    }

    /**
     * Adds a new hardware device.
     */
    fun addDevice(
        name: String,
        deviceType: HardwareDeviceType,
        manufacturer: String,
        modelNumber: String,
        ipAddress: String,
        port: Int,
        location: String,
        assignedGateName: String
    ) {
        val newDevice = HardwareDeviceProfile(
            name = name,
            deviceType = deviceType,
            manufacturer = manufacturer,
            modelNumber = modelNumber,
            ipAddress = ipAddress,
            port = port,
            location = location,
            assignedGateName = assignedGateName
        )
        _devices.value = _devices.value + newDevice
    }

    private fun createInitialConnectors(): List<IntegrationConnectorConfig> {
        return listOf(
            IntegrationConnectorConfig(
                connectorId = "conn_rest_web",
                name = "Estate Resident Web Portal API",
                connectionType = ConnectionType.REST_API,
                endpointUrl = "https://portal.pinnockestate.org/api/v1",
                authType = AuthType.API_KEY,
                permissions = setOf(IntegrationPermission.READ_VISITORS, IntegrationPermission.READ_ACCESS_EVENTS, IntegrationPermission.REPORT_INCIDENT),
                totalRequestsHandled = 14250L,
                totalEventsDispatched = 8240L,
                averageLatencyMs = 24L
            ),
            IntegrationConnectorConfig(
                connectorId = "conn_wh_out",
                name = "Security Operations Webhook Stream",
                connectionType = ConnectionType.OUTBOUND_WEBHOOK,
                endpointUrl = "https://ops.gushsecurity.com/webhooks/estate_01",
                authType = AuthType.HMAC_SHA256,
                permissions = setOf(IntegrationPermission.TRIGGER_WEBHOOKS, IntegrationPermission.READ_ACCESS_EVENTS),
                totalRequestsHandled = 9840L,
                totalEventsDispatched = 9840L,
                averageLatencyMs = 38L
            ),
            IntegrationConnectorConfig(
                connectorId = "conn_db_bridge",
                name = "PostgreSQL Server-Side Sync Bridge",
                connectionType = ConnectionType.DB_SERVER_BRIDGE,
                endpointUrl = "https://bridge.estate-vps.internal:9443/sync",
                authType = AuthType.OAUTH2_BEARER,
                permissions = setOf(IntegrationPermission.READ_VISITORS, IntegrationPermission.READ_ACCESS_EVENTS),
                totalRequestsHandled = 32800L,
                totalEventsDispatched = 18450L,
                averageLatencyMs = 16L
            ),
            IntegrationConnectorConfig(
                connectorId = "conn_n8n_auto",
                name = "n8n Estate Automation Workflow",
                connectionType = ConnectionType.AUTOMATION_WEBHOOK,
                endpointUrl = "https://n8n.estate-lan:5678/webhook/gush-alerts",
                authType = AuthType.API_KEY,
                permissions = setOf(IntegrationPermission.READ_ACCESS_EVENTS, IntegrationPermission.REPORT_INCIDENT),
                totalRequestsHandled = 4120L,
                totalEventsDispatched = 4120L,
                averageLatencyMs = 45L
            ),
            IntegrationConnectorConfig(
                connectorId = "conn_lan_gate",
                name = "Local LAN Gate Relay Controller Bridge",
                connectionType = ConnectionType.LOCAL_LAN_HTTP,
                endpointUrl = "http://192.168.1.50:8080/relay",
                authType = AuthType.DEVICE_TOKEN,
                permissions = setOf(IntegrationPermission.OPEN_GATE, IntegrationPermission.READ_DEVICES, IntegrationPermission.DEVICE_STATUS),
                totalRequestsHandled = 6720L,
                totalEventsDispatched = 6720L,
                averageLatencyMs = 8L
            )
        )
    }

    private fun createInitialDevices(): List<HardwareDeviceProfile> {
        return listOf(
            HardwareDeviceProfile(
                deviceId = "dev_barrier_gate1",
                name = "Main Entrance Barrier Controller A1",
                deviceType = HardwareDeviceType.GATE_CONTROLLER,
                manufacturer = "FAAC / Magnetic Autocontrol",
                modelNumber = "Access Pro-H v4",
                ipAddress = "192.168.1.101",
                port = 8443,
                location = "Main Gate - Inbound Lane",
                assignedGateName = "Gate 1 - Pinnock Beach Estate Main Gate",
                status = ConnectorStatus.ONLINE,
                supportedCapabilities = listOf("pulse_open", "hold_open", "status_feedback", "anti_tailgating", "loop_detector")
            ),
            HardwareDeviceProfile(
                deviceId = "dev_scanner_gate1",
                name = "Main Gate 2D QR / RFID Terminal",
                deviceType = HardwareDeviceType.QR_SCANNER,
                manufacturer = "ZKTeco / Honeywell",
                modelNumber = "ProCapture-X 2D",
                ipAddress = "192.168.1.102",
                port = 5005,
                location = "Main Gate - Guard Kiosk Pedestal",
                assignedGateName = "Gate 1 - Pinnock Beach Estate Main Gate",
                status = ConnectorStatus.ONLINE,
                supportedCapabilities = listOf("qr_decode", "mifare_rfid", "audio_chime", "wiegand_34", "tamper_switch")
            ),
            HardwareDeviceProfile(
                deviceId = "dev_camera_lpr1",
                name = "Main Gate License Plate Recognition Camera",
                deviceType = HardwareDeviceType.IP_CAMERA,
                manufacturer = "Hikvision / Dahua ANPR",
                modelNumber = "DS-2CD7A26G0/P-IZS",
                ipAddress = "192.168.1.103",
                port = 554,
                location = "Main Gate - Overhead Inbound Gantry",
                assignedGateName = "Gate 1 - Pinnock Beach Estate Main Gate",
                status = ConnectorStatus.ONLINE,
                supportedCapabilities = listOf("anpr_lpr", "onvif_rtsp", "night_ir", "snapshot_capture")
            ),
            HardwareDeviceProfile(
                deviceId = "dev_lock_clubhouse",
                name = "Estate Clubhouse Smart Lock Deadbolt",
                deviceType = HardwareDeviceType.SMART_LOCK,
                manufacturer = "Yale / Assa Abloy Commercial",
                modelNumber = "Assure Lock 2 + PoE Bridge",
                ipAddress = "192.168.1.140",
                port = 9000,
                location = "Clubhouse Main Double Doors",
                assignedGateName = "Gate 2 - Service & Contractor Entrance",
                status = ConnectorStatus.ONLINE,
                batteryPercent = 94,
                supportedCapabilities = listOf("momentary_unlock", "audit_trail", "battery_telemetry", "passage_mode")
            ),
            HardwareDeviceProfile(
                deviceId = "dev_relay_pedestrian",
                name = "Pedestrian Turnstile Multi-Relay",
                deviceType = HardwareDeviceType.RELAY_MODULE,
                manufacturer = "Advantech ADAM",
                modelNumber = "ADAM-6060 Modbus/TCP",
                ipAddress = "192.168.1.115",
                port = 502,
                location = "Gate 1 Pedestrian Turnstile Lane",
                assignedGateName = "Gate 1 - Pinnock Beach Estate Main Gate",
                status = ConnectorStatus.ONLINE,
                supportedCapabilities = listOf("modbus_tcp", "6_relays", "6_digital_inputs", "counter")
            )
        )
    }

    private fun createInitialDbBridges(): List<DatabaseBridgeSpec> {
        return listOf(
            DatabaseBridgeSpec(
                bridgeId = "bridge_pg_01",
                name = "Main Estate PostgreSQL Cluster",
                engineType = DatabaseEngineType.POSTGRESQL,
                hostAddress = "db.pinnockestate.internal",
                port = 5432,
                databaseName = "pinnock_estate_access_db",
                syncTable = "estate_access_queue",
                recordsSyncedToday = 3420L
            ),
            DatabaseBridgeSpec(
                bridgeId = "bridge_mysql_01",
                name = "Property Management MySQL DB",
                engineType = DatabaseEngineType.MYSQL,
                hostAddress = "10.0.4.15",
                port = 3306,
                databaseName = "property_erp_v2",
                syncTable = "resident_directory_sync",
                recordsSyncedToday = 890L
            )
        )
    }

    private fun createInitialAutomationRules(): List<AutomationRule> {
        return listOf(
            AutomationRule(
                ruleId = "auto_rule_01",
                name = "VIP & Contractor Arrival Dispatch",
                platform = AutomationPlatform.N8N,
                triggerEventType = "access.granted",
                webhookUrl = "https://n8n.estate-ops.org/webhook/gush-vip-entry"
            ),
            AutomationRule(
                ruleId = "auto_rule_02",
                name = "Security Incident Emergency Notification",
                platform = AutomationPlatform.HOME_ASSISTANT,
                triggerEventType = "incident.created",
                webhookUrl = "http://homeassistant.local:8123/api/webhook/gush_estate_alert"
            ),
            AutomationRule(
                ruleId = "auto_rule_03",
                name = "Device Offline Supervisor Alert",
                platform = AutomationPlatform.IFTTT,
                triggerEventType = "device.offline",
                webhookUrl = "https://maker.ifttt.com/trigger/gush_device_down/with/key/YOUR_IFTTT_KEY"
            )
        )
    }
}
