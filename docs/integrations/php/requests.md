# Gush Security — Request Format Specification

## 1. Standardized JSON Request Wrapper

All POST and PUT requests to the Gush Security Gateway use normalized JSON payloads.

```json
{
  "request_id": "req_849201fae",
  "timestamp": "2026-08-23T12:00:00Z",
  "estate_id": "pinnock_estate_01",
  "actor_id": "res_babatunde",
  "actor_role": "RESIDENT_HOST",
  "data": {
    "visitor_name": "Marcus Vance",
    "property_unit": "Villa 14B"
  }
}
```

### Field Definitions

| Field | Type | Description |
|---|---|---|
| `request_id` | String | Unique UUID or client-generated tracking ID |
| `timestamp` | String | ISO 8601 UTC timestamp of request dispatch |
| `estate_id` | String | Scoped estate tenant identifier |
| `actor_id` | String | User ID, resident name, or guard badge initiating request |
| `actor_role` | String | Security role (`RESIDENT_HOST`, `GUARD_OFFICER`, `ADMIN_SUPERVISOR`, `EXTERNAL_INTEGRATION`) |
| `data` | Object | Method-specific parameters and entity payloads |
