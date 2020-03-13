package com.zslide.models;

/**
 * Created by chulwoo on 2017. 12. 18..
 */

public class InviteInfo {

    String message;
    String reward;

    public InviteInfo(String message, String reward) {
        this.message = message;
        this.reward = reward;
    }

    public String getMessage() {
        return message;
    }

    public String getReward() {
        return reward;
    }
}
