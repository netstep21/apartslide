/**
 * @(#) ECBMode.java
 * Copyright 2010-2010 by  C&T Inc.
 * @SEC_TEST
 * @author
 * @MAKE 2010. 11. 21.
 * @EDIT
 */
package com.zslide.ocb.mode;

import com.zslide.ocb.DataLengthException;
import com.zslide.ocb.Engine.Seed;

/*
 * ECBMode
 */
public class ECBMode implements CryptMode {
    private int blockSize;
    private boolean encrypting;
    private Seed seed;

    public ECBMode(Seed seed) {
        this.seed = seed;
        this.blockSize = seed.getBlockSize();
        this.encrypting = seed.getCryptMode();

    }

    public int processBlock(
            byte[] in,
            int inOff,
            byte[] out,
            int outOff)
            throws IllegalStateException {
        return (encrypting) ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
    }

    private int encryptBlock(
            byte[] in,
            int inOff,
            byte[] out,
            int outOff)
            throws IllegalStateException {
        if ((inOff + blockSize) > in.length) {
            throw new DataLengthException("input buffer too short");
        }

        int length = seed.processBlock(in, inOff, out, outOff);

        System.arraycopy(out, outOff, in, inOff, blockSize);

        return length;
    }

    private int decryptBlock(
            byte[] in,
            int inOff,
            byte[] out,
            int outOff)
            throws IllegalStateException {
        if ((inOff + blockSize) > in.length) {
            throw new DataLengthException("input buffer too short");
        }

        System.arraycopy(in, inOff, out, outOff, blockSize);

        int length = seed.processBlock(in, inOff, out, outOff);

        return length;
    }
}
