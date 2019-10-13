package com.lmgy.exportapk.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.lmgy.exportapk.Global;
import com.lmgy.exportapk.R;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.utils.EnvironmentUtils;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class ExportRuleDialog extends AlertDialog implements View.OnClickListener, DialogInterface.OnClickListener {

    private EditText editApk;
    private EditText editZip;
    private TextView preview;
    private Spinner spinner;

    private SharedPreferences settings;

    /**
     * 编辑导出规则的UI，确定后会保存至SharedPreferences中
     */
    @SuppressLint("InflateParams")
    public ExportRuleDialog(Context context) {
        super(context);

        settings = Global.getGlobalSharedPreferences(context);

        final View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_rule, null);
        editApk = dialogView.findViewById(R.id.filename_apk);
        editZip = dialogView.findViewById(R.id.filename_zip);
        preview = dialogView.findViewById(R.id.filename_preview);
        spinner = dialogView.findViewById(R.id.spinner_zip_level);

        editApk.setText(settings.getString(Constant.PREFERENCE_FILENAME_FONT_APK, Constant.PREFERENCE_FILENAME_FONT_DEFAULT));
        editZip.setText(settings.getString(Constant.PREFERENCE_FILENAME_FONT_ZIP, Constant.PREFERENCE_FILENAME_FONT_DEFAULT));
        preview.setText(getFormatedExportFileName(editApk.getText().toString(), editZip.getText().toString()));
        spinner.setAdapter(new ArrayAdapter<>(context, R.layout.item_spinner_single_text, R.id.spinner_text, new String[]{context.getResources().getString(R.string.zip_level_default),
                getContext().getResources().getString(R.string.zip_level_stored), context.getResources().getString(R.string.zip_level_low), context.getResources().getString(R.string.zip_level_normal)
                , getContext().getResources().getString(R.string.zip_level_high)}));

        int levelSet = settings.getInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.PREFERENCE_ZIP_COMPRESS_LEVEL_DEFAULT);
        try {
            switch (levelSet) {
                default:
                    spinner.setSelection(0);
                    break;
                case Constant.ZIP_LEVEL_STORED:
                    spinner.setSelection(1);
                    break;
                case Constant.ZIP_LEVEL_LOW:
                    spinner.setSelection(2);
                    break;
                case Constant.ZIP_LEVEL_NORMAL:
                    spinner.setSelection(3);
                    break;
                case Constant.ZIP_LEVEL_HIGH:
                    spinner.setSelection(4);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!editApk.getText().toString().contains(Constant.FONT_APP_NAME) && !editApk.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
            dialogView.findViewById(R.id.filename_apk_warn).setVisibility(View.VISIBLE);
        } else {
            dialogView.findViewById(R.id.filename_apk_warn).setVisibility(View.GONE);
        }

        if (!editZip.getText().toString().contains(Constant.FONT_APP_NAME) && !editZip.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
            dialogView.findViewById(R.id.filename_zip_warn).setVisibility(View.VISIBLE);
        } else {
            dialogView.findViewById(R.id.filename_zip_warn).setVisibility(View.GONE);
        }
        setTitle(context.getResources().getString(R.string.dialog_filename_title));
        setView(dialogView);
        setButton(AlertDialog.BUTTON_POSITIVE, context.getResources().getString(R.string.dialog_button_confirm), (DialogInterface.OnClickListener) null);
        setButton(AlertDialog.BUTTON_NEGATIVE, context.getResources().getString(R.string.dialog_button_cancel), this);

        editApk.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                preview.setText(getFormatedExportFileName(editApk.getText().toString(), editZip.getText().toString()));
                if (!editApk.getText().toString().contains(Constant.FONT_APP_NAME) && !editApk.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                        && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editApk.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
                    dialogView.findViewById(R.id.filename_apk_warn).setVisibility(View.VISIBLE);
                } else {
                    dialogView.findViewById(R.id.filename_apk_warn).setVisibility(View.GONE);
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
                // TODO Auto-generated method stub
                preview.setText(getFormatedExportFileName(editApk.getText().toString(), editZip.getText().toString()));
                if (!editZip.getText().toString().contains(Constant.FONT_APP_NAME) && !editZip.getText().toString().contains(Constant.FONT_APP_PACKAGE_NAME)
                        && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONCODE) && !editZip.getText().toString().contains(Constant.FONT_APP_VERSIONNAME)) {
                    dialogView.findViewById(R.id.filename_zip_warn).setVisibility(View.VISIBLE);
                } else {
                    dialogView.findViewById(R.id.filename_zip_warn).setVisibility(View.GONE);
                }
            }

        });

        dialogView.findViewById(R.id.filename_appname).setOnClickListener(this);
        dialogView.findViewById(R.id.filename_packagename).setOnClickListener(this);
        dialogView.findViewById(R.id.filename_version).setOnClickListener(this);
        dialogView.findViewById(R.id.filename_versioncode).setOnClickListener(this);
        dialogView.findViewById(R.id.filename_connector).setOnClickListener(this);
        dialogView.findViewById(R.id.filename_upderline).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.filename_appname: {
                if (editApk.isFocused()) {
                    editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_NAME);
                }
                if (editZip.isFocused()) {
                    editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_NAME);
                }
            }
            break;
            case R.id.filename_packagename: {
                if (editApk.isFocused()) {
                    editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_PACKAGE_NAME);
                }
                if (editZip.isFocused()) {
                    editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_PACKAGE_NAME);
                }
            }
            break;
            case R.id.filename_version: {
                if (editApk.isFocused()) {
                    editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_VERSIONNAME);
                }
                if (editZip.isFocused()) {
                    editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_VERSIONNAME);
                }
            }
            break;
            case R.id.filename_versioncode: {
                if (editApk.isFocused()) {
                    editApk.getText().insert(editApk.getSelectionStart(), Constant.FONT_APP_VERSIONCODE);
                }
                if (editZip.isFocused()) {
                    editZip.getText().insert(editZip.getSelectionStart(), Constant.FONT_APP_VERSIONCODE);
                }
            }
            break;
            case R.id.filename_connector: {
                if (editApk.isFocused()) {
                    editApk.getText().insert(editApk.getSelectionStart(), "-");
                }
                if (editZip.isFocused()) {
                    editZip.getText().insert(editZip.getSelectionStart(), "-");
                }
            }
            break;
            case R.id.filename_upderline: {
                if (editApk.isFocused()) {
                    editApk.getText().insert(editApk.getSelectionStart(), "_");
                }
                if (editZip.isFocused()) {
                    editZip.getText().insert(editZip.getSelectionStart(), "_");
                }
            }
            break;
        }
    }

    @Override
    public void show() {
        super.show();
        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            if ("".equals(editApk.getText().toString().trim()) || "".equals(editZip.getText().toString().trim())) {
                ToastManager.showToast(getContext(), getContext().getResources().getString(R.string.dialog_filename_toast_blank), Toast.LENGTH_SHORT);
                return;
            }

            String apkReplacedVariables = editApk.getText().toString().replace(Constant.FONT_APP_NAME, "").replace(Constant.FONT_APP_PACKAGE_NAME, "").replace(Constant.FONT_APP_VERSIONCODE, "").replace(Constant.FONT_APP_VERSIONNAME, "");
            String zipReplacedVariables = editZip.getText().toString().replace(Constant.FONT_APP_NAME, "").replace(Constant.FONT_APP_PACKAGE_NAME, "").replace(Constant.FONT_APP_VERSIONCODE, "").replace(Constant.FONT_APP_VERSIONNAME, "");
            if (!EnvironmentUtils.isALegalFileName(apkReplacedVariables) || !EnvironmentUtils.isALegalFileName(zipReplacedVariables)) {
                ToastManager.showToast(getContext(), getContext().getResources().getString(R.string.file_invalid_name), Toast.LENGTH_SHORT);
                return;
            }

            SharedPreferences.Editor editor = settings.edit();
            editor.putString(Constant.PREFERENCE_FILENAME_FONT_APK, editApk.getText().toString());
            editor.putString(Constant.PREFERENCE_FILENAME_FONT_ZIP, editZip.getText().toString());
            int zipSelection = spinner.getSelectedItemPosition();
            switch (zipSelection) {
                default:
                    break;
                case 0:
                    editor.putInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.PREFERENCE_ZIP_COMPRESS_LEVEL_DEFAULT);
                    break;
                case 1:
                    editor.putInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.ZIP_LEVEL_STORED);
                    break;
                case 2:
                    editor.putInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.ZIP_LEVEL_LOW);
                    break;
                case 3:
                    editor.putInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.ZIP_LEVEL_NORMAL);
                    break;
                case 4:
                    editor.putInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.ZIP_LEVEL_HIGH);
                    break;
            }
            editor.apply();
            cancel();
        });
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
    }

    private String getFormatedExportFileName(String apk, String zip) {
        final String PREVIEW_APPNAME = getContext().getResources().getString(R.string.dialog_filename_preview_appname);
        final String PREVIEW_PACKAGENAME = getContext().getResources().getString(R.string.dialog_filename_preview_packagename);
        final String PREVIEW_VERSION = getContext().getResources().getString(R.string.dialog_filename_preview_version);
        final String PREVIEW_VERSIONCODE = getContext().getResources().getString(R.string.dialog_filename_preview_versioncode);
        return getContext().getResources().getString(R.string.word_preview) + ":\n\nAPK:  " + apk.replace(Constant.FONT_APP_NAME, PREVIEW_APPNAME)
                .replace(Constant.FONT_APP_PACKAGE_NAME, PREVIEW_PACKAGENAME).replace(Constant.FONT_APP_VERSIONCODE, PREVIEW_VERSIONCODE).replace(Constant.FONT_APP_VERSIONNAME, PREVIEW_VERSION) + ".apk\n\n"
                + "ZIP:  " + zip.replace(Constant.FONT_APP_NAME, PREVIEW_APPNAME)
                .replace(Constant.FONT_APP_PACKAGE_NAME, PREVIEW_PACKAGENAME).replace(Constant.FONT_APP_VERSIONCODE, PREVIEW_VERSIONCODE).replace(Constant.FONT_APP_VERSIONNAME, PREVIEW_VERSION) + ".zip";
    }

}
