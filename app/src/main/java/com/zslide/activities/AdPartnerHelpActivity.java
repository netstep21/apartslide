package com.zslide.activities;

import com.zslide.R;
import com.zslide.fragments.AdPartnerHelpFragment;

/**
 * Created by chulwoo on 15. 8. 25..
 */
public class AdPartnerHelpActivity extends BaseSingleFragmentActivity<AdPartnerHelpFragment> {

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_single_fragment_fullscreen;
    }

    @Override
    protected AdPartnerHelpFragment createFragment() {
        return AdPartnerHelpFragment.newInstance();
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_ad_help);
    }
}
