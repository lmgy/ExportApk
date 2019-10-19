package com.lmgy.exportapk.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * @author lmgy
 * @date 2019/10/17
 */
public class StorageUtils {

    public static String getMainStoragePath() {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return Environment.getExternalStorageDirectory().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    public static List<String> getAvailableStoragePaths() {
        try {
            List<String> paths = new ArrayList<>();
            String mainStorage = getMainStoragePath().toLowerCase(Locale.getDefault()).trim();
            try {
                paths.add(mainStorage);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("proc") || line.contains("tmpfs") || line.contains("media") || line.contains("asec") || line.contains("secure") || line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("misc") || line.contains("obb")) {
                    continue;
                }
                if (line.contains("fat") || line.contains("fuse") || (line.contains("ntfs"))) {
                    String[] items = line.split(" ");
                    if (items.length > 1) {
                        String path = items[1].toLowerCase(Locale.getDefault());
                        if (!path.toLowerCase(Locale.getDefault()).trim().equals(mainStorage))
                        {
                            paths.add(path);
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
