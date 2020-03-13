package com.zslide.activities;

import android.os.Bundle;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.fragments.FamilyMoveFragment;

/**
 * Created by chulwoo on 2016. 6. 3..
 */
public class FamilyMoveActivity extends BaseSingleFragmentActivity<FamilyMoveFragment> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        User user = UserManager.getInstance().getUserValue();
        Family family = UserManager.getInstance().getFamilyValue();
        if (family.isNull() || !family.isFamilyLeader(user)) {
            Navigator.startFamilyRegistrationActivity(this);
            finish();
        }
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_family_update);
    }

    @Override
    protected FamilyMoveFragment createFragment() {
        return FamilyMoveFragment.newInstance();
    }
}
