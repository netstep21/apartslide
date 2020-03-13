package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by chulwoo on 2018. 8. 28..
 */
public class ShoppingReview extends Review {

    protected ShoppingReview(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return super.describeContents();
    }

    public static final Parcelable.Creator<ShoppingReview> CREATOR = new Parcelable.Creator<ShoppingReview>() {
        @Override
        public ShoppingReview createFromParcel(Parcel in) {
            return new ShoppingReview(in);
        }

        @Override
        public ShoppingReview[] newArray(int size) {
            return new ShoppingReview[size];
        }
    };
}
