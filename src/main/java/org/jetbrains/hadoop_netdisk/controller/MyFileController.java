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

    @GetMapping("query/{fileName}")
    public String query(@PathVariable String fileName) {
        MyFile myFile = myFileService.getDetail(fileName);

        return myFile == null ? "myFile not found": myFile.toString();
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        String currentUser = request.getSession().getAttribute("currentUser").toString();
        logger.info(MessageFormat.format("当前登录用户:  {0}", currentUser));

        File localFile = FileUtil.multipartFileToFile(file);
        logger.info(MessageFormat.format("原始文件路径:  {0}", localFile.getPath()));

        // 上传文件到hadoop
        String desPath = hdfsService.upload(localFile, currentUser + "/" + localFile.getName());
        logger.info(MessageFormat.format("Hadoop目标路径:  {0}", desPath));

        // 插入文件记录
        String fileMD5HashCode = MD5Util.getFileMD5(localFile);
        double fileSize = localFile.length() / 1048576.0;
        logger.info(MessageFormat.format("文件大小:  {0}MB", fileSize));
        myFileService.insert(new MyFile(currentUser, fileMD5HashCode, desPath, localFile.getName(), fileSize));

        // 更新用户容量使用信息
        User user = userService.query(currentUser);
        user.setUsedCapacity(user.getUsedCapacity() + fileSize);
        userService.updateUsedCapacity(user);

        // 删除储存在服务器的文件
        localFile.delete();

        return "redirect:/user/main";
    }

    @PostMapping("/rename")
    public void rename() {

    }
}
