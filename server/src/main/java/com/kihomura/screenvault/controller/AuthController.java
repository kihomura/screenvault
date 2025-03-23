package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.pojo.User;
import com.kihomura.screenvault.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * POST: /auth/register
     * Registers a new user with the provided information.
     * @param user The user object containing user details.
     * @return ResponseEntity with the registration result.
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        try {
            User registeredUser = userService.register(user);

            //set password to null to hide sensitive information
            //password will not be returned to the frontend
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
     * POST: /auth/login
     * Handles user login by authenticating the provided credentials (username and password).
     * @param loginRequest A map containing the username and password to authenticate.
     * @return ResponseEntity
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String password = loginRequest.get("password");

        Map<String, Object> result = new HashMap<>();

        try {
            // 创建认证令牌
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(username, password);

            // 认证
            Authentication authentication = authenticationManager.authenticate(authToken);

            // 存储认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户详情
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userService.findByUsername(userDetails.getUsername());

            Map<String, Object> data = new HashMap<>();
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());

            result.put("code", 200);
            result.put("message", "Login successful");
            result.put("data", data);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 401);
            result.put("message", "Login failed: " + e.getMessage());
            return ResponseEntity.status(401).body(result);
        }
    }

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