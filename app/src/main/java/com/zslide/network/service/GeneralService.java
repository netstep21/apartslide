package com.zslide.network.service;

import com.zslide.data.model.AlertMessage;
import com.zslide.data.remote.response.AuthenticationResponse;
import com.zslide.models.AppValue;
import com.zslide.models.Notification;
import com.zslide.models.PaginationData;
import com.zslide.models.StaticImage;
import com.zslide.models.response.CertificationResponse;
import com.zslide.models.response.SimpleApiResponse;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by chulwoo on 15. 12. 24..
 * <p>
 * 특정 리소스가 없는 일반 API
 */
public interface GeneralService {

    /**
     * 업데이트 알림 정보를 가져온다.
     * 어플리케이션의 버전에 따라 서버에서 메세지를 제어한다.
     *
     * @param versionCode 현재 어플리케이션의 버전 코드
     * @return 업데이트 알림 정보
     */
    @GET("v2/version/{version_code}/")
    Observable<AlertMessage> getAlertMessage(@Path("version_code") int versionCode);

    /**
     * GCM 서비스를 위해 디바이스를 등록한다.
     *
     * @param deviceId 등록할 디바이스 id
     * @param token    등록할 디바이스 instance token
     * @return 가입 완료시 토큰
     */
    @FormUrlEncoded
    @POST("v2/push_notifications/gcmdevice/")
    Observable<SimpleApiResponse> createGcmDevice(@Field("device_id") String deviceId,
                                                  @Field("registration_id") String token);

    /**
     * '첫 화면 슬라이드 보기' 설정 상태를 서버에 저장한다.
     *
     * @param enabled 잠금화면 On/Off 상태를 나타내는 문자열로 "on" 혹은 "off" 값이 가능
     * @return 요청 성공 여부
     */
    @FormUrlEncoded
    @POST("v2/users/check_onoff/")
    Observable<SimpleApiResponse> setLockEnabled(@Field("status") String enabled);

    /**
     * 신한은행 자동이체 신청 요청을 전송한다.
     *
     * @param recruiterCode 모집인 코드
     * @return 요청 성공 여부
     */
    @GET("v2/cards/participation/")
    Observable<SimpleApiResponse> participateATEvent(@Query("code") String recruiterCode,
                                                     @Query("company") String company);

    @GET("v2/users/notification/")
    Observable<PaginationData<Notification>> getNotifications(@Query("page") int page);

    @FormUrlEncoded
    @PUT("v2/users/notification/{id}/")
    Observable<Notification> readNotification(@Path("id") long id, @Field("empty") String emptyValue);

    @GET("v3/users/phone_certification")
    Observable<SimpleApiResponse> requestPhoneCertificationCode(@Query("phone_number") String phoneNumber);

    @FormUrlEncoded
    @POST("v3/users/phone_certification")
    Observable<CertificationResponse> phoneCertification(@Field("phone_number") String phoneNumber,
                                                         @Field("auth_number") String certificationCode);

    @GET("v3/users/mail_certification")
    Observable<SimpleApiResponse> requestEmailCertificationCode(@Query("mail") String phoneNumber);

    @FormUrlEncoded
    @POST("v3/users/mail_certification")
    Observable<SimpleApiResponse> emailCertification(@Field("mail") String phoneNumber,
                                                     @Field("auth_number") String certificationCode);

    @GET("v3/users/mail_certification_login")
    Observable<SimpleApiResponse> requestEmailLoginCertificationCode(@Query("mail") String phoneNumber);

    @FormUrlEncoded
    @POST("v3/users/mail_certification_login")
    Observable<AuthenticationResponse> emailLoginCertification(@Field("mail") String phoneNumber,
                                                               @Field("auth_number") String certificationCode);

    @GET("/app/static/")
    Observable<StaticImage> staticImage(@Query("goal") String target);

    @GET("/app/value/")
    Observable<AppValue> value(@Query("goal") String target);
}
