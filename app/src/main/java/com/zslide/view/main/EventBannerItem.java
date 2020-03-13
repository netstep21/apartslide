package com.zslide.view.main;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zslide.data.model.EventBanner;

import io.reactivex.functions.Action;

/**
 * Created by chulwoo on 2017. 12. 29..
 */

public class EventBannerItem {

    private final EventBanner eventBanner;
    private final Action clickAction;

    public EventBannerItem(@NonNull EventBanner eventBanner, @Nullable Action clickAction) {
        this.eventBanner = eventBanner;
        this.clickAction = clickAction;
    }

    @NonNull
    public EventBanner getEventBanner() {
        return eventBanner;
    }

    @Nullable
    public Action getClickAction() {
        return clickAction;
    }
}
