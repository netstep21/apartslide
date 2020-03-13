package com.zslide.models;

import com.zslide.data.model.User;

/**
 * Created by chulwoo on 16. 7. 7..
 */
public class TempApartmentFamilyParams extends FamilyParams {
    private long tempApartmentId;

    public TempApartmentFamilyParams(int type, User user, long tempApartmentId) {
        super(type, user);
        this.tempApartmentId = tempApartmentId;
    }

    public long getTempApartmentId() {
        return tempApartmentId;
    }
}
