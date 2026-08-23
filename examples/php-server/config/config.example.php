<?php

declare(strict_types=1);

/**
 * Gush Security PHP Connector Configuration Template.
 * Copy this file to config.php or populate .env
 */

return [
    'app' => [
        'env' => getenv('APP_ENV') ?: 'production',
        'debug' => (bool)(getenv('APP_DEBUG') ?: false),
        'timezone' => 'UTC',
    ],

    'gush' => [
        // Production Gush Hub / Gateway Endpoint
        'hub_url' => getenv('GUSH_HUB_URL') ?: 'https://api.sstore.ng/api/gsecurity/api-access',
        'api_version' => getenv('GUSH_API_VERSION') ?: 'v1',
        'estate_id' => getenv('GUSH_ESTATE_ID') ?: 'pinnock_estate_01',
        'api_key' => getenv('GUSH_API_KEY') ?: 'YOUR_GUSH_API_KEY',
        'api_secret' => getenv('GUSH_API_SECRET') ?: 'YOUR_GUSH_API_SECRET',
        'allowed_drift_seconds' => (int)(getenv('GUSH_ALLOWED_DRIFT_SECONDS') ?: 300),
    ],

    'database' => [
        'connection' => getenv('DB_CONNECTION') ?: 'mysql',
        'host' => getenv('DB_HOST') ?: '127.0.0.1',
        'port' => (int)(getenv('DB_PORT') ?: 3306),
        'database' => getenv('DB_DATABASE') ?: 'gush_security_db',
        'username' => getenv('DB_USERNAME') ?: 'gush_user',
        'password' => getenv('DB_PASSWORD') ?: '',
        'charset' => getenv('DB_CHARSET') ?: 'utf8mb4',
    ],

    'logging' => [
        'file' => getenv('LOG_FILE') ?: dirname(__DIR__) . '/storage/logs/gush_connector.log',
        'level' => getenv('LOG_LEVEL') ?: 'INFO',
    ]
];
