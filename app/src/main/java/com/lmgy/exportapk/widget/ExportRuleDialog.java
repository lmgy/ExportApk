package com.lmgy.exportapk.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.lmgy.exportapk.R;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.utils.SpUtils;

/**
 * @author lmgy
 * @date 2019/10/26
 */
public class ExportRuleDialog extends BottomSheetDialog {

    private EditText editApk;
    private EditText editZip;
    private TextView preview;
    private View dialogView;
    private LinearLayout apkWarn;
    private LinearLayout zipWarn;
    private Button appName;
    private Button packageName;
    private Button version;
    private Button versionCode;
    private Button connector;
    private Button upderLine;
    private ImageButton confirmButton;
    private ClickListener clickListener;

    private Context mContext;

    public ExportRuleDialog(@NonNull Context context, int theme) {
        super(context, theme);
        this.mContext = context;
        initView(context);
        initData();
        this.setCancelable(true);
        this.setCanceledOnTouchOutside(true);
    }

    private void initView(@NonNull Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        dialogView = layoutInflater.inflate(R.layout.dialog_filename, null);
        setContentView(dialogView);

        editApk = dialogView.findViewById(R.id.filename_apk);
        editZip = dialogView.findViewById(R.id.filename_zip);
        preview = dialogView.findViewById(R.id.filename_preview);
        apkWarn = dialogView.findViewById(R.id.filename_apk_warn);
        zipWarn = dialogView.findViewById(R.id.filename_zip_warn);
        appName = dialogView.findViewById(R.id.filename_appname);
        packageName = dialogView.findViewById(R.id.filename_packagename);
        version = dialogView.findViewById(R.id.filename_version);
        versionCode = dialogView.findViewById(R.id.filename_versioncode);
        connector = dialogView.findViewById(R.id.filename_connector);
        upderLine = dialogView.findViewById(R.id.filename_upderline);
        confirmButton = dialogView.findViewById(R.id.btn_confirm);

        editApk.setText(SpUtils.getFontApk());
        editZip.setText(SpUtils.getFontZip());
        preview.setText(getFormatExportFileName(editApk.getText().toString(), editZip.getText().toString()));

        if (!editApk.getText().toString().contains(Constant.FONT_APP_NAME) && !editApk.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
            apkWarn.setVisibility(View.VISIBLE);
        } else {
            apkWarn.setVisibility(View.GONE);
        }

        if (!editZip.getText().toString().contains(Constant.FONT_APP_NAME) && !editZip.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
            zipWarn.setVisibility(View.VISIBLE);
        } else {
            zipWarn.setVisibility(View.GONE);
        }
    }

    public interface ClickListener {
        void onClick(String editApk, String editZip);
    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    private void initData() {
        confirmButton.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(editApk.getText().toString(), editZip.getText().toString());
            }
        });
        editApk.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                preview.setText(getFormatExportFileName(editApk.getText().toString(), editZip.getText().toString()));
                if (!editApk.getText().toString().contains(Constant.FONT_APP_NAME) && !editApk.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                        && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
                    apkWarn.setVisibility(View.VISIBLE);
                } else {
                    apkWarn.setVisibility(View.GONE);
                }
            }

        });
        editZip.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                preview.setText(getFormatExportFileName(editApk.getText().toString(), editZip.getText().toString()));
                if (!editZip.getText().toString().contains(Constant.FONT_APP_NAME) && !editZip.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                        && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
                    zipWarn.setVisibility(View.VISIBLE);
                } else {
                    zipWarn.setVisibility(View.GONE);
                }
            }

        });

        appName.setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_NAME);
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_NAME);
            }
        });

        packageName.setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_PACKAGE_NAME);
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_PACKAGE_NAME);
            }
        });

        version.setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_VERSIONNAME);
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_VERSIONNAME);
            }
        });

        versionCode.setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_VERSIONCODE);
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_VERSIONCODE);
            }
        });

        connector.setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), "-");
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), "-");
            }
        });

        upderLine.setOnClickListener(v -> {
            if (editApk.isFocused()) {
                editApk.getText().insert(editApk.getSelectionStart(), "_");
            }
            if (editZip.isFocused()) {
                editZip.getText().insert(editZip.getSelectionStart(), "_");
            }
        });
    }

    private String getFormatExportFileName(String apk, String zip) {
        String PREVIEW_APP_NAME = mContext.getResources().getString(R.string.dialog_filename_preview_appname);
        String PREVIEW_PACKAGE_NAME = mContext.getResources().getString(R.string.dialog_filename_preview_packagename);
        String PREVIEW_VERSION = mContext.getResources().getString(R.string.dialog_filename_preview_version);
        String PREVIEW_VERSIONCODE = mContext.getResources().getString(R.string.dialog_filename_preview_versioncode);
        return mContext.getResources().getString(R.string.preview) + ":\n\nAPK:  " + apk.replace(Constant.FONT_APP_NAME, PREVIEW_APP_NAME)
                .replace(Constant.FONT_APP_PACKAGE_NAME, PREVIEW_PACKAGE_NAME).replace(Constant.FONT_APP_VERSIONCODE, PREVIEW_VERSIONCODE).replace(Constant.FONT_APP_VERSIONNAME, PREVIEW_VERSION) + ".apk\n\n"
                + "ZIP:  " + zip.replace(Constant.FONT_APP_NAME, PREVIEW_PACKAGE_NAME)
                .replace(Constant.FONT_APP_PACKAGE_NAME, PREVIEW_PACKAGE_NAME).replace(Constant.FONT_APP_VERSIONCODE, PREVIEW_VERSIONCODE).replace(Constant.FONT_APP_VERSIONNAME, PREVIEW_VERSION) + ".zip";
    }
}
