package com.lmgy.exportapk.utils;

import android.content.Context;

import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.config.Constant;

import java.io.File;
import java.util.List;
import java.util.Locale;

/**
 * @author lmgy
 * @date 2019/10/16
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


    public static long getFileSize(String filepath) {
        return filepath != null ? getFileOrFolderSize(new File(filepath)) : 0;
    }

    public static long getFilesSize(String[] filepaths) {
        if (filepaths == null || filepaths.length == 0) {
            return 0;
        } else {
            long total = 0;
            for (int i = 0; i < filepaths.length; i++) {
                total += getFileSize(filepaths[i]);
            }
            return total;
        }
    }


    public static String getDuplicateFileInfo(Context context, List<AppItemBean> items, String extension) {
        try {
            String result = "";
            for (AppItemBean item : items) {
                File file = new File(getAbsoluteWritePath(context, item, extension));
                if (file.exists() && !file.isDirectory()) {
                    result += file.getAbsolutePath();
                    result += "\n\n";
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 返回此项的绝对写入路径
     *
     * @param item      应用程序信息
     * @param extension 必须是“apk”或“zip”，否则此方法将返回空字符串
     * @return 此AppItemInfo的绝对写入路径
     */
    public static String getAbsoluteWritePath(Context context, AppItemBean item, String extension) {
        try {
            if ("apk".equals(extension.toLowerCase(Locale.ENGLISH))) {
                return SpUtils.getSavePath() + "/" + SpUtils.getFontApk().replace(Constant.FONT_APP_NAME, String.valueOf(item.appName))
                        .replace(Constant.FONT_APP_PACKAGE_NAME, String.valueOf(item.packageName))
                        .replace(Constant.FONT_APP_VERSIONCODE, String.valueOf(item.versioncode))
                        .replace(Constant.FONT_APP_VERSIONNAME, String.valueOf(item.version)) + ".apk";
            }
            if ("zip".equals(extension.toLowerCase(Locale.ENGLISH))) {
                return SpUtils.getSavePath() + "/" + SpUtils.getFontZip().replace(Constant.FONT_APP_NAME, String.valueOf(item.appName))
                        .replace(Constant.FONT_APP_PACKAGE_NAME, String.valueOf(item.packageName))
                        .replace(Constant.FONT_APP_VERSIONCODE, String.valueOf(item.versioncode))
                        .replace(Constant.FONT_APP_VERSIONNAME, String.valueOf(item.version)) + ".zip";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
