# Gush Security — Inbound & Outbound Webhooks

## 1. Webhook Overview

Webhooks allow the Gush Security Hub to instantly notify your external PHP server whenever key events occur in the estate (such as gate entries, badge taps, emergency incidents, or device disconnections).

```
Estate Barrier / Gate Scanner
              │ (Access Verified)
              ▼
   Gush Security Hub Gateway
              │ (Signed HTTPS POST)
              ▼
Your PHP Endpoint: https://yourdomain.com/gush/public/api/v1/webhook
              │
              ├─▶ Validate HMAC-SHA256 Signature
              ├─▶ Check Replay Drift Window
              ├─▶ Record Event in MySQL Database
              └─▶ Return HTTP 200 OK
```

---

## 2. Webhook Ingestion Script in PHP

```php
<?php
require_once __DIR__ . '/src/Config.php';
require_once __DIR__ . '/src/Auth.php';
require_once __DIR__ . '/src/Response.php';
require_once __DIR__ . '/src/EventService.php';

use GushSecurity\Config;
use GushSecurity\Auth;
use GushSecurity\Response;
use GushSecurity\EventService;

Config::load();

// 1. Authenticate signature & replay protection
Auth::authenticate(requireSignature: true);

// 2. Read raw payload
$rawBody = file_get_contents('php://input');
$event = json_decode($rawBody, true);

// 3. Process event
$service = new EventService();
$result = $service->processEvent($event);

// 4. Return success
Response::json([
    'webhook_processed' => true,
    'result' => $result
]);
```

---

## 3. Webhook Delivery & Retry Policy

If your PHP server returns a non-2xx status code or times out, the Gush Security Hub will automatically retry with exponential backoff:

- **Attempt 1**: Immediate
- **Attempt 2**: +5 seconds
- **Attempt 3**: +30 seconds
- **Attempt 4**: +2 minutes
- **Attempt 5**: +10 minutes

Duplicate prevention is guaranteed via the unique `event_id` and `gush_events` database uniqueness constraint.
