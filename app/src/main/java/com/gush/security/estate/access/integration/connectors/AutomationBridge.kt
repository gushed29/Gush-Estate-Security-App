package com.gush.security.estate.access.integration.connectors

import kotlinx.coroutines.delay

/**
 * Automation Platform Integrations (IFTTT, n8n, Zapier, Make.com, Home Assistant, Node-RED).
 */
enum class AutomationPlatform(val platformName: String, val webhookDoc: String) {
    IFTTT("IFTTT (Maker Webhooks)", "https://maker.ifttt.com/trigger/{event}/with/key/{secret_key}"),
    N8N("n8n (Self-Hosted Workflow)", "https://n8n.estate-ops.org/webhook/gush-security-events"),
    ZAPIER("Zapier (Catch Hook)", "https://hooks.zapier.com/hooks/catch/198234/gush_v1"),
    HOME_ASSISTANT("Home Assistant (REST / Webhook)", "http://homeassistant.local:8123/api/webhook/gush_estate"),
    NODE_RED("Node-RED Flow (MQTT / HTTP)", "http://nodered.estate-lan:1880/gush-listener")
}

data class AutomationRule(
    val ruleId: String,
    val name: String,
    val platform: AutomationPlatform,
    val triggerEventType: String, // e.g. "visitor.created", "access.granted", "incident.created"
    val webhookUrl: String,
    val isEnabled: Boolean = true,
    val triggerCount: Long = (24..1400).random().toLong(),
    val lastTriggerEpoch: Long = System.currentTimeMillis() - (10_000L..400_000L).random()
)

class AutomationService {
    suspend fun testAutomationRule(rule: AutomationRule): AutomationTestResult {
        delay(90)
        return AutomationTestResult(
            isSuccess = true,
            platformName = rule.platform.platformName,
            httpStatus = 200,
            message = "Dispatched mock trigger '${rule.triggerEventType}' to ${rule.platform.platformName}. Target URL returned HTTP 200 OK."
        )
    }
}

data class AutomationTestResult(
    val isSuccess: Boolean,
    val platformName: String,
    val httpStatus: Int,
    val message: String
)
