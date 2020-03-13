package com.zslide.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.zslide.R;
import com.zslide.activities.LeaveActivity;
import com.zslide.dialogs.SimpleAlertDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by chulwoo on 16. 7. 21..
 */
public class LeaveReasonFragment extends BaseFragment {

    @BindViews({R.id.reason1, R.id.reason2, R.id.reason3, R.id.reason4}) List<CheckBox> reasonViews;
    @BindView(R.id.reason4Content) EditText reason4ContentView;

    public static LeaveReasonFragment newInstance() {

        Bundle args = new Bundle();

        LeaveReasonFragment fragment = new LeaveReasonFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_leave_reason;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {

    }


    @OnTextChanged(R.id.reason4Content)
    public void onExtraReasonChanged(CharSequence s) {
        reasonViews.get(3).setChecked(!TextUtils.isEmpty(s));
    }

    @OnClick(R.id.complete)
    public void onCompleteClicked() {
        boolean isChecked = false;
        for (CheckBox reasonView : reasonViews) {
            if (reasonView.isChecked()) {
                isChecked = true;
                break;
            }
        }

        if (!isChecked) {
            SimpleAlertDialog.newInstance("하나 이상 선택해주세요.")
                    .show(getFragmentManager(), "alert");
            return;
        }

        if (getActivity() instanceof LeaveActivity) {
            boolean zmoney = reasonViews.get(0).isChecked();
            boolean zstore = reasonViews.get(1).isChecked();
            boolean zmall = reasonViews.get(2).isChecked();
            String extraMessage = reason4ContentView.getText().toString().trim();

            ((LeaveActivity) getActivity()).leave(zmoney, zstore, zmall, extraMessage);
        }
    }
}
