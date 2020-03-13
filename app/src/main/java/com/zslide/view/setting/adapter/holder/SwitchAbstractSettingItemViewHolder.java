package com.zslide.view.setting.adapter.holder;

import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.View;

import com.zslide.R;
import com.zslide.view.setting.adapter.item.SwitchSettingItem;

import butterknife.BindView;

/**
 * Created by chulwoo on 2018. 1. 5..
 * <p>
 * {@link android.widget.Switch} 가 있는 설정 메뉴 holder
 */

public class SwitchAbstractSettingItemViewHolder extends AbstractSettingItemViewHolder<SwitchSettingItem> {

    @BindView(R.id.switcher) SwitchCompat switcher;

    public SwitchAbstractSettingItemViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bind(@NonNull SwitchSettingItem settingItem) {
        super.bind(settingItem);
        itemView.setOnClickListener(null);
        switcher.setChecked(settingItem.isChecked());
        switcher.setOnClickListener(__ -> {
            try {
                settingItem.getOnClickAction().accept(settingItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
