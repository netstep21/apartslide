package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 10. 5..
 */
public class Sido extends Location {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Sido createFromParcel(Parcel source) {
            return new Sido(source);
        }

        @Override
        public Sido[] newArray(int size) {
            return new Sido[size];
        }
    };
    @SerializedName("short_name") String shortName;

    private Sido(Parcel src) {
        super(src);
    }

    @Override
    public String getName() {
        return shortName;
    }

    public void readFromParcel(Parcel src) {
        super.readFromParcel(src);
        shortName = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(shortName);
    }
}
