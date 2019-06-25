package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.model.User;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.jetbrains.hadoop_netdisk.util.MD5Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


/**
 * @auther hasaker
 * @create_date 2019-06-19 16:44
 * @description
 */
@Controller
@RequestMapping("user")
public class UserController {

    private final UserService userService;
    private final HdfsService hdfsService;

    public UserController(UserService userService, HdfsService hdfsService) {
        this.userService = userService;
        this.hdfsService = hdfsService;
    }

    /**
     * 网盘首页
     */
    @GetMapping("/main/{username}")
    public String main(@PathVariable String username, Model model, HttpServletRequest request) {
        if (request.getSession().getAttribute(username) == null) {
            return "redirect:/user/login";
        }

        User user = userService.query(username);

        model.addAttribute("user", user);

        return "main";
    }

    /**
     * 个人主页
     */
    @GetMapping("/about/{username}")
    public String about(@PathVariable String username, Model model, HttpServletRequest request) {
        // 判断当前用户是否登录
        if (request.getSession().getAttribute(username) != null) {
            model.addAttribute("user", userService.query(username));

            return "about";
        } else {
            return "redirect:/user/login";
        }
    }

    /**
     * 登录界面
     */
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("user", new User());

        String[] messageCode = new String[]{"wrongPassword", "registerSuccess", "noUser"};
        String[] message = new String[]{"密码错误!", "注册成功!", "用户不存在!"};

        for (int i = 0; i < messageCode.length; i++) {
            if (request.getSession().getAttribute(messageCode[i]) != null) {
                model.addAttribute(messageCode[i], message[i]);
                request.getSession().removeAttribute(messageCode[i]);
            }
        }

        return "login";
    }

    /**
     * 登录
     *  用户不存在 --> 跳转注册界面
     *  密码错误   --> 提示密码错误
     *  登录成功   --> 跳转网盘首页
     */
    @PostMapping("/login")
    public String doLogin(@ModelAttribute User user, Model model, HttpServletRequest request) {
        User target = userService.query(user.getUsername());

        if (target == null) {
            request.getSession().setAttribute("noUser", true);

            return "redirect:/user/login";
        } else if (!target.getHashedPassword().equals(MD5Util.getStringMD5(user.getHashedPassword()))) {
            request.getSession().setAttribute("wrongPassword", true);

            return "redirect:/user/login";
        }

        model.addAttribute("user", target);
        request.getSession().setAttribute("currentUser", user.getUsername());

        return "redirect:/user/main/" + user.getUsername();
    }

    /**
     * 注册界面
     */
    @GetMapping("/register")
    public String register(Model model, HttpServletRequest request) {
        model.addAttribute("user", new User());

        if (request.getSession().getAttribute("userExisted") != null) {
            model.addAttribute("userExisted", true);
            request.getSession().removeAttribute("userExisted");
        }

        return "register";
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public String doRegister(@ModelAttribute User user, HttpServletRequest request) {
        if (userService.query(user.getUsername()) != null) {
            request.getSession().setAttribute("userExisted", true);

            return "redirect:/user/register";
        }

        user.setHashedPassword(MD5Util.getStringMD5(user.getHashedPassword()));

        // 将用户信息存入MySQL中
        userService.add(user);

        // 在hadoop中创建用户家目录
        hdfsService.mkdir(user.getUsername());

        return "redirect:/user/login";
    }
}
