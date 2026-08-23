# Gush Security — REST API Reference (v1)

Production Base URL: `https://api.sstore.ng/api/gsecurity/api-access`  
Local LAN Hub Base URL: `https://lan.gushsecurity.local:8443/api/v1`

---

## Endpoint Summary

| HTTP Method | Path | Auth Required | Description |
|---|---|---|---|
| `GET` | `/health` | No | Probes server health & uptime status |
| `GET` | `/capabilities` | No | Discovers supported estate features |
| `POST` | `/connection/test` | Yes (API Key) | Safe handshake test (no hardware actuation) |
| `POST` | `/access/verify` | Yes (Signed) | Verifies visitor QR code, PIN, or RFID badge |
| `POST` | `/visitor/passes` | Yes (Signed) | Generates a new visitor access pass |
| `POST` | `/visitor/passes/{id}/revoke` | Yes (Signed) | Revokes an existing pass immediately |
| `POST` | `/events` | Yes (Signed) | Dispatches or ingests estate security events |
| `POST` | `/webhook` | Yes (Signed) | Inbound webhook receiver for estate events |
| `GET` | `/devices` | Yes (API Key) | Lists registered estate hardware devices |
| `GET` | `/devices/{id}/status` | Yes (API Key) | Retrieves real-time device telemetry & state |
| `POST` | `/devices/{id}/command` | Yes (Signed) | Physical barrier/lock actuation command |

---

## 1. Verify Access Pass

`POST /access/verify`

### Request Body
```json
{
  "passcode": "849201",
  "qr_token": "GSH-9482-1049-8392",
  "gate_code": "PBE-GT01",
  "device_id": "dev_scanner_gate1"
}
```

### Response (HTTP 200 OK)
```json
{
  "success": true,
  "status_code": 200,
  "request_id": "req_839201fae",
  "timestamp": "2026-08-23T12:00:00Z",
  "data": {
    "status": "APPROVED",
    "visitor_name": "Marcus Vance",
    "pass_type": "GUEST",
    "host_name": "Engr. Babatunde Adeleke",
    "property_unit": "Villa 14B, Palm Boulevard",
    "expires_at": "2026-08-23T23:59:59Z"
  }
}
```

---

## 2. Create Visitor Access Pass

`POST /visitor/passes`

### Request Body
```json
{
  "visitor_name": "Dr. Chidi Okafor",
  "visitor_phone": "+2348012345678",
  "host_name": "Chief Adeleke",
  "property_unit": "Villa 14B, Palm Boulevard",
  "pass_type": "GUEST",
  "valid_from": "2026-08-23T12:00:00Z",
  "valid_until": "2026-08-23T23:59:59Z",
  "notes": "Invited for dinner meeting"
}
```

### Response (HTTP 201 Created)
```json
{
  "success": true,
  "status_code": 201,
  "request_id": "req_pass_7832",
  "timestamp": "2026-08-23T12:00:00Z",
  "data": {
    "visitor_id": "vis_938fa821",
    "passcode": "729482",
    "qr_token": "GSH-7294-8201-9482",
    "visitor_name": "Dr. Chidi Okafor",
    "status": "ACTIVE",
    "expires_at": "2026-08-23T23:59:59Z"
  }
}
```

---

## 3. Execute Hardware Command (Gate / Lock Actuation)

`POST /devices/{id}/command`

### Headers Required
```http
Authorization: Bearer YOUR_API_KEY
Idempotency-Key: cmd_open_gate1_20260823_120000
X-Gush-Signature: sha256=...
```

### Request Body
```json
{
  "command_type": "OPEN_GATE",
  "parameters": {
    "pulse_duration_ms": "3000",
    "reason": "Authorized VIP Arrival"
  }
}
```

### Response (HTTP 200 OK)
```json
{
  "success": true,
  "status_code": 200,
  "request_id": "req_cmd_9281",
  "timestamp": "2026-08-23T12:00:00Z",
  "data": {
    "command_id": "cmd_92810",
    "device_id": "dev_barrier_gate1",
    "status": "EXECUTED",
    "response_time_ms": 18,
    "message": "Relay contact pulsed for 3000ms. Barrier arm raised."
  }
}
```
