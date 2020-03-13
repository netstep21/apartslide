package com.zslide.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by chulwoo on 15. 7. 2..
 */
public class Address extends BaseModel implements Parcelable, Cloneable {

    @Getter @SerializedName("area_name") private String areaName;
    @Getter @SerializedName("dong_name") private String dongName;
    @Getter @SerializedName("latitude") private double lat;
    @Getter @SerializedName("longitude") private double lng;

    protected Address(Parcel in) {
        super(in);
        areaName = in.readString();
        dongName = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(areaName);
        dest.writeString(dongName);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public String getFullAddress() {
        return areaName + " " + dongName;
    }

    @Override
    public String toString() {
        return getFullAddress();
    }

    public Address clone() throws CloneNotSupportedException {
        return Address.class.cast(super.clone());
    }
}
