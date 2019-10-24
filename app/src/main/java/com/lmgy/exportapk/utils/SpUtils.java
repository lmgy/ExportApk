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

    public static void init(Context context) {
        settings = context.getSharedPreferences(Constant.PREFERENCE_NAME, Activity.MODE_PRIVATE);
    }

    public static SharedPreferences getSettings() {
        return settings;
    }

}
