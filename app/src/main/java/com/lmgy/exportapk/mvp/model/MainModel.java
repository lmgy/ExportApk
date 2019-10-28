package com.lmgy.exportapk.mvp.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.event.ProgressEvent;
import com.lmgy.exportapk.mvp.contract.MainContract;
import com.lmgy.exportapk.utils.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public class MainModel implements MainContract.Model {

    @Override
    public Observable<List<AppItemBean>> getAppList(Context context, boolean showSystemApp) {
        return Observable.create(emitter -> {
            PackageManager packagemanager = context.getPackageManager();
            List<PackageInfo> packageList = packagemanager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            List<AppItemBean> appItemBeanList1 = new ArrayList<>();
            for (int i = 0; i < packageList.size(); i++) {
                PackageInfo pak = packageList.get(i);
                AppItemBean appItem = new AppItemBean();
                if (!showSystemApp) {
                    if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                        appItem.setIcon(packagemanager.getApplicationIcon(pak.applicationInfo));
                        appItem.setAppName(packagemanager.getApplicationLabel(pak.applicationInfo).toString());
                        appItem.setPackageName(pak.applicationInfo.packageName);
                        appItem.setAppSize(FileUtils.getFileSize(pak.applicationInfo.sourceDir));
                        appItem.setPath(pak.applicationInfo.sourceDir);
                        appItem.setVersion(pak.versionName);
                        appItem.setVersioncode(pak.versionCode);
                        appItem.setLastUpdateTime(pak.lastUpdateTime);
                        if (Build.VERSION.SDK_INT >= 24) {
                            appItem.setMinSdkVersion(pak.applicationInfo.minSdkVersion);
                        }
                        appItemBeanList1.add(appItem);
                    }
                } else {
                    appItem.setIcon(packagemanager.getApplicationIcon(pak.applicationInfo));
                    appItem.setAppName(packagemanager.getApplicationLabel(pak.applicationInfo).toString());
                    appItem.setPackageName(pak.applicationInfo.packageName);
                    appItem.setAppSize(FileUtils.getFileSize(pak.applicationInfo.sourceDir));
                    appItem.setPath(pak.applicationInfo.sourceDir);
                    appItem.setVersion(pak.versionName);
                    appItem.setVersioncode(pak.versionCode);
                    appItem.setLastUpdateTime(pak.lastUpdateTime);
                    if (Build.VERSION.SDK_INT >= 24) {
                        appItem.setMinSdkVersion(pak.applicationInfo.minSdkVersion);
                    }
                    if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                        appItem.setSystemApp(true);
                    }
                    appItemBeanList1.add(appItem);
                }
                EventBus.getDefault().post(new ProgressEvent(i + 1));
            }
            emitter.onNext(appItemBeanList1);
            emitter.onComplete();
        });
    }

}
