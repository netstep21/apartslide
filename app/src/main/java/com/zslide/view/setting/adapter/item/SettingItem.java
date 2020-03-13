package com.zslide.view.setting.adapter.item;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import io.reactivex.functions.Consumer;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public class SettingItem {

    @Getter @DrawableRes private int iconResource;
    @Getter private String title;
    @Setter @Getter private String subtitle;
    @Setter @Getter private Consumer<SettingItem> onClickAction;
    @Getter private boolean sectionHeader;

    public SettingItem(@DrawableRes int iconResource, @NonNull String title) {
        this(iconResource, title, "", null);
    }

    public SettingItem(@DrawableRes int iconResource, @NonNull String title, String subtitle) {
        this(iconResource, title, subtitle, null);
    }

    public SettingItem(@DrawableRes int iconResource, @NonNull String title, @Nullable Consumer<SettingItem> onClickAction) {
        this(iconResource, title, "", onClickAction);
    }

    public SettingItem(@DrawableRes int iconResource, @NonNull String title, String subtitle, @Nullable Consumer<SettingItem> onClickAction) {
        this.iconResource = iconResource;
        this.title = title;
        this.subtitle = subtitle;
        this.onClickAction = onClickAction;
    }

    public boolean hasSubtitle() {
        return !TextUtils.isEmpty(subtitle);
    }

    public void setSectionHeader(boolean sectionHeader) {
        this.sectionHeader = sectionHeader;
    }
}
