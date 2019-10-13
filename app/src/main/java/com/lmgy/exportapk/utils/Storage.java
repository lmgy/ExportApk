package com.lmgy.exportapk.utils;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class Storage {

    /**
     * 获取指定path的可写入存储容量，单位字节
     */
    public static long getAvaliableSizeOfPath(@NonNull String path) {
        try {
            StatFs stat = new StatFs(path);
            int version = Build.VERSION.SDK_INT;
            long blockSize = version >= 18 ? stat.getBlockSizeLong() : stat.getBlockSize();
            long availableBlocks = version >= 18 ? stat.getAvailableBlocksLong() : stat.getAvailableBlocks();
            return blockSize * availableBlocks;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取外部存储主路径
     */
    @NonNull
    public static String getMainExternalStoragePath() {
        try {
            return Environment.getExternalStorageDirectory().getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 获取设备挂载的所有外部存储分区path
     */
    @NonNull
    public static List<String> getAvailableStoragePaths() {
        try {
            List<String> paths = new ArrayList<>();
            String mainStorage = getMainExternalStoragePath().toLowerCase(Locale.getDefault()).trim();
            paths.add(mainStorage);
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("fat") || line.contains("fuse") || line.contains("ntfs") || line.contains("sdcardfs") || line.contains("fuseblk")) {
                    String[] items = line.split(" ");
                    for (String s : items) {
                        s = s.trim().toLowerCase();
                        boolean condition = (s.contains(File.separator) || s.contains("/")) && !paths.contains(s);
                        if (condition) {
                            paths.add(s);
                        }
                    }
                }
            }
            return paths;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
