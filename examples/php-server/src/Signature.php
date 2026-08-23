<?php

declare(strict_types=1);

namespace GushSecurity;

/**
 * Cryptographic HMAC-SHA256 Signature Generator and Validator.
 * Protects webhook payloads and REST calls against tampering and replay attacks.
 */
final class Signature
{
    /**
     * Builds standard signature payload string.
     */
    public static function buildSignaturePayload(
        string $httpMethod,
        string $requestPath,
        int|string $timestamp,
        string $requestId,
        string $rawBody
    ): string {
        $bodyHash = hash('sha256', $rawBody);
        $normalizedPath = parse_url($requestPath, PHP_URL_PATH) ?? $requestPath;
        
        return strtoupper($httpMethod) . "\n"
            . $normalizedPath . "\n"
            . $timestamp . "\n"
            . $requestId . "\n"
            . $bodyHash;
    }

    /**
     * Computes HMAC-SHA256 signature for a request.
     */
    public static function calculate(
        string $httpMethod,
        string $requestPath,
        int|string $timestamp,
        string $requestId,
        string $rawBody,
        string $apiSecret
    ): string {
        $payload = self::buildSignaturePayload($httpMethod, $requestPath, $timestamp, $requestId, $rawBody);
        return 'sha256=' . hash_hmac('sha256', $payload, $apiSecret);
    }

    /**
     * Verifies signature in constant time with drift and replay protection.
     */
    public static function verify(
        string $httpMethod,
        string $requestPath,
        int|string $timestamp,
        string $requestId,
        string $rawBody,
        string $providedSignature,
        string $apiSecret,
        int $allowedDriftSeconds = 300
    ): bool {
        // 1. Anti-Replay Timestamp Drift Check
        $currentTime = time();
        $reqTime = (int) $timestamp;

        if (abs($currentTime - $reqTime) > $allowedDriftSeconds) {
            Logger::warning("Signature rejected: timestamp drift too high", [
                'current_time' => $currentTime,
                'request_time' => $reqTime,
                'drift_seconds' => abs($currentTime - $reqTime)
            ]);
            return false;
        }

        // 2. Compute expected signature
        $expectedSignature = self::calculate(
            $httpMethod,
            $requestPath,
            $timestamp,
            $requestId,
            $rawBody,
            $apiSecret
        );

        // 3. Constant-time comparison to prevent timing attacks
        return hash_equals($expectedSignature, $providedSignature);
    }
}
