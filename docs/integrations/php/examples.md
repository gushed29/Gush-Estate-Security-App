# Gush Security — Multi-Language Integration Examples

## 1. Complete PHP Client Usage

```php
<?php
require_once __DIR__ . '/src/GushClient.php';

use GushSecurity\GushClient;

$gush = new GushClient([
    'base_url' => 'https://api.sstore.ng/api/gsecurity/api-access',
    'api_key' => 'gush_live_a8f93b74c2d1e0f982341bc5',
    'api_secret' => 'sec_7f9b8c2d1a3e5f4a6b8c0d2e4f6a8b0c',
    'estate_id' => 'pinnock_estate_01'
]);

// 1. Health Probe
$health = $gush->health();
echo "Gateway status: " . $health['status'] . "\n";

// 2. Create Visitor Pass
$pass = $gush->createVisitor([
    'visitor_name' => 'Adewale Johnson',
    'property_unit' => 'Unit 12A, Sunset Crest',
    'pass_type' => 'GUEST'
]);
echo "Generated Passcode: " . $pass['data']['passcode'] . "\n";

// 3. Verify Access Code
$verify = $gush->verifyVisitor($pass['data']['passcode'], 'MAIN_GATE');
echo "Access Result: " . $verify['status'] . "\n";
```

---

## 2. cURL Example (Signed Request)

```bash
# Calculate timestamp and request ID
TIMESTAMP=$(date +%s)
REQUEST_ID="req_$(openssl rand -hex 6)"
SECRET="sec_7f9b8c2d1a3e5f4a6b8c0d2e4f6a8b0c"
BODY='{"qr_token":"GSH-9482-1049-8392","gate_code":"PBE-GT01"}'

# Calculate SHA256 of body
BODY_HASH=$(echo -n "$BODY" | openssl dgst -sha256 | sed 's/^.* //')

# Form payload string
PAYLOAD="POST\n/api/v1/access/verify\n$TIMESTAMP\n$REQUEST_ID\n$BODY_HASH"

# Calculate HMAC signature
SIGNATURE="sha256=$(echo -en "$PAYLOAD" | openssl dgst -sha256 -hmac "$SECRET" | sed 's/^.* //')"

curl -X POST "https://api.sstore.ng/api/gsecurity/api-access/access/verify" \
  -H "Authorization: Bearer gush_live_a8f93b74c2d1e0f982341bc5" \
  -H "X-Estate-Id: pinnock_estate_01" \
  -H "X-Gush-Timestamp: $TIMESTAMP" \
  -H "X-Gush-Request-Id: $REQUEST_ID" \
  -H "X-Gush-Signature: $SIGNATURE" \
  -H "Content-Type: application/json" \
  -d "$BODY"
```

---

## 3. Node.js (Server-Side) Signed Client

```javascript
const crypto = require('crypto');
const axios = require('axios');

async function verifyPass(qrToken) {
  const url = 'https://api.sstore.ng/api/gsecurity/api-access/access/verify';
  const path = '/api/v1/access/verify';
  const timestamp = Math.floor(Date.now() / 1000);
  const requestId = 'req_' + crypto.randomBytes(6).toString('hex');
  const secret = process.env.GUSH_API_SECRET;
  
  const body = JSON.stringify({ qr_token: qrToken, gate_code: 'MAIN_GATE' });
  const bodyHash = crypto.createHash('sha256').update(body).digest('hex');
  
  const payload = `POST\n${path}\n${timestamp}\n${requestId}\n${bodyHash}`;
  const signature = 'sha256=' + crypto.createHmac('sha256', secret).update(payload).digest('hex');

  const response = await axios.post(url, body, {
    headers: {
      'Authorization': `Bearer ${process.env.GUSH_API_KEY}`,
      'X-Estate-Id': 'pinnock_estate_01',
      'X-Gush-Timestamp': timestamp.toString(),
      'X-Gush-Request-Id': requestId,
      'X-Gush-Signature': signature,
      'Content-Type': 'application/json'
    }
  });

  return response.data;
}
```
