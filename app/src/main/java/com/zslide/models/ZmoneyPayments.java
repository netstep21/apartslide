package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 2017. 3. 8..
 */

public class ZmoneyPayments {

    // TODO: status

    @SerializedName("house_limit") int houseLimit;
    @SerializedName("apart_limit") int apartLimit;

    @SerializedName("calculation_status") int status;

    @SerializedName("total") int totalZmoney;
    @SerializedName("expected") int expectedZmoney;
    @SerializedName("carrying") int carriedZmoney;

    @SerializedName("latest_payment_type") int latestPaymentsType;
    @SerializedName("latest_month") int latestPaymentsMonth;
    @SerializedName("latest") int latestPaymentsZmoney;

    public int getHouseLimit() {
        return houseLimit;
    }

    public int getApartLimit() {
        return apartLimit;
    }

    public int getStatus() {
        return status;
    }

    public int getTotalZmoney() {
        return totalZmoney;
    }

    public int getExpectedZmoney() {
        return expectedZmoney;
    }

    public int getCarriedZmoney() {
        return carriedZmoney;
    }

    public int getLatestPaymentsType() {
        return latestPaymentsType;
    }

    public int getLatestPaymentsMonth() {
        return latestPaymentsMonth;
    }

    public int getLatestPaymentsZmoney() {
        return latestPaymentsZmoney;
    }
}
