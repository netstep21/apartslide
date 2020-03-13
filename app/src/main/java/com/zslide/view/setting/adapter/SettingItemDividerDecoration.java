package com.zslide.view.setting.adapter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.zslide.R;

/**
 * Created by chulwoo on 2018. 1. 9..
 */

public class SettingItemDividerDecoration extends RecyclerView.ItemDecoration {

    private Drawable divider;
    private final int spacing;

    public SettingItemDividerDecoration(Context context, int spacing) {
        this.divider = ContextCompat.getDrawable(context, R.drawable.divider_setting);
        this.spacing = spacing;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft() + spacing;
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + divider.getIntrinsicHeight();
            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }


    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    //    outRect.set(0, 0, 0, divider.getIntrinsicHeight());
    }
}
