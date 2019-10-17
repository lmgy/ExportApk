package com.lmgy.exportapk.widget;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.lmgy.exportapk.R;

import java.text.DecimalFormat;

/**
 * @author lmgy
 * @date 2019/10/17
 */
public class FileCopyDialog extends AlertDialog {
    private ProgressBar progressBar;
    private TextView tvSpeed;
    private TextView tvProgress;
    private TextView tvCurrentInfo;
    private long progress = 0;
    private long total = 1024 * 100;
    private long speed = 0;
    private int percent;
    private Context mContext;

    public FileCopyDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View dialogView = layoutInflater.inflate(R.layout.dialog_filecopy, null);
        this.setView(dialogView);
        progressBar = dialogView.findViewById(R.id.progressBar);
        tvCurrentInfo = dialogView.findViewById(R.id.currentfile);
        tvProgress = dialogView.findViewById(R.id.copyprogress);
        tvSpeed = dialogView.findViewById(R.id.copyspeed);
        setMax((int) total / 1024);
    }


    public void setTextAtt(String title) {
        this.tvCurrentInfo.setText(title);
    }

    public void setProgress(long bytes) {
        this.progress = bytes;
        refreshProgress(this.progress);
    }

    public void setMax(long bytes) {
        total = bytes;
        progressBar.setMax((int) (this.total / 1024));
    }

    public void setSpeed(long speedOfBytes) {
        this.speed = speedOfBytes;
        refreshSpeed();
    }

    private void refreshProgress(long progressOfBytes) {
        DecimalFormat dm = new DecimalFormat("#.00");
        int percent = (int) (Double.valueOf(dm.format((double) this.progress / this.total)) * 100);
        progressBar.setProgress((int) (progressOfBytes / 1024));
        this.percent = percent;
        tvProgress.setText(Formatter.formatFileSize(mContext, this.progress) + "/" + Formatter
                .formatFileSize(mContext, this.total) + "(" + this.percent + "%)");

    }

    private void refreshSpeed() {
        this.tvSpeed.setText(Formatter.formatFileSize(mContext, this.speed) + "/s");
    }

}
