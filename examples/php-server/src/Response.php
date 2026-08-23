<?php

declare(strict_types=1);

namespace GushSecurity;

/**
 * Standardized JSON API Response Builder.
 * Ensures consistent output format across all endpoints.
 */
final class Response
{
    public static function json(
        mixed $data,
        int $statusCode = 200,
        ?string $requestId = null,
        array $extraHeaders = []
    ): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=UTF-8');
        header('X-Content-Type-Options: nosniff');
        header('X-Frame-Options: DENY');
        header('X-XSS-Protection: 1; mode=block');

        foreach ($extraHeaders as $name => $value) {
            header("{$name}: {$value}");
        }

        $payload = [
            'success' => $statusCode >= 200 && $statusCode < 300,
            'status_code' => $statusCode,
            'request_id' => $requestId ?? ('req_' . bin2hex(random_bytes(8))),
            'timestamp' => gmdate('Y-m-d\TH:i:s\Z'),
            'data' => $data,
        ];

        echo json_encode($payload, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);
        exit;
    }

    public static function error(
        string $errorCode,
        string $errorMessage,
        int $statusCode = 400,
        ?string $requestId = null,
        array $details = []
    ): void {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=UTF-8');
        header('X-Content-Type-Options: nosniff');

        $payload = [
            'success' => false,
            'status_code' => $statusCode,
            'request_id' => $requestId ?? ('err_' . bin2hex(random_bytes(8))),
            'timestamp' => gmdate('Y-m-d\TH:i:s\Z'),
            'error' => [
                'code' => $errorCode,
                'message' => $errorMessage,
                'details' => $details,
            ],
        ];

        echo json_encode($payload, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE);
        exit;
    }
}
