package com.zslide.utils;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by chulwoo on 16. 5. 16..
 */
public class Ticker {

    private Subscription tick;

    public void start(long delayMillis, long intervalMillis, Runnable runnable) {
        if (tick != null) {
            tick.unsubscribe();
        }
        tick = Observable.interval(delayMillis, intervalMillis, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(timeInterval -> runnable.run(), ZLog::e);
    }

    public void stop() {
        if (tick != null) {
            tick.unsubscribe();
        }
    }
}
