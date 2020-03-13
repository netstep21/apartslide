package com.zslide.data.remote.response;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 8..
 */

public class ApiResponse {

    @Getter @SerializedName("success") private boolean success;
    @Getter @SerializedName(value = "message", alternate = {"detail"}) private String message;
}
