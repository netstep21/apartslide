package com.zslide.activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.fragments.AddressInputFragment;
import com.zslide.fragments.FamilyNameCheckFragment;
import com.zslide.fragments.FamilyRegistrationIntroFragment;
import com.zslide.fragments.FamilySearchFragment;
import com.zslide.fragments.InputFragment;
import com.zslide.models.FamilyParams;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 2016. 6. 3..
 * <p>
 * TODO: 복잡한 로직 추후 mvvm 적용하며 개선
 */
public class FamilyRegistrationActivity extends BaseSingleFragmentActivity<FamilyRegistrationIntroFragment> {

    @Override
    protected FamilyRegistrationIntroFragment createFragment() {
        User user = UserManager.getInstance().getUserValue();
        return FamilyRegistrationIntroFragment.newInstance(user.hasFamily());
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_family_update);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_single_fragment;
    }

    public void searchFamily() {
        FamilySearchFragment fragment = FamilySearchFragment.newInstance();
        fragment.getFamilyPublisher().subscribe(this::onComplete);
        startFragment(fragment, "family_input");
    }

    public void inputAddress() {
        AddressInputFragment fragment = AddressInputFragment.newInstance();
        fragment.getFamilyParamsPublisher().subscribe(params -> {
            showButtonProgress();
            createFamily(params);
        });
        startFragment(fragment, "family_input");
    }

    public void createFamily(FamilyParams params) {
        if (params.getApiType() == FamilyParams.TYPE_CREATE) {
            FamilyNameCheckFragment fragment = FamilyNameCheckFragment.newInstance();
            fragment.getFamilyNamePublisher().subscribe(familyName ->
                    familyWith(ZummaApi.user().createFamily(familyName, params)));
            startFragment(fragment, "family_name_check");
        } else if (params.getApiType() == FamilyParams.TYPE_UPDATE) {
            familyWith(ZummaApi.user().updateFamily(params));
        }
    }

    private void familyWith(Observable<Family> familyObservable) {
        familyObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(Family::isNotNull)
                .doOnError(e -> hideTitleProgress())
                .subscribe(this::onComplete, e -> {
                    hideButtonProgress();
                    ZummaApiErrorHandler.handleError(e);
                });
    }

    private void onComplete(Family family) {
        showButtonProgress();
        UserManager.getInstance().fetchUser()
                .andThen(UserManager.getInstance().fetchFamily())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Navigator.startMainActivity(this, 0);
                    Navigator.startInviteActivity(this, InviteActivity.TYPE_FAMILY);
                    finish();
                });
    }

    private void showButtonProgress() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("family_input");
        if (fragment instanceof InputFragment) {
            ((InputFragment) fragment).showProgress();
        }
    }

    private void hideButtonProgress() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("family_input");
        if (fragment instanceof InputFragment) {
            ((InputFragment) fragment).hideProgress();
        }
    }

    private void startFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentByTag(tag) == null) {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(
                            R.anim.slide_in_from_right, R.anim.slide_out_to_left,
                            R.anim.slide_in_from_left, R.anim.slide_out_to_right)
                    .add(R.id.fragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
