package com.zslide.network.service;

import com.zslide.data.model.Family;
import com.zslide.data.model.Payments;
import com.zslide.data.model.User;
import com.zslide.models.FamilyAddress;
import com.zslide.models.LevelCouponLog;
import com.zslide.models.PaginationData;
import com.zslide.models.response.AccountResponse;
import com.zslide.models.response.BanksResult;
import com.zslide.models.response.RecommendCountResponse;
import com.zslide.models.response.SimpleApiResponse;

import okhttp3.MultipartBody;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by chulwoo on 15. 12. 24..
 * <p>
 * 사용자 정보 관련 API
 */
public interface UserService {

    @FormUrlEncoded
    @POST("v3/users/kakao_regist/")
    Observable<SimpleApiResponse> kakaoLink(@Field("refresh_token") String refreshToken,
                                            @Field("access_token") String accessToken);

    /**
     * 사용자 정보를 생성한다.
     */
    @FormUrlEncoded
    @POST("v3/users/naver_regist/")
    Observable<SimpleApiResponse> naverLink(@Field("refresh_token") String refreshToken,
                                            @Field("access_token") String accessToken, @Field("uid") String uid);

    /**
     * 앱 내에 저장된 API  를  해 사용자 정보를 얻어온다.
     *
     * @return 사용자 정보
     */
    @GET("v2/users/social_user/")
    Observable<User> getUser();

    /**
     * 로그인을 요 한다.
     *
     * @param phoneNumber 로그인에 사용할 핸드폰 번호, 디바이스에 저장된 핸드폰 번호를 이용한다.
     * @param password    로그인에 사용할 비밀번호
     * @param deviceId    디바이스 id
     * @param token       GCM
     * @return 요  성공 여부, 로그인 정보
     */
    @FormUrlEncoded
    @POST("v2/users/auth/")
    Observable<User> login(@Field("phone_number") String phoneNumber,
                           @Field("password") String password,
                           @Field("device_id") String deviceId,
                           @Field("registration_id") String token);

    /**
     * 소셜 로그인을 요 한다.
     *
     * @param phoneNumber  로그인에 사용할 핸드폰 번호, 디바이스에 저장된 핸드폰 번호를 이용한다.
     * @param accessToken  Oauth Access
     * @param refreshToken Oauth Refresh
     * @param gcmToken     GCM
     * @param deviceId     디바이스 id
     * @return 요  성공 여부, 로그인 정보
     */
    @FormUrlEncoded
    @POST("v2/users/auth/")
    Observable<User> socialLogin(@Field("phone_number") String phoneNumber,
                                 @Field("uid") String uid,
                                 @Field("access_token") String accessToken,
                                 @Field("refresh_token") String refreshToken,
                                 @Field("registration_id") String gcmToken,
                                 @Field("device_id") String deviceId,
                                 @Field("type") String type);

    /**
     * 아   협약 요 을 한다.
     *
     * @param apartId 협약 요 할 아  의 id
     * @return 요  성공 여부
     */
    @FormUrlEncoded
    @POST("v2/users/recommend_apart/")
    Observable<SimpleApiResponse> requestApartAgree(@Field("apart_id") int apartId);

    /**
     * 계좌 정보를 생성한다.
     *
     * @param bankId        생성할 계좌의 은행 id
     * @param accountNumber 생성할 계좌의 계좌번호
     * @param owner         생성할 계좌의 예금주
     * @return 요  성공 여부, 계좌 정보
     */
    @FormUrlEncoded
    @POST("v2/users/account/")
    Observable<AccountResponse> createAccount(@Field("bank_id") int bankId,
                                              @Field("account") String accountNumber,
                                              @Field("owner") String owner);

    /**
     * 은행 목록을 가져온다.
     *
     * @return 요  성공 여부, 은행 목록
     */
    @GET("v1/users/bank_list/")
    Observable<BanksResult> getBanks();

    /**
     * 인 수를 가져온다.
     *
     * @return 요  성공 여부,   인 수
     */
    @GET("v2/users/recommend_count/")
    Observable<RecommendCountResponse> getRecommendCount();

    /**
     * 가족 이름을 통해 가족 정보를 가져온다. 전방일치
     *
     * @param name 검색 할 가족 이름
     * @return 검색된 가족 정보
     */
    @GET("v2/users/family_search/")
    Observable<Family> searchFamily(@Query("name") String name);

    @FormUrlEncoded
    @POST("v2/users/family/")
    Observable<Family> createFamily(@Field("name") String name, @Field("dong_id") long dongId,
                                    @Field("apart_id") long apartmentId, @Field("dong") String dong,
                                    @Field("ho") int ho, @Field("detail_address") String detailAddress,
                                    @Field("temp_apart_id") long tempApartmentId, @Field("living_type") String type);

    /**
     * 가족 이름을 통해 가족 정보를 가져온다. 전방일치
     *
     * @param id 이동할 가족 id
     * @return 이동한 가족 정보
     */
    @FormUrlEncoded
    @PUT("v2/users/family/{id}/")
    Observable<Family> setFamily(@Path("id") long id, @Field("id") long id2);

    /**
     * 가족 이름을 통해 가족 정보를 가져온다. 전방일치
     *
     * @param id 이동할 가족 id
     * @return 이동한 가족 정보
     */
    @FormUrlEncoded
    @PUT("v2/users/move/{id}/")
    Observable<Family> joinFamily(@Path("id") long id,
                                  @Field("family_id") long familyId,
                                  @Field("join_type") String joinType);

    /**
     * 가족 이름을 통해 가족 정보를 가져온다. 전방일치
     *
     * @param id 이동할 가족 id
     * @return 이동한 가족 정보
     */
    @FormUrlEncoded
    @PUT("v2/users/move/{id}/")
    Observable<Family> updateFamily(@Path("id") long id, @Field("name") String name,
                                    @Field("dong_id") long dongId, @Field("apart_id") long apartmentId,
                                    @Field("dong") String dong, @Field("ho") int ho,
                                    @Field("detail_address") String detailAddress,
                                    @Field("temp_apart_id") long tempApartId,
                                    @Field("living_type") String livingType,
                                    @Field("join_type") String joinType);

    /**
     * 주소지를 변경한다.
     *
     * @param ids         주소지를 변경 할 가족 id
     * @param apartmentId 변경할 아   id
     * @param dong        변경할 아   동
     * @param ho          변경할 아   호
     * @return 가족 정보
     */
    @FormUrlEncoded
    @PUT("v2/users/family_move/{ids}/")
    Observable<FamilyAddress> moveHouse(@Path("ids") String ids,
                                        @Field("apart_id") int apartmentId,
                                        @Field("dong") String dong, @Field("ho") int ho);

    /**
     * 개인 정보를 변경한다.
     *
     * @param id    정보를 변경 할 사용자 id
     * @param name  변경할 이름
     * @param birth 변경할 생일
     * @param sex   변경할 성별
     * @return 변경된 정보만 담긴 사용자 정보
     */
    @FormUrlEncoded
    @PUT("v2/users/profile/{id}/")
    Observable<User> editProfile(@Path("id") long id,
                                 @Field("user_name") String name,
                                 @Field("birthday") String birth,
                                 @Field("sex") String sex);

    @FormUrlEncoded
    @PUT("v2/users/user_nickname/{id}/")
    Observable<User> editNickname(@Path("id") long id, @Field("nickname") String nickname);

    @FormUrlEncoded
    @POST("v2/users/password_change/")
    Observable<SimpleApiResponse> findPassword(@Field("phone_number") String phoneNumber,
                                               @Field("new_password") String newPassword);

    @FormUrlEncoded
    @POST("v2/users/user_password_change/")
    Observable<SimpleApiResponse> editPassword(@Field("password") String password,
                                               @Field("new_password") String newPassword);

    @Multipart
    @PUT("v2/users/profile_image/{id}/")
    Observable<User> editProfileImage(@Path("id") long id, @Part MultipartBody.Part image);

    @FormUrlEncoded
    @PUT("v2/users/profile_image/{id}/")
    Observable<User> deleteProfileImage(@Path("id") long id, @Field("none_value") String none);

    @FormUrlEncoded
    @PUT("v2/users/family_name/{id}/")
    Observable<Family> editFamilyName(@Path("id") long id, @Field("name") String name);

    @FormUrlEncoded
    @PUT("v2/users/family_leader/{id}/")
    Observable<User> changeFamilyLeader(@Path("id") long userId, @Field("none_value") String none);

    @Multipart
    @PUT("v2/users/family_profile_image/{id}/")
    Observable<Family> editFamilyImage(@Path("id") long id, @Part MultipartBody.Part image);

    @FormUrlEncoded
    @PUT("v2/users/family_profile_image/{id}/")
    Observable<Family> deleteFamilyImage(@Path("id") long id, @Field("none_value") String none);

    @FormUrlEncoded
    @PUT("v2/users/banish/{id}/")
    Observable<Family> blockMembers(@Path("id") long id,
                                    @Field(value = "user_list", encoded = false) String userIds,
                                    @Field("banish_reason") String reason);

    @FormUrlEncoded
    @PUT("v2/users/accept/{id}/")
    Observable<Family> unblock(@Path("id") long id, @Field("none_value") String none);

    @DELETE("v2/users/social_user/{id}/")
    Observable<SimpleApiResponse> leave(@Path("id") long id, @Query("zmoney") boolean zmoney,
                                        @Query("zummastore") boolean zstore, @Query("zummamarket") boolean zmall,
                                        @Query("extra") String extra);

    @GET("v2/level/log/")
    Observable<PaginationData<LevelCouponLog>> getCouponLogs(@Query("page") int page);

    @GET("v3/calculations/calculation")
    Observable<PaginationData<Payments>> getCalculations(@Query("page") int page);
}
