# Gush Security — Hardware Commands & Actuation

## 1. High-Integrity Hardware Commands

Unlike standard read requests, commands that cause physical movements (opening gates, pulsing relays, unlocking doors) require:

1. Permission `OPEN_GATE` or `UNLOCK_DOOR` enabled for the API key in Gush Hub.
2. Unique `Idempotency-Key` header to prevent double actuation.
3. Cryptographic HMAC signature over the body.
4. Active validation that the estate is not under emergency lockdown.

---

## 2. Dispatching a Gate Open Command in PHP

```php
<?php
require_once __DIR__ . '/src/GushClient.php';

use GushSecurity\GushClient;

$gush = new GushClient([
    'base_url' => 'https://api.sstore.ng/api/gsecurity/api-access',
    'api_key' => 'gush_live_your_key',
    'api_secret' => 'sec_your_secret',
    'estate_id' => 'pinnock_estate_01'
]);

try {
    $result = $gush->executeDeviceCommand(
        deviceId: 'dev_barrier_gate1',
        commandType: 'OPEN_GATE',
        parameters: [
            'pulse_duration_ms' => '3000',
            'reason' => 'Authorized Delivery Entry'
        ]
    );

    echo "Command Status: " . $result['status'] . "\n";
    echo "Message: " . $result['message'] . "\n";
} catch (Exception $e) {
    echo "Command Rejected: " . $e->getMessage() . "\n";
}
```

---

## 3. Physical Feedback vs. HTTP 200

> **Important**: The PHP client must never assume `HTTP 200 = Gate Physically Raised`. 
> 
> Always inspect `result.status === 'EXECUTED'` in the JSON response payload. If safety loop photocells or obstruction sensors are active, the physical hardware adapter may safely hold the barrier arm down.
