package org.jetbrains.hadoop_netdisk.service;

import org.jetbrains.hadoop_netdisk.model.MyFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;


/**
 * @auther hasaker
 * @create_date 2019-06-20 11:38
 * @description
 */
public interface MyFileService {
    MyFile getDetailByFileMD5HashCode(String fileMD5HashCode);

    MyFile getDetailByFullName(String fullName);

    List<MyFile> getSharedFiles();

    List<MyFile> getSharedFilesOrderByDownloadCount();

    List<MyFile> getSharedFilesOrderByShareDate();

    int getSharedFilesCount(String username);

    int getTotalDownloadCount(String username);

    int increaseDownloadCount(String fileMD5HashCode);

    List<MyFile> getSharedFilesByUsername(String username);

    List<MyFile> searchFiles(String query);

    List<MyFile> searchSharedFiles(String query);

    int insert(MyFile myFile);

    int delete(String fileMD5HashCode);

    int rename(String fileMD5HashCode);

    int share(String fileMD5HashCode);

    /**
     * Upload local file to Hadoop
     */
    int upload(String currentUser, MultipartFile multipartFile);

    /**
     * Download file from Hadoop to Local
     */
    int download(String currentUser, String fullName);
}
