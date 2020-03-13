package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 15. 9. 16..
 */
public class ShippingAddress implements Parcelable {

    @SerializedName("id") private int id;
    @SerializedName("dong_id") private long dongId;
    @SerializedName("area_name") private String areaName;
    @SerializedName("dong_name") private String dongName;
    @SerializedName("shipping_address") private String shippingAddress;
    @SerializedName("shipping_user_name") private String userName;
    @SerializedName("shipping_user_phone_number") private String phoneNumber;

    protected ShippingAddress(Parcel src) {
        readFromParcel(src);
    }

    public long getId() {
        return id;
    }

    public long getDongId() {
        return dongId;
    }

    public String getAreaName() {
        return areaName;
    }

    public String getDongName() {
        return dongName;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getFullAddress(boolean newLine) {
        if (TextUtils.isEmpty(areaName) || TextUtils.isEmpty(dongName)) {
            return "";
        } else {
            String detailAddressSeperator = newLine ? "\n" : " ";
            return areaName + " " + dongName + detailAddressSeperator + shippingAddress;
        }
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = result * 31 + id;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof ShippingAddress)) return false;
        ShippingAddress shippingAddress = (ShippingAddress) obj;
        return id == shippingAddress.id;
    }

    public void readFromParcel(Parcel src) {
        id = src.readInt();
        dongId = src.readLong();
        areaName = src.readString();
        dongName = src.readString();
        shippingAddress = src.readString();
        userName = src.readString();
        phoneNumber = src.readString();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(dongId);
        dest.writeString(areaName);
        dest.writeString(dongName);
        dest.writeString(shippingAddress);
        dest.writeString(userName);
        dest.writeString(phoneNumber);
    }


    public static final Creator<ShippingAddress> CREATOR = new Creator<ShippingAddress>() {
        public ShippingAddress createFromParcel(Parcel in) {
            return new ShippingAddress(in);
        }

        public ShippingAddress[] newArray(int size) {
            return new ShippingAddress[size];
        }
    };
}
