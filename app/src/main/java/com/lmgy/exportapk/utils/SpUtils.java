package com.lmgy.exportapk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.lmgy.exportapk.config.Constant;

/**
 * @author lmgy
 * @date 2019/10/23
 */
public class SpUtils {

    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;

    public static void init(Context context) {
        settings = context.getSharedPreferences(Constant.PREFERENCE_NAME, Activity.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static String getSavePath() {
        return settings.getString(Constant.PREFERENCE_SAVE_PATH, Constant.PREFERENCE_SAVE_PATH_DEFAULT);
    }

    public static void setSavePath(String path){
        editor.putString(Constant.PREFERENCE_SAVE_PATH, path);
        editor.apply();
    }

    public static String getFontApk(){
        return settings.getString(Constant.PREFERENCE_FILENAME_FONT_APK, Constant.PREFERENCE_FILENAME_FONT_DEFAULT);
    }

    public static String getFontZip(){
        return settings.getString(Constant.PREFERENCE_FILENAME_FONT_ZIP, Constant.PREFERENCE_FILENAME_FONT_DEFAULT);
    }

    public static void setFontApk(String apk){
        editor.putString(Constant.PREFERENCE_FILENAME_FONT_APK, apk);
        editor.apply();
    }

    public static void setFontZip(String zip){
        editor.putString(Constant.PREFERENCE_FILENAME_FONT_ZIP, zip);
        editor.apply();
    }
}
