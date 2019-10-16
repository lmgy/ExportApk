package com.lmgy.exportapk.ui.activity;

import android.app.AlertDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.AppListAdapter;
import com.lmgy.exportapk.base.BaseActivity;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.utils.FileUtils;
import com.lmgy.exportapk.widget.LoadListDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


/**
 * @author lmgy
 * @date 2019/10/13
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    public boolean shareAfterExtract = false;
    public boolean showSystemApp = false;
    public boolean isMultiSelectMode = false, isSearchMode = false;

    public LoadListDialog dialogLoadList;
    //    public FileCopyDialog dialogCopyFile;
//    public SortDialog dialogSort;
    public AlertDialog dialogWait;
    public List<AppItemBean> listExtractMulti = new ArrayList<>();
    public Thread threadAppInfo, threadSearch, threadExtractApp;
    //    public CopyFilesTask runnableExtractApp;
    public RecyclerView recyclerView;
    private AppListAdapter list_adapter;

    //    private SearchTask runnableSearch;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private CardView cardView;
    private Menu menu;
    private ProgressBar pg_search;
    private String keyword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

//        dialogLoadList = new LoadListDialog(this);
//        dialogLoadList.setTitle(getResources().getString(R.string.activity_main_loading));
//        dialogLoadList.setCancelable(false);
//        dialogLoadList.setCanceledOnTouchOutside(false);
//        dialogLoadList.setMax(getPackageManager().getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT).size());

        refreshList(true);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        toolbar = findViewById(R.id.toolbar);
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.rv_main);
        cardView = findViewById(R.id.card_bar);
        pg_search = findViewById(R.id.progressbar_search);
        findViewById(R.id.choice_app_view).setVisibility(View.GONE);
        findViewById(R.id.main_msg_view).setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        setSupportActionBar(toolbar);
        fab.setOnClickListener(this);
    }


    private void refreshList(boolean isShowProcessDialog) {
        recyclerView.setAdapter(null);
//        Main.sendEmptyMessage(MESSAGE_SET_NORMAL_TEXT_ATT);
        findViewById(R.id.showSystemAPP).setEnabled(false);
//        listSum = new ArrayList<AppItemBean>();
        if (dialogLoadList != null && isShowProcessDialog) {
            dialogLoadList.show();
        }
        getObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AppItemBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<AppItemBean> appItemBeans) {
                        list_adapter = new AppListAdapter(getApplicationContext(), appItemBeans, true);
                        recyclerView.setAdapter(list_adapter);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<List<AppItemBean>> getObservable(){
        Observable<List<AppItemBean>> observable = Observable.create(emitter -> {
            PackageManager packagemanager = getPackageManager();
            List<PackageInfo> packageList = packagemanager.getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
            List<AppItemBean> appItemBeanList = new ArrayList<>();
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
                        appItemBeanList.add(appItem);
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
                        appItem.isSystemApp = true;
                    }
                    appItemBeanList.add(appItem);
                }
            }
            emitter.onNext(appItemBeanList);
        });
        return observable;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;

        }
    }


}
