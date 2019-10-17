package com.lmgy.exportapk.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.AppListAdapter;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.utils.FileUtils;
import com.lmgy.exportapk.utils.StorageUtils;
import com.lmgy.exportapk.widget.AppDetailDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lmgy
 * @date 2019/10/17
 */
public class ListenerNormalMode implements AppListAdapter.OnItemClickListener {
    private AppListAdapter mAdapter;
    private Context mContext;

    public ListenerNormalMode(Context context, AppListAdapter appListAdapter) {
        this.mContext = context.getApplicationContext();
        this.mAdapter = appListAdapter;
    }

    @Override
    public void onItemClick(int position) {
        if (mAdapter != null) {
            final AppItemBean item = mAdapter.getAppList().get(position);
            final AppDetailDialog appDetailDialog = new AppDetailDialog(mContext, R.style.BottomSheetDialog);
            appDetailDialog.setTitle(item.getAppName());
            appDetailDialog.setIcon(item.getIcon());
            appDetailDialog.setAppInfo(item.getVersion(), item.getVersioncode(), item.getLastUpdateTime(), item.getAppSize());
            if (Build.VERSION.SDK_INT >= 24) {
                appDetailDialog.setAPPMinSDKVersion(item.getMinSdkVersion());
            }
            appDetailDialog.show();
            Observable<List<Long>> observable = Observable.create(emitter -> {
                List<Long> list = new ArrayList<>();
                list.add(FileUtils.getFileOrFolderSize(new File(StorageUtils.getMainStoragePath() + "/android/data/" + item.packageName)));
                list.add(FileUtils.getFileOrFolderSize(new File(StorageUtils.getMainStoragePath() + "/android/obb/" + item.packageName)));
                emitter.onNext(list);
                emitter.onComplete();
            });

            observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Long>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<Long> longList) {
                            long dataSize = longList.get(0);
                            long obbSize = longList.get(1);
                            try {
                                CheckBox cbData = appDetailDialog.findViewById(R.id.dialog_appdetail_extract_data_cb);
                                CheckBox cbObb = appDetailDialog.findViewById(R.id.dialog_appdetail_extract_obb_cb);
                                cbData.setText("Data(" + Formatter.formatFileSize(mContext, dataSize) + ")");
                                cbObb.setText("Obb(" + Formatter.formatFileSize(mContext, obbSize) + ")");
                                cbData.setVisibility(View.VISIBLE);
                                cbObb.setVisibility(View.VISIBLE);
                                appDetailDialog.findViewById(R.id.dialog_appdetail_extract_extra_pb).setVisibility(View.GONE);
                                cbData.setEnabled(dataSize > 0);
                                cbObb.setEnabled(obbSize > 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });



            appDetailDialog.areaExtract.setOnClickListener(v -> {
                appDetailDialog.cancel();
                final boolean data = ((CheckBox) appDetailDialog.findViewById(R.id.dialog_appdetail_extract_data_cb)).isChecked();
                final boolean obb = ((CheckBox) appDetailDialog.findViewById(R.id.dialog_appdetail_extract_obb_cb)).isChecked();
                List<AppItemBean> selectedList = new ArrayList<AppItemBean>();
                selectedList.add(item);

                List<AppItemBean> itemBeanList = new ArrayList<AppItemBean>();
                itemBeanList.add(mAdapter.getAppList().get(position));
                String duplicate = FileUtils.getDuplicateFileInfo(mContext, itemBeanList, (data || obb) ? "zip" : "apk");

                if (duplicate.length() > 0) {
                    new AlertDialog.Builder(mContext)
                            .setIcon(R.drawable.ic_icon_warn)
                            .setTitle(mContext.getResources().getString(R.string.activity_main_duplicate_title))
                            .setCancelable(true)
                            .setMessage(mContext.getResources().getString(R.string.activity_main_duplicate_message) + "\n\n" + duplicate)
                            .setPositiveButton(mContext.getResources().getString(R.string.dialog_button_positive), (dialog, which) -> {
                                mContext.shareAfterExtract = false;
                                Message msg_extract = new Message();
                                msg_extract.what = Main.MESSAGE_EXTRACT_SINGLE_APP;
                                msg_extract.obj = new Integer[]{Integer.valueOf(position), data ? 1 : 0, obb ? 1 : 0};
                                mContext.processExtractMsg(msg_extract);
                            })
                            .setNegativeButton(mContext.getResources().getString(R.string.dialog_button_negative), (dialog, which) -> {

                            })
                            .show();

                } else {
                    mContext.shareAfterExtract = false;
                    Message msg_extract = new Message();
                    msg_extract.what = Main.MESSAGE_EXTRACT_SINGLE_APP;
                    msg_extract.obj = new Integer[]{Integer.valueOf(position), data ? 1 : 0, obb ? 1 : 0};
                    mContext.processExtractMsg(msg_extract);
                }
            });

            appDetailDialog.areaShare.setOnClickListener(v -> {
                appDetailDialog.cancel();
                if (mContext.settings.getInt(Constant.PREFERENCE_SHAREMODE, Constant.PREFERENCE_SHAREMODE_DEFAULT) == Constant.SHARE_MODE_DIRECT) {
                    mContext.shareAfterExtract = false;
                    Message msg_share = new Message();
                    msg_share.what = Main.MESSAGE_SHARE_SINGLE_APP;
                    msg_share.obj = Integer.valueOf(position);
                    Main.sendMessage(msg_share);
                } else if (mContext.settings.getInt(Constant.PREFERENCE_SHAREMODE, Constant.PREFERENCE_SHAREMODE_DEFAULT) == Constant.SHARE_MODE_AFTER_EXTRACT) {
                    List<AppItemBean> itemBeanList = new ArrayList<>();
                    itemBeanList.add(mAdapter.getAppList().get(position));
                    extractMultiSelectedApps(itemBeanList, true);
                }

            });

            appDetailDialog.areaDetail.setOnClickListener(v -> {
                appDetailDialog.cancel();
                Intent appDetail = new Intent();
                appDetail.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                appDetail.setData(Uri.fromParts("package", item.getPackageName(), null));
                mContext.startActivity(appDetail);
            });

        }
    }


    private void extractMultiSelectedApps(List<AppItemBean> extractList, boolean isShare) {
        mContext.shareAfterExtract = isShare;
        final List<AppItemBean> list = new ArrayList<AppItemBean>();
        for (int i = 0; i < extractList.size(); i++) {
            list.add(new AppItemBean(extractList.get(i)));
        }
        mContext.list_extract_multi = list;
        mContext.dialog_wait = new AlertDialog.Builder(mContext)
                .setTitle(mContext.getResources().getString(R.string.activity_main_wait))
                .setView(LayoutInflater.from(mContext).inflate(R.layout.layout_extract_multi_extra, null))
                .setCancelable(false)
                .show();
        new Thread(() -> {
            long data = 0, obb = 0;
            for (AppItemBean item : mContext.list_extract_multi) {
                long data_get = FileUtils.getFileOrFolderSize(new File(StorageUtils.getMainStoragePath() + "/android/data/" + item.packageName));
                long obb_get = FileUtils.getFileOrFolderSize(new File(StorageUtils.getMainStoragePath() + "/android/obb/" + item.packageName));
                if (data_get > 0) {
                    item.exportData = true;
                }
                if (obb_get > 0) {
                    item.exportObb = true;
                }
                data += data_get;
                obb += obb_get;
            }
            Message msg = new Message();
            msg.what = mContext.MESSAGE_EXTRA_MULTI_SHOW_SELECTION_IAG;
            msg.obj = new Long[]{data, obb};
            mContext.sendMessage(msg);
        }).start();
    }


}
