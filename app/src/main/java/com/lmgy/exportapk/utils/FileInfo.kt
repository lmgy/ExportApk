package com.lmgy.exportapk.utils

import android.app.Activity
import android.content.Context
import com.lmgy.exportapk.bean.AppItemBean
import com.lmgy.exportapk.config.Constant
import java.io.File
import java.util.*

/**
 * @author lmgy
 * @date 2019/10/16
 */
object FileInfo {

    fun getDuplicateFileInfo(context: Context, items: List<AppItemBean>, extension: String): String {
        try {
            var result = ""
            for (item in items) {
                val file = File(getAbsoluteWritePath(context, item, extension))
                if (file.exists() && !file.isDirectory) {
                    result += file.absolutePath
                    result += "\n\n"
                }
            }
            return result
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * 返回此项的绝对写入路径
     *
     * @param item      应用程序信息
     * @param extension 必须是“apk”或“zip”，否则此方法将返回空字符串
     * @return 此AppItemInfo的绝对写入路径
     */
    fun getAbsoluteWritePath(context: Context, item: AppItemBean, extension: String): String {
        try {
            val settings = context.getSharedPreferences(Constant.PREFERENCE_NAME, Activity.MODE_PRIVATE)
            if (extension.toLowerCase(Locale.ENGLISH) == "apk") {
                return Constant.PREFERENCE_SAVE_PATH_DEFAULT + "/" + settings.getString(Constant.PREFERENCE_FILENAME_FONT_APK,
                        Constant.PREFERENCE_FILENAME_FONT_DEFAULT)!!.replace(Constant.FONT_APP_NAME, item.appName.toString())
                        .replace(Constant.FONT_APP_PACKAGE_NAME, item.packageName.toString())
                        .replace(Constant.FONT_APP_VERSIONCODE, item.versionCode.toString())
                        .replace(Constant.FONT_APP_VERSIONNAME, item.version) + ".apk"
            }
            if (extension.toLowerCase(Locale.ENGLISH) == "zip") {
                return Constant.PREFERENCE_SAVE_PATH_DEFAULT + "/" + settings.getString(Constant.PREFERENCE_FILENAME_FONT_ZIP,
                        Constant.PREFERENCE_FILENAME_FONT_DEFAULT)!!.replace(Constant.FONT_APP_NAME, item.appName.toString())
                        .replace(Constant.FONT_APP_PACKAGE_NAME, item.packageName.toString())
                        .replace(Constant.FONT_APP_VERSIONCODE, item.versionCode.toString())
                        .replace(Constant.FONT_APP_VERSIONNAME, item.version) + ".zip"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }
}