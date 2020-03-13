/**
 * @(#) CBCMode.java
 * Copyright 2010-2010 by  C&T Inc.
 * @SEC_TEST
 * @author
 * @MAKE 2010. 11. 21.
 * @EDIT
 */
package com.zslide.ocb.mode;

import com.zslide.ocb.DataLengthException;
import com.zslide.ocb.Engine.Seed;


public class CBCMode implements CryptMode {
    private byte[] IV;
    private byte[] cbcV;
    private byte[] cbcNextV;

    private int blockSize;
    private boolean encrypting;
    private Seed seed;

    public CBCMode(Seed seed) {
        this.seed = seed;
        this.IV = seed.getIV();
        this.cbcV = this.IV;    //초기화 백터 할당
        this.blockSize = seed.getBlockSize();
        this.encrypting = seed.getCryptMode();
        this.cbcV = new byte[blockSize];
        this.cbcNextV = new byte[blockSize];

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

	    /*
         * XOR the cbcV and the input,
	     * then encrypt the cbcV
	     */
        for (int i = 0; i < blockSize; i++) {
            cbcV[i] ^= in[inOff + i];
        }

        int length = seed.processBlock(cbcV, 0, out, outOff);

	    /*
         * newInstance ciphertext to cbcV
	     */
        System.arraycopy(out, outOff, cbcV, 0, cbcV.length);

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

        System.arraycopy(in, inOff, cbcNextV, 0, blockSize);

        int length = seed.processBlock(in, inOff, out, outOff);

	    /*
         * XOR the cbcV and the output
	     */
        for (int i = 0; i < blockSize; i++) {
            out[outOff + i] ^= cbcV[i];
        }

	    /*
         * swap the back up buffer into next position
	     */
        byte[] tmp;

        tmp = cbcV;
        cbcV = cbcNextV;
        cbcNextV = tmp;

        return length;
    }
}
