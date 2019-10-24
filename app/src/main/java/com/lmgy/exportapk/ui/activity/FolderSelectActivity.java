package com.lmgy.exportapk.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.adapter.FileListAdapter;
import com.lmgy.exportapk.base.BaseActivity;
import com.lmgy.exportapk.bean.FileItemBean;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.utils.SpUtils;
import com.lmgy.exportapk.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lmgy
 * @date 2019/10/19
 */
public class FolderSelectActivity extends BaseActivity {

    @BindView(R.id.folderselect_filelist)
    ListView mListView;

    @BindView(R.id.folderselect_swiperefreshlayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.folderselect_spinner)
    Spinner mSpinner;

    @BindView(R.id.folderselector_facearea)
    RelativeLayout mRlFace;

    @BindView(R.id.folderselector_refresharea)
    RelativeLayout mRlLoad;

    private File mPath;
    private FileListAdapter mAdapter;
    private List<FileItemBean> mFileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_folder_select;
    }

    @Override
    public void initView() {
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);
        mSwipeRefreshLayout.setDistanceToTriggerSync(100);
        mSwipeRefreshLayout.setProgressViewEndTarget(false, 200);

        initData();
    }

    private void initData() {
        mFileList = new ArrayList<>();
        mPath = new File(SpUtils.getSavePath());
        mAdapter = new FileListAdapter(mFileList, this);
        final String currentSelectedStoragePath = StorageUtils.getMainStoragePath();
        try {
            List<String> storages = StorageUtils.getAvailableStoragePaths();
            mSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner_text,
                    R.id.item_storage_text, storages));
            OUT:
            for (int i = 0; i < storages.size(); i++) {
                try {
                    if (mPath.getAbsolutePath().toLowerCase(Locale.getDefault()).trim().equals(storages.get(i)
                            .toLowerCase(Locale.getDefault()).trim())) {
                        mSpinner.setSelection(i);
                        break;
                    } else {
                        File file = new File(mPath.getAbsolutePath());
                        while ((file = file.getParentFile()) != null) {
                            if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).trim()
                                    .equals(storages.get(i).toLowerCase(Locale.getDefault()).trim())) {
                                mSpinner.setSelection(i);
                                break OUT;
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("Spinner", "position is " + position);
                    try {
                        if (currentSelectedStoragePath.toLowerCase(Locale.getDefault()).trim().equals(((String) mSpinner.getSelectedItem()).toLowerCase(Locale.getDefault()).trim())) {
                            mPath = new File((String) mSpinner.getSelectedItem());
                        }
                        refreshList(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        try {
            if (!mPath.exists()) {
                if (!mPath.mkdirs()) {
                    Toast.makeText(this, getResources().getString(R.string.activity_folder_selector_initial_failed), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        refreshList(true);
        mSwipeRefreshLayout.setOnRefreshListener(() -> refreshList(false));
    }

    private void clickActionNewFolder(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_newfolder, null);
        final AlertDialog newFolder = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.new_folder))
                .setIcon(R.drawable.ic_newfolder)
                .setView(dialogView)
                .setPositiveButton(getResources().getString(R.string.dialog_button_positive), null)
                .setNegativeButton(getResources().getString(R.string.dialog_button_negative), null)
                .create();
        newFolder.show();

        final EditText edittext = dialogView.findViewById(R.id.dialog_newfolder_edittext);
        newFolder.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            try {
                String folderName = edittext.getText().toString().trim();
                File newFile = new File(mPath.getAbsolutePath() + "/" + folderName);
                if (folderName.length() == 0) {
                    Toast.makeText(this, getResources().getString(R.string.activity_folder_selector_invalid_pathname), Toast.LENGTH_SHORT).show();
                } else if (folderName.contains("?") || folderName.contains("\\") || folderName.contains("/") || folderName.contains(":")
                        || folderName.contains("*") || folderName.contains("\"") || folderName.contains("<") || folderName.contains(">")
                        || folderName.contains("|")) {
                    Toast.makeText(this, getResources().getString(R.string.activity_folder_selector_invalid_foldername), Toast.LENGTH_SHORT).show();
                } else if (newFile.exists()) {
                    Toast.makeText(this, getResources().getString(R.string.activity_folder_selector_folder_already_exists) + folderName, Toast.LENGTH_SHORT).show();
                } else {
                    if (newFile.mkdirs()) {
                        refreshList(true);
                        newFolder.cancel();
                    } else {
                        Toast.makeText(this, "Make Dirs error", Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            }

        });
        newFolder.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> newFolder.cancel());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                break;
            case android.R.id.home:
                backToParent();
                break;
            case R.id.folderselect_action_confirm:
                String savePath = mPath.getAbsolutePath();
                SpUtils.setSavePath(savePath);
                Toast.makeText(this, getResources().getString(R.string.activity_folder_selector_saved_font) + savePath, Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.folderselect_action_cancel:
                finish();
                break;
            case R.id.folderselect_action_sort_ascending:
                FileItemBean.SortConfig = 1;
                if (mFileList != null) {
                    Collections.sort(mFileList);
                }
                if (mAdapter != null) {
                    mAdapter.setSelected(-1);
                }
                break;
            case R.id.folderselect_action_sort_descending:
                FileItemBean.SortConfig = 2;
                if (mFileList != null) {
                    Collections.sort(mFileList);
                }
                if (mAdapter != null) {
                    mAdapter.setSelected(-1);
                }
                break;
            case R.id.folderselect_action_newfolder:
                clickActionNewFolder();
                break;
            case R.id.folderselect_action_reset:
                SpUtils.setSavePath(Constant.PREFERENCE_SAVE_PATH_DEFAULT);
                Toast.makeText(this, "默认路径: " + Constant.PREFERENCE_SAVE_PATH_DEFAULT, Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.folderselect, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToParent();
            return true;
        }
        return false;
    }

    private void setInfoAtt(String att) {
        if (att.length() > 50) {
            att = "/..." + att.substring(att.length() - 50);
        }
        ((TextView) findViewById(R.id.folderselector_pathname)).setText(getResources().getString(R.string.activity_folder_selector_current) + att);
    }

    public void refreshList(boolean isShowProgressBar) {
        if (mListView != null) {
            mListView.setAdapter(null);
        }
        if (mRlFace != null) {
            mRlFace.setVisibility(View.GONE);
        }
        if (mRlLoad != null && isShowProgressBar) {
            mRlLoad.setVisibility(View.VISIBLE);
        }

        getObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FileItemBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<FileItemBean> fileItemBeans) {
                        mFileList = fileItemBeans;
                        setInfoAtt(mPath.getAbsolutePath());
                        mAdapter = new FileListAdapter(mFileList, getApplicationContext());
                        if (mListView != null) {
                            mListView.setAdapter(mAdapter);
                            mListView.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
                                mPath = mFileList.get(arg2).getFile();
                                setInfoAtt(mPath.getAbsolutePath());
                                refreshList(true);
                            });

                            mAdapter.setOnRadioButtonClickListener(position -> {
                                mPath = mFileList.get(position).getFile();
                                setInfoAtt(mPath.getAbsolutePath());
                                mAdapter.setSelected(position);
                            });

                            if (mRlFace != null) {
                                if (mFileList.size() <= 0) {
                                    mRlFace.setVisibility(View.VISIBLE);
                                } else {
                                    mRlFace.setVisibility(View.GONE);
                                }
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        if (mRlLoad != null) {
                            mRlLoad.setVisibility(View.GONE);
                        }
                        if (mSwipeRefreshLayout != null) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    private Observable<List<FileItemBean>> getObservable() {
        return Observable.create(emitter -> {
            List<FileItemBean> fileList = new ArrayList<>();
            try {
                if (mPath.isDirectory()) {
                    File[] files = mPath.listFiles();
                    if (files != null && files.length > 0) {
                        for (File file : files) {
                            if (file.isDirectory() && file.getName().indexOf(".") != 0) {
                                FileItemBean fileItem = new FileItemBean(file);
                                fileList.add(fileItem);
                            }
                        }
                        Collections.sort(fileList);
                    }
                }
                emitter.onNext(fileList);
                emitter.onComplete();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void backToParent() {
        try {
            File parent = mPath.getParentFile();
            if (parent == null || parent.getAbsolutePath().trim().length() < ((String) ((Spinner) findViewById(R.id.folderselect_spinner))
                    .getSelectedItem()).trim().length()) {
                finish();
            } else {
                mPath = parent;
                refreshList(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
