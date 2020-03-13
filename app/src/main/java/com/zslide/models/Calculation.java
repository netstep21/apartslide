package com.zslide.models;

import com.zslide.utils.TimeUtil;
import com.google.gson.annotations.SerializedName;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jdekim43 on 2016. 6. 14..
 */
public class Calculation extends ZummaApiData {

    @SerializedName("calculation_completion") private boolean isCalculated;
    @SerializedName("calculation_date") private Date date;
    @SerializedName("calculation_status") private CalculationStatus status;
    /**
     * 2017년 3월 이후 총 지급 금액
     */
    @SerializedName("total_payment") private int totalPayments;
    /**
     * 정산(입금)된 금액
     */
    @SerializedName("real_payment") private int recentPayments;
    /**
     * 지급 예정 금액
     */
    @SerializedName("saved_cash") private int expectedPayments;
    /**
     * 이월 금액
     */
    @SerializedName("carrying_cash") private int carryingCash;
    @SerializedName("carrying_reason") String carryingReason;
    @SerializedName("account") Account account;

    public boolean isNull() {
        return date == null || status == null;
    }

    public boolean isCalculated() {
        return isCalculated;
    }

    public Date getDate() {
        return date;
    }

    public String getDepositDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return TimeUtil.format("yyyy년 MM월 25-26일", calendar.getTime());
    }

    public CalculationStatus getStatus() {
        return status;
    }

    public int getTotalPayments() {
        return totalPayments;
    }

    public int getRecentPayments() {
        return recentPayments;
    }

    public int getExpectedPayments() {
        return expectedPayments;
    }

    public int getCarryingCash() {
        return carryingCash;
    }

    public String getCarryingReason() {
        return carryingReason;
    }

    public Account getAccount() {
        return account;
    }

    public boolean hasAccount() {
        return account != null && !account.isNull();
    }
}