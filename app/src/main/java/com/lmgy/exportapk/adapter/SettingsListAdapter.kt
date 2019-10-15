package com.lmgy.exportapk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.lmgy.exportapk.R
import com.lmgy.exportapk.bean.SettingsContentBean

/**
 * @author lmgy
 * @date 2019/10/15
 */
class SettingsListAdapter(context: Context, private val resource: Int, objects: MutableList<SettingsContentBean>) : ArrayAdapter<SettingsContentBean>(context, resource, objects) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val bean = getItem(position)
        val view = LayoutInflater.from(context).inflate(resource, null)
        val textView = view.findViewById(R.id.tv_name)
        val imageView = view.findViewById(R.id.iv_icon)
        textView.setText(bean?.name)
        imageView.setImageResource(bean?.image)
        return view
    }

}