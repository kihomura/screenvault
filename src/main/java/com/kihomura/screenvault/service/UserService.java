package com.kihomura.screenvault.service;

import com.kihomura.screenvault.pojo.User;
import com.kihomura.screenvault.pojo.dto.UserDto;
import com.kihomura.screenvault.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public User add(UserDto user) {

        User userPojo = new User();
        BeanUtils.copyProperties(user, userPojo);

        return userRepository.save(userPojo);
    }
}
