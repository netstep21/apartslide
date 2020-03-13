package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.zslide.R;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.response.SimpleApiResponse;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.widget.RequestButton;

import butterknife.BindView;
import rx.Observable;

/**
 * Created by chulwoo on 16. 4. 14..
 */
public class EmailLoginCertificationCodeRequestFragment extends BaseFragment {

    @BindView(R.id.email) EditText emailView;
    @BindView(R.id.requestCertificationCode) RequestButton certificationCodeRequestButton;
    private OnCertificationCodeRequestedListener listener;

    public static EmailLoginCertificationCodeRequestFragment newInstance() {
        Bundle args = new Bundle();

        EmailLoginCertificationCodeRequestFragment instance = new EmailLoginCertificationCodeRequestFragment();
        instance.setArguments(args);
        return instance;
    }

    public EmailLoginCertificationCodeRequestFragment setOnCertificationCodeRequestedListener(
            OnCertificationCodeRequestedListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_email_login_certification_code_request;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        certificationCodeRequestButton.action(
                Observable.<String>create(subscriber -> subscriber.onNext(getEmail()))
                        .flatMap(ZummaApi.general()::requestEmailLoginCertificationCode),
                this::onCertificationCodeRequested, this::onFailureCertificationCodeRequest);
    }

    public String getEmail() {
        return emailView.getText().toString().trim();
    }

    public void onCertificationCodeRequested(SimpleApiResponse result) {
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
        ZummaApiErrorHandler.handleError(e);
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