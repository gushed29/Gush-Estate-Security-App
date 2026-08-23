# Gush Security — Universal Access Control Integration Hub (Gush Connect)

Welcome to the **Gush Security Universal Access Control Integration Hub** developer platform.

Gush Security is an API-first, event-driven, vendor-neutral access-control and perimeter security gateway. It is designed to act as the authoritative bridge between estate management platforms, resident mobile apps, security guard posts, external databases, cloud automation workflows, and physical gate/lock hardware.

```
                    GUSH SECURITY HUB (GUSH CONNECT)
                                  │
                       ┌──────────┴──────────┐
                       │ Integration Gateway │
                       └──────────┬──────────┘
                                  │
         ┌────────────────────────┼────────────────────────┐
         │                        │                        │
      REST API                 Webhooks                WebSocket
         │                        │                        │
  ┌──────┴───────┐         ┌──────┴───────┐         ┌──────┴───────┐
Your Website  Server   Automation (n8n) Cloud   Local LAN   Estate Guard
  │              │         │              │         │              │
  └──────────────┼─────────┴──────────────┴─────────┼──────────────┘
                 │                                  │
           Gush Event Bus                     Gush Command Bus
                 │                                  │
   ┌─────────────┼─────────────┐      ┌─────────────┼─────────────┐
Database      Automations   Webhooks  Smart Locks  Barrier Gates Relays
Connector     (IFTTT/n8n)   Outbound  RFID/QR      IP Cameras   Intercoms
(Postgres/    Home Asst.              (Wiegand/    (ONVIF/      (SIP/
 MySQL/DB)                            OSDP)         RTSP)       Modbus)
```

---

## Key Architectural Principles

1. **Authoritative Security Engine**: External systems NEVER bypass security policy simply because they possess API credentials. Every request is authenticated, authorized against least-privilege permissions, rate-limited, scoped to a tenant estate, and verified by the Security Policy Engine before physical actuation.
2. **Server-Side Database Bridge**: Mobile clients never embed direct database credentials. Databases (PostgreSQL, MySQL, MariaDB, SQLite) synchronize via server-side microservice bridges connecting to Gush APIs.
3. **Idempotency & Replay Protection**: High-risk physical actuation commands require an `Idempotency-Key` to prevent duplicate gate cycles. All webhooks use HMAC-SHA256 timestamped signatures with a 300-second drift tolerance.
4. **Hardware Agnostic**: Vendor-neutral adapter interfaces support Barrier Gates (FAAC, Magnetic, Centurion), Smart Locks (Yale, Assa Abloy, Salto), Multi-channel IP Relays (Advantech, Moxa), RFID/QR Terminals (ZKTeco, Honeywell), and ANPR IP Cameras (Hikvision, Dahua).

---

## Documentation Index

- [Architecture Overview](architecture.md)
- [Authentication & Secrets](authentication.md)
- [REST API Reference (v1)](api.md)
- [Inbound & Outbound Webhooks](webhooks.md)
- [Event Model & Event Bus](events.md)
- [Command Bus & Idempotency](commands.md)
- [Hardware Adapters & LAN Gateway](hardware.md)
- [Database Integration & Server Bridges](database.md)
- [Automation (IFTTT, n8n, Home Assistant)](automation.md)
- [Offline Mode & Local LAN Hub](offline-mode.md)
- [OpenAPI 3.0 Specification](../openapi.yaml)
