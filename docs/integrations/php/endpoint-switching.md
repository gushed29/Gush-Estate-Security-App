# Gush Security — Endpoint Switching & Failover

## 1. Dynamic Endpoint Management (No App Rebuild Required)

In Gush Security, backend URLs are fully configurable and remotely switchable via the **Admin Operations Console -> Gush Connect** interface. 

You do **NOT** need to recompile or release a new Android APK whenever server infrastructure moves or migrates.

```
Current Endpoint:
https://old-server.example.com/api/v1
       │
       ▼ (Admin updates in Gush Connect Console)
New Production Endpoint:
https://api.sstore.ng/api/gsecurity/api-access
```

---

## 2. Step-by-Step Endpoint Migration Workflow

1. **Deploy New PHP Server**: Upload `/examples/php-server/` to the new host and verify `/health` responds with `200 OK`.
2. **Open Gush Hub Admin**: Navigate to `Admin Ops -> Gush Connect -> Active Connectors`.
3. **Select Connector**: Click the **Edit / Switch URL** action on your connector card.
4. **Enter New URL**: Set the target to `https://api.sstore.ng/api/gsecurity/api-access` and provide an administrative reason (e.g., "Upgraded to production cloud cluster").
5. **Run Probe**: Click **Test Connection** to negotiate TLS 1.3 handshake and verify authorization.
6. **Audit Trail**: Gush Hub automatically emits a `connector.endpoint_switched` event in the tamper-evident Event Ledger recording the actor, old URL, new URL, and timestamp.
