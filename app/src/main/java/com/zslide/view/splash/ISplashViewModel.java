package com.zslide.view.splash;

import com.zslide.data.model.AlertMessage;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public interface ISplashViewModel {

    Completable initialize(boolean checkUpdate);

    Completable update(String action);

    Observable<AlertMessage> getAlertMessageObservable();

    void onDisposeAlertMessage();
}
