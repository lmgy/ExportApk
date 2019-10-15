package com.lmgy.exportapk.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.lmgy.exportapk.Global;
import com.lmgy.exportapk.R;
import com.lmgy.exportapk.utils.FileUtils;
import com.lmgy.exportapk.utils.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class DataObbDialog extends AlertDialog implements View.OnClickListener {

    private final View view;
    private DialogDataObbConfirmedCallback callback;
    private final List<AppItemBean> list;
    private final List<AppItemBean> listDataControllable = new ArrayList<>();
    private final List<AppItemBean> listObbControllable = new ArrayList<>();
    private CheckBox cbData;
    private CheckBox cbObb;

    /**
     * @param exportList 传递进来的AppItem务必为使用wrapper构造的副本，初始Data和Obb导出值为false
     */
    @SuppressLint("InflateParams")
    public DataObbDialog(@NonNull Context context, @NonNull List<AppItemBean> exportList, final DialogDataObbConfirmedCallback callback) {
        super(context);
        this.list = exportList;
        this.callback = callback;
        view = LayoutInflater.from(context).inflate(R.layout.dialog_data_obb, null);
        cbData = view.findViewById(R.id.dialog_checkbox_data);
        cbObb = view.findViewById(R.id.dialog_checkbox_obb);
        setView(view);
        setTitle(context.getResources().getString(R.string.dialog_data_obb_title));

        setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.dialog_button_confirm), (dialog, which) -> {
        });
        setButton(AlertDialog.BUTTON_NEGATIVE, context.getResources().getString(R.string.dialog_button_cancel), (dialog, which) -> {
        });


    }

    @Override
    public void show() {
        super.show();
        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(null);
        new Thread(() -> {
            synchronized (DataObbDialog.this) {
                long data = 0, obb = 0;
                for (AppItemBean item : list) {
                    long data_item = FileUtils.INSTANCE.getFileOrFolderSize(new File(Storage.INSTANCE.getMainExternalStoragePath() + "/android/data/" + item.getPackageName()));
                    long obb_item = FileUtils.INSTANCE.getFileOrFolderSize(new File(Storage.INSTANCE.getMainExternalStoragePath() + "/android/obb/" + item.getPackageName()));
                    data += data_item;
                    obb += obb_item;
                    if (data > 0) {
                        listDataControllable.add(item);
                    }
                    if (obb > 0) {
                        listObbControllable.add(item);
                    }
                }
                final long data_total = data;
                final long obb_total = obb;
                Global.HANDLER.post(() -> {
                    if (data_total == 0 && obb_total == 0) {
                        cancel();
                        if (callback != null) {
                            callback.onDialogDataObbConfirmed(list);
                        }
                        return;
                    }
                    view.findViewById(R.id.dialog_data_obb_wait_area).setVisibility(View.GONE);
                    view.findViewById(R.id.dialog_data_obb_show_area).setVisibility(View.VISIBLE);
                    cbData.setEnabled(data_total > 0);
                    cbObb.setEnabled(obb_total > 0);
                    cbData.setText("Data(" + Formatter.formatFileSize(getContext(), data_total) + ")");
                    cbObb.setText("Obb(" + Formatter.formatFileSize(getContext(), obb_total) + ")");
                    getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(DataObbDialog.this);
                });
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(getButton(AlertDialog.BUTTON_POSITIVE))) {
            if (cbData.isChecked()) {
                for (AppItemBean item : listDataControllable) {
                    item.exportData = true;
                }
            }
            if (cbObb.isChecked()) {
                for (AppItemBean item : listObbControllable) {
                    item.exportObb = true;
                }
            }
            if (callback != null) {
                callback.onDialogDataObbConfirmed(list);
            }
            cancel();
        }
    }

    public interface DialogDataObbConfirmedCallback {

        void onDialogDataObbConfirmed(@NonNull List<AppItemBean> exportList);

    }

}
