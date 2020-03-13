package com.zslide.models.response;

import com.zslide.data.model.AuthType;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by chulwoo on 15. 11. 26..
 */
public class AuthCheckResponse extends SimpleApiResponse {

    @SerializedName("type") private ArrayList<AuthType> authTypes;

    public boolean isAuthenticate(AuthType authType) {
        if (!isSuccess()) {
            return false;
        }

        for (AuthType type : authTypes) {
            if (type.equals(authType)) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<AuthType> getAuthTypes() {
        return authTypes;
    }
}