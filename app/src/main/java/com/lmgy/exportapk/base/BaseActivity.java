package com.lmgy.exportapk.base;

import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public abstract class BaseActivity extends AppCompatActivity {

    public static final String EXTRA_PARCELED_APP_ITEM = "app_item";

    public static final String EXTRA_PACKAGE_NAME = "package_name";

    public void setIconEnable(Menu menu, boolean enable) {
        try {
            //Class.forName("com.android.internal.view.menu.MenuBuilder");
            Class<?> clazz = Class.forName("android.support.v7.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);
            m.invoke(menu, enable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
