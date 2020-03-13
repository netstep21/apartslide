package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;

import com.zslide.Navigator;
import com.zslide.R;

import butterknife.OnClick;

/**
 * Created by chulwoo on 16. 8. 25..
 */
public class ServiceCenterFragment extends BaseFragment {

    public static ServiceCenterFragment newInstance() {

        Bundle args = new Bundle();

        ServiceCenterFragment fragment = new ServiceCenterFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_servicecenter;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {

    }

    @OnClick(R.id.faq)
    protected void startFaqActivity() {
        Navigator.startFaqActivity(getActivity());
    }

    @OnClick(R.id.personalHelp)
    public void startPersonalHelp() {
        Navigator.startPersonalHelp(getActivity());
    }

    @OnClick(R.id.adPartner)
    public void helpAdPartner() {
        Navigator.startAdPartner(getActivity());
    }

    @OnClick(R.id.suggest)
    public void suggest() {
        Navigator.startSuggestionActivity(getActivity());
    }
}
