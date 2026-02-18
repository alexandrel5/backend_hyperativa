-- Create database
CREATE DATABASE IF NOT EXISTS `card_management_db`;
USE `card_management_db`;

CREATE TABLE IF NOT EXISTS `cards` (
    `id`          BINARY(16) PRIMARY KEY,
    `owner_sub`   BINARY(16) NOT NULL,                  -- Keycloak user UUID
    `card_hash`   CHAR(64) NOT NULL UNIQUE,
    `last_four`   CHAR(4) NOT NULL,
    `created_at`  TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    INDEX idx_owner_sub (`owner_sub`)
    );

-- Create logs table for tracking API usage
CREATE TABLE IF NOT EXISTS `api_logs` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `owner_sub`  BINARY(16),                            -- Keycloak user UUID
    `action` VARCHAR(255) NOT NULL,
    `request_data` TEXT,
    `response_data` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_sub_action (`owner_sub`, `action`, `created_at`)
);
