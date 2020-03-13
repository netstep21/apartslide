package com.zslide.network;

import com.zslide.managers.ItemCache;
import com.zslide.models.CartItem;
import com.zslide.models.MarketItem;
import com.zslide.models.PaginationData;
import com.zslide.models.PaymentInfo;
import com.zslide.models.ShoppingReview;
import com.zslide.models.ZummaStore;
import com.zslide.models.response.SimpleApiResponse;
import com.zslide.network.service.ZummaShoppingService;

import java.util.ArrayList;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by chulwoo on 16. 9. 28..
 */
public class ZummaShoppingApi {

    private ZummaShoppingService service;

    protected ZummaShoppingApi(Retrofit retrofit) {
        service = retrofit.create(ZummaShoppingService.class);
    }

    public Observable<MarketItem> item(long id) {
        return service.getItem(id);
    }

    public Observable<PaginationData<MarketItem>> items(int page) {
        return service.getItems(page);
    }

    public Observable<PaginationData<PaymentInfo>> paymentInfos(int page) {
        return service.getPayments(page);
    }

    public Observable<PaymentInfo> paymentInfo(int id) {
        return service.getPayment(id);
    }

    public Observable<ArrayList<CartItem>> cartItems() {
        return service.getCartItems();
    }

    public Observable<ArrayList<CartItem>> createCartItem(long adId, int count) {
        return service.createCartItem(adId, count);
    }

    public Observable<SimpleApiResponse> deleteCartItem(int id) {
        return service.deleteCartItem(id);
    }

    public Observable<ArrayList<CartItem>> editCartItem(int adId, int count) {
        return service.editCartItem(adId, count);
    }

    public Observable<PaginationData<ShoppingReview>> reviews(long id, int page) {
        return service.getReviews(id, page).map(result -> {
            ItemCache cache = ItemCache.getInstance();
            ZummaStore store = cache.getZummaStore(id);
            if (store != null) {
                store.setReviewCount(result.getCount());
            }

            return result;
        });
    }

    public Observable<PaginationData<ShoppingReview>> myReviews(int page) {
        return service.getMyReviews(page);
    }

    public Observable<ShoppingReview> review(long id) {
        return service.getReview(id);
    }

    public Observable<ShoppingReview> writeReview(long storeId, String content, int rating) {
        return service.writeReview(storeId, content, rating).map(r -> {
            ItemCache cache = ItemCache.getInstance();
            ZummaStore store = cache.getZummaStore(storeId);
            if (store != null) {
                store.increaseReviewCount();
            }
            return r;
        });
    }

    public Observable<ShoppingReview> editReview(long id, String content, int rating) {
        return service.editReview(id, content, rating);
    }

    public Observable<SimpleApiResponse> deleteReview(long id) {
        return service.deleteReview(id);
    }
}
