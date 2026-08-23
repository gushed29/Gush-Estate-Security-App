# Gush Security — PHP Configuration & Environment Setup

## 1. Environment Variables Overview

Your PHP server uses environment variables to store credentials securely without hardcoding them into source files.

| Variable Name | Required | Default Value | Description |
|---|---|---|---|
| `GUSH_HUB_URL` | **Yes** | `https://api.sstore.ng/api/gsecurity/api-access` | Target production Gush Gateway URL |
| `GUSH_API_VERSION` | No | `v1` | REST API Protocol version |
| `GUSH_ESTATE_ID` | **Yes** | `pinnock_estate_01` | Unique Estate Identifier |
| `GUSH_API_KEY` | **Yes** | — | High-entropy integration API key |
| `GUSH_API_SECRET` | **Yes** | — | Cryptographic secret for HMAC-SHA256 signatures |
| `GUSH_ALLOWED_DRIFT_SECONDS` | No | `300` | Allowed clock skew for replay protection |
| `DB_CONNECTION` | No | `mysql` | Database driver (`mysql`, `pgsql`, `sqlite`) |
| `DB_HOST` | No | `127.0.0.1` | Database server hostname |
| `DB_PORT` | No | `3306` | Database connection port |
| `DB_DATABASE` | No | `gush_security_db` | Database name |
| `DB_USERNAME` | No | `gush_db_user` | Database user account |
| `DB_PASSWORD` | No | — | Database user password |

---

## 2. Configuring on cPanel Web Hosting

1. Log into your **cPanel Dashboard**.
2. Navigate to **File Manager** and open your website root (e.g. `public_html/gush-security/`).
3. Click **+ File** and name it `.env`.
4. Paste the configuration template and save changes.
5. In cPanel **MySQL Databases**, create your database and user, then assign all privileges.
6. Open **phpMyAdmin**, select your database, and click **Import** to upload `/examples/php-server/database/schema.sql`.

---

## 3. Configuring on Apache (VirtualHost / .htaccess)

In your Apache virtual host file:
```apache
<VirtualHost *:443>
    ServerName security.yourdomain.com
    DocumentRoot /var/www/gush-php-connector/public

    SetEnv GUSH_HUB_URL "https://api.sstore.ng/api/gsecurity/api-access"
    SetEnv GUSH_ESTATE_ID "pinnock_estate_01"
    SetEnv GUSH_API_KEY "gush_live_your_key"
    SetEnv GUSH_API_SECRET "sec_your_secret"

    SSLEngine on
    SSLCertificateFile /etc/letsencrypt/live/security.yourdomain.com/fullchain.pem
    SSLCertificateKeyFile /etc/letsencrypt/live/security.yourdomain.com/privkey.pem
</VirtualHost>
```

---

## 4. Configuring on Nginx + PHP-FPM

In `/etc/nginx/sites-available/gush-security.conf`:
```nginx
server {
    listen 443 ssl http2;
    server_name security.yourdomain.com;
    root /var/www/gush-php-connector/public;
    index index.php;

    ssl_certificate /etc/letsencrypt/live/security.yourdomain.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/security.yourdomain.com/privkey.pem;

    location / {
        try_files $uri $uri/ /index.php?$query_string;
    }

    location ~ \.php$ {
        include fastcgi_params;
        fastcgi_pass unix:/var/run/php/php8.1-fpm.sock;
        fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
        fastcgi_param GUSH_HUB_URL "https://api.sstore.ng/api/gsecurity/api-access";
        fastcgi_param GUSH_ESTATE_ID "pinnock_estate_01";
        fastcgi_param GUSH_API_KEY "gush_live_your_key";
        fastcgi_param GUSH_API_SECRET "sec_your_secret";
    }

    location ~ /\.(env|git|ht) {
        deny all;
    }
}
```
