package com.kihomura.screenvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kihomura.screenvault.mapper.UserMapper;
import com.kihomura.screenvault.pojo.User;
import com.kihomura.screenvault.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final UserMapper userMapper;
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    //Break circular dependency by injecting PasswordEncoder through setter
    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User register(User user) {
        //check if the username is existed
        if (checkUsernameExists(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        //use username as the default nickname
        if (user.getNickname() == null || user.getNickname().trim().isEmpty()) {
            user.setNickname(user.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);

        user.setEnabled(true);

        save(user);

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

        //update nickname
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }

        //if password is updated, encrypt it first
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        existingUser.setUpdatedAt(LocalDateTime.now());
        updateById(existingUser);

        return existingUser;
    }

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
        save(user);
        return user;
    }

    @Override
    @Transactional
    public User updateUserFromOAuth2(User user, Map<String, Object> attributes) {
        // 根据 OAuth2 返回的属性更新用户信息，例如昵称同步
        if (attributes.get("name") != null) {
            user.setNickname((String) attributes.get("name"));
        }
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);
        return user;
    }
}