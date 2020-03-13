package com.zslide.models;

import com.google.gson.annotations.SerializedName;

public class Bank {

    @SerializedName("id") private int mId;
    @SerializedName("name") private String mName;

    public static Bank mock(String name) {
        Bank bank = new Bank();
        bank.mId = -1;
        bank.mName = name;
        return bank;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    @Override
    public String toString() {
        return mName;
    }
}
