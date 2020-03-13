package com.zslide.activities;

import com.zslide.R;
import com.zslide.fragments.FaqFragment;

/**
 * Created by jdekim43 on 2016. 1. 25..
 */
public class FaqActivity extends BaseSingleFragmentActivity<FaqFragment> {

    @Override
    protected FaqFragment createFragment() {
        return FaqFragment.newInstance();
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_faq);
    }
}
