package com.zslide.network;

import com.zslide.data.model.FamilyZmoney;
import com.zslide.models.ZmoneyHistory;
import com.zslide.models.ZmoneyPayments;
import com.zslide.models.response.ZmoneyHistoryResponse;
import com.zslide.models.response.ZmoneyPaymentsResponse;
import com.zslide.network.service.ZmoneyService;

import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by chulwoo on 16. 9. 28..
 */
public class ZmoneyApi {

    private ZmoneyService zmoneyService;

    protected ZmoneyApi(Retrofit retrofit) {
        zmoneyService = retrofit.create(ZmoneyService.class);
    }

    public Observable<FamilyZmoney> daily() {
        return zmoneyService.daily();
    }

    public Observable<FamilyZmoney> monthly() {
        return zmoneyService.monthly();
    }

    public Observable<ZmoneyPayments> payments() {
        return zmoneyService.payments().map(ZmoneyPaymentsResponse::getItem);
    }

    public Observable<List<ZmoneyHistory>> history() {
        return zmoneyService.history().map(ZmoneyHistoryResponse::getItem);
    }

}
