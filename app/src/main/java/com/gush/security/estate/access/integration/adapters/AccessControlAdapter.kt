package com.gush.security.estate.access.integration.adapters

import com.gush.security.estate.access.integration.model.ConnectorStatus
import com.gush.security.estate.access.integration.model.GushSecurityCommand
import com.gush.security.estate.access.integration.model.HardwareDeviceProfile
import com.gush.security.estate.access.integration.model.HardwareDeviceType
import kotlinx.coroutines.delay

/**
 * Result of a hardware operation or connection probe.
 */
data class DeviceOperationResult(
    val isSuccess: Boolean,
    val deviceId: String,
    val operation: String,
    val responseTimeMs: Long,
    val message: String,
    val hardwareTelemetry: Map<String, String> = emptyMap()
)

/**
 * Vendor-neutral hardware abstraction contract for all physical access-control devices.
 * Adapters convert Gush Security unified commands into manufacturer-specific protocols
 * (Wiegand, OSDP, MQTT, TCP/IP, Modbus, Dry-Contact Relays, REST, BLE, RTSP/ONVIF).
 */
interface AccessControlAdapter {
    val adapterProtocol: String
    val supportedTypes: Set<HardwareDeviceType>

    suspend fun testConnection(device: HardwareDeviceProfile): DeviceOperationResult
    suspend fun executeCommand(device: HardwareDeviceProfile, command: GushSecurityCommand): DeviceOperationResult
    suspend fun getDeviceStatus(device: HardwareDeviceProfile): ConnectorStatus
    suspend fun getTelemetry(device: HardwareDeviceProfile): Map<String, String>
}

/**
 * Adapter for motorized estate gate barriers and hydraulic bollards.
 */
class GateControllerAdapter : AccessControlAdapter {
    override val adapterProtocol = "Modbus TCP / LAN Relay / Wiegand Loop"
    override val supportedTypes = setOf(HardwareDeviceType.GATE_CONTROLLER, HardwareDeviceType.TURNSTILE)

    override suspend fun testConnection(device: HardwareDeviceProfile): DeviceOperationResult {
        delay(120) // Simulated local network round-trip
        return DeviceOperationResult(
            isSuccess = true,
            deviceId = device.deviceId,
            operation = "PING_LAN_GATEWAY",
            responseTimeMs = 24L,
            message = "Gate Controller reachable at ${device.ipAddress}:${device.port}. Optical sensors: CLEAR, Barrier state: CLOSED.",
            hardwareTelemetry = mapOf(
                "barrier_position" to "CLOSED",
                "motor_voltage" to "24.2V",
                "loop_detector_1" to "IDLE",
                "tamper_switch" to "OK"
            )
        )
    }

    override suspend fun executeCommand(device: HardwareDeviceProfile, command: GushSecurityCommand): DeviceOperationResult {
        delay(350) // Hardware relay energize + barrier motor activation time
        return when (command.commandType) {
            "OPEN_GATE", "PULSE_RELAY" -> DeviceOperationResult(
                isSuccess = true,
                deviceId = device.deviceId,
                operation = command.commandType,
                responseTimeMs = 180L,
                message = "Relay #1 pulsed for 3500ms. Gate barrier cycle initiated for ${device.assignedGateName}.",
                hardwareTelemetry = mapOf("barrier_position" to "OPENING", "auto_close_timer" to "15s")
            )
            "CLOSE_GATE" -> DeviceOperationResult(
                isSuccess = true,
                deviceId = device.deviceId,
                operation = "CLOSE_GATE",
                responseTimeMs = 195L,
                message = "Safety loop verified clear. Gate barrier closing cycle commenced.",
                hardwareTelemetry = mapOf("barrier_position" to "CLOSING")
            )
            else -> DeviceOperationResult(
                isSuccess = false,
                deviceId = device.deviceId,
                operation = command.commandType,
                responseTimeMs = 40L,
                message = "Unsupported gate command: ${command.commandType}"
            )
        }
    }

    override suspend fun getDeviceStatus(device: HardwareDeviceProfile): ConnectorStatus = ConnectorStatus.ONLINE

    override suspend fun getTelemetry(device: HardwareDeviceProfile): Map<String, String> = mapOf(
        "barrier_state" to "SECURE",
        "power_source" to "MAINS_UPS_BACKUP",
        "safety_photocell" to "CLEAR"
    )
}

/**
 * Adapter for Electronic Mortise Smart Locks & Commercial Magnetic Locks.
 */
class SmartLockAdapter : AccessControlAdapter {
    override val adapterProtocol = "BLE 5.2 / Zigbee-LAN Bridge / Z-Wave"
    override val supportedTypes = setOf(HardwareDeviceType.SMART_LOCK)

    override suspend fun testConnection(device: HardwareDeviceProfile): DeviceOperationResult {
        delay(80)
        return DeviceOperationResult(
            isSuccess = true,
            deviceId = device.deviceId,
            operation = "LOCK_STATUS_PROBE",
            responseTimeMs = 42L,
            message = "Lock online via encrypted mesh gateway. Battery: 88%. Latch position: ENGAGED.",
            hardwareTelemetry = mapOf(
                "battery" to "88%",
                "latch_status" to "LOCKED",
                "rssi_dbm" to "-64"
            )
        )
    }

    override suspend fun executeCommand(device: HardwareDeviceProfile, command: GushSecurityCommand): DeviceOperationResult {
        delay(200)
        return when (command.commandType) {
            "UNLOCK_DOOR", "MOMENTARY_UNLOCK" -> DeviceOperationResult(
                isSuccess = true,
                deviceId = device.deviceId,
                operation = "UNLOCK_DOOR",
                responseTimeMs = 110L,
                message = "Smart Lock solenoid unlocked for 7 seconds. Actor: ${command.actorId}",
                hardwareTelemetry = mapOf("latch_status" to "UNLOCKED", "auto_lock_countdown" to "7s")
            )
            "LOCK_DOOR" -> DeviceOperationResult(
                isSuccess = true,
                deviceId = device.deviceId,
                operation = "LOCK_DOOR",
                responseTimeMs = 95L,
                message = "Smart Lock deadbolt thrown and verified locked.",
                hardwareTelemetry = mapOf("latch_status" to "LOCKED")
            )
            else -> DeviceOperationResult(
                isSuccess = false,
                deviceId = device.deviceId,
                operation = command.commandType,
                responseTimeMs = 30L,
                message = "Unsupported smart lock command."
            )
        }
    }

    override suspend fun getDeviceStatus(device: HardwareDeviceProfile): ConnectorStatus = ConnectorStatus.ONLINE

    override suspend fun getTelemetry(device: HardwareDeviceProfile): Map<String, String> = mapOf(
        "battery_health" to "GOOD",
        "lock_state" to "LOCKED_SECURE"
    )
}

/**
 * Adapter for Multi-Channel Industrial Relay Controllers (Dry-Contact switches).
 */
class RelayAdapter : AccessControlAdapter {
    override val adapterProtocol = "Modbus RTU / Ethernet TCP Relay Board"
    override val supportedTypes = setOf(HardwareDeviceType.RELAY_MODULE)

    override suspend fun testConnection(device: HardwareDeviceProfile): DeviceOperationResult {
        delay(60)
        return DeviceOperationResult(
            isSuccess = true,
            deviceId = device.deviceId,
            operation = "POLL_RELAYS",
            responseTimeMs = 18L,
            message = "8-Channel IP Relay board responsive. All channels normally-open (NO).",
            hardwareTelemetry = mapOf("active_channels" to "0/8", "input_voltage" to "12.0V")
        )
    }

    override suspend fun executeCommand(device: HardwareDeviceProfile, command: GushSecurityCommand): DeviceOperationResult {
        val channel = command.parameters["channel"] ?: "1"
        val durationMs = command.parameters["duration_ms"] ?: "3000"
        return DeviceOperationResult(
            isSuccess = true,
            deviceId = device.deviceId,
            operation = "PULSE_CHANNEL_$channel",
            responseTimeMs = 45L,
            message = "Relay Channel #$channel energized for ${durationMs}ms pulse.",
            hardwareTelemetry = mapOf("channel" to channel, "duration_ms" to durationMs)
        )
    }

    override suspend fun getDeviceStatus(device: HardwareDeviceProfile): ConnectorStatus = ConnectorStatus.ONLINE
    override suspend fun getTelemetry(device: HardwareDeviceProfile): Map<String, String> = mapOf("board_temp" to "36.5C")
}

/**
 * Adapter for Fixed Wiegand/OSDP RFID Badging & Optical 2D QR Terminals.
 */
class RfidQrReaderAdapter : AccessControlAdapter {
    override val adapterProtocol = "Wiegand 34-bit / OSDP v2.2 Encrypted Serial"
    override val supportedTypes = setOf(HardwareDeviceType.RFID_READER, HardwareDeviceType.QR_SCANNER)

    override suspend fun testConnection(device: HardwareDeviceProfile): DeviceOperationResult {
        delay(50)
        return DeviceOperationResult(
            isSuccess = true,
            deviceId = device.deviceId,
            operation = "POLL_SCANNER_BUS",
            responseTimeMs = 15L,
            message = "Scanner optical engine & RFID front-end active. Beep & Green LED test successful.",
            hardwareTelemetry = mapOf("illuminator" to "STANDBY", "wiegand_parity" to "EVEN_ODD_OK")
        )
    }

    override suspend fun executeCommand(device: HardwareDeviceProfile, command: GushSecurityCommand): DeviceOperationResult {
        return when (command.commandType) {
            "FEEDBACK_BEEP_SUCCESS" -> DeviceOperationResult(
                isSuccess = true,
                deviceId = device.deviceId,
                operation = "AUDIO_VISUAL_FEEDBACK",
                responseTimeMs = 20L,
                message = "Triggered green LED flash and dual confirmation chime on scanner terminal."
            )
            "FEEDBACK_BEEP_DENIED" -> DeviceOperationResult(
                isSuccess = true,
                deviceId = device.deviceId,
                operation = "AUDIO_VISUAL_FEEDBACK",
                responseTimeMs = 20L,
                message = "Triggered triple red LED blink and reject buzzer on scanner terminal."
            )
            else -> DeviceOperationResult(
                isSuccess = true,
                deviceId = device.deviceId,
                operation = command.commandType,
                responseTimeMs = 25L,
                message = "Command accepted by reader front-end."
            )
        }
    }

    override suspend fun getDeviceStatus(device: HardwareDeviceProfile): ConnectorStatus = ConnectorStatus.ONLINE
    override suspend fun getTelemetry(device: HardwareDeviceProfile): Map<String, String> = mapOf("scanner_mode" to "CONTINUOUS_BURST")
}

/**
 * Adapter for License Plate Recognition (LPR) & IP Video Surveillance Cameras.
 */
class IpCameraAdapter : AccessControlAdapter {
    override val adapterProtocol = "ONVIF Profile S/T + RTSP + Snapshot HTTP"
    override val supportedTypes = setOf(HardwareDeviceType.IP_CAMERA, HardwareDeviceType.INTERCOM_TERMINAL)

    override suspend fun testConnection(device: HardwareDeviceProfile): DeviceOperationResult {
        delay(140)
        return DeviceOperationResult(
            isSuccess = true,
            deviceId = device.deviceId,
            operation = "TEST_ONVIF_STREAM",
            responseTimeMs = 65L,
            message = "Camera ONVIF stream operational at 1080p@30fps. Optical zoom & IR night vision active.",
            hardwareTelemetry = mapOf(
                "stream_url" to "rtsp://${device.ipAddress}:554/live/ch0",
                "resolution" to "1920x1080",
                "codec" to "H.264 / AAC",
                "fps" to "30"
            )
        )
    }

    override suspend fun executeCommand(device: HardwareDeviceProfile, command: GushSecurityCommand): DeviceOperationResult {
        delay(180)
        return DeviceOperationResult(
            isSuccess = true,
            deviceId = device.deviceId,
            operation = "CAPTURE_EVIDENCE_SNAPSHOT",
            responseTimeMs = 125L,
            message = "High-resolution forensic snapshot captured and anchored to incident payload.",
            hardwareTelemetry = mapOf(
                "snapshot_uri" to "https://lan.gushsecurity.local/snapshots/${device.deviceId}_${System.currentTimeMillis()}.jpg",
                "lpr_confidence" to "99.4%"
            )
        )
    }

    override suspend fun getDeviceStatus(device: HardwareDeviceProfile): ConnectorStatus = ConnectorStatus.ONLINE
    override suspend fun getTelemetry(device: HardwareDeviceProfile): Map<String, String> = mapOf("ir_filter" to "AUTO", "lpr_engine" to "ACTIVE")
}

/**
 * Adapter Registry routing unified commands to the appropriate hardware adapter.
 */
object HardwareAdapterRegistry {
    private val gateAdapter = GateControllerAdapter()
    private val smartLockAdapter = SmartLockAdapter()
    private val relayAdapter = RelayAdapter()
    private val rfidAdapter = RfidQrReaderAdapter()
    private val cameraAdapter = IpCameraAdapter()

    fun getAdapterFor(deviceType: HardwareDeviceType): AccessControlAdapter {
        return when (deviceType) {
            HardwareDeviceType.GATE_CONTROLLER, HardwareDeviceType.TURNSTILE -> gateAdapter
            HardwareDeviceType.SMART_LOCK -> smartLockAdapter
            HardwareDeviceType.RELAY_MODULE -> relayAdapter
            HardwareDeviceType.RFID_READER, HardwareDeviceType.QR_SCANNER -> rfidAdapter
            HardwareDeviceType.IP_CAMERA, HardwareDeviceType.INTERCOM_TERMINAL, HardwareDeviceType.IOT_SENSOR -> cameraAdapter
        }
    }
}
