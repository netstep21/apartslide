package com.zslide.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 15. 10. 14..
 */
public class SimpleApiResponse {

    @SerializedName("success") private boolean mSuccess;
    @SerializedName(value = "message", alternate = {"detail"}) private String mMessage;

    public static SimpleApiResponse mock() {
        SimpleApiResponse result = new SimpleApiResponse();
        result.mMessage = "성공";
        result.mSuccess = true;
        return result;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getMessage() {
        return mMessage;
    }
}
