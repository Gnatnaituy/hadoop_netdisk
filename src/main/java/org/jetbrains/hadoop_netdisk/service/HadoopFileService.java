package org.jetbrains.hadoop_netdisk.service;

import org.jetbrains.hadoop_netdisk.entity.HadoopFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


/**
 * @auther hasaker
 * @create_date 2019-06-20 11:38
 * @description
 */
public interface HadoopFileService {
    String getCurrentDir(HttpServletRequest request);

    HadoopFile query(String hashCode);

    boolean mkdir(String path);

    int insert(HadoopFile hadoopFile);

    int fakeDelete(String hashCode, String username);

    int fakeDeleteDir(String currentPath, String username);

    int cancelFakeDelete(String hashCode, String username);

    int realDelete(String hashCode);

    int rename(String oldHdfsPath, String newFileName, boolean isDir, String hashCode);

    int share(String shareExpireDay, String shareEncryptCode, String hashCode);

    int upload(String currentPath, MultipartFile multipartFile);

    int download(String hashCode);

    int getSharedFilesCount(String username);

    int getTotalDownloadCount(String username);

    int increaseDownloadCount(String hashCode);

    List<HadoopFile> getUserFiles(String username);

    List<HadoopFile> getUserDeletedFiles(String username);

    List<HadoopFile> getSharedFiles();

    List<HadoopFile> getSharedFilesByUsername(String username);

    List<HadoopFile> getSharedFilesOrderByDownloadCount();

    List<HadoopFile> getSharedFilesOrderByShareDate();

    List<HadoopFile> searchFiles(String query);

    List<HadoopFile> searchSharedFiles(String query);
}
