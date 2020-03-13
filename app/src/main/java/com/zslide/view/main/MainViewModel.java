package com.zslide.view.main;

import com.zslide.data.MetaDataManager;
import com.zslide.data.UserManager;
import com.zslide.data.model.EventBanner;
import com.zslide.data.model.HomeZmoney;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.BehaviorSubject;

/**
 * Created by chulwoo on 2017. 12. 28..
 */

public class MainViewModel implements MainContract.ViewModel {

    private static final String TUTORIAL_ZMONEY = "zmoney_learned_user";

    private final MainNavigator navigator;
    private final MetaDataManager metaDataRepository;
    private final UserManager userRepository;

    private BehaviorSubject<Boolean> needShowZmoneyTutorialSubject;

    public MainViewModel(MainNavigator navigator,
                         MetaDataManager metaDataRepository, UserManager userRepository) {
        this.navigator = navigator;
        this.metaDataRepository = metaDataRepository;
        this.userRepository = userRepository;
        //this.needShowZmoneyTutorialSubject = BehaviorSubject.createDefault(metaDataRepository.isNeedTutorial(TUTORIAL_ZMONEY));
    }

    @Override
    public Completable refreshZmoney() {
        return userRepository.fetchHomeZmoney();
    }

    @Override
    public Observable<HomeZmoney> getHomeZmoneyObservable() {
        return userRepository.getZmoneysObservable();
    }

    @Override
    public Observable<MainUiModel> getHomeUiModelObservable() {
        return Observable.combineLatest(
                userRepository.getUserObservable(),
                userRepository.getFamilyObservable(),
                Observable.just(navigator),
                MainUiModel::new);
        // todo loading indicator visible. on next
    }

    public Single<List<EventBannerItem>> getEventBannerItems() {
        return metaDataRepository.getEventBanners()
                .flatMapObservable(Observable::fromIterable)
                .map(this::createEventBannerItem)
                .toList();
    }

    @Override
    public void onSettingsClicked() {
        navigator.openSettingsPage();
    }

    private EventBannerItem createEventBannerItem(EventBanner eventBanner) {
        return new EventBannerItem(eventBanner, () -> navigator.navigateFrom(eventBanner.getTarget()));
    }

/*    @NonNull
    @Override
    public Observable<Boolean> needShowZmoneyTutorialStream() {
        return needShowZmoneyTutorialSubject;
    }

    @NonNull
    @Override
    public Completable completeZmoneyTutorial() {
        return Completable.fromAction(() -> {
            metaDataRepository.completeTutorial(TUTORIAL_ZMONEY);
            needShowZmoneyTutorialSubject.onNext(false);
            needShowZmoneyTutorialSubject.onComplete();
            needShowZummaCastTutorialSubject.onNext(needShowZummaCastTutorialSubject.getValue());
        });
    }

    @NonNull
    @Override
    public Observable<Boolean> needShowFamilyRegistrationGuide() {
        return Observable.combineLatest(
                userRepository.getFamilyStream(),
                metaDataRepository.isTimeoutFamilyRegistrationGuide().toObservable(),
                (family, timeout) -> family.isEmpty() && timeout);
        // family.isEmpty() && viewModel.isTimeoutFamilyRegistrationGuideDisable()
    }*/
}
