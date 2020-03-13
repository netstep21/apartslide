package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 2016. 10. 14..
 */

public class Reward implements Parcelable {

    /**
     * 고정금액(원) 적립
     */
    public static final int TYPE_SAVE = 0;
    /**
     * 비율(%) 적립
     */
    public static final int TYPE_PERCENT = 1;
    /**
     * 콜 적립(원)
     */
    public static final int TYPE_CALL = 2;
    /**
     * 스탬프 적립
     */
    public static final int TYPE_STAMP = 3;
    public static final Creator<Reward> CREATOR = new Creator<Reward>() {
        public Reward createFromParcel(Parcel in) {
            return new Reward(in);
        }

        public Reward[] newArray(int size) {
            return new Reward[size];
        }
    };
    @SerializedName("type") private int type;
    @SerializedName("value") private String value;

    protected Reward(Parcel src) {
        readFromParcel(src);
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public void readFromParcel(Parcel src) {
        type = src.readInt();
        value = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(value);
    }


}
