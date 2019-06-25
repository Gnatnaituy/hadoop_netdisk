package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.model.MyFile;
import org.jetbrains.hadoop_netdisk.model.User;
import org.jetbrains.hadoop_netdisk.service.MyFileService;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.jetbrains.hadoop_netdisk.util.MD5Util;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * @auther hasaker
 * @create_date 2019-06-20 10:17
 * @description
 */
@RestController
@RequestMapping("file")
public class MyFileController {
    private final MyFileService myFileService;
    private final HdfsService hdfsService;
    private final UserService userService;

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
    public String upload(HttpServletRequest request) {
        String srcFile = request.getSession().getAttribute("srcFile").toString();
        String desPath = request.getSession().getAttribute("desPath").toString();
        String currentUser = request.getSession().getAttribute("currentUser").toString();

        // 上传文件到Hadoop
        hdfsService.upload(srcFile, desPath);

        // 插入文件记录
        File file = new File(srcFile);
        String fileMD5HashCode = MD5Util.getFileMD5(file);
        String fileName = srcFile.split("/")[srcFile.split("/").length - 1];
        int fileSize = (int) file.length();
        myFileService.insert(new MyFile(fileMD5HashCode, srcFile, fileName, currentUser, fileSize));

        // 更新用户容量使用信息
        User user = userService.query(currentUser);
        user.setUsedCapacity(user.getUsedCapacity() + fileSize);
        user.setTotalCapacity(user.getTotalCapacity() - fileSize);
        userService.update(user);

        return "redirect:/user/main";
    }
}
