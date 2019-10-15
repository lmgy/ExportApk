package com.lmgy.exportapk.bean

import java.io.File
import java.util.*

/**
 * @author lmgy
 * @date 2019/10/15
 */
data class FileItemBean(var file: File) : Comparable<FileItemBean> {

    override fun compareTo(other: FileItemBean): Int {
        var returnValue = 0
        when (sortConfig) {
            1 -> returnValue = file.name.trim { it <= ' ' }.toLowerCase(Locale.ENGLISH)
                    .compareTo(other.file.name.trim { it <= ' ' }.toLowerCase(Locale.ENGLISH))
            2 -> returnValue = 0 - file.name.trim { it <= ' ' }.toLowerCase(Locale.ENGLISH)
                    .compareTo(other.file.name.trim { it <= ' ' }.toLowerCase(Locale.ENGLISH))
            else -> {
            }
        }
        return returnValue
    }

    companion object {
        const val sortConfig = 1
    }
}