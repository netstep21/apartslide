package com.zslide.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by jdekim43 on 2016. 1. 25..
 */
public class Faq extends ZummaApiData {

    @SerializedName("id") private int id;
    @SerializedName("pub_date") private Date pubDate;
    @SerializedName("title") private String title;
    @SerializedName("content") private String content;

    public int getId() {
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

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Faq)) return false;

        Faq notice = (Faq) o;
        return id == notice.id;
    }

    @Override
    public int hashCode() {
        int result = 13;
        result = 31 * result + id;

        return result;
    }

    public static class Category extends ZummaApiData {
        @SerializedName("id") int id;
        @SerializedName("pub_date") Date pubDate;
        @SerializedName("title") String title;

        public int getId() {
            return id;
        }

        public Date getPubDate() {
            return pubDate;
        }

        public String getTitle() {
            return title;
        }
    }
}
