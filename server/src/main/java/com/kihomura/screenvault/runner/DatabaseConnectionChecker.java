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
 * 检查数据库连接并记录连接信息
 * 用于确保应用程序正确连接到Railway提供的MySQL服务
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

    @Override
    public void run(String... args) {
        logger.info("正在检查数据库连接...");
        logger.info("数据库连接URL: {}", datasourceUrl);
        logger.info("数据库用户名: {}", username);

        try {
            String dbVersion = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            logger.info("数据库连接成功! MySQL版本: {}", dbVersion);
            
            // 检查数据库中是否存在表
            Integer tableCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()", 
                Integer.class
            );
            logger.info("数据库中存在 {} 个表", tableCount);
            
            if (tableCount == 0) {
                logger.warn("数据库为空，将执行初始化脚本");
            }
        } catch (Exception e) {
            logger.error("数据库连接失败: {}", e.getMessage(), e);
        }
    }
} 