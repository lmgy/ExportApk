package com.lmgy.exportapk.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.base.BaseActivity;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.config.Constant;
import com.lmgy.exportapk.widget.FileCopyDialog;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author lmgy
 * @date 2019/10/17
 */
public class CopyFilesUtils implements Runnable {

    private static final String TAG = "CopyFilesUtils";

    private Context mContext;
    private List<AppItemBean> appList;
    private String savePath = Constant.PREFERENCE_SAVE_PATH_DEFAULT;
    private String currentWritePath = null;
    private boolean isInterrupted;
    private long progress;
    private long total;
    private long progressCheck;
    private long zipTime;
    private long zipWriteLengthSecond;
    private FileCopyDialog fileCopyDialog;
    private String errorMessage;
    private boolean isExtractSuccess = true;


    private List<String> writePaths = new ArrayList<>();
    private Handler mHandler = new Handler(Looper.getMainLooper());


    public CopyFilesUtils(List<AppItemBean> list, Context context) {
        appList = list;
        this.mContext = context;
        fileCopyDialog = new FileCopyDialog(mContext);
        this.isInterrupted = false;
        File initialPath = new File(this.savePath);
        if (initialPath.exists() && !initialPath.isDirectory()) {
            initialPath.delete();
        }
        if (!initialPath.exists()) {
            initialPath.mkdirs();
        }
    }

    @Override
    public void run() {
        total = getTotalLength();
        long byteTemp = 0;
        long bytesPerSecond = 0;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < this.appList.size(); i++) {
            AppItemBean item = appList.get(i);
            if (!this.isInterrupted) {
                fileCopyDialog.setIcon(appList.get(i).icon);
                fileCopyDialog.setTitle(mContext.getResources().getString(R.string.activity_main_extracting_title) + (i + 1) + "/" + appList.size() + " " + appList.get(i).appName);

                if ((!item.exportData) && (!item.exportObb)) {
                    int byteRead;
                    try {
                        String writePath = FileUtils.getAbsoluteWritePath(mContext, item, "apk");
                        this.currentWritePath = writePath;
                        InputStream in = new FileInputStream(item.getPath());
                        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(writePath));
                        String sendPath = writePath;
                        if (sendPath.length() > 90) {
                            sendPath = "..." + sendPath.substring(sendPath.length() - 90);
                        }
                        String currentFile = mContext.getResources().getString(R.string.copytask_apk_current) + sendPath;
                        mHandler.post(() -> fileCopyDialog.setTextAtt(currentFile));

                        byte[] buffer = new byte[1024 * 10];
                        while ((byteRead = in.read(buffer)) != -1 && !this.isInterrupted) {
                            out.write(buffer, 0, byteRead);
                            progress += byteRead;
                            bytesPerSecond += byteRead;
                            long endTime = System.currentTimeMillis();
                            if ((endTime - startTime) > 1000) {
                                startTime = endTime;
                                long speed = bytesPerSecond;
                                bytesPerSecond = 0;
                                fileCopyDialog.setSpeed(speed);
                            }

                            if ((progress - byteTemp) > 100 * 1024) {
                                byteTemp = progress;
                                Long[] progressInfo = new Long[]{progress, total};
                                fileCopyDialog.setMax(progressInfo[1]);
                                fileCopyDialog.setProgress(progressInfo[0]);
                            }

                        }
                        out.flush();
                        in.close();
                        out.close();
                        writePaths.add(writePath);
                    } catch (Exception e) {
                        Log.e(TAG, "error 2" );
                        e.printStackTrace();
                        try {
                            File file = new File(this.currentWritePath);
                            if (file.exists() && !file.isDirectory()) {
                                file.delete();
                            }
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                        progress += item.getAppSize();
                        String filename = item.getAppName() + " " + item.getVersion();

                        isExtractSuccess = false;
                        errorMessage += filename + "\nError Message:" + e.toString() + "\n\n";
                    }

                } else {
                    try {
                        String writePath = FileUtils.getAbsoluteWritePath(mContext, item, "zip");
                        this.currentWritePath = writePath;
                        ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(new File(writePath))));
                        zos.setComment("Packaged by lmgy");
                        int zipLevel = mContext.getSharedPreferences(Constant.PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.PREFERENCE_ZIP_COMPRESS_LEVEL_DEFAULT);

                        if (zipLevel >= 0 && zipLevel <= 9) {
                            zos.setLevel(zipLevel);
                        }

                        writeZip(new File(item.getPath()), "", zos, zipLevel);
                        if (item.exportData) {
                            writeZip(new File(StorageUtils.getMainStoragePath() + "/android/data/" + item.packageName), "Android/data/", zos, zipLevel);
                        }
                        if (item.exportObb) {
                            writeZip(new File(StorageUtils.getMainStoragePath() + "/android/obb/" + item.packageName), "Android/obb/", zos, zipLevel);
                        }
                        zos.flush();
                        zos.close();
                        writePaths.add(writePath);
                    } catch (Exception e) {
                        Log.e(TAG, "error 3" );
                        e.printStackTrace();
                        try {
                            File file = new File(this.currentWritePath);
                            if (file.exists() && !file.isDirectory()) {
                                file.delete();
                            }
                        } catch (Exception ee) {
                            ee.printStackTrace();
                        }
                        String filename = item.getAppName() + " " + item.getVersion();
                        Log.e(TAG, "run: 2"  );
                        isExtractSuccess = false;
                        errorMessage += filename + "\nError Message:" + e.toString() + "\n\n";

                    }
                    if (isInterrupted) {
                        new File(this.currentWritePath).delete();
                    }
                }
            } else {
                break;
            }

        }

        if (!this.isInterrupted) {
            if (fileCopyDialog != null) {
                fileCopyDialog.dismiss();
            }

            if (isExtractSuccess) {
                mHandler.post(() -> Toast.makeText(mContext, mContext.getResources().getString(R.string.activity_main_complete) + savePath, Toast.LENGTH_LONG).show());
            }

            if (!isExtractSuccess) {
                mHandler.post(() -> new AlertDialog.Builder(mContext).setTitle(mContext.getResources().getString(R.string.attention))
                        .setIcon(R.drawable.ic_icon_warn)
                        .setMessage(mContext.getResources().getString(R.string.activity_main_exception_head) + errorMessage + mContext.getResources().getString(R.string.activity_main_exception_end))
                        .setPositiveButton(mContext.getResources().getString(R.string.dialog_button_positive), (dialog, which) -> {
                        })
                        .show());
            }
            isExtractSuccess = true;
            errorMessage = "";

            List<String> paths = writePaths;
            Intent i = new Intent();
            i.setType("application/x-zip-compressed");
            if (paths.size() == 1) {
                i.setAction(Intent.ACTION_SEND);
                Uri uri = Uri.fromFile(new File(paths.get(0)));
                i.putExtra(Intent.EXTRA_STREAM, uri);
            } else {
                i.setAction(Intent.ACTION_SEND_MULTIPLE);
                ArrayList<Uri> uris = new ArrayList<>();
                for (int n = 0; n < paths.size(); n++) {
                    uris.add(Uri.fromFile(new File(paths.get(n))));
                }
                i.putExtra(Intent.EXTRA_STREAM, uris);
            }
            i.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.share));
            i.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.share));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(Intent.createChooser(i, mContext.getResources().getString(R.string.share)));

        }
    }

    private void writeZip(File file, String parent, ZipOutputStream zos, int zipLevel) {
        if (file == null || parent == null || zos == null || isInterrupted) {
            return;
        }
        if (file.exists()) {
            if (file.isDirectory()) {
                parent += file.getName() + File.separator;
                File files[] = file.listFiles();
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
                        zipentry.setCrc(getCRC32FromFile(file).getValue());
                    }

                    zos.putNextEntry(zipentry);
                    byte[] buffer = new byte[1024];
                    int length;


                    String currentPath = file.getAbsolutePath();
                    if (currentPath.length() > 90) {
                        currentPath = "..." + currentPath.substring(currentPath.length() - 90);
                    }

                    String currentFile = mContext.getResources().getString(R.string.copytask_zip_current) + currentPath;
                    if (fileCopyDialog != null) {
                        ((TextView) fileCopyDialog.findViewById(R.id.currentfile)).setText(currentFile);
                    }

                    while ((length = in.read(buffer)) != -1 && !isInterrupted) {
                        zos.write(buffer, 0, length);
                        this.progress += length;
                        this.zipWriteLengthSecond += length;
                        Long endTime = System.currentTimeMillis();
                        if (endTime - this.zipTime > 1000) {
                            this.zipTime = endTime;
                            long speed = zipWriteLengthSecond;
                            if (fileCopyDialog != null) {
                                fileCopyDialog.setSpeed(speed);
                            }
                            this.zipWriteLengthSecond = 0;
                        }
                        if (this.progress - progressCheck > 100 * 1024) {
                            progressCheck = this.progress;
                            Long[] progress = new Long[]{this.progress, this.total};
                            if (fileCopyDialog != null) {
                                fileCopyDialog.setMax(progress[1]);
                                fileCopyDialog.setProgress(progress[0]);
                            }
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

    private long getTotalLength() {
        long total = 0;
        for (AppItemBean item : appList) {
            total += item.appSize;
            if (item.exportData) {
                total += FileUtils.getFileOrFolderSize(new File(StorageUtils.getMainStoragePath() + "/android/data/" + item.packageName));
            }
            if (item.exportObb) {
                total += FileUtils.getFileOrFolderSize(new File(StorageUtils.getMainStoragePath() + "/android/obb/" + item.packageName));
            }
        }
        return total;
    }

    public void setInterrupted() {
        this.isInterrupted = true;
        try {
            File file = new File(this.currentWritePath);
            if (file.exists() && !file.isDirectory()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static CRC32 getCRC32FromFile(File file) throws Exception {
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file.getAbsolutePath()));
        CRC32 crc = new CRC32();
        byte[] bytes = new byte[1024];
        int cnt;
        while ((cnt = inputStream.read(bytes)) != -1) {
            crc.update(bytes, 0, cnt);
        }
        inputStream.close();
        return crc;
    }

}
