package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 6. 1..
 */
public enum LivingType {
    @SerializedName("apart")APARTMENT("apart"),
    @SerializedName("tempapart")TEMP_APARTMENT("tempapart"),
    @SerializedName("house")HOUSE("house");

    private String rawType;

    LivingType(String rawType) {
        this.rawType = rawType;
    }

    public String getRawType() {
        return rawType;
    }
}
