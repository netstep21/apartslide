package com.zslide.data.remote.api;

import com.zslide.data.model.AlertMessage;

import io.reactivex.Maybe;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by chulwoo on 2018. 1. 2..
 */

public interface GeneralApi {


    @GET("v2/version/{version_code}/")
    Maybe<AlertMessage> getAlertMessage(@Path("version_code") int versionCode);
}
