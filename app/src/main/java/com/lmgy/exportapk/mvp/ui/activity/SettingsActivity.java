package com.lmgy.exportapk.mvp.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.SettingsListAdapter;
import com.lmgy.exportapk.base.BaseMvpActivity;
import com.lmgy.exportapk.bean.SettingsBean;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.mvp.contract.SettingsContract;
import com.lmgy.exportapk.mvp.presenter.SettingsPresenter;
import com.lmgy.exportapk.utils.SpUtils;
import com.lmgy.exportapk.widget.ExportRuleDialog;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * @author lmgy
 * @date 2019/10/18
 */
public class SettingsActivity extends BaseMvpActivity<SettingsPresenter> implements SettingsContract.View,
        AdapterView.OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.listView)
    ListView mListView;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public void initView() {
        mPresenter = new SettingsPresenter();
        mPresenter.attachView(this);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("设置");
        mPresenter.loadSettingList();
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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            default:
                break;
            case 0:
                startActivity(new Intent(this, FolderSelectActivity.class));
                break;
            case 1:
                clickExportRule();
                break;
            case 2:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }


    @SuppressLint("InflateParams")
    private void clickExportRule() {
        ExportRuleDialog exportRuleDialog = new ExportRuleDialog(this, R.style.BottomSheetDialog);
        exportRuleDialog.setClickListener((editApk, editZip) -> {
            if ("".equals(editApk.trim()) || "".equals(editZip.trim())) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.dialog_filename_toast_blank), Toast.LENGTH_SHORT).show();
                return;
            }

            String apkReplacedVariables = editApk.replace(Constant.FONT_APP_NAME, "").replace(Constant.FONT_APP_PACKAGE_NAME, "").replace(Constant.FONT_APP_VERSIONCODE, "").replace(Constant.FONT_APP_VERSIONNAME, "");
            String zipReplacedVariables = editZip.replace(Constant.FONT_APP_NAME, "").replace(Constant.FONT_APP_PACKAGE_NAME, "").replace(Constant.FONT_APP_VERSIONCODE, "").replace(Constant.FONT_APP_VERSIONNAME, "");
            if (apkReplacedVariables.contains("?") || apkReplacedVariables.contains("\\") || apkReplacedVariables.contains("/") || apkReplacedVariables.contains(":") || apkReplacedVariables.contains("*") || apkReplacedVariables.contains("\"")
                    || apkReplacedVariables.contains("<") || apkReplacedVariables.contains(">") || apkReplacedVariables.contains("|")
                    || zipReplacedVariables.contains("?") || zipReplacedVariables.contains("\\") || zipReplacedVariables.contains("/") || zipReplacedVariables.contains(":") || zipReplacedVariables.contains("*") || zipReplacedVariables.contains("\"")
                    || zipReplacedVariables.contains("<") || zipReplacedVariables.contains(">") || zipReplacedVariables.contains("|")) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.activity_folder_selector_invalid_foldername), Toast.LENGTH_SHORT).show();
            }
            SpUtils.setFontApk(editApk);
            SpUtils.setFontZip(editZip);
            exportRuleDialog.dismiss();
        });
        exportRuleDialog.show();
    }

    @Override
    public void setAdapter(List<SettingsBean> settingsBeanList) {
        SettingsListAdapter adapter = new SettingsListAdapter(this, R.layout.layout_settings_card, settingsBeanList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
    }
}