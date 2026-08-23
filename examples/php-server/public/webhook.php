<?php

declare(strict_types=1);

/**
 * Gush Security PHP Connector — Webhook Inbound Receiver.
 * Receives signed events from Gush Hub, validates HMAC-SHA256, verifies replay window,
 * and records events idempotently in database.
 */

if (!class_exists('GushSecurity\\Config')) {
    require_once dirname(__DIR__) . '/src/Config.php';
    require_once dirname(__DIR__) . '/src/Response.php';
    require_once dirname(__DIR__) . '/src/Logger.php';
    require_once dirname(__DIR__) . '/src/Database.php';
    require_once dirname(__DIR__) . '/src/Signature.php';
    require_once dirname(__DIR__) . '/src/Auth.php';
    require_once dirname(__DIR__) . '/src/EventService.php';
}

use GushSecurity\Config;
use GushSecurity\Auth;
use GushSecurity\Response;
use GushSecurity\EventService;
use GushSecurity\Logger;

Config::load();

// 1. Authenticate with HMAC-SHA256 and Timestamp Anti-Replay Check
Auth::authenticate(requireSignature: true);

// 2. Read Raw Payload
$rawJson = file_get_contents('php://input');
$eventData = json_decode($rawJson, true);

if (!is_array($eventData)) {
    Logger::warning("Malformed JSON received on webhook endpoint");
    Response::error('INVALID_JSON', 'Webhook body must be valid JSON object', 400);
}

// 3. Ingest Event Idempotently
$eventService = new EventService();
$result = $eventService->processEvent($eventData);

// 4. Return HTTP 200 OK Response
Response::json([
    'webhook_processed' => true,
    'result' => $result,
]);
