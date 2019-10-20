package com.lmgy.exportapk.listener;

import android.util.Log;

import com.lmgy.exportapk.adapter.AppListAdapter;
import com.lmgy.exportapk.ui.activity.MainActivity;

/**
 * @author lmgy
 * @date 2019/10/20
 */
public class ListenerMultiSelectMode implements AppListAdapter.OnItemClickListener {

    private MainActivity activity;
    public ListenerMultiSelectMode(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onItemClick(int position) {
        Log.e("AppListAdapter", "onItemClick: ListenerMultiSelectMode");
        activity.updateMultiSelectMode(position);
    }
}
