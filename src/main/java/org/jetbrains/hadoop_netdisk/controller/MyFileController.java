package org.jetbrains.hadoop_netdisk.controller;

import org.jetbrains.hadoop_netdisk.model.MyFile;
import org.jetbrains.hadoop_netdisk.service.MyFileService;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;

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
    private Logger logger = LoggerFactory.getLogger(HdfsService.class);

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
        String srcFile = file.getOriginalFilename();
        logger.info(MessageFormat.format("文件原始路径:  #{0}", srcFile));
        String desPath = request.getSession().getAttribute("desPath").toString();
        logger.info(MessageFormat.format("Hadoop目标路径:  #{0}", desPath));
        String currentUser = request.getSession().getAttribute("currentUser").toString();

        // 上传文件到Hadoop
        hdfsService.upload(srcFile, desPath);

        // 插入文件记录
//        String fileMD5HashCode = MD5Util.getFileMD5(file.transferTo(new File()));
//        String fileName = srcFile.split("/")[srcFile.split("/").length - 1];
//        String fullName = desPath + "/" + fileName;
//        int fileSize = (int) file.getSize();
//        myFileService.insert(new MyFile(fileMD5HashCode, fullName, fileName, currentUser, fileSize));

//        // 更新用户容量使用信息
//        User user = userService.query(currentUser);
//        user.setUsedCapacity(user.getUsedCapacity() + fileSize);
//        user.setTotalCapacity(user.getTotalCapacity() - fileSize);
//        userService.update(user);

        return "redirect:/user/main";
    }

    @PostMapping("/rename")
    public void rename() {

    }
}
