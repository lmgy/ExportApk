package com.lmgy.exportapk.mvp.presenter;

import android.content.Context;
import android.widget.Toast;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.base.BasePresenter;
import com.lmgy.exportapk.bean.FileItemBean;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.mvp.contract.FolderSelectContract;
import com.lmgy.exportapk.mvp.model.FolderSelectModel;
import com.lmgy.exportapk.utils.SpUtils;

import java.io.File;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public class FolderSelectPresenter extends BasePresenter<FolderSelectContract.View> implements FolderSelectContract.Presenter {

    private FolderSelectContract.Model model;

    public FolderSelectPresenter() {
        model = new FolderSelectModel();
    }

    @Override
    public void loadFileList(File mPath) {
        model.getFileList(mPath).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<FileItemBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<FileItemBean> fileItemBeans) {
                        mView.setFileData(fileItemBeans);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void savePath(Context context, String path) {
        SpUtils.setSavePath(path);
        if (path.equals(Constant.PREFERENCE_SAVE_PATH_DEFAULT)) {
            Toast.makeText(context, "默认路径: " + Constant.PREFERENCE_SAVE_PATH_DEFAULT, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.activity_folder_selector_saved_font) + path, Toast.LENGTH_SHORT).show();
        }
    }

}