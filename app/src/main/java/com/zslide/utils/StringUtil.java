package com.zslide.utils;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by jdekim43 on 2016. 1. 13..
 */
public class StringUtil {

    public static Pattern getNamePattern() {
        // + 천지인
        return Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$");
    }

    public static Pattern getFamilyNamePattern() {
        // +천지인 + 하트
        return Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55\\u2661\\u2665]+$");
    }

    public static String plainVersion(String string) {
        String result = "";
        for (char i : string.toCharArray()) {
            if (NumberUtil.isNumber(i) || i == '.') {
                result += i;
            }
        }
        return result;
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.getDefault(), format, args);
    }
}
