package com.lmgy.exportapk.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lmgy.exportapk.R;

import java.text.DecimalFormat;

/**
 * @author lmgy
 * @date 2019/10/16
 */
public class LoadListDialog extends AlertDialog {

    private ProgressBar progressBar;
    private TextView textviewPercent;
    private TextView textviewProgress;
    private int progress = 0;
    private int max = 0;

    public LoadListDialog(Context context) {
        super(context);
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View dialogView = layoutInflater.inflate(R.layout.dialog_loadlist, null);
        this.setView(dialogView);
        this.progressBar = dialogView.findViewById(R.id.dialog_loadlist_pgbar);
        this.textviewPercent = dialogView.findViewById(R.id.dialog_loadlist_textview_percent);
        this.textviewProgress = dialogView.findViewById(R.id.dialog_loadlist_textview_progress);
    }

    public void setMax(int max) {
        this.max = max;
        this.progressBar.setMax(max);
    }

    public void setProgress(int progress) {
        this.progress = progress;
        refreshProgress();
    }

    private void refreshProgress() {
        DecimalFormat dm = new DecimalFormat("#.00");
        int percent = (int) (Double.valueOf(dm.format((double) this.progress / this.max)) * 100);
        this.progressBar.setProgress(this.progress);
        this.textviewPercent.setText(percent + "%");
        this.textviewProgress.setText(this.progress + "/" + this.max);
    }

}
