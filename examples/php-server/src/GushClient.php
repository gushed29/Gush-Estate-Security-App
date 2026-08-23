<?php

declare(strict_types=1);

namespace GushSecurity;

use Exception;

/**
 * High-Performance Outbound Gush Security API Client.
 * Connects PHP backend to Gush Hub / Gateway (e.g., https://api.sstore.ng/api/gsecurity/api-access).
 * Handles HMAC-SHA256 signature generation, anti-replay timestamps, timeouts, and JSON marshalling.
 */
final class GushClient
{
    private string $baseUrl;
    private string $apiKey;
    private string $apiSecret;
    private string $estateId;
    private int $timeoutSeconds;

    public function __construct(array $options = [])
    {
        $this->baseUrl = rtrim((string)($options['base_url'] ?? Config::get('GUSH_HUB_URL', 'https://api.sstore.ng/api/gsecurity/api-access')), '/');
        $this->apiKey = (string)($options['api_key'] ?? Config::get('GUSH_API_KEY', ''));
        $this->apiSecret = (string)($options['api_secret'] ?? Config::get('GUSH_API_SECRET', ''));
        $this->estateId = (string)($options['estate_id'] ?? Config::get('GUSH_ESTATE_ID', 'pinnock_estate_01'));
        $this->timeoutSeconds = (int)($options['timeout'] ?? 10);
    }

    /**
     * Probes Gush Hub Gateway Health & Capabilities.
     */
    public function health(): array
    {
        return $this->sendRequest('GET', '/health');
    }

    /**
     * Discovers Gush Hub capabilities.
     */
    public function capabilities(): array
    {
        return $this->sendRequest('GET', '/capabilities');
    }

    /**
     * Creates a new Visitor Access Pass in Gush Hub.
     */
    public function createVisitor(array $visitorData): array
    {
        return $this->sendRequest('POST', '/visitor/passes', [
            'visitor_name' => $visitorData['visitor_name'],
            'visitor_phone' => $visitorData['visitor_phone'] ?? '',
            'host_resident_name' => $visitorData['host_name'] ?? 'Resident Host',
            'property_unit' => $visitorData['property_unit'],
            'pass_type' => $visitorData['pass_type'] ?? 'GUEST',
            'valid_from' => $visitorData['valid_from'] ?? gmdate('Y-m-d\TH:i:s\Z'),
            'valid_until' => $visitorData['valid_until'] ?? gmdate('Y-m-d\TH:i:s\Z', time() + 86400),
            'notes' => $visitorData['notes'] ?? 'Generated from PHP Portal'
        ]);
    }

    /**
     * Verifies an access pass via QR token or 6-digit passcode.
     */
    public function verifyVisitor(string $passcodeOrQrToken, string $gateCode = 'MAIN_GATE', ?string $deviceId = null): array
    {
        $payload = [
            'gate_code' => $gateCode,
            'device_id' => $deviceId ?? 'php_server_verifier'
        ];

        if (strlen($passcodeOrQrToken) <= 8 && ctype_digit($passcodeOrQrToken)) {
            $payload['passcode'] = $passcodeOrQrToken;
        } else {
            $payload['qr_token'] = $passcodeOrQrToken;
        }

        return $this->sendRequest('POST', '/access/verify', $payload);
    }

    /**
     * Revokes an existing visitor pass.
     */
    public function revokeVisitor(string $passId, string $reason = 'Revoked by host'): array
    {
        return $this->sendRequest('POST', "/visitor/passes/{$passId}/revoke", [
            'reason' => $reason
        ]);
    }

    /**
     * Fetches registered hardware devices from Gush Hub.
     */
    public function getDevices(): array
    {
        return $this->sendRequest('GET', '/devices');
    }

    /**
     * Fetches real-time status for a specific hardware device.
     */
    public function getDeviceStatus(string $deviceId): array
    {
        return $this->sendRequest('GET', "/devices/{$deviceId}/status");
    }

    /**
     * Executes a physical access control command with Idempotency Key protection.
     */
    public function executeDeviceCommand(
        string $deviceId,
        string $commandType,
        array $parameters = [],
        ?string $idempotencyKey = null
    ): array {
        $key = $idempotencyKey ?? ('idemp_' . bin2hex(random_bytes(10)));
        return $this->sendRequest(
            method: 'POST',
            endpoint: "/devices/{$deviceId}/command",
            payload: [
                'command_type' => $commandType,
                'parameters' => $parameters,
            ],
            customHeaders: [
                'Idempotency-Key' => $key
            ]
        );
    }

    /**
     * Publishes a security event to Gush Hub Event Bus.
     */
    public function sendEvent(string $eventType, array $payloadData, ?string $propertyId = null): array
    {
        $eventId = 'evt_' . bin2hex(random_bytes(8));
        return $this->sendRequest('POST', '/events', [
            'event_id' => $eventId,
            'event_type' => $eventType,
            'estate_id' => $this->estateId,
            'property_id' => $propertyId,
            'timestamp' => gmdate('Y-m-d\TH:i:s\Z'),
            'data' => $payloadData
        ]);
    }

    /**
     * Core HTTP Transport via cURL with HMAC Signature and Error Handling.
     */
    private function sendRequest(
        string $method,
        string $endpoint,
        ?array $payload = null,
        array $customHeaders = []
    ): array {
        $url = $this->baseUrl . $endpoint;
        $timestamp = time();
        $requestId = 'req_' . bin2hex(random_bytes(8));
        $rawBody = $payload !== null ? json_encode($payload, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE) : '';

        // Compute HMAC-SHA256 Signature
        $signature = Signature::calculate(
            httpMethod: $method,
            requestPath: $endpoint,
            timestamp: $timestamp,
            requestId: $requestId,
            rawBody: $rawBody,
            apiSecret: $this->apiSecret
        );

        $headers = [
            "Authorization: Bearer {$this->apiKey}",
            "X-Estate-Id: {$this->estateId}",
            "X-Gush-Timestamp: {$timestamp}",
            "X-Gush-Request-Id: {$requestId}",
            "X-Gush-Signature: {$signature}",
            "Content-Type: application/json",
            "Accept: application/json",
            "User-Agent: GushSecurity-PHP-Connector/1.0.0"
        ];

        foreach ($customHeaders as $hKey => $hVal) {
            $headers[] = "{$hKey}: {$hVal}";
        }

        $ch = curl_init();
        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_TIMEOUT, $this->timeoutSeconds);
        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, true);
        curl_setopt($ch, CURLOPT_SSL_VERIFYHOST, 2);

        if ($method === 'POST') {
            curl_setopt($ch, CURLOPT_POST, true);
            curl_setopt($ch, CURLOPT_POSTFIELDS, $rawBody);
        } elseif ($method === 'PUT' || $method === 'DELETE') {
            curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
            if ($rawBody !== '') {
                curl_setopt($ch, CURLOPT_POSTFIELDS, $rawBody);
            }
        }

        $responseBody = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curlError = curl_error($ch);
        curl_close($ch);

        if ($responseBody === false) {
            Logger::error("Gush API connection failed", [
                'url' => $url,
                'curl_error' => $curlError
            ]);
            throw new Exception("Unable to communicate with Gush Security Gateway at {$url}: {$curlError}");
        }

        $decoded = json_decode($responseBody, true);
        if (!is_array($decoded)) {
            Logger::error("Invalid JSON from Gush API", [
                'http_code' => $httpCode,
                'response' => $responseBody
            ]);
            throw new Exception("Invalid JSON response from Gush Security Gateway (HTTP {$httpCode})");
        }

        if ($httpCode >= 400) {
            $errCode = $decoded['error']['code'] ?? 'UPSTREAM_API_ERROR';
            $errMsg = $decoded['error']['message'] ?? "HTTP Error {$httpCode}";
            Logger::warning("Gush API returned error", ['code' => $errCode, 'message' => $errMsg]);
            throw new Exception("[{$errCode}] {$errMsg}", $httpCode);
        }

        return $decoded;
    }
}
