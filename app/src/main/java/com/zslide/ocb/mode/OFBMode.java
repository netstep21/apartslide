/**
 * @(#) OFBMode.java
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
 * OFBMode
 */
public class OFBMode implements CryptMode {
    //private byte[]          IV;
    private byte[] ofbV;
    private byte[] ofbOutV;

    private int blockSize;
    private Seed seed;

    public OFBMode(Seed seed) {
        this.seed = seed;
        this.blockSize = seed.getBlockSize();
        //this.IV = seed.getIV();
        this.ofbV = new byte[seed.getBlockSize()];//seed.getIV();
        System.arraycopy(seed.getIV(), 0, ofbV, 0, seed.getIV().length);
        this.ofbOutV = new byte[seed.getBlockSize()];
    }


    public int processBlock(
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

        seed.processBlock(ofbV, 0, ofbOutV, 0);


        //
        // XOR the ofbV with the plaintext producing the cipher text (and
        // the next input block).
        //
        for (int i = 0; i < blockSize; i++) {
            out[outOff + i] = (byte) (ofbOutV[i] ^ in[inOff + i]);
        }

        //
        // change over the input block.
        //
        System.arraycopy(ofbV, blockSize, ofbV, 0, ofbV.length - blockSize);
        System.arraycopy(ofbOutV, 0, ofbV, ofbV.length - blockSize, blockSize);

        return blockSize;
    }

    public void reset() {
        // System.arraycopy(IV, 0, ofbV, 0, IV.length);
    }
}
