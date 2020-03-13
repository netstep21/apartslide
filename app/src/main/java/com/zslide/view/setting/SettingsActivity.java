package com.zslide.view.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.zslide.R;
import com.zslide.view.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar) Toolbar toolbar;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useToolbar(toolbar);
        toolbar.setTitle(R.string.label_settings);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, SettingsFragment.newInstance(), "fragment")
                .commit();
    }
/*
    @Override
    public void onSelectSnoozeSec(int snoozeSec) {
        SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("fragment");
        if (fragment != null) {
            fragment.onSelectSnoozeSec(snoozeSec);
        }
    }

    @Override
    public void onCancel() {
        SettingsFragment fragment = (SettingsFragment) getSupportFragmentManager().findFragmentByTag("fragment");
        if (fragment != null) {
            fragment.onCancel();
        }
    }
*/
}