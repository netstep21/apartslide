package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuItem;

import com.zslide.IntentConstants;
import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.fragments.PreviousZmoneyFragment;
import com.zslide.fragments.ZmoneyFragment;
import com.zslide.widget.ZummaViewPager;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by chulwoo on 2015. 10. 29..
 */
public class ZmoneyActivity extends BaseActivity {

    public static final int PAGE_TODAY = 0;
    public static final int PAGE_THIS_MONTH = 1;
    public static final int PAGE_ALL = 2;

    @BindView(R.id.tabLayout) TabLayout tabs;
    @BindView(R.id.viewPager) ZummaViewPager pager;

    private int page;

    @Override
    public String getScreenName() {
        return getString(R.string.screen_zmoney_detail);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_zmoney;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("적립");
        if (savedInstanceState == null) {
            page = getIntent().getIntExtra(IntentConstants.EXTRA_PAGE, PAGE_TODAY);
        }

        pager.setAdapter(new ZmoneyPagerAdapter(getSupportFragmentManager()));
        pager.setOffscreenPageLimit(3);
        pager.setCurrentItem(page);
        tabs.setupWithViewPager(pager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.zmoney, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.savingGuide) {
            Navigator.startZmoneyPaymentsHelpActivity(this, ZmoneyPaymentsHelpActivity.PAGE_SAVING);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected class ZmoneyPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<String> titles;
        private ArrayList<Fragment> fragments;

        public ZmoneyPagerAdapter(FragmentManager fm) {
            super(fm);
            titles = new ArrayList<>();
            fragments = new ArrayList<>();
            titles.add("오늘");
            fragments.add(ZmoneyFragment.newInstance(ZmoneyFragment.TYPE_DAILY));
            titles.add("이번 달");
            fragments.add(ZmoneyFragment.newInstance(ZmoneyFragment.TYPE_MONTHLY));
            titles.add("전체");
            fragments.add(PreviousZmoneyFragment.newInstance());
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
