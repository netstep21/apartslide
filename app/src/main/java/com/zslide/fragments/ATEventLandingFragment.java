package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;

import com.zslide.R;
import com.zslide.activities.ATEventActivity;

import butterknife.OnClick;

/**
 * Created by chulwoo on 16. 8. 11..
 */
public class ATEventLandingFragment extends BaseFragment {

    public static ATEventLandingFragment newInstance() {
        return new ATEventLandingFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_at_event_landing;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {

    }

    @OnClick(R.id.sh)
    public void replaceToShinhan() {
        ((ATEventActivity) getActivity()).replace(ATEventActivity.TYPE_SHINHAN);
    }

    @OnClick(R.id.kb)
    public void replaceToKB() {
        ((ATEventActivity) getActivity()).replace(ATEventActivity.TYPE_KB);
    }

    @OnClick(R.id.samsung)
    public void replaceToSamsung() {
        ((ATEventActivity) getActivity()).replace(ATEventActivity.TYPE_SAMSUNG);
    }
}
