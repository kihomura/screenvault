package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.mapper.UserMapper;
import com.kihomura.screenvault.entity.User;
import com.kihomura.screenvault.service.PlayListService;
import com.kihomura.screenvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Implementation of UserService interface.
 * Handles user management operations including registration, authentication,
 * profile updates, and OAuth2 user processing.
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private final PlayListService playListService;

    /**
     * Constructor with lazy injection to break circular dependencies.
     * 
     * @param userMapper mapper for user database operations
     * @param playListService service for playlist operations
     */
    public UserServiceImpl(UserMapper userMapper, @Lazy PlayListService playListService) {
        this.userMapper = userMapper;
        this.playListService = playListService;
    }

    /**
     * Setter injection for PasswordEncoder to break circular dependency.
     * 
     * @param passwordEncoder encoder for password hashing
     */
    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with form-based registration.
     * Creates default wishlist for the new user.
     * 
     * @param user the user to register
     * @return the registered user
     * @throws RuntimeException if username already exists
     */
    @Override
    @Transactional
    public User register(User user) {
        // Check if the username exists
        if (checkUsernameExists(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Use username as the default nickname if not provided
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            user.setNickname(user.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        user.setEnabled(true);

        save(user);
        
        // Create default wishlist for the new user
        playListService.createWishListForUser(user.getId());

        return user;
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return count(queryWrapper) > 0;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        User existingUser = getById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("User not found");
        }

        // Update nickname if provided
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }

        // If password is updated, encrypt it first
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setUpdatedAt(LocalDateTime.now());
        updateById(existingUser);

        return existingUser;
    }

    /**
     * Registers OAuth2 user and saves provider and providerId information.
     * Creates default wishlist for the new OAuth user.
     * 
     * @param user the OAuth2 user to register
     * @return the registered user
     * @throws RuntimeException if OAuth user already exists
     */
    @Override
    @Transactional
    public User registerOAuthUser(User user) {
        if (checkUsernameExists(user.getUsername())) {
            throw new RuntimeException("OAuth user already exists");
        }
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            user.setNickname(user.getUsername());
        }
        // Set default password for OAuth2 users (can be modified or set to null later)
        user.setPassword(passwordEncoder.encode("oauth2user"));
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setEnabled(true);
        // Requires provider and providerId to be set before calling this method
        save(user);
        
        // Create default wishlist for the new OAuth user
        playListService.createWishListForUser(user.getId());
        
        return user;
    }

    /**
     * Updates OAuth2 user information and ensures provider and providerId are saved.
     * Called during OAuth2 login if user exists but provider/providerId are not set.
     * 
     * @param user the user to update
     * @param attributes OAuth2 attributes from the provider
     * @param provider the OAuth2 provider name
     * @param providerId the user ID from the OAuth2 provider
     * @return the updated user
     */
    @Override
    @Transactional
    public User updateUserFromOAuth2(User user, Map<String, Object> attributes, String provider, String providerId) {
        // Update user information based on OAuth2 attributes, e.g., update nickname
        if (attributes.get("name") != null) {
            user.setNickname((String) attributes.get("name"));
        }
        user.setUpdatedAt(LocalDateTime.now());

        // If user's provider and providerId are not set, update them
        if (user.getProvider() == null || user.getProvider().isEmpty()) {
            user.setProvider(provider);
        }
        if (user.getProviderId() == null || user.getProviderId().isEmpty()) {
            user.setProviderId(providerId);
        }
        updateById(user);
        return user;
    }

    @Override
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            String username = authentication.getName();
            User user = this.getOne(new QueryWrapper<User>().eq("username", username));
            return user != null ? user.getId() : null;
        }
        return null;
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {

            String username = authentication.getName();
            return this.getOne(new QueryWrapper<User>().eq("username", username));
        }
        return null;
    }
}
