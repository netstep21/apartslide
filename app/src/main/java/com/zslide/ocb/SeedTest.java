/**
 * @(#) SeedTest.java
 * Copyright 2010-2010 by  C&T Inc.
 * @SEC_TEST
 * @author
 * @MAKE 2010. 11. 22.
 * @EDIT
 */
package com.zslide.ocb;

import com.zslide.ocb.Engine.Seed;

/*
 * SeedTest
 */
public class SeedTest {
    public SeedTest() {
        seedTest1();
        seedTest2();
    }

    public static void main(String args[]) {
        new SeedTest();

    }

    public void seedTest1() {
        System.out.println("==========================================");
        System.out.println("[SEED] ECB Mode Test (ECB Only)");
        System.out.println("==========================================");
        final byte[] iv = new byte[16];
        String _mode = Seed.MODE_ECB;//Seed.MODE_CBC;//Seed.MODE_CFB;//Seed.MODE_OFB
        String _pad = Seed.PAD_X923;//Seed.PAD_PKCS;//Seed.PAD_NO(사용하지 말것)
        String _key = "1234567890123456";    //key 값은 반드시 16자리
        String data = "암복호화 테스트 해봅시다.^^";

        SeedCrypt encrypter = new SeedCrypt();    //암호화용
        SeedCrypt decrypter = new SeedCrypt();    //복호화용

        //ECB 모드가 아닌 경우는 iv 파라미터 값 필수
        encrypter.init(encrypter.ENCRYPT_MODE, _mode, _pad, _key.getBytes());
        String encStr = encrypter.encrypt(data);

        System.out.println("Enc Data = " + encStr);

        decrypter.init(encrypter.DECRYPT_MODE, _mode, _pad, _key.getBytes());
        String decStr = decrypter.decrypt(encStr);

        System.out.println("Dec Data = " + decStr.trim());
    }

    public void seedTest2() {
        System.out.println("==========================================");
        System.out.println("[SEED] NOT ECB Mode Test (CBC/CFB/OFB)");
        System.out.println("==========================================");

        final byte[] iv = new byte[16];
        String _mode = Seed.MODE_CBC;//Seed.MODE_CBC;//Seed.MODE_CFB;//Seed.MODE_OFB
        String _pad = Seed.PAD_PKCS;//Seed.PAD_PKCS;//Seed.PAD_NO(사용하지 말것)
        String _key = "1234567890123456";
        String data = "암복호화 테스트 해봅시다.^^";

        SeedCrypt encrypter = new SeedCrypt();    //암호화용
        SeedCrypt decrypter = new SeedCrypt();    //복호화용

        //iv 파라미터 필수
        encrypter.init(encrypter.ENCRYPT_MODE, _mode, _pad, _key.getBytes(), iv);
        String encStr = encrypter.encrypt(data);

        System.out.println("Enc Data = " + encStr);

        decrypter.init(encrypter.DECRYPT_MODE, _mode, _pad, _key.getBytes(), iv);
        String decStr = decrypter.decrypt(encStr);

        System.out.println("Dec Data = " + decStr.trim());
    }
}
