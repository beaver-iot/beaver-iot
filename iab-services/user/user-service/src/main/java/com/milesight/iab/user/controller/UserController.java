package com.milesight.iab.user.controller;

import com.milesight.iab.user.request.UserRegisterRequest;
import com.milesight.iab.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author loong
 * @date 2024/10/14 8:40
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/register")
    public String register(UserRegisterRequest userRegisterRequest) {
        return userService.register(userRegisterRequest);
    }

}