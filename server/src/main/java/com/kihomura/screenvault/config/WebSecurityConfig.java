package com.kihomura.screenvault.config;

import com.kihomura.screenvault.config.authentication.*;
import com.kihomura.screenvault.security.JWTAuthenticationFilter;
import com.kihomura.screenvault.security.JWTTokenProvider;
import com.kihomura.screenvault.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JWTTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * 使用构造函数注入UserDetailsService
     * @param userDetailsService 用来从数据库中加载用户信息
     * @param jwtTokenProvider
     */
    public WebSecurityConfig(UserDetailsService userDetailsService, JWTTokenProvider jwtTokenProvider, UserService userService) {
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 未登录状态下可以访问的路径，其他路径的请求则需要先经过身份验证
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/auth/**", "/error").permitAll()
                .anyRequest().authenticated()
        );

        // Login config
        http.formLogin(form -> form
                .loginPage("/auth/login") //未经过身份认证访问时的重定向路径
                .loginProcessingUrl("/auth/login") //用于提交登录请求的路径
                .usernameParameter("username") //对应前端表单的name
                .passwordParameter("password")
                .successHandler(new MyAuthenticationSuccessHandler(jwtTokenProvider))
                .failureHandler(new MyAuthenticationFailureHandler())
                .permitAll() //允许所有的用户访问登陆页面
        );

        // Logout config
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
                /*Session的创建策略：IF_REQUIRED指当没有身份验证信息时才会创建会话，如使用了JWT则不会创建Session
                public enum SessionCreationPolicy {
                    ALWAYS,
                    IF_REQUIRED, 默认策略，只有需要时才创建会话
                    NEVER,
                    STATELESS 无状态认证，不创建会话
                }*/
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 设置为无Session状态认证，使用JWT进行认证
//                .maximumSessions(1)
//                .expiredSessionStrategy(new MySessionInformationExpiredStrategy())
        );

        // Disable CSRF
        http.csrf(csrf -> csrf.disable());

        // JWT Filter
        http.addFilterBefore(new JWTAuthenticationFilter(jwtTokenProvider, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        // OAuth2 login config
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/auth/login")
                .successHandler(new MyOAuth2AuthenticationSuccessHandler(jwtTokenProvider, userService))
                .failureHandler(new MyAuthenticationFailureHandler())
        );

        //build()构建并返回一个SecurityFilterChain对象
        //该对象包含所有配置的安全策略，会被Spring Security在应用启动时自动加载和执行
        return http.build();
    }

    /**
     * AuthenticationProvider - Spring Security中用于验证用户身份的组件
     * 通常依赖于UserDetailsService来加载用户的具体信息
     *
     * DaoAuthenticationProvider - Spring Security提供的一个实现
     * 使用UserDetailsService和PasswordEncoder来验证用户的身份
     *
     * AuthenticationProvider会在认证流程中被调用
     * 当提交登录请求时，Spring Security会根据配置的AuthenticationProvider来处理身份验证
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager 负责协调认证过程
     * 接收一个authentication对象
     * 并委托给相应的AuthenticationProvider进行认证处理
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 将PasswordEncoder定义为一个独立的Bean，这样其他组件可以直接注入它
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}