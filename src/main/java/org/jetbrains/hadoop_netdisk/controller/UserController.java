package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.model.MyFile;
import org.jetbrains.hadoop_netdisk.model.User;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.MyFileService;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.jetbrains.hadoop_netdisk.util.MD5Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;


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
    private final MyFileService myFileService;

    public UserController(UserService userService, HdfsService hdfsService, MyFileService myFileService) {
        this.userService = userService;
        this.hdfsService = hdfsService;
        this.myFileService = myFileService;
    }

    /**
     * 网盘首页
     */
    @GetMapping("/main")
    public String main(Model model, HttpSession session) {
        User user = userService.getCurrentUser(session);
        List<Map<String, Object>> fileList = hdfsService.listFiles(user.getUsername(), null);

        // 用户信息
        model.addAttribute("user", user);
        // 用户家目录
        model.addAttribute("fileList", fileList);

        return "main";
    }

    /**
     * 个人主页
     */
    @PostMapping("/about")
    public String about(Model model, HttpSession session) {
        User user = userService.getCurrentUser(session);
        List<MyFile> mySharedFiles = myFileService.getSharedFilesByUsername(user.getUsername());

        // 用户分享文件列表
        model.addAttribute("mySharedFiles", mySharedFiles);
        // 用户信息
        model.addAttribute("user", user);

        return "about";
    }

    /**
     * 登录界面
     */
    @GetMapping("/login")
    public String login(Model model, HttpSession session) {
        model.addAttribute("user", new User());

        // 如果有提示信息 则在登录页面显示
        if (session.getAttribute("msg") != null) {
            model.addAttribute("msg", session.getAttribute("msg").toString());
            session.removeAttribute("msg");
        }

        return "login";
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public String doLogin(@ModelAttribute User user, Model model, HttpSession session) {
        User target = userService.query(user.getUsername());

        if (target == null) {
            session.setAttribute("msg", "用户不存在!");

            return "redirect:/user/login";
        } else if (!target.getHashedPassword().equals(MD5Util.getStringMD5(user.getHashedPassword()))) {
            session.setAttribute("msg", "密码错误!");

            return "redirect:/user/login";
        }

        model.addAttribute("user", target);
        session.setAttribute("currentUser", user.getUsername());

        return "redirect:/user/main";
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/user/login";
    }

    /**
     * 注册界面
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());

        return "register";
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    public String doRegister(@ModelAttribute User user, HttpSession session) {
        if (userService.query(user.getUsername()) != null) {
            session.setAttribute("msg", "用户已存在!");

            return "redirect:/user/register";
        }

        user.setHashedPassword(MD5Util.getStringMD5(user.getHashedPassword()));

        // 将用户信息存入MySQL中
        userService.add(user);

        // 在hadoop中创建用户家目录
        hdfsService.mkdir(user.getUsername());

        return "redirect:/user/login";
    }

    /**
     * 注销
     */
    @PostMapping("/unregister")
    public String unregister(HttpSession session) {
        User user = userService.getCurrentUser(session);
        userService.delete(user);

        return "redirect:/user/login";
    }
}
