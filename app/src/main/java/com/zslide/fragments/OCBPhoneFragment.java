package com.zslide.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.Config;
import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.OCB;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.PackageUtil;
import com.zslide.utils.StringUtil;
import com.zslide.utils.TimeUtil;
import com.zslide.widget.RequestButton;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 6. 27..
 */
public class OCBPhoneFragment extends OCBUseFragment {

    private static final long COUNTDOWN_MILLS = (Config.DEBUG) ? 20 * 1000 : 2 * 60 * 1000;
    @BindString(R.string.message_certification_timeout) String CERTIFICATION_TIMEOUT;
    @BindViews({R.id.phoneNumber1, R.id.phoneNumber2, R.id.phoneNumber3}) List<EditText> phoneNumberViews;
    @BindView(R.id.alertContainer) ViewGroup alertContainer;
    @BindView(R.id.certification) RequestButton certificationButton;
    @BindView(R.id.certificationConfirmContainer) ViewGroup certificationConfirmContainer;
    @BindView(R.id.certificationConfirmCountdownContainer) ViewGroup certificationConfirmCountdownContainer;
    @BindView(R.id.certificationConfirmCountdown) TextView certificationConfirmCountdownView;
    @BindView(R.id.certificationConfirmProgress) CircularProgressBar certificationConfirmProgressView;
    private CountDownTimer certificationTimer;
    private boolean certificationFinished = false;
    private String type;
    private OCB authResult;

    public static OCBPhoneFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(IntentConstants.EXTRA_TYPE, type);

        OCBPhoneFragment fragment = new OCBPhoneFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected String getAuthId() {
        return getPhoneNumber();
    }

    @Override
    protected String getAuthPassword() {
        return "";
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_ocb_phone;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        super.setupLayout(view, savedInstanceState);
        type = getArguments().getString(IntentConstants.EXTRA_TYPE);
        setType(type);
        certificationButton.setEnableForceStop(false);
        initCertificationView();
    }

    @OnClick(R.id.certification)
    public void certification() {
        if (!PackageUtil.isPackageUsable(getContext(), "com.skmc.okcashbag.home_google")) {
            Toast.makeText(getContext(), "OK캐쉬백 어플리케이션을 설치한 뒤 이용해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        certificationButton.action(
                ZummaApi.ocb().auth(getPhoneNumber(), type),
                ocb -> {
                    if (ocb.isSuccess()) {
                        startCertificationCountdown(ocb);
                    } else {
                        Toast.makeText(getContext(), ocb.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, ZummaApiErrorHandler::handleError);
    }

    @OnClick(R.id.certificationConfirmContainer)
    public void confirmCertification() {
        cancelCountdown();
        if (authResult == null) {
            Toast.makeText(getContext(), "문제가 발생했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            initCertificationView();
            return;
        }

        certificationConfirmCountdownContainer.setVisibility(View.GONE);
        certificationConfirmProgressView.setVisibility(View.VISIBLE);
        ZummaApi.ocb().inquiry(authResult)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ocb -> {
                    certificationConfirmContainer.setVisibility(View.GONE);
                    alertContainer.setVisibility(View.GONE);
                    for (TextView phoneNumberView : phoneNumberViews) {
                        phoneNumberView.setEnabled(false);
                    }
                    bind(ocb);
                }, e -> {
                    initCertificationView();
                    ZummaApiErrorHandler.handleError(e);
                });
    }

    public void startCertificationCountdown(OCB authResult) {
        this.authResult = authResult;
        certificationButton.setVisibility(View.GONE);
        certificationConfirmContainer.setVisibility(View.VISIBLE);
        certificationTimer = new CountDownTimer(COUNTDOWN_MILLS, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                try {
                    certificationConfirmCountdownView.setText(
                            StringUtil.format("(%s)",
                                    TimeUtil.getCountdownText(getContext(), millisUntilFinished)));
                } catch (Exception e) {
                    // do nothing
                }
            }

            @Override
            public void onFinish() {
                try {
                    certificationTimer = null;
                    if (!certificationFinished) {
                        OCBPhoneFragment.this.authResult = null;
                        SimpleAlertDialog
                                .newInstance(CERTIFICATION_TIMEOUT)
                                .show(getFragmentManager(), "confirm_dialog");
                        initCertificationView();
                    }
                } catch (Exception e) {
                    // 액티비티가 화면상에 없을경우 에러발생
                }
            }
        }.start();
    }

    public void cancelCountdown() {
        if (certificationTimer != null) {
            certificationTimer.cancel();
        }
    }

    private void initCertificationView() {
        certificationConfirmCountdownContainer.setVisibility(View.VISIBLE);
        certificationConfirmProgressView.setVisibility(View.GONE);
        certificationConfirmContainer.setVisibility(View.GONE);
        certificationButton.setVisibility(View.VISIBLE);
        alertContainer.setVisibility(View.VISIBLE);
        onInputChanged();
    }

    @OnTextChanged({R.id.phoneNumber1, R.id.phoneNumber2, R.id.phoneNumber3})
    protected void onInputChanged() {
        boolean isCompleted = getPhoneNumber().length() >= 10;
        certificationButton.setEnabled(isCompleted);
    }

    public String getPhoneNumber() {
        StringBuilder phoneNumberBuilder = new StringBuilder();
        for (TextView phoneNumberView : phoneNumberViews) {
            phoneNumberBuilder.append(phoneNumberView.getText().toString());
        }

        return phoneNumberBuilder.toString();
    }

    @Override
    public void onDestroy() {
        cancelCountdown();
        initCertificationView();
        super.onDestroy();
    }
}
