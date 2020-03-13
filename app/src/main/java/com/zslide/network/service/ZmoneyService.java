package com.zslide.network.service;

import com.zslide.data.model.FamilyZmoney;
import com.zslide.models.response.ZmoneyHistoryResponse;
import com.zslide.models.response.ZmoneyPaymentsResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created by chulwoo on 2017. 3. 8..
 */

public interface ZmoneyService {

    @GET("v4/users/daily_zmoneys/")
    Observable<FamilyZmoney> daily();

    @GET("v4/users/monthly_zmoneys/")
    Observable<FamilyZmoney> monthly();

    @GET("v3/users/reward_history")
    Observable<ZmoneyHistoryResponse> history();

    @GET("v3/users/calculation_history")
    Observable<ZmoneyPaymentsResponse> payments();
}