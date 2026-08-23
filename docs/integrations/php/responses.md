# Gush Security — Standard Response Format & Error Codes

## 1. Success Response Structure (HTTP 2xx)

```json
{
  "success": true,
  "status_code": 200,
  "request_id": "req_849201fae",
  "timestamp": "2026-08-23T12:00:00Z",
  "data": {
    "status": "APPROVED",
    "visitor_name": "Marcus Vance"
  }
}
```

---

## 2. Error Response Structure (HTTP 4xx / 5xx)

```json
{
  "success": false,
  "status_code": 401,
  "request_id": "err_9281a0b1",
  "timestamp": "2026-08-23T12:00:00Z",
  "error": {
    "code": "INVALID_SIGNATURE",
    "message": "HMAC-SHA256 signature mismatch or timestamp expired",
    "details": {
      "drift_seconds": 450,
      "allowed_drift": 300
    }
  }
}
```

---

## 3. Standard Application Error Codes

| Error Code | HTTP Status | Description |
|---|---|---|
| `INVALID_JSON` | 400 | Request body is not well-formed JSON |
| `UNAUTHORIZED` | 401 | API key is missing or invalid |
| `INVALID_SIGNATURE` | 401 | HMAC-SHA256 signature verification failed |
| `EXPIRED_TIMESTAMP` | 401 | Request timestamp exceeds maximum allowed drift (300s) |
| `FORBIDDEN` | 403 | API key lacks permission for requested action |
| `ESTATE_LOCKDOWN` | 403 | Estate is under active emergency lockdown |
| `NOT_FOUND` | 404 | Target resource (device, pass, resident) does not exist |
| `DUPLICATE_REQUEST` | 409 | Request ID has already been executed (anti-replay) |
| `VALIDATION_ERROR` | 422 | Missing required parameters or format invalid |
| `RATE_LIMITED` | 429 | Request rate limit exceeded |
| `UPSTREAM_TIMEOUT` | 504 | Hardware device or gate controller failed to respond |
