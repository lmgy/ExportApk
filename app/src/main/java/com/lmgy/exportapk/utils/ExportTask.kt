package com.lmgy.exportapk.utils

import android.content.Context
import android.os.Message
import android.telephony.mbms.FileInfo

import com.lmgy.exportapk.Global
import com.lmgy.exportapk.R
import com.lmgy.exportapk.base.BaseActivity
import com.lmgy.exportapk.bean.AppItemBean
import com.lmgy.exportapk.config.Constant

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.zip.CRC32
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

/**
 * @author lmgy
 * @date 2019/10/13
 */
class ExportTask(var appList: List<AppItemBean>, private val context: Context) : Runnable {
    private val savePath = BaseActivity.savePath
    private var currentWritePath: String? = null
    private var isInterrupted = false
    private var progress: Long = 0
    private var total: Long = 0
    private var progressCheck: Long = 0
    private var zipTime: Long = 0
    private var zipWriteLengthSecond: Long = 0

    private val writePaths = ArrayList<String>()

    private val totalLength: Long
        get() {
            var total: Long = 0
            for ((_, packageName, _, appSize, _, _, _, _, _, exportData, exportObb) in appList) {
                total += appSize
                if (exportData) {
                    total += FileSize.getFileOrFolderSize(File(StorageUtils.getMainStoragePath() + "/android/data/" + packageName))
                }
                if (exportObb) {
                    total += FileSize.getFileOrFolderSize(File(StorageUtils.getMainStoragePath() + "/android/obb/" + packageName))
                }
            }
            return total
        }

    init {
        this.isInterrupted = false
        val initialPath = File(this.savePath)
        if (initialPath.exists() && !initialPath.isDirectory()) {
            initialPath.delete()
        }
        if (!initialPath.exists()) {
            initialPath.mkdirs()
        }
    }

    override fun run() {
        total = totalLength
        var bytetemp: Long = 0
        var bytesPerSecond: Long = 0
        var startTime = System.currentTimeMillis()
        for (i in this.appList.indices) {
            val item = appList[i]
            if (!this.isInterrupted) {
                val msgCurrentApp = Message()
                msgCurrentApp.what = Main.MESSAGE_COPYFILE_CURRENTAPP
                msgCurrentApp.obj = Integer.valueOf(i)
                Main.sendMessage(msgCurrentApp)
                if (!item.exportData && !item.exportObb) {
                    var byteread: Int
                    try {
                        val writePath = FileUtils.getAbsoluteWritePath(context, item, "apk")
                        this.currentWritePath = writePath
                        val `in` = FileInputStream(item.getResourcePath())
                        val out = BufferedOutputStream(FileOutputStream(writePath))
                        val msgCurrentfile = Message()
                        msgCurrentfile.what = BaseActivity.MESSAGE_COPYFILE_CURRENTFILE
                        var sendPath = writePath
                        if (sendPath.length > 90) {
                            sendPath = "..." + sendPath.substring(sendPath.length - 90)
                        }
                        msgCurrentfile.obj = context.resources.getString(R.string.copytask_apk_current) + sendPath
                        BaseActivity.sendMessage(msgCurrentfile)

                        val buffer = ByteArray(1024 * 10)
                        while ((byteread = `in`.read(buffer)) != -1 && !this.isInterrupted) {
                            out.write(buffer, 0, byteread)
                            progress += byteread.toLong()
                            bytesPerSecond += byteread.toLong()
                            val endTime = System.currentTimeMillis()
                            if (endTime - startTime > 1000) {
                                startTime = endTime
                                val speed = java.lang.Long.valueOf(bytesPerSecond)
                                bytesPerSecond = 0
                                val msgSpeed = Message()
                                msgSpeed.what = BaseActivity.MESSAGE_COPYFILE_REFRESH_SPEED
                                msgSpeed.obj = speed
                                BaseActivity.sendMessage(msgSpeed)
                            }

                            if (progress - bytetemp > 100 * 1024) {
                                bytetemp = progress
                                val msgProgress = Message()
                                val progressinfo = arrayOf(java.lang.Long.valueOf(progress), java.lang.Long.valueOf(total))
                                msgProgress.what = BaseActivity.MESSAGE_COPYFILE_REFRESH_PROGRESS
                                msgProgress.obj = progressinfo
                                BaseActivity.sendMessage(msgProgress)
                            }

                        }
                        out.flush()
                        `in`.close()
                        out.close()
                        writePaths.add(writepath)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        try {
                            val file = File(this.currentWritePath!!)
                            if (file.exists() && !file.isDirectory) {
                                file.delete()
                            }
                        } catch (ee: Exception) {
                            ee.printStackTrace()
                        }

                        progress += item.appSize
                        val msgException = Message()
                        val filename = item.appName + " " + item.version
                        msgException.what = BaseActivity.MESSAGE_COPYFILE_FILE_NOTFOUND_EXCEPTION
                        msgException.obj = "$filename\nError Message:$e"
                        BaseActivity.sendMessage(msgException)
                    }

                } else {
                    try {
                        val writePath = FileInfo.getAbsoluteWritePath(context, item, "zip")
                        this.currentWritePath = writePath
                        val zos = ZipOutputStream(BufferedOutputStream(FileOutputStream(File(writePath))))
                        zos.setComment("Packaged by lmgy")
                        val zipLevel = context.getSharedPreferences(Constant.PREFERENCE_NAME, Context.MODE_PRIVATE).getInt(Constant.PREFERENCE_ZIP_COMPRESS_LEVEL, Constant.PREFERENCE_ZIP_COMPRESS_LEVEL_DEFAULT)

                        if (zipLevel in 0..9) {
                            zos.setLevel(zipLevel)
                        }

                        writeZip(File(item.getResourcePath()), "", zos, zipLevel)
                        if (item.exportData) {
                            writeZip(File(StorageUtils.getMainStoragePath() + "/android/data/" + item.packageName), "Android/data/", zos, zip_level)
                        }
                        if (item.exportObb) {
                            writeZip(File(StorageUtils.getMainStoragePath() + "/android/obb/" + item.packageName), "Android/obb/", zos, zip_level)
                        }
                        zos.flush()
                        zos.close()
                        writePaths.add(writePath)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        try {
                            val file = File(this.currentWritePath!!)
                            if (file.exists() && !file.isDirectory) {
                                file.delete()
                            }
                        } catch (ee: Exception) {
                            ee.printStackTrace()
                        }

                        val msgFileNotFoundException = Message()
                        val filename = item.appName + " " + item.version
                        msgFileNotFoundException.what = BaseActivity.MESSAGE_COPYFILE_FILE_NOTFOUND_EXCEPTION
                        msgFileNotFoundException.obj = "$filename\nError Message:$e"
                        BaseActivity.sendMessage(msgFileNotFoundException)
                    }

                    if (isInterrupted) {
                        File(this.currentWritePath!!).delete()
                    }
                }
            } else {
                break
            }

        }

        if (!this.isInterrupted) {
            val msg = Message()
            msg.what = BaseActivity.MESSAGE_COPYFILE_COMPLETE
            msg.obj = writePaths
            BaseActivity.sendMessage(msg)
        }

    }

    private fun writeZip(file: File?, parent: String?, zos: ZipOutputStream?, zipLevel: Int) {
        var parent = parent
        if (file == null || parent == null || zos == null || isInterrupted) return
        if (file.exists()) {
            if (file.isDirectory) {
                parent += file.name + File.separator
                val files: Array<File> = file.listFiles()!!
                if (files.isNotEmpty()) {
                    for (f in files) {
                        writeZip(f, parent, zos, zipLevel)
                    }
                } else {
                    try {
                        zos.putNextEntry(ZipEntry(parent))
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }
            } else {
                try {
                    val input = FileInputStream(file)
                    val zipentry = ZipEntry(parent + file.name)
                    if (zipLevel == Constant.ZIP_LEVEL_STORED) {
                        zipentry.method = ZipEntry.STORED
                        zipentry.compressedSize = file.length()
                        zipentry.size = file.length()
                        zipentry.crc = getCRC32FromFile(file).value
                    }

                    zos.putNextEntry(zipentry)
                    val buffer = ByteArray(1024)
                    var length: Int

                    val msgCurrentFile = Message()
                    msgCurrentFile.what = BaseActivity.MESSAGE_COPYFILE_CURRENTFILE
                    var currentPath = file.absolutePath
                    if (currentPath.length > 90) {
                        currentPath = "..." + currentPath.substring(currentPath.length - 90)
                    }
                    msgCurrentFile.obj = context.resources.getString(R.string.copytask_zip_current) + currentPath
                    BaseActivity.sendMessage(msgCurrentFile)

                    while (input.read(buffer).also { length = it } != -1 && !isInterrupted) {
                        zos.write(buffer, 0, length)
                        this.progress += length.toLong()
                        this.zipWriteLengthSecond += length.toLong()
                        val endTime = System.currentTimeMillis()
                        if (endTime - this.zipTime > 1000) {
                            this.zipTime = endTime
                            val msgSpeed = Message()
                            msgSpeed.what = BaseActivity.MESSAGE_COPYFILE_REFRESH_SPEED
                            msgSpeed.obj = this.zipWriteLengthSecond
                            BaseActivity.sendMessage(msgSpeed)
                            this.zipWriteLengthSecond = 0
                        }
                        if (this.progress - progressCheck > 100 * 1024) {
                            progressCheck = this.progress
                            val msg = Message()
                            msg.what = Main.MESSAGE_COPYFILE_REFRESH_PROGRESS
                            msg.obj = arrayOf(this.progress, this.total)
                            BaseActivity.sendMessage(msg)
                        }

                    }
                    zos.flush()
                    input.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }

    fun setInterrupted() {
        this.isInterrupted = true
        try {
            val file = File(this.currentWritePath!!)
            if (file.exists() && !file.isDirectory) {
                file.delete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    companion object {

        @Throws(Exception::class)
        fun getCRC32FromFile(file: File): CRC32 {
            val inputStream = BufferedInputStream(FileInputStream(file.absolutePath))
            val crc = CRC32()
            val bytes = ByteArray(1024)
            var cnt: Int
            while ((cnt = inputStream.read(bytes)) != -1) {
                crc.update(bytes, 0, cnt)
            }
            inputStream.close()
            return crc
        }
    }

}
