package com.zslide.data.remote.api;

import com.zslide.data.model.Family;
import com.zslide.data.model.HomeZmoney;
import com.zslide.data.model.InviteInfo;
import com.zslide.data.model.User;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public interface UserApi {

    /**
     * @return 내 정보
     */
    @GET("v4/users/me")
    Single<User> getMe();

    /**
     * @param familyId 가져올 가족 id
     * @return 파라미터로 전달한 id의 가족 정보
     */
    @GET("v4/users/families/{family_id}")
    Single<Family> getFamily(@Path("family_id") long familyId);

    /**
     * @return 홈 화면용 줌머니 정보
     */
    @GET("v4/users/zmoneys/{family_id}")
    Single<HomeZmoney> getHomeZmoney(@Path("family_id") long familyId);

    /**
     * @return 초대 관련 정보
     */
    @GET("v4/users/invite_info")
    Single<InviteInfo> getInviteInfo();
}
