package com.zslide.network.service;

import com.zslide.data.model.Address;
import com.zslide.models.Apartment;
import com.zslide.models.Dong;
import com.zslide.models.Sido;
import com.zslide.models.Sigungu;
import com.zslide.models.TempApartment;
import com.zslide.models.response.SimpleApiResponse;

import java.util.List;

import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by chulwoo on 15. 12. 24..
 * <p>
 * 주소 관련 API
 */
public interface AddressService {

    /**
     * 전달받은 동 이름이 포함된 주소지 목록을 가져온다.
     *
     * @param dongName 검색할 동 이름
     * @return {@param dongName}이 포함된 주소지 목록
     */
    @GET("v2/users/address/")
    Observable<List<Address>> getAddresses(@Query("name") String dongName);

    /**
     * 전달받은 동에 있는 아파트 목록을 가져온다.
     *
     * @param dongId 검색할 동 ID
     * @return {@param dongName}이 포함된 주소지 목록
     */
    @GET("v2/users/apart/")
    Observable<List<Apartment>> getApartment(@Query("id") long dongId, @Query("name") String apartName);

    /**
     * 등록된 배송지를 제거한다.
     *
     * @param id 제거할 배송지의 id
     * @return 요청 성공 여부
     */
    @DELETE("v2/users/shipping_address/{id}/")
    Observable<SimpleApiResponse> deleteShippingAddress(@Path("id") long id);

    @GET("v2/users/temp_apartment/")
    Observable<List<TempApartment>> getTempApartments(@Query("dong_id") long dongId, @Query("name") String name);

    @FormUrlEncoded
    @POST("v2/users/temp_apartment/")
    Observable<TempApartment> createTempApartment(@Field("dong_id") long dongId, @Field("name") String name);

    @GET("v3/zummastores/sido")
    Observable<List<Sido>> sidos();

    @GET("v3/zummastores/sido/{sido_id}")
    Observable<List<Sigungu>> sigungus(@Path("sido_id") int sidoId);

    @GET("v3/zummastores/sigungu/{sigungu_id}")
    Observable<List<Dong>> dongs(@Path("sigungu_id") int sigunguId);

    @GET("v2/users/dong_search/")
    Observable<Dong> dong(@Query("latitude") double lat, @Query("longitude") double lng);
}
