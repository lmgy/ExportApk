package com.lmgy.exportapk.utils

import java.io.File

/**
 * @author lmgy
 * @date 2019/10/13
 */
object FileUtils {

    fun getFileOrFolderSize(file: File?): Long {
        try {
            if (file == null || !file.exists()) {
                return 0
            }
            if (!file.isDirectory) {
                return file.length()
            } else {
                var total: Long = 0
                val files = file.listFiles()
                if (files == null || files.isEmpty()) {
                    return 0
                }
                for (f in files) {
                    total += getFileOrFolderSize(f)
                }
                return total
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0
    }


    fun getFileSize(filepath: String?): Long {
        return if (filepath != null) getFileOrFolderSize(File(filepath)) else 0
    }

    fun getFilesSize(filepaths: Array<String>?): Long {
        if (filepaths == null || filepaths.isEmpty()) {
            return 0
        } else {
            var total: Long = 0
            for (filepath in filepaths) {
                total += getFileSize(filepath)
            }
            return total
        }
    }


}
