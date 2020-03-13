package com.zslide.view.setting;

import android.text.TextUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 9..
 */

@AllArgsConstructor
class SettingUiModel {

    @Getter private String userProfileUrl;
    @Getter private String nickname;
    @Getter private String displayLevel;

    public boolean hasProfileImage() {
        return !TextUtils.isEmpty(userProfileUrl);
    }
}
