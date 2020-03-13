package com.zslide.view.setting.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.zslide.R;
import com.zslide.view.setting.adapter.holder.SettingItemViewHolder;
import com.zslide.view.setting.adapter.holder.SwitchAbstractSettingItemViewHolder;
import com.zslide.view.setting.adapter.item.SettingItem;
import com.zslide.view.setting.adapter.item.SwitchSettingItem;

import java.util.List;

import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public class SettingItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_SWITCH = 1;

    @Getter private List<SettingItem> items;

    public SettingItemAdapter(@NonNull List<SettingItem> items) {
        this.items = items;
    }

    public void setItems(List<? extends SettingItem> items) {
        this.items.clear();
        this.items.addAll(items);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_SWITCH:
                return new SwitchAbstractSettingItemViewHolder(
                        inflater.inflate(R.layout.item_setting_switch, parent, false));
            case TYPE_NORMAL:
            default:
                return new SettingItemViewHolder(
                        inflater.inflate(R.layout.item_setting, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SettingItem item = items.get(position);
        if (item instanceof SwitchSettingItem) {
            ((SwitchAbstractSettingItemViewHolder) holder).bind((SwitchSettingItem) item);
        } else {
            ((SettingItemViewHolder) holder).bind(item);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        SettingItem item = items.get(position);
        if (item instanceof SwitchSettingItem) {
            return TYPE_SWITCH;
        } else {
            return TYPE_NORMAL;
        }
    }
}
