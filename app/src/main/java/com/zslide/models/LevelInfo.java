package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.zslide.data.model.BaseModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.Getter;

/**
 * Created by chulwoo on 16. 8. 3..
 */
public class LevelInfo extends BaseModel {

    @Getter @SerializedName("date_from") String fromDate;
    @Getter @SerializedName("date_to") String toDate;
    @Getter @SerializedName("advantage") Advantage advantage;
    @Getter @SerializedName("advantage_reward") int advantageReward;
    @Getter @SerializedName("condition") List<String> conditions;

    protected LevelInfo(Parcel in) {
        super(in);
        fromDate = in.readString();
        toDate = in.readString();
        advantage = in.readParcelable(Advantage.class.getClassLoader());
        advantageReward = in.readInt();
        conditions = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(fromDate);
        dest.writeString(toDate);
        dest.writeParcelable(advantage, flags);
        dest.writeInt(advantageReward);
        dest.writeStringList(conditions);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LevelInfo> CREATOR = new Creator<LevelInfo>() {
        @Override
        public LevelInfo createFromParcel(Parcel in) {
            return new LevelInfo(in);
        }

        @Override
        public LevelInfo[] newArray(int size) {
            return new LevelInfo[size];
        }
    };

    public static class Advantage extends BaseModel implements Parcelable {
        @Getter @SerializedName("priority") int priority;
        @Getter @SerializedName("name") String name;
        @Getter @SerializedName("slide_count") int slideCount;
        @Getter @SerializedName("mall_coupon_count") int zummaShoppingCouponCount;
        @Getter @SerializedName("zummastore_coupon_count") int zummaStoreCouponCount;
        @Getter @SerializedName("zmoney_coupon_count") int zummaCouponCount;
        @Getter @SerializedName("zmoney_limit") int zmoneyLimit;
        @Getter @SerializedName("zmoney_coupon_amount") int zmoneyCouponAmount;
        @Getter @SerializedName("zummastore_coupon_amount") int zummaStoreCouponAmount;
        @Getter @SerializedName("mall_coupon_amount") int zummaShoppingCouponAmount;
        @Getter @SerializedName("image_url") String imageUrl;
        @Getter @SerializedName("image_url_small") String smallImageUrl;
        @Getter @SerializedName("image_url_box") String boxImageUrl;

        protected Advantage(Parcel in) {
            super(in);
            priority = in.readInt();
            name = in.readString();
            slideCount = in.readInt();
            zummaShoppingCouponCount = in.readInt();
            zummaStoreCouponCount = in.readInt();
            zummaCouponCount = in.readInt();
            zmoneyLimit = in.readInt();
            zmoneyCouponAmount = in.readInt();
            zummaStoreCouponAmount = in.readInt();
            zummaShoppingCouponAmount = in.readInt();
            imageUrl = in.readString();
            smallImageUrl = in.readString();
            boxImageUrl = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(priority);
            dest.writeString(name);
            dest.writeInt(slideCount);
            dest.writeInt(zummaShoppingCouponCount);
            dest.writeInt(zummaStoreCouponCount);
            dest.writeInt(zummaCouponCount);
            dest.writeInt(zmoneyLimit);
            dest.writeInt(zmoneyCouponAmount);
            dest.writeInt(zummaStoreCouponAmount);
            dest.writeInt(zummaShoppingCouponAmount);
            dest.writeString(imageUrl);
            dest.writeString(smallImageUrl);
            dest.writeString(boxImageUrl);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Advantage> CREATOR = new Creator<Advantage>() {
            @Override
            public Advantage createFromParcel(Parcel in) {
                return new Advantage(in);
            }

            @Override
            public Advantage[] newArray(int size) {
                return new Advantage[size];
            }
        };
    }
}
