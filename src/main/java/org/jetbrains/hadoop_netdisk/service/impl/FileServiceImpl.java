package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.model.File;
import org.jetbrains.hadoop_netdisk.mapper.FileMapper;
import org.jetbrains.hadoop_netdisk.service.FileService;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public int delete(String fileMD5HashCode) {
        return fileMapper.delete(fileMD5HashCode);
    }

    public int deleteSelected(List<String> selected) {
        return fileMapper.deleteSelected(selected);
    }

    public int upload(File file) {
        return fileMapper.upload(file);
    }

    public int share(String fileMD5HasCode) {
        return fileMapper.share(fileMD5HasCode);
    }
}
