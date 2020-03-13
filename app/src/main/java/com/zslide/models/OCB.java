package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by chulwoo on 16. 6. 27..
 */
public class OCB implements Parcelable {

    public static final Creator CREATOR = new Creator() {
        @Override
        public OCB createFromParcel(Parcel source) {
            return new OCB(source);
        }

        @Override
        public OCB[] newArray(int size) {
            return new OCB[size];
        }
    };
    private static final String SUCCESS = "000000";
    @SerializedName("ReplyCode") String responseCode;
    @SerializedName("ReplyMessage") String message;
    @SerializedName("AvPoint") String point;
    @SerializedName("MctTrDate") String mctTrDate;
    @SerializedName("MctTrNo") String mctTrNo;
    @SerializedName("TxNo") String txNo;

    protected OCB(Parcel src) {
        readFromParcel(src);
    }

    public boolean isSuccess() {
        return SUCCESS.equals(responseCode);
    }

    public String getMessage() {
        return message;
    }

    public int getPoint() {
        try {
            return Integer.parseInt(point);
        } catch (Exception e) {
            return 0;
        }
    }

    public String getMctTrDate() {
        return mctTrDate;
    }

    public String getMctTrNo() {
        return mctTrNo;
    }

    public String getTxNo() {
        return txNo;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    public void readFromParcel(Parcel src) {
        responseCode = src.readString();
        message = src.readString();
        point = src.readString();
        txNo = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(responseCode);
        dest.writeString(message);
        dest.writeString(point);
        dest.writeString(txNo);
    }
}
