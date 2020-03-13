package com.zslide.activities;

import com.zslide.Navigator;
import com.zslide.R;

import butterknife.OnClick;

/**
 * Created by chulwoo on 2017. 9. 28..
 */

public class SignupCompleteActivity extends BaseActivity {

    @OnClick(R.id.familyRegistration)
    public void familyRegistration() {
        Navigator.startFamilyRegistrationActivity(this);
    }

    @OnClick(R.id.later)
    public void later() {
        Navigator.startMainActivity(this, 0);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_signup_complete;
    }
}