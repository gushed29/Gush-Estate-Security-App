# Gush Security — PHP Integration Architecture

## 1. Dual-Directional Communication Model

Gush Security and your external PHP server operate in two coordinated directions:

```
DIRECTION A: Outbound from PHP Server to Gush Security Hub
┌────────────────┐      HTTPS REST (POST /api/v1/access/verify)     ┌────────────────┐
│ Your Website / ├─────────────────────────────────────────────────▶│ Gush Security  │
│   PHP Server   │◀─────────────────────────────────────────────────┤ Hub / Gateway  │
└────────────────┘          JSON Response (Status: APPROVED)        └────────────────┘

DIRECTION B: Inbound from Gush Security Hub to PHP Server (Webhooks)
┌────────────────┐      HTTPS Webhook (POST /api/v1/webhook)        ┌────────────────┐
│ Your PHP Server│◀─────────────────────────────────────────────────┤ Gate Barrier / │
│   (Endpoint)   ├──────────────────────────────────────────────────┤ Optical Sensor │
└────────────────┘          HTTP 200 OK (Event Processed)           └────────────────┘
```

---

## 2. Gateway Pipeline & Policy Enforcement

When your PHP server sends a request to the production gateway (`https://api.sstore.ng/api/gsecurity/api-access`), the request passes through the Gush Security Pipeline:

1. **TLS 1.3 Transport Security**: Negotiates HTTPS with strict ciphers.
2. **API Key & Bearer Token Authentication**: Validates tenant credentials against the active connector registry.
3. **HMAC-SHA256 Signature Verification**: Ensures the request body and URL path have not been tampered with.
4. **Anti-Replay Verification**: Verifies that the `X-Gush-Timestamp` is within the 300-second allowable drift window and checks `X-Gush-Request-Id` against duplicate replay attacks.
5. **Security Policy Engine**: Validates estate boundaries, curfew hours, visitor pass rules, and emergency lockdown status.
6. **Physical Actuation**: If the command requests a physical gate cycle or lock trigger, the hardware adapter sends the appropriate electrical pulse or network packet (`Modbus TCP`, `Wiegand`).
7. **Event Ledger & Webhook Emission**: Dispatches an immutable event to all subscribed external listeners.

---

## 3. Server-Side Security Isolation Principle

> **CRITICAL ARCHITECTURAL MANDATE**: Mobile clients and public browsers MUST NEVER connect directly to your SQL database or embed raw privileged API secrets. 
>
> All property management database queries, resident sync operations, and privileged access grants must pass through your secure PHP backend server.
