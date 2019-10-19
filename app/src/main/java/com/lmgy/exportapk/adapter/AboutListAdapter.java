package com.lmgy.exportapk.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.bean.AboutBean;

import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/19
 */
public class AboutListAdapter extends ArrayAdapter<AboutBean> {

    private int mResource;

    public AboutListAdapter(Context context, int resource, List<AboutBean> objects) {
        super(context, resource, objects);
        this.mResource = resource;
    }

    @NonNull
    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AboutBean bean = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(mResource, null);
        TextView tvAuthor = view.findViewById(R.id.tv_author);
        TextView tvPath = view.findViewById(R.id.iv_icon);
        tvAuthor.setText(bean.getAuthor());
        tvPath.setText(bean.getPath());
        return view;
    }
}
