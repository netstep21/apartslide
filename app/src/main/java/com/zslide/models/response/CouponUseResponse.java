package com.zslide.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 2016. 12. 29..
 */

public class CouponUseResponse extends SimpleApiResponse {

    @SerializedName("point") private int zmoney;

    public int getZmoney() {
        return zmoney;
    }
}
