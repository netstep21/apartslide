package com.zslide.data;

import android.content.Context;

import com.zslide.data.local.LocalMetaDataSource;
import com.zslide.data.model.AlertMessage;
import com.zslide.data.model.EventBanner;
import com.zslide.data.model.User;
import com.zslide.data.remote.RemoteMetaDataSource;
import com.zslide.models.Sex;
import com.buzzvil.buzzscreen.sdk.BuzzScreen;
import com.buzzvil.buzzscreen.sdk.UserProfile;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class MetaDataManager {

    private boolean initialized = false;
    private LocalMetaDataSource localSource;
    private RemoteMetaDataSource remoteSource;

    public static MetaDataManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void init(Context context) {
        localSource = new LocalMetaDataSource(context);
        remoteSource = new RemoteMetaDataSource();
        localSource.migrationIfNeeded(context);

        initialized = true;
    }

    private MetaDataManager() {
    }

    public Maybe<AlertMessage> getAlertMessage() {
        checkInitialize();
        return remoteSource.getAlertMessage();
    }

    public Maybe<List<EventBanner>> getEventBanners() {
        return remoteSource.getEventBanners();
    }

    public Single<Boolean> isRewardNotificationEnabled() {
        return Single.defer(() -> Single.just(localSource.isRewardNotificationEnabled()));
    }

    public Completable setRewardNotificationEnabled(boolean enabled) {
        return Single.just(enabled).flatMapCompletable(value -> {
            localSource.setRewardNotificationEnabled(value);
            return Completable.complete();
        });
    }

    public Single<Boolean> isDontAskAgainZmoneyUse() {
        return Single.defer(() -> Single.just(localSource.isDontAskAgainZmoneyUse()));
    }

    public Completable setDontAskAgainZmoneyUse() {
        return Completable.fromAction(() -> localSource.setDontAskAgainZmoneyUse(false));
    }

    public Completable clear() {
        return Completable.fromAction(localSource::clear);
    }

    public Single<Boolean> isLockerEnabled() {
        return Single.defer(() ->
                Single.just(BuzzScreen.getInstance().isActivated() &&
                        !BuzzScreen.getInstance().isSnoozed()));
    }

    public Completable enableLocker() {
        return Completable.fromAction(() -> {
            User user = UserManager.getInstance().getUserValue();
            UserProfile userProfile = BuzzScreen.getInstance().getUserProfile();
            userProfile.setUserId(String.valueOf(user.getId()));
            userProfile.setGender(Sex.MAN.equals(user.getSex()) ? UserProfile.USER_GENDER_MALE : UserProfile.USER_GENDER_FEMALE);
            try {
                userProfile.setBirthYear(Integer.parseInt(user.getBirthYear()));
            } catch (Exception e) {
                // pass
            }

            BuzzScreen.getInstance().activate();
        });
    }

    public Completable disableLocker() {
        return Completable.fromAction(() -> BuzzScreen.getInstance().deactivate());
    }

    public Completable snoozeLocker(int sec) {
        return Single.just(sec).flatMapCompletable(value -> {
            BuzzScreen.getInstance().snooze(value);
            return Completable.complete();
        });
    }

    private static class InstanceHolder {

        private static final MetaDataManager INSTANCE = new MetaDataManager();
    }

    private void checkInitialize() {
        if (!initialized) {
            throw new IllegalStateException("사용을 시작하기 전에 init 메소드를 호출해야 합니다.");
        }
    }
}
