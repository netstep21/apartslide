/**
 * @(#) NoSuchModeException.java
 * Copyright 2010-2010 by  C&T Inc.
 * @SEC_TEST
 * @author
 * @MAKE 2010. 11. 21.
 * @EDIT
 */
package com.zslide.ocb;

/*
 * NoSuchModeException
 */
public class NoSuchModeException extends RuntimeException {
    public NoSuchModeException() {
        super();
    }

    public NoSuchModeException(String s) {
        super(s);
    }

    public NoSuchModeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchModeException(Throwable cause) {
        super(cause);
    }
}
