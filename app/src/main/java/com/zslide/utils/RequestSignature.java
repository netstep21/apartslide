package com.zslide.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

/**
 * Created by chulwoo on 2018. 1. 8..
 */

public class RequestSignature {

    private static final char[] APP_KEY = {
            'z', 's', 'l', 'i', 'd', 'e',
            '*', '#', '(', '*', '$', '2',
            '?', '?', 's', 'k', 'l', 'd',
            'f', 'j', '!', '@', '!', 'k',
            'w', '.', 'r', 'p', 'i', '4',
            'p', 's', '3', '$', 'e', 'w',
            'r', '3', '#', ')', 'a', 's',
            'd', 'f', '3', '4', '2', '3',
            'd', 'd'};

    public static String get(long timestamp) {
        StringTokenizer values = new StringTokenizer(new String(APP_KEY), ".");
        String key = values.nextToken() + timestamp + values.nextToken();
        String MD5;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(key.getBytes());
            byte bytes[] = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            MD5 = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            MD5 = null;
        }
        return MD5;
    }
}
