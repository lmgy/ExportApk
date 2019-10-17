package com.lmgy.exportapk.config;


import com.lmgy.exportapk.utils.StorageUtils;

public class Constant {

    public static final String PREFERENCE_NAME = "settings";
    public static final String PREFERENCE_SAVE_PATH = "savepath";
    public static final String PREFERENCE_SAVE_PATH_DEFAULT = StorageUtils.getMainStoragePath() + "/Backup";
    public static final String PREFERENCE_STORAGE_PATH = "storage_path";
    public static final String PREFERENCE_STORAGE_PATH_DEFAULT = StorageUtils.getMainStoragePath();
    public static final String PREFERENCE_FILENAME_FONT_APK = "font_apk";
    public static final String PREFERENCE_FILENAME_FONT_ZIP = "font_zip";
    public static final String PREFERENCE_ZIP_COMPRESS_LEVEL = "zip_level";
    public static final int PREFERENCE_ZIP_COMPRESS_LEVEL_DEFAULT = -1;
    public static final String PREFERENCE_SHAREMODE = "share_mode";
    public static final String PREFERENCE_SORT_CONFIG = "sort_config";
    public static final String PREFERENCE_SHOW_SYSTEM_APP = "show_system_app";
    public static final int SHARE_MODE_DIRECT = -1;
    public static final int SHARE_MODE_AFTER_EXTRACT = 0;
    public static final int PREFERENCE_SHAREMODE_DEFAULT = SHARE_MODE_DIRECT;
    public static final String FONT_APP_NAME = "?N";
    public static final String FONT_APP_PACKAGE_NAME = "?P";
    public static final String FONT_APP_VERSIONCODE = "?C";
    public static final String FONT_APP_VERSIONNAME = "?V";
    public static final int ZIP_LEVEL_STORED = 0;
    public static final int ZIP_LEVEL_LOW = 1;
    public static final int ZIP_LEVEL_NORMAL = 5;
    public static final int ZIP_LEVEL_HIGH = 9;
    public static final String PREFERENCE_FILENAME_FONT_DEFAULT = FONT_APP_PACKAGE_NAME + "-" + FONT_APP_VERSIONCODE;

}
