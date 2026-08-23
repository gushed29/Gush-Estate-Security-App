# Gush Security REST API Reference (v1)

Base URL: `https://api.gushsecurity.com/api/v1` (Cloud Gateway) or `https://lan.gushsecurity.local:8443/api/v1` (Estate LAN Hub)

All requests require authorization headers:
```http
Authorization: Bearer YOUR_API_KEY
X-Estate-Id: YOUR_ESTATE_ID
```

---

## 1. Verify Access Pass or PIN

`POST /api/v1/access/verify`

### Request Payload
```json
{
  "passcode": "849201",
  "qr_token": "GSH-9482-1049-8392",
  "gate_code": "PBE-GT01",
  "device_id": "dev_scanner_gate1"
}
```

### Response (200 OK)
```json
{
  "status": "APPROVED",
  "visitor_name": "Marcus Vance",
  "pass_type": "GUEST",
  "host_name": "Engr. Babatunde Adeleke",
  "property_unit": "Villa 14B, Palm Boulevard",
  "expires_at": "2026-08-23T23:59:59Z"
}
```

---

## Multi-Language Integration Code Examples

### cURL
```bash
curl -X POST "https://api.gushsecurity.com/api/v1/access/verify" \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "X-Estate-Id: YOUR_ESTATE_ID" \
  -H "Content-Type: application/json" \
  -d '{
    "qr_token": "GSH-9482-1049-8392",
    "gate_code": "PBE-GT01"
  }'
```

### JavaScript / Browser Fetch
```javascript
const response = await fetch("https://api.gushsecurity.com/api/v1/access/verify", {
  method: "POST",
  headers: {
    "Authorization": `Bearer ${YOUR_API_KEY}`,
    "X-Estate-Id": YOUR_ESTATE_ID,
    "Content-Type": "application/json"
  },
  body: JSON.stringify({
    qr_token: "GSH-9482-1049-8392",
    gate_code: "PBE-GT01"
  })
});
const result = await response.json();
console.log("Access Result:", result.status);
```

### Python (requests)
```python
import requests

url = "https://api.gushsecurity.com/api/v1/access/verify"
headers = {
    "Authorization": f"Bearer {YOUR_API_KEY}",
    "X-Estate-Id": YOUR_ESTATE_ID,
    "Content-Type": "application/json"
}
payload = {
    "qr_token": "GSH-9482-1049-8392",
    "gate_code": "PBE-GT01"
}

response = requests.post(url, json=payload, headers=headers)
print("Access Response:", response.json())
```

### PHP (cURL)
```php
<?php
$curl = curl_init();

$payload = json_encode([
    "qr_token" => "GSH-9482-1049-8392",
    "gate_code" => "PBE-GT01"
]);

curl_setopt_array($curl, [
    CURLOPT_URL => "https://api.gushsecurity.com/api/v1/access/verify",
    CURLOPT_RETURNTRANSFER => true,
    CURLOPT_POST => true,
    CURLOPT_POSTFIELDS => $payload,
    CURLOPT_HTTPHEADER => [
        "Authorization: Bearer YOUR_API_KEY",
        "X-Estate-Id: YOUR_ESTATE_ID",
        "Content-Type: application/json"
    ],
]);

$response = curl_exec($curl);
curl_close($curl);
echo $response;
?>
```

### Node.js (axios)
```javascript
const axios = require('axios');

async function verifyPass() {
  const { data } = await axios.post(
    'https://api.gushsecurity.com/api/v1/access/verify',
    {
      qr_token: 'GSH-9482-1049-8392',
      gate_code: 'PBE-GT01'
    },
    {
      headers: {
        'Authorization': `Bearer ${process.env.GUSH_API_KEY}`,
        'X-Estate-Id': process.env.GUSH_ESTATE_ID
      }
    }
  );
  return data;
}
```
