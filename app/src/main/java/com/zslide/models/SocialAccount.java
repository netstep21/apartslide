package com.zslide.models;

import com.zslide.data.model.AuthType;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 6. 10..
 */
public class SocialAccount extends ZummaApiData {

    @SerializedName("extra_data") Object extra;
    @SerializedName("uid") String uid;
    @SerializedName("provider") String provider;

    public Object getExtra() {
        return extra;
    }

    public String getUid() {
        return uid;
    }

    public String getProvider() {
        return provider;
    }

    public AuthType getAuthType() {
        try {
            return AuthType.valueOf(provider.toUpperCase());
        } catch (Exception e) {
            return AuthType.EMAIL;
        }
    }
}
