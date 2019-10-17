package com.lmgy.exportapk.utils;

import android.os.Environment;

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


}
