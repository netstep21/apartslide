package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Apartment implements Parcelable, Cloneable {

    public static final Parcelable.Creator<Apartment> CREATOR = new Creator<Apartment>() {
        @Override
        public Apartment createFromParcel(Parcel source) {
            return new Apartment(source);
        }

        @Override
        public Apartment[] newArray(int size) {
            return new Apartment[size];
        }
    };
    @SerializedName("living_type") LivingType livingType;
    @SerializedName("id") private int id;
    @SerializedName("apart_name") private String name;
    @SerializedName("dong") private String dong;
    @SerializedName("ho") private String ho;
    @SerializedName("detail_address") private String detailAddress;
    @SerializedName("date_joined") private String dateJoined;
    @SerializedName("recommend_count") private int requestCount;

    protected Apartment() {

    }

    protected Apartment(Parcel src) {
        id = src.readInt();
        name = src.readString();
        dong = src.readString();
        ho = src.readString();
        dateJoined = src.readString();
        requestCount = src.readInt();
        livingType = (LivingType) src.readSerializable();
    }

    public static Apartment mock() {
        Apartment apartment = new Apartment();
        apartment.id = 0;
        apartment.name = "신광";
        apartment.dong = "1111";
        apartment.ho = "1111";
        apartment.dateJoined = "";
        return apartment;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDong() {
        return dong;
    }

    public void setDong(String dong) {
        this.dong = dong;
    }

    public int getHo() {
        try {
            return Integer.parseInt(ho);
        } catch (Exception e) {
            return 0;
        }
    }

    public void setHo(int ho) {
        this.ho = Integer.toString(ho);
    }

    public String getDateJoinedString() {
        return dateJoined;
    }

    public Date getDateJoined() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = null;
        try {
            date = formatter.parse(dateJoined);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public boolean isJoined() {
        return !TextUtils.isEmpty(dateJoined);
    }

    public int getRequestCount() {
        return requestCount;
    }

    public void increaseRequestCount() {
        requestCount++;
    }

    public LivingType getLivingType() {
        return livingType;
    }

    public String getAddress() {
        if (LivingType.APARTMENT.equals(livingType)) {
            return name + " " + dong + "동 " + ho + "호";
        } else if (LivingType.HOUSE.equals(livingType)) {
            return detailAddress;
        }

        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof Apartment)) {
            return false;
        }

        Apartment apartment = (Apartment) o;
        return id == apartment.id;
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(dong);
        dest.writeString(ho);
        dest.writeString(dateJoined);
        dest.writeInt(requestCount);
        dest.writeSerializable(livingType);
    }

    @Override
    public String toString() {
        return getName();
    }

    public Account clone() throws CloneNotSupportedException {
        return Account.class.cast(super.clone());
    }
}
