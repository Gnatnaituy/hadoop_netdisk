package org.jetbrains.hadoop_netdisk.mapper;

import org.jetbrains.hadoop_netdisk.model.File;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @auther hasaker
 * @create_date 2019-06-19 18:26
 * @description
 */
@Repository
public interface FileMapper {
    File query(String fileName);
    int delete(String fileMD5HashCode);
    int deleteSelected(List<String> selected);
    int upload(File file);
    int share(String fileMD5HashCode);
}
