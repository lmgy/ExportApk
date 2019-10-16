package com.lmgy.exportapk.bean;

import android.graphics.drawable.Drawable;

import com.lmgy.exportapk.utils.PinYinUtils;

public class AppItemBean implements Comparable<AppItemBean> {

    public static int SortConfig = 0;

    public String appName = "";
    public String packageName = "";
    public Drawable icon;
    public long appSize = 0;
    public String path = "";
    public String version = "";
    public int versioncode = 0;
    public long lastUpdateTime = 0;
    public int minSdkVersion = 0;
    public boolean exportData = false;
    public boolean exportObb = false;
    public boolean isSystemApp = false;

    public AppItemBean() {
    }

    public AppItemBean(AppItemBean item) {
        this.appName = item.appName;
        this.packageName = item.packageName;
        this.icon = item.icon;
        this.appSize = item.appSize;
        this.path = item.path;
        this.version = item.version;
        this.versioncode = item.versioncode;
        this.lastUpdateTime = item.lastUpdateTime;
        this.minSdkVersion = item.minSdkVersion;
        this.exportData = false;
        this.exportObb = false;
    }

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }

    public long getAppSize() {
        return appSize;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public int getVersioncode() {
        return versioncode;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public int getMinSdkVersion() {
        return minSdkVersion;
    }

    public boolean isExportData() {
        return exportData;
    }

    public boolean isExportObb() {
        return exportObb;
    }

    public boolean isSystemApp() {
        return isSystemApp;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setAppSize(long appSize) {
        this.appSize = appSize;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setVersioncode(int versioncode) {
        this.versioncode = versioncode;
    }

    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public void setMinSdkVersion(int minSdkVersion) {
        this.minSdkVersion = minSdkVersion;
    }

    public void setExportData(boolean exportData) {
        this.exportData = exportData;
    }

    public void setExportObb(boolean exportObb) {
        this.exportObb = exportObb;
    }

    public void setSystemApp(boolean systemApp) {
        isSystemApp = systemApp;
    }

    @Override
    public int compareTo(AppItemBean o) {
        int returnValue = 0;
        switch (SortConfig) {
            default:
                break;
            case 1:
                returnValue = PinYinUtils.getFirstSpell(this.appName).compareTo(PinYinUtils.getFirstSpell(o.appName));
                break;
            case 2:
                returnValue = 0 - PinYinUtils.getFirstSpell(this.appName).compareTo(PinYinUtils.getFirstSpell(o.appName));
                break;
            case 3:
                returnValue = Long.compare(this.appSize, o.appSize);
                break;
            case 4:
                returnValue = 0 - Long.compare(this.appSize, o.appSize);
                break;
            case 5:
                returnValue = Long.compare(this.lastUpdateTime, o.lastUpdateTime);
                break;
            case 6:
                returnValue = 0 - Long.compare(this.lastUpdateTime, o.lastUpdateTime);
                break;
        }
        return returnValue;
    }

}

