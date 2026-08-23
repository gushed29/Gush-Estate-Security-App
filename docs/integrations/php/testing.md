# Gush Security — Integration Testing & Quality Assurance

## 1. Automated Test Workflow

Before deploying to production, run tests against all security boundaries:

```bash
# 1. Health Probe Test
curl -s https://yourdomain.com/gush/public/api/v1/health | grep '"status":"ok"'

# 2. Connection Probe (API Key Auth)
curl -s -X POST https://yourdomain.com/gush/public/api/v1/connection/test \
  -H "Authorization: Bearer YOUR_API_KEY"

# 3. Test Invalid Signature Rejection (Expect HTTP 401)
curl -s -X POST https://yourdomain.com/gush/public/api/v1/events \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "X-Gush-Timestamp: $(date +%s)" \
  -H "X-Gush-Request-Id: test_01" \
  -H "X-Gush-Signature: sha256=invalid_signature" \
  -d '{"event_type":"test"}'
```

---

## 2. Safety Rule: Connection Probes Never Actuate Physical Hardware

> **IMPORTANT**: Automated health probes (`GET /health`, `POST /connection/test`, `GET /devices`) NEVER send electrical pulses or motor commands to gate barriers or door deadbolts. Physical actuation requires an explicit `POST /devices/{id}/command` with an authorized administrative role and unique `Idempotency-Key`.
