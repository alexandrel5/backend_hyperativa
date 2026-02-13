-- Create database
CREATE DATABASE IF NOT EXISTS `card_management_db`;
USE `card_management_db`;

-- Create users table for authentication
CREATE TABLE IF NOT EXISTS `users` (
     `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP()
);

-- Create cards table for storing card numbers securely
CREATE TABLE IF NOT EXISTS `cards` (
    `id` BINARY(16) PRIMARY KEY,
    `user_id`     INT NOT NULL,
    `card_hash`   CHAR(64) NOT NULL UNIQUE,
    `last_four`   CHAR(4) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP(),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
);

-- Create logs table for tracking API usage
CREATE TABLE IF NOT EXISTS `api_logs` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `user_id` INT,
    `action` VARCHAR(255) NOT NULL,
    `request_data` TEXT,
    `response_data` TEXT,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE SET NULL
);
