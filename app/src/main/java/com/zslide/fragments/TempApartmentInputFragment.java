package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.models.TempApartment;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by chulwoo on 16. 4. 6..
 */
public class TempApartmentInputFragment extends InputFragment {

    @BindView(R.id.tempApartmentName) TextView tempApartmentNameView;
    private TempApartmentClearListener tempApartmentClearListener;
    private TempApartment tempApartment;

    public static TempApartmentInputFragment newInstance(TempApartment tempApartment) {
        Bundle args = new Bundle();
        args.putParcelable(IntentConstants.EXTRA_TEMP_APARTMENT, tempApartment);

        TempApartmentInputFragment instance = new TempApartmentInputFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_temp_apartment_input;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            tempApartment = getArguments().getParcelable(IntentConstants.EXTRA_TEMP_APARTMENT);
        }
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        bind(tempApartment);
    }

    @OnTextChanged(value = R.id.tempApartmentName)
    public void onTempApartmentChanged(CharSequence tempApartmentName) {
        onInputStateChanged();
    }

    @Override
    public boolean isCompleted() {
        return tempApartment != null;
    }

    public void setTempApartmentClearListener(TempApartmentClearListener listener) {
        tempApartmentClearListener = listener;
    }

    @OnClick(R.id.tempApartmentContainer)
    public void clear() {
        tempApartment = null;
        if (tempApartmentClearListener != null) {
            tempApartmentClearListener.onTempApartmentClear();
        }
    }

    private void bind(@NonNull TempApartment tempApartment) {
        tempApartmentNameView.setText(tempApartment.getName());
    }

    public TempApartment getTempApartment() {
        return tempApartment;
    }

    public void setTempApartment(@NonNull TempApartment tempApartment) {
        this.tempApartment = tempApartment;
        bind(tempApartment);
    }

    public interface TempApartmentClearListener {
        void onTempApartmentClear();
    }
}
