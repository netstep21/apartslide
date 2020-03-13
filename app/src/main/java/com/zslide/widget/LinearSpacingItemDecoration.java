package com.zslide.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by chulwoo on 15. 7. 10..
 */
public class LinearSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int space;

    public LinearSpacingItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if (parent.getChildAdapterPosition(view) < parent.getChildCount() - 1) {
            outRect.bottom = space;
        }

        if (parent.getChildAdapterPosition(view) > 0) {
            outRect.top = space;
        }
    }
}
