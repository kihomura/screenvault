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
 * A simple CORS filter that ensures proper CORS headers are added to all request responses.
 * This filter has the highest priority and runs before all other filters to handle cross-origin requests.
 * It supports configurable allowed origins and handles preflight OPTIONS requests.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SimpleCorsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(SimpleCorsFilter.class);

    @Value("${cors.allowed-origins:http://localhost:5173}")
    private String allowedOrigins;

    /**
     * Main filter method that processes all HTTP requests and adds appropriate CORS headers.
     * Validates the request origin against allowed origins and handles OPTIONS preflight requests.
     * 
     * @param req the servlet request
     * @param resp the servlet response
     * @param chain the filter chain to continue processing
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String origin = request.getHeader("Origin");
        String method = request.getMethod();
        String path = request.getRequestURI();
        
        logger.debug("Processing request: {} {} from origin: {}", method, path, origin);
        
        // Check if the origin is in the allowed list
        if (origin != null && isAllowedOrigin(origin)) {
            logger.debug("Allowing origin: {}", origin);
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", 
                "Origin, X-Requested-With, Content-Type, Accept, Authorization, X-XSRF-TOKEN, Cache-Control");
            response.setHeader("Access-Control-Expose-Headers", "Set-Cookie, Authorization");
        } else if (origin != null) {
            logger.warn("Rejecting origin: {}", origin);
        }

        // If it's an OPTIONS request, return 200 OK directly
        if ("OPTIONS".equalsIgnoreCase(method)) {
            logger.debug("Handling OPTIONS preflight request");
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Continue processing the request
            try {
                chain.doFilter(req, response);
            } catch (Exception e) {
                logger.error("Error occurred while processing request: {} {}", method, path, e);
                throw e;
            }
        }
    }

    /**
     * Checks if the given origin is in the list of allowed origins.
     * 
     * @param origin the origin to check
     * @return true if the origin is allowed, false otherwise
     */
    private boolean isAllowedOrigin(String origin) {
        String[] origins = allowedOrigins.split(",");
        for (String allowedOrigin : origins) {
            if (allowedOrigin.trim().equals(origin)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initializes the filter with configuration parameters.
     * Logs the allowed origins for debugging purposes.
     * 
     * @param filterConfig the filter configuration
     */
    @Override
    public void init(FilterConfig filterConfig) {
        logger.info("SimpleCorsFilter initialized with allowed origins: {}", allowedOrigins);
    }

    /**
     * Cleanup method called when the filter is destroyed.
     * Logs the filter destruction for debugging purposes.
     */
    @Override
    public void destroy() {
        logger.info("SimpleCorsFilter destroyed");
    }
} 