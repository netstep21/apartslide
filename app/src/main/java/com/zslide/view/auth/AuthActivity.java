package com.zslide.view.auth;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.Config;
import com.zslide.IntentConstants;
import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.AuthenticationManager;
import com.zslide.data.model.AuthType;
import com.zslide.data.remote.request.SignupRequest;
import com.zslide.data.remote.request.SocialLoginRequest;
import com.zslide.data.remote.response.SignUpCheckResponse;
import com.zslide.dialogs.SimpleAlertDialog;
import com.zslide.fragments.EmailLoginCertificationCodeRequestFragment;
import com.zslide.fragments.EmailLoginCertificationFragment;
import com.zslide.fragments.LoginFragment;
import com.zslide.fragments.PersonalInformationInputFragment;
import com.zslide.fragments.PhoneCertificationCodeRequestFragment;
import com.zslide.fragments.PhoneCertificationFragment;
import com.zslide.fragments.RecommendCodeInputFragment;
import com.zslide.models.Sex;
import com.zslide.network.RetrofitException;
import com.zslide.utils.DLog;
import com.zslide.utils.DisplayUtil;
import com.zslide.utils.EventLogger;
import com.zslide.utils.PhoneNumberUtil;
import com.zslide.utils.ZLog;
import com.zslide.view.base.BaseActivity;
import com.zslide.widget.AuthButton;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 3. 30..
 * <p>
 * Udpated by chulwoo on 18. 1. 8..
 * UserManager 사용하도록 변경
 * <p>
 * TODO: 2018. 1. 8. MVVM 적용
 */
@SuppressLint("MissingPermission")
public class AuthActivity extends BaseActivity implements AuthButton.Authable {

    private static final String TAG_EMAIL_LOGIN_CERTIFICATION_CODE_REQUEST = "email_login_certification_code_request";
    private static final String TAG_PERSONAL_INFORMATION = "personal_information";
    private static final String TAG_RECOMMEND_CODE = "recommend_code";

    private static final String FLOW_NORMAL = "일반";
    private static final String FLOW_SOCIAL = "소셜";
    private static final String EVENT_REQUEST_CERTIFICATION_CODE = "인증_코드_요청";
    private static final String EVENT_CERTIFICATION = "인증";
    private static final String EVENT_INPUT_INFO = "개인_정보_입력";
    private static final String EVENT_INPUT_INVITE_CODE = "추천인_코드_입력";
    private static final String EVENT_SELECT_SOCIAL_TYPE = "소셜_로그인_선택";
    private static final String EVENT_SIGNUP = "가입";
    private static final String EVENT_LOGIN = "로그인";

    private static final int REQUEST_KAKAO_LOGIN = 100;
    private static final int REQUEST_NAVER_LOGIN = 101;

    @BindView(R.id.logo) View logoView;
    @BindView(R.id.fragment) ViewGroup fragmentContainer;
    @BindView(R.id.progress) ViewGroup progressView;
    @BindView(R.id.into) ViewGroup intoButton;
    @BindView(R.id.intoLabel) TextView intoLabelView;

    @BindDimen(R.dimen.splash_logo_margin) int SPLASH_LOGO_MARGIN;
    @BindDimen(R.dimen.auth_logo_margin) int AUTH_LOGO_MARGIN;

    @BindString(R.string.label_into_login) String LOGIN_LABEL;
    @BindString(R.string.label_into_signup) String SIGNUP_LABEL;

    private String currentFlow = "";

    /**
     * variables for auth
     */
    private ValueAnimator showAnimator;
    private ValueAnimator hideAnimator;
    private AuthType currentAuthType;

    // social information
    private String refreshToken;
    private String accessToken;
    private String uid;
    private String email;
    private String name;
    private String nickname;
    private Sex sex;
    private Disposable currentRequest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        int animateMargin = SPLASH_LOGO_MARGIN - AUTH_LOGO_MARGIN;
        logoView.setTranslationY(animateMargin);
        fragmentContainer.setAlpha(0);

        if (Config.DEBUG) {
            PhoneNumberUtil.showSelectTestAccountDialog(this, this::startSignup);
        } else {
            startSignup();
        }

        logoView.animate()
                .translationY(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        overridePendingTransition(0, 0);
                    }
                })
                .setDuration(650)
                .start();
        fragmentContainer.animate()
                .alpha(1)
                .setStartDelay(300)
                .setDuration(600)
                .start();

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            FragmentManager manager = getSupportFragmentManager();
            Fragment fragment = manager.findFragmentById(R.id.fragment);
            if (fragment instanceof PhoneCertificationCodeRequestFragment) {
                if (manager.getBackStackEntryCount() == 0) {
                    initFormData();
                    intoLabelView.setText(R.string.label_into_login);
                    showIntoButton();
                } else {
                    hideIntoButton();
                }
            } else if (fragment instanceof LoginFragment) {
                intoLabelView.setText(R.string.label_into_signup);
                showIntoButton();
            } else {
                hideIntoButton();
            }
        });
    }

    private void initFormData() {
        currentFlow = FLOW_NORMAL;
        this.currentAuthType = AuthType.PHONE;
        this.refreshToken = "";
        this.accessToken = "";
        this.uid = "";
        this.name = "";
        this.nickname = "";
        this.sex = null;
    }

    private String getEmail() {
        EmailLoginCertificationCodeRequestFragment fragment =
                (EmailLoginCertificationCodeRequestFragment) getSupportFragmentManager()
                        .findFragmentByTag(TAG_EMAIL_LOGIN_CERTIFICATION_CODE_REQUEST);
        return fragment.getEmail();
    }

    private int getBirthYear() {
        PersonalInformationInputFragment fragment =
                (PersonalInformationInputFragment) getSupportFragmentManager().findFragmentByTag(TAG_PERSONAL_INFORMATION);

        int birthYear = 0;
        if (fragment != null) {
            birthYear = fragment.getBirthYear();
        }
        return birthYear;
    }

    private Sex getSex() {
        PersonalInformationInputFragment fragment =
                (PersonalInformationInputFragment) getSupportFragmentManager().findFragmentByTag(TAG_PERSONAL_INFORMATION);
        Sex sex = null;
        if (fragment != null) {
            sex = fragment.getSex();
        }

        return sex;
    }

    private String getRecommendCode() {
        RecommendCodeInputFragment fragment =
                (RecommendCodeInputFragment) getSupportFragmentManager().findFragmentByTag(TAG_RECOMMEND_CODE);
        String code = "";
        if (fragment != null) {
            code = fragment.getRecommendCode();
        }

        return code;
    }

    private boolean isSocialAuthRequest(int requestCode) {
        return requestCode >= 100 && requestCode < 200;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ZLog.d(this, "onActivityResult");
        if (isSocialAuthRequest(requestCode)) {
            ZLog.d(this, "social login success");
            if (resultCode == RESULT_OK) {
                handleSocialAuthData(requestCode, data);
                showProgress();
                AuthenticationManager.getInstance().isAuthenticated(getApplicationContext())
                        .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                        .subscribe(this::onCheckedAuthenticate, this::onFailureAuthentication);
            } else {
                ZLog.d(this, "activity result failure");
                onFailureAuthentication(new IllegalStateException());
            }
        }
    }

    private void handleSocialAuthData(int requestCode, Intent data) {
        if (requestCode == REQUEST_NAVER_LOGIN) {
            this.refreshToken = data.getStringExtra(IntentConstants.EXTRA_REFRESH_TOKEN);
            this.accessToken = data.getStringExtra(IntentConstants.EXTRA_ACCESS_TOKEN);
            this.uid = data.getStringExtra(IntentConstants.EXTRA_ID);
            this.email = data.getStringExtra(IntentConstants.EXTRA_EMAIL);
            this.name = data.getStringExtra(IntentConstants.EXTRA_NAME);
            this.nickname = data.getStringExtra(IntentConstants.EXTRA_NICKNAME);
            this.sex = (Sex) data.getSerializableExtra(IntentConstants.EXTRA_SEX);
        } else if (requestCode == REQUEST_KAKAO_LOGIN) {
            this.refreshToken = data.getStringExtra(IntentConstants.EXTRA_REFRESH_TOKEN);
            this.accessToken = data.getStringExtra(IntentConstants.EXTRA_ACCESS_TOKEN);
            this.uid = "";
            this.email = "";
            this.name = "";
            this.nickname = "";
            this.sex = null;
        }
    }

    private void onCheckedAuthenticate(SignUpCheckResponse result) {
        if (result.isSuccess()) {
            /**
             * v3 기준 AuthCheckResult의 type은 1개짜리 리스트를 반환.
             * 다를 경우 로그인 하지 못함.
             */
            boolean authenticated = false;
            for (AuthType type : result.getTypes()) {
                if (type.equals(currentAuthType)) {
                    authenticated = true;
                    break;
                }
            }

            if (!authenticated) {
                String type = "";
                switch (result.getTypes().get(0)) {
                    case EMAIL:
                        type = getString(R.string.label_link_account_email);
                        break;
                    case KAKAO:
                        type = getString(R.string.label_link_account_kakao);
                        break;
                    case NAVER:
                        type = getString(R.string.label_link_account_naver);
                        break;
                    case PHONE:
                        type = getString(R.string.label_phone);
                }

                hideProgress();
                SimpleAlertDialog.newInstance(getString(R.string.message_already_signup, type))
                        .setOnConfirmListener(this::startSocialLogin)
                        .show(getSupportFragmentManager(), "already_exists");
            } else {
                socialLogin();
            }
        } else {
            // 가입 안된 번호일 경우, 해당 소셜 아이디가 가입됐는지 확인
            SocialLoginRequest.Builder requestBuilder = new SocialLoginRequest.Builder(
                    PhoneNumberUtil.getPhoneNumber(this), currentAuthType)
                    .setRefreshToken(refreshToken)
                    .setAccessToken(accessToken)
                    .setUid(uid);
            AuthenticationManager.getInstance().isSocialAuthenticated(requestBuilder.build())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.isSuccess()) {
                            socialLogin();
                        } else {
                            hideProgress();
                            startPhoneCertificationCodeRequest();
                        }
                    }, this::onFailureAuthentication);
        }
    }

    @Override
    protected void onStop() {
        if (currentRequest != null && !currentRequest.isDisposed()) {
            currentRequest.dispose();
            hideProgress();
        }
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        if (progressView.getVisibility() == View.VISIBLE) {
            return;
        }

        super.onBackPressed();
        FragmentManager manager = getSupportFragmentManager();
        if (manager.findFragmentById(R.id.fragment) instanceof PhoneCertificationFragment) {
            manager.popBackStackImmediate();
        }
    }

    public void startSignup() {
        initFormData();
        logEvent(EVENT_REQUEST_CERTIFICATION_CODE);
        intoLabelView.setText(R.string.label_into_login);
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        manager.beginTransaction()
                .replace(R.id.fragment, PhoneCertificationCodeRequestFragment.newInstance()
                        .setOnCertificationCodeRequstedListener(this::startPhoneCertification))
                .commit();
    }

    public void startPhoneCertificationCodeRequest() {
        logEvent(EVENT_REQUEST_CERTIFICATION_CODE);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, PhoneCertificationCodeRequestFragment.newInstance()
                        .setOnCertificationCodeRequstedListener(this::startPhoneCertification))
                .addToBackStack(null)
                .commit();
    }

    public void startEmailCertificationCodeRequest() {
        logEvent(EVENT_REQUEST_CERTIFICATION_CODE);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment,
                        EmailLoginCertificationCodeRequestFragment.newInstance()
                                .setOnCertificationCodeRequestedListener(() ->
                                        startEmailCertification(getEmail())),
                        TAG_EMAIL_LOGIN_CERTIFICATION_CODE_REQUEST)
                .addToBackStack(null)
                .commit();
    }

    public void startSocialLogin() {
        currentFlow = FLOW_SOCIAL;
        this.refreshToken = "";
        this.accessToken = "";
        this.uid = "";
        this.name = "";
        this.nickname = "";
        this.sex = null;

        intoLabelView.setText(R.string.label_into_signup);
        logEvent(EVENT_SELECT_SOCIAL_TYPE);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, LoginFragment.newInstance().setAuthable(this))
                .addToBackStack(null)
                .commit();
    }

    private void startPhoneCertification() {
        logEvent(EVENT_CERTIFICATION);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, PhoneCertificationFragment.newInstance()
                        .setCallback(new PhoneCertificationFragment.Callback() {
                            @Override
                            public void onAuthenticated() {
                                DLog.e(this, "onAuthenticated");
                                onSuccessAuthentication(false);
                            }

                            @Override
                            public void onCertificated() {
                                startInputPersonalInformation();
                            }
                        }))
                .addToBackStack(null)
                .commit();
        DisplayUtil.hideKeyboard(this);
    }

    private void startEmailCertification(String email) {
        logEvent(EVENT_CERTIFICATION);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, EmailLoginCertificationFragment.newInstance(email)
                        .setOnCertificatedListener(response -> onSuccessAuthentication(response.getUser().isNull())))
                .addToBackStack(null)
                .commit();
        DisplayUtil.hideKeyboard(this);
    }

    private void startInputPersonalInformation() {
        logEvent(EVENT_INPUT_INFO);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, PersonalInformationInputFragment.newInstance(sex)
                                .setOnInputCompletedListener(this::startInputRecommendCode),
                        TAG_PERSONAL_INFORMATION)
                .addToBackStack(null)
                .commit();
        DisplayUtil.hideKeyboard(this);
    }

    private void startInputRecommendCode() {
        logEvent(EVENT_INPUT_INVITE_CODE);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment, RecommendCodeInputFragment.newInstance()
                                .setOnCompleteListener(this::signup),
                        TAG_RECOMMEND_CODE)
                .addToBackStack(null)
                .commit();
    }

    private void socialLogin() {
        if (currentAuthType == null ||
                AuthType.PHONE.equals(currentAuthType) ||
                AuthType.EMAIL.equals(currentAuthType)) {
            logEvent(EVENT_LOGIN);
            Toast.makeText(this, R.string.message_retry_auth, Toast.LENGTH_SHORT).show();
            startSocialLogin();
            return;
        }

        if (currentRequest == null) {
            showProgress();
            SocialLoginRequest.Builder requestBuilder = new SocialLoginRequest.Builder(
                    PhoneNumberUtil.getPhoneNumber(this), currentAuthType)
                    .setAccessToken(accessToken)
                    .setRefreshToken(refreshToken)
                    .setUid(uid);

            currentRequest = AuthenticationManager.getInstance().socialLogin(requestBuilder.build())
                    .toSingle(() -> false)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onSuccessAuthentication, this::onFailureAuthentication);
        }
    }

    private void signup() {
        if (currentAuthType == null || AuthType.EMAIL.equals(currentAuthType)) {
            Toast.makeText(this, R.string.message_retry_auth, Toast.LENGTH_SHORT).show();
            startSignup();
            return;
        }

        SignupRequest.Builder requestBuilder = new SignupRequest.Builder(
                this, currentAuthType, getBirthYear(), getSex());
        String recommendCode = getRecommendCode();
        if (!TextUtils.isEmpty(recommendCode)) {
            requestBuilder.setRecommendCode(recommendCode);
        }
        switch (currentAuthType) {
            case KAKAO:
                requestBuilder.setRefreshToken(refreshToken)
                        .setAccessToken(accessToken);
                break;
            case NAVER:
                requestBuilder.setRefreshToken(refreshToken)
                        .setAccessToken(accessToken)
                        .setUid(uid);
                break;
        }

        if (currentRequest == null) {
            showProgress();
            currentRequest = AuthenticationManager.getInstance().signup(requestBuilder.build())
                    .subscribeOn(Schedulers.newThread())
                    .toSingle(() -> true)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onSuccessAuthentication, this::onFailureAuthentication);
        }/* else {
            // error?
        }*/
    }

    @Override
    public void auth(AuthType authType) {
        if (authType == AuthType.EMAIL) {
            startEmailCertificationCodeRequest();
            return;
        }

        String phoneNumber = PhoneNumberUtil.getPhoneNumber(this);
        if (TextUtils.isEmpty(phoneNumber)) {
            SimpleAlertDialog
                    .newInstance(getString(R.string.message_deactivated_device))
                    .show(getSupportFragmentManager(), "deactivate_device_dialog");
            return;
        }

        showProgress();

        switch (authType) {
            case KAKAO:
                currentAuthType = AuthType.KAKAO;
                startActivityForResult(new Intent(this, KakaoLinkActivity.class), REQUEST_KAKAO_LOGIN);
                break;
            case NAVER:
                currentAuthType = AuthType.NAVER;
                startActivityForResult(new Intent(this, NaverLinkActivity.class), REQUEST_NAVER_LOGIN);
                break;
        }
    }

    private void onSuccessAuthentication(boolean navigateToSignup) {
        DLog.e(this, "success login");
        currentRequest = null;
        DisplayUtil.hideKeyboard(this);
        try {
            if (navigateToSignup) {
                logEvent(EVENT_SIGNUP);
                Navigator.startSignupCompleteActivity(this);
            } else {
                logEvent(EVENT_LOGIN);
                Navigator.startMainActivity(this, 0);
            }
            finish();
        } catch (Exception e) {
            onFailureAuthentication(e);
        }
    }

    protected void onFailureAuthentication(Throwable error) {
        error.printStackTrace();
        currentRequest = null;
        if (error instanceof RetrofitException) {
            handleError(error);
        } else {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
        }
        hideProgress();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_auth;
    }

    public void showProgress() {
        if (showAnimator != null) {
            if (showAnimator.isRunning() || showAnimator.isStarted()) {
                return;
            } else {
                showAnimator.end();
            }
        }

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        showAnimator = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(shortAnimTime);
        showAnimator.addUpdateListener(valueAnimator -> {
            float alpha = Float.parseFloat(valueAnimator.getAnimatedValue().toString());
            progressView.setAlpha(alpha);
            fragmentContainer.setAlpha(1 - alpha);
            intoButton.setAlpha(1 - alpha);
        });
        showAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                progressView.setVisibility(View.VISIBLE);
                fragmentContainer.setVisibility(View.GONE);
                intoButton.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(View.VISIBLE);
                fragmentContainer.setVisibility(View.GONE);
                intoButton.setVisibility(View.GONE);
            }
        });
        showAnimator.start();
    }

    public void hideProgress() {
        if (hideAnimator != null) {
            if (hideAnimator.isStarted() || hideAnimator.isRunning()) {
                return;
            } else {
                hideAnimator.end();
            }
        }

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        hideAnimator = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(shortAnimTime);
        hideAnimator.addUpdateListener(valueAnimator -> {
            float alpha = Float.parseFloat(valueAnimator.getAnimatedValue().toString());
            progressView.setAlpha(alpha);
            fragmentContainer.setAlpha(1 - alpha);
            intoButton.setAlpha(1 - alpha);
        });
        hideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                progressView.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
                intoButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                progressView.setVisibility(View.GONE);
                fragmentContainer.setVisibility(View.VISIBLE);
                intoButton.setVisibility(View.VISIBLE);
            }
        });
        hideAnimator.start();
    }

    private void showIntoButton() {
        if (intoButton.getVisibility() == View.VISIBLE) {
            return;
        }
        intoButton.setAlpha(0);
        intoButton.animate()
                .alpha(1)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        intoButton.setVisibility(View.VISIBLE);
                    }
                })
                .start();
    }

    private void hideIntoButton() {
        if (intoButton.getVisibility() != View.VISIBLE) {
            return;
        }

        intoButton.setAlpha(1);
        intoButton.animate()
                .alpha(0)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        intoButton.setVisibility(View.GONE);
                    }
                })
                .start();
        intoButton.setVisibility(View.GONE);
    }

    @OnClick(R.id.into)
    public void onClickIntoButton() {
        if (LOGIN_LABEL.equals(intoLabelView.getText())) {
            startSocialLogin();
        } else if (SIGNUP_LABEL.equals(intoLabelView.getText())) {
            startSignup();
        }
    }

    private void logEvent(String... events) {
        ArrayList<String> eventList = new ArrayList<>(Arrays.asList(events));
        eventList.add(0, currentFlow);
        String event = StringUtil.join(eventList, "_");
        ZLog.d(this, "logging: " + event);
        EventLogger.action(this, event);
    }
}
