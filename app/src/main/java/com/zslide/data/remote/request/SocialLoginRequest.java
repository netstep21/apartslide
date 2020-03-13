package com.zslide.data.remote.request;

import com.zslide.data.model.AuthType;

import lombok.Getter;

/**
 * Created by chulwoo on 2018. 1. 10..
 */

public class SocialLoginRequest {

    @Getter private String authType;
    @Getter private String refreshToken;
    @Getter private String accessToken;
    @Getter private String uid;
    @Getter private String phoneNumber;

    public SocialLoginRequest(Builder builder) {
        this.authType = builder.authType.getValue();
        this.refreshToken = builder.refreshToken;
        this.accessToken = builder.accessToken;
        this.uid = builder.uid;
        this.phoneNumber = builder.phoneNumber;
    }

    public static class Builder {
        private AuthType authType;
        private String refreshToken;
        private String accessToken;
        private String uid;
        private String phoneNumber;

        public Builder(String phoneNumber, AuthType authType) {
            if (!authType.isSocialType()) {
                throw new IllegalStateException("소셜 타입만 허용됩니다.");
            }
            this.phoneNumber = phoneNumber;
            this.authType = authType;
        }

        public Builder setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
            return this;
        }

        public Builder setAccessToken(String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        public Builder setUid(String uid) {
            this.uid = uid;
            return this;
        }

        public SocialLoginRequest build() {
            return new SocialLoginRequest(this);
        }
    }
}
