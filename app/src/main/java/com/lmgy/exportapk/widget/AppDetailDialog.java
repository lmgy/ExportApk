package com.lmgy.exportapk.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lmgy.exportapk.R;
import com.lmgy.exportapk.listener.DialogClick;

import java.util.Calendar;

/**
 * @author lmgy
 * @date 2019/10/17
 */
public class AppDetailDialog extends BottomSheetDialog {
    private TextView tvAtt;
    private RelativeLayout areaExtract, areaShare, areaDetail;
    private Context mContext;
    private String appInfo = "";
    private TextView appName;
    private ImageView appIcon;


    public AppDetailDialog(@NonNull Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View dialogView = layoutInflater.inflate(R.layout.dialog_appdetail, null);
        setContentView(dialogView);
        tvAtt = dialogView.findViewById(R.id.dialog_appdetail_text);
        areaExtract = dialogView.findViewById(R.id.dialog_appdetail_area_extract);
        areaShare = dialogView.findViewById(R.id.dialog_appdetail_area_share);
        areaDetail = dialogView.findViewById(R.id.dialog_appdetail_area_detail);

        appName = dialogView.findViewById(R.id.app_detail_name);
        appIcon = dialogView.findViewById(R.id.app_detail_icon);

        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
    }

    public void setTitle(String string) {
        appName.setText(string);
    }

    public void setIcon(Drawable drawable) {
        appIcon.setImageDrawable(drawable);
    }

    public void setonClickListener(DialogClick dialogClick){
        areaExtract.setOnClickListener(view -> dialogClick.onClick(1));
        areaShare.setOnClickListener(view -> dialogClick.onClick(2));
        areaDetail.setOnClickListener(view -> dialogClick.onClick(3));
    }


    public void setAppInfo(String version, int versioncode, long lastUpdateTime, long appSize) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(lastUpdateTime);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);

        String appInfo = mContext.getResources().getString(R.string.dialog_appdetail_text_versioncode) + versioncode + "\n"
                + mContext.getResources().getString(R.string.version) + version + "\n"
                + mContext.getResources().getString(R.string.dialog_appdetail_text_appsize) + Formatter.formatFileSize(mContext, appSize) + "\n"
                + mContext.getResources().getString(R.string.dialog_appdetail_text_lastupdatetime) + format(year) + "/" + format(month) + "/" + format(day) + "/" + format(hour) + ":" + format(min) + ":" + format(sec);

        this.appInfo = appInfo;
        this.tvAtt.setText(appInfo);
    }

    private String format(int x) {
        String s = "" + x;
        if (s.length() == 1) {
            s = "0" + s;
        }
        return s;
    }


    public void setAPPMinSDKVersion(int version) {
        if (Build.VERSION.SDK_INT >= 24) {
            String title = mContext.getResources().getString(R.string.dialog_appdetail_text_minsdkversion);
            appInfo += "\n" + title + version + " ";
            switch (version) {
                default:
                    break;
                case 1:
                    appInfo += " (Android 1.0 Base)";
                    break;
                case 2:
                    appInfo += " (Android 1.1 BASE_1_1)";
                    break;
                case 3:
                    appInfo += " (Android 1.5 CUPCAKE)";
                    break;
                case 4:
                    appInfo += " (Android 1.6 DONUT)";
                    break;
                case 5:
                    appInfo += " (Android 2.0	 ECLAIR)";
                    break;
                case 6:
                    appInfo += " (Android 2.0.1 ECLAIR_0_1)";
                    break;
                case 7:
                    appInfo += " (Android 2.1 ECLAIR_MR1)";
                    break;
                case 8:
                    appInfo += " (Android 2.2 FROYO)";
                    break;
                case 9:
                    appInfo += " (Android 2.3 GINGERBREAD)";
                    break;
                case 10:
                    appInfo += " (Android 2.3.3 GINGERBREAD_MR1)";
                    break;
                case 11:
                    appInfo += " (Android 3.0 HONEYCOMB)";
                    break;
                case 12:
                    appInfo += " (Android 3.1 HONEYCOMB_MR1)";
                    break;
                case 13:
                    appInfo += " (Android 3.2 HONEYCOMB_MR2)";
                    break;
                case 14:
                    appInfo += " (Android 4.0 ICE_CREAM_SANDWICH)";
                    break;
                case 15:
                    appInfo += " (Android 4.0 ICE_CREAM_SANDWICH_MR1)";
                    break;
                case 16:
                    appInfo += " (Android 4.1 JELLY_BEAN)";
                    break;
                case 17:
                    appInfo += " (Android 4.2 JELLY_BEAN_MR1)";
                    break;
                case 18:
                    appInfo += " (Android 4.3 JELLY_BEAN_MR2)";
                    break;
                case 19:
                    appInfo += " (Android 4.4 KITKAT)";
                    break;
                case 20:
                    appInfo += " (Android 4.4W KITKAT_WATCH)";
                    break;
                case 21:
                    appInfo += " (Android 5.0 LOLLIPOP)";
                    break;
                case 22:
                    appInfo += " (Android 5.1 LOLLIPOP_MR1)";
                    break;
                case 23:
                    appInfo += " (Android 6.0 Marshmallow)";
                    break;
                case 24:
                    appInfo += " (Android 7.0 Nougat)";
                    break;
                case 25:
                    appInfo += " (Android 7.1 Nougat_MR1)";
                    break;
                case 26:
                    appInfo += " (Android 8.0 Oreo)";
                    break;
                case 27:
                    appInfo += " (Android 8.1 Oreo_MR1)";
                    break;
                //the newest api now is 27 before I wrote this program
            }

            tvAtt.setText(appInfo);
        }
    }
}
