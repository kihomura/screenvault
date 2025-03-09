package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.pojo.ResponseMessage;
import com.kihomura.screenvault.pojo.User;
import com.kihomura.screenvault.pojo.dto.UserDto;
import com.kihomura.screenvault.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    /*
     * POST: /user
     */
    @PostMapping
    public ResponseMessage<User> add(@RequestBody UserDto user) {
        User newUser = userService.add(user);
        return ResponseMessage.success(newUser);
    }
}