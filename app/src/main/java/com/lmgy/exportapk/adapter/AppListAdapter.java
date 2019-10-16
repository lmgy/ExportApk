package com.lmgy.exportapk.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.widget.CircleImageView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.MyViewHolder> {

    private OnItemClickListener mItemClickListener;
    private OnLongClickListener mLongClickListener;

    private Context context;
    private List<AppItemBean> appItemBeanList;
    private boolean isMultiSelectMode = false;
    private boolean[] isSelected;

    public AppListAdapter(Context context, List<AppItemBean> appItemBeanList, boolean ifAnim) {
        this.context = context;
        this.appItemBeanList = appItemBeanList;
        this.isSelected = new boolean[this.appItemBeanList.size()];
        boolean[] ifshowedAnim = new boolean[this.appItemBeanList.size()];
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void setLongClickListener(OnLongClickListener longClickListener) {
        mLongClickListener = longClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnLongClickListener {
        boolean onLongClick(int position);
    }

    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.item_applist, parent, false));
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        AppItemBean item = appItemBeanList.get(position);
        holder.icon.setImageDrawable(item.getIcon());
        holder.label.setText(item.getAppName() + "(" + item.getVersion() + ")");
        holder.packageName.setText(item.getPackageName());
        if (item.isSystemApp) {
            holder.label.setTextColor(context.getResources().getColor(R.color.color_text_darkred));
        } else {
            holder.label.setTextColor(context.getResources().getColor(R.color.color_text_black));
        }
        holder.appSize.setText(Formatter.formatFileSize(context, item.getAppSize()));
        if (this.isMultiSelectMode && this.isSelected != null) {
            if (position < this.isSelected.length) {
                holder.select.setChecked(this.isSelected[position]);
            }
            holder.select.setVisibility(View.VISIBLE);
            holder.appSize.setVisibility(View.GONE);
        } else {
            holder.select.setVisibility(View.GONE);
            holder.appSize.setVisibility(View.VISIBLE);
        }

        //设置点击和长按事件
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(view -> mItemClickListener.onItemClick(position));
        }
        if (mLongClickListener != null) {
            holder.itemView.setOnLongClickListener(view -> mLongClickListener.onLongClick(position));
        }

    }

    @Override
    public int getItemCount() {
        return appItemBeanList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView icon;
        private TextView label;
        private TextView packageName;
        private TextView appSize;
        private CheckBox select;

        public MyViewHolder(View view) {
            super(view);
            icon = view.findViewById(R.id.appimg);
            label = view.findViewById(R.id.appname);
            packageName = view.findViewById(R.id.apppackagename);
            appSize = view.findViewById(R.id.appsize);
            select = view.findViewById(R.id.select);
        }

    }

    public void setMultiSelectMode() {
        this.isSelected = new boolean[this.appItemBeanList.size()];
        this.isMultiSelectMode = true;
    }

    public void cancelMutiSelectMode() {
        this.isMultiSelectMode = false;
        this.notifyDataSetChanged();
    }

    public void selectAll() {
        if (this.isSelected != null) {
            for (int i = 0; i < this.isSelected.length; i++) {
                this.isSelected[i] = true;
            }
            this.notifyDataSetChanged();
        }

    }

    public void deselectAll() {
        if (this.isSelected != null) {
            for (int i = 0; i < this.isSelected.length; i++) {
                this.isSelected[i] = false;
            }
            this.notifyDataSetChanged();
        }
    }

    public int getSelectedNum() {
        if (this.isMultiSelectMode && this.isSelected != null) {
            int num = 0;
            for (boolean b : this.isSelected) {
                if (b) {
                    num++;
                }
            }
            return num;
        } else {
            return 0;
        }
    }

    public long getSelectedAppsSize() {
        if (this.isSelected != null) {
            long size = 0;
            for (int i = 0; i < this.isSelected.length; i++) {
                if (this.isSelected[i]) {
                    size += this.appItemBeanList.get(i).getAppSize();
                }
            }
            return size;
        } else {
            return 0;
        }
    }

    public void onItemClicked(int position) {
        if (position < 0 || position > this.appItemBeanList.size()) {
            return;
        }
        this.isSelected[position] = !this.isSelected[position];
        this.notifyDataSetChanged();
    }

    public boolean[] getIsSelected() {
        return this.isSelected;
    }

    public List<AppItemBean> getAppList() {
        return this.appItemBeanList;
    }

}

