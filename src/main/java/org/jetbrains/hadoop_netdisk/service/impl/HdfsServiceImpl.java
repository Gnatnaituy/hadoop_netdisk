package org.jetbrains.hadoop_netdisk.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.jetbrains.hadoop_netdisk.service.HdfsService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @auther hasaker
 * @create_date 2019-06-24 08:36
 * @description
 */
@Service
public class HdfsServiceImpl implements HdfsService {
    private Configuration conf;
    private String defaultHdfsUri;

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
                return fileSystem.mkdirs(new Path(hdfsPath));
            } catch (IOException e) {
                return false;
            } finally {
                close(fileSystem);
            }
        }
    }

    /**
     * 上传文件
     */
    public void upload(String srcFile, String desPath) {
        this.upload(false, true, srcFile, desPath);
    }

    private void upload(boolean deleteSrc, boolean overwrite, String srcFile, String desPath) {
        Path localSrcPath = new Path(srcFile);
        Path hdfsDesPath = new Path(generateHdfsPath(desPath));
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            fileSystem.copyFromLocalFile(deleteSrc, overwrite, localSrcPath, hdfsDesPath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fileSystem);
        }
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
                        fileMap.put("path", status.getPath().toString());
                        fileMap.put("isDir", status.isDirectory());

                        res.add(fileMap);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close(fileSystem);
            }
        }

        return res;
    }

    /**
     * 下载
     */
    public void download(String srcFile, String desFile) {
        Path hdfsSrcPath = new Path(generateHdfsPath(srcFile));
        Path localDesPath = new Path(desFile);
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            fileSystem.copyFromLocalFile(hdfsSrcPath, localDesPath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fileSystem);
        }
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
    public <T extends Object> T openWithObject(String path, Class<T> clazz) {
        String jsonStr = this.openWithString(path);

        return JSON.parseObject(jsonStr, clazz);
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
            e.printStackTrace();
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
            e.printStackTrace();
        } finally {
            close(fileSystem);
        }

        return false;
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
            e.printStackTrace();
        } finally {
            close(fileSystem);
        }

        return null;
    }

    /**
     * 判断某个文件是否存在
     * @param path
     * @return
     */
    private boolean checkExists(String path) {
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            String hdfsPath = generateHdfsPath(path);
            return fileSystem.exists(new Path(hdfsPath));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close(fileSystem);
        }

        return false;
    }

    /**
     * 将相对路径转化为HDFS绝对路径
     * @param desPath
     * @return
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
     * @param fileSystem
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
