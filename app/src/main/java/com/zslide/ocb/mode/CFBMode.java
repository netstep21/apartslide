/**
 * @(#) CFBMode.java
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
 * CFBMode
 */
public class CFBMode implements CryptMode {
    private byte[] IV;
    private byte[] cfbV;
    private byte[] cfbOutV;

    private int blockSize;
    private boolean encrypting;
    private Seed seed;

    public CFBMode(Seed seed /*, int bitBlockSize*/) {
        this.seed = seed;
        this.encrypting = seed.getCryptMode();
        this.blockSize = seed.getBlockSize();
        this.cfbV = new byte[seed.getBlockSize()];//seed.getIV();
        System.arraycopy(seed.getIV(), 0, cfbV, 0, seed.getIV().length);
        this.cfbOutV = new byte[seed.getBlockSize()];
    }

    public int processBlock(
            byte[] in,
            int inOff,
            byte[] out,
            int outOff)
            throws IllegalStateException {
        return (encrypting) ? encryptBlock(in, inOff, out, outOff) : decryptBlock(in, inOff, out, outOff);
    }


    public int encryptBlock(
            byte[] in,
            int inOff,
            byte[] out,
            int outOff)
            throws IllegalStateException {
        if ((inOff + blockSize) > in.length) {
            throw new DataLengthException("input buffer too short");
        }

        if ((outOff + blockSize) > out.length) {
            throw new DataLengthException("output buffer too short");
        }

        seed.processBlock(cfbV, 0, cfbOutV, 0);

        //
        // XOR the cfbV with the plaintext producing the cipher text
        //
        for (int i = 0; i < blockSize; i++) {
            out[outOff + i] = (byte) (cfbOutV[i] ^ in[inOff + i]);
        }

        //
        // change over the input block.
        //
        System.arraycopy(cfbV, blockSize, cfbV, 0, cfbV.length - blockSize);
        System.arraycopy(out, outOff, cfbV, cfbV.length - blockSize, blockSize);

        return blockSize;
    }

    public int decryptBlock(
            byte[] in,
            int inOff,
            byte[] out,
            int outOff)
            throws IllegalStateException {
        if ((inOff + blockSize) > in.length) {
            throw new DataLengthException("input buffer too short");
        }

        if ((outOff + blockSize) > out.length) {
            throw new DataLengthException("output buffer too short");
        }

        seed.processBlock(cfbV, 0, cfbOutV, 0);

        //
        // change over the input block.
        //
        System.arraycopy(cfbV, blockSize, cfbV, 0, cfbV.length - blockSize);
        System.arraycopy(in, inOff, cfbV, cfbV.length - blockSize, blockSize);

        //
        // XOR the cfbV with the plaintext producing the plain text
        //
        for (int i = 0; i < blockSize; i++) {
            out[outOff + i] = (byte) (cfbOutV[i] ^ in[inOff + i]);
        }

        return blockSize;
    }
}
