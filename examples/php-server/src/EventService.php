<?php

declare(strict_types=1);

namespace GushSecurity;

use PDO;
use PDOException;

/**
 * Normalized Security Event Ingestion and Processing Service.
 * Ingests webhook events from Gush Hub, validates uniqueness, stores in DB, and dispatches internal hooks.
 */
final class EventService
{
    private ?PDO $pdo;

    public function __construct()
    {
        $this->pdo = Database::getConnection();
    }

    /**
     * Processes an inbound security event from Gush Hub.
     */
    public function processEvent(array $eventPayload): array
    {
        $eventId = $eventPayload['event_id'] ?? ('evt_' . bin2hex(random_bytes(8)));
        $eventType = $eventPayload['event_type'] ?? 'unknown.event';
        $estateId = $eventPayload['estate_id'] ?? Config::get('GUSH_ESTATE_ID', 'estate_01');
        $propertyId = $eventPayload['property_id'] ?? null;
        $deviceId = $eventPayload['device_id'] ?? null;
        $actorId = $eventPayload['actor_id'] ?? 'SYSTEM';
        $actorRole = $eventPayload['actor_role'] ?? 'SYSTEM';
        $occurredAt = $eventPayload['timestamp'] ?? date('Y-m-d H:i:s');
        $data = $eventPayload['data'] ?? [];

        Logger::info("Processing Gush Security Event [{$eventType}] ({$eventId})", [
            'estate_id' => $estateId,
            'device_id' => $deviceId,
            'actor' => $actorId
        ]);

        if ($this->pdo !== null) {
            try {
                // Check if already processed (Idempotency)
                $stmtCheck = $this->pdo->prepare("SELECT id FROM gush_events WHERE event_id = :eid LIMIT 1");
                $stmtCheck->execute([':eid' => $eventId]);
                if ($stmtCheck->fetch()) {
                    return [
                        'status' => 'ALREADY_PROCESSED',
                        'event_id' => $eventId,
                        'message' => 'Event already recorded in ledger'
                    ];
                }

                // Insert into events ledger
                $stmt = $this->pdo->prepare("
                    INSERT INTO gush_events 
                    (event_id, event_type, estate_id, property_id, device_id, actor_id, actor_role, payload_json, occurred_at)
                    VALUES 
                    (:eid, :etype, :est, :prop, :dev, :act, :role, :pjson, :occ)
                ");
                $stmt->execute([
                    ':eid' => $eventId,
                    ':etype' => $eventType,
                    ':est' => $estateId,
                    ':prop' => $propertyId,
                    ':dev' => $deviceId,
                    ':act' => $actorId,
                    ':role' => $actorRole,
                    ':pjson' => json_encode($data),
                    ':occ' => date('Y-m-d H:i:s', strtotime($occurredAt))
                ]);

                // Update related visitor status if pass was used
                if ($eventType === 'access.granted' && !empty($data['visitor_id'])) {
                    $upStmt = $this->pdo->prepare("UPDATE gush_visitors SET status = 'USED' WHERE visitor_id = :vid");
                    $upStmt->execute([':vid' => $data['visitor_id']]);
                }
            } catch (PDOException $e) {
                Logger::error("Database error saving security event: " . $e->getMessage());
            }
        }

        return [
            'status' => 'RECORDED',
            'event_id' => $eventId,
            'event_type' => $eventType,
            'processed_at' => gmdate('Y-m-d\TH:i:s\Z')
        ];
    }

    /**
     * Lists recent events for admin dashboards or web widgets.
     */
    public function getRecentEvents(int $limit = 50): array
    {
        if ($this->pdo === null) {
            return [];
        }

        try {
            $stmt = $this->pdo->prepare("
                SELECT event_id, event_type, estate_id, property_id, device_id, actor_id, actor_role, payload_json, occurred_at 
                FROM gush_events 
                ORDER BY id DESC 
                LIMIT :lim
            ");
            $stmt->bindValue(':lim', $limit, PDO::PARAM_INT);
            $stmt->execute();
            return $stmt->fetchAll();
        } catch (PDOException $e) {
            Logger::error("Failed to query events: " . $e->getMessage());
            return [];
        }
    }
}
