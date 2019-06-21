package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.model.File;
import org.jetbrains.hadoop_netdisk.mapper.FileMapper;
import org.jetbrains.hadoop_netdisk.service.FileService;
import org.springframework.stereotype.Service;

/**
 * @auther hasaker
 * @create_date 2019-06-20 11:37
 * @description
 */
@Service
public class FileServiceImpl implements FileService {
    private final FileMapper fileMapper;

    public FileServiceImpl(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    public File query(String fileName) {
        return fileMapper.query(fileName);
    }
}
