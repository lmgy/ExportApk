package com.lmgy.exportapk.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.bean.SettingsBean;

import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/18
 */
public class SettingsListAdapter extends ArrayAdapter<SettingsBean> {

    private int mResource;

    public SettingsListAdapter(Context context, int resource, List<SettingsBean> objects) {
        super(context, resource, objects);
        this.mResource = resource;
    }

    @NonNull
    @Override
    @SuppressLint("ViewHolder")
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        SettingsBean bean = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(mResource, null);
        TextView textView = view.findViewById(R.id.tv_name);
        ImageView imageView = view.findViewById(R.id.iv_icon);
        textView.setText(bean.getName());
        imageView.setImageResource(bean.getImage());
        return view;
    }

}
