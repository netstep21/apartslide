package com.zslide.data.remote;

import com.zslide.data.remote.api.AuthenticationApi;
import com.zslide.data.remote.base.AbstractRemoteSource;
import com.zslide.data.remote.request.SignupRequest;
import com.zslide.data.remote.request.SocialLoginRequest;
import com.zslide.data.remote.response.ApiResponse;
import com.zslide.data.remote.response.AuthenticationResponse;
import com.zslide.data.remote.response.SignUpCheckResponse;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by chulwoo on 2018. 1. 10..
 */

public class RemoteAuthenticationSource extends AbstractRemoteSource {

    private AuthenticationApi authenticationApi;

    public RemoteAuthenticationSource() {
        authenticationApi = create(AuthenticationApi.class);
    }

    public Single<AuthenticationResponse> signup(SignupRequest requset) {
        return authenticationApi.signup(
                requset.getAuthType(), requset.getPhoneNumber(), requset.getImei(),
                requset.getBirthYear(), requset.getSex(), requset.getRecommendCode(),
                requset.getRefreshToken(), requset.getAccessToken(), requset.getUid());
    }

    public Single<AuthenticationResponse> socialLogin(SocialLoginRequest request) {
        return authenticationApi.socialLogin(request.getPhoneNumber(), request.getAuthType(),
                request.getRefreshToken(), request.getAccessToken(), request.getUid());
    }

    public Completable logout() {
        return authenticationApi.logout();
    }

    public Single<SignUpCheckResponse> isAuthenticated(String phoneNumber) {
        return authenticationApi.isAuthenticated(phoneNumber);
    }

    public Single<ApiResponse> isSocialAuthenticated(SocialLoginRequest request) {
        return authenticationApi.isSocialAuthenticated(
                request.getAuthType(), request.getRefreshToken(), request.getAccessToken(), request.getUid());
    }

    public Single<ApiResponse> requestPhoneCertificationCode(String phoneNumber) {
        return authenticationApi.requestPhoneCertificationCode(phoneNumber);
    }

    public Single<AuthenticationResponse> checkPhoneCertificationCode(String phoneNumber, String certificationCode) {
        return authenticationApi.checkPhoneCertificationCode(phoneNumber, certificationCode);
    }
}
