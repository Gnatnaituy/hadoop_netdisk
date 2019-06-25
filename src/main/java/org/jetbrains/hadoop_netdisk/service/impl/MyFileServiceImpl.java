package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.model.MyFile;
import org.jetbrains.hadoop_netdisk.mapper.MyFileMapper;
import org.jetbrains.hadoop_netdisk.service.MyFileService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @auther hasaker
 * @create_date 2019-06-20 11:37
 * @description
 */
@Service
public class MyFileServiceImpl implements MyFileService {
    private final MyFileMapper myFileMapper;

    public MyFileServiceImpl(MyFileMapper myFileMapper) {
        this.myFileMapper = myFileMapper;
    }

    public MyFile getDetail(String fileName) {
        return myFileMapper.getDetail(fileName);
    }

    public int rename(String fileMD5HashCode) {
        return myFileMapper.rename(fileMD5HashCode);
    }

    public int delete(String fileMD5HashCode) {
        return myFileMapper.delete(fileMD5HashCode);
    }

    public int insert(MyFile myFile) {
        return myFileMapper.insert(myFile);
    }

    public int share(String fileMD5HasCode) {
        return myFileMapper.share(fileMD5HasCode);
    }

    public List<String> getSharedFiles() {
        return myFileMapper.getSharedFiles();
    }
}
