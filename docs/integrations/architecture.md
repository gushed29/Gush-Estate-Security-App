# Gush Security Hub — Architectural Specification

## 1. Gateway Request Pipeline

Every incoming command or integration request flows through the sequential 9-step Gush Pipeline:

```
External Request (REST / Webhook / DB Bridge / Local LAN)
    │
    ▼
1. Authentication Layer (Verify API Key, Bearer Token, HMAC Signature)
    │
    ▼
2. Authorization & Tenant Isolation (Tenant Org scoping, Permission checks)
    │
    ▼
3. Rate Limiting & Throttling (Sliding window per minute check)
    │
    ▼
4. Request Normalization & Validation (Schema validation, UUID format)
    │
    ▼
5. Anti-Replay & Idempotency Evaluation (Idempotency Key validation)
    │
    ▼
6. Security Policy Engine (Lockdown check, time-window, safety photocell loops)
    │
    ▼
7. Hardware Adapter Routing (Gate, Lock, Relay, QR, Camera protocol translation)
    │
    ▼
8. Physical Device Actuation & Result Verification
    │
    ▼
9. Audit Ledger Emission & Event Bus Dispatch
```

## 2. Event vs. Command Separation

- **Security Events** (`GushSecurityEvent`): Immutable, tamper-evident facts that occurred in the estate (e.g., `"visitor.pass.created"`, `"gate.01.opened"`, `"device.offline"`).
- **Security Commands** (`GushSecurityCommand`): Intentions to alter system or physical state (e.g., `"OPEN_GATE"`, `"UNLOCK_DOOR"`, `"PULSE_RELAY"`). A command is evaluated by the policy engine and hardware adapter before producing an event.

## 3. High-Risk Command Protections

Commands with physical consequences (`OPEN_GATE`, `UNLOCK_DOOR`, `EMERGENCY_OVERRIDE`) require:
- Explicit permission `OPEN_GATE` or `UNLOCK_DOOR`.
- Unique `Idempotency-Key` header.
- Verified target device ID enrolled in the hardware registry.
- Non-lockdown status on the estate perimeter.
