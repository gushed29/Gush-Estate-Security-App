<?php

declare(strict_types=1);

namespace GushSecurity;

use PDO;
use PDOException;
use Exception;

/**
 * Hardware Device Query and Actuation Service.
 * Allows web apps to discover gate controllers, smart locks, relays, and cameras.
 */
final class DeviceService
{
    private ?PDO $pdo;
    private GushClient $client;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
        $this->client = new GushClient();
    }

    /**
     * Retrieves enrolled hardware devices.
     */
    public function listDevices(): array
    {
        if ($this->pdo !== null) {
            try {
                $stmt = $this->pdo->query("SELECT * FROM gush_devices ORDER BY name ASC");
                $devices = $stmt->fetchAll();
                if (!empty($devices)) {
                    return $devices;
                }
            } catch (PDOException $e) {
                Logger::warning("Could not load devices from DB: " . $e->getMessage());
            }
        }

        // Fetch from Hub
        try {
            $result = $this->client->getDevices();
            return $result['data'] ?? [];
        } catch (Exception $e) {
            Logger::error("Upstream device fetch failed: " . $e->getMessage());
            return [];
        }
    }

    /**
     * Executes an authorized hardware actuation command (Open Gate, Unlock Door, Pulse Relay).
     */
    public function sendCommand(string $deviceId, string $commandType, array $params = []): array
    {
        $allowedCommands = ['OPEN_GATE', 'CLOSE_GATE', 'HOLD_OPEN', 'UNLOCK_DOOR', 'LOCK_DOOR', 'PULSE_RELAY', 'TEST_PING'];
        if (!in_array($commandType, $allowedCommands, true)) {
            Response::error('INVALID_COMMAND', "Command '{$commandType}' is not supported or permitted", 400);
        }

        Logger::info("Dispatching command [{$commandType}] to device {$deviceId}", ['params' => $params]);

        try {
            $idempotencyKey = 'cmd_' . bin2hex(random_bytes(10));
            $response = $this->client->executeDeviceCommand($deviceId, $commandType, $params, $idempotencyKey);
            return $response['data'] ?? $response;
        } catch (Exception $e) {
            Logger::error("Device command execution error: " . $e->getMessage());
            return [
                'success' => false,
                'device_id' => $deviceId,
                'command' => $commandType,
                'error' => $e->getMessage()
            ];
        }
    }
}
