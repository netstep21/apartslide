package com.zslide.data.remote.api;


import com.zslide.data.remote.response.ApiResponse;
import com.zslide.data.remote.response.AuthenticationResponse;
import com.zslide.data.remote.response.SignUpCheckResponse;

import io.reactivex.Completable;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by chulwoo on 2018. 1. 8..
 */

public interface AuthenticationApi {

    @FormUrlEncoded
    @POST("v4/users/signup/")
    Single<AuthenticationResponse> signup(@Field("signup_type") String type,
                                          @Field("phone_number") String phoneNumber, @Field("imei") String imei,
                                          @Field("birth") String birthYear, @Field("sex") String sex,
                                          @Field("recommend_code") String recommendCode,
                                          @Field("refresh_token") String refreshToken,
                                          @Field("access_token") String accessToken,
                                          @Field("uid") String uid);

    @FormUrlEncoded
    @POST("v4/users/checkusersignup/")
    Single<SignUpCheckResponse> isAuthenticated(@Field("phone_number") String phoneNumber);

    @FormUrlEncoded
    @POST("v4/users/checksocialsignup/")
    Single<ApiResponse> isSocialAuthenticated(@Field("signup_type") String type,
                                              @Field("refresh_token") String refreshToken,
                                              @Field("access_token") String accessToken,
                                              @Field("uid") String uid);

    @FormUrlEncoded
    @POST("v4/users/signin")
    Single<AuthenticationResponse> socialLogin(@Field("phone_number") String phoneNumber,
                                               @Field("signup_type") String type,
                                               @Field("refresh_token") String refreshToken,
                                               @Field("access_token") String accessToken,
                                               @Field("uid") String uid);


    @GET("v4/users/phone_certification")
    Single<ApiResponse> requestPhoneCertificationCode(@Query("phone_number") String phoneNumber);

    @FormUrlEncoded
    @POST("v4/users/phone_certification")
    Single<AuthenticationResponse> checkPhoneCertificationCode(@Field("phone_number") String phoneNumber,
                                                               @Field("auth_number") String certificationCode);


    @POST("v4/users/logout")
    Completable logout();
}
