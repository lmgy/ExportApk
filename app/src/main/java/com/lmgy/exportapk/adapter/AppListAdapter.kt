package com.lmgy.exportapk.adapter

import android.content.Context
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lmgy.exportapk.R
import com.lmgy.exportapk.bean.AppItemBean

/**
 * @author lmgy
 * @date 2019/10/15
 */
class AppListAdapter : RecyclerView.Adapter<AppListAdapter.MyViewHolder> {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_item_applist, parent, false))
    }

    override fun getItemCount(): Int {
        return appList.size
    }


    private var mItemClickListener: OnItemClickListener? = null
    private var mLongClickListener: OnLongClickListener? = null

    private var mContext: Context
    private var appList: MutableList<AppItemBean>
    private var isMultiSelectMode = false
    private var isSelected: BooleanArray
    private var ifShowedAnim: BooleanArray
    private var ifAnim: Boolean

    constructor(context: Context, appList: MutableList<AppItemBean>, ifAnim: Boolean) {
        this.mContext = context
        this.appList = appList
        this.isSelected = BooleanArray(appList.size)
        this.ifShowedAnim = BooleanArray(appList.size)
        this.ifAnim = ifAnim
    }


    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    interface OnLongClickListener {
        fun onLongClick(position: Int): Boolean
    }

    fun setItemClickListener(itemClickListener: OnItemClickListener) {
        mItemClickListener = itemClickListener
    }

    fun setLongClickListener(longClickListener: OnLongClickListener) {
        mLongClickListener = longClickListener
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = appList[position]
        holder.icon.setImageDrawable(item.icon)
        holder.label.text = item.appName + "(" + item.version + ")"
        holder.packageName.text = item.packageName
        if (item.isSystemApp) {
            holder.label.setTextColor(mContext.resources.getColor(R.color.color_text_darkred))
        } else {
            holder.label.setTextColor(mContext.resources.getColor(R.color.color_text_black))
        }
        holder.appSize.text = Formatter.formatFileSize(mContext, item.appSize)
        if (this.isMultiSelectMode) {
            if (position < this.isSelected.size) {
                holder.select.isChecked = this.isSelected[position]
            }
            holder.select.visibility = View.VISIBLE
            holder.appSize.visibility = View.GONE
        } else {
            holder.select.visibility = View.GONE
            holder.appSize.visibility = View.VISIBLE
        }

        //设置点击和长按事件
        holder.itemView.setOnClickListener { mItemClickListener?.onItemClick(position) }
        holder.itemView.setOnLongClickListener { mLongClickListener?.onLongClick(position) ?: false }

    }


    fun setMultiSelectMode() {
        this.isSelected = BooleanArray(this.appList.size)
        this.isMultiSelectMode = true
    }

    fun cancelMutiSelectMode() {
        this.isMultiSelectMode = false
        this.notifyDataSetChanged()
    }

    fun selectAll() {
        for (i in this.isSelected.indices) {
            this.isSelected[i] = true
        }
        this.notifyDataSetChanged()

    }

    fun deselectAll() {
        for (i in this.isSelected.indices) {
            this.isSelected[i] = false
        }
        this.notifyDataSetChanged()
    }

    fun getSelectedNum(): Int {
        if (this.isMultiSelectMode) {
            var num = 0
            for (i in this.isSelected.indices) {
                if (this.isSelected[i]) {
                    num++
                }
            }
            return num
        } else {
            return 0
        }
    }

    fun getSelectedAppsSize(): Long {
        var size: Long = 0
        for (i in this.isSelected.indices) {
            if (this.isSelected[i]) {
                size += this.appList[i].appSize
            }
        }
        return size
    }

    fun onItemClicked(position: Int) {
        if (position < 0 || position > this.appList.size) return
        this.isSelected[position] = !this.isSelected[position]
        this.notifyDataSetChanged()
    }

    fun getIsSelected(): BooleanArray {
        return isSelected
    }

    fun getAppList(): List<AppItemBean> {
        return appList
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var icon: CircleImageView
        internal var label: TextView
        internal var packageName: TextView
        internal var appSize: TextView
        internal var select: CheckBox

        init {
            icon = view.findViewById(R.id.appimg)
            label = view.findViewById(R.id.appname)
            packageName = view.findViewById(R.id.apppackagename)
            appSize = view.findViewById(R.id.appsize)
            select = view.findViewById(R.id.select)
        }

    }


}