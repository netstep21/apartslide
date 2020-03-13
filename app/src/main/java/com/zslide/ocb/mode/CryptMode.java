/**
 * @(#) CryptMode.java
 * Copyright 2010-2010 by  C&T Inc.
 * @SEC_TEST
 * @author
 * @MAKE 2010. 11. 21.
 * @EDIT
 */
package com.zslide.ocb.mode;

/*
 * CryptMode
 */
public interface CryptMode {
    int processBlock(byte[] in, int inOff, byte[] out, int outOff)
            throws IllegalStateException;


}
