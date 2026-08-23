# Gush Security — Server-Side Database Integration

## 1. Database Security Principle

```
Mobile Android Client ──── HTTPS API ────▶ Gush Gateway (api.sstore.ng)
                                                │
                                                ▼ HTTPS Signed
                                     External PHP Server
                                                │
                                                ▼ Local LAN / Private VPC
                                     MySQL / PostgreSQL Database
```

Mobile clients never connect directly to your SQL server. All database operations are handled securely by your PHP server using PDO prepared statements.

---

## 2. Table Summary

- `gush_request_ids`: Stores incoming request IDs to prevent replay attacks.
- `gush_events`: Immutable audit ledger of all estate events received via webhook.
- `gush_visitors`: Local mirrored cache of generated and active visitor passes.
- `gush_access_events`: Gate entry and exit audit transactions.
- `gush_devices`: Enrolled hardware gate controllers, locks, relays, and cameras.
- `gush_webhook_logs`: Complete log of outbound and inbound webhook deliveries.

---

## 3. Safe PDO Transaction Pattern in PHP

```php
<?php
$pdo = \GushSecurity\Database::getConnection();

try {
    $pdo->beginTransaction();

    // 1. Check duplicate
    $check = $pdo->prepare("SELECT id FROM gush_visitors WHERE qr_token = :qr FOR UPDATE");
    $check->execute([':qr' => $qrToken]);
    if ($check->fetch()) {
        throw new Exception("Duplicate pass token");
    }

    // 2. Insert new pass
    $insert = $pdo->prepare("
        INSERT INTO gush_visitors (visitor_id, passcode, qr_token, visitor_name, property_unit, expires_at)
        VALUES (:id, :pin, :qr, :name, :unit, :exp)
    ");
    $insert->execute([
        ':id' => $passId,
        ':pin' => $pin,
        ':qr' => $qrToken,
        ':name' => $visitorName,
        ':unit' => $propertyUnit,
        ':exp' => $expiresAt
    ]);

    $pdo->commit();
} catch (Exception $e) {
    $pdo->rollBack();
    throw $e;
}
```
