<?php

declare(strict_types=1);

/**
 * GUSH SECURITY — PHP SERVER CONNECTOR FRONT CONTROLLER
 * Entry point for all REST API endpoints, webhooks, and device operations.
 */

// 1. Autoload Classes (PSR-4 compliant with manual fallback)
spl_autoload_register(function (string $class) {
    $prefix = 'GushSecurity\\';
    $baseDir = dirname(__DIR__) . '/src/';
    $len = strlen($prefix);
    if (strncmp($prefix, $class, $len) !== 0) {
        return;
    }
    $relativeClass = substr($class, $len);
    $file = $baseDir . str_replace('\\', '/', $relativeClass) . '.php';
    if (file_exists($file)) {
        require_once $file;
    }
});

use GushSecurity\Config;
use GushSecurity\Router;
use GushSecurity\Response;
use GushSecurity\Auth;
use GushSecurity\AccessService;
use GushSecurity\EventService;
use GushSecurity\DeviceService;
use GushSecurity\GushClient;

// 2. Initialize Configuration
Config::load();

// 3. Create REST Router
$router = new Router();

// =========================================================================
// PUBLIC HEALTH & CAPABILITY DISCOVERY (NO SECRETS EXPOSED)
// =========================================================================
$router->get('/health', function () {
    require __DIR__ . '/health.php';
});

$router->get('/api/v1/health', function () {
    require __DIR__ . '/health.php';
});

$router->get('/api/v1/capabilities', function () {
    Response::json([
        'api_version' => 'v1',
        'server_name' => 'Gush Security PHP Connector',
        'supported_features' => [
            'visitor_pass_management',
            'realtime_access_verification',
            'security_event_ingestion',
            'hardware_actuation_commands',
            'inbound_webhooks_hmac_sha256',
            'anti_replay_idempotency'
        ],
        'signature_algorithm' => 'HMAC-SHA256',
        'allowed_timestamp_drift_seconds' => (int) Config::get('GUSH_ALLOWED_DRIFT_SECONDS', 300)
    ]);
});

// =========================================================================
// CONNECTION TEST (HUB PROBE)
// =========================================================================
$router->post('/api/v1/connection/test', function () {
    Auth::authenticate(requireSignature: false);
    Response::json([
        'connected' => true,
        'service' => 'gush-php-connector',
        'protocol_version' => '1.0.0',
        'server_time' => gmdate('Y-m-d\TH:i:s\Z'),
        'capabilities' => ['events', 'visitors', 'access', 'devices', 'webhooks'],
        'production_gateway_target' => Config::get('GUSH_HUB_URL', 'https://api.sstore.ng/api/gsecurity/api-access')
    ]);
});

// =========================================================================
// ACCESS CONTROL & PASS VERIFICATION
// =========================================================================
$router->post('/api/v1/access/verify', function () {
    Auth::authenticate(requireSignature: true);
    $input = json_decode(file_get_contents('php://input'), true) ?? [];
    $service = new AccessService();
    $result = $service->verifyAccess($input);
    Response::json($result);
});

// =========================================================================
// VISITOR PASSES
// =========================================================================
$router->post('/api/v1/visitors', function () {
    Auth::authenticate(requireSignature: true);
    $input = json_decode(file_get_contents('php://input'), true) ?? [];
    $service = new AccessService();
    $pass = $service->createPass($input);
    Response::json($pass, 201);
});

// =========================================================================
// SECURITY EVENTS & WEBHOOK INGESTION
// =========================================================================
$router->post('/api/v1/events', function () {
    Auth::authenticate(requireSignature: true);
    $input = json_decode(file_get_contents('php://input'), true) ?? [];
    $service = new EventService();
    $result = $service->processEvent($input);
    Response::json($result, 200);
});

$router->post('/api/v1/webhook', function () {
    require __DIR__ . '/webhook.php';
});

// =========================================================================
// HARDWARE DEVICES & COMMANDS
// =========================================================================
$router->get('/api/v1/devices', function () {
    Auth::authenticate(requireSignature: false);
    $service = new DeviceService();
    Response::json($service->listDevices());
});

$router->post('/api/v1/devices/{id}/command', function (array $params) {
    Auth::authenticate(requireSignature: true);
    $deviceId = $params['id'] ?? '';
    $input = json_decode(file_get_contents('php://input'), true) ?? [];
    $commandType = $input['command_type'] ?? '';
    $cmdParams = $input['parameters'] ?? [];
    
    $service = new DeviceService();
    $result = $service->sendCommand($deviceId, $commandType, $cmdParams);
    Response::json($result);
});

// 4. Dispatch Request
$router->dispatch();
