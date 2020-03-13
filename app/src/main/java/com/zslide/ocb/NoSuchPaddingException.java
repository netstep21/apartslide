/**
 * @(#) NoSuchPaddingException.java
 * Copyright 2010-2010 by  C&T Inc.
 * @SEC_TEST
 * @author
 * @MAKE 2010. 11. 21.
 * @EDIT
 */
package com.zslide.ocb;

/*
 * NoSuchPaddingException
 */
public class NoSuchPaddingException extends RuntimeException {
    public NoSuchPaddingException() {
        super();
    }

    public NoSuchPaddingException(String s) {
        super(s);
    }

    public NoSuchPaddingException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchPaddingException(Throwable cause) {
        super(cause);
    }
}
