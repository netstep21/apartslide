package com.zslide.view.setting.adapter.item;

import android.support.annotation.NonNull;

import io.reactivex.functions.Consumer;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public class SwitchSettingItem extends SettingItem {

    @Setter @Getter private boolean checked;

    public SwitchSettingItem(boolean checked, int iconResource, @NonNull String title, @NonNull Consumer<SettingItem> onClickAction) {
        this(checked, iconResource, title, "", onClickAction);
    }

    public SwitchSettingItem(boolean checked, int iconResource, @NonNull String title, @NonNull String subtitle, @NonNull Consumer<SettingItem> onClickAction) {
        super(iconResource, title, subtitle, onClickAction);
        this.checked = checked;
    }
}
