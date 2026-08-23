<?php

declare(strict_types=1);

namespace GushSecurity;

/**
 * Environment and Global Configuration Handler.
 * Supports .env files, server environment variables, and fallback defaults.
 */
final class Config
{
    private static array $config = [];
    private static bool $loaded = false;

    public static function load(?string $envPath = null): void
    {
        if (self::$loaded) {
            return;
        }

        $envFile = $envPath ?? dirname(__DIR__) . '/.env';
        if (file_exists($envFile)) {
            $lines = file($envFile, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
            foreach ($lines as $line) {
                $line = trim($line);
                if ($line === '' || str_starts_with($line, '#')) {
                    continue;
                }
                if (str_contains($line, '=')) {
                    [$key, $value] = explode('=', $line, 2);
                    $key = trim($key);
                    $value = trim($value, " \t\n\r\0\x0B\"'");
                    self::$config[$key] = $value;
                    if (getenv($key) === false) {
                        putenv("{$key}={$value}");
                        $_ENV[$key] = $value;
                        $_SERVER[$key] = $value;
                    }
                }
            }
        }

        self::$loaded = true;
    }

    public static function get(string $key, mixed $default = null): mixed
    {
        self::load();

        if (isset(self::$config[$key])) {
            return self::$config[$key];
        }

        $env = getenv($key);
        if ($env !== false) {
            return $env;
        }

        return $_ENV[$key] ?? $_SERVER[$key] ?? $default;
    }

    public static function isProduction(): bool
    {
        return strtolower((string) self::get('APP_ENV', 'production')) === 'production';
    }

    public static function getAll(): array
    {
        self::load();
        return self::$config;
    }
}
