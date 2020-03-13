package com.zslide.models.response;

import com.zslide.data.model.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 15. 11. 26..
 */
public class CertificationResponse extends SimpleApiResponse {

    @SerializedName("user") private User user;

    public User getUser() {
        return user;
    }
}