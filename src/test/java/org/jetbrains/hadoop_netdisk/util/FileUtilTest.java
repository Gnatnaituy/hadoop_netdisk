package org.jetbrains.hadoop_netdisk.util;

/**
 * @auther hasaker
 * @create_date 2019-07-04 20:35
 * @description
 */
public class FileUtilTest {

    public static void main(String[] args) {
        String hdfsPath = "hdfs://localhost:9000/Eminem/音乐/3bc840e20a08f9c2899a49420ea2ca2f-Adele - Hello.mp3";
        String hdfsPathDir = "hdfs://localhost:9000/Eminem/音乐";

        System.out.println("用户下载目录:" + FileUtil.getUserDownloadsDir());
        System.out.println("相对路径:" + FileUtil.getRelativePath(hdfsPath));
        System.out.println("HashCode:" + FileUtil.getHashCode(hdfsPath));
        System.out.println("文件名:"  + FileUtil.getFileName(hdfsPath, false));
        System.out.println("文件名:"  + FileUtil.getFileName(hdfsPathDir, true));
        System.out.println("获得当前目录: " + FileUtil.exceptFileName(hdfsPath, false));
        System.out.println("获得当前目录: " + FileUtil.exceptFileName(hdfsPathDir, true));
    }
}
