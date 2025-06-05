-- MySQL Container Initialization Script
-- This script runs when the MySQL container starts for the first time
-- Note: Spring Boot handles table creation via schema.sql

-- Create database if not exists (redundant but safe)
CREATE DATABASE IF NOT EXISTS `screen_vault` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the database
USE `screen_vault`;

-- Grant privileges (if needed for specific use cases)
-- GRANT ALL PRIVILEGES ON screen_vault.* TO 'root'@'%';
-- FLUSH PRIVILEGES;

-- Log initialization completion
SELECT 'MySQL container initialization completed' AS message; 