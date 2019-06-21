package org.jetbrains.hadoop_netdisk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * @auther hasaker
 * @create_date 2019-06-20 16:27
 * @description
 */
public class MD5Util {
    private static final char[] hexDigits = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    /**
     * 获取文件的MD5值
     * @param file 待获取MD5值得文件
     * @return 如果I/O错误 返回""
     */
    public static String getFileMD5(File file) {
        FileInputStream in = null;

        try {
            in = new FileInputStream(file);
            FileChannel channel = in.getChannel();
            return MD5(channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length()));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * MD5校验字符串
     * @param s String to be MD5
     * @return "" if can't get MessageDigest
     */
    public static String getStringMD5(String s) {
        MessageDigest messageDigest;

        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        byte[] byteInput = s.getBytes();
        messageDigest.update(byteInput);
        byte[] md = messageDigest.digest();

        // 把密文转化为16进制
        int length = md.length;
        char[] str = new char[length * 2];

        int index = 0;
        for (byte b : md) {
            str[index++] = hexDigits[b >>> 4 & 0xf];
            str[index++] = hexDigits[b & 0xf];
        }

        return new String(str);
    }

    @SuppressWarnings("unused")
    private static String getSubStr(String str, int subNum, char replace) {
        int length = str.length();
        if (length > subNum) {
            str = str.substring(length - subNum, length);
        } else if (length < subNum) {
            str += createPaddingString(subNum - length, replace);
        }

        return str;
    }

    private static String createPaddingString(int n, char pad) {
        if (n <= 0) {
            return "";
        }

        char[] paddingArray = new char[n];
        Arrays.fill(paddingArray, pad);
        return new String(paddingArray);
    }

    /**
     * 计算MD5值
     * @param buffer ByteBuffer
     * @return 如果失败, 返回""
     */
    private static String MD5(ByteBuffer buffer) {
        String res = "";

        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(buffer);
            byte[] tmp = messageDigest.digest();
            // MD5的计算结果是一个128位的长整数, 用字节表示就是16个字节
            // 每个字节用16进制表示的话, 需要使用两个字符, 所以表示成16进制需要32个字符
            char[] str = new char[16 * 2];

            // 转换结果中对应字符的位置
            int index = 0;
            // 从第一个字节开始, 对应MD5的每一个字节
            for (int i = 0; i < 16; i++) {
                // 转换成16进制字符
                // 取第i个字节
                byte byte0 = tmp[i];
                // 取字节中高4位的数字转换
                str[index++] = hexDigits[byte0 >>> 4 & 0xf];
                // 取字节中低4位的数字转换
                str[index++] = hexDigits[byte0 & 0xf];
            }
            res = new String(str);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return res;
    }
}
