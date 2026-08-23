# Gush Security — Troubleshooting & Diagnostics

## Common Issues & Solutions

### 1. `INVALID_SIGNATURE` (401 Unauthorized)
- **Cause**: Secret mismatch, trailing newline variations in JSON, or differing path formats.
- **Solution**:
  - Verify that `GUSH_API_SECRET` in `.env` matches the secret configured in Gush Hub.
  - Ensure the raw request body is passed without re-encoding through `file_get_contents('php://input')`.
  - Check that the URL path matches `/api/v1/access/verify` exactly.

### 2. `EXPIRED_TIMESTAMP` / Drift Exceeded
- **Cause**: Server system clock is out of sync with UTC time.
- **Solution**:
  - Run NTP time synchronization: `sudo timedatectl set-ntp on`.
  - If on shared hosting, verify server timezone setting in `php.ini` (`date.timezone = UTC`).

### 3. `DUPLICATE_REQUEST` (409 Conflict)
- **Cause**: Retried request with the same `X-Gush-Request-Id` or `Idempotency-Key`.
- **Solution**:
  - Each new intent requires a newly generated UUID/nonce.

### 4. Database Connection Refused
- **Cause**: MySQL user does not have permission on `127.0.0.1` or wrong port.
- **Solution**:
  - Test connection via command line: `mysql -u gush_db_user -p -h 127.0.0.1 gush_security_db`.
