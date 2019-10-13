package com.lmgy.exportapk.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lmgy.exportapk.Global;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.config.Constant;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author lmgy
 * @date 2019/10/13
 */
public class ExportTask implements Runnable {

    private Context context;
    private final List<AppItemBean> list;
    private ExportProgressListener listener;

    private boolean isInterrupted = false;
    private long progress = 0;
    private long total = 0;
    private long progressCheckZip = 0;
    private long zipTime = 0;
    private long zipWriteLengthSecond = 0;

    private String currentWritePath = null;

    private final ArrayList<String> writePaths = new ArrayList<>();
    private final StringBuilder errorMessage = new StringBuilder();

    /**
     * 导出任务构造方法
     *
     * @param list     要导出的AppItem集合
     * @param callback 任务进度回调，在主UI线程
     */
    public ExportTask(@NonNull Context context, @NonNull List<AppItemBean> list, @Nullable ExportProgressListener callback) {
        super();
        this.context = context;
        this.list = list;
        this.listener = callback;
    }

    public void setExportProgressListener(ExportProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            //初始化导出路径
            File exportPath = new File(Global.getSavePath(context));
            if (exportPath.exists() && !exportPath.isDirectory()) {
                exportPath.delete();
            }
            if (!exportPath.exists()) {
                exportPath.mkdirs();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onExportTaskFinished(new ArrayList<>(), e.toString());
            }
            return;
        }

        total = getTotalLength();
        long progressCheckApk = 0;
        long bytesPerSecond = 0;
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < list.size(); i++) {
            if (isInterrupted) {
                return;
            }
            try {
                final AppItemBean item = list.get(i);
                final int orderThisLoop = i + 1;

                if (!item.exportData && !item.exportObb) {

                    this.currentWritePath = Global.getAbsoluteWritePath(context, item, "apk");
                    postCallback2Listener(() -> {
                        if (listener != null) {
                            listener.onExportAppItemStarted(orderThisLoop, item, list.size(), currentWritePath);
                        }
                    });

                    //读入原文件
                    InputStream in = new FileInputStream(String.valueOf(item.getSourcePath()));
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(this.currentWritePath));

                    int byteread;
                    byte[] buffer = new byte[1024 * 10];
                    while ((byteread = in.read(buffer)) != -1 && !this.isInterrupted) {
                        out.write(buffer, 0, byteread);
                        progress += byteread;
                        bytesPerSecond += byteread;
                        long endTime = System.currentTimeMillis();
                        if ((endTime - startTime) > 1000) {
                            startTime = endTime;
                            final long speed = bytesPerSecond;
                            bytesPerSecond = 0;
                            postCallback2Listener(() -> {
                                if (listener != null) {
                                    listener.onExportSpeedUpdated(speed);
                                }
                            });

                        }
                        //每写100K发送一次更新进度的Message
                        if ((progress - progressCheckApk) > 100 * 1024) {
                            progressCheckApk = progress;
                            postCallback2Listener(() -> {
                                if (listener != null) {
                                    listener.onExportProgressUpdated(progress, total, currentWritePath);
                                }
                            });
                        }
                    }
                    out.flush();
                    in.close();
                    out.close();
                    writePaths.add(this.currentWritePath);
                } else {
                    this.currentWritePath = Global.getAbsoluteWritePath(context, item, "zip");
                    postCallback2Listener(() -> {
                        if (listener != null) {
                            listener.onExportAppItemStarted(orderThisLoop, item, list.size(), currentWritePath);
                        }
                    });

                    ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File(this.currentWritePath))));
                    zos.setComment("Packaged by com.github.ghmxr.apkextractor \nhttps://github.com/ghmxr/apkextractor");
                    int zipLevel = Global.getGlobalSharedPreferences(context).getInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.PREFERENCE_ZIP_COMPRESS_LEVEL_DEFAULT);

                    if (zipLevel >= 0 && zipLevel <= 9) {
                        zos.setLevel(zipLevel);
                    }

                    writeZip(new File(String.valueOf(item.getSourcePath())), "", zos, zipLevel);
                    if (item.exportData) {
                        writeZip(new File(Storage.getMainExternalStoragePath() + "/android/data/" + item.getPackageName()), "Android/data/", zos, zipLevel);
                    }
                    if (item.exportObb) {
                        writeZip(new File(Storage.getMainExternalStoragePath() + "/android/obb/" + item.getPackageName()), "Android/obb/", zos, zipLevel);
                    }
                    zos.flush();
                    zos.close();
                    writePaths.add(currentWritePath);
                }

            } catch (Exception e) {
                this.errorMessage.append(e.toString());
                this.errorMessage.append("\n\n");
            }

        }

        if (isInterrupted) {
            try {
                File file = new File(this.currentWritePath);
                if (file.exists() && !file.isDirectory()) {
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        postCallback2Listener(() -> {
            if (listener != null && !isInterrupted) {
                listener.onExportTaskFinished(writePaths, errorMessage.toString());
            }
        });

    }


    private void postCallback2Listener(Runnable runnable) {
        if (listener == null) {
            return;
        }
        Global.HANDLER.post(runnable);
    }


    /**
     * 获取本次导出的总计长度
     *
     * @return 总长度，字节
     */
    private long getTotalLength() {
        long total = 0;
        for (AppItemBean item : list) {
            total += item.getSize();
            if (item.exportData) {
                total += FileUtils.getFileOrFolderSize(new File(Storage.getMainExternalStoragePath() + "/android/data/" + item.getPackageName()));
            }
            if (item.exportObb) {
                total += FileUtils.getFileOrFolderSize(new File(Storage.getMainExternalStoragePath() + "/android/obb/" + item.getPackageName()));
            }
        }
        return total;
    }

    /**
     * 将本Runnable停止，删除当前正在导出而未完成的文件，使线程返回
     */
    public void setInterrupted() {
        this.isInterrupted = true;
    }

    private void writeZip(final File file, String parent, ZipOutputStream zos, final int zipLevel) {
        if (file == null || parent == null || zos == null || isInterrupted) {
            return;
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                parent += file.getName() + File.separator;
                File[] files = file.listFiles();
                if (files.length > 0) {
                    for (File f : files) {
                        writeZip(f, parent, zos, zipLevel);
                    }
                } else {
                    try {
                        zos.putNextEntry(new ZipEntry(parent));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    FileInputStream in = new FileInputStream(file);
                    ZipEntry zipentry = new ZipEntry(parent + file.getName());

                    if (zipLevel == Constant.ZIP_LEVEL_STORED) {
                        zipentry.setMethod(ZipEntry.STORED);
                        zipentry.setCompressedSize(file.length());
                        zipentry.setSize(file.length());
                        zipentry.setCrc(FileUtils.getCRC32FromFile(file).getValue());
                    }

                    zos.putNextEntry(zipentry);
                    byte[] buffer = new byte[1024];
                    int length;
                    postCallback2Listener(() -> {
                        if (listener != null) {
                            listener.onExportZipProgressUpdated(file.getAbsolutePath());
                        }
                    });

                    while ((length = in.read(buffer)) != -1 && !isInterrupted) {
                        zos.write(buffer, 0, length);
                        this.progress += length;
                        this.zipWriteLengthSecond += length;
                        Long endTime = System.currentTimeMillis();
                        if (endTime - this.zipTime > 1000) {
                            this.zipTime = endTime;
                            final long zipSpeed = zipWriteLengthSecond;
                            postCallback2Listener(() -> {
                                if (listener != null) {
                                    listener.onExportSpeedUpdated(zipSpeed);
                                }
                            });
                            this.zipWriteLengthSecond = 0;
                        }
                        if (this.progress - progressCheckZip > 100 * 1024) {
                            progressCheckZip = this.progress;
                            postCallback2Listener(() -> {
                                if (listener != null) {
                                    listener.onExportProgressUpdated(progress, total, file.getAbsolutePath());
                                }
                            });
                        }

                    }
                    zos.flush();
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public interface ExportProgressListener {

        void onExportAppItemStarted(int order, AppItemBean item, int total, String writePath);

        void onExportProgressUpdated(long current, long total, String writePath);

        void onExportZipProgressUpdated(String writePath);

        void onExportSpeedUpdated(long speed);

        void onExportTaskFinished(List<String> writePaths, String errorMessage);

    }

}
