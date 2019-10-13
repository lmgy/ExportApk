package com.lmgy.exportapk.bean;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lmgy.exportapk.utils.EnvironmentUtils;
import com.lmgy.exportapk.utils.FileUtils;
import com.lmgy.exportapk.utils.PinyinUtils;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class AppItemBean implements Parcelable, Comparable<AppItemBean> {

    public static transient int sortConfig = 0;

    public static final Creator<AppItemBean> CREATOR = new Creator<AppItemBean>() {
        @Override
        public AppItemBean createFromParcel(Parcel source) {
            return new AppItemBean(source);
        }

        @Override
        public AppItemBean[] newArray(int size) {
            return new AppItemBean[size];
        }
    };

    private PackageInfo info;

    /**
     * 程序名
     */
    private String title;

    /**
     * 应用大小
     */
    private long size;

    //private HashMap<String, List<String>> static_receivers;

    private Bundle staticReceiversBundle;

    //仅当构造ExportTask时用
    public transient boolean exportData = false;
    public transient boolean exportObb = false;

    /**
     * 初始化一个全新的AppItemBean
     *
     * @param context context实例，用来获取应用图标、名称等参数
     * @param info    PackageInfo实例，对应的本AppItem的信息
     */
    public AppItemBean(@NonNull Context context, @NonNull PackageInfo info) {
        this.info = info;
        this.title = context.getPackageManager().getApplicationLabel(info.applicationInfo).toString();
        this.size = FileUtils.getFileOrFolderSize(new File(info.applicationInfo.sourceDir));
        this.staticReceiversBundle = EnvironmentUtils.getStaticRegisteredReceiversOfBundleTypeForPackageName(context, info.packageName);
    }

    /**
     * 构造一个本Item的副本，用于ExportTask导出应用。
     *
     * @param wrapper   用于创造副本的目标
     * @param flag_data 指定是否导出data
     * @param flag_obb  指定是否导出obb
     */
    public AppItemBean(AppItemBean wrapper, boolean flag_data, boolean flag_obb) {
        this.title = wrapper.title;
        this.size = wrapper.size;
        this.info = wrapper.info;
        this.exportData = flag_data;
        this.exportObb = flag_obb;
    }

    private AppItemBean(Parcel in) {
        title = in.readString();
        size = in.readLong();
        info = in.readParcelable(PackageInfo.class.getClassLoader());
        staticReceiversBundle = in.readBundle(getClass().getClassLoader());
    }

    /**
     * 获取应用图标
     */
    @Nullable
    public Drawable getIcon(@NonNull Context context) {
        return context.getPackageManager().getApplicationIcon(info.applicationInfo);
    }

    /**
     * 获取应用名称
     */
    @Nullable
    public String getAppName() {
        return title;
    }

    /**
     * 获取包名
     */
    @Nullable
    public String getPackageName() {
        return info.packageName;
    }

    /**
     * 获取应用源路径
     */
    @Nullable
    public String getSourcePath() {
        return info.applicationInfo.sourceDir;
    }

    /**
     * 获取应用大小（源文件），单位字节
     */
    public long getSize() {
        return size;
    }

    /**
     * 获取应用版本名称
     */
    @Nullable
    public String getVersionName() {
        return info.versionName;
    }

    /**
     * 获取应用版本号
     */
    public int getVersionCode() {
        return info.versionCode;
    }

    /**
     * 获取本应用Item对应的PackageInfo实例
     */
    public @NonNull
    PackageInfo getPackageInfo() {
        return info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeLong(size);
        dest.writeParcelable(info, 0);
        dest.writeBundle(staticReceiversBundle);
    }

    @NonNull
    public Bundle getStaticReceiversBundle() {
        return staticReceiversBundle;
    }

    /**
     * 排序模式。
     * 0 - 默认
     * 1 - 名称升序
     * 2 - 名称降序
     * 3 - 大小升序
     * 4 - 大小降序
     * 5 - 更新日期升序
     * 6 - 更新日期降序
     * 7 - 安装日期升序
     * 8 - 安装日期降序
     */

    @Override
    public int compareTo(@NotNull AppItemBean o) {
        switch (sortConfig) {
            default:
                break;
            case 1: {
                try {
                    if (PinyinUtils.getFirstSpell(title).toLowerCase().compareTo(PinyinUtils.getFirstSpell(o.title).toLowerCase()) > 0) {
                        return 1;
                    }
                    if (PinyinUtils.getFirstSpell(title).toLowerCase().compareTo(PinyinUtils.getFirstSpell(o.title).toLowerCase()) < 0) {
                        return -1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case 2: {
                try {
                    if (PinyinUtils.getFirstSpell(title).toLowerCase().compareTo(PinyinUtils.getFirstSpell(o.title).toLowerCase()) < 0) {
                        return 1;
                    }
                    if (PinyinUtils.getFirstSpell(title).toLowerCase().compareTo(PinyinUtils.getFirstSpell(o.title).toLowerCase()) > 0) {
                        return -1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case 3: {
                if (size - o.size > 0) {
                    return 1;
                }
                if (size - o.size < 0) {
                    return -1;
                }
            }
            break;
            case 4: {
                if (size - o.size < 0) {
                    return 1;
                }
                if (size - o.size > 0) {
                    return -1;
                }
            }
            break;
            case 5: {
                if (info.lastUpdateTime - o.info.lastUpdateTime > 0) {
                    return 1;
                }
                if (info.lastUpdateTime - o.info.lastUpdateTime < 0) {
                    return -1;
                }
            }
            break;
            case 6: {
                if (info.lastUpdateTime - o.info.lastUpdateTime < 0) {
                    return 1;
                }
                if (info.lastUpdateTime - o.info.lastUpdateTime > 0) {
                    return -1;
                }
            }
            break;
            case 7: {
                if (info.firstInstallTime - o.info.firstInstallTime > 0) {
                    return 1;
                }
                if (info.firstInstallTime - o.info.firstInstallTime < 0) {
                    return -1;
                }
            }
            break;
            case 8: {
                if (info.firstInstallTime - o.info.firstInstallTime < 0) {
                    return 1;
                }
                if (info.firstInstallTime - o.info.firstInstallTime > 0) {
                    return -1;
                }
            }
            break;
        }
        return 0;
    }

}
