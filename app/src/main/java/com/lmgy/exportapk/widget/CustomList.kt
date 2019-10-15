package com.lmgy.exportapk.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.BaseAdapter
import android.widget.ListView

/**
 * @author lmgy
 * @date 2019/10/16
 */
class CustomList : ListView {

    private var mSelfAdapter: BaseAdapter? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    /**
     * 删除ListView中上一次渲染的View，并添加新View。
     */
    private fun buildList() {
        if (childCount > 0) {
            removeAllViews()
        }

        val count = mSelfAdapter!!.count

        for (i in 0 until count) {
            val view = mSelfAdapter!!.getView(i, null, null)
            if (view != null) {
                addView(view, i)
            }
        }
    }

    fun getSelfAdapter(): BaseAdapter? {
        return mSelfAdapter
    }

    /**
     * 设置Adapter。
     *
     * @param selfAdapter
     */
    fun setSelfAdapter(selfAdapter: BaseAdapter) {
        this.mSelfAdapter = selfAdapter
        buildList()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val expandSpec = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, View.MeasureSpec.AT_MOST)
        super.onMeasure(widthMeasureSpec, expandSpec)
    }

}