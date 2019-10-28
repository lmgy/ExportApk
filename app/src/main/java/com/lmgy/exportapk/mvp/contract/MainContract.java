package com.lmgy.exportapk.mvp.contract;

import android.content.Context;

import com.lmgy.exportapk.bean.AppItemBean;

import java.util.List;

import io.reactivex.Observable;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public interface MainContract {

    interface Model {

        Observable<List<AppItemBean>> getAppList(Context context, boolean showSystemApp);

    }

    interface View {

        void setAdapter(List<AppItemBean> appItemBeans);

    }


    interface Presenter {

        void loadAppList(Context context, boolean showSystemApp);

        void clickShare(Context context, AppItemBean item);

        void extractApp(Context context, List<AppItemBean> list, Integer[] position);

        void updateSearchList(Context context, String text, List<AppItemBean> appItemBeanList);
    }

}
