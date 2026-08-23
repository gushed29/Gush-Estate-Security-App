package com.gush.security.estate.access.integration.model

import java.util.UUID

/**
 * Supported connection protocols for Gush Security Hub.
 */
enum class ConnectionType(val displayName: String, val protocol: String) {
    REST_API("REST API (v1)", "https"),
    INBOUND_WEBHOOK("Inbound Webhook", "https"),
    OUTBOUND_WEBHOOK("Outbound Webhook", "https"),
    WEBSOCKET("WebSocket Live Stream", "wss"),
    SSE("Server-Sent Events", "https"),
    LOCAL_LAN_HTTP("Local LAN Gateway (HTTP/S)", "http"),
    MQTT_BROKER("MQTT Broker (IoT)", "mqtt"),
    TCP_UDP_RELAY("TCP/UDP Hardware Relay", "tcp"),
    BLUETOOTH_LE("Bluetooth Low Energy (BLE)", "ble"),
    DB_SERVER_BRIDGE("Server-Side DB Bridge (Postgres/MySQL)", "bridge"),
    AUTOMATION_WEBHOOK("Automation (IFTTT / n8n / Home Assistant)", "webhook"),
    CUSTOM_OPENAPI("Custom OpenAPI Connector", "openapi")
}

/**
 * Authentication mechanisms supported by Gush Integration Gateway.
 */
enum class AuthType {
    API_KEY,
    HMAC_SHA256,
    OAUTH2_BEARER,
    MTLS,
    DEVICE_TOKEN,
    LOCAL_LAN_PIN
}

/**
 * Granular permissions enforcing Least Privilege access for integrations.
 */
enum class IntegrationPermission(val label: String, val isHighRisk: Boolean = false) {
    READ_VISITORS("Read Visitor Passes"),
    READ_ACCESS_EVENTS("Read Access Events & Logs"),
    READ_DEVICES("Read Device Telemetry"),
    DEVICE_STATUS("Monitor Device Heartbeats"),
    REPORT_INCIDENT("Submit Incident Reports"),
    TRIGGER_WEBHOOKS("Dispatch Outbound Webhooks"),
    GRANT_ACCESS("Authorize Visitor Access", isHighRisk = true),
    OPEN_GATE("Remote Barrier / Gate Open", isHighRisk = true),
    UNLOCK_DOOR("Remote Smart Lock Unlock", isHighRisk = true),
    EMERGENCY_OVERRIDE("System-Wide Emergency Override", isHighRisk = true),
    MANAGE_DEVICES("Enroll & Configure Hardware Devices", isHighRisk = true)
}

/**
 * Access Control hardware categories managed by Gush Security Hub.
 */
enum class HardwareDeviceType(val label: String) {
    GATE_CONTROLLER("Barrier Gate Controller"),
    SMART_LOCK("Smart Deadbolt / Commercial Lock"),
    TURNSTILE("Pedestrian Turnstile"),
    RELAY_MODULE("Multi-Channel Relay Switch"),
    RFID_READER("Wiegand / OSDP RFID Reader"),
    QR_SCANNER("Optical QR / Barcode Scanner"),
    IP_CAMERA("LPR / Security Surveillance Camera"),
    INTERCOM_TERMINAL("SIP / 2-Way Video Intercom"),
    IOT_SENSOR("Vehicle Loop / Motion Sensor")
}

/**
 * Operational connectivity status for devices and connectors.
 */
enum class ConnectorStatus {
    ONLINE,
    OFFLINE,
    DEGRADED,
    UNAUTHORIZED,
    DISABLED,
    CONNECTING
}

/**
 * Registered integration connector profile in Gush Security Hub.
 */
data class IntegrationConnectorConfig(
    val connectorId: String = "conn_" + UUID.randomUUID().toString().take(8),
    val name: String,
    val tenantOrgId: String = "estate_pinnock_01",
    val connectionType: ConnectionType,
    val endpointUrl: String = "",
    val authType: AuthType = AuthType.API_KEY,
    val apiKeyMasked: String = "gsk_live_****" + (1000..9999).random(),
    val webhookSecretMasked: String = "whsec_****" + (1000..9999).random(),
    val permissions: Set<IntegrationPermission> = setOf(IntegrationPermission.READ_VISITORS, IntegrationPermission.READ_ACCESS_EVENTS),
    val rateLimitPerMinute: Int = 120,
    val timeoutMs: Long = 5000L,
    val retryCount: Int = 3,
    val isEnabled: Boolean = true,
    val status: ConnectorStatus = ConnectorStatus.ONLINE,
    val lastSuccessEpoch: Long = System.currentTimeMillis() - (1000L * (10..300).random()),
    val lastErrorEpoch: Long? = null,
    val lastErrorMessage: String? = null,
    val totalRequestsHandled: Long = (120..24500).random().toLong(),
    val totalEventsDispatched: Long = (85..18200).random().toLong(),
    val averageLatencyMs: Long = (18..145).random().toLong()
)

/**
 * Physical or network access-control hardware device profile.
 */
data class HardwareDeviceProfile(
    val deviceId: String = "dev_" + UUID.randomUUID().toString().take(8),
    val name: String,
    val deviceType: HardwareDeviceType,
    val manufacturer: String,
    val modelNumber: String,
    val firmwareVersion: String = "v3.4.12",
    val protocol: String = "LAN/HTTPS + Wiegand",
    val ipAddress: String = "192.168.1." + (10..240).random(),
    val port: Int = 8443,
    val location: String = "Main Estate Gate - Lane A",
    val assignedGateName: String = "Gate 1 - Pinnock Beach Estate Main Gate",
    val status: ConnectorStatus = ConnectorStatus.ONLINE,
    val batteryPercent: Int? = null, // null for hardwired gate barriers
    val isRelayEnergized: Boolean = false,
    val lastHeartbeatEpoch: Long = System.currentTimeMillis() - (500L..15000L).random(),
    val supportedCapabilities: List<String> = listOf("pulse_open", "hold_open", "status_feedback", "anti_passback", "tamper_alarm"),
    val isLocalLanOnly: Boolean = true
)

/**
 * Normalized Security Event emitted by Gush Event Engine.
 */
data class GushSecurityEvent(
    val eventId: String = "evt_" + UUID.randomUUID().toString(),
    val eventType: String, // e.g. "visitor.created", "access.granted", "gate.opened", "device.offline"
    val timestamp: Long = System.currentTimeMillis(),
    val source: String, // e.g. "gush.mobile.guard", "connector.rest_api", "bridge.lan.relay"
    val actorId: String,
    val actorRole: String,
    val tenantId: String = "pinnock_estate_01",
    val propertyId: String = "villa_14b",
    val deviceId: String? = null,
    val correlationId: String = "corr_" + UUID.randomUUID().toString().take(12),
    val requestId: String = "req_" + UUID.randomUUID().toString().take(10),
    val payload: Map<String, String> = emptyMap(),
    val schemaVersion: String = "1.0.0",
    val isDeliveredToWebhooks: Boolean = true
)

/**
 * High-Integrity Security Command dispatched to hardware adapters.
 */
data class GushSecurityCommand(
    val commandId: String = "cmd_" + UUID.randomUUID().toString(),
    val commandType: String, // e.g. "OPEN_GATE", "UNLOCK_DOOR", "PULSE_RELAY", "VERIFY_CREDENTIAL"
    val targetDeviceId: String,
    val targetGateName: String,
    val actorId: String,
    val actorRole: String,
    val idempotencyKey: String = UUID.randomUUID().toString(),
    val priority: CommandPriority = CommandPriority.HIGH,
    val timeoutMs: Long = 4000L,
    val parameters: Map<String, String> = emptyMap(),
    val createdEpoch: Long = System.currentTimeMillis(),
    val executionStatus: CommandExecutionStatus = CommandExecutionStatus.PENDING,
    val resultMessage: String? = null
)

enum class CommandPriority {
    CRITICAL_EMERGENCY,
    HIGH,
    NORMAL,
    LOW_BACKGROUND
}

enum class CommandExecutionStatus {
    PENDING,
    POLICY_APPROVED,
    EXECUTING,
    SUCCESS_CONFIRMED,
    REJECTED_POLICY,
    DEVICE_OFFLINE,
    TIMEOUT,
    FAILED
}
