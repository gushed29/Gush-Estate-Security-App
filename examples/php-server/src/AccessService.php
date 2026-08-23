<?php

declare(strict_types=1);

namespace GushSecurity;

use PDO;
use PDOException;
use Exception;

/**
 * Access Verification and Visitor Pass Service.
 * Manages local database cache and coordinates verification with Gush Security Hub.
 */
final class AccessService
{
    private ?PDO $pdo;
    private GushClient $client;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
        $this->client = new GushClient();
    }

    /**
     * Verifies an access pass locally and upstream in Gush Hub.
     */
    public function verifyAccess(array $requestData): array
    {
        $passcode = $requestData['passcode'] ?? null;
        $qrToken = $requestData['qr_token'] ?? null;
        $gateCode = $requestData['gate_code'] ?? 'MAIN_GATE';
        $deviceId = $requestData['device_id'] ?? 'scanner_terminal';

        if (empty($passcode) && empty($qrToken)) {
            Response::error('INVALID_INPUT', 'Either passcode or qr_token must be provided', 422);
        }

        // 1. Check local DB if available
        if ($this->pdo !== null) {
            $stmt = $this->pdo->prepare("
                SELECT * FROM gush_visitors 
                WHERE (passcode = :code OR qr_token = :qr) 
                  AND status = 'ACTIVE' 
                  AND expires_at >= NOW()
                LIMIT 1
            ");
            $stmt->execute([':code' => $passcode ?? '', ':qr' => $qrToken ?? '']);
            $localPass = $stmt->fetch();

            if ($localPass) {
                // Record entry event in local database
                $this->recordAccessEvent([
                    'pass_id' => $localPass['visitor_id'],
                    'visitor_name' => $localPass['visitor_name'],
                    'direction' => 'ENTRY',
                    'gate_name' => $gateCode,
                    'device_id' => $deviceId,
                    'verified_by' => 'LOCAL_PHP_CACHE',
                    'access_status' => 'APPROVED',
                ]);

                return [
                    'status' => 'APPROVED',
                    'visitor_id' => $localPass['visitor_id'],
                    'visitor_name' => $localPass['visitor_name'],
                    'host_name' => $localPass['host_resident_name'],
                    'property_unit' => $localPass['property_unit'],
                    'pass_type' => $localPass['pass_type'],
                    'expires_at' => $localPass['expires_at'],
                    'verified_source' => 'LOCAL_DB',
                ];
            }
        }

        // 2. Delegate to Gush Security Hub
        try {
            $tokenOrPin = $passcode ?: $qrToken;
            $gushResult = $this->client->verifyVisitor($tokenOrPin, $gateCode, $deviceId);
            return $gushResult['data'] ?? $gushResult;
        } catch (Exception $e) {
            Logger::error("Upstream pass verification failed: " . $e->getMessage());
            return [
                'status' => 'DENIED',
                'reason' => 'Pass not found or expired',
                'details' => $e->getMessage()
            ];
        }
    }

    /**
     * Creates and registers a new Visitor Access Pass.
     */
    public function createPass(array $data): array
    {
        $visitorId = 'vis_' . bin2hex(random_bytes(6));
        $passcode = (string) random_int(100000, 999999);
        $qrToken = 'GSH-' . random_int(1000, 9999) . '-' . random_int(1000, 9999) . '-' . random_int(1000, 9999);
        
        $visitorName = trim((string)($data['visitor_name'] ?? ''));
        $hostName = trim((string)($data['host_resident_name'] ?? 'Resident Host'));
        $unit = trim((string)($data['property_unit'] ?? ''));
        $phone = trim((string)($data['visitor_phone'] ?? ''));
        $passType = $data['pass_type'] ?? 'GUEST';
        $validFrom = $data['valid_from'] ?? date('Y-m-d H:i:s');
        $expiresAt = $data['expires_at'] ?? date('Y-m-d H:i:s', time() + 86400);

        if (empty($visitorName) || empty($unit)) {
            Response::error('VALIDATION_ERROR', 'visitor_name and property_unit are mandatory fields', 422);
        }

        // Save to local DB
        if ($this->pdo !== null) {
            try {
                $stmt = $this->pdo->prepare("
                    INSERT INTO gush_visitors 
                    (visitor_id, passcode, qr_token, visitor_name, visitor_phone, host_resident_name, property_unit, pass_type, status, valid_from, expires_at, synced_to_gush)
                    VALUES 
                    (:vid, :pin, :qr, :vname, :vphone, :hname, :unit, :ptype, 'ACTIVE', :vfrom, :vexp, 0)
                ");
                $stmt->execute([
                    ':vid' => $visitorId,
                    ':pin' => $passcode,
                    ':qr' => $qrToken,
                    ':vname' => $visitorName,
                    ':vphone' => $phone,
                    ':hname' => $hostName,
                    ':unit' => $unit,
                    ':ptype' => $passType,
                    ':vfrom' => $validFrom,
                    ':vexp' => $expiresAt
                ]);
            } catch (PDOException $e) {
                Logger::error("Failed to store visitor pass in database: " . $e->getMessage());
            }
        }

        return [
            'visitor_id' => $visitorId,
            'passcode' => $passcode,
            'qr_token' => $qrToken,
            'visitor_name' => $visitorName,
            'host_name' => $hostName,
            'property_unit' => $unit,
            'pass_type' => $passType,
            'valid_from' => $validFrom,
            'expires_at' => $expiresAt,
            'status' => 'ACTIVE'
        ];
    }

    private function recordAccessEvent(array $event): void
    {
        if ($this->pdo === null) return;
        try {
            $stmt = $this->pdo->prepare("
                INSERT INTO gush_access_events 
                (transaction_id, pass_id, visitor_name, direction, gate_name, device_id, verified_by, access_status, occurred_at)
                VALUES 
                (:tx, :pid, :vname, :dir, :gate, :dev, :ver, :st, NOW())
            ");
            $stmt->execute([
                ':tx' => 'tx_' . bin2hex(random_bytes(8)),
                ':pid' => $event['pass_id'] ?? null,
                ':vname' => $event['visitor_name'],
                ':dir' => $event['direction'] ?? 'ENTRY',
                ':gate' => $event['gate_name'] ?? 'MAIN_GATE',
                ':dev' => $event['device_id'] ?? 'scanner',
                ':ver' => $event['verified_by'] ?? 'PHP_SERVICE',
                ':st' => $event['access_status'] ?? 'APPROVED'
            ]);
        } catch (PDOException $e) {
            Logger::warning("Could not record access event transaction: " . $e->getMessage());
        }
    }
}
