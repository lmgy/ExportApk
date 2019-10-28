package com.lmgy.exportapk.mvp.contract;

import android.content.Context;

import com.lmgy.exportapk.bean.FileItemBean;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public interface FolderSelectContract {

    interface Model {
        Observable<List<FileItemBean>> getFileList(File mPath);
    }

    interface View {

        void setFileData(List<FileItemBean> fileItemBeans);

        void refreshList(boolean isShowProgressBar);

    }

    interface Presenter {

        void loadFileList(File mPath);

        void savePath(Context context, String path);

    }
}