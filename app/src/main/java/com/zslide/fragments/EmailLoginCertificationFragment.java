package com.zslide.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.Config;
import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.data.remote.response.AuthenticationResponse;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.response.SimpleApiResponse;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.TimeUtil;
import com.zslide.widget.RequestButton;

import butterknife.BindView;
import butterknife.OnTextChanged;
import rx.Observable;

/**
 * Created by chulwoo on 16. 4. 14..
 */
public class EmailLoginCertificationFragment extends BaseFragment {

    private static final long COUNTDOWN_MILLS = (Config.DEBUG) ? 20 * 1000 : 10 * 60 * 1000;
    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.certificationCode) EditText certificationCodeView;
    @BindView(R.id.countdown) TextView countdownView;
    @BindView(R.id.requestCertificationCode) RequestButton certificationCodeRequestButton;
    @BindView(R.id.certification) RequestButton certificationButton;
    private CountDownTimer certificationTimer;
    private boolean certificationFinished = false;
    private String email;
    private OnCertificatedListener listener;

    public static EmailLoginCertificationFragment newInstance(String email) {
        Bundle args = new Bundle();
        args.putString(IntentConstants.EXTRA_EMAIL, email);

        EmailLoginCertificationFragment instance = new EmailLoginCertificationFragment();
        instance.setArguments(args);
        return instance;
    }

    public EmailLoginCertificationFragment setOnCertificatedListener(OnCertificatedListener listener) {
        this.listener = listener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_email_login_certification;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(IntentConstants.EXTRA_EMAIL, email);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            email = savedInstanceState.getString(IntentConstants.EXTRA_EMAIL);
        }
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            email = getArguments().getString(IntentConstants.EXTRA_EMAIL);
        } else {
            email = savedInstanceState.getString(IntentConstants.EXTRA_EMAIL);
        }

        messageView.setText(getString(R.string.message_certification_email, email));
        certificationCodeRequestButton.action(
                ZummaApi.general().requestEmailLoginCertificationCode(email),
                this::onCertificationCodeRequested,
                this::onFailureCertificationCodeRequest);
        certificationButton.action(
                Observable.<String>create(subscriber -> subscriber.onNext(getCertificationCode()))
                        .flatMap(code -> ZummaApi.general().emailLoginCertification(email, code)),
                this::onSuccessCertification,
                this::onFailureCertification);
        startCountdown();
    }

    private void onCertificationCodeRequested(SimpleApiResponse response) {
        if (response.isSuccess()) {
            onSuccessCertificationCodeRequest();
        } else {
            showConfirmAlertDialog(response.getMessage());
            onFailureCertificationCodeRequest();
        }
    }

    private void onSuccessCertificationCodeRequest() {
        certificationCodeRequestButton.setProgressing(false);
        certificationCodeRequestButton.setEnabled(false);
        messageView.setText(getString(R.string.message_certification_email, email));
        restartCountdown();
    }

    private void onFailureCertificationCodeRequest() {
        certificationCodeRequestButton.setProgressing(false);
        certificationCodeRequestButton.setEnabled(true);
    }

    private void onFailureCertificationCodeRequest(Throwable e) {
        Toast.makeText(getContext(), R.string.message_failure_request, Toast.LENGTH_SHORT).show();
        ZummaApiErrorHandler.handleError(e);
        onFailureCertificationCodeRequest();
    }

    @OnTextChanged(value = R.id.certificationCode, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onChangedCertificationCode(CharSequence certificationCode) {
        certificationButton.setEnabled(certificationCode.length() == 5 && certificationTimer != null);
    }

    public void restartCountdown() {
        stopCountdown();
        startCountdown();
    }

    public void startCountdown() {
        countdownView.setVisibility(View.VISIBLE);
        certificationTimer = new CountDownTimer(COUNTDOWN_MILLS, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    countdownView.setText(
                            TimeUtil.getCountdownText(getContext(), millisUntilFinished));
                } catch (Exception e) {
                    // do nothing
                }
            }

            @Override
            public void onFinish() {
                if (!certificationFinished) {
                    countdownView.setText(R.string.label_certification_timeout);
                    messageView.setText(R.string.message_certification_timeout);
                    certificationCodeView.setText("");
                    certificationCodeRequestButton.setEnabled(true);
                    certificationButton.setEnabled(false);
                }

                certificationTimer = null;
            }
        }.start();
    }

    public void stopCountdown() {
        if (certificationTimer != null) {
            certificationTimer.cancel();
        }
    }

    public String getCertificationCode() {
        return certificationCodeView.getText().toString();
    }

    private void onSuccessCertification(AuthenticationResponse result) {
        if (result.isSuccess() && result.getUser() != null) {
            if (listener != null) {
                listener.onCertificated(result);
            }
        } else {
            showConfirmAlertDialog(result.getMessage());
            onFailureCertification();
        }
    }

    private void onFailureCertification() {
        certificationButton.setProgressing(false);
    }

    private void onFailureCertification(Throwable e) {
        ZummaApiErrorHandler.handleError(e);
        onFailureCertification();
    }

    private void showConfirmAlertDialog(String message) {
        SimpleAlertDialog
                .newInstance(message)
                .show(getFragmentManager(), "confirm_dialog");
    }

    @Override
    public void onDestroy() {
        stopCountdown();
        super.onDestroy();
    }

    public interface OnCertificatedListener {
        void onCertificated(AuthenticationResponse response);
    }
}
