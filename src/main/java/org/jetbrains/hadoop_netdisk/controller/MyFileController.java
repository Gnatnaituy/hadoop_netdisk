package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.model.MyFile;
import org.jetbrains.hadoop_netdisk.model.User;
import org.jetbrains.hadoop_netdisk.service.MyFileService;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.jetbrains.hadoop_netdisk.util.FileUtil;
import org.jetbrains.hadoop_netdisk.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.MessageFormat;

/**
 * @auther hasaker
 * @create_date 2019-06-20 10:17
 * @description
 */
@Controller
@RequestMapping("file")
public class MyFileController {
    private final MyFileService myFileService;
    private final HdfsService hdfsService;
    private final UserService userService;
    private Logger logger = LoggerFactory.getLogger(MyFileController.class);

    public MyFileController(MyFileService myFileService, HdfsService hdfsService, UserService userService) {
        this.myFileService = myFileService;
        this.hdfsService = hdfsService;
        this.userService = userService;
    }

    /**
     * Get file detail information
     */
    @GetMapping("query/{fullName}")
    public String query(@PathVariable String fullName) {
        MyFile myFile = myFileService.getDetailByFullName(fullName);

        return myFile == null ? "myFile not found": myFile.toString();
    }

    /**
     * Upload File from local machine to Hadoop
     */
    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String currentUser = request.getSession().getAttribute("currentUser").toString();
        logger.info(MessageFormat.format("Current logged in user:       {0}", currentUser));

        // Upload to Hadoop
        myFileService.upload(currentUser, file);

        return "redirect:/user/main";
    }

    /**
     * Download file from Hadoop to Local directly
     */
    @GetMapping("/download")
    public int download(@RequestParam String fullName, HttpServletRequest request) {
        String currentUser = request.getSession().getAttribute("currentUser").toString();

        return myFileService.download(currentUser, fullName);
    }

    @PostMapping("/rename")
    public String rename(@RequestParam String srcName, @RequestParam String desName) {
        hdfsService.rename(srcName, desName);

        return "redirect:/user/main";
    }

    @PostMapping("/share")
    public String share(@RequestParam String fullName) {

        return "redirect:/user/main";
    }
}
