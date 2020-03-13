package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 2016. 10. 6..
 */

public class Location extends ZummaApiData implements Parcelable {

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public Location createFromParcel(Parcel source) {
            return new Location(source);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };
    @SerializedName("id") private int id;
    @SerializedName("name") private String name;
    @SerializedName("store_count") private int storeCount;

    Location() {

    }

    Location(Parcel src) {
        readFromParcel(src);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getStoreCount() {
        return storeCount;
    }

    public boolean hasStore() {
        return storeCount > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Location)) return false;
        Location location = (Location) obj;
        return location.id == this.id;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id;
        return result;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public void readFromParcel(Parcel src) {
        id = src.readInt();
        name = src.readString();
        storeCount = src.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(storeCount);
    }
}
