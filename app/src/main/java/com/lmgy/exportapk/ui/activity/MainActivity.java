package com.lmgy.exportapk.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.lmgy.exportapk.listener.DialogClick;
import com.lmgy.exportapk.listener.ListenerMultiSelectMode;
import com.lmgy.exportapk.listener.ListenerNormalMode;
import com.lmgy.exportapk.listener.ListenerOnLongClick;
import com.lmgy.exportapk.utils.CopyFilesUtils;
import com.lmgy.exportapk.utils.FileUtils;
import com.lmgy.exportapk.utils.SearchUtils;
import com.lmgy.exportapk.widget.LoadListDialog;
import com.lmgy.exportapk.widget.SortDialog;
import com.wyt.searchbox.SearchFragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
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

    @BindView(R.id.rv_main)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.card_bar)
    CardView mCardView;
    @BindView(R.id.progressbar_search)
    ProgressBar mProgressBar;
    @BindView(R.id.showSystemAPP)
    CheckBox mCheckBox;
    @BindView(R.id.text_selectall)
    TextView mSelectAll;
    @BindView(R.id.text_deselectall)
    TextView mDeselectAll;
    @BindView(R.id.text_extract)
    TextView mExtract;
    @BindView(R.id.text_share)
    TextView mShare;

    private boolean showSystemApp = false;
    private boolean isMultiSelectMode = false;
    private boolean isSearchMode = false;
    private AppListAdapter mAdapter;
    private Menu menu;
    private String keyword;
    private LoadListDialog mDialogLoadList;
    private SortDialog mDialogSort;
    private List<AppItemBean> appItemBeanList = new ArrayList<>();
    private Handler mHandler;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(getMainLooper());
        mContext = this;

        mDialogLoadList = new LoadListDialog(this);
        mDialogLoadList.setTitle(getResources().getString(R.string.activity_main_loading));
        mDialogLoadList.setCancelable(false);
        mDialogLoadList.setCanceledOnTouchOutside(false);
        mDialogLoadList.setMax(getPackageManager().getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT).size());

        refreshList();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        findViewById(R.id.choice_app_view).setVisibility(View.GONE);
        findViewById(R.id.main_msg_view).setVisibility(View.VISIBLE);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        setSupportActionBar(mToolbar);
        mFab.setOnClickListener(this);

        mCheckBox.setOnCheckedChangeListener((button, isChecked) -> {
            if (isMultiSelectMode) {
                closeMultiSelectMode();
            }
            showSystemApp = isChecked;
            refreshList();
        });
    }


    private void refreshList() {
        mRecyclerView.setAdapter(null);
        if (mDialogLoadList != null) {
            mDialogLoadList.show();
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
                        mAdapter = new AppListAdapter(getApplicationContext(), appItemBeans);
                        mRecyclerView.setAdapter(mAdapter);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mDialogLoadList.cancel();
                        mAdapter.setItemClickListener(new ListenerNormalMode(mContext, mAdapter));
                        mAdapter.setLongClickListener(new ListenerOnLongClick((MainActivity) mContext));
                    }
                });
    }

    private Observable<List<AppItemBean>> getObservable() {
        return Observable.create(emitter -> {
            PackageManager packagemanager = getPackageManager();
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
                final int process = i + 1;
                mHandler.post(() -> mDialogLoadList.setProgress(process));
            }
            emitter.onNext(appItemBeanList1);
            emitter.onComplete();
        });
    }

    public void updateMultiSelectMode(int position) {
        mAdapter.onItemClicked(position);
        mExtract.setText(getResources().getString(R.string.button_extract) + "(" + mAdapter.getSelectedNum() + ")");
        mShare.setText(getResources().getString(R.string.button_share) + "(" + mAdapter.getSelectedNum() + ")");

        if (mAdapter.getSelectedNum() > 0) {
            mExtract.setClickable(true);
            mShare.setClickable(true);
            mExtract.setOnClickListener(this);
            mShare.setOnClickListener(this);
        } else {
            mShare.setOnClickListener(null);
            mExtract.setOnClickListener(null);
        }
    }


    public void startMultiSelectMode(int position) {
        isMultiSelectMode = true;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAdapter.setMultiSelectMode();
        updateMultiSelectMode(position);

        mSelectAll.setClickable(true);
        mSelectAll.setOnClickListener(v -> {
            mAdapter.selectAll();
            updateMultiSelectMode(-1);
        });

        mDeselectAll.setClickable(true);
        mDeselectAll.setOnClickListener(v -> {
            mAdapter.deselectAll();
            updateMultiSelectMode(-1);
        });

        mAdapter.setItemClickListener(new ListenerMultiSelectMode(this));
        mAdapter.setLongClickListener(new ListenerOnLongClick(this));

        View multiSelectArea = findViewById(R.id.choice_app_view);
        findViewById(R.id.main_msg_view).setVisibility(View.GONE);

        Animation anim = AnimationUtils.loadAnimation(mContext, R.anim.anim_multiselectarea_entry);
        multiSelectArea.startAnimation(anim);
        multiSelectArea.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            backToParent();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void backToParent() {
        if (isMultiSelectMode) {
            closeMultiSelectMode();
        } else if (isSearchMode) {
            closeSearchView();
        } else {
            finish();
        }
    }

    private void clickActionSearch() {
        isSearchMode = true;
        if (isMultiSelectMode) {
            closeMultiSelectMode();
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_multiselectarea_entry);
            mCardView.startAnimation(anim);
            mCardView.setVisibility(View.VISIBLE);
        }
        SearchFragment searchFragment = SearchFragment.newInstance();
        searchFragment.showFragment(getSupportFragmentManager(), SearchFragment.TAG);
        searchFragment.setOnSearchClickListener(keyword -> {
            Toast.makeText(getBaseContext(), keyword, Toast.LENGTH_SHORT).show();
            this.keyword = keyword;
            showSearchView();
        });
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_multiselectarea_exit);
        mCardView.startAnimation(anim);
        mCardView.setVisibility(View.GONE);
    }

    private void clickActionSort() {
        DialogClick click = position -> {
            if (position == 0) {
                if (isMultiSelectMode) {
                    closeMultiSelectMode();
                }
                AppItemBean.SortConfig = 0;
                mRecyclerView.setAdapter(null);
                refreshList();
                mDialogSort.cancel();
            } else {
                dialogClick(position);
            }
        };
        mDialogSort = new SortDialog(this, click);
        mDialogSort.show();
    }

    private void dialogClick(int sortConfig) {
        AppItemBean.SortConfig = sortConfig;
        sortList();
        mDialogSort.cancel();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            default:
                break;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case android.R.id.home:
                backToParent();
                break;
            case R.id.action_search:
                clickActionSearch();
                break;
            case R.id.action_sort:
                clickActionSort();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
            case R.id.text_extract:
                if (mAdapter == null) {
                    return;
                }
                for (int i = 0; i < mAdapter.getAppList().size(); i++) {
                    if (mAdapter.getIsSelected()[i]) {
                        extractApp(new Integer[]{i, 1, 0});
                    }
                }
                break;
            case R.id.text_share:
                if (mAdapter == null) {
                    return;
                }
                for (int i = 0; i < mAdapter.getAppList().size(); i++) {
                    if (mAdapter.getIsSelected()[i]) {
                        clickShare(i);
                    }
                }
                break;
            case R.id.fab:
                if (isSearchMode) {
                    updateSearchList(keyword);
                } else {
                    refreshList();
                }
                break;
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
                    mThread.start();
                }
            }
        }
    }

    private void sortList() {
        if (mAdapter != null && !isSearchMode) {
            if (isMultiSelectMode) {
                closeMultiSelectMode();
            }
            Collections.sort(appItemBeanList);
            mAdapter = new AppListAdapter(this, appItemBeanList);
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setItemClickListener(new ListenerNormalMode(mContext, mAdapter));
            mAdapter.setLongClickListener(new ListenerOnLongClick(this));
        }
    }

    private void showSearchView() {
        updateSearchList(keyword);
        isSearchMode = true;
        mAdapter.setLongClickListener(null);
        setMenuVisible(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateSearchList(String text) {
        String searchInfo = text.trim().toLowerCase(Locale.ENGLISH);
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(mAdapter);
        SearchUtils.getSearch(searchInfo, appItemBeanList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<AppItemBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<AppItemBean> appItemBeanList) {
                        mAdapter = new AppListAdapter(mContext, appItemBeanList);
                        mRecyclerView.setAdapter(mAdapter);
                        mAdapter.setItemClickListener(new ListenerNormalMode(mContext, mAdapter));
                        mAdapter.setLongClickListener(new ListenerOnLongClick((MainActivity) mContext));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void setMenuVisible(boolean isVisible) {
        if (this.menu != null) {
            for (int i = 0; i < menu.size(); i++) {
                this.menu.getItem(i).setEnabled(isVisible);
                this.menu.getItem(i).setVisible(isVisible);
            }
        }
    }

    private void closeSearchView() {
        isSearchMode = false;
        mAdapter = new AppListAdapter(this, appItemBeanList);
        mProgressBar.setVisibility(View.GONE);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemClickListener(new ListenerNormalMode(this, mAdapter));
        mAdapter.setLongClickListener(new ListenerOnLongClick(this));

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.anim_multiselectarea_entry);
        mCardView.startAnimation(anim);
        mCardView.setVisibility(View.VISIBLE);
        setMenuVisible(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        return true;
    }

    public void closeMultiSelectMode() {
        isMultiSelectMode = false;
        mExtract.setOnClickListener(null);
        mShare.setOnClickListener(null);
        mSelectAll.setOnClickListener(null);
        mDeselectAll.setOnClickListener(null);

        mAdapter.cancelMutiSelectMode();
        mAdapter.setItemClickListener(new ListenerNormalMode(this, mAdapter));
        mAdapter.setLongClickListener(new ListenerOnLongClick(this));

        Animation animExit = AnimationUtils.loadAnimation(this, R.anim.anim_multiselectarea_exit);
        Animation animEntry = AnimationUtils.loadAnimation(this, R.anim.anim_multiselectarea_entry);

        View multiSelectArea = findViewById(R.id.choice_app_view);
        View mainMsgView = findViewById(R.id.main_msg_view);

        multiSelectArea.startAnimation(animExit);
        multiSelectArea.setVisibility(View.GONE);

        mainMsgView.startAnimation(animEntry);
        mainMsgView.setVisibility(View.VISIBLE);

        if (!isSearchMode) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @SuppressLint("InflateParams")
    private void clickActionAbout() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_about, null);
        AlertDialog dialogAbout = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.app_name))
                .setIcon(R.mipmap.ic_launcher_round)
                .setCancelable(true)
                .setView(dialogView)
                .setPositiveButton(getResources().getString(R.string.dialog_button_positive), (dialogInterface, i) -> {
                }).create();
        dialogAbout.show();
    }

}
