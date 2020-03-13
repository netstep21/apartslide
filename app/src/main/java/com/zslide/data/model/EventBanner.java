package com.zslide.data.model;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.LocalDateTime;

import lombok.Getter;

/**
 * Created by chulwoo on 16. 5. 16..
 */
public class EventBanner extends BaseModel {

    @Getter @SerializedName("active") boolean active;
    @Getter @SerializedName("count") int count;
    @Getter @SerializedName("title") private String title;
    @Getter @SerializedName("end_date") private LocalDateTime endDate;
    @Getter @SerializedName("target") private String target;
    @Getter @SerializedName("image_url") private String imageUrl;
}
