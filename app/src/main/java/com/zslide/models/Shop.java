package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by jdekim43 on 2015. 12. 23..
 */
public class Shop extends ZummaApiData implements Parcelable {

    public static final Creator<Shop> CREATOR = new Creator<Shop>() {
        @Override
        public Shop createFromParcel(Parcel in) {
            return new Shop(in);
        }

        @Override
        public Shop[] newArray(int size) {
            return new Shop[size];
        }
    };
    private static AtomicInteger noStoreId = new AtomicInteger(0);
    /**
     * 업체 고유 id
     */
    @SerializedName("id") private int mId;
    /**
     * 업체 이름
     */
    @SerializedName("name") private String mName;

    protected Shop() {
    }

    protected Shop(Parcel src) {
        readFromParcel(src);
    }

    public static Shop nextAnonymousStore() {
        Shop shop = new Shop();
        shop.mId = noStoreId.decrementAndGet();
        shop.mName = "No Shop";

        return shop;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public void readFromParcel(Parcel src) {
        mId = src.readInt();
        mName = src.readString();
    }

    @Override
    public int hashCode() {
        return mId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Shop) {
            return Shop.this.getId() == ((Shop) o).getId();
        }
        return false;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
    }
}
