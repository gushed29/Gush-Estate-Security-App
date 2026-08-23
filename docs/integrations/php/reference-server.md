# Gush Security — PHP Reference Server Walkthrough

The standalone reference server in `/examples/php-server/` is designed for zero-dependency deployment across standard PHP 8.1+ hosting platforms (cPanel, Apache, Nginx, Docker).

---

## 1. Class Architecture

```
examples/php-server/
├── src/
│   ├── Config.php        # Parses .env and system environments
│   ├── Auth.php          # Validates API keys, bearer tokens & signatures
│   ├── Signature.php     # Computes & verifies HMAC-SHA256 in constant time
│   ├── Router.php        # Technology-neutral REST router (no .php in URLs)
│   ├── Response.php      # Enforces standardized JSON envelope
│   ├── Logger.php        # Audit logger with automatic credential redaction
│   ├── Database.php      # PDO database connection manager
│   ├── GushClient.php    # Outbound API client connecting to Gush Hub
│   ├── AccessService.php # Local pass validation & entry logging
│   ├── EventService.php  # Event ledger storage & deduplication
│   └── DeviceService.php # Enrolled hardware query & command actuation
```

---

## 2. Running Locally with PHP Built-in Server

```bash
cd examples/php-server
cp .env.example .env
php -S 127.0.0.1:8000 -t public
```

Test the local server in your browser or terminal:
```bash
curl http://127.0.0.1:8000/api/v1/health
```
