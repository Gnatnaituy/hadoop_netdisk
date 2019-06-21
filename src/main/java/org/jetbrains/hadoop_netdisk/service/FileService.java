package org.jetbrains.hadoop_netdisk.service;

import org.jetbrains.hadoop_netdisk.model.File;

/**
 * @auther hasaker
 * @create_date 2019-06-20 11:38
 * @description
 */
public interface FileService {
    File query(String fileName);
}
