package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.AuthenticationManager;
import com.zslide.data.remote.response.ApiResponse;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.utils.PhoneNumberUtil;
import com.zslide.widget.RequestButton;

import butterknife.BindView;
import rx.Observable;

/**
 * Created by chulwoo on 16. 4. 14..
 */
public class PhoneCertificationCodeRequestFragment extends com.zslide.view.base.BaseFragment {

    @BindView(R.id.phoneNumber) TextView phoneNumberView;
    @BindView(R.id.requestCertificationCode) RequestButton certificationCodeRequestButton;
    private OnCertificationCodeRequestedListener listener;

    public static PhoneCertificationCodeRequestFragment newInstance() {
        Bundle args = new Bundle();

        PhoneCertificationCodeRequestFragment instance = new PhoneCertificationCodeRequestFragment();
        instance.setArguments(args);
        return instance;
    }

    public PhoneCertificationCodeRequestFragment setOnCertificationCodeRequstedListener(
            OnCertificationCodeRequestedListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_phone_certification_code_request;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String phoneNumber = PhoneNumberUtil.getPhoneNumber(view.getContext());
        if (TextUtils.isEmpty(phoneNumber)) {
            phoneNumberView.setText(R.string.message_empty_phone_number);
            certificationCodeRequestButton.setEnabled(false);
        } else {
            phoneNumberView.setText(phoneNumber);
        }

        certificationCodeRequestButton.action(
                Observable.create(emitter -> AuthenticationManager.getInstance()
                        .requestPhoneCertificationCode(getActivity())
                        .filter(__ -> !emitter.isUnsubscribed())
                        .subscribe(emitter::onNext, emitter::onError, emitter::onCompleted)),
                this::onCertificationCodeRequested, this::onFailureCertificationCodeRequest);
    }

    public void onCertificationCodeRequested(ApiResponse result) {
        if (result.isSuccess()) {
            onSuccessCertificationCodeRequest();
            if (listener != null) {
                listener.onCertificationCodeRequested();
            }
        } else {
            showConfirmAlertDialog(result.getMessage());
            onFailureCertificationCodeRequest();
        }
    }

    private void onSuccessCertificationCodeRequest() {
        certificationCodeRequestButton.setProgressing(false);
    }

    private void onFailureCertificationCodeRequest() {
        certificationCodeRequestButton.setProgressing(false);
    }

    private void onFailureCertificationCodeRequest(Throwable e) {
        handleError(e);
        onFailureCertificationCodeRequest();
    }

    private void showConfirmAlertDialog(String message) {
        SimpleAlertDialog
                .newInstance(message)
                .show(getFragmentManager(), "confirm_dialog");
    }

    public interface OnCertificationCodeRequestedListener {
        void onCertificationCodeRequested();
    }
}