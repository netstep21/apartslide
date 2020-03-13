package com.zslide.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.zslide.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chulwoo on 16. 4. 8..
 */
public class ApartmentEmptyView extends LinearLayout {

    private ApartmentRegister apartmentRegister;

    public ApartmentEmptyView(Context context) {
        this(context, null);
    }

    public ApartmentEmptyView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ApartmentEmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ApartmentEmptyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.view_empty_apartment, this);
        ButterKnife.bind(this);
    }

    public void setApartmentRegister(ApartmentRegister apartmentRegister) {
        this.apartmentRegister = apartmentRegister;
    }

    @OnClick(R.id.report)
    public void registrationApartment() {
        if (apartmentRegister != null) {
            apartmentRegister.registrationApartment();
        }
    }

    public interface ApartmentRegister {
        void registrationApartment();
    }
}
