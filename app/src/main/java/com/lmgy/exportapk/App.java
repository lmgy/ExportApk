package com.lmgy.exportapk;

import android.app.Application;
import android.content.Context;

import com.lmgy.exportapk.utils.SpUtils;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class App extends Application {

    private static Context mContext;

    public static Context getContext() {
        return mContext;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        mContext = base.getApplicationContext();
        SpUtils.init(this);
    }

}
