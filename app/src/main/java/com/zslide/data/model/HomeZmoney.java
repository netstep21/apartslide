package com.zslide.data.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 11..
 *
 * {@link com.mobitle.zmoney.fragments.HomeFragment}에서 사용하는 줌머니 정보
 */

public class HomeZmoney extends BaseModel {

    public static final HomeZmoney NULL = new HomeZmoney();

    @Getter @SerializedName("family_total_saved_cash") int totalZmoney;
    @Getter @SerializedName("family_saved_cash") int familyZmoney;
    @Getter @SerializedName("user_saved_cash") int userZmoney;
}
