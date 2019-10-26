package com.lmgy.exportapk.utils;

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

    public static long getFileSize(String filePath) {
        return filePath != null ? getFileOrFolderSize(new File(filePath)) : 0;
    }

    public static String getDuplicateFileInfo(List<AppItemBean> items, String extension) {
        try {
            String result = "";
            for (AppItemBean item : items) {
                File file = new File(getAbsoluteWritePath(item, extension));
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
    public static String getAbsoluteWritePath(AppItemBean item, String extension) {
        try {
            if ("apk".equals(extension.toLowerCase(Locale.ENGLISH))) {
                return SpUtils.getSavePath() + "/" + SpUtils.getFontApk().replace(Constant.FONT_APP_NAME, String.valueOf(item.getAppName()))
                        .replace(Constant.FONT_APP_PACKAGE_NAME, String.valueOf(item.getPackageName()))
                        .replace(Constant.FONT_APP_VERSIONCODE, String.valueOf(item.getVersioncode()))
                        .replace(Constant.FONT_APP_VERSIONNAME, String.valueOf(item.getVersion())) + ".apk";
            }
            if ("zip".equals(extension.toLowerCase(Locale.ENGLISH))) {
                return SpUtils.getSavePath() + "/" + SpUtils.getFontZip().replace(Constant.FONT_APP_NAME, String.valueOf(item.getAppName()))
                        .replace(Constant.FONT_APP_PACKAGE_NAME, String.valueOf(item.getPackageName()))
                        .replace(Constant.FONT_APP_VERSIONCODE, String.valueOf(item.getVersioncode()))
                        .replace(Constant.FONT_APP_VERSIONNAME, String.valueOf(item.getVersion())) + ".zip";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
