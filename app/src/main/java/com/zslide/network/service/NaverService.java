package com.zslide.network.service;

import com.zslide.models.NaverUserProfileWrapper;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by chulwoo on 16. 5. 26..
 */
public interface NaverService {

    @GET("v1/nid/me")
    Observable<NaverUserProfileWrapper> getProfile();
}
