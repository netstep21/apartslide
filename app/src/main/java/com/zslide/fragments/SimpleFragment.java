package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/**
 * Created by jdekim43 on 2016. 5. 4..
 */
public class SimpleFragment extends BaseFragment {

    private int layoutResource;

    public static SimpleFragment newInstance(int resId) {
        Bundle args = new Bundle();
        args.putInt("res", resId);

        SimpleFragment instance = new SimpleFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        layoutResource = getArguments().getInt("res");
    }

    @Override
    protected int getLayoutResourceId() {
        return layoutResource;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {

    }
}
