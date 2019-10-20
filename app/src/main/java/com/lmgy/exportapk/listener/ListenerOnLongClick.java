package com.lmgy.exportapk.listener;

import android.content.Context;
import android.util.Log;

import com.lmgy.exportapk.adapter.AppListAdapter;
import com.lmgy.exportapk.ui.activity.MainActivity;

/**
 * @author lmgy
 * @date 2019/10/20
 */
public class ListenerOnLongClick implements AppListAdapter.OnLongClickListener {

    private MainActivity activity;

    public ListenerOnLongClick(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public boolean onLongClick(int position) {
        Log.e("AppListAdapter", "onLongClick: ListenerOnLongClick");
        activity.startMultiSelectMode(position);
        return false;
    }
}