package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;

import com.zslide.Navigator;
import com.zslide.R;

import butterknife.OnClick;

/**
 * Created by chulwoo on 16. 7. 21..
 */
public class LeaveCompleteFragment extends BaseFragment {

    public static LeaveCompleteFragment newInstance() {

        Bundle args = new Bundle();

        LeaveCompleteFragment fragment = new LeaveCompleteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_leave_complete;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {

    }

    @OnClick(R.id.exit)
    public void exit() {
        Navigator.startAuthActivity(getActivity());
    }
}
