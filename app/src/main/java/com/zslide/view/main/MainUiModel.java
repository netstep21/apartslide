package com.zslide.view.main;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.zslide.R;
import com.zslide.data.model.Family;
import com.zslide.data.model.Payments;
import com.zslide.data.model.PaymentsState;
import com.zslide.data.model.User;
import com.zslide.models.TempApartment;

import io.reactivex.functions.Action;

/**
 * Created by chulwoo on 2017. 12. 29..
 */

public class MainUiModel {

    private final User user;
    private final Family family;
    private final MainNavigator navigator;

    @StringRes private int recentPaymentsLabel = 0;
    @StringRes private int recentPaymentsAlert = 0;
    private boolean recentPaymentsLabelVisibility = false;
    private boolean recentPaymentsAlertVisibility = false;
    private Action onRecentPaymentsClickAction;

    public MainUiModel(@NonNull User user, @NonNull Family family, MainNavigator navigator) {
        this.user = user;
        this.family = family;
        this.navigator = navigator;

        setupUiStatus();
    }

    private void setupUiStatus() {
        if (!family.isNull()) {
            Payments payments = family.getPayments();
            if (payments != null && !payments.isNull()) {
                if (payments.isCompletePayments()) {
                    recentPaymentsLabel = R.string.payments_price;
                } else {
                    recentPaymentsLabel = R.string.label_expected_zmoney;
                }

                PaymentsState type = payments.getState();
                if (PaymentsState.WRONG_ADDRESS.equals(type)) {
                    recentPaymentsAlert = R.string.message_error_expected_payments5;
                    onRecentPaymentsClickAction = this::startTempApartmentComplete;
                }
            }

            if (family.hasTempApartment()) {
                switch (family.getTempApartment().getStatus()) {
                    case TempApartment.STATUS_WAIT:
                        recentPaymentsAlert = R.string.message_error_expected_payments3;
                        onRecentPaymentsClickAction = navigator::showTempApartmentWaitDialog;
                        break;
                    case TempApartment.STATUS_SUCCESS:
                        recentPaymentsAlert = R.string.message_error_expected_payments4;
                        onRecentPaymentsClickAction = this::startTempApartmentComplete;
                        break;
                    case TempApartment.STATUS_FAILURE:
                        recentPaymentsAlert = R.string.message_error_expected_payments4;
                        onRecentPaymentsClickAction = this::startTempApartmentComplete;
                        break;
                }
            } else {
                if (!family.getApartment().isJoined() && !family.hasAccount()) {
                    recentPaymentsAlert = R.string.message_error_expected_payments2;
                    onRecentPaymentsClickAction = navigator::openZmoneyPaymentsPage;
                } else {
                    recentPaymentsAlert = R.string.message_error_expected_payments2;
                    onRecentPaymentsClickAction = navigator::openZmoneyPaymentsPage;
                }
            }
        } else {
            recentPaymentsAlert = R.string.message_error_expected_payments1;
            onRecentPaymentsClickAction = navigator::openFamilyRegistrationPage;
        }

        recentPaymentsLabelVisibility = recentPaymentsLabel != 0;
        recentPaymentsAlertVisibility = recentPaymentsAlert != 0;

    }

    private void startTempApartmentComplete() {
        if (family.isFamilyLeader(user)) {
            navigator.openTempApartmentCompletePage();
        } else {
            navigator.showBlurredNameLeaderDialog(family);
        }
    }

    public boolean isRecentPaymentsLabelVisibility() {
        return recentPaymentsLabelVisibility;
    }

    public boolean isRecentPaymentsAlertVisibility() {
        return recentPaymentsAlertVisibility;
    }

    public int getRecentPaymentsAlert() {
        return recentPaymentsAlert;
    }

    public int getRecentPaymentsLabel() {
        return recentPaymentsLabel;
    }

    public Action getOnRecentPaymentsClickAction() {
        return onRecentPaymentsClickAction;
    }

    public Payments getPayments() {
        return family.getPayments();
    }
}
