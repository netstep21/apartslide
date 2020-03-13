package com.zslide.view.splash;

import android.Manifest;

import com.zslide.R;
import com.zslide.data.AuthenticationManager;
import com.zslide.data.MetaDataManager;
import com.zslide.data.UserManager;
import com.zslide.data.model.AlertMessage;
import com.zslide.utils.DLog;
import com.zslide.view.base.PermissionContract;
import com.zslide.view.base.PermissionViewModel;
import com.zslide.view.splash.exception.GoogleApiAvailabilityException;
import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class SplashViewModel extends PermissionViewModel implements ISplashViewModel {

    enum VoidEmitItem {INSTANCE}

    private static final int REQUEST_PERMISSION_PHONE = 100;

    private BehaviorSubject<AlertMessage> alertMessageSubject;
    private PublishSubject<VoidEmitItem> disposedAlertmessageSubject;

    private final UserManager userRepository;
    private final MetaDataManager metaDataRepository;
    private final SplashNavigator navigator;

    private Disposable currentDisposable;

    public SplashViewModel(PermissionContract.View permissionView,
                           UserManager userRepository,
                           MetaDataManager metaDataRepository,
                           SplashNavigator navigator) {
        super(permissionView);
        this.userRepository = userRepository;
        this.metaDataRepository = metaDataRepository;
        this.navigator = navigator;

        this.alertMessageSubject = BehaviorSubject.create();
        this.disposedAlertmessageSubject = PublishSubject.create();
    }

    @Override
    public Completable initialize(boolean checkUpdate) {

        LinkedList<Completable> completables = new LinkedList<>();
        completables.add(checkSplashTimeout());
        completables.add(checkPermission());
        completables.add(launchBuzzScreen());
        completables.add(checkGooglePlayServices());
        if (checkUpdate) {
            completables.add(checkUpdate());
        }

        return Completable.merge(completables)
                .doOnSubscribe(disposable -> currentDisposable = disposable)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    if (currentDisposable != null && !currentDisposable.isDisposed()) {
                        if (AuthenticationManager.getInstance().isLoggedIn()) {
                            navigator.route();
                        } else {
                            navigator.openLogin();
                        }
                    }
                    currentDisposable = null;
                });
    }

    @Override
    public Completable update(String action) {
        return Completable.fromAction(() -> {
            if (currentDisposable != null) {
                currentDisposable.dispose();
            }

            String packageName = "com.apartslide";
            if (action.startsWith("http") || action.startsWith("market")) {
                int startPosition = action.lastIndexOf("id=");
                packageName = action.substring(startPosition + 3, action.length());
            }
            navigator.openMarket(packageName);
        });
    }

    private Completable checkSplashTimeout() {
        return Completable.timer(1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.newThread());
    }

    private Completable checkPermission() {
        return checkPermission(new PermissionContract.PermissionRequests(REQUEST_PERMISSION_PHONE)
                .addRequired(Manifest.permission.READ_PHONE_STATE))
                .subscribeOn(Schedulers.newThread())
                .flatMapCompletable(results -> {
                    if (results.isGrantedPermission(Manifest.permission.READ_PHONE_STATE)) {
                        return Completable.complete();
                    } else {
                        return Completable.error(new IllegalStateException("필수 권한이 없습니다."));
                    }
                })
                .doOnComplete(() -> DLog.d(this, "complete checkPermissions"));
    }

    private Completable launchBuzzScreen() {
        return Completable.fromAction(() -> BuzzScreen.getInstance().launch());
    }

    private Completable checkGooglePlayServices() {
        return Completable.create(e -> {
            if (e.isDisposed()) {
                return;
            }

            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            int resultCode = apiAvailability.isGooglePlayServicesAvailable(navigator.getContext());
            if (resultCode != ConnectionResult.SUCCESS) {
                e.onError(new GoogleApiAvailabilityException(apiAvailability));
            } else {
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .doOnComplete(() -> DLog.d(this, "complete checkGooglePlayServices"));
    }

    private Completable checkUpdate() {
        return metaDataRepository.getAlertMessage()
                .subscribeOn(Schedulers.newThread())
                .onErrorReturn(e -> AlertMessage.NULL)
                .flatMapCompletable(alertMessage -> {
                    if (alertMessage.isNull()) {
                        return Completable.complete();
                    } else {
                        return Single.just(alertMessage)
                                .flatMapCompletable(message -> {
                                    DLog.e(this, "observe");
                                    alertMessageSubject.onNext(message);
                                    return disposedAlertmessageSubject.take(1)
                                            .flatMapCompletable(v -> {
                                                DLog.e(this, "take");
                                                return Completable.complete();
                                            });
                                });
                    }
                }).doOnComplete(() -> DLog.d(this, "complete checkUpdate"));
    }

    @Override
    public Observable<AlertMessage> getAlertMessageObservable() {
        return alertMessageSubject;
    }

    @Override
    public void onDisposeAlertMessage() {
        DLog.e(this, "onDispose");
        disposedAlertmessageSubject.onNext(VoidEmitItem.INSTANCE);
    }

    @Override
    public int getRationaleMessageResource(List<String> permissions) {
        return R.string.require_phone_permission_splash;
    }
}
