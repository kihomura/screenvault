package com.kihomura.screenvault.controller;

import com.kihomura.screenvault.pojo.dto.ResponseMessage;
import com.kihomura.screenvault.pojo.User;
import com.kihomura.screenvault.pojo.dto.UserDto;
import com.kihomura.screenvault.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    IUserService userService;

    /*
     * GET: /user/{userID}
     */
    @GetMapping
    public ResponseMessage add(@PathVariable Integer id) {
        User user = userService.getUser(id);
        return ResponseMessage.success(user);
    }

    /*
     * POST: /user
     */
    @PostMapping
    public ResponseMessage<User> get(@Validated @RequestBody UserDto user) {
        User newUser = userService.add(user);
        return ResponseMessage.success(newUser);
    }

    /*
     * PUT: /user/{userID}
     */
    @PutMapping
    public ResponseMessage<User> update(@Validated @RequestBody UserDto user) {
        User userUpdate = userService.update(user);
        return ResponseMessage.success(userUpdate);
    }

    /*
     * DELETE: /user/{userID}
     */
    @DeleteMapping
    public ResponseMessage<User> delete(@Validated @RequestBody UserDto user) {
        userService.delete(user.getId());
        return ResponseMessage.success();
    }
}