package com.zslide.data.model;

import com.zslide.models.Account;
import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDateTime;

import lombok.Getter;

/**
 * Created by chulwoo on 2017. 12. 29..
 */
public class Payments extends BaseModel {

    @Getter @SerializedName("calculation_completion") private boolean completePayments;
    @Getter @SerializedName("calculation_date") private LocalDateTime date;
    @Getter @SerializedName("calculation_status") private PaymentsState state;
    /**
     * 2017년 3월 이후 총 지급 금액
     */
    @Getter @SerializedName("total_payment") private int totalPayments;
    /**
     * 정산(입금)된 금액
     */
    @Getter @SerializedName("real_payment") private int recentPayments;
    /**
     * 지급 예정 금액
     */
    @Getter @SerializedName("saved_cash") private int expectedPayments;
    /**
     * 이월 금액
     */
    @Getter @SerializedName("carrying_cash") private int carryingCash;
    @Getter @SerializedName("carrying_reason") String carryingReason;
    @Getter @SerializedName("account") Account account;

    public boolean hasAccount() {
        return account != null && !account.isNull();
    }
}