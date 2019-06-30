package org.jetbrains.hadoop_netdisk.service;

import org.apache.hadoop.fs.BlockLocation;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.PathFilter;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * @auther hasaker
 * @create_date 2019-06-24 10:27
 * @description
 */

public interface HdfsService {
    boolean mkdir(String path);

    String upload(File srcFile, String desFile);

    void download(String srcFile, String desFile);

    boolean rename(String srcFile, String desFile);

    boolean fakeDelete(String path);

    boolean realDelete(String path);

    List<Map<String, Object>> listFiles(String path, PathFilter pathFilter);

    FSDataInputStream open(String path);

    byte[] openWithBytes(String path);

    String openWithString(String path);

    <T> T openWithObject(String path, Class<T> clazz);

    BlockLocation[] getFileBlockLocations(String path);
}