# Gush Security — PHP Integration Security & Hardening

## 1. Top 10 Integration Security Commandments

1. **Never Commit `.env` Files**: Always add `.env` and `.env.local` to `.gitignore`.
2. **Never Expose Database Credentials to Clients**: The Android client communicates via API, never through direct database connections.
3. **Use HTTPS / TLS 1.3 Exclusively**: HTTP requests must be rejected immediately with HTTP 403 or upgraded.
4. **Use Constant-Time Signature Comparison**: Always use `hash_equals()` to prevent timing side-channel attacks.
5. **Always Bind Parameters in SQL**: Use PDO prepared statements (`:param`) to eliminate SQL injection risks.
6. **Enforce Rate Limiting**: Apply token bucket or sliding window rate limiting (recommended: 120 requests/minute per API key).
7. **Redact Sensitive Data from Logs**: Never log plaintext API keys, HMAC secrets, user passwords, or tokens.
8. **Set Restrictive File Permissions**: On Linux/cPanel servers, use `chmod 755` for directories and `chmod 644` (or `600` for `.env`) for files.
9. **Use Idempotency Keys for Hardware Actuation**: Gate pulses, barrier opening, and lock actuations must require unique `Idempotency-Key` headers.
10. **Disable PHP Error Display in Production**: In `php.ini`, set `display_errors = Off` and `log_errors = On`.

---

## 2. Apache Security Headers (.htaccess)

```apache
<IfModule mod_headers.c>
    Header set X-Content-Type-Options "nosniff"
    Header set X-Frame-Options "DENY"
    Header set X-XSS-Protection "1; mode=block"
    Header set Referrer-Policy "no-referrer"
    Header set Strict-Transport-Security "max-age=31536000; includeSubDomains"
</IfModule>
```
