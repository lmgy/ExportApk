package com.lmgy.exportapk.ui;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.format.Formatter;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import com.google.android.material.snackbar.Snackbar;
import com.lmgy.exportapk.Global;
import com.lmgy.exportapk.R;
import com.lmgy.exportapk.base.BaseActivity;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.utils.EnvironmentUtils;
import com.lmgy.exportapk.utils.FileUtils;
import com.lmgy.exportapk.utils.Storage;
import com.lmgy.exportapk.widget.ToastManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class AppDetailActivity extends BaseActivity implements View.OnClickListener {

    private AppItemBean appItem;
    private CheckBox cbData;
    private CheckBox cbObb;
    private ViewGroup permissionViews;
    private ViewGroup activityViews;
    private ViewGroup receiverViews;
    private ViewGroup staticLoaderViews;
    private int itemPermission = 0;
    private int itemActivity = 0;
    private int itemReceiver = 0;
    private int itemLoader = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            appItem = Global.getAppItemByPackageNameFromList(Global.list, getIntent().getStringExtra(EXTRA_PACKAGE_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (appItem == null) {
            ToastManager.showToast(this, "(-_-)The AppItem info is null, try to restart this application.", Toast.LENGTH_SHORT);
            finish();
            return;
        }

        setContentView(R.layout.activity_app_detail);


        final SharedPreferences settings = Global.getGlobalSharedPreferences(this);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(appItem.getAppName());

        cbData = findViewById(R.id.app_detail_export_data);
        cbObb = findViewById(R.id.app_detail_export_obb);
        permissionViews = findViewById(R.id.app_detail_permission);
        activityViews = findViewById(R.id.app_detail_activity);
        receiverViews = findViewById(R.id.app_detail_receiver);
        staticLoaderViews = findViewById(R.id.app_detail_static_loader);

        PackageInfo packageInfo = appItem.getPackageInfo();

        ((TextView) findViewById(R.id.app_detail_name)).setText(appItem.getAppName());
        ((TextView) findViewById(R.id.app_detail_version_name_title)).setText(appItem.getVersionName());
        ((ImageView) findViewById(R.id.app_detail_icon)).setImageDrawable(appItem.getIcon(this));

        ((TextView) findViewById(R.id.app_detail_package_name)).setText(appItem.getPackageName());
        ((TextView) findViewById(R.id.app_detail_version_name)).setText(appItem.getVersionName());
        ((TextView) findViewById(R.id.app_detail_version_code)).setText(String.valueOf(appItem.getVersionCode()));
        ((TextView) findViewById(R.id.app_detail_size)).setText(Formatter.formatFileSize(this, appItem.getSize()));
        ((TextView) findViewById(R.id.app_detail_install_time)).setText(EnvironmentUtils.getFormatDateAndTime(packageInfo.firstInstallTime));
        ((TextView) findViewById(R.id.app_detail_update_time)).setText(EnvironmentUtils.getFormatDateAndTime(packageInfo.lastUpdateTime));
        ((TextView) findViewById(R.id.app_detail_minimum_api)).setText(Build.VERSION.SDK_INT >= 24 ? String.valueOf(packageInfo.applicationInfo.minSdkVersion) : getResources().getString(R.string.word_unknown));
        ((TextView) findViewById(R.id.app_detail_target_api)).setText(String.valueOf(packageInfo.applicationInfo.targetSdkVersion));
        ((TextView) findViewById(R.id.app_detail_is_system_app)).setText(getResources().getString((appItem.getPackageInfo().applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0 ? R.string.word_yes : R.string.word_no));
        ((TextView) findViewById(R.id.app_detail_signature)).setText(EnvironmentUtils.getSignatureStringOfPackageInfo(packageInfo));

        findViewById(R.id.app_detail_run_area).setOnClickListener(this);
        findViewById(R.id.app_detail_export_area).setOnClickListener(this);
        findViewById(R.id.app_detail_share_area).setOnClickListener(this);
        findViewById(R.id.app_detail_detail_area).setOnClickListener(this);
        findViewById(R.id.app_detail_market_area).setOnClickListener(this);
        findViewById(R.id.app_detail_delete_area).setOnClickListener(this);

        findViewById(R.id.app_detail_permission_area).setOnClickListener(this);
        findViewById(R.id.app_detail_activity_area).setOnClickListener(this);
        findViewById(R.id.app_detail_receiver_area).setOnClickListener(this);
        findViewById(R.id.app_detail_static_loader_area).setOnClickListener(this);

        new Thread(() -> {
            final long data = FileUtils.INSTANCE.getFileOrFolderSize(new File(Storage.INSTANCE.getMainExternalStoragePath() + "/android/data/" + appItem.getPackageName()));
            final long obb = FileUtils.INSTANCE.getFileOrFolderSize(new File(Storage.INSTANCE.getMainExternalStoragePath() + "/android/obb/" + appItem.getPackageName()));
            Global.HANDLER.post((Runnable) () -> {
                findViewById(R.id.app_detail_export_progress_bar).setVisibility(View.GONE);
                cbData.setText("Data:" + Formatter.formatFileSize(AppDetailActivity.this, data));
                cbObb.setText("Obb:" + Formatter.formatFileSize(AppDetailActivity.this, obb));
                cbData.setEnabled(data > 0);
                cbObb.setEnabled(obb > 0);
                findViewById(R.id.app_detail_export_checkboxes).setVisibility(View.VISIBLE);
            });
        }).start();


        final String[] permissions = packageInfo.requestedPermissions;
        final ActivityInfo[] activities = packageInfo.activities;
        final ActivityInfo[] receivers = packageInfo.receivers;

        final boolean get_permissions = settings.getBoolean(Constant.INSTANCE.getPREFERENCE_LOAD_PERMISSIONS(), Constant.INSTANCE.getPREFERENCE_LOAD_PERMISSIONS_DEFAULT());
        final boolean get_activities = settings.getBoolean(Constant.INSTANCE.getPREFERENCE_LOAD_ACTIVITIES(), Constant.INSTANCE.getPREFERENCE_LOAD_ACTIVITIES_DEFAULT());
        final boolean get_receivers = settings.getBoolean(Constant.INSTANCE.getPREFERENCE_LOAD_RECEIVERS(), Constant.INSTANCE.getPREFERENCE_LOAD_RECEIVERS_DEFAULT());
        final boolean get_static_loaders = settings.getBoolean(Constant.INSTANCE.getPREFERENCE_LOAD_STATIC_LOADERS(), Constant.INSTANCE.getPREFERENCE_LOAD_STATIC_LOADERS_DEFAULT());

        new Thread(() -> {
            final ArrayList<View> permission_child_views = new ArrayList<>();
            final ArrayList<View> activity_child_views = new ArrayList<>();
            final ArrayList<View> receiver_child_views = new ArrayList<>();
            final ArrayList<View> loaders_child_views = new ArrayList<>();

            if (permissions != null && get_permissions) {
                for (final String s : permissions) {
                    if (s == null) continue;
                    permission_child_views.add(getSingleItemView(permissionViews, s, v -> clip2ClipboardAndShowSnackbar(s), null));
                }
            }
            if (activities != null && get_activities) {
                for (final ActivityInfo info : activities) {
                    activity_child_views.add(getSingleItemView(activityViews, info.name, v -> clip2ClipboardAndShowSnackbar(info.name), new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            try {
                                Intent intent = new Intent();
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setClassName(info.packageName, info.name);
                                startActivity(intent);
                            } catch (Exception e) {
                                ToastManager.showToast(AppDetailActivity.this, e.toString(), Toast.LENGTH_SHORT);
                            }
                            return true;
                        }
                    }));
                }
            }
            if (receivers != null && get_receivers) {
                for (final ActivityInfo activityInfo : receivers) {
                    receiver_child_views.add(getSingleItemView(receiverViews, activityInfo.name, v -> clip2ClipboardAndShowSnackbar(activityInfo.name), null));
                }
            }

            Bundle bundle = appItem.getStaticReceiversBundle();
            final Set<String> keys = bundle.keySet();
            if (get_static_loaders) {
                for (final String s : keys) {
                    View static_loader_item_view = LayoutInflater.from(AppDetailActivity.this).inflate(R.layout.item_static_loader, staticLoaderViews, false);
                    ((TextView) static_loader_item_view.findViewById(R.id.static_loader_name)).setText(s);
                    static_loader_item_view.setOnClickListener(v -> clip2ClipboardAndShowSnackbar(s));
                    ViewGroup filter_views = static_loader_item_view.findViewById(R.id.static_loader_intents);
                    List<String> filters = bundle.getStringArrayList(s);
                    if (filters == null) continue;
                    for (final String filter : filters) {
                        View itemView = LayoutInflater.from(AppDetailActivity.this).inflate(R.layout.item_single_textview, filter_views, false);
                        ((TextView) itemView.findViewById(R.id.item_textview)).setText(filter);
                        itemView.setOnClickListener(v -> clip2ClipboardAndShowSnackbar(filter));
                        filter_views.addView(itemView);
                    }
                    loaders_child_views.add(static_loader_item_view);
                }
            }

            Global.HANDLER.post(() -> {
                if (get_permissions) {
                    for (View view : permission_child_views) permissionViews.addView(view);
                    TextView att_permission = findViewById(R.id.app_detail_permission_area_att);
                    att_permission.setText(getResources().getString(R.string.activity_detail_permissions)
                            + "(" + permission_child_views.size() + getResources().getString(R.string.unit_item) + ")");
                }
                if (get_activities) {
                    for (View view : activity_child_views) activityViews.addView(view);
                    TextView att_activity = findViewById(R.id.app_detail_activity_area_att);
                    att_activity.setText(getResources().getString(R.string.activity_detail_activities)
                            + "(" + activity_child_views.size() + getResources().getString(R.string.unit_item) + ")");
                }
                if (get_receivers) {
                    for (View view : receiver_child_views) receiverViews.addView(view);
                    TextView att_receiver = findViewById(R.id.app_detail_receiver_area_att);
                    att_receiver.setText(getResources().getString(R.string.activity_detail_receivers) + "(" + receiver_child_views.size() + getResources().getString(R.string.unit_item) + ")");
                }
                if (get_static_loaders) {
                    for (View view : loaders_child_views) staticLoaderViews.addView(view);
                    TextView att_static_loader = findViewById(R.id.app_detail_static_loader_area_att);
                    att_static_loader.setText(getResources().getString(R.string.activity_detail_static_loaders) + "(" + keys.size() + getResources().getString(R.string.unit_item) + ")");
                }

                itemPermission = permission_child_views.size();
                itemActivity = activity_child_views.size();
                itemReceiver = receiver_child_views.size();
                itemLoader = loaders_child_views.size();

                findViewById(R.id.app_detail_card_pg).setVisibility(View.GONE);
                findViewById(R.id.app_detail_card_permissions).setVisibility(get_permissions ? View.VISIBLE : View.GONE);
                findViewById(R.id.app_detail_card_activities).setVisibility(get_activities ? View.VISIBLE : View.GONE);
                findViewById(R.id.app_detail_card_receivers).setVisibility(get_receivers ? View.VISIBLE : View.GONE);
                findViewById(R.id.app_detail_card_static_loaders).setVisibility(get_static_loaders ? View.VISIBLE : View.GONE);
            });

        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
            case R.id.app_detail_run_area: {
                try {
                    startActivity(getPackageManager().getLaunchIntentForPackage(appItem.getPackageName()));
                } catch (Exception e) {
                    ToastManager.showToast(AppDetailActivity.this, e.toString(), Toast.LENGTH_SHORT);
                }
            }
            break;
            case R.id.app_detail_export_area: {
                if (Build.VERSION.SDK_INT >= 23 && PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    Global.showRequestingWritePermissionSnackBar(this);
                    return;
                }
                final List<AppItemBean> single_list = getSingleItemArrayList(true);
                final AppItemBean item = single_list.get(0);
                Global.checkAndExportCertainAppItemsToSetPathWithoutShare(this, single_list, false, error_message -> {
                    if (!error_message.trim().equals("")) {
                        new AlertDialog.Builder(AppDetailActivity.this)
                                .setTitle(getResources().getString(R.string.exception_title))
                                .setMessage(getResources().getString(R.string.exception_message) + error_message)
                                .setPositiveButton(getResources().getString(R.string.dialog_button_confirm), (dialog, which) -> {
                                })
                                .show();
                        return;
                    }
                    ToastManager.showToast(AppDetailActivity.this, getResources().getString(R.string.toast_export_complete)
                            + Global.getAbsoluteWritePath(AppDetailActivity.this, single_list.get(0), (item.exportData || item.exportObb) ? "zip" : "apk"), Toast.LENGTH_SHORT);
                });
            }
            break;
            case R.id.app_detail_share_area: {
                if (Build.VERSION.SDK_INT >= 23 && PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                    Global.showRequestingWritePermissionSnackBar(this);
                    return;
                }
                Global.shareCertainAppsByItems(this, getSingleItemArrayList(false));
            }
            break;
            case R.id.app_detail_detail_area: {
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.fromParts("package", appItem.getPackageName(), null));
                startActivity(intent);
            }
            break;
            case R.id.app_detail_market_area: {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appItem.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    ToastManager.showToast(AppDetailActivity.this, e.toString(), Toast.LENGTH_SHORT);
                }
            }
            break;
            case R.id.app_detail_delete_area: {
                try {
                    Intent uninstall_intent = new Intent();
                    uninstall_intent.setAction(Intent.ACTION_DELETE);
                    uninstall_intent.setData(Uri.parse("package:" + appItem.getPackageName()));
                    startActivity(uninstall_intent);
                } catch (Exception e) {
                    ToastManager.showToast(AppDetailActivity.this, e.toString(), Toast.LENGTH_SHORT);
                }
            }
            break;
            case R.id.app_detail_permission_area: {
                if (permissionViews.getVisibility() == View.VISIBLE) {
                    findViewById(R.id.app_detail_permission_area_arrow).setRotation(0);
                    permissionViews.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(findViewById(android.R.id.content));
                } else {
                    findViewById(R.id.app_detail_permission_area_arrow).setRotation(90);
                    permissionViews.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(findViewById(android.R.id.content));
                }
            }
            break;
            case R.id.app_detail_activity_area: {
                if (activityViews.getVisibility() == View.VISIBLE) {
                    findViewById(R.id.app_detail_activity_area_arrow).setRotation(0);
                    activityViews.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(findViewById(android.R.id.content));
                } else {
                    findViewById(R.id.app_detail_activity_area_arrow).setRotation(90);
                    activityViews.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(findViewById(android.R.id.content));
                }
            }
            break;
            case R.id.app_detail_receiver_area: {
                if (receiverViews.getVisibility() == View.VISIBLE) {
                    findViewById(R.id.app_detail_receiver_area_arrow).setRotation(0);
                    receiverViews.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(findViewById(android.R.id.content));
                } else {
                    findViewById(R.id.app_detail_receiver_area_arrow).setRotation(90);
                    receiverViews.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(findViewById(android.R.id.content));
                }
            }
            break;
            case R.id.app_detail_static_loader_area: {
                if (staticLoaderViews.getVisibility() == View.VISIBLE) {
                    findViewById(R.id.app_detail_static_loader_area_arrow).setRotation(0);
                    staticLoaderViews.setVisibility(View.GONE);
                    TransitionManager.beginDelayedTransition(findViewById(android.R.id.content));
                } else {
                    findViewById(R.id.app_detail_static_loader_area_arrow).setRotation(90);
                    staticLoaderViews.setVisibility(View.VISIBLE);
                    TransitionManager.beginDelayedTransition(findViewById(android.R.id.content));
                }
            }
            break;
            case R.id.app_detail_package_name_area: {
                clip2ClipboardAndShowSnackbar(appItem.getPackageName());
            }
            break;
            case R.id.app_detail_version_name_area: {
                clip2ClipboardAndShowSnackbar(appItem.getVersionName());
            }
            break;
            case R.id.app_detail_version_code_area: {
                clip2ClipboardAndShowSnackbar(String.valueOf(appItem.getVersionCode()));
            }
            break;
            case R.id.app_detail_size_area: {
                clip2ClipboardAndShowSnackbar(Formatter.formatFileSize(this, appItem.getSize()));
            }
            break;
            case R.id.app_detail_install_time_area: {
                clip2ClipboardAndShowSnackbar(EnvironmentUtils.getFormatDateAndTime(appItem.getPackageInfo().firstInstallTime));
            }
            break;
            case R.id.app_detail_update_time_area: {
                clip2ClipboardAndShowSnackbar(EnvironmentUtils.getFormatDateAndTime(appItem.getPackageInfo().lastUpdateTime));
            }
            break;
            case R.id.app_detail_minimum_api_area: {
                if (Build.VERSION.SDK_INT >= 24) {
                    clip2ClipboardAndShowSnackbar(String.valueOf(appItem.getPackageInfo().applicationInfo.minSdkVersion));
                }
            }
            break;
            case R.id.app_detail_target_api_area: {
                clip2ClipboardAndShowSnackbar(String.valueOf(appItem.getPackageInfo().applicationInfo.targetSdkVersion));
            }
            break;
            case R.id.app_detail_is_system_app_area: {
                clip2ClipboardAndShowSnackbar(((TextView) findViewById(R.id.app_detail_is_system_app)).getText().toString());
            }
            break;
            case R.id.app_detail_signature_area: {
                clip2ClipboardAndShowSnackbar(((TextView) findViewById(R.id.app_detail_signature)).getText().toString());
            }
            break;
        }
    }


    private void clip2ClipboardAndShowSnackbar(String s) {
        try {
            ClipboardManager manager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            manager.setPrimaryClip(ClipData.newPlainText("message", s));
            Snackbar.make(findViewById(android.R.id.content), getResources().getString(R.string.snack_bar_clipboard), Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造包含单个副本AppItem的ArrayList
     */
    private @NonNull
    ArrayList<AppItemBean> getSingleItemArrayList(boolean putCheckboxValue) {
        ArrayList<AppItemBean> list = new ArrayList<>();
        AppItemBean item = new AppItemBean(appItem, false, false);
        if (putCheckboxValue) {
            item.exportData = cbData.isChecked();
            item.exportObb = cbObb.isChecked();
        }
        list.add(item);
        return list;
    }

    private View getSingleItemView(ViewGroup group, String text, View.OnClickListener clickListener, View.OnLongClickListener longClickListener) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_single_textview, group, false);
        ((TextView) view.findViewById(R.id.item_textview)).setText(text);
        view.setOnClickListener(clickListener);
        view.setOnLongClickListener(longClickListener);
        return view;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            checkHeightAndFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void checkHeightAndFinish() {
        int visible_items = (permissionViews.getVisibility() == View.VISIBLE ? itemPermission : 0) + (activityViews.getVisibility() == View.VISIBLE ? itemActivity : 0)
                + (receiverViews.getVisibility() == View.VISIBLE ? itemReceiver : 0) + (staticLoaderViews.getVisibility() == View.VISIBLE ? itemLoader : 0);

        //根布局项目太多时低版本Android会引发一个底层崩溃。版本号暂定28
        if (Build.VERSION.SDK_INT >= 28) {
            ActivityCompat.finishAfterTransition(this);
        } else {
            if (visible_items > 170) {
                finish();
            } else {
                ActivityCompat.finishAfterTransition(this);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            checkHeightAndFinish();
        }
        return super.onOptionsItemSelected(item);
    }
}
