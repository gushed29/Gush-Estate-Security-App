# Gush Security — PHP Server Connector & External Integration Guide

Welcome to the comprehensive developer documentation for integrating external **PHP Web Servers**, property management software, and custom websites with the **Gush Security Universal Access Control Hub**.

---

## 🌐 Official Production Gateway
```
Production Gateway Endpoint: https://api.sstore.ng/api/gsecurity/api-access
Local LAN Hub Gateway:       https://lan.gushsecurity.local:8443/api/v1
```

---

## 🎯 What This Documentation Covers

This documentation specifies the complete external server contract and reference architecture for connecting your PHP server to Gush Security:

```
┌────────────────────────────────────────────────────────┐
│                   GUSH SECURITY HUB                    │
│    (Android Hub, Guard Kiosks, Gate Hardware Adapters) │
└───────────────────────────┬────────────────────────────┘
                            │
              HTTPS REST / Webhooks / HMAC-SHA256
                            │
┌───────────────────────────▼────────────────────────────┐
│         PRODUCTION ENDPOINT GATEWAY                    │
│     https://api.sstore.ng/api/gsecurity/api-access     │
└───────────────────────────┬────────────────────────────┘
                            │
                            │  Mutual Signed Requests
                            ▼
┌────────────────────────────────────────────────────────┐
│             YOUR EXTERNAL PHP WEB SERVER               │
│   (cPanel, Apache, Nginx, VPS, Docker, PHP 8.1+)       │
└──────────────┬──────────────────────────┬──────────────┘
               │                          │
               ▼                          ▼
    ┌────────────────────┐      ┌────────────────────┐
    │  Your MySQL / DB   │      │  Your Website ERP  │
    └────────────────────┘      └────────────────────┘
```

---

## 📑 Documentation Index

1. [Architecture Overview](architecture.md) — Dual-directional request flow and gateway design.
2. [Quick-Start & Configuration](configuration.md) — Setting up `.env`, cPanel, Apache, and Nginx.
3. [Authentication & HMAC Signatures](authentication.md) — API Keys, Bearer tokens, and cryptographic signing.
4. [Security Guidelines](security.md) — Rate limiting, least-privilege, and anti-replay protection.
5. [REST API Reference](api-reference.md) — Endpoints for pass verification, visitor creation, and devices.
6. [Request Format Specification](requests.md) — JSON schema and header requirements.
7. [Response Format & Error Codes](responses.md) — Standardized success and error payloads.
8. [Inbound & Outbound Webhooks](webhooks.md) — Real-time event notifications and retry policies.
9. [Normalized Event Model](events.md) — Estate domain event schemas and lifecycle facts.
10. [Hardware Commands & Idempotency](commands.md) — Physical gate barrier, lock, and relay actuation.
11. [Database Integration](database.md) — MySQL/PostgreSQL schema, PDO transactions, and sync bridges.
12. [Deployment Guide (cPanel, Apache, Nginx)](deployment.md) — Step-by-step production hosting setup.
13. [Troubleshooting & Diagnostics](troubleshooting.md) — Resolving signature mismatches, drift, and errors.
14. [Code Examples & Snippets](examples.md) — Complete examples in PHP, cURL, and JavaScript.
15. [Testing & Verification](testing.md) — Safe health probes, unit tests, and validation scripts.
16. [Endpoint Switching & Failover](endpoint-switching.md) — Updating backend URLs dynamically without app rebuilds.
17. [Reference Server Walkthrough](reference-server.md) — Anatomy of `/examples/php-server/`.

---

## ⚡ Connect Your PHP Server in 10 Minutes

1. **Upload Connector**: Copy the files from `/examples/php-server/` to your web server (e.g. `public_html/gush/`).
2. **Configure Database**: Create a database in cPanel/MySQL and import `/examples/php-server/database/schema.sql`.
3. **Configure Environment**: Copy `.env.example` to `.env` and set:
   ```ini
   GUSH_HUB_URL=https://api.sstore.ng/api/gsecurity/api-access
   GUSH_API_KEY=your_api_key_here
   GUSH_API_SECRET=your_api_secret_here
   GUSH_ESTATE_ID=pinnock_estate_01
   ```
4. **Test Health Endpoint**: Open `https://yourdomain.com/gush/api/v1/health` in your browser.
5. **Register in Gush Hub**: In the Gush Security Android app, navigate to `Admin Ops -> Gush Connect -> Add Connector`, enter your PHP server URL, and click **Test Connection**.
