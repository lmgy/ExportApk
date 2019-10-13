package com.lmgy.exportapk.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.lmgy.exportapk.R;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public abstract class ProgressDialog extends AlertDialog {

    ProgressBar progressBar;
    TextView att;
    TextView attLeft;
    TextView attRight;

    @SuppressLint("InflateParams")
    public ProgressDialog(@NonNull Context context, @NonNull String title) {
        super(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_with_progress, null);
        setView(dialogView);
        progressBar = dialogView.findViewById(R.id.dialog_progress_bar);
        att = dialogView.findViewById(R.id.dialog_att);
        attLeft = dialogView.findViewById(R.id.dialog_att_left);
        attRight = dialogView.findViewById(R.id.dialog_att_right);
        setTitle(title);
    }

}
