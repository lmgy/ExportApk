package com.lmgy.exportapk.utils

import android.annotation.SuppressLint
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

/**
 * @author lmgy
 * @date 2019/10/13
 */
object StorageUtils {

    @SuppressLint("NewApi")
    fun getMainStorageAvailableSize(): Long {
        try {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val version: Int = Build.VERSION.SDK_INT
                val blockSize: Long = if (version >= 18) stat.blockSizeLong else stat.blockSize.toLong()
                val availableBlocks: Long = if (version >= 18) stat.availableBlocksLong else stat.availableBlocks.toLong()
                return blockSize * availableBlocks
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    fun getMainStoragePath(): String {

        try {
            if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                return Environment.getExternalStorageDirectory().toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }


    fun getAvailableStoragePaths(): List<String> {

        try {
            val paths = ArrayList<String>()
            val mainStorage = getMainStoragePath().toLowerCase(Locale.getDefault()).trim { it <= ' ' }
            paths.add(mainStorage)

            val runtime = Runtime.getRuntime()
            val process = runtime.exec("mount")
            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String
            while (reader.readLine().also { line = it } != null) {
                if (line.contains("proc") || line.contains("tmpfs") || line.contains("media") || line.contains("asec") || line.contains("secure") || line.contains("system") || line.contains("cache")
                        || line.contains("sys") || line.contains("data") || line.contains("shell") || line.contains("root") || line.contains("acct") || line.contains("misc") || line.contains("obb")) {
                    continue
                }
                if (line.contains("fat") || line.contains("fuse") || line.contains("ntfs")) {
                    val items = line.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (items.size > 1) {
                        val path = items[1].toLowerCase(Locale.getDefault())
                        if (path.toLowerCase(Locale.getDefault()).trim { it <= ' ' } != mainStorage) {
                            paths.add(path)
                        }
                    }
                }
            }
            Log.d("StoragePaths", paths.toTypedArray().contentToString())
            return paths
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ArrayList()
    }

    @SuppressLint("NewApi")
    fun getAvailableSizeOfPath(path: String): Long {
        try {
            val stat = StatFs(path)
            val version: Int = Build.VERSION.SDK_INT
            val blockSize: Long = if (version >= 18) stat.blockSizeLong else stat.blockSize.toLong()
            val availableBlocks: Long = if (version >= 18) stat.availableBlocksLong else stat.availableBlocks.toLong()
            return blockSize * availableBlocks
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0
    }

}
