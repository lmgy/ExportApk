package com.lmgy.exportapk.config;


import com.lmgy.exportapk.utils.StorageUtils;

/**
 * @author lmgy
 * @date 2019/10/24
 */
public class Constant {

    public static final String PREFERENCE_NAME = "settings";
    public static final String PREFERENCE_SAVE_PATH = "savepath";
    public static final String PREFERENCE_SAVE_PATH_DEFAULT = StorageUtils.getMainStoragePath() + "/Backup";
    public static final String PREFERENCE_FILENAME_FONT_APK = "font_apk";
    public static final String PREFERENCE_FILENAME_FONT_ZIP = "font_zip";
    public static final String PREFERENCE_ZIP_COMPRESS_LEVEL = "zip_level";
    public static final int PREFERENCE_ZIP_COMPRESS_LEVEL_DEFAULT = -1;
    public static final String FONT_APP_NAME = "?N";
    public static final String FONT_APP_PACKAGE_NAME = "?P";
    public static final String FONT_APP_VERSIONCODE = "?C";
    public static final String FONT_APP_VERSIONNAME = "?V";
    public static final int ZIP_LEVEL_STORED = 0;
    public static final String PREFERENCE_FILENAME_FONT_DEFAULT = FONT_APP_PACKAGE_NAME + "-" + FONT_APP_VERSIONCODE;

}
