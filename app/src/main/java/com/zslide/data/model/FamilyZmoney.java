package com.zslide.data.model;

import android.support.annotation.NonNull;

import java.util.List;

import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 9..
 */

public class FamilyZmoney {

    public static final FamilyZmoney NULL = new FamilyZmoney();

    @Getter private int total;
    @Getter private List<Zmoney> zmoneys;

    public Zmoney getUserZmoney(@NonNull User user) {
        if (zmoneys != null) {
            for (Zmoney zmoney : zmoneys) {
                if (user.equals(zmoney.getUser())) {
                    return zmoney;
                }
            }
        }

        return Zmoney.NULL;
    }
}
