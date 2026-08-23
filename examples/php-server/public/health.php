<?php

declare(strict_types=1);

/**
 * Gush Security PHP Connector — Health Check Endpoint.
 * Safe for monitoring tools, load balancers, and uptime probes.
 * Never outputs internal secrets or sensitive server paths.
 */

if (!class_exists('GushSecurity\\Config')) {
    require_once dirname(__DIR__) . '/src/Config.php';
    require_once dirname(__DIR__) . '/src/Response.php';
    require_once dirname(__DIR__) . '/src/Database.php';
}

use GushSecurity\Config;
use GushSecurity\Response;
use GushSecurity\Database;

Config::load();

$dbStatus = Database::hasDatabase() ? 'connected' : 'unconfigured_or_offline';

Response::json([
    'service' => 'Gush Security PHP Connector',
    'status' => 'ok',
    'version' => '1.0.0',
    'timestamp' => gmdate('Y-m-d\TH:i:s\Z'),
    'environment' => Config::get('APP_ENV', 'production'),
    'database_status' => $dbStatus,
    'configured_gateway' => Config::get('GUSH_HUB_URL', 'https://api.sstore.ng/api/gsecurity/api-access'),
]);
