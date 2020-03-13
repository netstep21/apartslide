package com.zslide.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.zslide.R;
import com.zslide.fragments.BaseFragment;

public abstract class BaseSingleFragmentActivity<FragmentType extends BaseFragment> extends BaseActivity {

    protected static final String FRAGMENT_TAG = "single_fragment";

    protected FragmentType fragment;

    protected abstract FragmentType createFragment();

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_single_fragment;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            try {
                fragment = (FragmentType) manager.findFragmentById(R.id.fragment);
            } catch (Exception e) {
                // TODO: unchecked
            }

            if (fragment == null) {
                fragment = createFragment();
            }

            manager.beginTransaction()
                    .add(R.id.fragment, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    @SuppressWarnings("unchecked")
    protected FragmentType getFragment() {
        return (FragmentType) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    }
}
