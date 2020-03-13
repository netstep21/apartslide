package com.zslide.activities;

import com.zslide.R;
import com.zslide.fragments.NoticesFragment;

/**
 * Created by chulwoo on 2015. 10. 29..
 */
public class NoticesActivity extends BaseSingleFragmentActivity<NoticesFragment> {

    @Override
    protected NoticesFragment createFragment() {
        return NoticesFragment.newInstance();
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_notices);
    }
}
