package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.model.MyFile;
import org.jetbrains.hadoop_netdisk.model.User;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.MyFileService;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
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
    
    private final String CURRENT_USER = "currentUser";
    private final String MSG = "msg";

    private Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, HdfsService hdfsService, MyFileService myFileService) {
        this.userService = userService;
        this.hdfsService = hdfsService;
        this.myFileService = myFileService;
    }

    /**
     * Main page controller
     */
    @GetMapping("/main")
    public String main(Model model, HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        logger.info(MessageFormat.format("Current user --> {0}", user.getUsername()));

        List<Map<String, Object>> fileListMap = hdfsService.listFiles(user.getUsername(), null);

        // Add user information
        model.addAttribute("user", user);
        // Add user file information
        model.addAttribute("fileListMap", fileListMap);

        return "main";
    }

    /**
     * Personal information page
     */
//    @GetMapping("/about")
//    public String about(Model model, HttpServletRequest request) {
//        User user = userService.getCurrentUser(request);
//        logger.info(MessageFormat.format("Current user --> {0}", user.getUsername()));
//
//        List<MyFile> mySharedFiles = myFileService.getSharedFilesByUsername(user.getUsername());
//
//        // All the files that user shared
//        model.addAttribute("mySharedFiles", mySharedFiles);
//        // User's information
//        model.addAttribute("user", user);
//
//        return "about";
//    }

    /**
     * Login View
     */
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("user", new User());

        // Display tips if there is
        if (request.getSession().getAttribute(MSG) != null) {
            model.addAttribute(MSG, request.getSession().getAttribute(MSG).toString());
            request.getSession().removeAttribute(MSG);
        }

        return "login";
    }

    /**
     * Do login
     */
    @PostMapping("/login")
    public String doLogin(@ModelAttribute User user, Model model, HttpServletRequest request) {
        User target = userService.checkLogin(user.getUsername(), user.getPassword());

        if (target == null) {
            request.getSession().setAttribute(MSG, "用户不存在或密码错误!");

            return "redirect:/user/login";
        }

        model.addAttribute("user", target);
        request.getSession().setAttribute(CURRENT_USER, user.getUsername());

        return "redirect:/user/main";
    }

    /**
     * Logout
     */
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();

        return "redirect:/user/login";
    }

    /**
     * Register view
     */
    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new User());

        return "register";
    }

    /**
     * Do register
     */
    @PostMapping("/register")
    public String doRegister(@ModelAttribute User user, HttpServletRequest request) {
        if (userService.query(user.getUsername()) != null) {
            request.getSession().setAttribute(MSG, "用户已存在!");

            return "redirect:/user/register";
        }

        userService.register(user);

        return "redirect:/user/login";
    }

    /**
     * Unregister
     */
    @GetMapping("/unregister")
    public String unregister(HttpServletRequest request) {
        User user = userService.getCurrentUser(request);
        logger.info(MessageFormat.format("User --> {0} unregistered!", user.getUsername()));
        userService.delete(user);

        return "redirect:/user/login";
    }
}
