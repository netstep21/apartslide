package com.zslide.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.data.model.Address;
import com.zslide.models.TempApartment;
import com.zslide.utils.DisplayUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chulwoo on 16. 4. 8..
 */
public class TempApartmentSelectDialog extends DialogFragment {

    private static final String ARG_ADDRESS = "address";
    private static final String ARG_TEMP_APARTMENTS = "temp_apartments";
    //@BindView(R.id.tempApartments) BaseRecyclerView tempApartmentsView;
    @BindView(R.id.registrationContainer) ViewGroup registrationContainer;
    @BindView(R.id.apartmentName) EditText apartmentNameView;
    private Address address;
    private ArrayList<TempApartment> tempApartments;
    private OnTempApartmentSelectListener listener;

    public static TempApartmentSelectDialog newInstance(Address address, ArrayList<TempApartment> tempApartments) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_ADDRESS, address);
        args.putParcelableArrayList(ARG_TEMP_APARTMENTS, tempApartments);
        TempApartmentSelectDialog instance = new TempApartmentSelectDialog();
        instance.setArguments(args);
        return instance;
    }

    public void setOnTempApartmentSelectListener(OnTempApartmentSelectListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        address = args.getParcelable(ARG_ADDRESS);
        tempApartments = args.getParcelableArrayList(ARG_TEMP_APARTMENTS);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_temp_apartment_select, null);
        ButterKnife.bind(this, view);
        apartmentNameView.postDelayed(() -> {
            apartmentNameView.requestFocus();
            DisplayUtil.showKeyboard(getContext(), apartmentNameView);
        }, 100);

        return view;
    }

    @OnClick(R.id.close)
    @Override
    public void dismiss() {
        super.dismiss();
    }

    @OnClick(R.id.registrationApply)
    public void registrationApply() {
        String name = apartmentNameView.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getContext(), R.string.message_input_apartment, Toast.LENGTH_SHORT).show();
        } else {
            listener.onInputTempApartmentName(name);
        }
    }

    public interface OnTempApartmentSelectListener {
        void onTempApartmentSelected(TempApartment tempApartment);
        void onInputTempApartmentName(String name);
    }
}
