-- =====================================================================
-- GUSH SECURITY — PHP SERVER CONNECTOR DATABASE SCHEMA
-- Compatible with MySQL 8.0+, MariaDB 10.4+, and PostgreSQL 13+
-- =====================================================================

-- 1. Anti-Replay Idempotency & Request Ledger Table
CREATE TABLE IF NOT EXISTS `gush_request_ids` (
    `request_id` VARCHAR(128) NOT NULL,
    `received_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `endpoint` VARCHAR(255) NOT NULL,
    `client_ip` VARCHAR(64) NULL,
    PRIMARY KEY (`request_id`),
    INDEX `idx_req_received` (`received_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Security Events Ledger (Ingested Facts from Estate Gates/Sensors)
CREATE TABLE IF NOT EXISTS `gush_events` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `event_id` VARCHAR(128) NOT NULL UNIQUE,
    `event_type` VARCHAR(100) NOT NULL,
    `estate_id` VARCHAR(64) NOT NULL,
    `property_id` VARCHAR(64) NULL,
    `device_id` VARCHAR(64) NULL,
    `actor_id` VARCHAR(64) NULL,
    `actor_role` VARCHAR(64) NULL,
    `payload_json` JSON NOT NULL,
    `occurred_at` DATETIME NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_event_type` (`event_type`),
    INDEX `idx_estate_time` (`estate_id`, `occurred_at`),
    INDEX `idx_device_id` (`device_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Access Verification & Pass Records (Mirrored & Generated Passes)
CREATE TABLE IF NOT EXISTS `gush_visitors` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `visitor_id` VARCHAR(128) NOT NULL UNIQUE,
    `passcode` VARCHAR(32) NOT NULL,
    `qr_token` VARCHAR(255) NOT NULL UNIQUE,
    `visitor_name` VARCHAR(255) NOT NULL,
    `visitor_phone` VARCHAR(32) NULL,
    `host_resident_name` VARCHAR(255) NOT NULL,
    `property_unit` VARCHAR(128) NOT NULL,
    `pass_type` VARCHAR(32) NOT NULL DEFAULT 'GUEST',
    `status` VARCHAR(32) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, USED, EXPIRED, REVOKED
    `valid_from` DATETIME NOT NULL,
    `expires_at` DATETIME NOT NULL,
    `synced_to_gush` TINYINT(1) NOT NULL DEFAULT 0,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_passcode` (`passcode`),
    INDEX `idx_qr_token` (`qr_token`),
    INDEX `idx_status` (`status`),
    INDEX `idx_property_unit` (`property_unit`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Access Entry & Exit Transactions
CREATE TABLE IF NOT EXISTS `gush_access_events` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `transaction_id` VARCHAR(128) NOT NULL UNIQUE,
    `pass_id` VARCHAR(128) NULL,
    `visitor_name` VARCHAR(255) NOT NULL,
    `direction` VARCHAR(16) NOT NULL, -- ENTRY, EXIT
    `gate_name` VARCHAR(128) NOT NULL,
    `device_id` VARCHAR(64) NULL,
    `verified_by` VARCHAR(64) NOT NULL, -- GUARD, AUTOMATED_OPTICAL, LPR_ANPR
    `access_status` VARCHAR(32) NOT NULL, -- APPROVED, DENIED, OVERRIDE
    `license_plate` VARCHAR(32) NULL,
    `occurred_at` DATETIME NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_access_visitor` (`visitor_name`),
    INDEX `idx_access_status` (`access_status`),
    INDEX `idx_occurred` (`occurred_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Registered Hardware Devices Cache
CREATE TABLE IF NOT EXISTS `gush_devices` (
    `device_id` VARCHAR(64) NOT NULL PRIMARY KEY,
    `name` VARCHAR(255) NOT NULL,
    `device_type` VARCHAR(64) NOT NULL, -- GATE_CONTROLLER, SMART_LOCK, RELAY_MODULE, QR_SCANNER, IP_CAMERA
    `manufacturer` VARCHAR(128) NULL,
    `model_number` VARCHAR(128) NULL,
    `ip_address` VARCHAR(64) NULL,
    `status` VARCHAR(32) NOT NULL DEFAULT 'ONLINE',
    `location` VARCHAR(255) NULL,
    `assigned_gate` VARCHAR(255) NULL,
    `last_heartbeat` DATETIME NULL,
    `capabilities_json` JSON NULL,
    `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 6. Inbound & Outbound Webhook Delivery Logs
CREATE TABLE IF NOT EXISTS `gush_webhook_logs` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `delivery_id` VARCHAR(128) NOT NULL UNIQUE,
    `direction` VARCHAR(16) NOT NULL, -- INBOUND, OUTBOUND
    `event_type` VARCHAR(100) NOT NULL,
    `target_url` VARCHAR(512) NOT NULL,
    `http_status` INT NOT NULL,
    `payload_json` JSON NOT NULL,
    `response_body` TEXT NULL,
    `attempt_number` INT NOT NULL DEFAULT 1,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_wh_event` (`event_type`),
    INDEX `idx_wh_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
