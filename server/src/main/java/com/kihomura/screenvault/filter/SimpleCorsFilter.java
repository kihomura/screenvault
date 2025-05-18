package com.kihomura.screenvault.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 一个简单的CORS过滤器，确保在所有请求响应中添加正确的CORS头部
 * 这个过滤器优先级最高，在所有其他过滤器之前运行
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCorsFilter.class);

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String origin = request.getHeader("Origin");
        String method = request.getMethod();
        String path = request.getRequestURI();
        
        logger.debug("处理请求: {} {} 来自 {}", method, path, origin);
        
        // 检查来源是否在允许的列表中
        if (origin != null && isAllowedOrigin(origin)) {
            logger.debug("允许来源: {}", origin);
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", 
                "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-XSRF-TOKEN, Cache-Control");
            response.setHeader("Access-Control-Expose-Headers", "Set-Cookie, Authorization");
        } else if (origin != null) {
            logger.warn("不允许的来源: {}", origin);
        }

        // 如果是OPTIONS请求，直接返回200 OK
        if ("OPTIONS".equalsIgnoreCase(method)) {
            logger.debug("处理OPTIONS预检请求");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // 继续处理请求
            try {
                chain.doFilter(req, response);
            } catch (Exception e) {
                logger.error("处理请求时出错: {} {}", method, path, e);
                throw e;
            }
        }
    }

    private boolean isAllowedOrigin(String origin) {
        String[] origins = allowedOrigins.split(",");
        for (String allowedOrigin : origins) {
            if (allowedOrigin.trim().equals(origin)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("SimpleCorsFilter初始化，允许的来源: {}", allowedOrigins);
    }

    @Override
    public void destroy() {
        logger.info("SimpleCorsFilter销毁");
    }
} 