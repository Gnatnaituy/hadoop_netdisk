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

        List<Map<String, Object>> hadoopFileList = hdfsService.listFiles(currentPath, null);
        List<HadoopFile> mySqlFileList = hadoopFileService.getUserFiles(currentUser.getUsername());
        List<HadoopFile> userDeletedFiles = hadoopFileService.getUserDeletedFiles(currentUser.getUsername());
        List<HadoopFile> sharedFileList = hadoopFileService.getSharedFiles();

        model.addAttribute("user", currentUser);
        model.addAttribute(CURRENT_PATH, currentPath);
        model.addAttribute("paths", paths);
        model.addAttribute("hadoopFileList", hadoopFileList);
        model.addAttribute("mySqlFileList", mySqlFileList);
        model.addAttribute("userDeletedFiles", userDeletedFiles);
        model.addAttribute("sharedFileList", sharedFileList);

        return "main";
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
