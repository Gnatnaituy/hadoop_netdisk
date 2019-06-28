package org.jetbrains.hadoop_netdisk.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.MessageFormat;
import java.util.Objects;

/**
 * @auther hasaker
 * @create_date 2019-06-26 20:04
 * @description
 */
public class FileUtil {

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
}
