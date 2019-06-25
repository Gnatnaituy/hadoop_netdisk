package org.jetbrains.hadoop_netdisk.service;

import org.jetbrains.hadoop_netdisk.model.File;

import java.util.List;

/**
 * @auther hasaker
 * @create_date 2019-06-20 11:38
 * @description
 */
public interface FileService {
    File query(String fileName);

    int delete(String fileMD5HashCode);

    int deleteSelected(List<String> selected);

    int upload(File file);

    int share(String fileMD5HashCode);
}
