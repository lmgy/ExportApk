package com.lmgy.exportapk;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.lmgy.exportapk.config.Constant;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences settings = Global.getGlobalSharedPreferences(this);
        int nightMode = settings.getInt(Constant.INSTANCE.getPREFERENCE_NIGHT_MODE(), Constant.INSTANCE.getPREFERENCE_NIGHT_MODE_DEFAULT());
        AppCompatDelegate.setDefaultNightMode(nightMode);
    }
}
