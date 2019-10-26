package com.lmgy.exportapk.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.AboutListAdapter;
import com.lmgy.exportapk.base.BaseActivity;
import com.lmgy.exportapk.bean.AboutBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * @author lmgy
 * @date 2019/10/18
 */
public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mToolbarLayout.setTitle("关于");
        mToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        initData();
    }

    private void initData() {
        List<AboutBean> aboutBeanList = new ArrayList<>();
        aboutBeanList.add(new AboutBean("AndroidX", "https://source.google.com"));
        aboutBeanList.add(new AboutBean("Butterknife", "https://github.com/JakeWharton/butterknife"));
        aboutBeanList.add(new AboutBean("Rxjava", "https://github.com/ReactiveX/RxJava"));
        aboutBeanList.add(new AboutBean("SearchDialog", "https://github.com/wenwenwen888/SearchDialog"));
        AboutListAdapter adapter = new AboutListAdapter(this, aboutBeanList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
