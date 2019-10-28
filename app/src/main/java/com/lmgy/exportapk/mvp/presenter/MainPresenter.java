package com.lmgy.exportapk.mvp.presenter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.base.BasePresenter;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.mvp.contract.MainContract;
import com.lmgy.exportapk.mvp.model.MainModel;
import com.lmgy.exportapk.utils.CopyFilesUtils;
import com.lmgy.exportapk.utils.SearchUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public class MainPresenter extends BasePresenter<MainContract.View> implements MainContract.Presenter {

    private MainContract.Model model;

    public MainPresenter() {
        model = new MainModel();
    }


    @Override
    public void loadAppList(Context context, boolean showSystemApp) {
        model.getAppList(context, showSystemApp).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AppItemBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<AppItemBean> appItemBeans) {
                        mView.setAdapter(appItemBeans);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    @Override
    public void clickShare(Context context, AppItemBean item) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            String apkPath = item.getPath();
            File apk = new File(apkPath);
            Uri uri = Uri.fromFile(apk);
            intent.setType("application/vnd.android.package-archive");
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share) + item.getAppName());
            intent.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share) + item.getAppName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share) + item.getAppName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void extractApp(Context context, List<AppItemBean> list, Integer[] position) {
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
                CopyFilesUtils mCopyFilesUtils = new CopyFilesUtils(exportList, context);
                Thread mThread = new Thread(mCopyFilesUtils);
                mThread.start();
            }
        }
    }

    @Override
    public void updateSearchList(Context context, String text, List<AppItemBean> appItemBeanList) {
        String searchInfo = text.trim().toLowerCase(Locale.ENGLISH);
        SearchUtils.getSearch(searchInfo, appItemBeanList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AppItemBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<AppItemBean> appItemBeanList) {
                        mView.setAdapter(appItemBeanList);
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
