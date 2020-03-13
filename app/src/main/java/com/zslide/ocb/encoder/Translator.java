/**
 * @(#) Translator.java
 * Copyright 2004-2008 by  Famz Inc.
 * @BoCShop
 * @author JANGHASO, janghaso@famz.co.kr
 * @MAKE 2008. 10. 10
 * @EDIT
 */
package com.zslide.ocb.encoder;

/**
 * general interface for an translator.
 */
public interface Translator {
    /**
     * size of the output block on encoding produced by getDecodedBlockSize()
     * bytes.
     */
    int getEncodedBlockSize();

    int encode(byte[] in, int inOff, int length, byte[] out, int outOff);

    /**
     * size of the output block on decoding produced by getEncodedBlockSize()
     * bytes.
     */
    int getDecodedBlockSize();

    int decode(byte[] in, int inOff, int length, byte[] out, int outOff);
}
