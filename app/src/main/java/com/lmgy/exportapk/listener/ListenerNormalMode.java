package com.lmgy.exportapk.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.format.Formatter;
import android.view.View;
import android.widget.CheckBox;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.AppListAdapter;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.utils.CopyFilesUtils;
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

            DialogClick dialogClick = position1 -> {
                if (position1 == 1) {
                    clickExtract(appDetailDialog, item, position);
                } else if (position1 == 2) {
                    appDetailDialog.cancel();
                    clickShare(position);
                } else {
                    appDetailDialog.cancel();
                    clickDetail(item);
                }
            };

            appDetailDialog.setonClickListener(dialogClick);
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

    private void clickDetail(AppItemBean item) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", item.getPackageName(), null));
        mContext.startActivity(intent);
    }

    private void clickExtract(AppDetailDialog appDetailDialog, AppItemBean item, int position) {
        appDetailDialog.cancel();
        boolean data = ((CheckBox) appDetailDialog.findViewById(R.id.dialog_appdetail_extract_data_cb)).isChecked();
        boolean obb = ((CheckBox) appDetailDialog.findViewById(R.id.dialog_appdetail_extract_obb_cb)).isChecked();
        List<AppItemBean> listItem = new ArrayList<>();
        listItem.add(item);
        String duplicate = FileUtils.getDuplicateFileInfo(listItem, (data || obb) ? "zip" : "apk");
        if (duplicate.length() > 0) {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.drawable.ic_icon_warn)
                    .setTitle(mContext.getResources().getString(R.string.activity_main_duplicate_title))
                    .setCancelable(true)
                    .setMessage(mContext.getResources().getString(R.string.activity_main_duplicate_message) + "\n\n" + duplicate)
                    .setPositiveButton(mContext.getResources().getString(R.string.dialog_button_positive), (dialog, which) -> extractApp(new Integer[]{position, data ? 1 : 0, obb ? 1 : 0}))
                    .setNegativeButton(mContext.getResources().getString(R.string.dialog_button_negative), null)
                    .show();
        } else {
            extractApp(new Integer[]{position, data ? 1 : 0, obb ? 1 : 0});
        }
    }

    private void clickShare(int position) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            List<AppItemBean> list = mAdapter.getAppList();
            String apkPath = list.get(position).getPath();
            File apk = new File(apkPath);
            Uri uri = Uri.fromFile(apk);
            intent.setType("application/vnd.android.package-archive");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.share) + list.get(position).getAppName());
            intent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.share) + list.get(position).getAppName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(Intent.createChooser(intent, mContext.getResources().getString(R.string.share) + list.get(position).getAppName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void extractApp(Integer[] position) {
        List<AppItemBean> list;
        if (mAdapter != null) {
            list = mAdapter.getAppList();
            if (list != null) {
                if (list.size() > 0) {
                    List<AppItemBean> exportList = new ArrayList<>();
                    AppItemBean item = new AppItemBean(list.get(position[0]));
                    if (position[1] == 1) {
                        item.setExportData(true);
                    }
                    if (position[2] == 1) {
                        item.setExportObb(true);
                    }
                    exportList.add(item);
                    CopyFilesUtils mCopyFilesUtils = new CopyFilesUtils(exportList, mContext);
                    Thread mThread = new Thread(mCopyFilesUtils);
//                    mFileCopyDialog = new FileCopyDialog(mContext);
//                    mFileCopyDialog.setCancelable(false);
//                    mFileCopyDialog.setCanceledOnTouchOutside(false);
//                    mFileCopyDialog.setMax(list.get(position[0]).getAppSize());
//                    mFileCopyDialog.setIcon(list.get(position[0]).getIcon());
//                    mFileCopyDialog.setTitle(mContext.getResources().getString(R.string.activity_main_extracting_title));
//                    mFileCopyDialog.setButton(AlertDialog.BUTTON_NEGATIVE, mContext.getResources().getString(R.string.activity_main_stop), new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            if (mThread != null) {
//                                if (mCopyFilesUtils != null) {
//                                    mCopyFilesUtils.setInterrupted();
//                                }
//                                mThread.interrupt();
//                                mThread = null;
//                            }
//                            mFileCopyDialog.cancel();
//                            Toast.makeText(mContext, "已停止!", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    mFileCopyDialog.show();
                    //TODO 需要把这个alertDialog添加到CopyFilesUtils
                    mThread.start();
                }
            }
        }
    }

    private Observable<List<Long>> calculateSize(AppItemBean item) {
        return Observable.create(emitter -> {
            List<Long> list = new ArrayList<>();
            list.add(FileUtils.getFileOrFolderSize(new File(StorageUtils.getMainStoragePath() + "/android/data/" + item.getPackageName())));
            list.add(FileUtils.getFileOrFolderSize(new File(StorageUtils.getMainStoragePath() + "/android/obb/" + item.getPackageName())));
            emitter.onNext(list);
            emitter.onComplete();
        });
    }
}
