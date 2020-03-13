package com.zslide.activities;

import android.os.Bundle;

import com.zslide.R;
import com.zslide.fragments.VersionInfoFragment;

import butterknife.BindColor;

/**
 * Created by chulwoo on 16. 1. 15..
 */
public class VersionInfoActivity extends BaseSingleFragmentActivity<VersionInfoFragment> {

    @BindColor(R.color.white) int BACKGROUND_COLOR;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(BACKGROUND_COLOR);
    }

    @Override
    protected VersionInfoFragment createFragment() {
        return VersionInfoFragment.newInstance();
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_version_info);
    }
}
