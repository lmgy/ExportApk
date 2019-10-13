package com.lmgy.exportapk.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class FileUtils {

    public static long getFileOrFolderSize(File file) {
        try {
            if (file == null || !file.exists()) {
                return 0;
            }
            if (!file.isDirectory()) {
                return file.length();
            } else {
                long total = 0;
                File[] files = file.listFiles();
                if (files == null || files.length == 0) {
                    return 0;
                }
                for (File f : files) {
                    total += getFileOrFolderSize(f);
                }
                return total;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static CRC32 getCRC32FromFile(File file) throws IOException {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file.getAbsoluteFile()));
        CRC32 crc = new CRC32();
        byte[] bytes = new byte[1024];
        int cnt;
        while ((cnt = inputStream.read(bytes)) != -1) {
            crc.update(bytes, 0, cnt);
        }
        inputStream.close();
        return crc;
    }

}
