package com.lmgy.exportapk.config;

import androidx.appcompat.app.AppCompatDelegate;

import com.lmgy.exportapk.utils.Storage;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class Constant {

    public static String[] staticFilters = new String[]{
            "android.intent.action.LOCKED_BOOT_COMPLETED",
            "android.intent.action.BOOT_COMPLETED",
            "android.intent.action.USER_INITIALIZE",
            "android.intent.action.USER_ADDED",
            "android.intent.action.USER_REMOVED",
            "android.intent.action.TIME_SET",
            "android.intent.action.TIMEZONE_CHANGED",
            "android.app.action.NEXT_ALARM_CLOCK_CHANGED",
            "android.intent.action.LOCALE_CHANGED",
            "android.hardware.usb.action.USB_ACCESSORY_ATTACHED",
            "android.hardware.usb.action.USB_ACCESSORY_DETACHED",
            "android.hardware.usb.action.USB_DEVICE_ATTACHED",
            "android.hardware.usb.action.USB_DEVICE_DETACHED",
            "android.bluetooth.headset.profile.action.CONNECTION_STATE_CHANGED",
            "android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED",
            "android.bluetooth.device.action.ACL_CONNECTED",
            "android.bluetooth.device.action.ACL_DISCONNECTED",
            "android.telephony.action.CARRIER_CONFIG_CHANGED",
            "android.intent.action.PHONE_STATE",
            "android.telecom.action.PHONE_ACCOUNT_REGISTERED",
            "android.telecom.action.PHONE_ACCOUNT_UNREGISTERED",
            "android.accounts.LOGIN_ACCOUNTS_CHANGED",
            "android.intent.action.PACKAGE_DATA_CLEARED",
            "android.intent.action.PACKAGE_FULLY_REMOVED",
            "android.intent.action.NEW_OUTGOING_CALL",
            "android.app.action.DEVICE_OWNER_CHANGED",
            "android.intent.action.EVENT_REMINDER",
            "android.intent.action.MEDIA_MOUNTED",
            "android.intent.action.MEDIA_CHECKING",
            "android.intent.action.MEDIA_EJECT",
            "android.intent.action.MEDIA_UNMOUNTED",
            "android.intent.action.MEDIA_UNMOUNTABLE",
            "android.intent.action.MEDIA_REMOVED",
            "android.intent.action.MEDIA_BAD_REMOVAL",
            "android.provider.Telephony.SMS_RECEIVED",
            "android.provider.Telephony.WAP_PUSH_RECEIVED",
            "android.permission.RECEIVE_SMS",
            "android.permission.RECEIVE_WAP_PUSH"
    };

    public static final String PREFERENCE_NAME = "settings";
    public static final String PREFERENCE_SAVE_PATH = "savepath";
    public static final String PREFERENCE_SAVE_PATH_DEFAULT = Storage.getMainExternalStoragePath() + "/Backup";
    public static final String PREFERENCE_STORAGE_PATH = "storage_path";
    public static final String PREFERENCE_STORAGE_PATH_DEFAULT = Storage.getMainExternalStoragePath();
    public static final String PREFERENCE_FILENAME_FONT_APK = "font_apk";
    public static final String PREFERENCE_FILENAME_FONT_ZIP = "font_zip";
    public static final String PREFERENCE_ZIP_COMPRESS_LEVEL = "zip_level";
    public static final int PREFERENCE_ZIP_COMPRESS_LEVEL_DEFAULT = -1;
    public static final String PREFERENCE_SHAREMODE = "share_mode";
    public static final String PREFERENCE_SORT_CONFIG = "sort_config";
    public static final String PREFERENCE_MAIN_PAGE_VIEW_MODE = "main_view_mode";
    public static final int PREFERENCE_MAIN_PAGE_VIEW_MODE_DEFAULT = 0;
    public static final String PREFERENCE_SHOW_SYSTEM_APP = "show_system_app";
    public static final boolean PREFERENCE_SHOW_SYSTEM_APP_DEFAULT = false;
    public static final String PREFERENCE_LOAD_PERMISSIONS = "load_permissions";
    public static final String PREFERENCE_LOAD_ACTIVITIES = "load_activities";
    public static final String PREFERENCE_LOAD_RECEIVERS = "load_receivers";
    public static final String PREFERENCE_LOAD_STATIC_LOADERS = "load_static_receivers";
    public static final String PREFERENCE_NIGHT_MODE = "night_mode";
    public static final int PREFERENCE_NIGHT_MODE_DEFAULT = AppCompatDelegate.MODE_NIGHT_NO;
    public static final boolean PREFERENCE_LOAD_PERMISSIONS_DEFAULT = true;
    public static final boolean PREFERENCE_LOAD_ACTIVITIES_DEFAULT = true;
    public static final boolean PREFERENCE_LOAD_RECEIVERS_DEFAULT = true;
    public static final boolean PREFERENCE_LOAD_STATIC_LOADERS_DEFAULT = false;
    public static final int SHARE_MODE_DIRECT = -1;
    public static final int SHARE_MODE_AFTER_EXTRACT = 0;
    public static final int PREFERENCE_SHAREMODE_DEFAULT = SHARE_MODE_DIRECT;
    public static final String FONT_APP_NAME = "?N";
    public static final String FONT_APP_PACKAGE_NAME = "?P";
    public static final String FONT_APP_VERSIONCODE = "?C";
    public static final String FONT_APP_VERSIONNAME = "?V";
    public static final int ZIP_LEVEL_STORED = 0;
    public static final int ZIP_LEVEL_LOW = 1;
    public static final int ZIP_LEVEL_NORMAL = 5;
    public static final int ZIP_LEVEL_HIGH = 9;
    public static final String PREFERENCE_FILENAME_FONT_DEFAULT = FONT_APP_PACKAGE_NAME + "-" + FONT_APP_VERSIONCODE;

}
