package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.fragments.CalculationsFragment;
import com.zslide.fragments.ZmoneyPaymentsFragment;

import butterknife.BindView;

/**
 * Created by chulwoo on 2017. 3. 8..
 */

public class ZmoneyPaymentsActivity extends BaseActivity {

    @BindView(R.id.tabLayout) TabLayout tabLayout;
    @BindView(R.id.viewPager) ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void setupToolbar(Toolbar toolbar) {
        super.setupToolbar(toolbar);
        toolbar.setTitle("지급");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.zmoney_payments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.paymentsGuide) {
            Navigator.startZmoneyPaymentsHelpActivity(this);
        }
        return super.onOptionsItemSelected(item);
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
                return ZmoneyPaymentsFragment.newInstance();
            } else {
                return CalculationsFragment.newInstance();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) {
                return "이번 달";
            } else {
                return "전체";
            }
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
