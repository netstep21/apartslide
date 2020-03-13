package com.zslide.models;

import com.zslide.data.model.Family;
import com.zslide.data.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 8..
 * <p>
 * 추후 MVVM 적용하면서 제거할 것
 */

@Deprecated
@AllArgsConstructor
public class FamilyUser {

    @Getter User user;
    @Getter Family family;

    public boolean isNotNull() {
        return user.isNotNull();
    }

    public boolean isNull() {
        return user.isNull();
    }
}
