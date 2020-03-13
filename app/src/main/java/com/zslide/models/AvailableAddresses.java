package com.zslide.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.zslide.data.model.Address;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chulwoo on 2017. 5. 31..
 */

public class AvailableAddresses implements Parcelable {

    private final List<Long> addresses;

    public AvailableAddresses(List<Long> addresses) {
        this.addresses = addresses;
    }

    public AvailableAddresses(Parcel in) {
        int size = in.readInt();
        long[] parcelFileHandles = new long[size];
        in.readLongArray(parcelFileHandles);
        this.addresses = toObjects(size, parcelFileHandles);
    }

    public boolean isAvailable(Address address) {
        return addresses.size() == 0 || addresses.contains(address.getId());
    }

    public boolean isAvailable(ShippingAddress address) {
        return addresses.size() == 0 || addresses.contains(address.getDongId());
    }

    public boolean isAvailable(long id) {
        return addresses.size() == 0 |addresses.contains(id);
    }

    private List<Long> toObjects(int size, long[] parcelFileHandles) {
        List<Long> primitiveConv = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            primitiveConv.add(parcelFileHandles[i]);
        }
        return primitiveConv;
    }

    public List<Long> asList() { // Prefer you didn't use this method & added domain login here, but stackoverflow can only teach so much..
        return addresses;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(addresses.size());
        dest.writeLongArray(toPrimitives(addresses));
    }

    private static long[] toPrimitives(List<Long> list) {
        return toPrimitives(list.toArray(new Long[list.size()]));
    }

    public static long[] toPrimitives(Long... objects) {
        long[] primitives = new long[objects.length];
        for (int i = 0; i < objects.length; i++)
            primitives[i] = objects[i];

        return primitives;
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public AvailableAddresses createFromParcel(Parcel in) {
            return new AvailableAddresses(in);
        }

        @Override
        public AvailableAddresses[] newArray(int size) {
            return new AvailableAddresses[size];
        }
    };
}