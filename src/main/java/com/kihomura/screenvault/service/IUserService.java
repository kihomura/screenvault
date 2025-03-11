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

    /**
     * get user
     * @param id user's id
     */
    User getUser(Integer id);

    /**
     * update user
     * @param user user object
     */
    User update(UserDto user);

    /**
     * delete user
     * @param id user id
     */
    void delete(Integer id);
}
