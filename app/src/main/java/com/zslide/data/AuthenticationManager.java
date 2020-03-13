package com.zslide.data;

import android.content.Context;
import android.text.TextUtils;

import com.zslide.data.local.LocalAuthenticationSource;
import com.zslide.data.model.User;
import com.zslide.data.remote.RemoteAuthenticationSource;
import com.zslide.data.remote.exception.LoginFailureException;
import com.zslide.data.remote.exception.UnsignedUserException;
import com.zslide.data.remote.request.SignupRequest;
import com.zslide.data.remote.request.SocialLoginRequest;
import com.zslide.data.remote.response.ApiResponse;
import com.zslide.data.remote.response.AuthenticationResponse;
import com.zslide.data.remote.response.SignUpCheckResponse;
import com.zslide.utils.DLog;
import com.zslide.utils.PhoneNumberUtil;
import com.zslide.utils.ZLog;
import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.nhn.android.naverlogin.OAuthLogin;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 8..
 * <p>
 * // TODO: 2018. 1. 8.  singleton??
 */

public class AuthenticationManager {

    private boolean initialized = false;

    private LocalAuthenticationSource localSource;
    private RemoteAuthenticationSource remoteSource;

    @Getter private String apiKey;

    public static AuthenticationManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private AuthenticationManager() {
    }

    public void init(Context context) {
        localSource = new LocalAuthenticationSource(context);
        remoteSource = new RemoteAuthenticationSource();

        apiKey = localSource.getApiKey();

        initialized = true;
    }

    public Completable updateApiKey(String apiKey) {
        checkInitialize();
        return Single.just(apiKey).flatMapCompletable(key -> {
            this.apiKey = key;
            this.localSource.setApiKey(key);
            return Completable.complete();
        });
    }

    public Completable signup(SignupRequest request) {
        checkInitialize();
        return remoteSource.signup(request)
                .flatMapCompletable(this::authenticationResponseToCompletable);
    }

    public Completable socialLogin(SocialLoginRequest request) {
        checkInitialize();
        return remoteSource.socialLogin(request)
                .flatMapCompletable(this::authenticationResponseToCompletable);
    }

    private Completable authenticationResponseToCompletable(AuthenticationResponse authenticationResponse) {
        checkInitialize();
        return Single.just(authenticationResponse)
                .flatMapCompletable(response -> {
                    if (response.isSuccess()) {
                        if (response.getUser() != null && response.getUser().isNotNull()) {
                            return AuthenticationManager.getInstance().updateApiKey(response.getApiKey())
                                    .andThen(UserManager.getInstance().fetchUser())
                                    .andThen(UserManager.getInstance().fetchFamily());
                        } else {
                            return Completable.error(new UnsignedUserException(response.getMessage()));
                        }
                    } else {
                        return Completable.error(new LoginFailureException(response.getMessage()));
                    }
                });
    }


    public Completable logout() {
        checkInitialize();
        return isLoggedIn() ? remoteLogout() : localLogout();
    }

    public Completable remoteLogout() {
        checkInitialize();
        BuzzScreen.getInstance().logout();
        return remoteSource.logout()
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> {
                    apiKey = "";
                    localSource.clear();
                })
                .andThen(UserManager.getInstance().clear())
                .andThen(MetaDataManager.getInstance().clear())
                .onErrorComplete();
    }

    public Completable localLogout() {
        checkInitialize();
        return UserManager.getInstance().clear()
                .doOnSubscribe(disposable -> {
                    BuzzScreen.getInstance().logout();
                    apiKey = "";
                    localSource.clear();
                })
                .andThen(MetaDataManager.getInstance().clear())
                .onErrorComplete();
    }

    public Single<SignUpCheckResponse> isAuthenticated(Context context) {
        checkInitialize();
        return remoteSource.isAuthenticated(PhoneNumberUtil.getPhoneNumber(context))
                .subscribeOn(Schedulers.io());
    }

    public Single<ApiResponse> isSocialAuthenticated(SocialLoginRequest request) {
        checkInitialize();
        return remoteSource.isSocialAuthenticated(request)
                .subscribeOn(Schedulers.io());
    }

    public Single<ApiResponse> requestPhoneCertificationCode(Context context) {
        checkInitialize();
        return remoteSource.requestPhoneCertificationCode(PhoneNumberUtil.getPhoneNumber(context))
                .subscribeOn(Schedulers.io());
    }

    public Completable checkPhoneCertificationCode(Context context, String certificationCode) {
        checkInitialize();
        return remoteSource.checkPhoneCertificationCode(PhoneNumberUtil.getPhoneNumber(context), certificationCode)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::authenticationResponseToCompletable);
    }

    public boolean isLoggedIn() {
        checkInitialize();
        if (TextUtils.isEmpty(apiKey)) {
            apiKey = localSource.getApiKey();
        }

        return !TextUtils.isEmpty(apiKey);
    }

    public void leave(Context context) {
        // TODO: API 연결 후 완료 작업 떄 이 작업 해야 함
        onSuccessLeave(context);
    }

    private void onSuccessLeave(Context context) {
        User user = UserManager.getInstance().getUserValue();
        if (user.getSocialAccount() != null) {
            switch (user.getSocialAccount().getAuthType()) {
                case KAKAO:
                    unlinkKakao();
                    break;
                case NAVER:
                    logoutNaver(context);
                    break;
            }
        }

        UserManager.getInstance().clear()
                .andThen(MetaDataManager.getInstance().clear())
                .onErrorComplete()
                .subscribe();
    }

    private void unlinkKakao() {
        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                ZLog.e(this, "session closed");
            }

            @Override
            public void onNotSignedUp() {
                ZLog.e(this, "not signed up");
            }

            @Override
            public void onSuccess(Long result) {
                ZLog.e(this, "success");
            }
        });
    }

    private void logoutNaver(Context context) {
        Single.defer(() -> Single
                .just(OAuthLogin.getInstance().logoutAndDeleteToken(context)))
                .subscribeOn(Schedulers.io())
                .subscribe(isSuccess -> DLog.e(this, "isSuccess:" + isSuccess), DLog::e);
    }

    private static class InstanceHolder {

        private static final AuthenticationManager INSTANCE = new AuthenticationManager();
    }

    private void checkInitialize() {
        if (!initialized) {
            throw new IllegalStateException("사용을 시작하기 전에 init 메소드를 호출해야 합니다.");
        }
    }
}
