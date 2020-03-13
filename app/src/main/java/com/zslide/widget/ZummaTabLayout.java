package com.zslide.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.zslide.R;

/**
 * Created by chulwoo on 16. 3. 19..
 * <p>
 * 커스텀 폰트 적용을 위한 TabLayout
 */
public class ZummaTabLayout extends TabLayout {

    public ZummaTabLayout(Context context) {
        super(context);
        init(context);
    }

    public ZummaTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZummaTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.subAccentColor));
    }

    @Override
    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        super.setupWithViewPager(viewPager);
        if (viewPager == null) {
            return;
        }

        PagerAdapter adapter = viewPager.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            TabLayout.Tab tab = getTabAt(i);

            if (tab != null) {
                tab.setCustomView(R.layout.view_tab_text);
                tab.setText(adapter.getPageTitle(i));
            }
        }

        Tab firstTab = getTabAt(0);
        if (firstTab != null) {
            View customView = firstTab.getCustomView();
            if (customView != null) {
                customView.setSelected(true);
            }
        }
    }
}