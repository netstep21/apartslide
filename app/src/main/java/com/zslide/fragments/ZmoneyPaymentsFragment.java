package com.zslide.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.zslide.Navigator;
import com.zslide.R;
import com.zslide.activities.ZmoneyPaymentsHelpActivity;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.Payments;
import com.zslide.data.model.PaymentsState;
import com.zslide.widget.AccountInfoView;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by chulwoo on 2017. 9. 11..
 */

public class ZmoneyPaymentsFragment extends com.zslide.view.base.BaseFragment {

    @BindView(R.id.paymentsLabel) TextView paymentsLabelView;
    @BindView(R.id.payments) TextView paymentsView;
    @BindView(R.id.alert) TextView alertView;
    @BindView(R.id.message) TextView messageView;
    @BindView(R.id.carryingCash) TextView carryingCashView;
    @BindView(R.id.carryingReasonContainer) View carryingReasonContainer;
    @BindView(R.id.carryingReason) TextView carryingReasonView;
    @BindView(R.id.accountInfoContainer) View accountInfoContainer;
    @BindView(R.id.accountInfo) AccountInfoView accountInfoView;

    private DateTimeFormatter depositDateFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 25-26일");

    public static ZmoneyPaymentsFragment newInstance() {

        Bundle args = new Bundle();

        ZmoneyPaymentsFragment fragment = new ZmoneyPaymentsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        bind(UserManager.getInstance().getFamilyObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::bind, this::handleError));
    }

    public void bind(@NonNull Family family) {
        accountInfoView.setFamily(family);
        Payments payments = family.getPayments();
        if (payments == null) {
            showEmptyCalculation(family);
        } else {
            showPayments(family, payments);
        }
    }

    /**
     * 정산 정보가 전혀 없는 상태일 때 호출
     */
    private void showEmptyCalculation(Family family) {
        paymentsView.setText(getString(R.string.format_price, 0));
        carryingCashView.setText(getString(R.string.format_price, 0));

        hideAlert();
        if (!family.hasAccount()) {
            showMessage("아래에서 계좌를 등록해주세요.");
        } else {
            showMessage("아직 정산 정보가 없습니다.");
        }
    }

    private void showPayments(Family family, Payments payments) {
        paymentsView.setText(getString(R.string.format_price, payments.getRecentPayments()));
        carryingCashView.setText(getString(R.string.format_point, payments.getCarryingCash()));

        if (TextUtils.isEmpty(payments.getCarryingReason())) {
            carryingReasonContainer.setVisibility(View.GONE);
        } else {
            carryingReasonContainer.setVisibility(View.VISIBLE);
            carryingReasonView.setText(payments.getCarryingReason());

            if (payments.getCarryingCash() > 0) {
                if (payments.getRecentPayments() == 0) {
                    showMessage("모두 이월 됐습니다.\n아래 이월 사유를 확인해주세요.");
                } else {
                    showMessage("일부 금액이 이월 됐습니다.\n아래 이월 사유를 확인해주세요.");
                }
            }
        }

        if (payments.isCompletePayments()) {
            showRealCalculation(payments);
        } else {
            showExpectedCalculation(family);
        }
    }


    private void showExpectedCalculation(Family family) {
        Payments payments = family.getPayments();
        if (payments != null) {
            switch (family.getHouseType()) {
                case ZUMMA_APART:
                    paymentsLabelView.setText(R.string.expected_deduction_price);
                    showMessage(getString(R.string.deduction_message, getDeductionDate(payments)));
                    hideAccount();
                    break;
                default:
                    paymentsLabelView.setText(R.string.expected_payments_price);
                    showMessage(getString(R.string.deposit_date, getDepositDate(payments)));
                    showAccount();
                    if (family.hasAccount()) {
                        hideAlert();
                    } else {
                        showAlert("계좌 정보가 없습니다.");
                        showMessage(messageView.getText() + "\n" + "계좌 정보가 없을 경우, 이월될 수 있습니다.");
                    }
                    break;
            }
        }
    }


    private void showRealCalculation(Payments payments) {
        PaymentsState state = payments.getState();

        if (PaymentsState.ZUMMA_APART.equals(state)) {
            paymentsLabelView.setText(R.string.deduction_price);
            showMessage(getString(R.string.deduction_message, getDeductionDate(payments)));
            hideAccount();
        } else {
            paymentsLabelView.setText(R.string.payments_price);
            showMessage(getString(R.string.deposit_date, getDepositDate(payments)));
            showAccount();
        }

        if (PaymentsState.WRONG_ACCOUNT.equals(state)) {
            showAlert("계좌 정보가 부정확합니다.");
        } else {
            hideAlert();
        }
    }

    private void showMessage(String message) {
        messageView.setVisibility(View.VISIBLE);
        messageView.setText(message);
    }

    private void showAlert(String alert) {
        alertView.setVisibility(View.VISIBLE);
        alertView.setText(alert);
    }

    private String getDeductionDate(Payments payments) {
        LocalDateTime date = payments.getDate();
        if (date == null) {
            date = LocalDateTime.now();
        }
        return String.valueOf(date.getMonthValue());
    }

    private String getDepositDate(Payments payments) {
        LocalDateTime date = payments.getDate();
        if (date == null) {
            date = LocalDateTime.now();
        }
        return date.format(depositDateFormatter);
    }

    private void hideAlert() {
        alertView.setVisibility(View.GONE);
    }

    private void showAccount() {
        accountInfoContainer.setVisibility(View.VISIBLE);
    }

    private void hideAccount() {
        accountInfoContainer.setVisibility(View.GONE);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_zmoney_payments;
    }

    @OnClick(R.id.carryingCashGuide)
    public void showCarryingCashGuide() {
        Navigator.startZmoneyPaymentsHelpActivity(getActivity(), ZmoneyPaymentsHelpActivity.PAGE_PAYMENTS);
    }
}
