package com.zslide.models;

import com.zslide.models.response.SimpleApiResponse;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chulwoo on 2017. 4. 12..
 */

public class AppValue extends SimpleApiResponse {

    @SerializedName("value") private Value value;

    public String getValue() {
        return value.content;
    }

    private static class Value {
        @SerializedName("id") private long id;
        @SerializedName("pub_date") private Date pubDate;
        @SerializedName("content") private String content;
        @SerializedName("goal") private String goal;
    }
}
