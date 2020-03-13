package com.zslide.data.remote.exception;

/**
 * Created by chulwoo on 2018. 1. 11..
 *
 * 로그인 실패 시 발생하는 에러
 */

public class LoginFailureException extends RuntimeException {

    public LoginFailureException(String message) {
        super(message);
    }
}
