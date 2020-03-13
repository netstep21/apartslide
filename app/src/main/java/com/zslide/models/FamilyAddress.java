package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 1. 14..
 * <p>
 * 가족 정보
 */
public class FamilyAddress extends ZummaApiData {

    @SerializedName("dong") String dong;
    @SerializedName("ho") int ho;
    @SerializedName("apart") int apartId;
    @SerializedName("id") private int id;
    @SerializedName("specific_address") private String specificAddress;

    public int getId() {
        return id;
    }

    public String getSpecificAddress() {
        return specificAddress;
    }

    public String getDong() {
        return dong;
    }

    public int getHo() {
        return ho;
    }

    public int getApartId() {
        return apartId;
    }
}
