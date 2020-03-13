package com.zslide.data.remote;

import com.zslide.Config;
import com.zslide.data.model.AlertMessage;
import com.zslide.data.model.EventBanner;
import com.zslide.data.remote.api.EventApi;
import com.zslide.data.remote.api.GeneralApi;
import com.zslide.data.remote.base.AbstractRemoteSource;

import java.util.List;

import io.reactivex.Maybe;

/**
 * Created by chulwoo on 2017. 12. 29..
 */

public class RemoteMetaDataSource extends AbstractRemoteSource {

    private final GeneralApi generalApi;
    private final EventApi eventApi;

    public RemoteMetaDataSource() {
        generalApi = create(GeneralApi.class);
        eventApi = create(EventApi.class);
    }

    public Maybe<AlertMessage> getAlertMessage() {
        return generalApi.getAlertMessage(Config.VERSION_CODE);
    }

    public Maybe<List<EventBanner>> getEventBanners() {
        return eventApi.getEventBanners();
    }
}