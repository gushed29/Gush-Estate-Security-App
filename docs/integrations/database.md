# Gush Security — Server-Side Database Integration Architecture

## Architectural Mandate: No Direct Mobile-to-Database Connections

```
❌ INSECURE ANTI-PATTERN:
Android Mobile App  ──── DIRECT DATABASE CONNECTION ────▶  Production MySQL / PostgreSQL DB
(Exposes database credentials, ports, SQL injection surfaces, and firewall openings on mobile devices)

✅ SECURE GUSH CONNECT ARCHITECTURE:
Android Mobile App
       │
       ▼ (HTTPS / TLS 1.3 + Bearer Token)
Gush Security Integration Gateway
       │
       ▼ (Private Internal VPC / Mutual TLS)
Server-Side Database Bridge (Node.js / Python / Go / PHP daemon)
       │
       ▼ (Local LAN / Private Subnet)
Production PostgreSQL / MySQL / MariaDB / SQLite Database
```

## Running the Server-Side Database Bridge

The server bridge runs as a daemon service on your estate Linux server, Docker container, or VPS. It synchronizes records between your property management ERP database and Gush Security Hub.

### Python Database Bridge Example (`gush_db_bridge.py`)
```python
import psycopg2
import requests
import time
import os

GUSH_API_URL = "https://api.gushsecurity.com/api/v1/visitor/passes"
API_KEY = os.getenv("GUSH_API_KEY", "YOUR_API_KEY")
ESTATE_ID = os.getenv("GUSH_ESTATE_ID", "YOUR_ESTATE_ID")

conn = psycopg2.connect(
    dbname="estate_erp",
    user="postgres",
    password=os.getenv("DB_PASSWORD", "YOUR_SECRET"),
    host="localhost",
    port=5432
)

def sync_pending_passes():
    with conn.cursor() as cur:
        cur.execute("SELECT id, visitor_name, phone, unit_number, pass_type FROM pending_passes WHERE synced = FALSE")
        rows = cur.fetchall()
        for row in rows:
            pass_id, visitor, phone, unit, pass_type = row
            payload = {
                "visitor_name": visitor,
                "visitor_phone": phone,
                "property_unit": unit,
                "pass_type": pass_type
            }
            res = requests.post(GUSH_API_URL, json=payload, headers={
                "Authorization": f"Bearer {API_KEY}",
                "X-Estate-Id": ESTATE_ID
            })
            if res.status_code == 201:
                cur.execute("UPDATE pending_passes SET synced = TRUE WHERE id = %s", (pass_id,))
                conn.commit()
                print(f"Synced pass for {visitor}")

if __name__ == "__main__":
    while True:
        sync_pending_passes()
        time.sleep(10)
```
