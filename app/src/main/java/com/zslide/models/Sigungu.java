package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 10. 5..
 */
public class Sigungu extends Location implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Sigungu createFromParcel(Parcel source) {
            return new Sigungu(source);
        }

        @Override
        public Sigungu[] newArray(int size) {
            return new Sigungu[size];
        }
    };
    @SerializedName("sido") private Sido sido;

    private Sigungu(Parcel src) {
        super(src);
    }

    public Sido getSido() {
        return sido;
    }

    public void readFromParcel(Parcel src) {
        super.readFromParcel(src);
        sido = src.readParcelable(Sido.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(sido, flags);
    }
}
