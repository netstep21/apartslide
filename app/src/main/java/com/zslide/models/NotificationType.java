package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jdekim43 on 2016. 3. 16..
 */
public enum NotificationType {
    @SerializedName("0")SAVE(0),
    @SerializedName("1")NOTICE(1),
    @SerializedName("2")STORE_NEWS(2),
    @SerializedName("3")EVENT(3),
    @SerializedName("4")LINK(4);

    private int value;

    NotificationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
