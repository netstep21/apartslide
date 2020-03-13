package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 15. 7. 23..
 */
public class Participation extends ZummaApiData {

    @SerializedName("ad_id") int mAdId;
    @SerializedName("is_left") boolean mLeft;

    public Participation(int adId, boolean isLeft) {
        mAdId = adId;
        mLeft = isLeft;
    }

    public int getAdId() {
        return mAdId;
    }

    public boolean isLeft() {
        return mLeft;
    }
}
