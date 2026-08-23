<?php

declare(strict_types=1);

namespace GushSecurity;

use PDO;
use PDOException;

/**
 * Robust Database Connection Layer with Prepared Statements and Transaction Support.
 * Supports MySQL, MariaDB, PostgreSQL, and SQLite.
 */
final class Database
{
    private static ?PDO $pdo = null;

    public static function getConnection(): ?PDO
    {
        if (self::$pdo !== null) {
            return self::$pdo;
        }

        $connection = strtolower((string) Config::get('DB_CONNECTION', 'mysql'));
        
        try {
            if ($connection === 'sqlite') {
                $dbPath = (string) Config::get('DB_DATABASE', dirname(__DIR__) . '/storage/gush.sqlite');
                self::$pdo = new PDO("sqlite:{$dbPath}", null, null, [
                    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::ATTR_EMULATE_PREPARES => false,
                ]);
                return self::$pdo;
            }

            if ($connection === 'pgsql' || $connection === 'postgres' || $connection === 'postgresql') {
                $host = Config::get('DB_HOST', '127.0.0.1');
                $port = Config::get('DB_PORT', '5432');
                $dbName = Config::get('DB_DATABASE', 'gush_security_db');
                $user = Config::get('DB_USERNAME', 'postgres');
                $pass = Config::get('DB_PASSWORD', '');

                $dsn = "pgsql:host={$host};port={$port};dbname={$dbName}";
                self::$pdo = new PDO($dsn, (string)$user, (string)$pass, [
                    PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                    PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    PDO::ATTR_EMULATE_PREPARES => false,
                ]);
                return self::$pdo;
            }

            // Default to MySQL / MariaDB
            $host = Config::get('DB_HOST', '127.0.0.1');
            $port = Config::get('DB_PORT', '3306');
            $dbName = Config::get('DB_DATABASE', 'gush_security_db');
            $user = Config::get('DB_USERNAME', 'root');
            $pass = Config::get('DB_PASSWORD', '');
            $charset = Config::get('DB_CHARSET', 'utf8mb4');

            $dsn = "mysql:host={$host};port={$port};dbname={$dbName};charset={$charset}";
            self::$pdo = new PDO($dsn, (string)$user, (string)$pass, [
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES => false,
                PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES {$charset}",
            ]);

            return self::$pdo;
        } catch (PDOException $e) {
            Logger::error("Database connection failure: " . $e->getMessage());
            return null;
        }
    }

    public static function hasDatabase(): bool
    {
        return self::getConnection() !== null;
    }
}
