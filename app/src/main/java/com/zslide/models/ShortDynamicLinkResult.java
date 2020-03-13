package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 2016. 12. 22..
 */

public class ShortDynamicLinkResult {
    @SerializedName("shortLink") String shortLink;
    @SerializedName("previewLink") String previewLink;

    public String getShortLink() {
        return shortLink;
    }

    public String getPreviewLink() {
        return previewLink;
    }
}
