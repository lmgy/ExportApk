package com.lmgy.exportapk.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lmgy.exportapk.R

/**
 * @author lmgy
 * @date 2019/10/15
 */
class SearchHistoryAdapter(context: Context, private var historyList: MutableList<String>) : RecyclerView.Adapter<SearchHistoryAdapter.MyViewHolder> {
    private var mContext: Context = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_search_history, parent, false))
    }

    override fun getItemCount(): Int {
        return historyList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.historyInfo.text = historyList[position]
        holder.historyInfo.setOnClickListener { iOnItemClickListener.onItemClick(historyList[position]) }
        holder.delete.setOnClickListener { iOnItemClickListener.onItemDeleteClick(historyList[position]) }
    }


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        internal var historyInfo: TextView
        internal var delete: ImageView

        init {
            historyInfo = view.findViewById(R.id.tv_item_search_history)
            delete = view.findViewById(R.id.iv_item_search_delete)
        }
    }

    private var iOnItemClickListener: IOnItemClickListener? = null

    fun setOnItemClickListener(iOnItemClickListener: IOnItemClickListener) {
        this.iOnItemClickListener = iOnItemClickListener
    }


}