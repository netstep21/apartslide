package com.zslide.data.remote.request;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.zslide.data.model.AuthType;
import com.zslide.models.Sex;
import com.zslide.utils.PhoneNumberUtil;

import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 10..
 */

public class SignupRequest {

    @Getter private String authType;
    @Getter private String phoneNumber;
    @Getter private String imei;
    @Getter private String birthYear;
    @Getter private String sex;
    @Getter private String recommendCode;
    @Getter private String refreshToken;
    @Getter private String accessToken;
    @Getter private String uid;

    public SignupRequest(Builder builder) {
        this.authType = builder.authType.getValue();
        this.phoneNumber = builder.phoneNumber;
        this.imei = builder.imei;
        this.birthYear = builder.birthYear == 0 ? "" : String.valueOf(builder.birthYear);
        this.sex = builder.sex.getSimpleEnglish();
        this.recommendCode = builder.recommendCode;
        this.refreshToken = builder.refreshToken;
        this.accessToken = builder.accessToken;
        this.uid = builder.uid;
    }

    public static class Builder {
        private AuthType authType;
        private String phoneNumber;
        private String imei;
        private int birthYear;
        private Sex sex = Sex.MAN;
        private String recommendCode;
        private String refreshToken;
        private String accessToken;
        private String uid;

        public Builder(Context context, AuthType authType, int birthYear, Sex sex) {
            this.authType = authType;
            this.phoneNumber = PhoneNumberUtil.getPhoneNumber(context);
            this.imei = getImei(context);
            this.birthYear = birthYear;
            this.sex = sex;
        }

        public Builder setRecommendCode(String recommendCode) {
            this.recommendCode = recommendCode;
            return this;
        }

        public Builder setRefreshToken(String refreshToken) {
            if (!authType.isSocialType()) {
                throw new IllegalStateException("소셜 타입일 경우에만 허용됩니다.");
            }
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder setAccessToken(String accessToken) {
            if (!authType.isSocialType()) {
                throw new IllegalStateException("소셜 타입일 경우에만 허용됩니다.");
            }
            this.accessToken = accessToken;
            return this;
        }

        public Builder setUid(String uid) {
            if (!AuthType.NAVER.equals(authType)) {
                throw new IllegalStateException("NAVER 일 경우에만 허용됩니다.");
            }
            this.uid = uid;
            return this;
        }

        @SuppressLint({"MissingPermission", "HardwareIds"})
        private String getImei(Context context) {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return (manager == null) ? "" : manager.getDeviceId();
        }

        public SignupRequest build() {
            return new SignupRequest(this);
        }
    }
}
