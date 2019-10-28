package com.lmgy.exportapk.mvp.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.AppListAdapter;
import com.lmgy.exportapk.base.BaseMvpActivity;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.event.ProgressEvent;
import com.lmgy.exportapk.listener.DialogClick;
import com.lmgy.exportapk.listener.ListenerNormalMode;
import com.lmgy.exportapk.mvp.contract.MainContract;
import com.lmgy.exportapk.mvp.presenter.MainPresenter;
import com.lmgy.exportapk.utils.ToastUtils;
import com.lmgy.exportapk.widget.LoadListDialog;
import com.lmgy.exportapk.widget.SortDialog;
import com.wyt.searchbox.SearchFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;


/**
 * @author lmgy
 * @date 2019/10/13
 */
public class MainActivity extends BaseMvpActivity<MainPresenter> implements MainContract.View,
        View.OnClickListener,
        AppListAdapter.OnItemClickListener,
        AppListAdapter.OnLongClickListener {

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
    @BindView(R.id.text_selectall)
    TextView mSelectAll;
    @BindView(R.id.text_deselectall)
    TextView mDeselectAll;
    @BindView(R.id.text_extract)
    TextView mExtract;
    @BindView(R.id.text_share)
    TextView mShare;
    @BindView(R.id.choice_app_view)
    View multiSelectArea;

    private boolean showSystemApp;
    private boolean isMultiSelectMode;
    private boolean isSearchMode;
    private AppListAdapter mAdapter;
    private Menu menu;
    private String keyword;
    private LoadListDialog mDialogLoadList;
    private SortDialog mDialogSort;
    private List<AppItemBean> appItemBeanList = new ArrayList<>();
    private Context mContext;
    private Animation mAnimEntry;
    private Animation mAnimExit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        XXPermissions.with(this)
                .constantRequest()
                .permission(Permission.Group.STORAGE)
                .request(new OnPermission() {
                    @Override
                    public void hasPermission(List<String> granted, boolean isAll) {
                        mDialogLoadList = new LoadListDialog(mContext);
                        mDialogLoadList.setTitle(getResources().getString(R.string.activity_main_loading));
                        mDialogLoadList.setCancelable(false);
                        mDialogLoadList.setCanceledOnTouchOutside(false);
                        mDialogLoadList.setMax(getPackageManager().getInstalledPackages(PackageManager.COMPONENT_ENABLED_STATE_DEFAULT).size());
                        refreshList();
                    }

                    @Override
                    public void noPermission(List<String> denied, boolean quick) {
                        if (quick) {
                            ToastUtils.show("被永久拒绝授权，请手动授予权限");
                            XXPermissions.gotoPermissionSettings(MainActivity.this);
                        } else {
                            ToastUtils.show("获取权限失败");
                        }
                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onProgressEvent(ProgressEvent event) {
        mDialogLoadList.setProgress(event.getProgress());
    }

    @Override
    public void initView() {
        mPresenter = new MainPresenter();
        mPresenter.attachView(this);
        mAnimEntry = AnimationUtils.loadAnimation(this, R.anim.anim_multiselectarea_entry);
        mAnimExit = AnimationUtils.loadAnimation(this, R.anim.anim_multiselectarea_exit);
        multiSelectArea.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        setSupportActionBar(mToolbar);
        mFab.setOnClickListener(this);
    }

    private void refreshList() {
        mRecyclerView.setAdapter(null);
        if (mDialogLoadList != null) {
            mDialogLoadList.show();
        }
        mPresenter.loadAppList(mContext, showSystemApp);
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
            mCardView.startAnimation(mAnimEntry);
            mCardView.setVisibility(View.VISIBLE);
        }
        SearchFragment searchFragment = SearchFragment.newInstance();
        searchFragment.showFragment(getSupportFragmentManager(), SearchFragment.TAG);
        searchFragment.setOnSearchClickListener(keyword -> {
            Toast.makeText(getBaseContext(), keyword, Toast.LENGTH_SHORT).show();
            this.keyword = keyword;
            showSearchView();
        });
        mCardView.startAnimation(mAnimExit);
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
                AppItemBean.SortConfig = position;
                if (mAdapter != null && !isSearchMode) {
                    if (isMultiSelectMode) {
                        closeMultiSelectMode();
                    }
                    Collections.sort(appItemBeanList);
                    setAdapter(appItemBeanList);
                }
                mDialogSort.cancel();
            }
        };
        mDialogSort = new SortDialog(this, click);
        mDialogSort.show();
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
            case R.id.action_showsysapp:
                if (isMultiSelectMode) {
                    closeMultiSelectMode();
                }
                showSystemApp = !showSystemApp;
                if (showSystemApp) {
                    menu.getItem(2).setTitle(getString(R.string.text_hidesysapp));
                } else {
                    menu.getItem(2).setTitle(getString(R.string.text_showsysapp));
                }
                refreshList();
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
                for (int i = 0; i < mAdapter.getAppList().size(); i++) {
                    if (mAdapter.getIsSelected()[i]) {
                        mPresenter.extractApp(mContext, appItemBeanList, new Integer[]{i, 1, 0});
                    }
                }
                break;
            case R.id.text_share:
                for (int i = 0; i < mAdapter.getAppList().size(); i++) {
                    if (mAdapter.getIsSelected()[i]) {
                        mPresenter.clickShare(mContext, appItemBeanList.get(i));
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

    private void showSearchView() {
        updateSearchList(keyword);
        isSearchMode = true;
        mAdapter.setLongClickListener(null);
        setMenuVisible(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void updateSearchList(String text) {
        mProgressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setAdapter(mAdapter);
        mPresenter.updateSearchList(mContext, text, appItemBeanList);
    }

    private void setMenuVisible(boolean isVisible) {
        if (menu != null) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setEnabled(isVisible);
                menu.getItem(i).setVisible(isVisible);
            }
        }
    }

    private void closeSearchView() {
        isSearchMode = false;
        refreshList();
        setAdapter(appItemBeanList);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        mCardView.startAnimation(mAnimEntry);
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
        mAdapter.setLongClickListener(this);
        multiSelectArea.startAnimation(mAnimExit);
        multiSelectArea.setVisibility(View.GONE);
        if (!isSearchMode) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    public void onItemClick(int position) {
        updateMultiSelectMode(position);
    }

    @Override
    public boolean onLongClick(int position) {
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
        mAdapter.setItemClickListener(this);
        mAdapter.setLongClickListener(this);
        multiSelectArea.startAnimation(mAnimEntry);
        multiSelectArea.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void setAdapter(List<AppItemBean> appItemBeans) {
        appItemBeanList = appItemBeans;
        mAdapter = new AppListAdapter(mContext, appItemBeans);
        mRecyclerView.setAdapter(mAdapter);
        mDialogLoadList.cancel();
        mAdapter.setItemClickListener(new ListenerNormalMode(mContext, mAdapter));
        mAdapter.setLongClickListener(this);
        mProgressBar.setVisibility(View.GONE);
    }
}