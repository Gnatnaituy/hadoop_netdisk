package org.jetbrains.hadoop_netdisk.service.impl;

import org.jetbrains.hadoop_netdisk.service.HdfsService;

import com.alibaba.fastjson.JSON;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.jetbrains.hadoop_netdisk.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
     * Create directory on Hadoop
     */
    public boolean mkdir(String path) {
        if (checkExists(path)) {
            return true;
        } else {
            FileSystem fileSystem = null;

            try {
                fileSystem = getFileSystem();
                String hdfsPath = generateHdfsPath(path);
                logger.info(MessageFormat.format("Create directory on Hadoop: {0}", hdfsPath));

                return fileSystem.mkdirs(new Path(hdfsPath));
            } catch (IOException e) {
                logger.error(MessageFormat.format("Create directory on Hadoop failed, path:{0}", path), e);

                return false;
            } finally {
                close(fileSystem);
            }
        }
    }

    /**
     * Upload local file to Hadoop
     * @return hdfsPath file absolute path on Hadoop if success else ""
     */
    public String upload(File srcFile, String desPath) {
        Path localPath = new Path(srcFile.getPath());
        Path hdfsPath = new Path(generateHdfsPath(desPath));
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            fileSystem.copyFromLocalFile(localPath, hdfsPath);

            return hdfsPath.toString();
        } catch (IOException e) {
            logger.error(MessageFormat.format("Upload local file to Hadoop failed, srcFile:{0}, desPath:{1}", srcFile,
                    desPath), e);

            return "";
        } finally {
            close(fileSystem);
        }
    }

    /**
     * Download file from Hadoop
     */
    public void download(String srcFile, String desFile) {
        Path hdfsPath = new Path(generateHdfsPath(srcFile));
        Path localPath  = new Path(desFile);
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            fileSystem.copyToLocalFile(hdfsPath, localPath);
        } catch (IOException e) {
            logger.error(MessageFormat.format("Download form HDFS failed，srcFile:{0}, desFile:{1}", srcFile, desFile),
                    e);
        } finally {
            close(fileSystem);
        }
    }

    /**
     * Rename target on Hadoop
     */
    public boolean rename(String srcPath, String desPath) {
        Path srcFilePath = new Path(generateHdfsPath(srcPath));
        Path desFilePath = new Path(generateHdfsPath(desPath));
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();

            return fileSystem.rename(srcFilePath, desFilePath);
        } catch (IOException e) {
            logger.error(MessageFormat.format("Rename target failed，srcPath:{0}, desPath:{1}", srcPath, desPath), e);
        } finally {
            close(fileSystem);
        }

        return false;
    }

    /**
     * Delete target on Hadoop
     */
    public boolean realDelete(String path) {
        Path hdfsPath = new Path(generateHdfsPath(path));
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();

            return fileSystem.delete(hdfsPath, true);
        } catch (IOException e) {
            logger.error(MessageFormat.format("Delete target on Hadoop failed，path:{0}", path), e);
        } finally {
            close(fileSystem);
        }

        return false;
    }

    /**
     * Fake delete target on Hadoop
     */
    public boolean fakeDelete(String path) {
        String deletedPath = path + "-deleted";

        return rename(path, deletedPath);
    }

    /**
     * Get file and directory list of target path on Hadoop
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
                        Map<String, Object> file = new HashMap<>();
                        String lastModifiedDate =
                                new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(status.getModificationTime()));

                        file.put("hdfsPath", status.getPath());
                        logger.info(MessageFormat.format("hdfsPath: {0}", status.getPath()));
                        file.put("lastModifiedDate", lastModifiedDate);
                        file.put("isDir", status.isDirectory());

                        if (status.isDirectory()) {
                            file.put("fileName", status.getPath().getName());
                        } else {
                            file.put("hashCode", status.getPath().getName().substring(0, 32));
                            file.put("fileName", status.getPath().getName().substring(33));
                            file.put("fileSize", status.getLen());
                        }

                        res.add(file);
                    }
                }
            } catch (IOException e) {
                logger.error(MessageFormat.format("Get file and directory list of target path on Hadoop " +
                        "failed，path:{0}", path), e);
            } finally {
                close(fileSystem);
            }
        }

        // Sort fileList Alphabetically
        // Directory always at the previous of file
        FileUtil.sortFileListByLastModifiedDate(res);

        return res;
    }

    /**
     * Open HDFS file and return InputStream
     */
    public FSDataInputStream open(String path) {
        Path hdfsPath = new Path(generateHdfsPath(path));
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();

            return fileSystem.open(hdfsPath);
        } catch (IOException e) {
            logger.error(MessageFormat.format("Open HDFS file failed，path:{0}", path), e);
        } finally {
            close(fileSystem);
        }

        return null;
    }

    /**
     * Open HDFS file and return byte array
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
            logger.error(MessageFormat.format("Open HDFS file failed，path:{0}",path),e);
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
     * Open HDFS file and return String
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
            logger.error(MessageFormat.format("Open HDFS file failed，path:{0}",path),e);
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
     * Open HDFS file and return JSON object
     */
    public <T> T openWithObject(String path, Class<T> clazz) {
        String jsonStr = this.openWithString(path);

        return JSON.parseObject(jsonStr, clazz);
    }

    /**
     * Get file block location information on Hadoop cluster
     */
    public BlockLocation[] getFileBlockLocations(String path) {
        Path hdfsPath = new Path(generateHdfsPath(path));
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            FileStatus fileStatus = fileSystem.getFileStatus(hdfsPath);

            return fileSystem.getFileBlockLocations(fileStatus, 0, fileStatus.getLen());
        } catch (IOException e) {
            logger.error(MessageFormat.format("Get file block locations failed，path:{0}", path), e);
        } finally {
            close(fileSystem);
        }

        return null;
    }

    /**
     * Judge target exists or not on Hadoop
     */
    private boolean checkExists(String path) {
        FileSystem fileSystem = null;

        try {
            fileSystem = getFileSystem();
            String hdfsPath = generateHdfsPath(path);

            return fileSystem.exists(new Path(hdfsPath));
        } catch (IOException e) {
            logger.error(MessageFormat.format("Check exists failed, path:{0}", path), e);
        } finally {
            close(fileSystem);
        }

        return false;
    }

    /**
     * Convert relative path to absolute HDFS path
     */
    private String generateHdfsPath(String path) {
        String hdfsPath = defaultHdfsUri;

        if (path.startsWith("/")) {
            hdfsPath += path;
        } else {
            hdfsPath = hdfsPath + "/" + path;
        }

        return hdfsPath;
    }

    /**
     * Close FileSystem
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
