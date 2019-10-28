package com.lmgy.exportapk.mvp.model;

import com.lmgy.exportapk.bean.FileItemBean;
import com.lmgy.exportapk.mvp.contract.FolderSelectContract;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;

/**
 * @author lmgy
 * @date 2019/10/28
 */
public class FolderSelectModel implements FolderSelectContract.Model {


    @Override
    public Observable<List<FileItemBean>> getFileList(File mPath) {
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
}