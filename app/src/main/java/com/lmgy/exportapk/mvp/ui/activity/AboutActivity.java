package com.lmgy.exportapk.mvp.ui.activity;

import android.graphics.Color;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.AboutListAdapter;
import com.lmgy.exportapk.base.BaseMvpActivity;
import com.lmgy.exportapk.bean.AboutBean;
import com.lmgy.exportapk.mvp.contract.AboutContract;
import com.lmgy.exportapk.mvp.presenter.AboutPresenter;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * @author lmgy
 * @date 2019/10/18
 */
public class AboutActivity extends BaseMvpActivity<AboutPresenter> implements AboutContract.View {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        mPresenter = new AboutPresenter();
        mPresenter.attachView(this);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mToolbarLayout.setTitle("关于");
        mToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        mPresenter.loadAboutList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setAdapter(List<AboutBean> aboutBeanList) {
        AboutListAdapter adapter = new AboutListAdapter(this, aboutBeanList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }
}
