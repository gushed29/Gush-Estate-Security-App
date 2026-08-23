# Gush Security — Normalized Security Event Model

All security events generated in Gush Security Hub are normalized into standard immutable facts:

## Event Schema Structure

```json
{
  "event_id": "evt_948201fa82",
  "event_type": "access.granted",
  "estate_id": "pinnock_estate_01",
  "property_id": "villa_14b",
  "device_id": "dev_barrier_gate1",
  "actor_id": "GD-0492",
  "actor_role": "GUARD_OFFICER",
  "timestamp": "2026-08-23T12:00:00Z",
  "data": {
    "visitor_name": "Marcus Vance",
    "pass_id": "vis_8392",
    "gate_name": "Gate 1 - Pinnock Beach Main Gate",
    "direction": "ENTRY",
    "vehicle_plate": "LAG-492-AA"
  }
}
```

---

## Core Supported Event Types

| Event Type | Category | Description |
|---|---|---|
| `access.granted` | Access Control | Visitor or resident entered or exited gate |
| `access.denied` | Access Control | Invalid passcode, expired pass, or blacklist match |
| `visitor.created` | Visitor Ops | New visitor pass generated |
| `visitor.revoked` | Visitor Ops | Pass cancelled by host or admin |
| `incident.created` | Emergency | Security guard or resident reported incident |
| `device.online` | Telemetry | Hardware device re-established connection |
| `device.offline` | Telemetry | Hardware device lost connection / heartbeat |
| `estate.lockdown` | Emergency | Estate entered emergency lockdown status |
