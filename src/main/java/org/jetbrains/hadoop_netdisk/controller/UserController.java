package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.model.User;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.jetbrains.hadoop_netdisk.util.MD5Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * @auther hasaker
 * @create_date 2019-06-19 16:44
 * @description
 */
@Controller
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("user", new User());

        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@ModelAttribute User user) {
        User target = userService.query(user.getUsername());

        if (target == null || !target.getHashedPassword().equals(MD5Util.getStringMD5(user.getHashedPassword())))
            return "failed";

        return "success";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());

        return "register";
    }

    @PostMapping("/register")
    public String doRegister(@ModelAttribute User user) {
        user.setHashedPassword(MD5Util.getStringMD5(user.getHashedPassword()));
        userService.add(user);

        return "success";
    }
}
