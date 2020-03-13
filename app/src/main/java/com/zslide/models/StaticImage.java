package com.zslide.models;

import com.zslide.models.response.SimpleApiResponse;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chulwoo on 2017. 4. 12..
 */

public class StaticImage extends SimpleApiResponse {

    @SerializedName("image") private Image image;

    public String getUrl() {
        return image.url;
    }

    public int getWidth() {
        return image.width;
    }

    public int getHeight() {
        return image.height;
    }

    private static class Image {
        @SerializedName("id") private long id;
        @SerializedName("pub_date") private Date pubDate;
        @SerializedName("image_url") private String url;
        @SerializedName("goal") private String goal;
        @SerializedName("height") private int height;
        @SerializedName("width") private int width;
    }
}
