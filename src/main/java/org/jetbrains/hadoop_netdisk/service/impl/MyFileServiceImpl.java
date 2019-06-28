package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.model.MyFile;
import org.jetbrains.hadoop_netdisk.mapper.MyFileMapper;
import org.jetbrains.hadoop_netdisk.model.User;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.MyFileService;
import org.jetbrains.hadoop_netdisk.service.UserService;
import org.jetbrains.hadoop_netdisk.util.FileUtil;
import org.jetbrains.hadoop_netdisk.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

/**
 * @auther hasaker
 * @create_date 2019-06-20 11:37
 * @description
 */
@Service
public class MyFileServiceImpl implements MyFileService {
    private final MyFileMapper myFileMapper;
    private final HdfsService hdfsService;
    private final UserService userService;

    private final Logger logger = LoggerFactory.getLogger(MyFileServiceImpl.class);

    public MyFileServiceImpl(MyFileMapper myFileMapper, HdfsService hdfsService, UserService userService) {
        this.myFileMapper = myFileMapper;
        this.hdfsService = hdfsService;
        this.userService = userService;
    }

    public MyFile getDetailByFileMD5HashCode(String fileMD5HashCode) {
        return myFileMapper.getDetailByFileMD5HashCode(fileMD5HashCode);
    }

    public MyFile getDetailByFullName(String fullName) {
        return myFileMapper.getDetailByFullName(fullName);
    }

    public List<MyFile> getSharedFiles() {
        return myFileMapper.getSharedFiles();
    }

    public List<MyFile> getSharedFilesOrderByDownloadCount() {
        return myFileMapper.getSharedFilesOrderByDownloadCount();
    }

    public List<MyFile> getSharedFilesOrderByShareDate() {
        return myFileMapper.getSharedFilesOrderByShareDate();
    }

    public List<MyFile> getSharedFilesByUsername(String username) {
        return myFileMapper.getSharedFilesByUsername(username);
    }

    public int getSharedFilesCount(String username) {
        return myFileMapper.getSharedFilesCount(username);
    }

    public int getTotalDownloadCount(String username) {
        return myFileMapper.getTotalDownloadCount(username);
    }

    public int increaseDownloadCount(String fileMD5HashCode) {
        return myFileMapper.increaseDownloadCount(fileMD5HashCode);
    }

    public List<MyFile> searchFiles(String query) {
        return myFileMapper.searchFiles(query);
    }

    public List<MyFile> searchSharedFiles(String query) {
        return myFileMapper.searchSharedFiles(query);
    }

    public int rename(String fileMD5HashCode) {
        return myFileMapper.rename(fileMD5HashCode);
    }

    public int delete(String fileMD5HashCode) {
        return myFileMapper.delete(fileMD5HashCode);
    }

    public int insert(MyFile myFile) {
        return myFileMapper.insert(myFile);
    }

    public int share(String fileMD5HasCode) {
        return myFileMapper.share(fileMD5HasCode);
    }

    public int upload(String currentUser, MultipartFile multipartFile) {
        // Convert multipartFile to File
        File localFile = FileUtil.multipartFileToFile(multipartFile);
        logger.info(MessageFormat.format("File path on Tomcat server:   {0}", localFile.getPath()));

        // Upload `localFile` to Hadoop
        String desPath = hdfsService.upload(localFile, currentUser + "/" + localFile.getName());
        logger.info(MessageFormat.format("Destination path on Hadoop:   {0}", desPath));

        // Insert file record to hadoop.files
        String fileMD5HashCode = MD5Util.getFileMD5(localFile);
        String fileName = localFile.getName();
        // Convert bit to MB, 1MB = 1024 * 1024 = 1048576bit
        double fileSize = localFile.length() / 1048576.0;
        logger.info(MessageFormat.format("FileMD5HashCode:  {0}", fileMD5HashCode));
        logger.info(MessageFormat.format("FileName:         {0}", fileName));
        logger.info(MessageFormat.format("FileSize:         {0}MB", fileSize));
        int uploadRes = insert(new MyFile(currentUser, fileMD5HashCode, desPath, fileName, fileSize));

        // Update user capacity information
        User user = userService.query(currentUser);
        user.setUsedCapacity(user.getUsedCapacity() + fileSize);
        userService.updateUsedCapacity(user);

        // Delete file on Tomcat server
        localFile.delete();

        return uploadRes;
    }

    public int download(String currentUser, String fullName) {
        MyFile file = getDetailByFullName(fullName);
        int downloadRes = 0;

        // 只能下载自己的或者已经分享的文件
        if (file.isShared() || currentUser.equals(file.getOwner())) {
            downloadRes = increaseDownloadCount(file.getFileMD5HashCode());
            // 默认下载到用户的Downloads目录
            hdfsService.download(file.getFullName(), FileUtil.getUserDownloadsDir());
        } else {
            System.out.println("你无权下载此文件!");
        }

        return downloadRes;
    }
}
