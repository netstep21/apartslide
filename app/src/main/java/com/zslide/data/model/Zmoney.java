package com.zslide.data.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by chulwoo on 2017. 3. 8..
 */

public class Zmoney extends BaseModel {

    public static final Zmoney NULL = new Zmoney();

    @SerializedName(value = "today", alternate = "current_charged_cash") int total;
    @SerializedName(value = "today_purchase_cash", alternate = "purchase_cash") int zummaStore;
    @SerializedName(value = "today_calllog_cash", alternate = "calllog_cash") int call;
    @SerializedName(value = "today_market_cash", alternate = "market_cash") int zummaShopping;
    @SerializedName(value = "today_ad_cash", alternate = "ad_cash") int offerwall;
    @SerializedName(value = "today_recommend_cash", alternate = "recommend_cash") int invite;
    @SerializedName(value = "today_ocb_cash", alternate = "ocb_cash") int ocb;
    @SerializedName(value = "today_extra_cash", alternate = "extra_cash") int extra;

    @Getter private User user;

    public int getTotal() {
        return total;
    }

    public int getZummaStore() {
        return zummaStore;
    }

    public int getCall() {
        return call;
    }

    public int getZummaShopping() {
        return zummaShopping;
    }

    public int getSlide() {
        return total - (zummaStore + zummaShopping +
                call + invite + offerwall + ocb + extra);
    }

    public int getOfferwall() {
        return offerwall;
    }

    public int getInvite() {
        return invite;
    }

    public int getOcb() {
        return ocb;
    }

    public int getExtra() {
        return extra;
    }

    public int[] asArray() {
        return new int[]{extra, ocb, invite, offerwall, call, zummaStore, zummaShopping, getSlide()};
    }
}
