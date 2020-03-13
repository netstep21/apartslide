package com.zslide.data;

import android.content.Context;

import com.zslide.data.local.LocalUserSource;
import com.zslide.data.model.Family;
import com.zslide.data.model.HomeZmoney;
import com.zslide.data.model.InviteInfo;
import com.zslide.data.model.User;
import com.zslide.data.remote.RemoteUserSource;
import com.zslide.network.RetrofitException;
import com.zslide.utils.DLog;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class UserManager {

    private boolean initialized = false;

    private BehaviorSubject<User> userSubject;
    private BehaviorSubject<Family> familySubject;
    private BehaviorSubject<HomeZmoney> homeZmoneySubject;

    private LocalUserSource localSource;
    private RemoteUserSource remoteSource;

    public static UserManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private UserManager() {
    }

    public void init(Context context) {
        localSource = new LocalUserSource(context);
        remoteSource = new RemoteUserSource();

        userSubject = BehaviorSubject.createDefault(localSource.getUser());
        familySubject = BehaviorSubject.createDefault(localSource.getFamily());
        homeZmoneySubject = BehaviorSubject.createDefault(localSource.getHomeZmoney());

        initialized = true;
    }

    public Completable fetchUserAndFamily() {
        checkInitialize();
        return Completable.concatArray(remoteSource.getMe().flatMapCompletable(this::updateUser),
                Single.defer(() -> {
                    User user = UserManager.getInstance().getUserValue();
                    return Single.just(user.getFamilyId());
                })
                        .flatMap(remoteSource::getFamily)
                        .flatMapCompletable(this::updateFamily))
                .subscribeOn(Schedulers.io())
                .doOnError(this::handleNetworkErrorOnFetch)
                .onErrorComplete();
    }

    public Completable fetchUser() {
        checkInitialize();
        return remoteSource.getMe()
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::updateUser)
                .doOnError(this::handleNetworkErrorOnFetch)
                .doOnSubscribe(disposable -> DLog.d(this, "fetchUser start"))
                .doOnComplete(() -> DLog.d(this, "fetchUser complete"))
                .onErrorComplete();
    }

    public Completable fetchFamily() {
        checkInitialize();
        return Single.defer(() -> {
            User user = UserManager.getInstance().getUserValue();
            return Single.just(user.getFamilyId());
        }).flatMap(remoteSource::getFamily)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::updateFamily)
                .doOnError(this::handleNetworkErrorOnFetch)
                .doOnSubscribe(disposable -> DLog.d(this, "fetchFamily start"))
                .doOnComplete(() -> DLog.d(this, "fetchFamily complete"))
                .onErrorComplete();
    }

    public Completable fetchHomeZmoney() {
        checkInitialize();
        return Single.defer(() -> {
            User user = UserManager.getInstance().getUserValue();
            return Single.just(user.getFamilyId());
        }).flatMap(remoteSource::getHomeZmoney)
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(this::updateZmoneys)
                .doOnError(this::handleNetworkErrorOnFetch)
                .onErrorComplete();
    }

    private void handleNetworkErrorOnFetch(Throwable throwable) {
        if (!(throwable instanceof RetrofitException)) {
            DLog.e(throwable);
        }
    }

    public Completable clear() {
        checkInitialize();
        return Completable.fromAction(() -> {
            localSource.clear();

            userSubject.onComplete();
            familySubject.onComplete();
            homeZmoneySubject.onComplete();

            userSubject = BehaviorSubject.createDefault(User.NULL);
            familySubject = BehaviorSubject.createDefault(Family.NULL);
            homeZmoneySubject = BehaviorSubject.createDefault(HomeZmoney.NULL);
        });
    }

    public Observable<User> getUserObservable() {
        checkInitialize();
        return userSubject;
    }

    public User getUserValue() {
        checkInitialize();
        return userSubject.getValue();
    }

    public Observable<Family> getFamilyObservable() {
        checkInitialize();
        return familySubject;
    }

    public Family getFamilyValue() {
        checkInitialize();
        return familySubject.getValue();
    }

    public Observable<HomeZmoney> getZmoneysObservable() {
        checkInitialize();
        return homeZmoneySubject;
    }

    public HomeZmoney getZmoneyValue() {
        checkInitialize();
        return homeZmoneySubject.getValue();
    }

    public Completable updateUser(User user) {
        checkInitialize();
        return Single.just(user).flatMapCompletable(u -> {
            localSource.setUser(u);
            userSubject.onNext(u);

            return Completable.complete();
        });
    }

    public Completable updateFamily(Family family) {
        checkInitialize();
        return Single.just(family).flatMapCompletable(f -> {
            localSource.setFamily(f);
            familySubject.onNext(f);

            /*
             * 가족 정보가 바뀌었는지 확인하고 사용자의 가족 id를 업데이트 함, (정상적인 fetch를 위해)
             */
            User user = userSubject.getValue();
            if (user.getFamilyId() != f.getId()) {
                setNotifiedBan(false);
                user.setFamilyId(f.getId());
                updateUser(user);
            }

            return Completable.complete();
        });
    }

    private Completable updateZmoneys(HomeZmoney homeZmoney) {
        return Single.just(homeZmoney).flatMapCompletable(zmoneys -> {
            localSource.setHomeZmoney(zmoneys);
            homeZmoneySubject.onNext(zmoneys);
            return Completable.complete();
        });
    }

    public Single<Boolean> checkNotifiedBan() {
        checkInitialize();
        return Single.defer(() -> Single.just(localSource.isNotifiedBan()));
    }

    public Completable setNotifiedBan(boolean notified) {
        checkInitialize();
        return Single.just(notified).flatMapCompletable(n -> {
            localSource.setNotifiedBan(n);
            return Completable.complete();
        });
    }

    public Single<InviteInfo> getInviteInfo() {
        return remoteSource.getInviteInfo().subscribeOn(Schedulers.io());
    }

    private static class InstanceHolder {

        private static final UserManager INSTANCE = new UserManager();
    }

    private void checkInitialize() {
        if (!initialized) {
            throw new IllegalStateException("사용을 시작하기 전에 init 메소드를 호출해야 합니다.");
        }
    }
}