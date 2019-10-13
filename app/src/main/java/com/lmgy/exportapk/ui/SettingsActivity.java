package com.lmgy.exportapk.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.PermissionChecker;

import com.lmgy.exportapk.Global;
import com.lmgy.exportapk.R;
import com.lmgy.exportapk.base.BaseActivity;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.widget.ExportRuleDialog;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener {

    private static final String ACTIVITY_RESULT = "result";
    private int resultCode = RESULT_CANCELED;
    private SharedPreferences settings;

    private static final int REQUEST_CODE_SET_PATH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = Global.getGlobalSharedPreferences(SettingsActivity.this);
        setContentView(R.layout.activity_settings);
        setSupportActionBar(findViewById(R.id.toolbar));
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        findViewById(R.id.settings_share_mode_area).setOnClickListener(this);
        findViewById(R.id.settings_night_mode_area).setOnClickListener(this);
        findViewById(R.id.settings_loading_options_area).setOnClickListener(this);
        findViewById(R.id.settings_rules_area).setOnClickListener(this);
        findViewById(R.id.settings_path_area).setOnClickListener(this);
        findViewById(R.id.settings_about_area).setOnClickListener(this);

        refreshSettingValues();

        if (savedInstanceState != null) {
            setResult(savedInstanceState.getInt(ACTIVITY_RESULT));
        }
    }

    @Override
    public void onClick(View view) {
        if (settings == null) {
            return;
        }
        final SharedPreferences.Editor editor = settings.edit();
        switch (view.getId()) {
            default:
                break;
            case R.id.settings_share_mode_area: {
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_share_mode, null);
                int mode = settings.getInt(Constant.PREFERENCE_SHAREMODE, Constant.PREFERENCE_SHAREMODE_DEFAULT);
                ((RadioButton) dialogView.findViewById(R.id.share_mode_direct_ra)).setChecked(mode == Constant.SHARE_MODE_DIRECT);
                ((RadioButton) dialogView.findViewById(R.id.share_mode_after_extract_ra)).setChecked(mode == Constant.SHARE_MODE_AFTER_EXTRACT);
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.activity_settings_share_mode))
                        .setView(dialogView)
                        .show();
                dialogView.findViewById(R.id.share_mode_direct).setOnClickListener(v -> {
                    dialog.cancel();
                    editor.putInt(Constant.PREFERENCE_SHAREMODE, Constant.SHARE_MODE_DIRECT);
                    editor.apply();
                    refreshSettingValues();
                });
                dialogView.findViewById(R.id.share_mode_after_extract).setOnClickListener(v -> {
                    dialog.cancel();
                    editor.putInt(Constant.PREFERENCE_SHAREMODE, Constant.SHARE_MODE_AFTER_EXTRACT);
                    editor.apply();
                    refreshSettingValues();
                });

            }
            break;
            case R.id.settings_night_mode_area: {
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_night_mode, null);
                int night_mode = settings.getInt(Constant.PREFERENCE_NIGHT_MODE, Constant.PREFERENCE_NIGHT_MODE_DEFAULT);
                ((RadioButton) dialogView.findViewById(R.id.night_mode_enabled_ra)).setChecked(night_mode == AppCompatDelegate.MODE_NIGHT_YES);
                ((RadioButton) dialogView.findViewById(R.id.night_mode_disabled_ra)).setChecked(night_mode == AppCompatDelegate.MODE_NIGHT_NO);
                ((RadioButton) dialogView.findViewById(R.id.night_mode_auto_ra)).setChecked(night_mode == AppCompatDelegate.MODE_NIGHT_AUTO);
                ((RadioButton) dialogView.findViewById(R.id.night_mode_follow_system_ra)).setChecked(night_mode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                final AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.activity_settings_night_mode))
                        .setView(dialogView)
                        .show();
                dialogView.findViewById(R.id.night_mode_enabled).setOnClickListener(v -> {
                    dialog.cancel();
                    editor.putInt(Constant.PREFERENCE_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_YES);
                    editor.apply();
                    refreshNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                });
                dialogView.findViewById(R.id.night_mode_disabled).setOnClickListener(v -> {
                    dialog.cancel();
                    editor.putInt(Constant.PREFERENCE_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_NO);
                    editor.apply();
                    refreshNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                });
                dialogView.findViewById(R.id.night_mode_auto).setOnClickListener(v -> {
                    dialog.cancel();
                    editor.putInt(Constant.PREFERENCE_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_AUTO);
                    editor.apply();
                    refreshNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                });
                dialogView.findViewById(R.id.night_mode_follow_system).setOnClickListener(v -> {
                    dialog.cancel();
                    editor.putInt(Constant.PREFERENCE_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    editor.apply();
                    refreshNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                });
            }
            break;
            case R.id.settings_loading_options_area: {
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_loading_selection, null);
                final CheckBox cb_permissions = dialogView.findViewById(R.id.loading_permissions);
                final CheckBox cb_activities = dialogView.findViewById(R.id.loading_activities);
                final CheckBox cb_receivers = dialogView.findViewById(R.id.loading_receivers);
                final CheckBox cb_static_loaders = dialogView.findViewById(R.id.loading_static_loaders);
                cb_permissions.setChecked(settings.getBoolean(Constant.PREFERENCE_LOAD_PERMISSIONS, Constant.PREFERENCE_LOAD_PERMISSIONS_DEFAULT));
                cb_activities.setChecked(settings.getBoolean(Constant.PREFERENCE_LOAD_ACTIVITIES, Constant.PREFERENCE_LOAD_ACTIVITIES_DEFAULT));
                cb_receivers.setChecked(settings.getBoolean(Constant.PREFERENCE_LOAD_RECEIVERS, Constant.PREFERENCE_LOAD_RECEIVERS_DEFAULT));
                cb_static_loaders.setChecked(settings.getBoolean(Constant.PREFERENCE_LOAD_STATIC_LOADERS, Constant.PREFERENCE_LOAD_STATIC_LOADERS_DEFAULT));
                new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.activity_settings_loading_options))
                        .setView(dialogView)
                        .setPositiveButton(getResources().getString(R.string.dialog_button_confirm), (dialog, which) -> {
                            editor.putBoolean(Constant.PREFERENCE_LOAD_PERMISSIONS, cb_permissions.isChecked());
                            editor.putBoolean(Constant.PREFERENCE_LOAD_ACTIVITIES, cb_activities.isChecked());
                            editor.putBoolean(Constant.PREFERENCE_LOAD_RECEIVERS, cb_receivers.isChecked());
                            editor.putBoolean(Constant.PREFERENCE_LOAD_STATIC_LOADERS, cb_static_loaders.isChecked());
                            editor.apply();
                            refreshSettingValues();
                            setResult(RESULT_OK);
                        })
                        .setNegativeButton(getResources().getString(R.string.dialog_button_cancel), (dialog, which) -> {
                        })
                        .show();

            }
            break;
            case R.id.settings_rules_area: {
                new ExportRuleDialog(this).show();
            }
            break;
            case R.id.settings_path_area: {
                if (Build.VERSION.SDK_INT >= 23 && PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    Global.showRequestingWritePermissionSnackBar(this);
                    return;
                }
                startActivityForResult(new Intent(this, FolderSelectorActivity.class), REQUEST_CODE_SET_PATH);
            }
            break;
            case R.id.settings_about_area: {
                View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_about, null);
                dialogView.findViewById(R.id.layout_about_donate).setOnClickListener(v -> {
                    // TODO Auto-generated method stub
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://qr.alipay.com/FKX08041Y09ZGT6ZT91FA5")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
                new AlertDialog.Builder(this)
                        .setTitle(this.getResources().getString(R.string.dialog_about_title))
                        .setIcon(R.mipmap.ic_launcher)
                        .setCancelable(true)
                        .setView(dialogView)
                        .setPositiveButton(getResources().getString(R.string.dialog_button_confirm), (arg0, arg1) -> {
                        }).show();

            }
            break;
        }
    }


    private void refreshNightMode(int value) {
        resultCode = RESULT_OK;
        AppCompatDelegate.setDefaultNightMode(value);
        recreate();
    }

    private void refreshSettingValues() {
        if (settings == null) {
            return;
        }
        ((TextView) findViewById(R.id.settings_path_value)).setText(settings.getString(Constant.PREFERENCE_SAVE_PATH, Constant.PREFERENCE_SAVE_PATH_DEFAULT));
        ((TextView) findViewById(R.id.settings_share_mode_value)).setText(
                getResources().getString(
                        settings.getInt(Constant.PREFERENCE_SHAREMODE, Constant.PREFERENCE_SHAREMODE_DEFAULT) == Constant.SHARE_MODE_DIRECT ?
                                R.string.share_mode_direct : R.string.share_mode_export));
        String night_mode_value = "";
        switch (settings.getInt(Constant.PREFERENCE_NIGHT_MODE, Constant.PREFERENCE_NIGHT_MODE_DEFAULT)) {
            default:
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                night_mode_value = getResources().getString(R.string.night_mode_enabled);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                night_mode_value = getResources().getString(R.string.night_mode_disabled);
                break;
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                night_mode_value = getResources().getString(R.string.night_mode_auto);
                break;
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                night_mode_value = getResources().getString(R.string.night_mode_follow_system);
                break;
        }
        ((TextView) findViewById(R.id.settings_night_mode_value)).setText(night_mode_value);
        String read_options = "";
        if (settings.getBoolean(Constant.PREFERENCE_LOAD_PERMISSIONS, Constant.PREFERENCE_LOAD_PERMISSIONS_DEFAULT)) {
            read_options += getResources().getString(R.string.activity_detail_permissions);
        }
        if (settings.getBoolean(Constant.PREFERENCE_LOAD_ACTIVITIES, Constant.PREFERENCE_LOAD_ACTIVITIES_DEFAULT)) {
            if (!"".equals(read_options)) {
                read_options += ",";
            }
            read_options += getResources().getString(R.string.activity_detail_activities);
        }
        if (settings.getBoolean(Constant.PREFERENCE_LOAD_RECEIVERS, Constant.PREFERENCE_LOAD_RECEIVERS_DEFAULT)) {
            if (!"".equals(read_options)) {
                read_options += ",";
            }
            read_options += getResources().getString(R.string.activity_detail_receivers);
        }
        if (settings.getBoolean(Constant.PREFERENCE_LOAD_STATIC_LOADERS, Constant.PREFERENCE_LOAD_STATIC_LOADERS_DEFAULT)) {
            if (!"".equals(read_options)) {
                read_options += ",";
            }
            read_options += getResources().getString(R.string.activity_detail_static_loaders);
        }
        if ("".equals(read_options.trim())) {
            read_options = getResources().getString(R.string.word_blank);
        }
        ((TextView) findViewById(R.id.settings_loading_options_value)).setText(read_options);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ACTIVITY_RESULT, resultCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SET_PATH && resultCode == RESULT_OK) {
            refreshSettingValues();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
