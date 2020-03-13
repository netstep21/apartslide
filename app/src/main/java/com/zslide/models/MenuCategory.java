package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by chulwoo on 2017. 5. 17..
 */

public class MenuCategory implements Parcelable {

    @SerializedName("id") long id;
    @SerializedName("name") String title;
    @SerializedName("description") String subtitle;
    @SerializedName("menuinfos") List<Menu> menu;

    public MenuCategory(long id, String title, String subtitle, List<Menu> menu) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.menu = menu;
    }

    protected MenuCategory(Parcel in) {
        id = in.readLong();
        title = in.readString();
        subtitle = in.readString();
        menu = in.createTypedArrayList(Menu.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeTypedList(menu);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof MenuCategory)) return false;
        MenuCategory menuCategory = (MenuCategory) obj;
        return id == menuCategory.id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MenuCategory> CREATOR = new Creator<MenuCategory>() {
        @Override
        public MenuCategory createFromParcel(Parcel in) {
            return new MenuCategory(in);
        }

        @Override
        public MenuCategory[] newArray(int size) {
            return new MenuCategory[size];
        }
    };

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public List<Menu> getMenuList() {
        return menu;
    }

}
