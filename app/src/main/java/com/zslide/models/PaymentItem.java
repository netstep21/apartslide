package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 15. 9. 22..
 */
public class PaymentItem extends ZummaApiData implements Parcelable {

    public static final Creator<PaymentItem> CREATOR = new Creator<PaymentItem>() {
        public PaymentItem createFromParcel(Parcel in) {
            return new PaymentItem(in);
        }

        public PaymentItem[] newArray(int size) {
            return new PaymentItem[size];
        }
    };
    @SerializedName("delivery_code") String mDeliveryCode;
    @SerializedName("delivery_office") String mDeliveryOffice;
    @SerializedName("product") String mProduct;
    @SerializedName("delivery_price") int mDeliveryPrice;
    @SerializedName("price") int mPrice;
    @SerializedName("real_price") int mRealPrice;
    @SerializedName("zmoney") int mZmoney;
    @SerializedName("logo_image_url") String mLogoImageUrl;
    @SerializedName("market_item_id") int mMarketItemId;
    @SerializedName("id") private int mId;
    @SerializedName("count") private int mCount;

    protected PaymentItem(Parcel src) {
        readFromParcel(src);
    }

    public int getId() {
        return mId;
    }

    public int getCount() {
        return mCount;
    }

    public String getDeliveryCode() {
        return mDeliveryCode;
    }

    public String getProduct() {
        return mProduct;
    }

    public String getDeliveryOffice() {
        return mDeliveryOffice;
    }

    public int getDeliveryPrice() {
        return mDeliveryPrice;
    }

    public int getPrice() {
        return mPrice;
    }

    public int getPaymentPrice() {
        return mRealPrice;
    }

    public int getZmoney() {
        return mZmoney;
    }

    public String getLogoImageUrl() {
        return mLogoImageUrl;
    }

    public int getMarketItemId() {
        return mMarketItemId;
    }

    public void readFromParcel(Parcel src) {
        mId = src.readInt();
        mCount = src.readInt();
        mDeliveryCode = src.readString();
        mDeliveryOffice = src.readString();
        mProduct = src.readString();
        mDeliveryPrice = src.readInt();
        mPrice = src.readInt();
        mRealPrice = src.readInt();
        mZmoney = src.readInt();
        mLogoImageUrl = src.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mCount);
        dest.writeString(mDeliveryCode);
        dest.writeString(mDeliveryOffice);
        dest.writeString(mProduct);
        dest.writeInt(mDeliveryPrice);
        dest.writeInt(mPrice);
        dest.writeInt(mRealPrice);
        dest.writeInt(mZmoney);
        dest.writeString(mLogoImageUrl);
    }
}
