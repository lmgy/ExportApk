package com.lmgy.exportapk.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.lmgy.exportapk.Global;
import com.lmgy.exportapk.config.Constant;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class EnvironmentUtils {

    public static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    @NonNull
    public static String getFormatDateAndTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar.get(Calendar.YEAR) + "/" + getFormatNumberWithZero(calendar.get(Calendar.MONTH) + 1)
                + "/" + getFormatNumberWithZero(calendar.get(Calendar.DAY_OF_MONTH))
                + "/" + getFormatNumberWithZero(calendar.get(Calendar.HOUR_OF_DAY))
                + ":" + getFormatNumberWithZero(calendar.get(Calendar.MINUTE))
                + ":" + getFormatNumberWithZero(calendar.get(Calendar.SECOND));
    }

    @NonNull
    public static String getFormatNumberWithZero(int value) {
        if (value < 0) {
            return String.valueOf(0);
        }
        if (value <= 9) {
            return "0" + value;
        }
        return String.valueOf(value);
    }


    @NonNull
    public static String getSignatureStringOfPackageInfo(@NonNull PackageInfo info) {
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(info.signatures[0].toByteArray());
            return getHexString(localMessageDigest.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @NonNull
    private static String getHexString(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return "";
        }
        StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length) {
                return localStringBuilder.toString();
            }
            String str = Integer.toString(0xFF & paramArrayOfByte[i], 16);
            if (str.length() == 1) {
                str = "0" + str;
            }
            localStringBuilder.append(str);
        }
    }


    @NonNull
    public static HashMap<String, List<String>> getStaticRegisteredReceiversForPackageName(@NonNull Context context, @NonNull String packageName) {
        HashMap<String, List<String>> map = new HashMap<>();
        PackageManager packageManager = context.getPackageManager();
        String[] staticFilters = Constant.INSTANCE.getStaticFilters();
        for (String s : staticFilters) {
            List<ResolveInfo> list = packageManager.queryBroadcastReceivers(new Intent(s), 0);
            for (ResolveInfo info : list) {
                String pn = info.activityInfo.packageName;
                if (pn == null) {
                    continue;
                }
                List<String> filtersClass = map.get(info.activityInfo.name);
                if (filtersClass == null) {
                    filtersClass = new ArrayList<>();
                    filtersClass.add(s);
                    if (pn.equals(packageName)) {
                        map.put(info.activityInfo.name, filtersClass);
                    }
                } else {
                    if (!filtersClass.contains(s)) {
                        filtersClass.add(s);
                    }
                }

            }
        }
        return map;
    }

    @NonNull
    public static Bundle getStaticRegisteredReceiversOfBundleTypeForPackageName(@NonNull Context context, @NonNull String packageName) {
        Bundle bundle = new Bundle();
        if (!Global.getGlobalSharedPreferences(context)
                .getBoolean(Constant.INSTANCE.getPREFERENCE_LOAD_STATIC_LOADERS(), Constant.INSTANCE.getPREFERENCE_LOAD_STATIC_LOADERS_DEFAULT())) {
            return bundle;
        }
        PackageManager packageManager = context.getPackageManager();
        String[] staticFilters = Constant.INSTANCE.getStaticFilters();

        for (String s : staticFilters) {
            List<ResolveInfo> list = packageManager.queryBroadcastReceivers(new Intent(s), 0);
            for (ResolveInfo info : list) {
                String pn = info.activityInfo.packageName;
                if (pn == null) {
                    continue;
                }
                ArrayList<String> filtersClass = bundle.getStringArrayList(info.activityInfo.name);
                if (filtersClass == null) {
                    filtersClass = new ArrayList<>();
                    filtersClass.add(s);
                    if (pn.equals(packageName)) {
                        bundle.putStringArrayList(info.activityInfo.name, filtersClass);
                    }
                } else {
                    if (!filtersClass.contains(s)) {
                        filtersClass.add(s);
                    }
                }

            }
        }
        return bundle;
    }


    public static boolean isALegalFileName(@NonNull String name) {
        try {
            if (name.contains("?") || name.contains("\\") || name.contains("/") || name.contains(":") || name.contains("*") || name.contains("\"")
                    || name.contains("<") || name.contains(">") || name.contains("|")) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
