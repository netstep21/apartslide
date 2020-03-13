package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;
import com.zslide.data.model.BaseModel;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by jdekim43 on 2016. 3. 16..
 */
public class Review extends BaseModel implements Parcelable {

    public static final int MIN_LENGTH = 10;

    @Getter
    @SerializedName("active") private boolean active;
    @Getter
    @SerializedName("profile_image") private String profileImageUrl;
    @Getter
    @SerializedName("user") private String nickname;
    @Setter
    @Getter
    @SerializedName("content") private String content;
    @Setter
    @Getter
    @SerializedName("rereview") private Review comment;
    @Setter
    @SerializedName("delete_permission") private boolean permission;
    @SerializedName("rating") private int rating;

    protected Review(Parcel in) {
        active = in.readByte() != 0;
        profileImageUrl = in.readString();
        nickname = in.readString();
        content = in.readString();
        comment = in.readParcelable(Review.class.getClassLoader());
        permission = in.readByte() != 0;
        rating = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (active ? 1 : 0));
        dest.writeString(profileImageUrl);
        dest.writeString(nickname);
        dest.writeString(content);
        dest.writeParcelable(comment, flags);
        dest.writeByte((byte) (permission ? 1 : 0));
        dest.writeInt(rating);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public int getRating() {
        return rating == 0 ? 5 : rating;
    }

    public boolean hasPermission() {
        return permission;
    }
}
