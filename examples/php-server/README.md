# Gush Security — Reference PHP Server Connector

This directory contains a complete, production-grade standalone PHP reference implementation for connecting any external web server, property ERP system, website, or hosting environment (cPanel, Apache, Nginx, Docker) to **Gush Security Hub**.

---

## ⚡ Production Endpoint

- **Production Gateway URL**: `https://api.sstore.ng/api/gsecurity/api-access`
- **Protocol**: HTTPS REST (TLS 1.3) + HMAC-SHA256 Signatures
- **Local LAN Fallback**: `https://lan.gushsecurity.local:8443/api/v1`

---

## Quick Start in 5 Minutes

### 1. Copy Files & Configure Environment
```bash
cp .env.example .env
```
Edit `.env` with your estate credentials generated in Gush Hub (`Admin Ops -> Gush Connect -> Integrations`):
```ini
GUSH_HUB_URL=https://api.sstore.ng/api/gsecurity/api-access
GUSH_ESTATE_ID=pinnock_estate_01
GUSH_API_KEY=gush_live_your_api_key_here
GUSH_API_SECRET=sec_your_secret_here

DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_DATABASE=gush_security_db
DB_USERNAME=gush_db_user
DB_PASSWORD=YourSecurePassword
```

### 2. Import Database Schema
```bash
mysql -u gush_db_user -p gush_security_db < database/schema.sql
```

### 3. Verify Health Probe
Point your browser or cURL to:
```bash
curl -i https://yourdomain.com/gush-security/public/api/v1/health
```
Expected Output:
```json
{
  "success": true,
  "service": "Gush Security PHP Connector",
  "status": "ok",
  "version": "1.0.0",
  "timestamp": "2026-08-23T12:00:00Z",
  "database_status": "connected",
  "configured_gateway": "https://api.sstore.ng/api/gsecurity/api-access"
}
```

---

## Directory Structure
```
examples/php-server/
├── public/
│   ├── index.php         # REST API Front Controller & Router
│   ├── health.php        # Monitoring & Uptime probe
│   ├── webhook.php       # Inbound HMAC-verified webhook receiver
│   ├── .htaccess         # Apache clean-URL rewrites
│   └── api/
│       ├── status.php    # Server status & latency check
│       ├── events.php    # Event ingestion & query
│       ├── access.php    # Real-time pass & PIN verification
│       ├── visitors.php  # Pass creation endpoint
│       └── devices.php   # Hardware device commands & query
├── src/
│   ├── Config.php        # Environment & configuration loader
│   ├── Auth.php          # API key, bearer token & replay verification
│   ├── Signature.php     # Cryptographic HMAC-SHA256 generator & validator
│   ├── Router.php        # Technology-neutral REST router
│   ├── Response.php      # Standardized JSON response builder
│   ├── Logger.php        # Redacted audit logging
│   ├── Database.php      # PDO database connection manager
│   ├── GushClient.php    # Outbound API client connecting to Gush Hub
│   ├── AccessService.php # Access verification business logic
│   ├── EventService.php  # Event ledger and idempotency logic
│   └── DeviceService.php # Physical hardware actuation logic
├── config/
│   └── config.example.php
├── database/
│   └── schema.sql        # MySQL / MariaDB / PostgreSQL schema
├── storage/              # Logs and cache directory
├── .env.example
├── composer.json
└── README.md
```
