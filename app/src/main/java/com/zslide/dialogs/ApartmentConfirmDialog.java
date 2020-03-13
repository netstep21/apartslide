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
public class ApartmentConfirmDialog extends DialogFragment {

    private static final String ARG_ADDRESS = "address";
    private static final String ARG_APARTMENT_NAME = "apartment_name";
    private static final String ARG_DONG = "dong";
    private static final String ARG_HO = "ho";

    @BindView(R.id.name) TextView nameView;
    @BindView(R.id.address) TextView addressView;

    private Action0 confirmListener;
    private Address address;
    private String apartmentName;
    private String dong;
    private int ho;

    public static ApartmentConfirmDialog newInstance(Address address, String name, String dong, int ho) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_ADDRESS, address);
        args.putString(ARG_APARTMENT_NAME, name);
        args.putString(ARG_DONG, dong);
        args.putInt(ARG_HO, ho);

        ApartmentConfirmDialog instance = new ApartmentConfirmDialog();
        instance.setArguments(args);

        return instance;
    }

    public ApartmentConfirmDialog setOnConfirmListener(Action0 listener) {
        this.confirmListener = listener;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        address = args.getParcelable(ARG_ADDRESS);
        apartmentName = args.getString(ARG_APARTMENT_NAME);
        dong = args.getString(ARG_DONG);
        ho = args.getInt(ARG_HO);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_apartment_confirm, null);
        ButterKnife.bind(this, view);
        nameView.setText(String.format("%s %s동 %s호", apartmentName, dong, ho));
        addressView.setText(address.toString());
        return new AlertDialog.Builder(getContext())
                .setView(view)
                .create();
    }

    @OnClick(R.id.confirm)
    public void onConfirm() {
        dismiss();
        if (confirmListener != null) {
            confirmListener.call();
        }
    }

    @OnClick(R.id.cancel)
    public void onCancel() {
        dismiss();
    }
}
