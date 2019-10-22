package com.lmgy.exportapk.widget;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.listener.DialogClick;

/**
 * @author lmgy
 * @date 2019/10/22
 */
public class SortDialog extends AlertDialog {

    @SuppressLint("InflateParams")
    public SortDialog(Context context, DialogClick dialogClick) {
        super(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View dialogView = layoutInflater.inflate(R.layout.dialog_sort, null);
        this.setView(dialogView);
        RadioButton rbDefault = dialogView.findViewById(R.id.sort_ra_default);
        RadioButton rbAscendingAppName = dialogView.findViewById(R.id.sort_ra_ascending_appname);
        RadioButton rbDescendingAppName = dialogView.findViewById(R.id.sort_ra_descending_appname);
        RadioButton rbAscendingSize = dialogView.findViewById(R.id.sort_ra_ascending_appsize);
        RadioButton rbDescendingSize = dialogView.findViewById(R.id.sort_ra_descending_appsize);
        RadioButton rbAscendingDate = dialogView.findViewById(R.id.sort_ra_ascending_date);
        RadioButton rbDescendingDate = dialogView.findViewById(R.id.sort_ra_descending_date);

        this.setTitle(getContext().getResources().getString(R.string.action_sort));
        this.setIcon(R.drawable.ic_sort_black);
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);

        rbDefault.setOnClickListener(v -> dialogClick.onClick(0));
        rbAscendingAppName.setOnClickListener(v -> dialogClick.onClick(1));
        rbDescendingAppName.setOnClickListener(v -> dialogClick.onClick(2));
        rbAscendingSize.setOnClickListener(v -> dialogClick.onClick(3));
        rbDescendingSize.setOnClickListener(v -> dialogClick.onClick(4));
        rbAscendingDate.setOnClickListener(v -> dialogClick.onClick(5));
        rbDescendingDate.setOnClickListener(v -> dialogClick.onClick(6));

        switch (AppItemBean.SortConfig) {
            default:
                break;
            case 0:
                rbDefault.setChecked(true);
                break;
            case 1:
                rbAscendingAppName.setChecked(true);
                break;
            case 2:
                rbDescendingAppName.setChecked(true);
                break;
            case 3:
                rbAscendingSize.setChecked(true);
                break;
            case 4:
                rbDescendingSize.setChecked(true);
                break;
            case 5:
                rbAscendingDate.setChecked(true);
                break;
            case 6:
                rbDescendingDate.setChecked(true);
                break;
        }
    }
}
