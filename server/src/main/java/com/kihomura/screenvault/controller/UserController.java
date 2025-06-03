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

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET: /user/profile
     * Retrieves the profile of the currently authenticated user.
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
     * PUT: /user/update
     * Updates the profile information of the currently authenticated user.
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
     * PUT: /user/change-password
     * Changes the password of the currently authenticated user.
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