<?php

declare(strict_types=1);

require_once dirname(__DIR__, 2) . '/src/Config.php';
require_once dirname(__DIR__, 2) . '/src/Response.php';
require_once dirname(__DIR__, 2) . '/src/Database.php';

use GushSecurity\Config;
use GushSecurity\Response;
use GushSecurity\Database;

Config::load();

Response::json([
    'service' => 'Gush Security PHP Connector',
    'status' => 'operational',
    'hub_endpoint' => Config::get('GUSH_HUB_URL', 'https://api.sstore.ng/api/gsecurity/api-access'),
    'database' => Database::hasDatabase() ? 'connected' : 'disconnected',
    'time_utc' => gmdate('Y-m-d\TH:i:s\Z'),
]);
