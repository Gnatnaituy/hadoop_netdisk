package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.service.HdfsService;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther hasaker
 * @create_date 2019-06-24 08:36
 * @description
 */
public class HdfsServiceImpl implements HdfsService {
    private Configuration conf;
    private String defaultHdfsUri;

    private Logger logger = LoggerFactory.getLogger(HdfsService.class);

    public HdfsServiceImpl(Configuration conf, String defaultHdfsUri) {
        this.conf = conf;
        this.defaultHdfsUri = defaultHdfsUri;
    }

    private FileSystem getFileSystem() throws IOException {
        return FileSystem.get(conf);
    }

    /**
     * 创建目录
     */
    public boolean mkdir(String path) {
        if (checkExists(path)) {
            return true;
        } else {
            FileSystem fileSystem = null;

            try {
                fileSystem = getFileSystem();
                String hdfsPath = generateHdfsPath(path);
                logger.info(MessageFormat.format("hdfsPath --> {0}", hdfsPath));

                return fileSystem.mkdirs(new Path(hdfsPath));
            } catch (IOException e) {
                logger.error(MessageFormat.format("创建HDFS目录失败, path:{0}", path), e);

                return false;
            } finally {
                close(fileSystem);
            }
        }
    }

    /**
     * 上传
     * @return 上传后文件在hadoop上的路径
     */
    public String upload(File srcFile, String desPath) {
        Path localSrcPath = new Path(srcFile.getPath());
        Path hdfsDesPath = new Path(generateHdfsPath(desPath));
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            fileSystem.copyFromLocalFile(localSrcPath, hdfsDesPath);

            return hdfsDesPath.toString();
        } catch (IOException e) {
            logger.error(MessageFormat.format("上传文件到HDFS失败, srcFile:{0}, desPath:{1}", srcFile, desPath), e);

            return "上传失败!";
        } finally {
            close(fileSystem);
        }
    }

    /**
     * 下载
     */
    public void download(String srcFile, String desFile) {
        Path hdfsSrcPath = new Path(generateHdfsPath(srcFile));
        Path localDesPath  = new Path(desFile);
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            fileSystem.copyToLocalFile(hdfsSrcPath, localDesPath);
        } catch (IOException e) {
            logger.error(MessageFormat.format("从HDFS下载文件至本地失败，srcFile:{0}, desFile:{1}", srcFile, desFile), e);
        } finally {
            close(fileSystem);
        }
    }

    /**
     * 重命名
     */
    public boolean rename(String srcFile, String desFile) {
        Path srcFilePath = new Path(generateHdfsPath(srcFile));
        Path desFilePath = new Path(desFile);
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();

            return fileSystem.rename(srcFilePath, desFilePath);
        } catch (IOException e) {
            logger.error(MessageFormat.format("重命名失败，srcFile:{0}, desFile:{1}", srcFile, desFile), e);
        } finally {
            close(fileSystem);
        }

        return false;
    }

    /**
     * 删除HDFS文件或目录
     */
    public boolean delete(String path) {
        Path hdfsPath = new Path(generateHdfsPath(path));
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();

            return fileSystem.delete(hdfsPath);
        } catch (IOException e) {
            logger.error(MessageFormat.format("删除HDFS文件或目录失败，path:{0}", path), e);
        } finally {
            close(fileSystem);
        }

        return false;
    }

    /**
     * 获取文件列表
     */
    public List<Map<String, Object>> listFiles(String path, PathFilter pathFilter) {
        List<Map<String, Object>> res = new ArrayList<>();

        if (checkExists(path)) {
            FileSystem fileSystem = null;

            try {
                fileSystem = getFileSystem();
                String hdfsPath = generateHdfsPath(path);
                FileStatus[] statuses;

                if (pathFilter != null) {
                    statuses = fileSystem.listStatus(new Path(hdfsPath), pathFilter);
                } else {
                    statuses = fileSystem.listStatus(new Path(hdfsPath));
                }

                if (statuses != null) {
                    for (FileStatus status : statuses) {
                        Map<String, Object> fileMap = new HashMap<>(2);
                        fileMap.put("fullName", status.getPath().toString());
                        fileMap.put("fileName", status.getPath().getName());
                        fileMap.put("fileSize", status.getLen());
                        fileMap.put("isDir", status.isDirectory());

                        res.add(fileMap);
                    }
                }
            } catch (IOException e) {
                logger.error(MessageFormat.format("获取HDFS上面的某个路径下面的所有文件失败，path:{0}", path), e);
            } finally {
                close(fileSystem);
            }
        }

        return res;
    }

    /**
     * 打开HDFS文件并返回一个InputStream
     */
    public FSDataInputStream open(String path) {
        Path hdfsPath = new Path(generateHdfsPath(path));
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();

            return fileSystem.open(hdfsPath);
        } catch (IOException e) {
            logger.error(MessageFormat.format("打开HDFS上面的文件失败，path:{0}", path), e);
        } finally {
            close(fileSystem);
        }

        return null;
    }

    /**
     * 打开HDFS文件并返回byte数组, 用于web端下载文件
     */
    public byte[] openWithBytes(String path) {
        Path hdfsPath = new Path(generateHdfsPath(path));
        FileSystem fileSystem = null;
        FSDataInputStream inputStream = null;

        try {
            fileSystem = getFileSystem();
            inputStream = fileSystem.open(hdfsPath);

            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            logger.error(MessageFormat.format("打开HDFS上面的文件失败，path:{0}",path),e);
        } finally {
            close(fileSystem);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 打开HDFS文件并返回String字符串
     */
    public String openWithString(String path) {
        Path hdfsPath = new Path(generateHdfsPath(path));
        FileSystem fileSystem = null;
        FSDataInputStream inputStream = null;

        try {
            fileSystem = getFileSystem();
            inputStream = fileSystem.open(hdfsPath);

            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            logger.error(MessageFormat.format("打开HDFS上面的文件失败，path:{0}",path),e);
        } finally {
            close(fileSystem);
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 打开HDFS文件并返回Java对象
     */
    public <T> T openWithObject(String path, Class<T> clazz) {
        String jsonStr = this.openWithString(path);

        return JSON.parseObject(jsonStr, clazz);
    }

    /**
     * 获取文件在Hadoop集群上的位置
     */
    public BlockLocation[] getFileBlockLocations(String path) {
        Path hdfsPath = new Path(generateHdfsPath(path));
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            FileStatus fileStatus = fileSystem.getFileStatus(hdfsPath);

            return fileSystem.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        } catch (IOException e) {
            logger.error(MessageFormat.format("获取某个文件在HDFS集群的位置失败，path:{0}", path), e);
        } finally {
            close(fileSystem);
        }

        return null;
    }

    /**
     * 判断某个文件是否存在
     */
    private boolean checkExists(String path) {
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            String hdfsPath = generateHdfsPath(path);

            return fileSystem.exists(new Path(hdfsPath));
        } catch (IOException e) {
            logger.error(MessageFormat.format("判断文件是否存在与HDFS失败, path:{0}", path), e);
        } finally {
            close(fileSystem);
        }

        return false;
    }

    /**
     * 将相对路径转化为HDFS绝对路径
     */
    private String generateHdfsPath(String desPath) {
        String hdfsPath = defaultHdfsUri;

        if (desPath.startsWith("/")) {
            hdfsPath += desPath;
        } else {
            hdfsPath = hdfsPath + "/" + desPath;
        }

        return hdfsPath;
    }

    /**
     * 关闭FileSystem
     */
    private void close(FileSystem fileSystem) {
        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
