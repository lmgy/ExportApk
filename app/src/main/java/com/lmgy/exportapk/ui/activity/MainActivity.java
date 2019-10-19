package com.lmgy.exportapk.ui.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
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
import com.lmgy.exportapk.listener.ListenerNormalMode;
import com.lmgy.exportapk.utils.FileUtils;
import com.lmgy.exportapk.widget.LoadListDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import io.reactivex.Observable;
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
    private List<AppItemBean> appItemBeanList = new ArrayList<>();
    public List<AppItemBean> listExtractMulti = new ArrayList<>();
    public Thread threadAppInfo, threadSearch, threadExtractApp;
    //    public CopyFilesTask runnableExtractApp;
    public RecyclerView recyclerView;
    private AppListAdapter mAdapter;

    //    private SearchTask runnableSearch;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private CardView cardView;
    private Menu menu;
    private ProgressBar pg_search;
    private String keyword;

    private Handler mHandler;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(getMainLooper());
        mContext = this;
        ButterKnife.bind(this);

        dialogLoadList = new LoadListDialog(this);
        dialogLoadList.setTitle(getResources().getString(R.string.activity_main_loading));
        dialogLoadList.setCancelable(false);
        dialogLoadList.setCanceledOnTouchOutside(false);
        dialogLoadList.setMax(getPackageManager().getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT).size());

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
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        setSupportActionBar(toolbar);
        fab.setOnClickListener(this);
    }


    private void refreshList(boolean isShowProcessDialog) {
        recyclerView.setAdapter(null);
        findViewById(R.id.showSystemAPP).setEnabled(false);
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
                        appItemBeanList = appItemBeans;
                        mAdapter = new AppListAdapter(getApplicationContext(), appItemBeans, true);
                        recyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        dialogLoadList.cancel();
                        mAdapter.setItemClickListener(new ListenerNormalMode(mContext, mAdapter));
                    }
                });
    }

    private Observable<List<AppItemBean>> getObservable() {
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
                final int process = i + 1;
                mHandler.post(() -> dialogLoadList.setProgress(process));
            }
            emitter.onNext(appItemBeanList);
            emitter.onComplete();
        });
        return observable;
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
//            if (isMultiSelectMode) {
//                closeMultiSelectMode();
//            } else if (isSearchMode) {
//                closeSearchView();
//            } else {
//                this.finish();
//            }
//            return true;
//        } else {
//            return super.onKeyDown(keyCode, event);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case android.R.id.home:
                break;
            case R.id.action_search:
                break;
            case R.id.action_sort:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;

        }
    }


    private void setMenuVisible(boolean isVisible) {
        if (this.menu != null) {
            for (int i = 0; i < menu.size(); i++) {
                this.menu.getItem(i).setEnabled(isVisible);
                this.menu.getItem(i).setVisible(isVisible);
            }
        }
    }

//    private void closeSearchView() {
//        isSearchMode = false;
////        if (runnable_search != null) {
////            runnable_search.setInterrupted();
////        }
////        if (this.thread_search != null) {
////            thread_search.interrupt();
////            thread_search = null;
////        }
//
//        mAdapter = new AppListAdapter(this, appItemBeanList, true);
//        pg_search.setVisibility(View.GONE);
//        recyclerView.setAdapter(mAdapter);
////        mAdapter.setItemClickListener(new ListenerNormalMode(this, mAdapter));
////        mAdapter.setLongClickListener(new ListenerOnLongClick(this));
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_multiselectarea_entry);
//        cardView.startAnimation(anim);
//        cardView.setVisibility(View.VISIBLE);
//        setMenuVisible(true);
////        Main.sendEmptyMessage(MESSAGE_SET_NORMAL_TEXT_ATT);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }


//    public void closeMultiSelectMode() {
//        TextView extract = findViewById(R.id.text_extract);
//        TextView share = findViewById(R.id.text_share);
//        TextView selectAll = findViewById(R.id.text_selectall);
//        TextView deselectAll = findViewById(R.id.text_deselectall);
//        isMultiSelectMode = false;
//        extract.setOnClickListener(null);
//        share.setOnClickListener(null);
//        selectAll.setOnClickListener(null);
//        deselectAll.setOnClickListener(null);
//
//        mAdapter.cancelMutiSelectMode();
////        mAdapter.setItemClickListener(new ListenerNormalMode(this, mAdapter));
////        mAdapter.setLongClickListener(new ListenerOnLongClick(this));
//
//        Animation animExit = AnimationUtils.loadAnimation(this, R.anim.anim_multiselectarea_exit);
//        Animation animEntry = AnimationUtils.loadAnimation(this, R.anim.anim_multiselectarea_entry);
//
//        View multiSelectArea = findViewById(R.id.choice_app_view);
//        View mainMsgView = findViewById(R.id.main_msg_view);
//
//        multiSelectArea.startAnimation(animExit);
//        multiSelectArea.setVisibility(View.GONE);
//
//        mainMsgView.startAnimation(animEntry);
//        mainMsgView.setVisibility(View.VISIBLE);
//
//        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//    }

//    @SuppressLint("InflateParams")
//    private void clickActionAbout() {
//        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_about, null);
//        AlertDialog dialogAbout = new AlertDialog.Builder(this)
//                .setTitle(getResources().getString(R.string.app_name))
//                .setIcon(R.mipmap.ic_launcher_round)
//                .setCancelable(true)
//                .setView(dialogView)
//                .setPositiveButton(getResources().getString(R.string.dialog_button_positive), (dialogInterface, i) -> {
//                }).create();
//        dialogAbout.show();
//    }

}
