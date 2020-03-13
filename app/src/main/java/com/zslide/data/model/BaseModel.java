package com.zslide.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import lombok.Getter;

/**
 * Created by chulwoo on 2017. 12. 29..
 */

public class BaseModel implements Parcelable {

    private static final long NULL_ID = 0L;

    @Getter @SerializedName("id") private long id;
    @Getter @SerializedName("pub_date") private LocalDateTime pubDate;

    public BaseModel() {

    }

    public BaseModel(long id, LocalDateTime pubDate) {
        this.id = id;
        this.pubDate = pubDate;
    }

    protected BaseModel(Parcel in) {
        id = in.readLong();
        pubDate = readLocalDateTime(in);
    }

    protected LocalDateTime readLocalDateTime(Parcel in) {
        long millis = in.readLong();
        return millis == 0 ? null :
                LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
    }

    protected void writeLocalDateTime(Parcel dest, LocalDateTime date) {
        long millis = (date == null) ? 0 : date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        dest.writeLong(millis);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        writeLocalDateTime(dest, pubDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BaseModel> CREATOR = new Creator<BaseModel>() {
        @Override
        public BaseModel createFromParcel(Parcel in) {
            return new BaseModel(in);
        }

        @Override
        public BaseModel[] newArray(int size) {
            return new BaseModel[size];
        }
    };

    public boolean isNull() {
        return id == NULL_ID;
    }

    public boolean isNotNull() {
        return id != NULL_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof BaseModel)) return false;

        BaseModel model = (BaseModel) o;
        return id == model.id;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + (int) id;

        return result;
    }
}
