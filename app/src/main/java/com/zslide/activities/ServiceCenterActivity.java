package com.zslide.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.zslide.R;
import com.zslide.fragments.ServiceCenterFragment;

/**
 * Created by chulwoo on 16. 8. 25..
 */
public class ServiceCenterActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, ServiceCenterFragment.newInstance())
                .commit();
    }

    @Override
    public String getScreenName() {
        return getString(R.string.label_servicecenter);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_single_fragment;
    }
}
