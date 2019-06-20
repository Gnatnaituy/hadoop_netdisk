package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.entity.User;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther hasaker
 * @create_date 2019-06-19 16:44
 * @description
 */
@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping("query/{username}")
    public String query(@PathVariable String username) {
        User user = userService.query(username);

        return user == null ? "No such user" : user.toString();
    }
}
