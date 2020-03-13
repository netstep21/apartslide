package com.zslide.view.setting.adapter.holder;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.view.base.BaseViewHolder;
import com.zslide.view.setting.adapter.item.SettingItem;

import butterknife.BindView;

/**
 * Created by chulwoo on 2018. 1. 5..
 * <p>
 * 다음 상세 페이지가 있는 기본 메뉴 holder.
 */

public class AbstractSettingItemViewHolder<T extends SettingItem> extends BaseViewHolder {

    @BindView(R.id.icon) ImageView icon;
    @BindView(R.id.title) TextView title;
    @BindView(R.id.subtitle) TextView subtitle;

    public AbstractSettingItemViewHolder(View itemView) {
        super(itemView);
    }

    public void bind(@NonNull T settingItem) {
        itemView.setOnClickListener(__ -> {
            try {
                settingItem.getOnClickAction().accept(settingItem);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        icon.setImageResource(settingItem.getIconResource());
        title.setText(settingItem.getTitle());
        if (settingItem.hasSubtitle()) {
            subtitle.setVisibility(View.VISIBLE);
            subtitle.setText(settingItem.getSubtitle());
        } else {
            subtitle.setVisibility(View.GONE);
        }
    }
}
