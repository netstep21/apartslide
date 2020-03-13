package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.activities.BaseActivity;
import com.zslide.data.model.Address;
import com.zslide.dialogs.TempApartmentConfirmDialog;
import com.zslide.dialogs.TempApartmentSelectDialog;
import com.zslide.dialogs.TempApartmentSelectDialog.OnTempApartmentSelectListener;
import com.zslide.models.Apartment;
import com.zslide.models.TempApartment;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.DisplayUtil;
import com.zslide.utils.StringUtil;
import com.zslide.widget.ApartmentAutoCompleteTextView;
import com.zslide.widget.ZummaAutoCompleteTextView.SearchProgressListener;

import java.util.ArrayList;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 16. 4. 6..
 */
public class ApartmentInputFragment extends InputFragment
        implements SearchProgressListener, OnTempApartmentSelectListener {

    private static final String TEMP_APARTMENT_SELECT_DIALOG = "temp_apartments_dialog";
    private static final String APARTMENT_REGISTRATION_DIALOG = "apartment_registration_dialog";
    private static final String TAG = "ApartmentInputFragment";

    @BindView(R.id.apartment) ApartmentAutoCompleteTextView apartmentView;
    @BindView(R.id.detailAddressContainer) ViewGroup detailAddressContainer;
    @BindView(R.id.dong) EditText dongView;
    @BindView(R.id.ho) EditText hoView;

    private Address address;
    private Apartment apartment;

    private TempApartmentSelectDialog tempApartmentSelectDialog;
    private TempApartmentConfirmDialog tempApartmentConfirmDialog;

    private Action1<Apartment> onApartmentSelected;
    private Action1<TempApartment> onTempApartmentSelected;
    private Subscription request;

    public static ApartmentInputFragment newInstance(Address address) {
        return newInstance(address, null);
    }

    public static ApartmentInputFragment newInstance(Address address, Apartment apartment) {
        Bundle args = new Bundle();
        args.putParcelable(IntentConstants.EXTRA_ADDRESS, address);
        args.putParcelable(IntentConstants.EXTRA_APARTMENT, apartment);
        ApartmentInputFragment instance = new ApartmentInputFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_apartment_input;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            address = getArguments().getParcelable(IntentConstants.EXTRA_ADDRESS);
            apartment = getArguments().getParcelable(IntentConstants.EXTRA_APARTMENT);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        apartmentView.requestFocus();
    }

    @Override
    public void onPause() {
        if (request != null && !request.isUnsubscribed()) {
            request.unsubscribe();
        }
        super.onPause();
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        apartmentView.setAddress(address);
        apartmentView.setOnItemSelectedListener(this::setApartment);
        apartmentView.setSearchProgressListener(this);
        apartmentView.setApartmentRegister(this::onSuccessLoading);
        dongView.setFilters(new InputFilter[]{(source, start, end, dest, dstart, dend) -> {
            Pattern namePattern = StringUtil.getNamePattern();
            return !namePattern.matcher(source).matches() ? "" : null;
        }, new InputFilter.LengthFilter(6)});
        dongView.setRawInputType(InputType.TYPE_CLASS_NUMBER);
        bindApartment(apartment);
    }

    private void onSuccessLoading() {
        showTitleProgress();
        request = ZummaApi.address().tempApartments(address)
                .subscribeOn(Schedulers.newThread())
                .map(ArrayList::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccessLoading, this::onFailureLoading);
    }

    private void dismissTempApartmentsDialog() {
        FragmentManager fm = getFragmentManager();
        if (fm.findFragmentByTag(TEMP_APARTMENT_SELECT_DIALOG) != null) {
            tempApartmentSelectDialog.dismiss();
            tempApartmentSelectDialog = null;
        }
    }

    private void onSuccessLoading(ArrayList<TempApartment> tempApartments) {
        dismissTempApartmentsDialog();
        hideTitleProgress();
        tempApartmentSelectDialog = TempApartmentSelectDialog.newInstance(address, tempApartments);
        tempApartmentSelectDialog.setOnTempApartmentSelectListener(this);
        tempApartmentSelectDialog.show(getFragmentManager(), TEMP_APARTMENT_SELECT_DIALOG);
    }

    private void onFailureLoading(Throwable e) {
        hideTitleProgress();
        ZummaApiErrorHandler.handleError(e);
    }

    private void showTitleProgress() {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).showTitleProgress();
        }
    }

    private void hideTitleProgress() {
        if (getActivity() != null) {
            ((BaseActivity) getActivity()).hideTitleProgress();
        }
    }

    @OnClick(R.id.apartment)
    public void clearApartment() {
        this.apartment = null;
        apartmentView.clear();
        bindApartment(null);
        bindApartmentDetail("", -1);
    }

    protected void bindApartment(Apartment apartment) {
        if (apartment == null) {
            detailAddressContainer.setVisibility(View.GONE);
        } else {
            apartmentView.setItem(apartment);
            detailAddressContainer.setVisibility(View.VISIBLE);
        }
        bindApartmentDetail("", -1);
    }

    protected void bindApartmentDetail(String dong, int ho) {
        dongView.setText(dong);
        hoView.setText((ho == -1) ? "" : String.format("%s", ho));
    }

    public void dismissDropdown() {
        if (apartmentView != null) {
            apartmentView.dismissDropdown();
        }
    }

    @OnTextChanged(value = R.id.tempApartmentName)
    public void onTempApartmentChanged(CharSequence tempApartmentName) {
        onInputStateChanged();
    }

    @OnTextChanged(value = R.id.dong, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onDongChanged(CharSequence dong) {
        onInputStateChanged();
    }

    @OnTextChanged(value = R.id.ho, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void onHoChanged(CharSequence ho) {
        onInputStateChanged();
    }

    @Override
    public boolean isCompleted() {
        return apartment != null && !TextUtils.isEmpty(getDong()) && getHo() != -1;
    }

    @OnEditorAction({R.id.dong, R.id.ho})
    boolean onEditorAction(int actionId) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_NEXT:
                hoView.post(hoView::requestFocus);
                return true;
            case EditorInfo.IME_ACTION_DONE:
                if (isCompleted()) {
                    complete();
                } else {
                    dongView.post(dongView::requestFocus);
                }
                return true;
        }
        return false;
    }

    @OnFocusChange({R.id.apartment, R.id.dong, R.id.ho})
    public void showKeyboard(View v, boolean hasFocus) {
        if (hasFocus) {
            v.postDelayed(() -> DisplayUtil.showKeyboard(getContext(), v), 100);
        }
    }

    public void setOnApartmentSelected(Action1<Apartment> onApartmentSelected) {
        this.onApartmentSelected = onApartmentSelected;
    }

    public void setOnTempApartmentSelected(Action1<TempApartment> onTempApartmentSelected) {
        this.onTempApartmentSelected = onTempApartmentSelected;
    }

    @Override
    public void onTempApartmentSelected(TempApartment tempApartment) {
        tempApartmentComplete(tempApartment.getName());

        // TODO: replce to temp apartment view/fragment
    }

    @Override
    public void onInputTempApartmentName(String name) {
        tempApartmentComplete(name);
    }

    private void tempApartmentComplete(String name) {
        dismissTempApartmentsDialog();
        showTitleProgress();
        request = ZummaApi.address().createTempApartment(address, name)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(tempApartment -> onTempApartmentSelected != null)
                .subscribe(onTempApartmentSelected, this::onFailureLoading, this::onFinish);
//        tempApartmentConfirmDialog = TempApartmentConfirmDialog.newInstance(address, name);
//        tempApartmentConfirmDialog.setOnConfirmListener(() -> {
//            dismissTempApartmentsDialog();
//            showTitleProgress();
//            request = ZummaApi.address().createTempApartment(address, name)
//                    .subscribeOn(Schedulers.newThread())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .filter(tempApartment -> onTempApartmentSelected != null)
//                    .subscribe(onTempApartmentSelected, this::onFailureLoading, this::onFinish);
//        });
//        tempApartmentConfirmDialog.show(getFragmentManager(), APARTMENT_REGISTRATION_DIALOG);
    }

    @Override
    public void onStart(String keyword) {
        showTitleProgress();
    }

    @Override
    public void onFinish() {
        hideTitleProgress();
    }

    public Address getAddress() {
        return address;
    }

    public Apartment getApartment() {
        return apartment;
    }

    public void setApartment(Apartment apartment) {
        this.apartment = apartment;
        if (onApartmentSelected != null) {
            onApartmentSelected.call(apartment);
        }
        bindApartment(apartment);
    }

    public String getDong() {
        try {
            return dongView.getText().toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public int getHo() {
        try {
            return Integer.parseInt(hoView.getText().toString());
        } catch (Exception e) {
            return -1;
        }
    }
}
