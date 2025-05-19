package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.mapper.UserMapper;
import com.kihomura.screenvault.pojo.User;
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

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private final PlayListService playListService;

    public UserServiceImpl(UserMapper userMapper, @Lazy PlayListService playListService) {
        this.userMapper = userMapper;
        this.playListService = playListService;
    }

    // Break circular dependency by injecting PasswordEncoder through setter
    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

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
     * 注册 OAuth2 用户，同时保存 provider 和 providerId 信息
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
        // 为 OAuth2 用户设置默认密码（后续可支持修改或置空）
        user.setPassword(passwordEncoder.encode("oauth2user"));
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        user.setEnabled(true);
        // 此处要求在调用前已设置好 provider 和 providerId
        save(user);
        
        // Create default wishlist for the new OAuth user
        playListService.createWishListForUser(user.getId());
        
        return user;
    }

    /**
     * 更新 OAuth2 用户信息，同时确保 provider 与 providerId 被保存
     * 在 OAuth2 登录过程中，如果用户已存在，且 provider 与 providerId 尚未设置，则进行保存
     */
    @Override
    @Transactional
    public User updateUserFromOAuth2(User user, Map<String, Object> attributes, String provider, String providerId) {
        // 根据 OAuth2 返回的属性更新用户信息，例如更新昵称
        if (attributes.get("name") != null) {
            user.setNickname((String) attributes.get("name"));
        }
        user.setUpdatedAt(LocalDateTime.now());

        // 如果用户的 provider 和 providerId 还未设置，则更新
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
