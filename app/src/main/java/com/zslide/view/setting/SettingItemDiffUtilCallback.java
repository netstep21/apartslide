package com.zslide.view.setting;

import android.support.v7.util.DiffUtil;

import com.zslide.view.setting.adapter.item.SettingItem;
import com.zslide.view.setting.adapter.item.SwitchSettingItem;

import java.util.List;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public class SettingItemDiffUtilCallback extends DiffUtil.Callback {

    private final List<SettingItem> oldItems;
    private final List<SettingItem> newItems;

    public SettingItemDiffUtilCallback(List<SettingItem> oldItems, List<SettingItem> newItems) {
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems == null ? 0 : oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems == null ? 0 : newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).getTitle().equals(
                newItems.get(newItemPosition).getTitle());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        SettingItem oldItem = oldItems.get(oldItemPosition);
        SettingItem newItem = newItems.get(oldItemPosition);
        return oldItem instanceof SwitchSettingItem
                && newItem instanceof SwitchSettingItem
                && oldItem.getTitle().equals(newItem.getTitle())
                && oldItem.getIconResource() == newItem.getIconResource()
                && ((SwitchSettingItem) oldItem).isChecked() == ((SwitchSettingItem) newItem).isChecked();

    }
}
