package org.jetbrains.hadoop_netdisk.mapper;

import org.jetbrains.hadoop_netdisk.entity.HadoopFile;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @auther hasaker
 * @create_date 2019-06-19 18:26
 * @description
 */
@Repository
public interface HadoopFileMapper {
    HadoopFile query(String hashCode);

    int insert(HadoopFile hadoopFile);

    int rename(String newHdfsPath, String hashCode);

    int share(int shareExpireDay, String hashCode);

    int shareWithEncrypt(int shareExpireDay, String shareEncryptCode, String hashCode);

    int fakeDelete(String newHdfsPath, String hashCode);

    int cancelFakeDelete(String newHdfsPath, String hashCode);

    int realDelete(String hashCode);

    List<HadoopFile> getFilesUnderDir(String currentPath);

    List<HadoopFile> getUserFiles(String username);

    List<HadoopFile> getUserDeletedFiles(String username);

    List<HadoopFile> getSharedFiles();

    List<HadoopFile> getSharedFilesOrderByDownloadCount();

    List<HadoopFile> getSharedFilesOrderByShareDate();

    List<HadoopFile> getSharedFilesByUsername(String username);

    List<HadoopFile> searchFiles(String query);

    List<HadoopFile> searchSharedFiles(String query);

    int getSharedFilesCount(String username);

    int getTotalDownloadCount(String username);

    int increaseDownloadCount(String hashCode);
}
