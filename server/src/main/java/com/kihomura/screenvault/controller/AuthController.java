package com.kihomura.screenvault.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.kihomura.screenvault.mapper.UserMapper;
import com.kihomura.screenvault.entity.User;
import com.kihomura.screenvault.security.JWTTokenProvider;
import com.kihomura.screenvault.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller for handling user registration, login, logout and user information.
 * Provides endpoints for user authentication and authorization operations.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;
    private final UserMapper userMapper;

    /**
     * Constructor for AuthController.
     * 
     * @param userService the service for user operations
     * @param authenticationManager Spring Security authentication manager
     * @param jwtTokenProvider JWT token provider for generating and validating tokens
     * @param userMapper MyBatis mapper for user database operations
     */
    public AuthController(UserService userService, AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider, UserMapper userMapper) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userMapper = userMapper;
    }

    /**
     * Gets the current authenticated user information.
     * Indicates whether the current user is logged in and returns user details.
     * 
     * GET: /auth/me
     * @param authentication the current authentication object
     * @return ResponseEntity containing user information or unauthorized status
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        System.out.println("--------------Authentication: " + authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        Object principal = authentication.getPrincipal();

        if(principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", userDetails.getUsername());
            userInfo.put("roles", userDetails.getAuthorities());
            return ResponseEntity.ok(userInfo);
        }

        return ResponseEntity.ok(principal);
    }

    /**
     * Registers a new user with the provided information.
     * Creates a new user account and returns the registered user details.
     * 
     * POST: /auth/register
     * @param user The user object containing registration details
     * @return ResponseEntity with the registration result and user data
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        try {
            User registeredUser = userService.register(user);

            // Set password to null to hide sensitive information
            // Password will not be returned to the frontend
            registeredUser.setPassword(null);

            result.put("code", 200);
            result.put("message", "Register successful");
            result.put("data", registeredUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", "Register failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Checks if a username already exists in the system.
     * Used for validating username availability during registration.
     * 
     * GET: /auth/check_username
     * @param username the username to check for availability
     * @return ResponseEntity containing boolean result indicating if username exists
     */
    @GetMapping("/check_username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        int count = Math.toIntExact(userMapper.selectCount(new QueryWrapper<User>().eq("username", username)));
        boolean exists = count > 0 ;
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", exists);
        return ResponseEntity.ok(result);
    }

    /**
     * Handles user login by authenticating the provided credentials.
     * Authenticates username and password, generates JWT token, and sets secure cookie.
     * 
     * POST: /auth/login
     * @param loginRequest A map containing the username and password to authenticate
     * @param response HTTP response object for setting authentication cookie
     * @return ResponseEntity containing login result and user data
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest,
                                                     HttpServletResponse response) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Map<String, Object> result = new HashMap<>();

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            Authentication authentication = authenticationManager.authenticate(authToken);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());

            String jwt = jwtTokenProvider.generateToken(authentication);

            ResponseCookie cookie = ResponseCookie.from("token", jwt)
                    .httpOnly(true)
                    .secure(false)
                    .path("/")
                    .maxAge(24 * 60 * 60 * 30)
                    .sameSite("Strict")
                    .build();

            response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            Map<String, Object> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());

            result.put("code", 200);
            result.put("message", "Login successful");
            result.put("data", data);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 401);
            result.put("username", username);
            result.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(401).body(result);
        }
    }

    /**
     * Logs out the current user by clearing security context and authentication cookie.
     * Removes JWT token cookie and clears Spring Security context.
     * 
     * POST: /auth/logout
     * @param response HTTP response object for clearing authentication cookie
     * @return ResponseEntity indicating successful logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();

        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves detailed information about the currently authenticated user.
     * Returns comprehensive user profile data for authenticated users.
     * 
     * GET: /auth/user-info
     * @return ResponseEntity containing detailed user information or error status
     */
    @GetMapping("/user-info")
    public ResponseEntity<Map<String, Object>> getUserInfo() {
        Map<String, Object> result = new HashMap<>();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.findByUsername(username);

            if (user != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", user.getId());
                data.put("username", user.getUsername());
                data.put("nickname", user.getNickname());
                data.put("enabled", user.isEnabled());
                data.put("createdAt", user.getCreatedAt());

                result.put("code", 200);
                result.put("message", "Retrieve user info successful");
                result.put("data", data);

                return ResponseEntity.ok(result);
            }
        }

        result.put("code", 401);
        result.put("message", "Unauthenticated or user info not found");
        return ResponseEntity.status(401).body(result);
    }
}