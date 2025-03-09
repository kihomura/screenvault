package com.kihomura.screenvault.service;

import com.kihomura.screenvault.pojo.User;
import com.kihomura.screenvault.pojo.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {

    /**
     * add user
     * @param user
     */

    User add(UserDto user);
}
