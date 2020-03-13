package com.zslide.data.remote.response;

import com.zslide.data.model.AuthType;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 8..
 */

public class SignUpCheckResponse extends ApiResponse {

    @Getter @SerializedName("type") List<AuthType> types;
}
