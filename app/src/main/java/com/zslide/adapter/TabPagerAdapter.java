package com.zslide.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.zslide.fragments.HomeFragment;
import com.zslide.view.setting.SettingsFragment;

/**
 * Created by cheyuni on 2018. 3. 30..
 */

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    // Count number of tabs
    private int tabCount;

    public TabPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {

        // Returning the current tabs
        switch (position) {
            case 0:
                HomeFragment home = new HomeFragment();
                return home;
            case 1:
                SettingsFragment settings = new SettingsFragment();
                return settings;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

}
