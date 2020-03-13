package com.zslide.activities;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.fragments.AddressInputFragment;
import com.zslide.models.Apartment;
import com.zslide.models.TempApartment;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 4. 12..
 * <p>
 * 아파트 주소 완성 액티비티
 * precondition: {@code Apartment}가 포함된 {@code TempApartment}를 갖고있다.
 */
public class AddressCompleteActivity extends BaseSingleFragmentActivity<AddressInputFragment> {

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_temp_apartment_complete;
    }

    @Override
    protected AddressInputFragment createFragment() {
        Family family = UserManager.getInstance().getFamilyValue();
        TempApartment tempApartment = family.getTempApartment();
        Apartment apartment;
        if (tempApartment == null) {
            apartment = family.getApartment();
        } else {
            apartment = tempApartment.getApartment();
        }

        return AddressInputFragment.newInstance(family.getAddress(), apartment);
    }

    @Override
    protected void onStart() {
        super.onStart();
        fragment.getFamilyParamsPublisher().subscribe(params -> {
            showTitleProgress();
            ZummaApi.user().updateFamily(params)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(family -> {
                        Navigator.startMainActivity(this, 0);
                        finish();
                    }, e -> {
                        hideTitleProgress();
                        ZummaApiErrorHandler.handleError(e);
                    });
        });
    }

    @Override
    public String getScreenName() {
        return getString(R.string.screen_apartment_complete);
    }
}
