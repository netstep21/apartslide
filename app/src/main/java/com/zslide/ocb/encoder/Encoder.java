/**
 * @(#) Encoder.java
 * Copyright 2004-2008 by  Famz Inc.
 * @BoCShop
 * @author JANGHASO, janghaso@famz.co.kr
 * @MAKE 2008. 10. 10
 * @EDIT
 */
package com.zslide.ocb.encoder;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Encode and decode byte arrays (typically from binary to 7-bit ASCII
 * encodings).
 */
public interface Encoder {
    int encode(byte[] data, int off, int length, OutputStream out) throws IOException;

    int decode(byte[] data, int off, int length, OutputStream out) throws IOException;

    int decode(String data, OutputStream out) throws IOException;
}
