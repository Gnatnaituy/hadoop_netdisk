package org.jetbrains.hadoop_netdisk.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @auther hasaker
 * @create_date 2019-06-26 20:04
 * @description
 */
public class FileUtil {
    private static final int HDFS_URL_LENGTH = "hdfs://localhost:9000".length();
    private static final int HASHCODE_LENGTH = 32;

    public static String getUserDownloadsDir() {
        String userHomeFolder = System.getProperty("user.home");

        return userHomeFolder + "/Downloads";
    }

    private static void inputStreamToFile(InputStream inputStream, File file) {
        try {
            OutputStream outputStream = new FileOutputStream(file);
            int bytesRead;
            byte[] buffer = new byte[8192];

            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static File multipartFileToFile(MultipartFile multipartFile) {
        File file = null;

        try {
            if (multipartFile != null && multipartFile.getSize() > 0) {
                InputStream inputStream = multipartFile.getInputStream();
                file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                inputStreamToFile(inputStream, file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public static String getRelativePath(String hdfsPath) {

        return hdfsPath.substring(HDFS_URL_LENGTH + 1);
    }

    public static String getHashCode(String hdfsPath) {
        int lastSlashIndex = hdfsPath.lastIndexOf('/');

        return hdfsPath.substring(lastSlashIndex + 1, lastSlashIndex + HASHCODE_LENGTH + 1);
    }

    public static String getFileName(String hdfsPath, boolean isDir) {
        if (isDir) {
            return hdfsPath.substring(hdfsPath.substring(0, hdfsPath.length() - 1).lastIndexOf('/') + 1);
        } else {
            return hdfsPath.substring(hdfsPath.lastIndexOf('/') + HASHCODE_LENGTH + 2);
        }
    }

    public static String exceptFileName(String hdfsPath, boolean isDir) {
        if (isDir) {
            return hdfsPath.substring(0, hdfsPath.lastIndexOf("/") + 1);
        } else {
            return hdfsPath.substring(0, hdfsPath.lastIndexOf('/') + HASHCODE_LENGTH + 2);
        }
    }

    public static void sortFileListByName(List<Map<String, Object>> fileList) {
        fileList.sort((o1, o2) -> {
            if (o1.get("isDir") == o2.get("isDir")) {
                return o1.get("fileName").toString().toLowerCase().compareTo(o2.get("fileName").toString().toLowerCase());
            } else {
                return (boolean) o1.get("isDir") ? -1 : 1;
            }
        });
    }

    public static void sortFileListByLastModifiedDate(List<Map<String, Object>> fileList) {
        fileList.sort((o1, o2) -> {
            if (o1.get("isDir") == o2.get("isDir")) {
                return o2.get("lastModifiedDate").toString().compareTo(o1.get("lastModifiedDate").toString());
            } else {
                return (boolean) o1.get("isDir") ? -1 : 1;
            }
        });
    }

    public static void sortFileListByFileSize(List<Map<String, Object>> fileList) {
        fileList.sort((o1, o2) -> {
            if (!(((boolean) o1.get("isDir") && ((boolean) o2.get("isDir"))))) {
                return Long.compare((long) o1.get("fileSize"), (long) o2.get("fileSize"));
            } else {
                return (boolean) o1.get("isDir") ? -1 : 1;
            }
        });
    }
}
