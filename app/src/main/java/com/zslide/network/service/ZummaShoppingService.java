package com.zslide.network.service;

import com.zslide.models.CartItem;
import com.zslide.models.MarketItem;
import com.zslide.models.PaginationData;
import com.zslide.models.PaymentInfo;
import com.zslide.models.ShoppingReview;
import com.zslide.models.response.SimpleApiResponse;

import java.util.ArrayList;

import retrofit2.http.DELETE;
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
 * 마켓 관련 API
 */
public interface ZummaShoppingService {

    /**
     * 줌마마켓 상품 정보를 페이지네이션 가능한 형태로 가져온다.
     *
     * @param page 가져올 페이지
     * @return {@param page}에 있는 줌마마켓 상품 목록
     */
    @GET("v2/market/marketitem/")
    Observable<PaginationData<MarketItem>> getItems(@Query("page") int page);

    /**
     * 줌마마켓 상품 정보를 가져온다.
     *
     * @param id 가져올 상품의 id
     * @return 줌마마켓 상품 정보
     */
    @GET("v2/market/marketitem/{id}")
    Observable<MarketItem> getItem(@Path("id") long id);


    @GET("v2/zummastore/review/{id}/")
    Observable<PaginationData<ShoppingReview>> getReviews(@Path("id") long adId,
                                                          @Query("page") int page);

    @GET("v3/zummastores/review/?type=market")
    Observable<PaginationData<ShoppingReview>> getMyReviews(@Query("page") int page);

    @GET("v3/zummastore/review/{id}/")
    Observable<ShoppingReview> getReview(@Path("id") long id);

    @FormUrlEncoded
    @POST("v2/zummastore/review/")
    Observable<ShoppingReview> writeReview(@Field("ad_id") long adId,
                                           @Field("review") String content,
                                           @Field("rating") int rating);

    @FormUrlEncoded
    @PUT("v2/zummastore/review/{id}/")
    Observable<ShoppingReview> editReview(@Path("id") long id,
                                          @Field("review") String content,
                                          @Field("rating") int rating);

    @DELETE("v2/zummastore/review/{id}/")
    Observable<SimpleApiResponse> deleteReview(@Path("id") long id);


    /**
     * 구매내역 정보를 페이지네이션 가능한 형태로 가져온다.
     *
     * @param page 가져올 페이지
     * @return {@param page}에 있는 구매내역 목록
     */
    @GET("v2/market/payment")
    Observable<PaginationData<PaymentInfo>> getPayments(@Query("page") int page);

    /**
     * 구매내역 정보를 가져온다.
     *
     * @param id 가져올 구매내역의 id
     * @return 구매내역 정보
     */
    @GET("v2/market/payment/{id}")
    Observable<PaymentInfo> getPayment(@Path("id") int id);

    @GET("v2/market/cart/")
    Observable<ArrayList<CartItem>> getCartItems();

    @FormUrlEncoded
    @POST("v2/market/cart/")
    Observable<ArrayList<CartItem>> createCartItem(@Field("ad_id") long adId, @Field("count") int count);

    @DELETE("v2/market/cart/{id}/")
    Observable<SimpleApiResponse> deleteCartItem(@Path("id") int id);

    @FormUrlEncoded
    @PUT("v2/market/cart/{id}/")
    Observable<ArrayList<CartItem>> editCartItem(@Path("id") int adId, @Field("count") int count);
}
