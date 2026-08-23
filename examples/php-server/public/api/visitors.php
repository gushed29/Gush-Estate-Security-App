<?php

declare(strict_types=1);

require_once dirname(__DIR__, 2) . '/src/Config.php';
require_once dirname(__DIR__, 2) . '/src/Response.php';
require_once dirname(__DIR__, 2) . '/src/Logger.php';
require_once dirname(__DIR__, 2) . '/src/Database.php';
require_once dirname(__DIR__, 2) . '/src/Signature.php';
require_once dirname(__DIR__, 2) . '/src/Auth.php';
require_once dirname(__DIR__, 2) . '/src/GushClient.php';
require_once dirname(__DIR__, 2) . '/src/AccessService.php';

use GushSecurity\Config;
use GushSecurity\Auth;
use GushSecurity\Response;
use GushSecurity\AccessService;

Config::load();
Auth::authenticate(requireSignature: true);

$input = json_decode(file_get_contents('php://input'), true) ?? [];
$service = new AccessService();
$pass = $service->createPass($input);

Response::json($pass, 201);
