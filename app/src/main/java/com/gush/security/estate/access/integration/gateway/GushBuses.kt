package com.gush.security.estate.access.integration.gateway

import com.gush.security.estate.access.integration.adapters.DeviceOperationResult
import com.gush.security.estate.access.integration.adapters.HardwareAdapterRegistry
import com.gush.security.estate.access.integration.model.CommandExecutionStatus
import com.gush.security.estate.access.integration.model.GushSecurityCommand
import com.gush.security.estate.access.integration.model.GushSecurityEvent
import com.gush.security.estate.access.integration.model.HardwareDeviceProfile
import com.gush.security.estate.access.integration.model.IntegrationConnectorConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.ConcurrentLinkedQueue

/**
 * Normalized High-Throughput Event Bus for Gush Security Hub.
 * Handles event distribution, webhook fan-out, retry queues, and audit persistence.
 */
class GushEventBus {

    private val _eventsFlow = MutableSharedFlow<GushSecurityEvent>(extraBufferCapacity = 64)
    val eventsFlow: SharedFlow<GushSecurityEvent> = _eventsFlow.asSharedFlow()

    private val _recentEvents = MutableStateFlow<List<GushSecurityEvent>>(emptyList())
    val recentEvents: StateFlow<List<GushSecurityEvent>> = _recentEvents.asStateFlow()

    private val deadLetterQueue = ConcurrentLinkedQueue<GushSecurityEvent>()

    suspend fun publishEvent(event: GushSecurityEvent) {
        val updated = (_recentEvents.value + event).takeLast(100)
        _recentEvents.value = updated
        _eventsFlow.emit(event)
    }

    fun publishEventSync(event: GushSecurityEvent) {
        val updated = (_recentEvents.value + event).takeLast(100)
        _recentEvents.value = updated
        _eventsFlow.tryEmit(event)
    }

    fun getDeadLetterQueue(): List<GushSecurityEvent> = deadLetterQueue.toList()

    fun moveToDeadLetter(event: GushSecurityEvent) {
        deadLetterQueue.add(event)
    }
}

/**
 * Secure Command Bus routing physical access operations through the Policy Engine & Hardware Adapters.
 */
class GushCommandBus(
    private val policyEngine: SecurityPolicyEngine,
    private val eventBus: GushEventBus
) {
    private val _recentCommands = MutableStateFlow<List<GushSecurityCommand>>(emptyList())
    val recentCommands: StateFlow<List<GushSecurityCommand>> = _recentCommands.asStateFlow()

    /**
     * Executes a security command with full policy validation, adapter dispatching, and audit event emission.
     */
    suspend fun dispatchCommand(
        command: GushSecurityCommand,
        targetDevice: HardwareDeviceProfile?,
        connector: IntegrationConnectorConfig? = null
    ): DeviceOperationResult {
        // 1. Evaluate Security Policy
        val policyDecision = policyEngine.evaluateCommandPolicy(connector, command)
        if (policyDecision is PolicyEvaluationResult.Denied) {
            val rejectedCmd = command.copy(
                executionStatus = CommandExecutionStatus.REJECTED_POLICY,
                resultMessage = "${policyDecision.rejectionCode}: ${policyDecision.violationReason}"
            )
            _recentCommands.value = (_recentCommands.value + rejectedCmd).takeLast(100)

            // Emit Audit Event
            eventBus.publishEvent(
                GushSecurityEvent(
                    eventType = "security.command.rejected",
                    source = connector?.name ?: "gush.internal.command_bus",
                    actorId = command.actorId,
                    actorRole = command.actorRole,
                    deviceId = command.targetDeviceId,
                    payload = mapOf(
                        "command_type" to command.commandType,
                        "rejection_code" to policyDecision.rejectionCode,
                        "reason" to policyDecision.violationReason
                    )
                )
            )

            return DeviceOperationResult(
                isSuccess = false,
                deviceId = command.targetDeviceId,
                operation = command.commandType,
                responseTimeMs = 12L,
                message = "Command Denied by Security Policy: ${policyDecision.violationReason}"
            )
        }

        // 2. Hardware Validation
        if (targetDevice == null) {
            val failedCmd = command.copy(
                executionStatus = CommandExecutionStatus.DEVICE_OFFLINE,
                resultMessage = "Target device '${command.targetDeviceId}' not found in hardware registry."
            )
            _recentCommands.value = (_recentCommands.value + failedCmd).takeLast(100)
            return DeviceOperationResult(
                isSuccess = false,
                deviceId = command.targetDeviceId,
                operation = command.commandType,
                responseTimeMs = 10L,
                message = "Device not found in registry."
            )
        }

        // 3. Dispatch to Hardware Adapter
        val adapter = HardwareAdapterRegistry.getAdapterFor(targetDevice.deviceType)
        val result = try {
            adapter.executeCommand(targetDevice, command)
        } catch (e: Exception) {
            DeviceOperationResult(
                isSuccess = false,
                deviceId = targetDevice.deviceId,
                operation = command.commandType,
                responseTimeMs = 50L,
                message = "Hardware communication error: ${e.localizedMessage}"
            )
        }

        // 4. Update Command Execution State
        val finalStatus = if (result.isSuccess) CommandExecutionStatus.SUCCESS_CONFIRMED else CommandExecutionStatus.FAILED
        val completedCmd = command.copy(
            executionStatus = finalStatus,
            resultMessage = result.message
        )
        _recentCommands.value = (_recentCommands.value + completedCmd).takeLast(100)

        // 5. Emit Audit & Gate Actuation Events
        val eventType = if (result.isSuccess) {
            when (command.commandType) {
                "OPEN_GATE" -> "gate.opened"
                "CLOSE_GATE" -> "gate.closed"
                "UNLOCK_DOOR" -> "door.unlocked"
                else -> "device.command.executed"
            }
        } else {
            "device.command.failed"
        }

        eventBus.publishEvent(
            GushSecurityEvent(
                eventType = eventType,
                source = connector?.name ?: "gush.hardware.bus",
                actorId = command.actorId,
                actorRole = command.actorRole,
                deviceId = targetDevice.deviceId,
                payload = mapOf(
                    "command" to command.commandType,
                    "gate" to command.targetGateName,
                    "status" to if (result.isSuccess) "SUCCESS" else "FAILURE",
                    "device_ip" to targetDevice.ipAddress,
                    "response_ms" to result.responseTimeMs.toString(),
                    "message" to result.message
                )
            )
        )

        return result
    }
}
