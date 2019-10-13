package com.lmgy.exportapk.widget;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lmgy.exportapk.Global;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class ToastManager {

    private static Toast toast;

    public static void showToast(@NonNull final Context context, @NonNull final String content, final int length){
        Global.HANDLER.post(() -> {
            if(toast!=null){
                toast.cancel();
                toast=null;
            }
            toast=Toast.makeText(context,content,length);
            toast.show();
        });
    }

}
