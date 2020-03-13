package com.zslide.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.Config;
import com.zslide.R;
import com.zslide.data.AuthenticationManager;
import com.zslide.data.remote.exception.UnsignedUserException;
import com.zslide.data.remote.response.ApiResponse;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.receivers.CertificationCodeSMSReceiver;
import com.zslide.utils.DLog;
import com.zslide.utils.PermissionUtil;
import com.zslide.utils.TimeUtil;
import com.zslide.widget.RequestButton;

import butterknife.BindView;
import butterknife.OnEditorAction;
import butterknife.OnTextChanged;
import rx.Completable;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chulwoo on 16. 4. 14..
 */
public class PhoneCertificationFragment extends com.zslide.view.base.BaseFragment {

    private static final int REQUEST_PERMISSION_SMS = 100;
    private static final long COUNTDOWN_MILLS = (Config.DEBUG) ? 20 * 1000 : 3 * 60 * 1000;
    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.certificationCode) EditText certificationCodeView;
    @BindView(R.id.countdown) TextView countdownView;
    @BindView(R.id.requestCertificationCode) RequestButton certificationCodeRequestButton;
    @BindView(R.id.certification) RequestButton certificationButton;
    private CertificationCodeSMSReceiver smsReceiver;
    private CountDownTimer certificationTimer;
    private Subscription certificationCodeSubscription;
    private boolean certificationFinished = false;
    private Callback callback;

    public static PhoneCertificationFragment newInstance() {
        Bundle args = new Bundle();

        PhoneCertificationFragment instance = new PhoneCertificationFragment();
        instance.setArguments(args);
        return instance;
    }

    public PhoneCertificationFragment setCallback(Callback callback) {
        this.callback = callback;
        return this;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (PermissionUtil.checkAndRequestPermission(this, REQUEST_PERMISSION_SMS, Manifest.permission.RECEIVE_SMS)) {
            registerCertificationCodeReceiver();
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_phone_certification;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        certificationCodeRequestButton.action(
                Observable.create(subscriber -> AuthenticationManager.getInstance()
                        .requestPhoneCertificationCode(getActivity())
                        .filter(__ -> !subscriber.isUnsubscribed())
                        .subscribe(subscriber::onNext, subscriber::onError, subscriber::onCompleted)),
                this::onCertificationCodeRequested,
                this::onFailureCertificationCodeRequest);

        certificationButton.action(Observable.defer(() -> Observable.just(getCertificationCode()))
                        .flatMapCompletable(this::checkPhoneCertificationCode),
                null,
                this::onFailureAuthentication,
                this::onSuccessAuthentication);
        startCountdown();
    }

    private Completable checkPhoneCertificationCode(String code) {
        return Completable.create(subscriber -> AuthenticationManager.getInstance()
                .checkPhoneCertificationCode(getActivity(), code)
                .subscribe(subscriber::onCompleted, subscriber::onError));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_SMS:
                if (PermissionUtil.verifyPermissions(grantResults)) {
                    registerCertificationCodeReceiver();
                }
                break;
        }
    }

    private void registerCertificationCodeReceiver() {
        Activity activity = getActivity();
        if (activity != null) {
            smsReceiver = new CertificationCodeSMSReceiver();
            certificationCodeSubscription = smsReceiver.getCertificationCodePublisher()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(code -> {
                        certificationCodeView.setText(code);
                        certificationButton.performClick();
                    });
            activity.registerReceiver(smsReceiver, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
        }
    }

    private void unregisterCertificationCodeReceiver() {
        Activity activity = getActivity();
        if (activity != null && smsReceiver != null) {
            certificationCodeSubscription.unsubscribe();
            certificationCodeSubscription = null;
            activity.unregisterReceiver(smsReceiver);
            smsReceiver = null;
        }
    }

    @Override
    public void onStop() {
        unregisterCertificationCodeReceiver();
        super.onStop();
    }

    private void onCertificationCodeRequested(ApiResponse result) {
        if (result.isSuccess()) {
            onSuccessCertificationCodeRequest();
        } else {
            showConfirmAlertDialog(result.getMessage());
            onFailureCertificationCodeRequest();
        }
    }

    private void onSuccessCertificationCodeRequest() {
        certificationCodeRequestButton.setProgressing(false);
        certificationCodeRequestButton.setEnabled(false);
        messageView.setText(R.string.message_certification_phone);
        restartCountdown();
    }

    private void onFailureCertificationCodeRequest() {
        certificationCodeRequestButton.setProgressing(false);
        certificationCodeRequestButton.setEnabled(true);
    }

    private void onFailureCertificationCodeRequest(Throwable e) {
        Toast.makeText(getContext(), R.string.message_failure_request, Toast.LENGTH_SHORT).show();
        handleError(e);
        onFailureCertificationCodeRequest();
    }

    @OnTextChanged(value = R.id.certificationCode, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    protected void onChangedCertificationCode(CharSequence certificationCode) {
        certificationButton.setEnabled(certificationCode.length() == 5 && certificationTimer != null);
    }

    @OnEditorAction(R.id.certificationCode)
    public boolean onActionDone() {
        if (certificationButton.isEnabled()) {
            certificationButton.performClick();
        }
        return true;
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
                    countdownView.setText(TimeUtil.getCountdownText(getContext(), millisUntilFinished));
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

    private void onSuccessAuthentication() {
        DLog.e(this, "onSuccessAuthentication");
        if (callback != null) {
            callback.onAuthenticated();
        }
        certificationFinished = true;
        stopCountdown();
        certificationButton.setProgressing(false);
        certificationCodeRequestButton.setEnabled(false);
        certificationCodeView.setEnabled(false);
        certificationButton.setEnabled(false);
    }

    private void onFailureAuthentication(Throwable e) {
        if (e instanceof UnsignedUserException) {
            if (callback != null) {
                callback.onCertificated();
            }
        } else {
            handleError(e);
            showConfirmAlertDialog(e.getMessage());
        }
        certificationButton.setProgressing(false);
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

    public interface Callback {

        void onAuthenticated();

        void onCertificated();
    }
}
