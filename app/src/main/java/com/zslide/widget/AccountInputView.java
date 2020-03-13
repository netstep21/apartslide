package com.zslide.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;

import com.zslide.R;
import com.zslide.models.Bank;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.NumberUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 1. 13..
 */
public class AccountInputView extends TableLayout {

    @BindView(R.id.banks) Spinner selectBankView;
    @BindView(R.id.accountNumber) EditText accountNumberView;
    @BindView(R.id.accountOwner) EditText accountOwnerView;

    private ArrayAdapter<Bank> bankAdapter;

    private OnValidatedListener validatedListener;
    private OnFinishLoadBankListListener finishLoadBankListListener;

    public AccountInputView(Context context) {
        this(context, null);
    }

    public AccountInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_account_input, this);
        ButterKnife.bind(this);
        bankAdapter = new ArrayAdapter<>(context, R.layout.item_simple_string);
        selectBankView.setAdapter(bankAdapter);
        ZummaApi.user().banks()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> setBankList(result.getBanks()), ZummaApiErrorHandler::handleError);
    }

    public void setBankList(List<Bank> bankList) {
        bankAdapter.clear();
        bankAdapter.addAll(bankList);
        if (finishLoadBankListListener != null) {
            finishLoadBankListListener.onLoaded(bankList);
        }
    }

    public void setBank(Bank bank) {
        selectBankView.setSelection(bankAdapter.getPosition(bank));
    }

    public void setAccountOwnerView(String accountOwner) {
        accountOwnerView.setText(accountOwner);
    }

    public Bank getSelectedBank() {
        return (Bank) selectBankView.getSelectedItem();
    }

    public String getAccountNumber() {
        CharSequence _accountNumber = accountNumberView.getText().toString();
        return NumberUtil.isNumber(_accountNumber) ? _accountNumber.toString() : "";
    }

    public void setAccountNumber(String accountNumber) {
        accountNumberView.setText(accountNumber);
    }

    public String getAccountOwner() {
        return accountOwnerView.getText().toString();
    }

    public void setOnValidatedListener(OnValidatedListener listener) {
        this.validatedListener = listener;
    }

    public void setOnFinishLoadBankListListener(OnFinishLoadBankListListener listener) {
        this.finishLoadBankListListener = listener;
    }

    @OnTextChanged({R.id.accountNumber, R.id.accountOwner})
    void validate() {
        if (validatedListener != null) {
            validatedListener.onValidated(accountNumberView.getText().length() > 0
                    && accountOwnerView.getText().length() > 0);
        }
    }

    public interface OnValidatedListener {
        void onValidated(boolean isValidate);
    }

    public interface OnFinishLoadBankListListener {
        void onLoaded(List<Bank> bankList);
    }
}
