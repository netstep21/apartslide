package com.zslide.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 15. 11. 27..
 */
public class RecommendCountResponse extends SimpleApiResponse {

    @SerializedName("recommend_count") private int mRecommendCount;

    public int getRecommendCount() {
        return mRecommendCount;
    }
}
