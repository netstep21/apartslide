package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jdekim43 on 2016. 6. 14..
 */
public enum CalculationStatus {

    @SerializedName("0")BASIC(""), //정산 이전

    @SerializedName("1")ZUMMA_APART("관리비 차감"), // 업무 협약 아파트
    @SerializedName("2")APART("계좌입금"), // 개인 계좌
    @SerializedName("5")OTHER("계좌입금"), // 주택

    @SerializedName("3")KEEP("다음 달로 이월"),
    @SerializedName("4")MOVE("다음 달로 이월"),
    @SerializedName("6")WRONG_ACCOUNT("다음 달로 이월"),
    @SerializedName("7")WRONG_ADDRESS("다음 달로 이월"),//아파트 동, 호수가 잘못되었을 때
    @SerializedName("8")EXCEPTION("다음 달로 이월");//위 경우를 제외한 예외

    private String typeName;

    CalculationStatus(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeName() {
        return typeName;
    }
}
