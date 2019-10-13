package com.lmgy.exportapk.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lmgy.exportapk.Global;
import com.lmgy.exportapk.bean.AppItemBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class SearchTask implements Runnable {

    private boolean isInterrupted = false;
    private final String searchInfo;
    private final List<AppItemBean> totalList;
    private final ArrayList<AppItemBean> searchResult = new ArrayList<>();
    private SearchTaskCompletedCallback callback;

    public SearchTask(@NonNull List<AppItemBean> totalList, @NonNull String info, @Nullable SearchTaskCompletedCallback callback) {
        this.searchInfo = info;
        this.totalList = totalList;
        this.callback = callback;
    }


    @Override
    public void run() {
        for (AppItemBean item : totalList) {
            if (isInterrupted) {
                searchResult.clear();
                return;
            }
            try {
                boolean b = (getFormatString(item.getAppName()).contains(searchInfo)
                        || getFormatString(item.getPackageName()).contains(searchInfo)
                        || getFormatString(item.getVersionName()).contains(searchInfo)
                        || PinyinUtils.getFirstSpell(item.getAppName()).contains(searchInfo)
                        || PinyinUtils.getFullSpell(item.getAppName()).contains(searchInfo)
                        || PinyinUtils.getPinYin(item.getAppName()).contains(searchInfo)) && !"".equals(searchInfo.trim());
                if (b) {
                    searchResult.add(item);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Global.HANDLER.post(() -> {
            if (callback != null && !isInterrupted) {
                callback.onSearchTaskCompleted(searchResult);
            }
        });
    }

    public void setInterrupted() {
        isInterrupted = true;
    }

    @NonNull
    private static String getFormatString(@NonNull String s) {
        return s.trim().toLowerCase(Locale.getDefault());
    }

    public interface SearchTaskCompletedCallback {
        void onSearchTaskCompleted(@NonNull List<AppItemBean> result);
    }

}
