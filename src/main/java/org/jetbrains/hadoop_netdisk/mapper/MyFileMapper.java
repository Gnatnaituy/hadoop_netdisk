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

    int insert(MyFile myFile);

    int rename(String fileMD5HashCode);

    int delete(String fileMD5HashCode);

    int share(String fileMD5HashCode);

    List<String> getSharedFiles();
}
