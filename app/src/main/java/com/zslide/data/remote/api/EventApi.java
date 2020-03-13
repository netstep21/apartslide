package com.zslide.data.remote.api;

import com.zslide.data.model.EventBanner;
import com.zslide.models.Event;
import com.zslide.models.PaginationData;

import java.util.List;

import io.reactivex.Maybe;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by chulwoo on 2017. 12. 29..
 */

public interface EventApi {

    @GET("v4/events/popups")
    Maybe<List<Event>> getEvents();

    @GET("v4/events/banners?state={event_state}")
    Maybe<PaginationData<EventBanner>> getCompletedEventBanners(@Query("page") int page);

    @GET("v4/events/banners")
    Maybe<List<EventBanner>> getEventBanners();
}
