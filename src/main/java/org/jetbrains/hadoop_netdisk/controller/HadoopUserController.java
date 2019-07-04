package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.entity.HadoopFile;
import org.jetbrains.hadoop_netdisk.entity.HadoopUser;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.HadoopFileService;
import org.jetbrains.hadoop_netdisk.service.HadoopUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sun.awt.ModalExclude;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.*;


/**
 * @auther hasaker
 * @create_date 2019-06-19 16:44
 * @description
 */
@Controller
@RequestMapping("user")
public class HadoopUserController {

    private final HadoopUserService hadoopUserService;
    private final HdfsService hdfsService;
    private final HadoopFileService hadoopFileService;
    
    private final String CURRENT_USER = "currentUser";
    private final String CURRENT_PATH = "currentPath";
    private final String PATHS = "paths";
    private final String USER_HADOOP_FILES = "userHadoopFiles";
    private final String USER_MYSQL_FILES = "userMySQLFiles";
    private final String USER_DELETED_FILES = "userDeletedFiles";
    private final String SHARED_FILES = "sharedFiles";

    private final String MSG = "msg";

    private Logger logger = LoggerFactory.getLogger(HadoopUserController.class);

    public HadoopUserController(HadoopUserService hadoopUserService, HdfsService hdfsService,
                                HadoopFileService hadoopFileService) {
        this.hadoopUserService = hadoopUserService;
        this.hdfsService = hdfsService;
        this.hadoopFileService = hadoopFileService;
    }

    /**
     * Main page controller
     */
    @GetMapping("/main")
    public String main(Model model, HttpServletRequest request) {
        HadoopUser currentUser = hadoopUserService.getCurrentUser(request);
        String currentPath = hadoopFileService.getCurrentDir(request);

        List<String[]> paths = new ArrayList<>();
        StringBuilder absPath = new StringBuilder();
        for (String path : currentPath.split("/")) {
            absPath.append(path).append("/");
            paths.add(new String[]{path, absPath.toString()});
        }

        List<Map<String, Object>> userHadoopFiles = hdfsService.listFiles(currentPath, null);
        List<HadoopFile> userMySQLFiles = hadoopFileService.getUserFiles(currentUser.getUsername());
        List<HadoopFile> userDeletedFiles = hadoopFileService.getUserDeletedFiles(currentUser.getUsername());
        List<HadoopFile> sharedFiles = hadoopFileService.getSharedFiles();

        model.addAttribute(CURRENT_USER, currentUser);
        model.addAttribute(CURRENT_PATH, currentPath);
        model.addAttribute(PATHS, paths);
        model.addAttribute(USER_HADOOP_FILES, userHadoopFiles);
        model.addAttribute(USER_MYSQL_FILES, userMySQLFiles);
        model.addAttribute(USER_DELETED_FILES, userDeletedFiles);
        model.addAttribute(SHARED_FILES, sharedFiles);

        return "main";
    }

    /**
     * Update capacity information
     */
    @GetMapping("/updateCapacity")
    public String updateCapacity(Model model, HttpServletRequest request) {
        HadoopUser currentUser = hadoopUserService.query(request.getSession().getAttribute(CURRENT_USER).toString());
        model.addAttribute(CURRENT_USER, currentUser);

        return "main::capacity_refresh";
    }

    /**
     * Update capacity information
     */
    @GetMapping("/updateNavbar")
    public String updateNavbar(Model model, HttpServletRequest request) {
        String currentPath = hadoopFileService.getCurrentDir(request);

        List<String[]> paths = new ArrayList<>();
        StringBuilder absPath = new StringBuilder();
        for (String path : currentPath.split("/")) {
            absPath.append(path).append("/");
            paths.add(new String[]{path, absPath.toString()});
        }

        model.addAttribute(CURRENT_PATH, currentPath);
        model.addAttribute(PATHS, paths);

        return "main::capacity_refresh";
    }

    /**
     * Update Personal information
     */
    @PostMapping("/edit")
    public String about(@RequestParam HadoopUser user) {
        hadoopUserService.update(user);

        return "redirect:/user/main";
    }

    /**
     * Login View
     */
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("user", new HadoopUser());

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
    public String doLogin(@ModelAttribute HadoopUser user, Model model, HttpServletRequest request) {
        HadoopUser target = hadoopUserService.checkLogin(user.getUsername(), user.getPassword());

        if (target == null) {
            request.getSession().setAttribute(MSG, "用户不存在或密码错误!");

            return "redirect:/user/login";
        }

        model.addAttribute("user", target);
        request.getSession().setAttribute(CURRENT_USER, user.getUsername());
        request.getSession().setAttribute(CURRENT_PATH, user.getUsername() + "/");

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
        model.addAttribute("user", new HadoopUser());

        return "register";
    }

    /**
     * Do register
     */
    @PostMapping("/register")
    public String doRegister(@ModelAttribute HadoopUser user, HttpServletRequest request) {
        if (hadoopUserService.query(user.getUsername()) != null) {
            request.getSession().setAttribute(MSG, "用户已存在!");

            return "redirect:/user/register";
        }

        hadoopUserService.register(user);

        return "redirect:/user/login";
    }

    /**
     * Unregister
     */
    @GetMapping("/unregister")
    public String unregister(HttpServletRequest request) {
        HadoopUser hadoopUser = hadoopUserService.getCurrentUser(request);
        logger.info(MessageFormat.format("HadoopUser --> {0} unregistered!", hadoopUser.getUsername()));
        hadoopUserService.delete(hadoopUser);

        return "redirect:/user/login";
    }
}
