package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.zslide.R;
import com.zslide.activities.LeaveActivity;
import com.zslide.dialogs.SimpleAlertDialog;

import java.util.List;

import butterknife.BindViews;
import butterknife.OnClick;

/**
 * Created by chulwoo on 16. 7. 21..
 */
public class LeaveAgreeFragment extends BaseFragment {

    @BindViews({R.id.agree1, R.id.agree2, R.id.agree3}) List<CheckBox> agreeViews;

    public static LeaveAgreeFragment newInstance() {

        Bundle args = new Bundle();

        LeaveAgreeFragment fragment = new LeaveAgreeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_leave_agree;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {

    }

    @OnClick(R.id.next)
    public void onNextClicked() {
        boolean isAgree = true;
        for (CheckBox agreeView : agreeViews) {
            if (!agreeView.isChecked()) {
                isAgree = false;
                break;
            }
        }

        if (!isAgree) {
            SimpleAlertDialog.newInstance("모든 안내 사항에 동의해주세요.")
                    .show(getFragmentManager(), "alert");
        } else if (getActivity() instanceof LeaveActivity) {
            ((LeaveActivity) getActivity()).next();
        }
    }
}
