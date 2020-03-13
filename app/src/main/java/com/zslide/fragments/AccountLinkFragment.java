package com.zslide.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.activities.BaseActivity;
import com.zslide.data.UserManager;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.models.Sex;
import com.zslide.models.response.SimpleApiResponse;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.ZLog;
import com.zslide.view.auth.KakaoLinkActivity;
import com.zslide.view.auth.NaverLinkActivity;
import com.zslide.widget.RequestButton;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 2016. 11. 23..
 */

public class AccountLinkFragment extends BaseFragment {

    private static final int REQUEST_KAKAO_LINK = 100;
    private static final int REQUEST_NAVER_LINK = 101;
    @BindView(R.id.email) EditText emailView;
    @BindView(R.id.emailCertification) RequestButton certificationButton;
    @BindView(R.id.kakao) TextView kakaoButton;
    @BindView(R.id.naver) TextView naverButton;
    private OnCertificationCodeRequestedListener listener;
    // social information
    private String refreshToken;
    private String accessToken;
    private String uid;
    private String email;
    private String name;
    private String nickname;
    private Sex sex;

    public static AccountLinkFragment newInstance() {

        Bundle args = new Bundle();

        AccountLinkFragment fragment = new AccountLinkFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        certificationButton.setOnClickListener(view1 -> {
            SimpleAlertDialog.newInstance(getString(R.string.message_confirm_email_certification, getEmail()), true)
                    .setOnConfirmListener(() -> {
                        if (TextUtils.isEmpty(getEmail())) {
                            SimpleAlertDialog.newInstance("이메일을 확인해주세요.")
                                    .show(getFragmentManager(), "check");
                        } else {
                            certificationButton.setProgressing(true);
                            ZummaApi.general().requestEmailCertificationCode(getEmail())
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .doOnTerminate(() -> certificationButton.setProgressing(false))
                                    .subscribe(AccountLinkFragment.this::onCertificationCodeRequested,
                                            AccountLinkFragment.this::onFailureCertificationCodeRequest);
                        }
                    })
                    .show(getFragmentManager(), "confirm");
        });
    }

    public void setOnCertificationCodeRequestedListener(OnCertificationCodeRequestedListener listener) {
        this.listener = listener;
    }

    public void onCertificationCodeRequested(SimpleApiResponse result) {
        if (result.isSuccess()) {
            if (listener != null) {
                listener.onCertificationCodeRequested();
            }
            getFragmentManager().beginTransaction()
                    .replace(R.id.fragment, EmailCertificationFragment.newInstance(getEmail()))
                    .commit();
        } else {
            showConfirmAlertDialog(result.getMessage());
        }
    }

    private void onFailureCertificationCodeRequest(Throwable e) {
        ZummaApiErrorHandler.handleError(e);
    }

    private void showConfirmAlertDialog(String message) {
        SimpleAlertDialog
                .newInstance(message)
                .show(getFragmentManager(), "confirm_dialog");
    }

    public String getEmail() {
        return emailView.getText().toString().trim();
    }

    @OnClick(R.id.kakao)
    public void linkKakao() {
        startActivityForResult(new Intent(getActivity(), KakaoLinkActivity.class), REQUEST_KAKAO_LINK);
    }

    @OnClick(R.id.naver)
    public void linkNaver() {
        startActivityForResult(new Intent(getActivity(), NaverLinkActivity.class), REQUEST_NAVER_LINK);
    }

    private boolean isSocialAuthRequest(int requestCode) {
        return requestCode >= 100 && requestCode < 200;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (isSocialAuthRequest(requestCode)) {
            ZLog.d(this, "social login success");
            if (resultCode == Activity.RESULT_OK) {
                handleSocialAuthData(requestCode, data);
                ((BaseActivity) getActivity()).showTitleProgress();
                switch (requestCode) {
                    case REQUEST_KAKAO_LINK:
                        ZummaApi.user().kakaoLink(refreshToken, accessToken)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::onSuccessLink, this::onFailureLink);
                        break;
                    case REQUEST_NAVER_LINK:
                        ZummaApi.user().naverLink(refreshToken, accessToken, uid)
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(this::onSuccessLink, this::onFailureLink);
                        break;
                }
            } else {
                ZLog.d(this, "social login failure");
                onFailureLink();
            }
        }
    }

    public void onSuccessLink(SimpleApiResponse result) {
        if (result.isSuccess()) {
            UserManager.getInstance().fetchUser()
                    .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        BaseActivity activity = (BaseActivity) getActivity();
                        activity.hideTitleProgress();
                        Toast.makeText(activity, R.string.message_success_auth_link, Toast.LENGTH_SHORT).show();
                        activity.finish();
                    });
        } else {
            Toast.makeText(getActivity(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void onFailureLink(Throwable throwable) {
        ZummaApiErrorHandler.handleError(throwable);
        onFailureLink();
    }

    public void onFailureLink() {
        Toast.makeText(getActivity(), R.string.message_failure_auth_link, Toast.LENGTH_SHORT).show();
        ((BaseActivity) getActivity()).hideTitleProgress();
    }

    private void handleSocialAuthData(int requestCode, Intent data) {
        if (requestCode == REQUEST_NAVER_LINK) {
            this.refreshToken = data.getStringExtra(IntentConstants.EXTRA_REFRESH_TOKEN);
            this.accessToken = data.getStringExtra(IntentConstants.EXTRA_ACCESS_TOKEN);
            this.uid = data.getStringExtra(IntentConstants.EXTRA_ID);
            this.email = data.getStringExtra(IntentConstants.EXTRA_EMAIL);
            this.name = data.getStringExtra(IntentConstants.EXTRA_NAME);
            this.nickname = data.getStringExtra(IntentConstants.EXTRA_NICKNAME);
            this.sex = (Sex) data.getSerializableExtra(IntentConstants.EXTRA_SEX);
        } else if (requestCode == REQUEST_KAKAO_LINK) {
            this.refreshToken = data.getStringExtra(IntentConstants.EXTRA_REFRESH_TOKEN);
            this.accessToken = data.getStringExtra(IntentConstants.EXTRA_ACCESS_TOKEN);
            this.uid = "";
            this.email = "";
            this.name = "";
            this.nickname = "";
            this.sex = null;
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_account_link;
    }

    public interface OnCertificationCodeRequestedListener {
        void onCertificationCodeRequested();
    }
}
