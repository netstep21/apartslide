package com.zslide.data.remote.exception;

/**
 * Created by chulwoo on 2018. 1. 11..
 * <p>
 * 회원이 아닐 경우 발생하는 에러
 */

public class UnsignedUserException extends RuntimeException {

    public UnsignedUserException() {
    }

    public UnsignedUserException(String message) {
        super(message);
    }
}
