package com.lmgy.exportapk.mvp.ui.activity;

import android.app.AlertDialog;
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
import com.lmgy.exportapk.base.BaseMvpActivity;
import com.lmgy.exportapk.bean.FileItemBean;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.mvp.contract.FolderSelectContract;
import com.lmgy.exportapk.mvp.presenter.FolderSelectPresenter;
import com.lmgy.exportapk.utils.SpUtils;
import com.lmgy.exportapk.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;

/**
 * @author lmgy
 * @date 2019/10/19
 */
public class FolderSelectActivity extends BaseMvpActivity<FolderSelectPresenter> implements FolderSelectContract.View {

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
    @BindView(R.id.folderselector_pathname)
    TextView pathName;

    private File mPath;
    private FileListAdapter mAdapter;
    private List<FileItemBean> mFileList;

    @Override
    public int getLayoutId() {
        return R.layout.activity_folder_select;
    }

    @Override
    public void initView() {
        mPresenter = new FolderSelectPresenter();
        mPresenter.attachView(this);
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
            List<String> storage = StorageUtils.getAvailableStoragePaths();
            mSpinner.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner_text,
                    R.id.item_storage_text, storage));
            OUT:
            for (int i = 0; i < storage.size(); i++) {
                try {
                    if (mPath.getAbsolutePath().toLowerCase(Locale.getDefault()).trim().equals(storage.get(i)
                            .toLowerCase(Locale.getDefault()).trim())) {
                        mSpinner.setSelection(i);
                        break;
                    } else {
                        File file = new File(mPath.getAbsolutePath());
                        while ((file = file.getParentFile()) != null) {
                            if (file.getAbsolutePath().toLowerCase(Locale.getDefault()).trim()
                                    .equals(storage.get(i).toLowerCase(Locale.getDefault()).trim())) {
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
                    try {
                        if (currentSelectedStoragePath.toLowerCase(Locale.getDefault()).trim().equals(((String) mSpinner.getSelectedItem()).toLowerCase(Locale.getDefault()).trim())) {
                            mPath = new File((String) mSpinner.getSelectedItem());
                        }
                        refreshList(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (!mPath.exists() && !mPath.mkdirs()) {
                Toast.makeText(this, getResources().getString(R.string.activity_folder_selector_initial_failed), Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshList(true);
        mSwipeRefreshLayout.setOnRefreshListener(() -> refreshList(false));
    }

    private void clickActionNewFolder() {
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
        EditText edittext = dialogView.findViewById(R.id.dialog_newfolder_edittext);
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
                mPresenter.savePath(this, savePath);
                finish();
                break;
            case R.id.folderselect_action_cancel:
                finish();
                break;
            case R.id.folderselect_action_sort_ascending:
                FileItemBean.SortConfig = 1;
                Collections.sort(mFileList);
                mAdapter.setSelected(-1);
                break;
            case R.id.folderselect_action_sort_descending:
                FileItemBean.SortConfig = 2;
                Collections.sort(mFileList);
                mAdapter.setSelected(-1);
                break;
            case R.id.folderselect_action_newfolder:
                clickActionNewFolder();
                break;
            case R.id.folderselect_action_reset:
                mPresenter.savePath(this, Constant.PREFERENCE_SAVE_PATH_DEFAULT);
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
        pathName.setText(getResources().getString(R.string.activity_folder_selector_current) + att);
    }

    @Override
    public void refreshList(boolean isShowProgressBar) {
        mListView.setAdapter(null);
        mRlFace.setVisibility(View.GONE);
        if (isShowProgressBar) {
            mRlLoad.setVisibility(View.VISIBLE);
        }
        mPresenter.loadFileList(mPath);
    }

    public void backToParent() {
        try {
            File parent = mPath.getParentFile();
            if (parent == null || parent.getAbsolutePath().trim().length() < ((String) mSpinner.getSelectedItem()).trim().length()) {
                finish();
            } else {
                mPath = parent;
                refreshList(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setFileData(List<FileItemBean> fileItemBeans) {
        mFileList = fileItemBeans;
        setInfoAtt(mPath.getAbsolutePath());
        mAdapter = new FileListAdapter(mFileList, getApplicationContext());
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

        if (mFileList.size() <= 0) {
            mRlFace.setVisibility(View.VISIBLE);
        } else {
            mRlFace.setVisibility(View.GONE);
        }
        mRlLoad.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}