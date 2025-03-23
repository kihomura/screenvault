package com.kihomura.screenvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kihomura.screenvault.pojo.User;

public interface UserService extends IService<User> {
    User register(User user);
    User findByUsername(String username);
    boolean checkUsernameExists(String username);
    User updateUser(User user);
}