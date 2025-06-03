package com.kihomura.screenvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kihomura.screenvault.entity.User;

import java.util.Map;

public interface UserService extends IService<User> {
    User register(User user);
    User findByUsername(String username);
    boolean checkUsernameExists(String username);
    User updateUser(User user);
    User registerOAuthUser(User user);
    User updateUserFromOAuth2(User user, Map<String, Object> attributes, String provider, String providerId);
    Integer getCurrentUserId();
    User getCurrentUser();
}