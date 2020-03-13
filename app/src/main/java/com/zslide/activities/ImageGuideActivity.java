package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;

import com.zslide.IntentConstants;
import com.zslide.fragments.BaseFragment;
import com.zslide.fragments.ImageGuideFragment;

/**
 * Created by chulwoo on 2017. 4. 12..
 */

public class ImageGuideActivity extends BaseSingleFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String title = getIntent().getStringExtra(IntentConstants.EXTRA_TITLE);
        if (TextUtils.isEmpty(title)) {
            title = "안내";
        }

        setToolbarTitle(title);
    }

    @Override
    protected void setupActionBar(ActionBar actionBar) {
        super.setupActionBar(actionBar);
    }

    @Override
    protected BaseFragment createFragment() {
        String target = getIntent().getStringExtra(IntentConstants.EXTRA_TARGET);
        return ImageGuideFragment.newInstance(target);
    }
}
