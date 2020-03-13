package com.zslide.data.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by chulwoo on 16. 5. 24..
 *
 */
public enum AuthType {
    @SerializedName("email")EMAIL("email"),
    @SerializedName(value = "phone", alternate = "zumma")PHONE("phone"),
    @SerializedName("kakao")KAKAO("kakao"),
    @SerializedName("naver")NAVER("naver");

    @Getter private String value;

    AuthType(String value) {
        this.value = value;
    }

    public boolean isSocialType() {
        return this.equals(AuthType.KAKAO) || this.equals(AuthType.NAVER);
    }
}
