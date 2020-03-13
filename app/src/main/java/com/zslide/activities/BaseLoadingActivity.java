package com.zslide.activities;

import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.RxUtil;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseLoadingActivity<Data> extends BaseActivity {

    private Subscription subscription;
    private boolean isFirst = true;

    protected abstract Observable<Data> getData();

    protected abstract void onSuccessLoading(Data data);

    @Override
    protected void onStart() {
        super.onStart();
        if (isFirst) {
            isFirst = false;
            refresh();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscription.unsubscribe();
    }

    protected void onFailureLoading(Throwable e) {
        ZummaApiErrorHandler.handleError(e);
        hideTitleProgress();
    }

    protected void onCompleteLoading() {
        hideTitleProgress();
    }

    public void refresh() {
        if (subscription != null) {
            subscription.unsubscribe();
        }

        showTitleProgress();
        subscription = getData()
                .subscribeOn(Schedulers.newThread())
                .retryWhen(RxUtil::exponentialBackoff)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccessLoading, this::onFailureLoading, this::onCompleteLoading);
    }
}
