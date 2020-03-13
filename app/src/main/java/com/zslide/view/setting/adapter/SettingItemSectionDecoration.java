package com.zslide.view.setting.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

/**
 * Created by chulwoo on 2018. 1. 9..
 */

public class SettingItemSectionDecoration extends RecyclerView.ItemDecoration {

    private int space;
    private int lastItemInFirstLane = -1;
    private List<Integer> spacingItemIndex;

    public SettingItemSectionDecoration(int space, List<Integer> spacingItemIndex) {
        this.space = space;
        this.spacingItemIndex = spacingItemIndex;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        final int position = params.getViewAdapterPosition();
        // invalid value
        if (!spacingItemIndex.contains(position)) {
            return;
        }

        outRect.top = space;
    }
}
