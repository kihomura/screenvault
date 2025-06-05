package com.kihomura.screenvault.service.impl;

import com.kihomura.screenvault.entity.User;
import com.kihomura.screenvault.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Implementation of Spring Security's UserDetailsService interface.
 * Loads user-specific data for authentication and authorization purposes.
 * Uses lazy injection to break circular dependency with UserService.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;

    /**
     * Constructor with lazy injection to break circular dependency.
     * 
     * @param userService user service for loading user data
     */
    public UserDetailsServiceImpl(@Lazy UserService userService) {
        this.userService = userService;
    }

    /**
     * Loads user by username for Spring Security authentication.
     * Converts application User entity to Spring Security UserDetails.
     * 
     * @param username the username identifying the user
     * @return UserDetails object for Spring Security
     * @throws UsernameNotFoundException if user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("LoadUserByUsername - Raw username: [" + username + "]");
        System.out.println("LoadUserByUsername - Username length: " + (username != null ? username.length() : "null"));

        User user = userService.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
}