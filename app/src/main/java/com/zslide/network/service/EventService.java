package com.zslide.network.service;

import com.zslide.data.model.EventBanner;
import com.zslide.models.Event;
import com.zslide.models.PaginationData;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by jdekim43 on 2016. 1. 25..
 */
public interface EventService {

    @GET("v2/events/event/")
    Observable<List<Event>> getEvents();

    @GET("v2/events/deactivated_event_banners/")
    Observable<PaginationData<EventBanner>> getCompletedEventBanners(@Query("page") int page);

    @GET("v2/events/event_banners/")
    Observable<List<EventBanner>> getEventBanners();
}
