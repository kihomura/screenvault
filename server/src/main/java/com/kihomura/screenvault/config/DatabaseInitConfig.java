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
 * 数据库初始化配置
 * 在生产环境中负责初始化数据库结构
 */
@Configuration
@Profile("prod")
public class DatabaseInitConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitConfig.class);

    @Value("${spring.sql.init.mode:never}")
    private String initMode;

    /**
     * 配置数据库初始化器
     * 如果spring.sql.init.mode=always，则执行初始化
     */
    @Bean
    @ConditionalOnProperty(name = "spring.sql.init.mode", havingValue = "always")
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        logger.info("正在初始化数据库...");
        
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("schema.sql"));
        populator.setContinueOnError(true);
        
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        
        return initializer;
    }
} 