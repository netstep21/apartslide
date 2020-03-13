package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class MarketItem extends ZummaApiData implements Parcelable {

    @SerializedName("id") private long mId;
    @SerializedName("active") private boolean active;
    @SerializedName("title") private String mTitle;
    @SerializedName("logo_image_url") private String mLogoImageUrl;
    @SerializedName("detail_image_url") private String mDetailImageUrl;
    @SerializedName("detail_image_url2") private String mDetailImageUrl2;
    @SerializedName("detail_image_url3") private String mDetailImageUrl3;
    @SerializedName("detail_image_url4") private String mDetailImageUrl4;
    @SerializedName("detail_image_url5") private String mDetailImageUrl5;
    @SerializedName("detail_image_url6") private String mDetailImageUrl6;
    @SerializedName("detail_main_image_url") private String mDetailMainImageUrl;
    @SerializedName("image_url") private String mImageUrl;
    @SerializedName("coupon_rate_or_money") private int mDeductionRate;
    @SerializedName("is_delivery_free") private boolean mDeliveryFree;
    @SerializedName("is_new") private boolean mNew;
    @SerializedName("is_hit") private boolean mHit;
    @SerializedName("is_recommend") private boolean mRecommend;
    @SerializedName("custom_label") private String mCustomLabel;
    @SerializedName("market_real_price") private int mOriginPrice;
    @SerializedName("market_user_price") private int mPrice;
    @SerializedName("delivery_price") private int mDeliveryPrice;
    @SerializedName("store") private Shop mShop;
    @SerializedName("category") private String categoryName;
    @SerializedName("delivery_info") private String deliveryInfo;
    @SerializedName("delivery_extra") private String deliveryExtra;
    @SerializedName("delivery_detail_info") private String deliveryDetailInfo;
    @SerializedName("exchange_detail_info") private String exchangeDetailInfo;

    protected MarketItem(Parcel in) {
        mId = in.readLong();
        active = in.readByte() != 0;
        mTitle = in.readString();
        mLogoImageUrl = in.readString();
        mDetailImageUrl = in.readString();
        mDetailImageUrl2 = in.readString();
        mDetailImageUrl3 = in.readString();
        mDetailImageUrl4 = in.readString();
        mDetailImageUrl5 = in.readString();
        mDetailImageUrl6 = in.readString();
        mDetailMainImageUrl = in.readString();
        mImageUrl = in.readString();
        mDeductionRate = in.readInt();
        mDeliveryFree = in.readByte() != 0;
        mNew = in.readByte() != 0;
        mHit = in.readByte() != 0;
        mRecommend = in.readByte() != 0;
        mCustomLabel = in.readString();
        mOriginPrice = in.readInt();
        mPrice = in.readInt();
        mDeliveryPrice = in.readInt();
        mShop = in.readParcelable(Shop.class.getClassLoader());
        categoryName = in.readString();
        deliveryInfo = in.readString();
        deliveryExtra = in.readString();
        deliveryDetailInfo = in.readString();
        exchangeDetailInfo = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mId);
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(mTitle);
        dest.writeString(mLogoImageUrl);
        dest.writeString(mDetailImageUrl);
        dest.writeString(mDetailImageUrl2);
        dest.writeString(mDetailImageUrl3);
        dest.writeString(mDetailImageUrl4);
        dest.writeString(mDetailImageUrl5);
        dest.writeString(mDetailImageUrl6);
        dest.writeString(mDetailMainImageUrl);
        dest.writeString(mImageUrl);
        dest.writeInt(mDeductionRate);
        dest.writeByte((byte) (mDeliveryFree ? 1 : 0));
        dest.writeByte((byte) (mNew ? 1 : 0));
        dest.writeByte((byte) (mHit ? 1 : 0));
        dest.writeByte((byte) (mRecommend ? 1 : 0));
        dest.writeString(mCustomLabel);
        dest.writeInt(mOriginPrice);
        dest.writeInt(mPrice);
        dest.writeInt(mDeliveryPrice);
        dest.writeParcelable(mShop, flags);
        dest.writeString(categoryName);
        dest.writeString(deliveryInfo);
        dest.writeString(deliveryExtra);
        dest.writeString(deliveryDetailInfo);
        dest.writeString(exchangeDetailInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MarketItem> CREATOR = new Creator<MarketItem>() {
        @Override
        public MarketItem createFromParcel(Parcel in) {
            return new MarketItem(in);
        }

        @Override
        public MarketItem[] newArray(int size) {
            return new MarketItem[size];
        }
    };

    public long getId() {
        return mId;
    }

    public boolean isActive() {
        return active;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLogoImageUrl() {
        return mLogoImageUrl;
    }

    public String[] getDetailImageUrls() {
        return new String[]{
                mDetailImageUrl, mDetailImageUrl2, mDetailImageUrl3,
                mDetailImageUrl4, mDetailImageUrl5, mDetailImageUrl6};
    }

    public String getDetailMainImageUrl() {
        return mDetailMainImageUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public int getDeductionRate() {
        return mDeductionRate;
    }

    public int getDeductionPrice() {
        int deductionPrice = mDeductionRate;
        if (mDeductionRate > 0 && mDeductionRate < 100) {
            deductionPrice =
                    (int) (mPrice * (((double) mDeductionRate) / 100));
        }

        return deductionPrice;
    }

    public boolean isDeliveryFree() {
        return mDeliveryFree;
    }

    public boolean isNew() {
        return mNew;
    }

    public boolean isHit() {
        return mHit;
    }

    public boolean isRecommend() {
        return mRecommend;
    }

    public String getCustomLabel() {
        return mCustomLabel;
    }

    public int getOriginPrice() {
        return mOriginPrice;
    }

    public int getPrice() {
        return mPrice;
    }

    public int getDeliveryPrice() {
        return mDeliveryPrice;
    }

    public Shop getStore() {
        return mShop;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDeliveryInfo() {
        return deliveryInfo;
    }

    public String getDeliveryExtra() {
        return deliveryExtra;
    }

    public String getDeliveryDetailInfo() {
        return deliveryDetailInfo;
    }

    public String getExchangeDetailInfo() {
        return exchangeDetailInfo;
    }

    public void readFromParcel(Parcel src) {
        mId = src.readLong();
        active = src.readInt() == 1;
        mTitle = src.readString();
        mLogoImageUrl = src.readString();
        mDetailImageUrl = src.readString();
        mDetailImageUrl2 = src.readString();
        mDetailImageUrl3 = src.readString();
        mDetailImageUrl4 = src.readString();
        mDetailImageUrl5 = src.readString();
        mDetailImageUrl6 = src.readString();
        mDetailMainImageUrl = src.readString();
        mImageUrl = src.readString();
        mDeductionRate = src.readInt();
        mDeliveryFree = src.readInt() == 1;
        mNew = src.readInt() == 1;
        mHit = src.readInt() == 1;
        mRecommend = src.readInt() == 1;
        mCustomLabel = src.readString();
        mOriginPrice = src.readInt();
        mPrice = src.readInt();
        mDeliveryPrice = src.readInt();
        mShop = src.readParcelable(Shop.class.getClassLoader());
        categoryName = src.readString();
    }

}
