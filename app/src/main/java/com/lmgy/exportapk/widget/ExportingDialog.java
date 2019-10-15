package com.lmgy.exportapk.widget;

import android.content.Context;
import android.text.format.Formatter;

import androidx.annotation.NonNull;

import com.lmgy.exportapk.R;

import java.text.DecimalFormat;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class ExportingDialog extends ProgressDialog {

    public ExportingDialog(@NonNull Context context) {
        super(context, context.getResources().getString(R.string.dialog_export_title));
    }

    public void setProgressOfApp(int current, int total, @NonNull AppItemBean item, @NonNull String writePath) {
        setTitle(getContext().getResources().getString(R.string.dialog_export_title) + "(" + current + "/" + total + ")" + ":" + item.getAppName());
        setIcon(item.getIcon(getContext()));
        att.setText(getContext().getResources().getString(R.string.dialog_export_msg_apk) + writePath);
    }

    public void setProgressOfWriteBytes(long current, long total) {
        if (current < 0 || current > total) {
            return;
        }
        progressBar.setMax((int) (total / 1024));
        progressBar.setProgress((int) (current / 1024));
        DecimalFormat dm = new DecimalFormat("#.00");
        int percent = (int) (Double.valueOf(dm.format((double) current / total)) * 100);
        attRight.setText(Formatter.formatFileSize(getContext(), current) + "/" + Formatter.formatFileSize(getContext(), total) + "(" + percent + "%)");
    }

    public void setSpeed(long bytes) {
        attLeft.setText(Formatter.formatFileSize(getContext(), bytes) + "/s");
    }

    public void setProgressOfCurrentZipFile(@NonNull String writePath) {
        att.setText(getContext().getResources().getString(R.string.dialog_export_zip) + writePath);
    }

}
