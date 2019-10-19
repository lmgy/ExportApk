package com.lmgy.exportapk.bean;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Locale;

/**
 * @author lmgy
 * @date 2019/10/19
 */
public class FileItemBean implements Comparable<FileItemBean> {

    public static int SortConfig = 1;

    private File file;

    public FileItemBean(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int compareTo(@NotNull FileItemBean fileItemBean) {
        int returnValue = 0;
        switch (SortConfig) {
            default:
                break;
            case 1:
                returnValue = file.getName().trim().toLowerCase(Locale.ENGLISH)
                        .compareTo(fileItemBean.file.getName().trim().toLowerCase(Locale.ENGLISH));
                break;
            case 2:
                returnValue = 0 - file.getName().trim().toLowerCase(Locale.ENGLISH)
                        .compareTo(fileItemBean.file.getName().trim().toLowerCase(Locale.ENGLISH));
                break;
        }
        return returnValue;
    }
}
