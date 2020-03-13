package com.zslide.network.service;

import com.zslide.models.ShortDynamicLinkResult;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by chulwoo on 16. 7. 26..
 * <p>
 * 줌마캐스트 API
 */
public interface FirebaseService {

    @FormUrlEncoded
    @POST("v1/shortLinks")
    Observable<ShortDynamicLinkResult> shortLinks(@Field("longDynamicLink") String longDynamicLink);

}
