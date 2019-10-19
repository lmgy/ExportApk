package com.lmgy.exportapk.ui.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.SettingsListAdapter;
import com.lmgy.exportapk.base.BaseActivity;
import com.lmgy.exportapk.bean.SettingsBean;
import com.lmgy.exportapk.config.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;

/**
 * @author lmgy
 * @date 2019/10/18
 */
public class SettingsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.listView)
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public void initView() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mToolbar.setTitle("设置");
        initData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initData() {
        List<SettingsBean> settingsBeanList = new ArrayList<>();
        settingsBeanList.add(new SettingsBean("导出路径", R.drawable.ic_settings_export));
        settingsBeanList.add(new SettingsBean("导出规则", R.drawable.ic_settings_rule));
        settingsBeanList.add(new SettingsBean("分享模式", R.drawable.ic_settings_share));
        settingsBeanList.add(new SettingsBean("关于", R.drawable.ic_settings_about));
        SettingsListAdapter adapter = new SettingsListAdapter(this, R.layout.layout_settings_card, settingsBeanList);
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(this);
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
                clickExportMode();
                break;
            case 3:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }

    @SuppressLint("InflateParams")
    private void clickExportMode(){
        int mode = Constant.SHARE_MODE_DIRECT;
//        int mode = settings.getInt(Constant.PREFERENCE_SHAREMODE, Constant.PREFERENCE_SHAREMODE_DEFAULT);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_sharemode, null, false);
        RadioButton raDirect = dialogView.findViewById(R.id.share_mode_direct_ra);
        RadioButton raAfterExtract = dialogView.findViewById(R.id.share_mode_after_extract_ra);
        raDirect.setChecked(mode == Constant.SHARE_MODE_DIRECT);
        raAfterExtract.setChecked(mode == Constant.SHARE_MODE_AFTER_EXTRACT);
        final AlertDialog shareDialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.action_sharemode))
                .setView(dialogView)
                .show();
        shareDialog.findViewById(R.id.share_mode_direct).setOnClickListener(v -> {
//            editor.putInt(Constant.PREFERENCE_SHAREMODE, Constant.SHARE_MODE_DIRECT);
//            editor.apply();
            shareDialog.cancel();
        });
        dialogView.findViewById(R.id.share_mode_after_extract).setOnClickListener(v -> {
//            editor.putInt(Constant.PREFERENCE_SHAREMODE, Constant.SHARE_MODE_AFTER_EXTRACT);
//            editor.apply();
            shareDialog.cancel();
        });
    }

    private String getFormatExportFileName(String apk, String zip) {
        String PREVIEW_APP_NAME = getResources().getString(R.string.dialog_filename_preview_appname);
        String PREVIEW_PACKAGE_NAME = getResources().getString(R.string.dialog_filename_preview_packagename);
        String PREVIEW_VERSION = getResources().getString(R.string.dialog_filename_preview_version);
        String PREVIEW_VERSIONCODE = getResources().getString(R.string.dialog_filename_preview_versioncode);
        return getResources().getString(R.string.preview) + ":\n\nAPK:  " + apk.replace(Constant.FONT_APP_NAME, PREVIEW_APP_NAME)
                .replace(Constant.FONT_APP_PACKAGE_NAME, PREVIEW_PACKAGE_NAME).replace(Constant.FONT_APP_VERSIONCODE, PREVIEW_VERSIONCODE).replace(Constant.FONT_APP_VERSIONNAME, PREVIEW_VERSION) + ".apk\n\n"
                + "ZIP:  " + zip.replace(Constant.FONT_APP_NAME, PREVIEW_PACKAGE_NAME)
                .replace(Constant.FONT_APP_PACKAGE_NAME, PREVIEW_PACKAGE_NAME).replace(Constant.FONT_APP_VERSIONCODE, PREVIEW_VERSIONCODE).replace(Constant.FONT_APP_VERSIONNAME, PREVIEW_VERSION) + ".zip";
    }

    @SuppressLint("InflateParams")
    private void clickExportRule(){
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_filename, null);
        EditText editApk = dialogView.findViewById(R.id.filename_apk);
        EditText editZip = dialogView.findViewById(R.id.filename_zip);
        TextView preview = dialogView.findViewById(R.id.filename_preview);

//        editApk.setText(settings.getString(Constant.PREFERENCE_FILENAME_FONT_APK, Constant.PREFERENCE_FILENAME_FONT_DEFAULT));
//        editZip.setText(settings.getString(Constant.PREFERENCE_FILENAME_FONT_ZIP, Constant.PREFERENCE_FILENAME_FONT_DEFAULT));
        preview.setText(getFormatExportFileName(editApk.getText().toString(), editZip.getText().toString()));

        if (!editApk.getText().toString().contains(Constant.FONT_APP_NAME) && !editApk.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
            dialogView.findViewById(R.id.filename_apk_warn).setVisibility(View.VISIBLE);
        } else {
            dialogView.findViewById(R.id.filename_apk_warn).setVisibility(View.GONE);
        }

        if (!editZip.getText().toString().contains(Constant.FONT_APP_NAME) && !editZip.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
            dialogView.findViewById(R.id.filename_zip_warn).setVisibility(View.VISIBLE);
        } else {
            dialogView.findViewById(R.id.filename_zip_warn).setVisibility(View.GONE);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.dialog_filename_title))
                .setView(dialogView)
                .setPositiveButton(getResources().getString(R.string.dialog_button_positive), null)
                .setNegativeButton(getResources().getString(R.string.dialog_button_negative), (dialog1, which) -> {

                })
                .show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if ("".equals(editApk.getText().toString().trim()) || "".equals(editZip.getText().toString().trim())) {
                Toast.makeText(this, getResources().getString(R.string.dialog_filename_toast_blank), Toast.LENGTH_SHORT).show();
                return;
            }

            String apk_replaced_variables = editApk.getText().toString().replace(Constant.FONT_APP_NAME, "").replace(Constant.FONT_APP_PACKAGE_NAME, "").replace(Constant.FONT_APP_VERSIONCODE, "").replace(Constant.FONT_APP_VERSIONNAME, "");
            String zip_replaced_variables = editZip.getText().toString().replace(Constant.FONT_APP_NAME, "").replace(Constant.FONT_APP_PACKAGE_NAME, "").replace(Constant.FONT_APP_VERSIONCODE, "").replace(Constant.FONT_APP_VERSIONNAME, "");
            if (apk_replaced_variables.contains("?") || apk_replaced_variables.contains("\\") || apk_replaced_variables.contains("/") || apk_replaced_variables.contains(":") || apk_replaced_variables.contains("*") || apk_replaced_variables.contains("\"")
                    || apk_replaced_variables.contains("<") || apk_replaced_variables.contains(">") || apk_replaced_variables.contains("|")
                    || zip_replaced_variables.contains("?") || zip_replaced_variables.contains("\\") || zip_replaced_variables.contains("/") || zip_replaced_variables.contains(":") || zip_replaced_variables.contains("*") || zip_replaced_variables.contains("\"")
                    || zip_replaced_variables.contains("<") || zip_replaced_variables.contains(">") || zip_replaced_variables.contains("|")) {
                Toast.makeText(this, getResources().getString(R.string.activity_folder_selector_invalid_foldername), Toast.LENGTH_SHORT).show();
            }
//            editor.putString(Constant.PREFERENCE_FILENAME_FONT_APK, editApk.getText().toString());
//            editor.putString(Constant.PREFERENCE_FILENAME_FONT_ZIP, editZip.getText().toString());
//            int zipSelection = spinner.getSelectedItemPosition();
//            switch (zipSelection) {
//                default:
//                    break;
//                case 0:
//                    editor.putInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.PREFERENCE_ZIP_COMPRESS_LEVEL_DEFAULT);
//                    break;
//                case 1:
//                    editor.putInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.ZIP_LEVEL_STORED);
//                    break;
//                case 2:
//                    editor.putInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.ZIP_LEVEL_LOW);
//                    break;
//                case 3:
//                    editor.putInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.ZIP_LEVEL_NORMAL);
//                    break;
//                case 4:
//                    editor.putInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.ZIP_LEVEL_HIGH);
//                    break;
//            }
//            editor.apply();
//            dialog.cancel();
        });
        editApk.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                preview.setText(getFormatExportFileName(editApk.getText().toString(), editZip.getText().toString()));
                if (!editApk.getText().toString().contains(Constant.FONT_APP_NAME) && !editApk.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                        && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
                    dialogView.findViewById(R.id.filename_apk_warn).setVisibility(View.VISIBLE);
                } else {
                    dialogView.findViewById(R.id.filename_apk_warn).setVisibility(View.GONE);
                }
            }

        });
        editZip.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                preview.setText(getFormatExportFileName(editApk.getText().toString(), editZip.getText().toString()));
                if (!editZip.getText().toString().contains(Constant.FONT_APP_NAME) && !editZip.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                        && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
                    dialogView.findViewById(R.id.filename_zip_warn).setVisibility(View.VISIBLE);
                } else {
                    dialogView.findViewById(R.id.filename_zip_warn).setVisibility(View.GONE);
                }
            }

        });

        dialogView.findViewById(R.id.filename_appname).setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_NAME);
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_NAME);
            }
        });

        dialogView.findViewById(R.id.filename_packagename).setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_PACKAGE_NAME);
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_PACKAGE_NAME);
            }
        });

        dialogView.findViewById(R.id.filename_version).setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_VERSIONNAME);
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_VERSIONNAME);
            }
        });

        dialogView.findViewById(R.id.filename_versioncode).setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_VERSIONCODE);
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_VERSIONCODE);
            }
        });

        dialogView.findViewById(R.id.filename_connector).setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), "-");
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), "-");
            }
        });

        dialogView.findViewById(R.id.filename_upderline).setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), "_");
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), "_");
            }
        });
    }
}
