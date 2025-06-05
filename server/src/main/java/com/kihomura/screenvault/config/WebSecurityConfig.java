package com.kihomura.screenvault.config;

import com.kihomura.screenvault.config.authentication.*;
import com.kihomura.screenvault.filter.SimpleCorsFilter;
import com.kihomura.screenvault.security.JWTAuthenticationFilter;
import com.kihomura.screenvault.security.JWTTokenProvider;
import com.kihomura.screenvault.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Web security configuration for Spring Security.
 * Configures authentication, authorization, CORS, and JWT-based security.
 * Supports both form-based and OAuth2 authentication mechanisms.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    private MyOAuth2AuthenticationSuccessHandler myOAuth2AuthenticationSuccessHandler;

    @Autowired
    private SimpleCorsFilter simpleCorsFilter;

    private final UserDetailsService userDetailsService;
    private final JWTTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * Constructor injection for security-related services.
     * 
     * @param userDetailsService service for loading user information from database
     * @param jwtTokenProvider JWT token provider for authentication
     * @param userService user service for user management operations
     */
    public WebSecurityConfig(UserDetailsService userDetailsService, JWTTokenProvider jwtTokenProvider, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    /**
     * Configures the main security filter chain.
     * Sets up authentication, authorization, CORS, session management, and custom filters.
     * 
     * @param http the HttpSecurity to configure
     * @return configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain");
        
        // Paths accessible without authentication, other paths require authentication
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/auth/login", "/auth/register", "/auth/check_username").permitAll()
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow all OPTIONS requests
                .requestMatchers("/auth/**", "/error", "/api/ip/**").permitAll()
                .anyRequest().authenticated()
        );

        // CORS configuration - explicitly enable and point to corsConfigurationSource
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // Login configuration
        http.formLogin(form -> form
                .loginPage("/auth/login") // Redirect path when accessing without authentication
                .loginProcessingUrl("/auth/login") // Path for submitting login requests
                .usernameParameter("username") // Corresponds to frontend form name
                .passwordParameter("password")
                .successHandler(new MyAuthenticationSuccessHandler(jwtTokenProvider))
                .failureHandler(new MyAuthenticationFailureHandler())
                .permitAll() // Allow all users to access login page
        );

        // Logout configuration
        http.logout(logout -> logout
                .logoutUrl("/auth/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("token")
                .logoutSuccessHandler(new MyLogoutSuccessHandler())
                .permitAll()
        );

        // Exception handling
        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                .accessDeniedHandler(new MyAccessDeniedHandler())
        );

        // Session management
        http.sessionManagement(session -> session
                /*
                Session creation policy: STATELESS for JWT-based authentication
                SessionCreationPolicy options:
                - ALWAYS: Always create session
                - IF_REQUIRED: Default strategy, create session only when needed
                - NEVER: Never create session
                - STATELESS: Stateless authentication, no session creation
                */
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Set to stateless authentication using JWT
        );

        // OAuth2 login configuration
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/auth/login")
                .successHandler(myOAuth2AuthenticationSuccessHandler)
                .failureHandler(new MyAuthenticationFailureHandler())
        );

        // Disable CSRF
        http.csrf(AbstractHttpConfigurer::disable);
        
        // Add SimpleCorsFilter to the front of the filter chain
        http.addFilterBefore(simpleCorsFilter, UsernamePasswordAuthenticationFilter.class);

        // JWT Filter
        http.addFilterBefore(new JWTAuthenticationFilter(jwtTokenProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        logger.info("SecurityFilterChain configuration completed");
        return http.build();
    }

    /**
     * Configures CORS (Cross-Origin Resource Sharing) settings.
     * Defines allowed origins, methods, headers, and other CORS policies.
     * 
     * @return configured CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        logger.info("Configuring CorsConfigurationSource");
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(Arrays.asList("https://screenvault-client-production.up.railway.app"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Accept", "X-Requested-With", "Origin"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie", "Content-Disposition"));
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        logger.info("CorsConfigurationSource configuration completed");
        return source;
    }

    /**
     * AuthenticationProvider - Spring Security component for verifying user identity.
     * Typically relies on UserDetailsService to load specific user information.
     *
     * DaoAuthenticationProvider - Implementation provided by Spring Security
     * that uses UserDetailsService and PasswordEncoder to verify user identity.
     *
     * AuthenticationProvider is called during the authentication flow.
     * When login requests are submitted, Spring Security processes authentication
     * based on the configured AuthenticationProvider.
     * 
     * @return configured DaoAuthenticationProvider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager coordinates the authentication process.
     * Receives an authentication object and delegates to appropriate
     * AuthenticationProvider for authentication processing.
     * 
     * @param config authentication configuration
     * @return configured AuthenticationManager
     * @throws Exception if configuration fails
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines PasswordEncoder as an independent Bean for dependency injection.
     * Uses BCrypt algorithm for secure password hashing.
     * 
     * @return BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}