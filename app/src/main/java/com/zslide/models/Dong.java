package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.zslide.utils.StringUtil;
import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 10. 5..
 */
public class Dong extends Location implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        @Override
        public Dong createFromParcel(Parcel source) {
            return new Dong(source);
        }

        @Override
        public Dong[] newArray(int size) {
            return new Dong[size];
        }
    };
    @SerializedName("dong_name") private String name;
    @SerializedName("latitude") private double lat;
    @SerializedName("longitude") private double lng;
    @SerializedName("code") private String code;
    @SerializedName("sigungu") private Sigungu sigungu;

    private Dong(Parcel src) {
        super(src);
    }

    public String getAddress() {
        return StringUtil.format("%s %s %s", sigungu.getSido().getName(), sigungu.getName(), name);
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getCode() {
        return code;
    }

    public Sigungu getSigungu() {
        return sigungu;
    }

    public Sido getSido() {
        return sigungu.getSido();
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + getId();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Dong)) {
            return false;
        }

        Dong dong = (Dong) obj;
        return dong.getId() == getId();
    }

    public void readFromParcel(Parcel src) {
        super.readFromParcel(src);
        name = src.readString();
        lat = src.readDouble();
        lng = src.readDouble();
        code = src.readString();
        sigungu = src.readParcelable(Sigungu.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(name);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
        dest.writeString(code);
        dest.writeParcelable(sigungu, flags);
    }
}
