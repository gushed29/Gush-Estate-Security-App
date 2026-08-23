<?php

declare(strict_types=1);

require_once dirname(__DIR__, 2) . '/src/Config.php';
require_once dirname(__DIR__, 2) . '/src/Response.php';
require_once dirname(__DIR__, 2) . '/src/Logger.php';
require_once dirname(__DIR__, 2) . '/src/Database.php';
require_once dirname(__DIR__, 2) . '/src/Signature.php';
require_once dirname(__DIR__, 2) . '/src/Auth.php';
require_once dirname(__DIR__, 2) . '/src/GushClient.php';
require_once dirname(__DIR__, 2) . '/src/DeviceService.php';

use GushSecurity\Config;
use GushSecurity\Auth;
use GushSecurity\Response;
use GushSecurity\DeviceService;

Config::load();

$method = $_SERVER['REQUEST_METHOD'] ?? 'GET';
$service = new DeviceService();

if ($method === 'POST') {
    Auth::authenticate(requireSignature: true);
    $input = json_decode(file_get_contents('php://input'), true) ?? [];
    $deviceId = $input['device_id'] ?? '';
    $command = $input['command_type'] ?? '';
    $params = $input['parameters'] ?? [];
    $result = $service->sendCommand($deviceId, $command, $params);
    Response::json($result);
} else {
    Auth::authenticate(requireSignature: false);
    $devices = $service->listDevices();
    Response::json($devices);
}
