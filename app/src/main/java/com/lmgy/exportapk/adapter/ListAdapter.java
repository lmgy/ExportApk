package com.lmgy.exportapk.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.bean.AppItemBean;
import com.lmgy.exportapk.ui.AppDetailActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/15
 */
public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int MODE_LINEAR = 0;
    static final int MODE_GRID = 1;

    private List<AppItemBean> list;
    private boolean[] isSelected;
    private boolean isMultiSelectMode = false;
    private int mode = 0;
    private Context mContext;

    public ListAdapter(Context context, @NonNull List<AppItemBean> list, int mode) {
        this.list = list;
        this.mContext = context;
        isSelected = new boolean[list.size()];
        if (mode == 1) {
            this.mode = 1;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_app_info_grid, viewGroup, false), mode);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder vh, int position) {
        ViewHolder viewHolder = (ViewHolder) vh;
        try {
            final AppItemBean item = list.get(viewHolder.getAdapterPosition());
            if (item == null) {
                return;
            }
            viewHolder.title.setText(String.valueOf(item.getAppName()));
            viewHolder.title.setTextColor(mContext.getResources().getColor((item.getPackageInfo().applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0 ?
                    R.color.colorSystemAppTitleColor : R.color.colorHighLightText));
            //viewHolder.icon.setImageDrawable(item.getIcon());
            viewHolder.icon.setImageDrawable(item.getIcon(mContext));
            if (mode == 0) {
                viewHolder.description.setText(String.valueOf(item.getPackageName()));
                viewHolder.right.setText(Formatter.formatFileSize(mContext, item.getSize()));
                viewHolder.cb.setChecked(isSelected[viewHolder.getAdapterPosition()]);
                viewHolder.right.setVisibility(isMultiSelectMode ? View.GONE : View.VISIBLE);
                viewHolder.cb.setVisibility(isMultiSelectMode ? View.VISIBLE : View.GONE);
            } else if (mode == 1) {
                if (isMultiSelectMode) {
                    viewHolder.root.setBackgroundColor(mContext.getResources().getColor(isSelected[viewHolder.getAdapterPosition()]
                            ? R.color.colorSelectedBackground
                            : R.color.colorCardArea));
                } else {
                    viewHolder.root.setBackgroundColor(mContext.getResources().getColor(R.color.colorCardArea));
                }
            }

            viewHolder.root.setOnClickListener(v -> {
                if (isMultiSelectMode) {
                    isSelected[viewHolder.getAdapterPosition()] = !isSelected[viewHolder.getAdapterPosition()];
                    refreshButtonStatus();
                    notifyItemChanged(viewHolder.getAdapterPosition());
                } else {
                    Intent intent = new Intent(mContext, AppDetailActivity.class);
                    //intent.putExtra(EXTRA_PARCELED_APP_ITEM,item);
                    intent.putExtra("package_name", item.getPackageName());
                    ActivityOptionsCompat compat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) mContext, new Pair<>(viewHolder.icon, "icon"));
                    try {
                        ActivityCompat.startActivity(mContext, intent, compat.toBundle());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });
            viewHolder.root.setOnLongClickListener(isMultiSelectMode ? null : (View.OnLongClickListener) v -> {
//                swipeRefreshLayout.setEnabled(false);
                isSelected = new boolean[list.size()];
                isSelected[viewHolder.getAdapterPosition()] = true;
                isMultiSelectMode = true;
                refreshButtonStatus();
                notifyDataSetChanged();
                try {
//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
//                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                bottomCard.setVisibility(View.GONE);
//                bottomCard.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.entry_300));
//                bottomCardMultiSelect.setVisibility(View.VISIBLE);
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void closeMultiSelectMode() {
        isMultiSelectMode = false;
        notifyDataSetChanged();
    }

    public boolean getIsMultiSelectMode() {
        return isMultiSelectMode;
    }

    public void setSelectAll(boolean selected) {
        if (!isMultiSelectMode || isSelected == null) {
            return;
        }
        for (int i = 0; i < isSelected.length; i++) {
            isSelected[i] = selected;
        }
//        mainExport.setEnabled(selected);
//        mainShare.setEnabled(selected);
        refreshButtonStatus();
        notifyDataSetChanged();
    }

    private void refreshButtonStatus() {
//        mainExport.setEnabled(getSelectedNum() > 0);
//        mainShare.setEnabled(getSelectedNum() > 0);
//        ((TextView) findViewById(R.id.main_select_num_size)).setText(getSelectedNum() + mContext.getResources().getString(R.string.unit_item) + "/" + Formatter.formatFileSize(MainActivity.this, getSelectedFileLength()));
    }


    /**
     * 返回的是初始化data,obb为false的副本
     */
    public List<AppItemBean> getSelectedAppItems() {
        ArrayList<AppItemBean> list_selected = new ArrayList<>();
        if (!isMultiSelectMode) {
            return list_selected;
        }
        for (int i = 0; i < list.size(); i++) {
            if (isSelected[i]) {
                list_selected.add(new AppItemBean(list.get(i), false, false));
            }
        }
        return list_selected;
    }

    private int getSelectedNum() {
        int i = 0;
        for (boolean b : isSelected) {
            if (b) {
                i++;
            }
        }
        return i;
    }

    private long getSelectedFileLength() {
        long length = 0;
        for (int i = 0; i < isSelected.length; i++) {
            if (isSelected[i]) {
                length += list.get(i).getSize();
            }
        }
        return length;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView description;
        TextView right;
        CheckBox cb;
        View root;

        public ViewHolder(@NonNull View itemView, int mode) {
            super(itemView);
            root = itemView.findViewById(R.id.item_app_root);
            icon = itemView.findViewById(R.id.item_app_icon);
            title = itemView.findViewById(R.id.item_app_title);
            if (mode == 0) {
                description = itemView.findViewById(R.id.item_app_description);
                right = itemView.findViewById(R.id.item_app_right);
                cb = itemView.findViewById(R.id.item_app_cb);
            }
        }
    }

}
