package com.zslide.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.activities.BaseActivity;
import com.zslide.data.UserManager;
import com.zslide.data.model.Address;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.models.Apartment;
import com.zslide.models.ApartmentFamilyParams;
import com.zslide.models.FamilyParams;
import com.zslide.models.HouseFamilyParams;
import com.zslide.models.TempApartment;
import com.zslide.models.TempApartmentFamilyParams;
import com.zslide.utils.DisplayUtil;
import com.zslide.widget.AddressAutoCompleteTextView;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by chulwoo on 16. 5. 27..
 */
public class AddressInputFragment extends InputFragment
        implements AddressAutoCompleteTextView.SearchProgressListener {

    private static final String CHILD_FRAGMENT_TAG = "child_fragment";

    @BindView(R.id.address) AddressAutoCompleteTextView addressAutoCompleteTextView;
    @BindView(R.id.detailAddress) ViewGroup detailAddressView;
    //@BindView(R.id.houseHelp) View houseHelpView;
    @BindView(R.id.inputModeContainer) ViewGroup inputModeContainer;
    @BindView(R.id.inputModeMessage) TextView inputModeMessageView;
    @BindView(R.id.inputModeLabel) TextView inputModeLabelView;

    private Address address;
    private Fragment currentFragment;

    private PublishSubject<FamilyParams> paramsPublisher = PublishSubject.create();

    public static AddressInputFragment newInstance() {
        Bundle args = new Bundle();
        AddressInputFragment instance = new AddressInputFragment();
        instance.setArguments(args);
        return instance;
    }

    public static AddressInputFragment newInstance(Address address, Apartment apartment) {
        Bundle args = new Bundle();
        args.putParcelable(IntentConstants.EXTRA_ADDRESS, address);
        args.putParcelable(IntentConstants.EXTRA_APARTMENT, apartment);

        AddressInputFragment instance = new AddressInputFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_address_input;
    }

    @OnClick(R.id.address)
    public void clearAddress() {
        address = null;
        addressAutoCompleteTextView.clear();
        addressAutoCompleteTextView.requestFocus();
        addressAutoCompleteTextView.setSearchProgressListener(this);
        hideApartmentInput();
        setCompleteButtonEnabled(false);
    }


    @Override
    public void onStart(String keyword) {
        ((BaseActivity) getActivity()).showTitleProgress();
    }

    @Override
    public void onFinish() {
        ((BaseActivity) getActivity()).hideTitleProgress();
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        addressAutoCompleteTextView.setOnItemSelectedListener(this::setAddress);
        Address address = getArguments().getParcelable(IntentConstants.EXTRA_ADDRESS);
        if (address != null) {
            addressAutoCompleteTextView.setItem(address);
        }
        setAddress(address);
        useCompleteButton("주소 입력 완료", this::createFamilyParams);
    }

    public void setAddress(Address address) {
        this.address = address;
        if (address == null) {
            hideApartmentInput();
        } else {
            showApartmentInput(address);
        }
    }

    private void createFamilyParams() {
        if (currentFragment == null) {
            return;
        }

        User user = UserManager.getInstance().getUserValue();
        Family family = UserManager.getInstance().getFamilyValue();
        int type = FamilyParams.TYPE_CREATE;
        if (family.isFamilyLeader(user)) {
            type = FamilyParams.TYPE_UPDATE;
        }

        FamilyParams params = null;
        if (currentFragment instanceof ApartmentInputFragment) {
            ApartmentInputFragment fragment = (ApartmentInputFragment) currentFragment;
            params = new ApartmentFamilyParams(type, user, fragment.getApartment().getId(), fragment.getDong(), fragment.getHo());
        } else if (currentFragment instanceof TempApartmentInputFragment) {
            TempApartmentInputFragment fragment = (TempApartmentInputFragment) currentFragment;
            params = new TempApartmentFamilyParams(type, user, fragment.getTempApartment().getId());
        } else if (currentFragment instanceof HouseInputFragment) {
            HouseInputFragment fragment = (HouseInputFragment) currentFragment;
            params = new HouseFamilyParams(type, user, address.getId(), fragment.getDetailAddress());
        }

        if (params != null) {
            paramsPublisher.onNext(params);
        }
    }

    private void hideApartmentInput() {
        inputModeContainer.setVisibility(View.GONE);
        detailAddressView.setVisibility(View.GONE);
        if (currentFragment != null) {
            getFragmentManager().beginTransaction()
                    .remove(currentFragment)
                    .commit();
        }
    }

    private void showApartmentInput(Address address) {
        showHouseInputModeMessage();
        Apartment apartment = null;
        if (getArguments() != null) {
            apartment = getArguments().getParcelable(IntentConstants.EXTRA_APARTMENT);
            if (apartment != null) {
                inputModeContainer.setVisibility(View.GONE);
            }
        }
        detailAddressView.setVisibility(View.VISIBLE);
        ApartmentInputFragment fragment = ApartmentInputFragment.newInstance(address, apartment);
        fragment.setOnApartmentSelected(selectedApartment -> {
            if (selectedApartment != null) {
                inputModeContainer.setVisibility(View.GONE);
            }
        });
        fragment.setOnTempApartmentSelected(this::showTempApartmentInput);
        startFragment(fragment);
    }

    private void showTempApartmentInput(TempApartment tempApartment) {
        inputModeContainer.setVisibility(View.GONE);
        TempApartmentInputFragment fragment = TempApartmentInputFragment.newInstance(tempApartment);
        fragment.setTempApartmentClearListener(() -> showApartmentInput(address));
        startFragment(fragment);
    }

    private void showApartmentInputModeMessage() {
        inputModeContainer.setVisibility(View.VISIBLE);
        inputModeMessageView.setText(R.string.message_input_mode_apartment);
        inputModeLabelView.setText(R.string.label_input_mode_apartment);
    }

    private void showHouseInputModeMessage() {
        inputModeContainer.setVisibility(View.VISIBLE);
        inputModeMessageView.setText(R.string.message_input_mode_house);
        inputModeLabelView.setText(R.string.label_input_mode_house);
    }

    @OnClick(R.id.inputMode)
    public void changeInputMode() {
        if (currentFragment instanceof HouseInputFragment) {
            startApartmentInput();
        } else {
            startHouseInput();
        }
    }

    public void startApartmentInput() {
        showHouseInputModeMessage();
        startFragment(ApartmentInputFragment.newInstance(address));
    }

    public void startHouseInput() {
        showApartmentInputModeMessage();
        startFragment(HouseInputFragment.newInstance());
    }

    private void startFragment(InputFragment fragment) {
        currentFragment = fragment;
        fragment.setOnInputStateChangeListener(completed -> onInputStateChanged());
        getFragmentManager().beginTransaction()
                .replace(R.id.detailAddress, fragment, CHILD_FRAGMENT_TAG)
                .commit();
    }

    public Observable<FamilyParams> getFamilyParamsPublisher() {
        return paramsPublisher;
    }

    @OnFocusChange({R.id.address})
    public void showKeyboard(View v, boolean hasFocus) {
        if (hasFocus) {
            addressAutoCompleteTextView.postDelayed(() -> DisplayUtil.showKeyboard(getContext(), v), 100);
            addressAutoCompleteTextView.clear();
            // 어드레스를 누를 경우 focuschange가 콜 된 후에 click이 콜 되더라.
            // 그러다보니 clear동작이 바로 동작하지않는 버그 있었음. 이 라인 추가로 해결
            setCompleteButtonEnabled(false);
        }
    }

    @Override
    public boolean isCompleted() {
        if (address == null) {
            return false;
        }

        Fragment fragment = getFragmentManager().findFragmentByTag(CHILD_FRAGMENT_TAG);

        if (fragment == null) {
            return false;
        }

        if (fragment instanceof ApartmentInputFragment) {
            return ((ApartmentInputFragment) fragment).isCompleted();
        } else if (fragment instanceof TempApartmentInputFragment) {
            return ((TempApartmentInputFragment) fragment).isCompleted();
        } else if (fragment instanceof HouseInputFragment) {
            return ((HouseInputFragment) fragment).isCompleted();
        }

        return false;
    }
}
