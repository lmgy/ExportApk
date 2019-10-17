package com.lmgy.exportapk.listener;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.AppListAdapter;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.widget.AppDetailDialog;

/**
 * @author lmgy
 * @date 2019/10/17
 */
public class ListenerNormalMode implements AppListAdapter.OnItemClickListener {

    private static final String TAG = "ListenerNormalMode";
    private Context mContext;
    private AppListAdapter mAdapter;

    public ListenerNormalMode(Context context, AppListAdapter adapter) {
        this.mContext = context;
        this.mAdapter = adapter;
    }

    @Override
    public void onItemClick(int position) {
        Log.e(TAG, "onItemClick: " );
        if(mAdapter != null){
            Log.e(TAG, "onItemClick: " );
            AppItemBean item = mAdapter.getAppList().get(position);
            AppDetailDialog appDetailDialog = new AppDetailDialog(mContext, R.style.BottomSheetDialog);
            appDetailDialog.setTitle(item.getAppName());
            appDetailDialog.setIcon(item.getIcon());
            appDetailDialog.setAppInfo(item.getVersion(), item.getVersioncode(), item.getLastUpdateTime(), item.getAppSize());
            if (Build.VERSION.SDK_INT >= 24) {
                appDetailDialog.setAPPMinSDKVersion(item.getMinSdkVersion());
            }
            appDetailDialog.show();
        }
    }



}
