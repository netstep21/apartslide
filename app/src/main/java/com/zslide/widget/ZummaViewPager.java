package com.zslide.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

/**
 * Created by chulwoo on 15. 8. 25..
 */
public class ZummaViewPager extends ViewPager {

    private boolean smoothScrollEnabled = false;

    public ZummaViewPager(Context context) {
        super(context);
    }

    public ZummaViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSmoothScrollEnabled(boolean enabled) {
        this.smoothScrollEnabled = enabled;
    }

    @Override
    public void setCurrentItem(int item) {
        super.setCurrentItem(item, smoothScrollEnabled);
    }
}
