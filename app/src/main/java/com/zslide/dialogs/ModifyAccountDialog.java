package com.zslide.dialogs;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.models.Account;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.widget.AccountInputView;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by jdekim43 on 2016. 5. 31..
 */
public class ModifyAccountDialog extends BaseAlertDialog {

    @BindView(R.id.account) AccountInputView accountView;

    private Action1<Account> modifyListener;

    private Button positiveButton;
    private Button negativeButton;

    public static ModifyAccountDialog newInstance() {
        return new ModifyAccountDialog();
    }

    public void setOnModifyListener(Action1<Account> listener) {
        this.modifyListener = listener;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.dialog_modify_account;
    }

    @Override
    protected void setupLayout(View view) {

    }

    @Override
    protected void setupDialog(AlertDialog.Builder builder) {
        builder.setPositiveButton(R.string.label_confirm, null);
        builder.setNegativeButton(R.string.label_cancel, null);
    }

    @Override
    protected void setupButton(Button positiveButton, Button negativeButton, Button neutralButton) {
        super.setupButton(positiveButton, negativeButton, neutralButton);
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        positiveButton.setOnClickListener(v -> {
            negativeButton.setEnabled(false);
            negativeButton.setEnabled(false);
            int bankId = accountView.getSelectedBank().getId();
            String accountNumber = accountView.getAccountNumber();
            String accountOwner = accountView.getAccountOwner();

            Context context = getContext();

            if (bankId < 0) {
                Toast.makeText(context, R.string.message_select_bank, Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(accountNumber)) {
                Toast.makeText(context, R.string.message_input_account_number, Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(accountOwner)) {
                Toast.makeText(context, R.string.message_input_account_owner, Toast.LENGTH_SHORT).show();
                return;
            }

            sendServer(bankId, accountNumber, accountOwner);
        });
    }

    private void sendServer(int bankId, String accountNumber, String accountOwner) {
        ZummaApi.user().registrationAccount(bankId, accountNumber, accountOwner)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(accountResult -> {
                    if (accountResult.isSuccess()) {
                        Family family = UserManager.getInstance().getFamilyValue();
                        family.setAccount(accountResult.getAccount());
                        UserManager.getInstance().updateFamily(family)
                                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                    if (modifyListener != null) {
                                        modifyListener.call(accountResult.getAccount());
                                    }
                                    dismiss();
                                });
                    } else {
                        String message = "계좌 변경을 실패했습니다.";
                        if (!TextUtils.isEmpty(accountResult.getMessage())) {
                            message = accountResult.getMessage();
                        }
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        enableButtons();
                    }
                }, this::onError);
    }

    private void onError(Throwable e) {
        ZummaApiErrorHandler.handleError(e);
        enableButtons();
    }

    private void enableButtons() {
        if (positiveButton != null) {
            positiveButton.setEnabled(true);
        }
        if (negativeButton != null) {
            negativeButton.setEnabled(true);
        }
    }
}
