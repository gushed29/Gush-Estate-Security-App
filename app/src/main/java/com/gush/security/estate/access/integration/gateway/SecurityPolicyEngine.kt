package com.gush.security.estate.access.integration.gateway

import com.gush.security.estate.access.integration.model.GushSecurityCommand
import com.gush.security.estate.access.integration.model.GushSecurityEvent
import com.gush.security.estate.access.integration.model.IntegrationConnectorConfig
import com.gush.security.estate.access.integration.model.IntegrationPermission
import java.util.concurrent.ConcurrentHashMap

/**
 * Policy Evaluation Outcome returned by Gush Authoritative Security Policy Engine.
 */
sealed class PolicyEvaluationResult {
    data class Allowed(val policyReason: String, val evaluatedAtEpoch: Long = System.currentTimeMillis()) : PolicyEvaluationResult()
    data class Denied(val rejectionCode: String, val violationReason: String) : PolicyEvaluationResult()
}

/**
 * The Authoritative Security Policy Engine.
 *
 * CRITICAL SECURITY PRINCIPLE:
 * External systems (APIs, webhooks, automation platforms, external DBs) NEVER bypass the security policy.
 * Having a valid API token grants entry to the integration pipeline, but NOT direct hardware actuation.
 * Every command must pass tenant scoping, permission checks, safety loop verification, rate limits,
 * and anti-replay idempotency enforcement.
 */
class SecurityPolicyEngine {

    // Idempotency cache mapping idempotency keys to previous results (prevents duplicate physical actuation)
    private val idempotencyCache = ConcurrentHashMap<String, Long>()
    
    // Rate limit window tracker: connectorId -> list of request timestamps
    private val rateLimitTracker = ConcurrentHashMap<String, MutableList<Long>>()

    // System-wide emergency lockdown status
    private var isEstateUnderEmergencyLockdown = false

    fun setEmergencyLockdown(active: Boolean) {
        isEstateUnderEmergencyLockdown = active
    }

    fun isEmergencyLockdownActive(): Boolean = isEstateUnderEmergencyLockdown

    /**
     * Evaluates whether an external connector is permitted to execute a given command on physical hardware.
     */
    fun evaluateCommandPolicy(
        connector: IntegrationConnectorConfig?,
        command: GushSecurityCommand,
        tenantId: String = "estate_pinnock_01"
    ): PolicyEvaluationResult {
        val now = System.currentTimeMillis()

        // 1. Emergency Lockdown Check
        if (isEstateUnderEmergencyLockdown && command.actorRole != "ADMIN_SUPERVISOR") {
            return PolicyEvaluationResult.Denied(
                rejectionCode = "ERR_ESTATE_EMERGENCY_LOCKDOWN",
                violationReason = "Estate is currently under Active Emergency Lockdown. Physical hardware remote commands are disabled except for estate supervisors."
            )
        }

        // 2. Connector Enabled Status Check
        if (connector != null && !connector.isEnabled) {
            return PolicyEvaluationResult.Denied(
                rejectionCode = "ERR_CONNECTOR_DISABLED",
                violationReason = "Connector '${connector.name}' (${connector.connectorId}) is disabled by Estate Administrator."
            )
        }

        // 3. Multi-Tenant Scoping Boundary Enforcement
        if (connector != null && connector.tenantOrgId != tenantId) {
            return PolicyEvaluationResult.Denied(
                rejectionCode = "ERR_TENANT_ISOLATION_VIOLATION",
                violationReason = "Connector tenant scope '${connector.tenantOrgId}' does not match target estate '$tenantId'."
            )
        }

        // 4. Rate Limiting Check
        if (connector != null) {
            val timestamps = rateLimitTracker.computeIfAbsent(connector.connectorId) { mutableListOf() }
            synchronized(timestamps) {
                // Prune timestamps older than 60 seconds
                timestamps.removeAll { now - it > 60_000L }
                if (timestamps.size >= connector.rateLimitPerMinute) {
                    return PolicyEvaluationResult.Denied(
                        rejectionCode = "ERR_RATE_LIMIT_EXCEEDED",
                        violationReason = "Connector '${connector.name}' exceeded rate limit (${connector.rateLimitPerMinute} req/min). Throttle active."
                    )
                }
                timestamps.add(now)
            }
        }

        // 5. Anti-Replay Idempotency Enforcement
        if (command.idempotencyKey.isNotBlank()) {
            val previousExecution = idempotencyCache[command.idempotencyKey]
            if (previousExecution != null && (now - previousExecution) < 300_000L) { // 5-minute idempotency window
                return PolicyEvaluationResult.Denied(
                    rejectionCode = "ERR_IDEMPOTENT_REPLAY_IGNORED",
                    violationReason = "Duplicate command with Idempotency Key '${command.idempotencyKey}' detected. Physical actuation skipped."
                )
            }
            idempotencyCache[command.idempotencyKey] = now
        }

        // 6. Granular Permission & Least Privilege Verification
        if (connector != null) {
            val requiredPermission = when (command.commandType) {
                "OPEN_GATE" -> IntegrationPermission.OPEN_GATE
                "UNLOCK_DOOR" -> IntegrationPermission.UNLOCK_DOOR
                "GRANT_ACCESS" -> IntegrationPermission.GRANT_ACCESS
                "MANAGE_DEVICES" -> IntegrationPermission.MANAGE_DEVICES
                else -> IntegrationPermission.READ_ACCESS_EVENTS
            }

            if (!connector.permissions.contains(requiredPermission)) {
                return PolicyEvaluationResult.Denied(
                    rejectionCode = "ERR_UNAUTHORIZED_PERMISSION",
                    violationReason = "Connector '${connector.name}' lacks required permission: ${requiredPermission.label} (${requiredPermission.name})."
                )
            }
        }

        // 7. High-Risk Physical Safety Policy
        if (command.commandType == "OPEN_GATE" || command.commandType == "UNLOCK_DOOR") {
            if (command.targetDeviceId.isBlank()) {
                return PolicyEvaluationResult.Denied(
                    rejectionCode = "ERR_MISSING_TARGET_DEVICE",
                    violationReason = "High-risk command requires an explicit, registered hardware target device ID."
                )
            }
        }

        return PolicyEvaluationResult.Allowed(
            policyReason = "All security boundary checks, tenant isolation, rate limits, and permission constraints verified successfully."
        )
    }

    /**
     * Evaluates whether an inbound event from a webhook/database is permitted to be ingested.
     */
    fun evaluateEventIngestionPolicy(
        connector: IntegrationConnectorConfig?,
        event: GushSecurityEvent
    ): PolicyEvaluationResult {
        if (connector != null && !connector.isEnabled) {
            return PolicyEvaluationResult.Denied(
                rejectionCode = "ERR_CONNECTOR_DISABLED",
                violationReason = "Event source connector is disabled."
            )
        }

        // Verify event schema version
        if (event.schemaVersion != "1.0.0") {
            return PolicyEvaluationResult.Denied(
                rejectionCode = "ERR_UNSUPPORTED_SCHEMA_VERSION",
                violationReason = "Event schema version '${event.schemaVersion}' is incompatible with Gush Engine v1.0.0."
            )
        }

        return PolicyEvaluationResult.Allowed("Event validated for ingestion into Gush Security Event Ledger.")
    }
}
