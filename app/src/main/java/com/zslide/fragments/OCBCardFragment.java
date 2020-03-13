package com.zslide.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zslide.IntentConstants;
import com.zslide.R;
import com.zslide.models.OCB;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.widget.RequestButton;

import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by chulwoo on 16. 6. 27..
 */
public class OCBCardFragment extends OCBUseFragment {

    @BindViews({R.id.cardNumber1, R.id.cardNumber2, R.id.cardNumber3, R.id.cardNumber4})
    List<EditText> cardNumberViews;
    @BindView(R.id.password) EditText passwordView;
    @BindView(R.id.lookupOCB) RequestButton lookupOCBButton;

    private String type;

    public static OCBCardFragment newInstance(String type) {
        Bundle args = new Bundle();
        args.putString(IntentConstants.EXTRA_TYPE, type);

        OCBCardFragment fragment = new OCBCardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        super.setupLayout(view, savedInstanceState);
        type = getArguments().getString(IntentConstants.EXTRA_TYPE);
        setType(type);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_ocb_card;
    }

    @Override
    protected String getAuthId() {
        return getCardNumber();
    }

    @Override
    protected String getAuthPassword() {
        return getPassword();
    }

    @OnClick(R.id.lookupOCB)
    public void lookup() {
        lookupOCBButton.action(ZummaApi.ocb().auth(getCardNumber(), getPassword(), type),
                ocb -> {
                    if (ocb.isSuccess()) {
                        onSuccessLookup(ocb);
                    } else {
                        Toast.makeText(getContext(), ocb.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, ZummaApiErrorHandler::handleError);
    }

    protected void onSuccessLookup(OCB ocb) {
        lookupOCBButton.setVisibility(View.GONE);
        bind(ocb);
        for (TextView cardNumberView : cardNumberViews) {
            cardNumberView.setEnabled(false);
        }
        passwordView.setEnabled(false);
    }

    @OnTextChanged({R.id.cardNumber1, R.id.cardNumber2, R.id.cardNumber3, R.id.cardNumber4, R.id.password})
    protected void onInputChanged() {
        boolean isCompleted = getCardNumber().length() >= 15 && getPassword().length() >= 2;
        lookupOCBButton.setEnabled(isCompleted);
    }

    public String getCardNumber() {
        StringBuilder cardNumberBuilder = new StringBuilder();
        for (TextView cardNumberView : cardNumberViews) {
            cardNumberBuilder.append(cardNumberView.getText().toString());
        }

        return cardNumberBuilder.toString();
    }

    public String getPassword() {
        return passwordView.getText().toString();
    }
}