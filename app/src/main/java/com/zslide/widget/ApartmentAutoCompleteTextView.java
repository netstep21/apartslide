package com.zslide.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.zslide.R;
import com.zslide.data.model.Address;
import com.zslide.models.Apartment;
import com.zslide.network.ZummaApi;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by chulwoo on 2016. 4. 6..
 * <p>
 */
public class ApartmentAutoCompleteTextView extends ZummaAutoCompleteTextView<Apartment> {

    private Address address;
    private ApartmentEmptyView emptyView;
    private ApartmentEmptyView.ApartmentRegister apartmentRegister;

    public ApartmentAutoCompleteTextView(Context context) {
        super(context);
        init();
    }

    public ApartmentAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ApartmentAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ApartmentAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setHint(R.string.message_input_apartment);
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public Observable<List<Apartment>> search(String keyword) {
        if (address == null) {
            return Observable.just(new ArrayList<>());
        } else {
            if (keyword.endsWith("아파트")) {
                keyword = keyword.substring(0, keyword.length() - 3);
            }
            return ZummaApi.address().apartment(address.getId(), keyword);
        }
    }

    @Override
    protected View createDropDownEmptyView(Context context) {
        emptyView = new ApartmentEmptyView(context);
        emptyView.setApartmentRegister(apartmentRegister);
        return emptyView;
    }

    public void setApartmentRegister(ApartmentEmptyView.ApartmentRegister apartmentRegister) {
        this.apartmentRegister = apartmentRegister;
        if (emptyView != null) {
            emptyView.setApartmentRegister(apartmentRegister);
        }
    }
}
