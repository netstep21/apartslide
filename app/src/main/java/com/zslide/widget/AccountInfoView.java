package com.zslide.widget;

import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.Payments;
import com.zslide.data.model.PaymentsState;
import com.zslide.data.model.User;
import com.zslide.dialogs.ModifyAccountDialog;
import com.zslide.models.Account;
import com.zslide.utils.ZLog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by jdekim43 on 16. 05. 30..
 * <p>
 * Updated by chulwoo on 17. 04. 13..
 */
public class AccountInfoView extends LinearLayout {

    @BindView(R.id.bankAccount) TextView bankAccountView;
    @BindView(R.id.bankAccountAlertContainer) View bankAccountAlertContainer;
    @BindView(R.id.bankAccountAlert) TextView bankAccountAlertView;
    @BindView(R.id.wrongAccountTitle) TextView wrongAccountTitleView;
    @BindView(R.id.wrongAccountContent) TextView wrongAccountContentView;
    @BindView(R.id.editBankAccount) TextView editBankAccountButton;
    @BindView(R.id.registrationBankAccount) View registrationAccountButton;

    private Family family;
    private User me;

    public AccountInfoView(Context context) {
        this(context, null);
    }

    public AccountInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init();
        }
    }

    private void init() {
        inflate(getContext(), R.layout.view_account_info, this);
        ButterKnife.bind(this);
    }

    public void setFamily(Family family) {
        this.family = family;
        this.me = UserManager.getInstance().getUserValue();
        if (me.isNull()) {
            return;
        }

        if (family.isNull()) {
            setVisibility(View.GONE);
        } else {
            setVisibility(View.VISIBLE);
            setAccount(family.getAccount());
        }
    }

    public void setAccount(Account account) {
        Context context = getContext();

        if (account == null || account.isNull()) {
            editBankAccountButton.setVisibility(View.GONE);

            if (family.isFamilyLeader(me)) {
                // 계좌 번호(안내) + 등록 버튼
                bankAccountView.setText(context.getString(R.string.message_bank_account_alert1));
                bankAccountAlertContainer.setVisibility(View.GONE);
                registrationAccountButton.setVisibility(View.VISIBLE);
            } else {
                // 계좌번호(+ 알림)
                User leader = family.getLeader();
                bankAccountView.setText(context.getString(R.string.message_bank_account_alert2));
                bankAccountAlertContainer.setVisibility(View.VISIBLE);
                wrongAccountTitleView.setVisibility(View.GONE);
                wrongAccountContentView.setVisibility(View.GONE);
                bankAccountAlertView.setText(context.getString(R.string.message_bank_account_alert3,
                        leader.getBlurredName(context), leader.getBlurredPhoneNumber(true)));
                registrationAccountButton.setVisibility(View.GONE);
            }
        } else {
            registrationAccountButton.setVisibility(View.GONE);
            bankAccountView.setText(context.getString(R.string.format_bank_account,
                    account.getBank(), account.getBlurredAccount(), account.getBlurredOwner()));

            Payments payments = UserManager.getInstance().getFamilyValue().getPayments();
            if (PaymentsState.WRONG_ACCOUNT.equals(payments.getState())) {
                bankAccountAlertContainer.setVisibility(View.VISIBLE);
                wrongAccountTitleView.setVisibility(View.VISIBLE);
                wrongAccountContentView.setVisibility(View.VISIBLE);
            } else {
                bankAccountAlertContainer.setVisibility(View.GONE);
                wrongAccountTitleView.setVisibility(View.GONE);
                wrongAccountContentView.setVisibility(View.GONE);
            }

            if (family.isFamilyLeader(me)) {
                // 계좌번호 + 수정 버튼
                editBankAccountButton.setVisibility(View.VISIBLE);
                bankAccountAlertContainer.setVisibility(View.GONE);
            } else {
                // 계좌번호 + 수정 알림
                editBankAccountButton.setVisibility(View.GONE);
                User leader = family.getLeader();
                bankAccountAlertContainer.setVisibility(View.VISIBLE);
                bankAccountAlertView.setText(context.getString(R.string.message_bank_account_alert3,
                        leader.getBlurredName(context), leader.getBlurredPhoneNumber(true)));
            }


            if (family.getApartment() != null) {
                if (family.getApartment().isJoined()) {
                    setVisibility(View.GONE);
                }
            }
        }
    }

    @OnClick({R.id.registrationBankAccount, R.id.editBankAccount})
    public void editAccount() {
        if (getActivity() == null) {
            ZLog.e(this, "getActivity 가 null 이어서 다이얼로그를 띄울 수 없음");
            return;
        }

        ModifyAccountDialog dialog = ModifyAccountDialog.newInstance();
        dialog.setOnModifyListener(this::setAccount);
        dialog.show(getActivity().getSupportFragmentManager(), "dialog.modify_account");
    }

    private AppCompatActivity getActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof AppCompatActivity) {
                return (AppCompatActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
