package com.zslide.view.setting;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;

import com.zslide.R;
import com.zslide.data.MetaDataManager;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.models.LevelInfo;
import com.zslide.utils.DLog;
import com.zslide.view.setting.adapter.item.SettingItem;
import com.zslide.view.setting.adapter.item.SwitchSettingItem;
import com.crashlytics.android.Crashlytics;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public class SettingsViewModel implements SettingsContract.ViewModel {

    private static final int SETTING_HOUSE = 0;
    private static final int SETTING_USE_LOCKER = 1;
    private static final int SETTING_NOTIFICATION = 2;
    private static final int SETTING_USE_ZMONEY = 3;
    private static final int SETTING_NOTICE = 4;
    private static final int SETTING_HELP = 5;
    private static final int SETTING_INFO = 6;

    private final Context context;
    private final UserManager userManager;
    private final MetaDataManager metaDataManager;
    private final SettingNavigator navigator;

    private BehaviorSubject<List<SettingItem>> settingItemsSubject;

    public SettingsViewModel(@NonNull Context context,
                             @NonNull LifecycleProvider<ActivityEvent> lifecycleProvider,
                             UserManager userManager,
                             MetaDataManager metaDataManager,
                             SettingNavigator navigator) {
        this.context = context;
        this.userManager = userManager;
        this.metaDataManager = metaDataManager;
        this.navigator = navigator;
        this.settingItemsSubject = BehaviorSubject.createDefault(new ArrayList<>());

        lifecycleProvider.lifecycle()
                .filter(ActivityEvent.RESUME::equals)
                .subscribe(__ -> {
                    userManager.getFamilyObservable()
                            .filter(___ -> !settingItemsSubject.getValue().isEmpty())
                            .compose(lifecycleProvider.bindUntilEvent(ActivityEvent.PAUSE))
                            .subscribe(this::onFamilyUpdate, DLog::e);
                    Single.zip(metaDataManager.isLockerEnabled(),
                            metaDataManager.isRewardNotificationEnabled(),
                            this::createSettingItems)
                            .compose(lifecycleProvider.bindUntilEvent(ActivityEvent.PAUSE))
                            .onErrorResumeNext(error -> {
                                if (error instanceof CancellationException) return Single.never();
                                return Single.error(error);
                            })
                            .subscribe(settingItemsSubject::onNext, DLog::e);
                }, DLog::e);
    }

    private void onFamilyUpdate(Family family) {
        String subtitle = "";
        User me = userManager.getUserValue();
        if (family.isNull() || !family.isMember(me)) {
            subtitle = context.getString(R.string.please_registration_family);
        }

        List<SettingItem> settingItems = settingItemsSubject.getValue();
        settingItems.get(SETTING_HOUSE).setSubtitle(subtitle);
        settingItemsSubject.onNext(settingItems);
    }

    private List<SettingItem> createSettingItems(boolean lockerEnabled, boolean rewardNotificationEnabled) {
        List<SettingItem> settingItems = new ArrayList<>();

        TypedArray icons = context.getResources().obtainTypedArray(R.array.setting_icons);
        String[] labels = context.getResources().getStringArray(R.array.setting_labels);

        for (int i = 0; i < icons.length(); i++) {
            int icon = icons.getResourceId(i, 0);
            String label = labels[i];
            if (i == SETTING_HOUSE) {
                String subtitle = "";
                Family family = userManager.getFamilyValue();
                User me = userManager.getUserValue();
                if (family.isNull() || !family.isMember(me)) {
                    subtitle = context.getString(R.string.please_registration_family);
                }

                SettingItem item = new SettingItem(icon, label, subtitle);
                item.setOnClickAction(createItemBehavior(i)); // i == type
                settingItems.add(item);
            } else if (i == SETTING_USE_LOCKER) {
                settingItems.add(new SwitchSettingItem(lockerEnabled, icon, label,
                        item -> toggleLockerEnabled((SwitchSettingItem) item)));
            } else if (i == SETTING_NOTIFICATION) {
                settingItems.add(new SwitchSettingItem(rewardNotificationEnabled, icon, label,
                        item -> toggleRewardNotification((SwitchSettingItem) item)));
            } else {
                SettingItem item = new SettingItem(icon, label);
                item.setOnClickAction(createItemBehavior(i)); // i == type
                settingItems.add(item);
            }
        }
        icons.recycle();
        settingItems.get(SETTING_NOTICE).setSectionHeader(true);

        return settingItems;
    }

    private void toggleLockerEnabled(SwitchSettingItem item) {
        boolean enabled = !item.isChecked();
        if (enabled) {
            enableLocker();
        } else {
            navigator.showLockerEnableDialog(snoozeSec -> {
                if (snoozeSec == Integer.MAX_VALUE) {
                    disableLocker();
                } else {
                    snooze(snoozeSec);
                }
            }, () -> {
                item.setChecked(true);
                settingItemsSubject.onNext(settingItemsSubject.getValue());
            });
        }
    }

    private void toggleRewardNotification(SwitchSettingItem item) {
        boolean enabled = !item.isChecked();
        metaDataManager.setRewardNotificationEnabled(enabled)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    item.setChecked(enabled);
                    settingItemsSubject.onNext(settingItemsSubject.getValue());
                });
    }

    private Consumer<SettingItem> createItemBehavior(int type) {
        switch (type) {
            case SETTING_HOUSE:
                return __ -> {
                    User user = userManager.getUserValue();
                    Family family = userManager.getFamilyValue();
                    if (family.isNotNull() && family.isMember(user)) {
                        navigator.openFamilyInfoPage();
                    } else {
                        navigator.openFamilyRegistrationPage();
                    }
                };
            case SETTING_USE_ZMONEY:
                return __ -> metaDataManager.isDontAskAgainZmoneyUse()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(dontAsk -> {
                            if (dontAsk) {
                                navigator.startZmoney();
                            } else {
                                navigator.showZmoneySuggestionDialog();
                            }
                        });
            case SETTING_NOTICE:
                return __ -> navigator.openNoticesPage();
            case SETTING_HELP:
                return __ -> navigator.openHelpPage();
            case SETTING_INFO:
                return __ -> navigator.openAppInfoPage();
            default:
                return null;
        }
    }

    @Override
    public Completable refreshFamily() {
        return userManager.fetchFamily();
    }

    @Override
    public Observable<List<SettingItem>> getSettingItemsObservable() {
        return settingItemsSubject;
    }

    @Override
    public Observable<SettingUiModel> getSettingUiModelObservable() {
        return userManager.getUserObservable().map(this::createSettingUiModel);
    }

    private SettingUiModel createSettingUiModel(User user) {
        LevelInfo levelInfo = user.getLevelInfo();

        String displayLevel = "";
        if (levelInfo == null) {
            // 대체 왜... 서버에선 이걸 해결 안해줄까;
            Crashlytics.log("LevelInfo is null...");
        } else {
            LevelInfo.Advantage advantage = levelInfo.getAdvantage();
            if (advantage == null) {
                // 대체 왜... 서버에선 이걸 해결 안해줄까;
                Crashlytics.log("Advantage is null...");
            } else {
                displayLevel = advantage.getName();
            }
        }

        return new SettingUiModel(user.getProfileImageUrl(), user.getDisplayNickname(context), displayLevel);
    }

    @Override
    public void onUserProfileClick() {
        navigator.openMyAccountPage();
    }

    @Override
    public SwitchSettingItem getLockerSettingItem() {
        return (SwitchSettingItem) settingItemsSubject.getValue().get(SETTING_USE_LOCKER);
    }

    public void enableLocker() {
        metaDataManager.enableLocker()
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    getLockerSettingItem().setChecked(true);
                    settingItemsSubject.onNext(settingItemsSubject.getValue());
                });
    }

    @Override
    public void disableLocker() {
        metaDataManager.disableLocker()
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    getLockerSettingItem().setChecked(false);
                    settingItemsSubject.onNext(settingItemsSubject.getValue());
                });
    }

    @Override
    public void snooze(int snoozeSec) {
        metaDataManager.snoozeLocker(snoozeSec)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    getLockerSettingItem().setChecked(false);
                    settingItemsSubject.onNext(settingItemsSubject.getValue());
                });
    }
}
