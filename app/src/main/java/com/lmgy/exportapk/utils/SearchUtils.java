package com.lmgy.exportapk.utils;

import com.lmgy.exportapk.bean.AppItemBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observable;

/**
 * @author lmgy
 * @date 2019/10/20
 */
public class SearchUtils {

    public static Observable<List<AppItemBean>> getSearch(String info, List<AppItemBean> appItemBeanList) {
        String searchInfo = info.trim().toLowerCase(Locale.ENGLISH);
        return Observable.create(emitter -> {
            List<AppItemBean> retList = new ArrayList<>();
            if (searchInfo.length() > 0) {
                for (int i = 0; i < appItemBeanList.size(); i++) {
                    try {
                        if (appItemBeanList.get(i).getAppName().toLowerCase(Locale.ENGLISH).contains(searchInfo)
                                || appItemBeanList.get(i).getPackageName().toLowerCase(Locale.ENGLISH).contains(searchInfo)
                                || appItemBeanList.get(i).getVersion().toLowerCase(Locale.ENGLISH).contains(searchInfo)
                                || PinYinUtils.getFullSpell(appItemBeanList.get(i).getAppName()).toLowerCase(Locale.ENGLISH).contains(searchInfo)
                                || PinYinUtils.getFirstSpell(appItemBeanList.get(i).getAppName()).toLowerCase(Locale.ENGLISH).contains(searchInfo)
                                || PinYinUtils.getPinYin(appItemBeanList.get(i).getAppName()).toLowerCase(Locale.ENGLISH).contains(searchInfo)
                        ) {
                            retList.add(appItemBeanList.get(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            emitter.onNext(retList);
            emitter.onComplete();
        });
    }
}

