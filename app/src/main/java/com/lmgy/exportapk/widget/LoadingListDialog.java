package com.lmgy.exportapk.widget;

import android.content.Context;
import android.view.View;

import com.lmgy.exportapk.R;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class LoadingListDialog extends ProgressDialog {

    public LoadingListDialog(Context context) {
        super(context, context.getResources().getString(R.string.dialog_loading_title));
        att.setVisibility(View.GONE);
        progressBar.setMax(100);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setProgress(int progress, int total) {
        progressBar.setMax(total);
        if (progress > progressBar.getMax()) {
            return;
        }
        progressBar.setProgress(progress);
        attLeft.setText(progress + "/" + progressBar.getMax());
        attRight.setText((int) ((float) progress / total * 100) + "%");
    }

}
