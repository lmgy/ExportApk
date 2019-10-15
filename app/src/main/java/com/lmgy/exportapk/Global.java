package com.lmgy.exportapk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.utils.ExportTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class Global {

    /**
     * 全局Handler，用于向主UI线程发送消息
     */
    public static final Handler HANDLER = new Handler(Looper.getMainLooper());

    /**
     * 用于持有对读取出的list的引用
     */
    public static List<AppItemBean> list;

    public static void showRequestingWritePermissionSnackBar(@NonNull final Activity activity) {
        Snackbar snackbar = Snackbar.make(activity.findViewById(android.R.id.content), activity.getResources().getString(R.string.permission_write), Snackbar.LENGTH_SHORT);
        snackbar.setAction(activity.getResources().getString(R.string.permission_grant), v -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", activity.getPackageName(), null));
            activity.startActivity(intent);
        });
        snackbar.show();
    }

    public interface ExportTaskFinishedListener {

        void onFinished(@NonNull String errorMessage);

    }

    /**
     * 选择data,obb项，确认重复文件，导出list集合中的应用，并向activity显示一个dialog，传入接口来监听完成回调（在主线程）
     *
     * @param list           AppItem的副本，当check_data_obb值为true时无需初始，false时须提前设置好data,obb值
     * @param check_data_obb 传入true 则会执行一次data,obb检查（list中没有设置data,obb值）
     */
    public static void checkAndExportCertainAppItemsToSetPathWithoutShare(@NonNull final Activity activity, @NonNull final List<AppItemBean> list, boolean check_data_obb, @Nullable final ExportTaskFinishedListener listener) {
        if (list.size() == 0) {
            return;
        }
        if (check_data_obb) {
            DataObbDialog dialog = new DataObbDialog(activity, list, exportList -> {
                String dulplicatedInfo = getDuplicatedFileInfo(activity, exportList);
                if (!"".equals(dulplicatedInfo.trim())) {
                    new AlertDialog.Builder(activity)
                            .setTitle(activity.getResources().getString(R.string.dialog_duplicate_title))
                            .setMessage(activity.getResources().getString(R.string.dialog_duplicate_msg) + dulplicatedInfo)
                            .setPositiveButton(activity.getResources().getString(R.string.dialog_button_confirm), (dialog12, which) -> exportCertainAppItemsToSetPathAndShare(activity, exportList, false, listener))
                            .setNegativeButton(activity.getResources().getString(R.string.dialog_button_cancel), (dialog1, which) -> {
                            })
                            .show();
                    return;
                }
                exportCertainAppItemsToSetPathAndShare(activity, exportList, false, listener);
            });
            dialog.show();
        } else {
            String dulplicatedInfo = getDuplicatedFileInfo(activity, list);
            if (!"".equals(dulplicatedInfo.trim())) {
                new AlertDialog.Builder(activity)
                        .setTitle(activity.getResources().getString(R.string.dialog_duplicate_title))
                        .setMessage(activity.getResources().getString(R.string.dialog_duplicate_msg) + dulplicatedInfo)
                        .setPositiveButton(activity.getResources().getString(R.string.dialog_button_confirm), (dialog, which) -> exportCertainAppItemsToSetPathAndShare(activity, list, false, listener))
                        .setNegativeButton(activity.getResources().getString(R.string.dialog_button_cancel), (dialog, which) -> {
                        })
                        .show();
                return;
            }
            exportCertainAppItemsToSetPathAndShare(activity, list, false, listener);
        }

    }

    /**
     * 导出list集合中的应用，并向activity显示一个dialog，传入接口来监听完成回调（在主线程）
     *
     * @param ifShare 完成后是否执行分享操作
     */
    private static void exportCertainAppItemsToSetPathAndShare(@NonNull final Activity activity, @NonNull List<AppItemBean> exportList, final boolean ifShare, @Nullable final ExportTaskFinishedListener listener) {
        final ExportingDialog dialog = new ExportingDialog(activity);
        final ExportTask task = new ExportTask(activity, exportList, null);
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, activity.getResources().getString(R.string.dialog_export_stop), (dialog1, which) -> {
            task.setInterrupted();
            dialog1.cancel();
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        task.setExportProgressListener(new ExportTask.ExportProgressListener() {
            @Override
            public void onExportAppItemStarted(int order, AppItemBean item, int total, String writePath) {
                dialog.setProgressOfApp(order, total, item, writePath);
            }

            @Override
            public void onExportProgressUpdated(long current, long total, String writePath) {
                dialog.setProgressOfWriteBytes(current, total);
            }

            @Override
            public void onExportZipProgressUpdated(String writePath) {
                dialog.setProgressOfCurrentZipFile(writePath);
            }

            @Override
            public void onExportSpeedUpdated(long speed) {
                dialog.setSpeed(speed);
            }

            @Override
            public void onExportTaskFinished(List<String> writePaths, String errorMessage) {
                dialog.cancel();
                if (listener != null) {
                    listener.onFinished(errorMessage);
                }
                if (ifShare) {
                    shareCertainApps(activity, writePaths, activity.getResources().getString(R.string.share_title));
                }
            }
        });
        task.run();
    }

    /**
     * 通过包名获取指定list中的item
     *
     * @param list        要遍历的list
     * @param packageName 要定位的包名
     * @return 查询到的AppItem
     */
    @Nullable
    public static AppItemBean getAppItemByPackageNameFromList(@NonNull List<AppItemBean> list, @NonNull String packageName) {
        try {
            for (AppItemBean item : list) {
                if (item.getPackageName().trim().toLowerCase().equals(packageName.trim().toLowerCase())) {
                    return item;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDuplicatedFileInfo(@NonNull Context context, @NonNull List<AppItemBean> items) {
        if (items.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (AppItemBean item : items) {
            File file = new File(getAbsoluteWritePath(context, item, (item.exportData || item.exportObb) ? "zip" : "apk"));
            if (file.exists()) {
                builder.append(file.getAbsolutePath());
                builder.append("\n\n");
            }
        }
        return builder.toString();
    }

    /**
     * 分享指定item应用
     *
     * @param items 传入AppItem的副本，data obb为false
     */
    public static void shareCertainAppsByItems(@NonNull final Activity activity, @NonNull final List<AppItemBean> items) {
        if (items.size() == 0) {
            return;
        }
        boolean ifNeedExport = Global.getGlobalSharedPreferences(activity)
                .getInt(Constant.INSTANCE.getPREFERENCE_SHAREMODE(), Constant.INSTANCE.getPREFERENCE_SHAREMODE_DEFAULT()) == Constant.INSTANCE.getSHARE_MODE_AFTER_EXTRACT();
        if (ifNeedExport) {
            DataObbDialog dialog = new DataObbDialog(activity, items, exportList -> {
                String dulplicatedInfo = getDuplicatedFileInfo(activity, items);
                final ExportTaskFinishedListener exportTaskFinishedListener = errorMessage -> {
                    if (!"".equals(errorMessage.trim())) {
                        new AlertDialog.Builder(activity)
                                .setTitle(activity.getResources().getString(R.string.exception_title))
                                .setMessage(activity.getResources().getString(R.string.exception_message) + errorMessage)
                                .setPositiveButton(activity.getResources().getString(R.string.dialog_button_confirm), (dialog1, which) -> {
                                })
                                .show();
                        return;
                    }
                    ToastManager.showToast(activity, activity.getResources().getString(R.string.toast_export_complete) + getSavePath(activity), Toast.LENGTH_SHORT);
                };
                if (!"".equals(dulplicatedInfo.trim())) {
                    new AlertDialog.Builder(activity)
                            .setTitle(activity.getResources().getString(R.string.dialog_duplicate_title))
                            .setMessage(activity.getResources().getString(R.string.dialog_duplicate_msg) + dulplicatedInfo)
                            .setPositiveButton(activity.getResources().getString(R.string.dialog_button_confirm), (dialog12, which) -> exportCertainAppItemsToSetPathAndShare(activity, items, true, exportTaskFinishedListener))
                            .setNegativeButton(activity.getResources().getString(R.string.dialog_button_cancel), (dialog13, which) -> {
                            })
                            .show();
                    return;
                }
                exportCertainAppItemsToSetPathAndShare(activity, items, true, exportTaskFinishedListener);
            });
            dialog.show();
        } else {
            ArrayList<String> paths = new ArrayList<>();
            if (items.size() == 1) {
                AppItemBean item = items.get(0);
                paths.add(item.getSourcePath());
                shareCertainApps(activity, paths, activity.getResources().getString(R.string.share_title) + " " + item.getAppName());
            } else {
                for (AppItemBean item : items) {
                    paths.add(item.getSourcePath());
                }
                shareCertainApps(activity, paths, activity.getResources().getString(R.string.share_title));
            }
        }
    }

    /**
     * 执行分享应用操作
     */
    private static void shareCertainApps(@NonNull Activity activity, @NonNull List<String> paths, @NonNull String title) {
        if (paths.size() == 0) {
            return;
        }
        Intent intent = new Intent();
        intent.setType("application/x-zip-compressed");
        if (paths.size() > 1) {
            intent.setAction(Intent.ACTION_SEND_MULTIPLE);
            ArrayList<Uri> uris = new ArrayList<>();
            for (String path : paths) {
                uris.add(Uri.fromFile(new File(path)));
            }
            intent.putExtra(Intent.EXTRA_STREAM, uris);
        } else {
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(paths.get(0))));
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, title);
        intent.putExtra(Intent.EXTRA_TEXT, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(Intent.createChooser(intent, title));
    }


    /**
     * 获取当前应用导出的主路径
     *
     * @return 应用导出路径，最后没有文件分隔符，例如 /storage/emulated/0
     */
    @NonNull
    public static String getSavePath(@NonNull Context context) {
        try {
            return getGlobalSharedPreferences(context).getString(Constant.INSTANCE.getPREFERENCE_SAVE_PATH(), Constant.INSTANCE.getPREFERENCE_SAVE_PATH_DEFAULT());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Constant.INSTANCE.getPREFERENCE_SAVE_PATH_DEFAULT();
    }

    /**
     * 为AppItem获取一个绝对写入路径
     *
     * @param extension "apk"或者"zip"
     */
    @NonNull
    public static String getAbsoluteWritePath(@NonNull Context context, @NonNull AppItemBean item, @NonNull String extension) {
        try {
            SharedPreferences settings = getGlobalSharedPreferences(context);
            if ("apk".equals(extension.toLowerCase(Locale.getDefault()))) {
                return settings.getString(Constant.INSTANCE.getPREFERENCE_SAVE_PATH(), Constant.INSTANCE.getPREFERENCE_SAVE_PATH_DEFAULT())
                        + "/" + settings.getString(Constant.INSTANCE.getPREFERENCE_FILENAME_FONT_APK(), Constant.INSTANCE.getPREFERENCE_FILENAME_FONT_DEFAULT()).replace(Constant.INSTANCE.getFONT_APP_NAME(), String.valueOf(item.getAppName()))
                        .replace(Constant.INSTANCE.getFONT_APP_PACKAGE_NAME(), String.valueOf(item.getPackageName()))
                        .replace(Constant.INSTANCE.getFONT_APP_VERSIONCODE(), String.valueOf(item.getVersionCode()))
                        .replace(Constant.INSTANCE.getFONT_APP_VERSIONNAME(), String.valueOf(item.getVersionName())) + ".apk";
            }
            if ("zip".equals(extension.toLowerCase(Locale.ENGLISH))) {
                return settings.getString(Constant.INSTANCE.getPREFERENCE_SAVE_PATH(), Constant.INSTANCE.getPREFERENCE_SAVE_PATH_DEFAULT())
                        + "/" + settings.getString(Constant.INSTANCE.getPREFERENCE_FILENAME_FONT_ZIP(), Constant.INSTANCE.getPREFERENCE_FILENAME_FONT_DEFAULT()).replace(Constant.INSTANCE.getFONT_APP_NAME(), String.valueOf(item.getAppName()))
                        .replace(Constant.INSTANCE.getFONT_APP_PACKAGE_NAME(), String.valueOf(item.getPackageName()))
                        .replace(Constant.INSTANCE.getFONT_APP_VERSIONCODE(), String.valueOf(item.getVersionCode()))
                        .replace(Constant.INSTANCE.getFONT_APP_VERSIONNAME(), String.valueOf(item.getVersionName())) + ".zip";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static SharedPreferences getGlobalSharedPreferences(@NonNull Context context) {
        return context.getSharedPreferences(Constant.INSTANCE.getPREFERENCE_NAME(), Context.MODE_PRIVATE);
    }

    /**
     * 刷新已安装的应用列表
     */
    public static class RefreshInstalledListTask extends Thread {
        private Context context;
        private boolean flagSystem;
        private RefreshInstalledListTaskCallback listener;
        private List<AppItemBean> listSum = new ArrayList<>();

        public RefreshInstalledListTask(@NonNull Context context, boolean flagSystem, @Nullable RefreshInstalledListTaskCallback callback) {
            this.context = context;
            this.flagSystem = flagSystem;
            this.listener = callback;
        }

        @Override
        public void run() {
            PackageManager manager = context.getApplicationContext().getPackageManager();
            SharedPreferences settings = getGlobalSharedPreferences(context);
            int flag = PackageManager.GET_SIGNATURES;
            if (settings.getBoolean(Constant.INSTANCE.getPREFERENCE_LOAD_PERMISSIONS(), Constant.INSTANCE.getPREFERENCE_LOAD_PERMISSIONS_DEFAULT())) {
                flag |= PackageManager.GET_PERMISSIONS;
            }
            if (settings.getBoolean(Constant.INSTANCE.getPREFERENCE_LOAD_ACTIVITIES(), Constant.INSTANCE.getPREFERENCE_LOAD_ACTIVITIES_DEFAULT())) {
                flag |= PackageManager.GET_ACTIVITIES;
            }
            if (settings.getBoolean(Constant.INSTANCE.getPREFERENCE_LOAD_RECEIVERS(), Constant.INSTANCE.getPREFERENCE_LOAD_RECEIVERS_DEFAULT())) {
                flag |= PackageManager.GET_RECEIVERS;
            }

            final List<PackageInfo> list = manager.getInstalledPackages(flag);
            for (int i = 0; i < list.size(); i++) {
                PackageInfo info = list.get(i);
                boolean infoIsSystemApp = ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0);
                final int current = i + 1;
                Global.HANDLER.post(() -> {
                    if (listener != null) {
                        listener.onRefreshProgressUpdated(current, list.size());
                    }
                });
                if (!flagSystem && infoIsSystemApp) {
                    continue;
                }
                listSum.add(new AppItemBean(context, info));
            }
            AppItemBean.sortConfig = settings.getInt(Constant.INSTANCE.getPREFERENCE_SORT_CONFIG(), 0);
            Collections.sort(listSum);
            synchronized (Global.class) {
                //向全局list保存一个引用
                Global.list = listSum;
            }
            Global.HANDLER.post(() -> {
                if (listener != null) {
                    listener.onRefreshCompleted(listSum);
                }
            });

        }
    }

    public interface RefreshInstalledListTaskCallback {

        void onRefreshProgressUpdated(int current, int total);

        void onRefreshCompleted(List<AppItemBean> appList);

    }

}
