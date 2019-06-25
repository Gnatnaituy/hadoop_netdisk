package org.jetbrains.hadoop_netdisk.service;

import org.jetbrains.hadoop_netdisk.model.MyFile;

import java.util.List;


/**
 * @auther hasaker
 * @create_date 2019-06-20 11:38
 * @description
 */
public interface MyFileService {
    MyFile getDetail(String fileName);

    int insert(MyFile myFile);

    int delete(String fileMD5HashCode);

    int rename(String fileMD5HashCode);

    int share(String fileMD5HashCode);

    List<String> getSharedFiles();
}
