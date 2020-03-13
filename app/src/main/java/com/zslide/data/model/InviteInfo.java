package com.zslide.data.model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 11..
 */

public class InviteInfo extends BaseModel {

    @Getter @SerializedName("recommend_count") int count;
    @Getter @SerializedName("recommend_code") String inviteCode;
    @Getter @SerializedName("message") String message;
    @Getter @SerializedName("invite_reward") int reward;
}
