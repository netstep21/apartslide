package com.zslide.network;

import com.zslide.data.model.EventBanner;
import com.zslide.models.Event;
import com.zslide.models.PaginationData;
import com.zslide.network.service.EventService;

import java.util.List;

import retrofit2.Retrofit;
import rx.Observable;

/**
 * Created by chulwoo on 16. 9. 28..
 */
public class EventApi {

    private EventService eventService;

    protected EventApi(Retrofit retrofit) {
        eventService = retrofit.create(EventService.class);
    }

    public Observable<List<Event>> popups() {
        return eventService.getEvents();
    }

    public Observable<PaginationData<EventBanner>> completedItems(int page) {
        return eventService.getCompletedEventBanners(page);
    }

    public Observable<List<EventBanner>> items() {
        return eventService.getEventBanners();
    }
}
