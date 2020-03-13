/**
 * @(#) SeedCrypt.java
 * Copyright 2010-2010 by  C&T Inc.
 * @SEC_TEST
 * @author
 * @MAKE 2010. 11. 21.
 * @EDIT
 */
package com.zslide.ocb;

import com.zslide.ocb.Engine.Seed;
import com.zslide.ocb.encoder.Base64;
import com.zslide.ocb.mode.CBCMode;
import com.zslide.ocb.mode.CFBMode;
import com.zslide.ocb.mode.CryptMode;
import com.zslide.ocb.mode.ECBMode;
import com.zslide.ocb.mode.OFBMode;

import java.io.UnsupportedEncodingException;

//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
/*
 * SeedCrypt
 */
public class SeedCrypt {
    public boolean ENCRYPT_MODE = true;
    public boolean DECRYPT_MODE = false;

    private String mode;
    private String pad;
    private byte[] key;

    private Seed seed;
    private CryptMode CMode;

    public SeedCrypt() {

    }

    public void init(boolean cryptMode, String mode, String pad, byte[] key) {
        this.mode = mode;
        this.pad = pad;
        this.key = key;
        this.seed = new Seed();

        seed.init(cryptMode, mode, pad, key);

        if (mode.equals(Seed.MODE_ECB)) {
            CMode = new ECBMode(seed);
        } else {
            throw new DataLengthException("initialisation vector(IV) must be the same length as block size");
        }
    }

    public void init(boolean cryptMode, String mode, String pad, byte[] key, byte[] iv) {
        this.mode = mode;
        this.pad = pad;
        this.key = key;
        this.seed = new Seed();

        seed.init(cryptMode, mode, pad, key, iv);

        if (mode.equals(Seed.MODE_CBC)) {
            CMode = new CBCMode(seed);
        } else if (mode.equals(Seed.MODE_CFB)) {
            CMode = new CFBMode(seed);
            seed.setCryptMode(true);
        } else if (mode.endsWith(Seed.MODE_OFB)) {
            CMode = new OFBMode(seed);
            seed.setCryptMode(true);
        } else {
            CMode = new ECBMode(seed);
        }
    }

    public String encrypt(String data) {
        return encrypt(data, null);
    }

    public String encrypt(String data, String encoding) {
        byte[] cipherTxt = null;

        try {
            cipherTxt = (encoding == null ? data.getBytes() : data.getBytes(encoding));
        } catch (UnsupportedEncodingException e) {
            return null;
        }

        return encrypt(cipherTxt);
    }

    public String encrypt(byte[] cipherTxt) {
        int cipherLen = cipherTxt.length;
        int blockLen = cipherLen / Seed.BLOCK_SIZE;    //원 데이터 블럭 길이
        int padLen = Seed.BLOCK_SIZE - cipherLen % Seed.BLOCK_SIZE;    //패딩될 길이

        if (padLen > 0) blockLen += 1;
        int allLen = blockLen * Seed.BLOCK_SIZE;

        if (padLen == 0) padLen = Seed.BLOCK_SIZE;

        // padding
        byte[] endBlock = new byte[Seed.BLOCK_SIZE - padLen];
        System.arraycopy(cipherTxt, (blockLen - 1) * Seed.BLOCK_SIZE, endBlock, 0, endBlock.length);


        byte[] padByte = null;
        if (pad.equals(Seed.PAD_PKCS)) {
            padByte = PKCSPading(endBlock);
        } else if (pad.equals(Seed.PAD_X923)) {
            padByte = X923Pading(endBlock);
        } else {
            padByte = noPadding(endBlock);
        }

        // padding byte add
        byte[] paddedTxt = new byte[allLen];
        System.arraycopy(cipherTxt, 0, paddedTxt, 0, cipherLen);
        // 원 데이터의 마지막 블럭에서 부터 패딩된 바이트를 덧붙인다.
        System.arraycopy(padByte, 0, paddedTxt, (blockLen - 1) * Seed.BLOCK_SIZE, padByte.length);

        // checked with iOS X923, PKCS
        /*
        for(int p=0;p<allLen;p++)
		{
			Log.d("key",String.format("%02X", paddedTxt[p]));
		}
		*/


        byte[] encTxt = new byte[allLen];
        int off = 0;
        while (off < blockLen * Seed.BLOCK_SIZE) {
            CMode.processBlock(paddedTxt, off, encTxt, off);
            off += Seed.BLOCK_SIZE;
        }
        //String encData2 = new String(encTxt);
        String encData = new String(Base64.encode(encTxt));

        return encData;
    }

    public String decrypt(String data) {
        return decrypt(data, null);
    }

    public String decrypt(byte[] encByte, String encoding) {
        byte[] decByte = new byte[encByte.length];
        byte[] cipherByte = null;
        int blockLen = encByte.length / Seed.BLOCK_SIZE;

        int off = 0;

        //decrypt
        while (off < blockLen * Seed.BLOCK_SIZE) {
            CMode.processBlock(encByte, off, decByte, off);

            off += Seed.BLOCK_SIZE;
        }

        // unpadding
        int padLen = 0;
        if (pad.equals(Seed.PAD_PKCS) || pad.equals(Seed.PAD_X923)) {
            padLen = (int) decByte[decByte.length - 1];
            cipherByte = new byte[decByte.length - padLen];
            System.arraycopy(decByte, 0, cipherByte, 0, decByte.length - padLen);
        } else {
            padLen = 0;    //알수 없음. (null 값으로 채워져 있음)
            cipherByte = new byte[decByte.length];
            System.arraycopy(decByte, 0, cipherByte, 0, decByte.length);
        }

        try {
            return encoding == null ? (new String(cipherByte)).trim() : (new String(cipherByte, encoding)).trim();
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public String decrypt(String data, String encoding) {
        byte[] encByte = Base64.decode(data);
        return decrypt(encByte, encoding);
    }

    public byte[] PKCSPading(byte[] endBlock) {
        byte[] buf = null;

        if (endBlock.length == Seed.BLOCK_SIZE) {
            buf = new byte[Seed.BLOCK_SIZE * 2];    //마지막 블럭이 블럭사이즈와 동일하면 *2배 만큼 블럭 처리
        } else {
            buf = new byte[Seed.BLOCK_SIZE];
        }

        System.arraycopy(endBlock, 0, buf, 0, endBlock.length);

        byte code = (byte) (Seed.BLOCK_SIZE - endBlock.length);
        int off = endBlock.length;

        while (off < Seed.BLOCK_SIZE) {
            buf[off] = code;
            off++;
        }

        return buf;
    }

    public byte[] PKCSUnPadding(byte[] endBlock) {
        byte buf[] = new byte[Seed.BLOCK_SIZE];

        int count = buf[Seed.BLOCK_SIZE - 1] & 0xff;
        int endLen = Seed.BLOCK_SIZE - count;

        System.arraycopy(buf, 0, endBlock, 0, endLen);

        return buf;
    }

    public byte[] X923Pading(byte[] endBlock) {
        byte[] buf = null;

        if (endBlock.length == Seed.BLOCK_SIZE) {
            buf = new byte[Seed.BLOCK_SIZE * 2];    //마지막 블럭이 블럭사이즈와 동일하면 *2배 만큼 블럭 처리
        } else {
            buf = new byte[Seed.BLOCK_SIZE];
        }

        System.arraycopy(endBlock, 0, buf, 0, endBlock.length);

        byte code = (byte) (Seed.BLOCK_SIZE - endBlock.length);

        int off = endBlock.length;

        while (off < Seed.BLOCK_SIZE) {
            if (off == (Seed.BLOCK_SIZE - 1))
                buf[off] = code;
            else
                buf[off] = 0x00;
            off++;
        }

        return buf;
    }

    public byte[] X923UnPadding(byte[] endBlock) {
        byte buf[] = new byte[Seed.BLOCK_SIZE];

        int count = buf[Seed.BLOCK_SIZE - 1] & 0xff;
        int endLen = Seed.BLOCK_SIZE - count;

        System.arraycopy(buf, 0, endBlock, 0, endLen);

        return buf;
    }

    public byte[] noPadding(byte[] endBlock) {
        byte[] buf = new byte[Seed.BLOCK_SIZE];

        int off = endBlock.length;

        while (off < Seed.BLOCK_SIZE) {
            buf[off] = 0x00;
            off++;
        }

        return buf;
    }
}
