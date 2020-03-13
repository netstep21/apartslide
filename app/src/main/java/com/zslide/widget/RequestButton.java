package com.zslide.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 6. 23..
 * <p>
 * Updated by chulwoo on 2016. 11. 15..
 * precondition 추가
 */
public class RequestButton extends ProgressButton {

    protected Observable observable;
    protected Subscription subscription;

    private boolean canForceStop = true;

    private Observable<Boolean> precondition;
    private Action1 onNext;
    private Action1<Throwable> onError;
    private Action0 onComplete;

    public RequestButton(Context context) {
        this(context, null);
    }

    public RequestButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RequestButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        precondition = Observable.create(subscriber -> subscriber.onNext(true));
    }

    public RequestButton precondition(@NonNull Observable<Boolean> precondition) {
        this.precondition = precondition;
        return this;
    }

    public <T> void action(Observable<T> obsrvable) {
        action(observable, null);
    }

    public <T> void action(Observable<T> observable, @Nullable Action1<T> onNext) {
        action(observable, onNext, null);
    }

    public <T> void action(Observable<T> observable, @Nullable Action1<T> onNext,
                           @Nullable Action1<Throwable> onError) {
        action(observable, onNext, onError, null);
    }

    public <T> void action(Observable<T> observable, @Nullable Action1<T> onNext,
                           @Nullable Action1<Throwable> onError, @Nullable Action0 onComplete) {
        this.observable = observable;
        this.onNext = onNext;
        this.onError = onError;
        this.onComplete = onComplete;
    }

    public void setEnableForceStop(boolean enable) {
        this.canForceStop = enable;
    }

    public boolean canForceStop() {
        return canForceStop;
    }

    @SuppressWarnings("unchecked")
    public void request() {
        setProgressing(observable != null);
        if (observable != null) {
            if (onNext == null) {
                onNext = RxUtil::doNothing;
            }
            if (onError == null) {
                onError = ZummaApiErrorHandler::handleError;
            }
            if (onComplete == null) {
                onComplete = RxUtil::doNothing;
            }

            subscription = observable
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doAfterTerminate(() -> setProgressing(false))
                    .subscribe(onNext, onError, onComplete);

        }
    }

    public void stop() {
        if (subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        setProgressing(false);
    }

    @Override
    protected void onClick(View v) {
        precondition.subscribe(success -> {
            if (success) {
                if (isProgressing()) {
                    if (canForceStop) {
                        stop();
                    }
                } else {
                    if (clickListener != null) {
                        clickListener.onClick(v);
                    }

                    if (canAutoProgressing()) {
                        request();
                    }
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }
}