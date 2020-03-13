package com.zslide.data.remote.response;

import com.zslide.data.model.User;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 8..
 */

public class AuthenticationResponse extends ApiResponse {

    @Getter @SerializedName("user") User user;
    @Getter @SerializedName("api_key") String apiKey;
}
