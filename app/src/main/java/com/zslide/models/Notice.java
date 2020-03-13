package com.zslide.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chulwoo on 2015. 10. 29..
 */
public class Notice extends ZummaApiData {

    @SerializedName("id") private long id;
    @SerializedName("pub_date") private Date pubDate;
    @SerializedName("title") private String title;
    @SerializedName("content") private String content;
    @SerializedName("count") private int count;
    @SerializedName("is_new") private boolean newNotice;

    public long getId() {
        return id;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public int getCount() {
        return count;
    }

    public boolean isNew() {
        return newNotice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Notice)) return false;

        Notice notice = (Notice) o;
        return id == notice.id;
    }

    @Override
    public int hashCode() {
        int result = 13;
        result = 31 * result + (int) id;

        return result;
    }
}
