package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.entity.User;
import com.kihomura.screenvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller for managing user profile operations and account settings.
 * Handles user profile management including profile retrieval, updates, and password changes.
 * Provides endpoints for authenticated users to manage their personal information.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserService userService;

    /**
     * Constructor for UserController.
     * 
     * @param userService the service for user operations
     */
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves the profile information of the currently authenticated user.
     * Returns comprehensive user profile data excluding sensitive information like passwords.
     * 
     * GET: /user/profile
     * @return ResponseEntity containing user profile data or error if user not found
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        Map<String, Object> result = new HashMap<>();

        if (user != null) {
            user.setPassword(null);

            result.put("code", 200);
            result.put("message", "Retrieve user profile successful");
            result.put("data", user);
            return ResponseEntity.ok(result);
        } else {
            result.put("code", 404);
            result.put("message", "User not found");
            return ResponseEntity.status(404).body(result);
        }
    }

    /**
     * Updates the profile information of the currently authenticated user.
     * Allows users to modify their profile details such as nickname, email, and other information.
     * 
     * PUT: /user/update
     * @param userUpdate the user object containing updated profile information
     * @return ResponseEntity indicating success or failure of the update operation
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUserProfile(@RequestBody User userUpdate) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User currentUser = userService.findByUsername(username);

        Map<String, Object> result = new HashMap<>();

        if (currentUser == null) {
            result.put("code", 404);
            result.put("message", "User not found");
            return ResponseEntity.status(404).body(result);
        }

        try {
            userUpdate.setId(currentUser.getId());

            User updatedUser = userService.updateUser(userUpdate);

            updatedUser.setPassword(null);

            result.put("code", 200);
            result.put("message", "Update user info successful");
            result.put("data", updatedUser);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", "Update user info failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

    /**
     * Changes the password of the currently authenticated user.
     * Securely updates the user's password with proper encryption.
     * 
     * PUT: /user/change-password
     * @param passwords a map containing the new password to set
     * @return ResponseEntity indicating success or failure of the password change
     */
    @PutMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwords) {
        String newPassword = passwords.get("newPassword");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userService.findByUsername(username);

        Map<String, Object> result = new HashMap<>();

        if (user == null) {
            result.put("code", 404);
            result.put("message", "User not found");
            return ResponseEntity.status(404).body(result);
        }

        try {
            String encodedNewPassword = passwordEncoder.encode(newPassword);

            user.setPassword(encodedNewPassword);
            userService.updateUser(user);

            result.put("code", 200);
            result.put("message", "Change password successful");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("code", 400);
            result.put("message", "Change password failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(result);
        }
    }

}