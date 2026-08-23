<?php

declare(strict_types=1);

namespace GushSecurity;

use PDO;
use PDOException;

/**
 * Authentication and Request Verification Engine.
 * Validates API Keys, HMAC-SHA256 Signatures, Request IDs, and Replay Protection.
 */
final class Auth
{
    /**
     * Authenticates incoming request from Gush Security Hub or client.
     */
    public static function authenticate(bool $requireSignature = true): bool
    {
        $headers = self::getRequestHeaders();
        
        $apiKey = self::extractBearerToken($headers);
        $expectedApiKey = (string) Config::get('GUSH_API_KEY', '');
        $expectedSecret = (string) Config::get('GUSH_API_SECRET', '');

        // 1. API Key Validation
        if (empty($expectedApiKey)) {
            Logger::error("Authentication failed: GUSH_API_KEY is not configured on server");
            Response::error('SERVER_CONFIG_ERROR', 'Server API credentials not configured', 500);
        }

        if (empty($apiKey) || !hash_equals($expectedApiKey, $apiKey)) {
            Logger::warning("Unauthorized request attempt with invalid API key");
            Response::error('UNAUTHORIZED', 'Invalid or missing API key', 401);
        }

        if (!$requireSignature) {
            return true;
        }

        // 2. Signature and Anti-Replay Validation
        $timestamp = $headers['x-gush-timestamp'] ?? null;
        $requestId = $headers['x-gush-request-id'] ?? null;
        $signature = $headers['x-gush-signature'] ?? null;

        if (!$timestamp || !$requestId || !$signature) {
            Response::error('MISSING_SIGNATURE_HEADERS', 'Missing required X-Gush-Timestamp, X-Gush-Request-Id, or X-Gush-Signature headers', 401);
        }

        $rawBody = file_get_contents('php://input') ?: '';
        $method = $_SERVER['REQUEST_METHOD'] ?? 'GET';
        $path = $_SERVER['REQUEST_URI'] ?? '/';
        $allowedDrift = (int) Config::get('GUSH_ALLOWED_DRIFT_SECONDS', 300);

        $isValid = Signature::verify(
            httpMethod: $method,
            requestPath: $path,
            timestamp: $timestamp,
            requestId: $requestId,
            rawBody: $rawBody,
            providedSignature: $signature,
            apiSecret: $expectedSecret,
            allowedDriftSeconds: $allowedDrift
        );

        if (!$isValid) {
            Logger::warning("Invalid HMAC signature received for request: {$requestId}");
            Response::error('INVALID_SIGNATURE', 'Request signature verification failed or timestamp expired', 401, $requestId);
        }

        // 3. Prevent Request ID Replay
        self::recordAndVerifyRequestId($requestId, $path);

        return true;
    }

    /**
     * Checks if a request ID has already been processed to prevent replay.
     */
    public static function recordAndVerifyRequestId(string $requestId, string $endpoint): void
    {
        $pdo = Database::getConnection();
        if ($pdo === null) {
            return; // If DB is not available, allow pass-through in stateless mode
        }

        try {
            $stmt = $pdo->prepare("SELECT COUNT(*) FROM gush_request_ids WHERE request_id = :req_id");
            $stmt->execute([':req_id' => $requestId]);
            if ((int) $stmt->fetchColumn() > 0) {
                Logger::warning("Duplicate request ID detected: {$requestId}");
                Response::error('DUPLICATE_REQUEST', 'This request has already been processed (Replay protection)', 409, $requestId);
            }

            $insertStmt = $pdo->prepare("INSERT INTO gush_request_ids (request_id, endpoint, client_ip) VALUES (:req_id, :ep, :ip)");
            $insertStmt->execute([
                ':req_id' => $requestId,
                ':ep' => substr($endpoint, 0, 255),
                ':ip' => $_SERVER['REMOTE_ADDR'] ?? '127.0.0.1',
            ]);
        } catch (PDOException $e) {
            Logger::error("Request ID ledger check error: " . $e->getMessage());
        }
    }

    public static function getRequestHeaders(): array
    {
        $headers = [];
        foreach ($_SERVER as $key => $value) {
            if (str_starts_with($key, 'HTTP_')) {
                $headerName = strtolower(str_replace('_', '-', substr($key, 5)));
                $headers[$headerName] = $value;
            }
        }
        if (isset($_SERVER['CONTENT_TYPE'])) {
            $headers['content-type'] = $_SERVER['CONTENT_TYPE'];
        }
        if (isset($_SERVER['AUTHORIZATION'])) {
            $headers['authorization'] = $_SERVER['AUTHORIZATION'];
        }
        return $headers;
    }

    private static function extractBearerToken(array $headers): ?string
    {
        $auth = $headers['authorization'] ?? '';
        if (preg_match('/Bearer\s+(\S+)/i', $auth, $matches)) {
            return $matches[1];
        }
        return $headers['x-gush-api-key'] ?? null;
    }
}
