package com.zslide.view.main;

import com.zslide.data.model.HomeZmoney;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public interface MainContract {

    interface ViewModel {

        Completable refreshZmoney();

        Observable<HomeZmoney> getHomeZmoneyObservable();

        Observable<MainUiModel> getHomeUiModelObservable();

        Single<List<EventBannerItem>> getEventBannerItems();

        void onSettingsClicked();
    }
}
