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

        return request.getSession().getAttribute("currentPath").toString();
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

    public int fakeDelete(String hashCode, String username) {
        HadoopFile file = query(hashCode);
        HadoopUser user = hadoopUserService.query(username);

        hdfsService.fakeDelete(FileUtil.getRelativePath(file.getHdfsPath()));
        String newHdfsPath = file.getHdfsPath() + "-deleted";

        user.setUsedCapacity(user.getUsedCapacity() - file.getFileSize());
        hadoopUserService.updateUsedCapacity(user);

        return hadoopFileMapper.fakeDelete(newHdfsPath, hashCode);
    }

    public int fakeDeleteDir(String hdfsPath, String username) {
        List<HadoopFile> files = hadoopFileMapper.getFilesUnderDir(hdfsPath);
        HadoopUser user = hadoopUserService.query(username);

        for (HadoopFile file : files) {
            if (!file.getHdfsPath().endsWith("-deleted")) {
                hadoopFileMapper.fakeDelete(file.getHdfsPath() + "-deleted", file.getHashCode());
                user.setUsedCapacity(user.getUsedCapacity() - file.getFileSize());
            }
        }

        hadoopUserService.updateUsedCapacity(user);
        hdfsService.fakeDelete(FileUtil.getRelativePath(hdfsPath));

        return 0;
    }

    public int cancelFakeDelete(String hashCode, String username) {
        HadoopFile file = query(hashCode);
        HadoopUser user = hadoopUserService.query(username);

        hdfsService.cancelFakeDelete(FileUtil.getRelativePath(file.getHdfsPath()));
        String newHdfsPath = file.getHdfsPath().substring(0, file.getHdfsPath().length() - 8);

        user.setUsedCapacity(user.getUsedCapacity() + file.getFileSize());
        hadoopUserService.updateUsedCapacity(user);

        return hadoopFileMapper.cancelFakeDelete(newHdfsPath, hashCode);
    }

    public int realDelete(String hashCode) {
        String relativePath = FileUtil.getRelativePath(query(hashCode).getHdfsPath());
        hdfsService.realDelete(relativePath);

        return hadoopFileMapper.realDelete(hashCode);
    }

    public int insert(HadoopFile hadoopFile) {
        return hadoopFileMapper.insert(hadoopFile);
    }

    public int share(String shareExpireDay, String shareEncryptCode, String hashCode) {
        boolean shareEncrypt = !"".equals(shareEncryptCode);

        if (shareEncrypt) {
            return hadoopFileMapper.shareWithEncrypt(Integer.valueOf(shareExpireDay), shareEncryptCode, hashCode);
        } else {
            return hadoopFileMapper.share(Integer.valueOf(shareExpireDay), hashCode);
        }
    }

    public int upload(String currentPath, MultipartFile multipartFile) {
        String currentUser = currentPath.split("/")[0];
        // Convert multipartFile to File
        File localFile = FileUtil.multipartFileToFile(multipartFile);
        logger.info(MessageFormat.format("File path on Tomcat server:   {0}", localFile.getPath()));

        String hashCode = MD5Util.getFileMD5(localFile);
        // Convert bit to MB, 1MB = 1024 * 1024 = 1048576bit
        double fileSize = localFile.length() / 1048576.0;
        logger.info(MessageFormat.format("HashCode:     {0}", hashCode));
        logger.info(MessageFormat.format("FileSize:     {0} MB", fileSize));

        // Upload localFile to Hadoop and convert file name to hashcode-filename
        String hdfsPath = hdfsService.upload(localFile, currentPath + "/" + hashCode + "-" + localFile.getName());
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

    public List<HadoopFile> getUserDeletedFiles(String username) {
        return hadoopFileMapper.getUserDeletedFiles(username);
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
