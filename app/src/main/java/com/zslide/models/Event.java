package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.zslide.data.model.BaseModel;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by jdekim43 on 2016. 1. 18..
 */
public class Event extends BaseModel implements Parcelable {

    public static final int TYPE_TODAY_NOT_SHOWING = 0;
    public static final int TYPE_NEVER_SHOWING = 1;
    public static final int TYPE_WEEK_NOT_SHOWING = 2;

    @Getter @SerializedName("title") private String title;
    @Getter @SerializedName("target") private String target;
    @Getter @SerializedName("image_url") private String imageUrl;
    @Getter @SerializedName("label") private String label;
    @Getter @SerializedName("type") private int type;

    protected Event(Parcel in) {
        super(in);
        title = in.readString();
        target = in.readString();
        imageUrl = in.readString();
        label = in.readString();
        type = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(target);
        dest.writeString(imageUrl);
        dest.writeString(label);
        dest.writeInt(type);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}