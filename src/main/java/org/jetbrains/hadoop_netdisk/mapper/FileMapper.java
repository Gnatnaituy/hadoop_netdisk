package org.jetbrains.hadoop_netdisk.mapper;

import org.jetbrains.hadoop_netdisk.entity.File;
import org.springframework.stereotype.Repository;

/**
 * @auther hasaker
 * @create_date 2019-06-19 18:26
 * @description
 */
@Repository
public interface FileMapper {
    File query(String fileName);
}
