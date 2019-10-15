package com.lmgy.exportapk.utils

import com.lmgy.exportapk.base.BaseActivity
import com.lmgy.exportapk.bean.AppItemBean
import java.util.*

/**
 * @author lmgy
 * @date 2019/10/13
 */
class SearchTask(searchInfo: String) : Runnable {

    private val searchInfo: String
    private var ifCanSearch: Boolean = false
    private val listSearch: MutableList<AppItemBean>
    private val listSum: List<AppItemBean>

    init {
        this.searchInfo = searchInfo.trim { it <= ' ' }.toLowerCase(Locale.ENGLISH)
        this.ifCanSearch = true
        this.listSum = BaseActivity.listSum
        this.listSearch = ArrayList()
    }

    override fun run() {
        if (this.listSum != null) {
            listSearch.clear()
            if (searchInfo.isNotEmpty()) {
                for (i in this.listSum.indices) {

                    if (this.ifCanSearch) {
                        try {
                            if (this.listSum[i].appName!!.toLowerCase(Locale.ENGLISH).contains(searchInfo)
                                    || this.listSum[i].packageName!!.toLowerCase(Locale.ENGLISH).contains(searchInfo)
                                    || this.listSum[i].version.toLowerCase(Locale.ENGLISH).contains(searchInfo)
                                    || PinyinUtils.getFullSpell(this.listSum[i].appName!!).toLowerCase(Locale.ENGLISH).contains(searchInfo)
                                    || PinyinUtils.getFirstSpell(this.listSum[i].appName!!).toLowerCase(Locale.ENGLISH).contains(searchInfo)
                                    || PinyinUtils.getPinYin(this.listSum[i].appName!!).toLowerCase(Locale.ENGLISH).contains(searchInfo)) {
                                listSearch.add(this.listSum[i])
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        listSearch.clear()
                        break
                    }
                }
            } else {
                listSearch.clear()
            }
        } else {
            this.ifCanSearch = false
        }
        if (this.ifCanSearch) {
            BaseActivity.listSearch = this.listSearch
            BaseActivity.sendEmptyMessage(Main.MESSAGE_SEARCH_COMPLETE)
        }
    }

    fun setInterrupted() {
        this.ifCanSearch = false
    }

}
