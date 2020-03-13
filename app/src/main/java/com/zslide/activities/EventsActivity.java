package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.zslide.R;
import com.zslide.fragments.EventsFragment;
import com.zslide.widget.ZummaTabLayout;

import butterknife.BindView;

/**
 * Created by jdekim43 on 2016. 1. 26..
 * <p>
 * Updated by chulwoo on 2016. 8. 9..
 * 진행중 이벤트/종료 이벤트 구분, 레이아웃 업데이트
 */
public class EventsActivity extends BaseActivity {

    @BindView(R.id.tabLayout) ZummaTabLayout tabs;
    @BindView(R.id.viewPager) ViewPager pager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pager.setAdapter(new EventTabAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(2);
        tabs.setupWithViewPager(pager);
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_event);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_events;
    }

    static class EventTabAdapter extends FragmentPagerAdapter {

        EventTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "진행중인 이벤트";
                case 1:
                    return "완료된 이벤트";
                default:
                    return "";
            }
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return EventsFragment.newInstance(EventsFragment.TYPE_ON_GOING);
                case 1:
                    return EventsFragment.newInstance(EventsFragment.TYPE_COMPLETED);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
