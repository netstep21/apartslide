package com.zslide.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.zslide.R;
import com.zslide.fragments.BaseFragment;

public abstract class SingleFragmentLoadingActivity<Data, FragmentType extends BaseFragment> extends BaseLoadingActivity<Data> {

    protected FragmentType fragment;

    protected abstract FragmentType createFragment();

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_single_fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            fragment = (FragmentType) manager.findFragmentById(R.id.fragment);

            if (fragment == null) {
                fragment = createFragment();
            }

            manager.beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit();
        }
    }
}
