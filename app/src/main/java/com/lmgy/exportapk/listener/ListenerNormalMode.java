package com.lmgy.exportapk.listener;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.AppListAdapter;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.utils.CopyFilesUtils;
import com.lmgy.exportapk.utils.FileUtils;
import com.lmgy.exportapk.utils.SpUtils;
import com.lmgy.exportapk.utils.StorageUtils;
import com.lmgy.exportapk.widget.AppDetailDialog;
import com.lmgy.exportapk.widget.FileCopyDialog;

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
    private CopyFilesUtils mCopyFilesUtils;
    private FileCopyDialog mFileCopyDialog;
    private Thread mThread;
    private List<AppItemBean> extractMultiList;
    private AlertDialog dialogWait;

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
                    clickShare(appDetailDialog, position);
                } else {
                    clickDetail(appDetailDialog, item);
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

    private void clickDetail(AppDetailDialog appDetailDialog, AppItemBean item) {
        if (appDetailDialog != null) {
            appDetailDialog.cancel();
        }
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
        String duplicate = FileUtils.getDuplicateFileInfo(mContext, listItem, (data || obb) ? "zip" : "apk");
        Log.e(TAG, "clickExtract: " + duplicate );
        if (duplicate.length() > 0) {
            new AlertDialog.Builder(mContext)
                    .setIcon(R.drawable.ic_icon_warn)
                    .setTitle(mContext.getResources().getString(R.string.activity_main_duplicate_title))
                    .setCancelable(true)
                    .setMessage(mContext.getResources().getString(R.string.activity_main_duplicate_message) + "\n\n" + duplicate)
                    .setPositiveButton(mContext.getResources().getString(R.string.dialog_button_positive), (dialog, which) -> {
                        extractApp(new Integer[]{position, data ? 1 : 0, obb ? 1 : 0});
                    })
                    .setNegativeButton(mContext.getResources().getString(R.string.dialog_button_negative), (dialog, which) -> {

                    })
                    .show();
        } else {
            extractApp(new Integer[]{position, data ? 1 : 0, obb ? 1 : 0});
        }
    }

    private void clickShare(AppDetailDialog appDetailDialog, int position) {
        appDetailDialog.cancel();
        directShare(position);
    }

    private void directShare(int pos) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            List<AppItemBean> list = mAdapter.getAppList();
            String apkPath = list.get(pos).getPath();
            File apk = new File(apkPath);
            Uri uri = Uri.fromFile(apk);
            intent.setType("application/vnd.android.package-archive");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.share) + list.get(pos).getAppName());
            intent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.share) + list.get(pos).getAppName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(Intent.createChooser(intent, mContext.getResources().getString(R.string.share) + list.get(pos).getAppName()));
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
                        item.exportData = true;
                    }
                    if (position[2] == 1) {
                        item.exportObb = true;
                    }
                    exportList.add(item);
                    mCopyFilesUtils = new CopyFilesUtils(exportList, mContext);
                    mThread = new Thread(mCopyFilesUtils);
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
