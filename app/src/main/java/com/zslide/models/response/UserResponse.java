package com.zslide.models.response;

import com.zslide.data.model.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 15. 10. 14..
 */
public class UserResponse extends SimpleApiResponse {

    @SerializedName("user") private User mUser;

    public User getUser() {
        return mUser;
    }
}
