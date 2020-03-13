package com.zslide.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chulwoo on 16. 8. 3..
 */
public class LevelCouponLog extends ZummaApiData {

    @SerializedName("id") int id;
    @SerializedName("pub_date") Date createdAt;
    @SerializedName("description") String description;
    @SerializedName("zmoney") int zmoney;

    public int getId() {
        return id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public String getDescription() {
        return description;
    }

    public int getZmoney() {
        return zmoney;
    }
}
