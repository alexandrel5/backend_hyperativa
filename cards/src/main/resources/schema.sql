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
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY,          -- bigger range
    `owner_sub`       BINARY(16) NULL,                             -- Keycloak sub (nullable for anonymous)
    `correlation_id`  VARCHAR(36) NULL,                            -- for tracing across services
    `method`          VARCHAR(10) NOT NULL,
    `path`            VARCHAR(500) NOT NULL,
    `query_string`    VARCHAR(1000) NULL,
    `status`          INT NOT NULL,
    `duration_ms`     INT NOT NULL,
    `ip_address`      VARCHAR(45) NULL,
    `action`          VARCHAR(150) NOT NULL,                       -- e.g. "CREATE_CARD", "UPLOAD_FILE"
    `request_body`    TEXT NULL,                                   -- masked / truncated
    `response_body`   TEXT NULL,                                   -- masked / truncated / only on error
    `error_message`   TEXT NULL,
    `created_at`      TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    INDEX idx_owner_action     (`owner_sub`, `action`, `created_at`),
    INDEX idx_correlation      (`correlation_id`),
    INDEX idx_status_duration  (`status`, `duration_ms`)
);
