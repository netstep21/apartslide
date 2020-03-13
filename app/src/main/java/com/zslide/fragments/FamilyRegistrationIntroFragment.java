package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.activities.FamilyRegistrationActivity;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by chulwoo on 16. 5. 27..
 */
public class FamilyRegistrationIntroFragment extends BaseFragment {

    @BindView(R.id.registrationText) TextView registrationTextView;

    public static FamilyRegistrationIntroFragment newInstance(boolean hasFamily) {

        Bundle args = new Bundle();
        args.putBoolean("has_family", hasFamily);

        FamilyRegistrationIntroFragment fragment = new FamilyRegistrationIntroFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_family_registration_intro;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        boolean hasFamily = getArguments().getBoolean("has_family");
        if (hasFamily) {
            registrationTextView.setText("주소 변경");
        } else {
            registrationTextView.setText("주소 등록");
        }
    }

    @OnClick(R.id.search)
    public void searchFamily() {
        if (getActivity() instanceof FamilyRegistrationActivity) {
            ((FamilyRegistrationActivity) getActivity()).searchFamily();
        }
    }

    @OnClick(R.id.registration)
    public void createFamily() {
        if (getActivity() instanceof FamilyRegistrationActivity) {
            ((FamilyRegistrationActivity) getActivity()).inputAddress();
        }
    }
}
