package com.kihomura.screenvault.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Database connection checker that validates and logs database connection information.
 * Used to ensure the application properly connects to MySQL service provided by Railway.
 * Only runs in production profile to verify database connectivity and schema status.
 */
@Component
@Profile("prod")
public class DatabaseConnectionChecker implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionChecker.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    /**
     * Executes database connection check on application startup.
     * Validates connection, logs database information, and checks schema status.
     * 
     * @param args command line arguments (not used)
     */
    @Override
    public void run(String... args) {
        logger.info("Checking database connection...");
        logger.info("Database connection URL: {}", datasourceUrl);
        logger.info("Database username: {}", username);

        try {
            String dbVersion = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            logger.info("Database connection successful! MySQL version: {}", dbVersion);
            
            // Check if tables exist in the database
            Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()", 
                Integer.class
            );
            logger.info("Database contains {} tables", tableCount);
            
            if (tableCount == 0) {
                logger.warn("Database is empty, initialization scripts will be executed");
            }
        } catch (Exception e) {
            logger.error("Database connection failed: {}", e.getMessage(), e);
        }
    }
} 