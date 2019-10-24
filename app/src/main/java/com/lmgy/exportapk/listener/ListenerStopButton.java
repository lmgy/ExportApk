package com.lmgy.exportapk.listener;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

/**
 * @author lmgy
 * @date 2019/10/24
 */
public class ListenerStopButton {

    private DialogInterface.OnClickListener onClickListener;

    public ListenerStopButton(DialogInterface.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

}
