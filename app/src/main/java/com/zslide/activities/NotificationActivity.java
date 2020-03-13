package com.zslide.activities;

import com.zslide.R;
import com.zslide.fragments.NotificationFragment;

/**
 * Created by jdekim43 on 2016. 3. 4..
 */
public class NotificationActivity extends BaseSingleFragmentActivity<NotificationFragment> {

    @Override
    protected NotificationFragment createFragment() {
        return NotificationFragment.newInstance();
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_notification);
    }
}
