package com.zslide.firebase;

/**
 * Created by chulwoo on 16. 1. 6..
 * <p>
 * GCM 등록을 위한 정보.
 * 디바이스 id와 GCM 토큰을 갖고 있다.
 */
public class MyFirebaseInstanceID {

    public String deviceId;
    public String token;

    public MyFirebaseInstanceID(String deviceId, String token) {
        this.deviceId = deviceId;
        this.token = token;
    }
}
