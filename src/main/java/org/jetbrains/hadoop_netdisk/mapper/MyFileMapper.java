package org.jetbrains.hadoop_netdisk.mapper;

import org.jetbrains.hadoop_netdisk.model.MyFile;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @auther hasaker
 * @create_date 2019-06-19 18:26
 * @description
 */
@Repository
public interface MyFileMapper {
    MyFile getDetail(String fileName);

    List<MyFile> getSharedFiles();

    List<MyFile> getSharedFilesOrderByDownloadCount();

    List<MyFile> getSharedFilesOrderByShareDate();

    int getSharedFilesCount(String username);

    int getTotalDownloadCount(String username);

    List<MyFile> getSharedFilesByUsername(String username);

    List<MyFile> searchFiles(String query);

    List<MyFile> searchSharedFiles(String query);

    int insert(MyFile myFile);

    int rename(String fileMD5HashCode);

    int delete(String fileMD5HashCode);

    int share(String fileMD5HashCode);
}
