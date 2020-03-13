package com.zslide.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by jdekim43 on 2016. 3. 15..
 */
public class Like extends ZummaApiData {

    @SerializedName("id") private int id;
    @SerializedName("logo_image_url") private String logoImageUrl;
    @SerializedName("title") private String title;
    @SerializedName("description") private String description;

    public int getId() {
        return id;
    }

    public String getLogoImageUrl() {
        return logoImageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
