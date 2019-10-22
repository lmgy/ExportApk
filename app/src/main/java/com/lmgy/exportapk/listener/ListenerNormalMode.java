package com.lmgy.exportapk.listener;

import android.content.Context;
import android.os.Build;
import android.text.format.Formatter;
import android.view.View;
import android.widget.CheckBox;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.AppListAdapter;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.utils.FileUtils;
import com.lmgy.exportapk.utils.StorageUtils;
import com.lmgy.exportapk.widget.AppDetailDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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
        if (mAdapter != null) {
            AppItemBean item = mAdapter.getAppList().get(position);
            AppDetailDialog appDetailDialog = new AppDetailDialog(mContext, R.style.BottomSheetDialog);
            appDetailDialog.setTitle(item.getAppName());
            appDetailDialog.setIcon(item.getIcon());
            appDetailDialog.setAppInfo(item.getVersion(), item.getVersioncode(), item.getLastUpdateTime(), item.getAppSize());
            if (Build.VERSION.SDK_INT >= 24) {
                appDetailDialog.setAPPMinSDKVersion(item.getMinSdkVersion());
            }
            appDetailDialog.show();


            calculateSize(item)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Long>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<Long> longList) {
                            CheckBox cbData = appDetailDialog.findViewById(R.id.dialog_appdetail_extract_data_cb);
                            CheckBox cbObb = appDetailDialog.findViewById(R.id.dialog_appdetail_extract_obb_cb);
                            cbData.setText("Data(" + Formatter.formatFileSize(mContext, longList.get(0)) + ")");
                            cbObb.setText("Obb(" + Formatter.formatFileSize(mContext, longList.get(1)) + ")");
                            cbData.setVisibility(View.VISIBLE);
                            cbObb.setVisibility(View.VISIBLE);
                            appDetailDialog.findViewById(R.id.dialog_appdetail_extract_extra_pb).setVisibility(View.GONE);
                            cbData.setEnabled(longList.get(0) > 0);
                            cbObb.setEnabled(longList.get(1) > 0);
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        }
    }

    private Observable<List<Long>> calculateSize(AppItemBean item) {
        Observable<List<Long>> observable = Observable.create(emitter -> {
            List<Long> list = new ArrayList<>();
            list.add(FileUtils.getFileOrFolderSize(new File(StorageUtils.getMainStoragePath() + "/android/data/" + item.packageName)));
            list.add(FileUtils.getFileOrFolderSize(new File(StorageUtils.getMainStoragePath() + "/android/obb/" + item.packageName)));
            emitter.onNext(list);
            emitter.onComplete();
        });
        return observable;
    }


}
