# Gush Security — Authentication & Cryptographic Signatures

## 1. Authentication Headers

Every authenticated request between Gush Security and your PHP server MUST supply the following HTTP headers:

```http
Authorization: Bearer YOUR_API_KEY
X-Estate-Id: pinnock_estate_01
X-Gush-Timestamp: 1787484000
X-Gush-Request-Id: req_9a8b7c6d5e4f
X-Gush-Signature: sha256=a1b2c3d4e5f6...
Content-Type: application/json
```

---

## 2. HMAC-SHA256 Signature Algorithm

Signatures ensure that neither the URL path, HTTP method, nor the payload body was altered in transit.

### Mathematical Formulation
```
body_sha256 = SHA256(raw_request_body)

signature_payload = 
    HTTP_METHOD + "\n" +
    REQUEST_PATH + "\n" +
    TIMESTAMP + "\n" +
    REQUEST_ID + "\n" +
    body_sha256

signature = "sha256=" + HMAC_SHA256(signature_payload, API_SECRET)
```

### Complete PHP Implementation
```php
<?php
function calculateGushSignature(
    string $method,
    string $path,
    int $timestamp,
    string $requestId,
    string $rawBody,
    string $secret
): string {
    $bodyHash = hash('sha256', $rawBody);
    $normalizedPath = parse_url($path, PHP_URL_PATH) ?? $path;

    $payload = strtoupper($method) . "\n"
        . $normalizedPath . "\n"
        . $timestamp . "\n"
        . $requestId . "\n"
        . $bodyHash;

    return 'sha256=' . hash_hmac('sha256', $payload, $secret);
}
```

---

## 3. Signature Verification & Anti-Replay in PHP

```php
<?php
function verifyGushRequest(
    string $providedSignature,
    string $method,
    string $path,
    int $timestamp,
    string $requestId,
    string $rawBody,
    string $secret,
    int $maxDriftSeconds = 300
): bool {
    // 1. Check timestamp drift to prevent stale replayed requests
    if (abs(time() - $timestamp) > $maxDriftSeconds) {
        return false;
    }

    // 2. Compute expected signature
    $expected = calculateGushSignature($method, $path, $timestamp, $requestId, $rawBody, $secret);

    // 3. Constant-time equality comparison to prevent timing attacks
    return hash_equals($expected, $providedSignature);
}
```
