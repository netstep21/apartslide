package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by chulwoo on 16. 4. 7..
 */
public class TempApartment implements Parcelable {

    public static final int STATUS_WAIT = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = 2;
    public static final Parcelable.Creator CREATOR = new Creator() {
        @Override
        public TempApartment createFromParcel(Parcel source) {
            return new TempApartment(source);
        }

        @Override
        public TempApartment[] newArray(int size) {
            return new TempApartment[ size];
        }
    };
    private @SerializedName("id") int id;
    private @SerializedName("pub_date") Date pubDate;
    private @SerializedName("active") boolean active;
    private @SerializedName("active_date") Date activeDate;
    private @SerializedName("apart_name") String name;
    private @SerializedName("status") int status;
    private @SerializedName("count") int count;
    private @SerializedName("dong_id") int dongId;
    private @SerializedName("dong") String dong;
    private @SerializedName("message") String message;
    private @SerializedName("apart") Apartment apartment;

    protected TempApartment(Parcel src) {
        this.id = src.readInt();
        long tmpDate = src.readLong();
        this.pubDate = tmpDate == -1 ? null : new Date(tmpDate);
        this.active = src.readInt() == 1;
        tmpDate = src.readLong();
        this.activeDate = tmpDate == -1 ? null : new Date(tmpDate);
        this.name = src.readString();
        this.status = src.readInt();
        this.count = src.readInt();
        this.dongId = src.readInt();
        this.dong = src.readString();
        this.message = src.readString();
        this.apartment = src.readParcelable(Apartment.class.getClassLoader());
    }

    public int getId() {
        return id;
    }

    public Date getPubDate() {
        return pubDate;
    }

    public boolean isActive() {
        return active;
    }

    public Date getActiveDate() {
        return activeDate;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public int getCount() {
        return count;
    }

    public int getDongId() {
        return dongId;
    }

    public String getDong() {
        return dong;
    }

    public String getMessage() {
        return message;
    }

    public Apartment getApartment() {
        return apartment;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong((pubDate == null) ? -1 : pubDate.getTime());
        dest.writeInt(active ? 1 : 0);
        dest.writeLong(activeDate == null ? -1 : activeDate.getTime());
        dest.writeString(name);
        dest.writeInt(status);
        dest.writeInt(count);
        dest.writeInt(dongId);
        dest.writeString(dong);
        dest.writeString(message);
        dest.writeParcelable(apartment, flags);
    }
}
