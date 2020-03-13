package com.zslide.models;

import com.zslide.data.model.User;

/**
 * Created by chulwoo on 16. 7. 7..
 */
public class HouseFamilyParams extends FamilyParams {

    private long dongId;
    private String detailAddress;

    public HouseFamilyParams(int type, User user, long dongId, String detailAddress) {
        super(type, user);
        this.dongId = dongId;
        this.detailAddress = detailAddress;
    }

    public long getDongId() {
        return dongId;
    }

    public String getDetailAddress() {
        return detailAddress;
    }
}
