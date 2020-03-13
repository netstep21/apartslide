package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 6. 27..
 */
public class OCBPoint {

    @SerializedName("point") int point;

    public int getPoint() {
        return point;
    }
}
