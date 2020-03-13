package com.zslide.models.response;

import com.zslide.models.ApiData;
import com.zslide.data.model.User;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 5. 17..
 */
public class SocialLoginResult extends ApiData {

    @SerializedName("is_social_user") boolean isSocialUser;
    @SerializedName("user") User user;
}
