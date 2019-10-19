package com.lmgy.exportapk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lmgy.exportapk.R;
import com.lmgy.exportapk.bean.FileItemBean;

import java.util.List;

/**
 * @author lmgy
 * @date 2019/10/19
 */
public class FileListAdapter extends BaseAdapter {

    private List<FileItemBean> list;
    private LayoutInflater inflater;
    private int selectedPosition = -1;

    private onRadioButtonClickedListener monRadioButtonClicked;

    public FileListAdapter(List<FileItemBean> list, Context context) {
        this.list = list;
        this.inflater = LayoutInflater.from(context);
        this.selectedPosition = -1;
    }

    public interface onRadioButtonClickedListener {
        void onClick(int position);
    }

    public void setOnRadioButtonClickListener(onRadioButtonClickedListener monRadioButtonClicked) {
        this.monRadioButtonClicked = monRadioButtonClicked;
    }

    public void setList(List<FileItemBean> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }

    public void setSelected(int position) {
        this.selectedPosition = position;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_folderitem, viewGroup, false);
            holder = new ViewHolder();
            holder.tvFileName = view.findViewById(R.id.item_folderitem_name);
            holder.raSelect = view.findViewById(R.id.item_folderitem_selecor);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvFileName.setText(list.get(i).getFile().getName());

        if (i == this.selectedPosition) {
            holder.raSelect.setChecked(true);
        } else {
            holder.raSelect.setChecked(false);
        }

        holder.raSelect.setOnClickListener(v -> {
            if (monRadioButtonClicked != null) {
                monRadioButtonClicked.onClick(i);
            }
        });
        return view;
    }

    class ViewHolder {
        //	ImageView img;
        TextView tvFileName;
        RadioButton raSelect;
    }
}
