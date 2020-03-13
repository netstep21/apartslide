package com.zslide.view.setting;

import com.zslide.view.setting.adapter.item.SettingItem;
import com.zslide.view.setting.adapter.item.SwitchSettingItem;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;

/**
 * Created by chulwoo on 2018. 1. 5..
 */

public interface SettingsContract {

    interface ViewModel {

        Completable refreshFamily();

        Observable<List<SettingItem>> getSettingItemsObservable();
        
        Observable<SettingUiModel> getSettingUiModelObservable();

        void onUserProfileClick();

        SwitchSettingItem getLockerSettingItem();

        void disableLocker();

        void snooze(int snoozeSec);
    }
}
