package com.zslide.models;

import com.zslide.data.model.User;

/**
 * Created by chulwoo on 16. 7. 7..
 */
public class ApartmentFamilyParams extends FamilyParams {
    private long apartId;
    private String dong;
    private int ho;

    public ApartmentFamilyParams(int type, User user, long apartId, String dong, int ho) {
        super(type, user);
        this.apartId = apartId;
        this.dong = dong;
        this.ho = ho;
    }

    public long getApartId() {
        return apartId;
    }

    public String getDong() {
        return dong;
    }

    public int getHo() {
        return ho;
    }
}
