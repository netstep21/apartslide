package com.zslide.models;

import com.zslide.data.model.User;

/**
 * Created by chulwoo on 16. 7. 7..
 */
public class FamilyParams {

    public static final int TYPE_CREATE = 0;
    public static final int TYPE_UPDATE = 1;

    private int apiType;
    private long familyId;

    public FamilyParams(int type, User user) {
        if (user.hasFamily()) {
            this.familyId = user.getFamilyId();
        } else {
            this.familyId = -1;
        }
        this.apiType = type;
    }

    public int getApiType() {
        return apiType;
    }

    public long getFamilyId() {
        return familyId;
    }
}
