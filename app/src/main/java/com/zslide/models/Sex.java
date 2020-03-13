package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jdekim43 on 2016. 5. 26..
 */
public enum Sex {

    @SerializedName("M")MAN("man", "남자"),
    @SerializedName(value = "W", alternate = {"F"})WOMAN("woman", "여자"),
    NONE("", "none", "", "기타");

    private String simpleEnglish;
    private String english;
    private String simpleKorean;
    private String korean;

    Sex(String english, String korean) {
        this.simpleEnglish = english.substring(0, 1).toUpperCase();
        this.english = english;
        this.simpleKorean = korean.substring(0, 1).toUpperCase();
        this.korean = korean;
    }

    Sex(String simpleEnglish, String english, String simpleKorean, String korean) {
        this.simpleEnglish = simpleEnglish;
        this.english = english;
        this.simpleKorean = simpleKorean;
        this.korean = korean;
    }

    public String getSimpleEnglish() {
        return simpleEnglish;
    }

    public String getEnglish() {
        return english;
    }

    public String getSimpleKorean() {
        return simpleKorean;
    }

    public String getKorean() {
        return korean;
    }

    @Override
    public String toString() {
        return korean;
    }
}
