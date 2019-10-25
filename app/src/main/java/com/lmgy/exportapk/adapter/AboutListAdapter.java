package com.lmgy.exportapk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.bean.AboutBean;

import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/19
 */
public class AboutListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<AboutBean> aboutBeanList;

    public AboutListAdapter(Context context, List<AboutBean> aboutBeanList) {
        this.mContext = context;
        this.aboutBeanList = aboutBeanList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_about_card, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
        recyclerViewHolder.tvName.setText(aboutBeanList.get(position).getName());
        recyclerViewHolder.tvPath.setText(aboutBeanList.get(position).getPath());
    }

    @Override
    public int getItemCount() {
        return aboutBeanList.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvPath;

        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPath = itemView.findViewById(R.id.tv_path);
        }
    }
}
