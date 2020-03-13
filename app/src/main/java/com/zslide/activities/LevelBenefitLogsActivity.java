package com.zslide.activities;

import com.zslide.fragments.LevelBenefitLogsFragment;

/**
 * Created by chulwoo on 16. 8. 8..
 */
public class LevelBenefitLogsActivity extends BaseSingleFragmentActivity<LevelBenefitLogsFragment> {
    @Override
    protected LevelBenefitLogsFragment createFragment() {
        return LevelBenefitLogsFragment.newInstance();
    }

    @Override
    public String getScreenName() {
        return "받은 등급 혜택";
    }
}
