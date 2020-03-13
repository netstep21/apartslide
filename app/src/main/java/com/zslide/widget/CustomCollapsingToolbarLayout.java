package com.zslide.widget;

import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.util.AttributeSet;

/**
 * Created by chulwoo on 16. 4. 18..
 */
public class CustomCollapsingToolbarLayout extends CollapsingToolbarLayout {

    private OnScrimVisibleChangedListener onScrimVisibleChangedListener;
    private boolean prevVisible = false;

    public CustomCollapsingToolbarLayout(Context context) {
        this(context, null);
    }

    public CustomCollapsingToolbarLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCollapsingToolbarLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setScrimsShown(boolean shown) {
        if (prevVisible != shown) {
            if (onScrimVisibleChangedListener != null) {
                onScrimVisibleChangedListener.onScrimVisibleChanged(shown);
            }
        }
        prevVisible = shown;
        super.setScrimsShown(shown);
    }

    public void setOnScrimVisibleChangedListener(OnScrimVisibleChangedListener listener) {
        this.onScrimVisibleChangedListener = listener;
    }

    public interface OnScrimVisibleChangedListener {
        void onScrimVisibleChanged(boolean visible);
    }
}
