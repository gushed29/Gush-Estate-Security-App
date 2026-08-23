<?php

declare(strict_types=1);

namespace GushSecurity;

/**
 * Structured Security Audit and Error Logger.
 * Never logs raw passwords, secrets, or bearer tokens.
 */
final class Logger
{
    public static function log(string $level, string $message, array $context = []): void
    {
        $logFile = Config::get('LOG_FILE', dirname(__DIR__) . '/storage/logs/gush_connector.log');
        $logDir = dirname($logFile);

        if (!is_dir($logDir)) {
            @mkdir($logDir, 0755, true);
        }

        // Redact sensitive keys from context
        $safeContext = self::redactSensitiveData($context);

        $entry = [
            'timestamp' => gmdate('Y-m-d\TH:i:s\Z'),
            'level' => strtoupper($level),
            'message' => $message,
            'context' => $safeContext,
            'client_ip' => $_SERVER['REMOTE_ADDR'] ?? '127.0.0.1',
        ];

        $line = json_encode($entry, JSON_UNESCAPED_SLASHES | JSON_UNESCAPED_UNICODE) . PHP_EOL;
        @file_put_contents($logFile, $line, FILE_APPEND | LOCK_EX);
    }

    public static function info(string $message, array $context = []): void
    {
        self::log('INFO', $message, $context);
    }

    public static function warning(string $message, array $context = []): void
    {
        self::log('WARNING', $message, $context);
    }

    public static function error(string $message, array $context = []): void
    {
        self::log('ERROR', $message, $context);
    }

    private static function redactSensitiveData(array $data): array
    {
        $sensitiveKeys = [
            'password', 'secret', 'api_secret', 'token', 'authorization',
            'api_key', 'private_key', 'pin', 'card_number', 'cvv'
        ];

        $cleaned = [];
        foreach ($data as $key => $value) {
            $lowerKey = strtolower((string) $key);
            $isSensitive = false;
            foreach ($sensitiveKeys as $sens) {
                if (str_contains($lowerKey, $sens)) {
                    $isSensitive = true;
                    break;
                }
            }

            if ($isSensitive) {
                $cleaned[$key] = '***REDACTED***';
            } elseif (is_array($value)) {
                $cleaned[$key] = self::redactSensitiveData($value);
            } else {
                $cleaned[$key] = $value;
            }
        }

        return $cleaned;
    }
}
