package com.lmgy.exportapk.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * @author lmgy
 * @date 2019/10/27
 */
public class ToastUtils {

    private static Context mContext;
    private static Toast toast;

    private ToastUtils() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
    }

    public static Context getContext() {
        return mContext;
    }

    public static void show(String content) {
        if (toast == null) {
            toast = Toast.makeText(mContext, content, Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

    public static void show(String content, int duratior) {
        if (toast == null) {
            toast = Toast.makeText(mContext, content, duratior);
        } else {
            toast.setText(content);
        }
        toast.show();
    }

}
