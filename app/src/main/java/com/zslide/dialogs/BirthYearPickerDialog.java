package com.zslide.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.NumberPicker;

import com.zslide.IntentConstants;
import com.zslide.R;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by chulwoo on 2016. 11. 15..
 */

public class BirthYearPickerDialog extends DialogFragment {

    @BindView(R.id.birthYear) NumberPicker birthYearPicker;
    private OnYearSelectedListener listener;

    public static BirthYearPickerDialog newInstance() {
        return newInstance(-1);
    }

    public static BirthYearPickerDialog newInstance(int birthYear) {

        Bundle args = new Bundle();
        args.putInt(IntentConstants.EXTRA_YEAR, birthYear);

        BirthYearPickerDialog fragment = new BirthYearPickerDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_birth_year);
        ButterKnife.bind(this, dialog);

        Calendar calendar = Calendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);

        birthYearPicker.setMinValue(currentYear - 100);
        birthYearPicker.setMaxValue(currentYear);
        birthYearPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        int birthYear = getArguments().getInt(IntentConstants.EXTRA_YEAR);
        if (birthYear == -1) {
            birthYear = currentYear - 35;
        }
        birthYearPicker.setValue(birthYear);
        return dialog;
    }

    public BirthYearPickerDialog setOnConfirmListener(OnYearSelectedListener listener) {
        this.listener = listener;
        return this;
    }

    @OnClick(R.id.confirm)
    public void confirm() {
        if (listener != null) {
            listener.onYearSelected(birthYearPicker.getValue());
        }
        dismiss();
    }

    @OnClick(R.id.cancel)
    @Override
    public void dismiss() {
        super.dismiss();
    }

    public interface OnYearSelectedListener {
        void onYearSelected(int year);
    }
}
