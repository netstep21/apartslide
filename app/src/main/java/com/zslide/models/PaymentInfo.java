package com.zslide.models;

/**
 * Created by chulwoo on 15. 9. 15..
 */

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PaymentInfo extends ZummaApiData implements Parcelable {

    public static final int STATUS_WAIT_PAYMENT = 0;
    public static final int STATUS_COMPLETE_PAYMENT = 1;
    public static final int STATUS_CANCEL = 9;
    public static final Creator<PaymentInfo> CREATOR = new Creator<PaymentInfo>() {
        public PaymentInfo createFromParcel(Parcel in) {
            return new PaymentInfo(in);
        }

        public PaymentInfo[] newArray(int size) {
            return new PaymentInfo[size];
        }
    };
    @SerializedName("id") private int mId;
    @SerializedName("sign_date") private String mSignDate;
    @SerializedName("payment_price") private int mPaymentPrice;
    @SerializedName("payment_status") private int mPaymentStatusCode;
    @SerializedName("account_limit") private String mAccountLimit;
    @SerializedName("shipping_address") private ShippingAddress mShippingAddress;
    @SerializedName("items") private ArrayList<PaymentItem> mPaymentItems;
    @SerializedName("payment_type") private String mPaymentType;
    @SerializedName("bank") private String mBank;
    @SerializedName("account_no") private String mAccountNo;
    @SerializedName("zslide_payment_code") private String mZslidePaymentCode;

    protected PaymentInfo(Parcel src) {
        readFromParcel(src);
    }

    public static String getStatus(int statusCode) {
        switch (statusCode) {
            case STATUS_WAIT_PAYMENT:
                return "입금대기중";
            case STATUS_COMPLETE_PAYMENT:
                return "결제완료";
            case STATUS_CANCEL:
                return "주문취소";
        }

        return "";
    }

    public int getId() {
        return mId;
    }

    public String getSignDate() {
        return mSignDate;
    }

    public int getPaymentPrice() {
        return mPaymentPrice;
    }

    public int getStatusCode() {
        return mPaymentStatusCode;
    }

    public String getStatus() {
        return getStatus(mPaymentStatusCode);
    }

    public String getAccountLimit() {
        return mAccountLimit;
    }

    public ShippingAddress getShippingAddress() {
        return mShippingAddress;
    }

    public List<PaymentItem> getItems() {
        return mPaymentItems;
    }

    public String getPaymentType() {
        return mPaymentType;
    }

    public String getBank() {
        return mBank;
    }

    public String getAccountNo() {
        return mAccountNo;
    }

    public String getPaymentCode() {
        return mZslidePaymentCode;
    }

    public void readFromParcel(Parcel src) {
        mId = src.readInt();
        mSignDate = src.readString();
        mPaymentPrice = src.readInt();
        mPaymentStatusCode = src.readInt();
        mAccountLimit = src.readString();
        mShippingAddress = src.readParcelable(ShippingAddress.class.getClassLoader());
        mPaymentItems = new ArrayList<>();
        src.readTypedList(mPaymentItems, PaymentItem.CREATOR);
        mPaymentType = src.readString();
        mBank = src.readString();
        mAccountNo = src.readString();
        mZslidePaymentCode = src.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mSignDate);
        dest.writeInt(mPaymentPrice);
        dest.writeInt(mPaymentStatusCode);
        dest.writeString(mAccountLimit);
        dest.writeParcelable(mShippingAddress, flags);
        dest.writeTypedList(mPaymentItems);
        dest.writeString(mPaymentType);
        dest.writeString(mBank);
        dest.writeString(mAccountNo);
        dest.writeString(mZslidePaymentCode);
    }
}
