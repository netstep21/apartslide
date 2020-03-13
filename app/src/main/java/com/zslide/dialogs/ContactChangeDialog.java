package com.zslide.dialogs;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.zslide.R;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

/**
 * Created by chulwoo on 16. 5. 29..
 */
public class ContactChangeDialog extends BaseAlertDialog {

    @BindColor(R.color.accentColor) int positiveColor;

    @BindView(R.id.editText) EditText editText;

    private Action1<String> onContactInputAction;

    public static ContactChangeDialog newInstance(String contact) {

        Bundle args = new Bundle();
        args.putString("contact", contact);

        ContactChangeDialog fragment = new ContactChangeDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_phone_number_change;
    }

    @Override
    protected void setupLayout(View view) {
        String phoneNumber = getArguments().getString("contact");
        editText.setHint("연락처를 입력하세요.");
        editText.setText(phoneNumber);
    }

    @Override
    protected View onCreateTitleView() {
        View titleContainer = LayoutInflater.from(getActivity())
                .inflate(R.layout.view_dialog_title, null, false);
        TextView titleView = ButterKnife.findById(titleContainer, R.id.title);
        titleView.setText("연락처");
        return titleContainer;
    }

    @Override
    protected void setupDialog(AlertDialog.Builder builder) {
        builder.setPositiveButton("변경", (dialog, which) -> {
            if (onContactInputAction != null) {
                onContactInputAction.call(editText.getText().toString());
            }
        }).setNegativeButton("취소", null);
    }

    @Override
    protected void setupButton(Button positiveButton, Button negativeButton, Button neutralButton) {
        super.setupButton(positiveButton, negativeButton, neutralButton);
        positiveButton.setTextColor(positiveColor);
    }

    public ContactChangeDialog setOnContactInputAction(Action1<String> onContactInputAction) {
        this.onContactInputAction = onContactInputAction;
        return this;
    }
}
