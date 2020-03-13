package com.zslide.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.model.Address;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action0;

/**
 * Created by chulwoo on 16. 4. 7..
 */
public class TempApartmentConfirmDialog extends DialogFragment {

    private static final String ARG_ADDRESS = "address";
    private static final String ARG_APARTMENT_NAME = "apartment_name";

    @BindView(R.id.name) TextView nameView;
    @BindView(R.id.address) TextView addressView;

    private Action0 listener;
    private Address address;
    private String apartmentName;

    public static TempApartmentConfirmDialog newInstance(Address address, String name) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_ADDRESS, address);
        args.putString(ARG_APARTMENT_NAME, name);

        TempApartmentConfirmDialog instance = new TempApartmentConfirmDialog();
        instance.setArguments(args);

        return instance;
    }

    public void setOnConfirmListener(Action0 listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        address = args.getParcelable(ARG_ADDRESS);
        apartmentName = args.getString(ARG_APARTMENT_NAME);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_temp_apartment_confirm, null);
        ButterKnife.bind(this, view);
        nameView.setText(apartmentName);
        addressView.setText(address.toString());

        return new AlertDialog.Builder(getContext())
                .setView(view)
                .create();
    }

    @OnClick(R.id.confirm)
    public void confirm() {
        dismiss();
        if (listener != null) {
            listener.call();
        }
    }

    @OnClick(R.id.cancel)
    public void onCancel() {
        dismiss();
    }
}
