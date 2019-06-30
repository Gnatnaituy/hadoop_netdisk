package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.entity.HadoopFile;
import org.jetbrains.hadoop_netdisk.mapper.HadoopFileMapper;
import org.jetbrains.hadoop_netdisk.entity.HadoopUser;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.jetbrains.hadoop_netdisk.service.HadoopFileService;
import org.jetbrains.hadoop_netdisk.service.HadoopUserService;
import org.jetbrains.hadoop_netdisk.util.FileUtil;
import org.jetbrains.hadoop_netdisk.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;

/**
 * @auther hasaker
 * @create_date 2019-06-20 11:37
 * @description
 */
@Service
public class HadoopFileServiceImpl implements HadoopFileService {
    private final HadoopFileMapper hadoopFileMapper;
    private final HdfsService hdfsService;
    private final HadoopUserService hadoopUserService;

    private final Logger logger = LoggerFactory.getLogger(HadoopFileServiceImpl.class);

    public HadoopFileServiceImpl(HadoopFileMapper hadoopFileMapper, HdfsService hdfsService, HadoopUserService hadoopUserService) {
        this.hadoopFileMapper = hadoopFileMapper;
        this.hdfsService = hdfsService;
        this.hadoopUserService = hadoopUserService;
    }

    public String getCurrentDir(HttpServletRequest request) {

        return request.getSession().getAttribute("currentDir").toString();
    }

    public HadoopFile query(String hashCode) {
        return hadoopFileMapper.query(hashCode);
    }

    public boolean mkdir(String path) {

        return hdfsService.mkdir(path);
    }

    public int rename(String oldHdfsPath, String newFileName, boolean isDir, String hashCode) {
        String newHdfsPath = FileUtil.exceptFileName(oldHdfsPath, isDir) + newFileName;

        hdfsService.rename(FileUtil.getRelativePath(oldHdfsPath), FileUtil.getRelativePath(newHdfsPath));

        return hadoopFileMapper.rename(newHdfsPath, hashCode);
    }

    public int fakeDelete(String hashCode) {
        hdfsService.fakeDelete(FileUtil.getRelativePath(query(hashCode).getHdfsPath()));
        String newHdfsPath = query(hashCode).getHdfsPath() + "-deleted";

        return hadoopFileMapper.fakeDelete(newHdfsPath, hashCode);
    }

    public int realDelete(String hashCode) {
        hdfsService.realDelete(FileUtil.getRelativePath(query(hashCode).getHdfsPath()));

        return hadoopFileMapper.realDelete(hashCode);
    }

    public int insert(HadoopFile hadoopFile) {
        return hadoopFileMapper.insert(hadoopFile);
    }

    public int share(boolean shareEncrypt, String shareEncryptCode, String hashCode) {
        return hadoopFileMapper.share(shareEncrypt, shareEncryptCode, hashCode);
    }

    public int upload(String currentUser, MultipartFile multipartFile) {
        // Convert multipartFile to File
        File localFile = FileUtil.multipartFileToFile(multipartFile);
        logger.info(MessageFormat.format("File path on Tomcat server:   {0}", localFile.getPath()));

        String hashCode = MD5Util.getFileMD5(localFile);
        // Convert bit to MB, 1MB = 1024 * 1024 = 1048576bit
        double fileSize = localFile.length() / 1048576.0;
        logger.info(MessageFormat.format("HashCode:     {0}", hashCode));
        logger.info(MessageFormat.format("FileSize:     {0} MB", fileSize));

        // Upload localFile to Hadoop and convert file name to hashcode-filename
        String hdfsPath = hdfsService.upload(localFile, currentUser + "/" + hashCode + "-" + localFile.getName());
        logger.info(MessageFormat.format("HdfsPath:     {0}", hdfsPath));

        // Insert file record to hadoop.files
        int uploadRes = insert(new HadoopFile(hashCode, hdfsPath, fileSize, currentUser));

        // Update hadoopUser capacity information
        HadoopUser hadoopUser = hadoopUserService.query(currentUser);
        hadoopUser.setUsedCapacity(hadoopUser.getUsedCapacity() + fileSize);
        hadoopUserService.updateUsedCapacity(hadoopUser);

        // Delete file on Tomcat server
        localFile.delete();

        return uploadRes;
    }

    public int download(String hashCode) {
        HadoopFile file = query(hashCode);

        // 默认下载到用户的Downloads目录
        hdfsService.download(FileUtil.getRelativePath(file.getHdfsPath()), FileUtil.getUserDownloadsDir());

        return increaseDownloadCount(file.getHashCode());
    }

    public int getSharedFilesCount(String username) {
        return hadoopFileMapper.getSharedFilesCount(username);
    }

    public int getTotalDownloadCount(String username) {
        return hadoopFileMapper.getTotalDownloadCount(username);
    }

    public int increaseDownloadCount(String hashCode) {
        return hadoopFileMapper.increaseDownloadCount(hashCode);
    }

    public List<HadoopFile> getUserFiles(String username) {
        return hadoopFileMapper.getUserFiles(username);
    }

    public List<HadoopFile> getSharedFiles() {
        return hadoopFileMapper.getSharedFiles();
    }

    public List<HadoopFile> getSharedFilesOrderByDownloadCount() {
        return hadoopFileMapper.getSharedFilesOrderByDownloadCount();
    }

    public List<HadoopFile> getSharedFilesOrderByShareDate() {
        return hadoopFileMapper.getSharedFilesOrderByShareDate();
    }

    public List<HadoopFile> getSharedFilesByUsername(String username) {
        return hadoopFileMapper.getSharedFilesByUsername(username);
    }

    public List<HadoopFile> searchFiles(String query) {
        return hadoopFileMapper.searchFiles(query);
    }

    public List<HadoopFile> searchSharedFiles(String query) {
        return hadoopFileMapper.searchSharedFiles(query);
    }
}
