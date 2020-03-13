package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.fragments.ImageGuideFragment;

import butterknife.BindView;

/**
 * Created by chulwoo on 2017. 9. 11..
 */

public class ZmoneyPaymentsHelpActivity extends BaseActivity {

    public static final int PAGE_SAVING = 0;
    public static final int PAGE_PAYMENTS = 1;

    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int page = PAGE_SAVING;
        if (savedInstanceState == null) {
            page = getIntent().getIntExtra(IntentConstants.EXTRA_PAGE, PAGE_SAVING);
        }

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(page);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        super.setupToolbar(toolbar);
        toolbar.setTitle("도움말");
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_tab;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return ImageGuideFragment.newInstance("help_saving");
            } else {
                return ImageGuideFragment.newInstance("help_payments");
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "적립";
            } else {
                return "지급";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
