package com.zslide.network.service;

import com.zslide.models.OCB;
import com.zslide.models.OCBPoint;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

/**
 * Created by chulwoo on 16. 9. 28..
 */
public interface OCBService {

    @FormUrlEncoded
    @POST("v2/cash/ocb/auth/")
    Observable<OCB> lookupOCB(@Field("Enc") String encrypt, @Field("paymesTpCd") String type, @Field("ocb_type") String ocbType);

    @FormUrlEncoded
    @POST("v2/cash/ocb/use/")
    Observable<OCB> useOCB(@Field("Enc") String encrypt, @Field("MctTrNo") String mctTrNo,
                           @Field("Amount") int amount, @Field("cancel_enc") String cancelEncrypt);

    @FormUrlEncoded
    @POST("v2/cash/ocb/inquiry/")
    Observable<OCB> inquiryOCB(@Field("MctTrNo") String mctTrNo);

    @GET("v2/cash/ocb/point/")
    Observable<OCBPoint> getUsedOCBPoint();

    @FormUrlEncoded
    @POST("v2/cash/ocb/cancel/")
    Observable<OCB> cancelOCB(@Field("dummy") String dummy);
}
