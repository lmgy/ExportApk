package com.lmgy.exportapk.bean

import android.graphics.drawable.Drawable
import com.lmgy.exportapk.utils.PinyinUtils

/**
 * @author lmgy
 * @date 2019/10/15
 */
data class AppItemBean(
        var appName: String? = "",
        var packageName: String? = "",
        var icon: Drawable? = null,
        var appSize: Long = 0L,
        var path: String = "",
        var version: String = "",
        var versionCode: Int = 0,
        var lastUpdateTime: Long = 0L,
        var minSdkVersion: Int = 0,
        var exportData: Boolean = false,
        var exportObb: Boolean = false,
        var isSystemApp: Boolean = false

) : Comparable<AppItemBean> {

    override fun compareTo(other: AppItemBean): Int {
        var returnValue = 0
        when (sortConfig) {
            0 -> {
            }
            1 -> returnValue = PinyinUtils.getFirstSpell(appName).compareTo(PinyinUtils.getFirstSpell(other.appName))
            2 -> returnValue = 0 - PinyinUtils.getFirstSpell(appName).compareTo(PinyinUtils.getFirstSpell(other.appName))
            3 -> returnValue = java.lang.Long.valueOf(appSize).compareTo(other.appSize)
            4 -> returnValue = 0 - java.lang.Long.valueOf(appSize).compareTo(other.appSize)
            5 -> returnValue = java.lang.Long.valueOf(lastUpdateTime).compareTo(other.lastUpdateTime)
            6 -> returnValue = 0 - java.lang.Long.valueOf(lastUpdateTime).compareTo(other.lastUpdateTime)
            else -> {
            }
        }
        return returnValue
    }

    companion object {
        const val sortConfig = 0
    }
}