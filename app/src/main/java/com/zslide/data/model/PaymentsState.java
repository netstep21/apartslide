package com.zslide.data.model;

import android.content.Context;
import android.support.annotation.StringRes;

import com.zslide.R;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 2017. 12. 29..
 */
public enum PaymentsState {

    @SerializedName("0")BASIC(0), //정산 이전

    @SerializedName("1")ZUMMA_APART(R.string.payments_state_deduct), // 업무 협약 아파트
    @SerializedName("2")APART(R.string.payments_state_deposit), // 개인 계좌
    @SerializedName("5")OTHER(R.string.payments_state_deposit), // 주택

    @SerializedName("3")KEEP(R.string.payments_state_keep),
    @SerializedName("4")MOVE(R.string.payments_state_keep),
    @SerializedName("6")WRONG_ACCOUNT(R.string.payments_state_keep),
    @SerializedName("7")WRONG_ADDRESS(R.string.payments_state_keep),//아파트 동, 호수가 잘못되었을 때
    @SerializedName("8")EXCEPTION(R.string.payments_state_keep);//위 경우를 제외한 예외

    @StringRes private int resId;

    PaymentsState(@StringRes int resId) {
        this.resId = resId;
    }

    public String getDisplayLabel(Context context) {
        if (resId == 0) {
            return "";
        } else {
            return context.getString(resId);
        }
    }
}
