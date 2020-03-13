package com.zslide.activities;

import com.zslide.R;
import com.zslide.fragments.AppSettingsFragment;

public class AppSettingsActivity extends BaseSingleFragmentActivity<AppSettingsFragment> {

    @Override
    public String getScreenName() {
        return getString(R.string.screen_settings);
    }

    @Override
    protected AppSettingsFragment createFragment() {
        return AppSettingsFragment.newInstance();
    }
}