package com.kihomura.screenvault.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Database initialization configuration for production environment.
 * Responsible for initializing database structure in production deployments.
 * Only activates when spring.sql.init.mode is set to 'always'.
 */
@Configuration
@Profile("prod")
public class DatabaseInitConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitConfig.class);

    @Value("${spring.sql.init.mode:never}")
    private String initMode;

    /**
     * Configures database initializer to execute schema scripts.
     * Only executes when spring.sql.init.mode property is set to 'always'.
     * 
     * @param dataSource the data source to initialize
     * @return configured DataSourceInitializer
     */
    @Bean
    @ConditionalOnProperty(name = "spring.sql.init.mode", havingValue = "always")
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        logger.info("Initializing database...");
        
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));
        populator.setContinueOnError(true);
        
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        
        return initializer;
    }
} 