package com.zslide.ocb.util;

public class ByteUtil {
    /**
     * Byte 의 Hex 값을 출력한다.
     */
    public static void printHexString(byte[] arr) {
        String hex = "";
        String _tmp = "";

        for (int i = 0; i < arr.length; i++) {
            _tmp = Integer.toHexString(arr[i] & 0xff).toUpperCase();
            if (_tmp.length() == 1)
                _tmp = "0" + _tmp;

            hex += "0x" + _tmp + " ";

            if (((i + 1) % 10) == 0)
                hex += "\n";
        }

        System.out.println("-------------------------------------------------");
        System.out.println("\t Byte Length = " + arr.length);
        System.out.println("-------------------------------------------------");
        System.out.println(hex);
        System.out.println("-------------------------------------------------");
    }


    /**
     * Byte 의 Hex 값을 출력한다.
     */
    public static String getHexString(byte[] arr) {
        return getHexString(arr, " ");
    }

    public static String getHexString(byte _byte) {
        String hex = Integer.toHexString(_byte & 0xff).toUpperCase();
        if (hex.length() == 1) hex = "0" + hex;
        hex = "0x" + hex;

        return hex;
    }

    public static String getHexString(byte[] arr, String div) {
        String hex = "";
        String _tmp = "";

        for (int i = 0; i < arr.length; i++) {
            _tmp = Integer.toHexString(arr[i] & 0xff).toUpperCase();
            if (_tmp.length() == 1)
                _tmp = "0" + _tmp;

            hex += "0x" + _tmp;

            if (i < arr.length - 1)
                hex += div;
        }

        return hex;
    }

    /**
     * 주어진 바이트 배열을 원 바이트 배열을 추가한다.
     * 리턴되는 전체 바이트 배열의 크기는 원래 바이트배열의 크기에 len으로 주어진 길이의 크기이다.
     * 만약 src 바이트 배열의 크기가 len 보다 작거나 같으면면 src 전체가 복사되고,
     * src 바이트 배열의 크기가 len 보다 크면 src 배열에서 len 의 길이 만큼만 복사된다.
     * add된 전체 바이트 배열의 크기는 dest.length + len 이다.
     * (src 배열 크기와 len 사이에서의 예외 상황은 없다.)
     *
     * @param dest
     * @param src
     * @param len
     * @return
     */
    public static byte[] add(byte[] dest, byte[] src, int len) {
        byte[] tmpBytes = new byte[dest.length + len];
        System.arraycopy(dest, 0, tmpBytes, 0, dest.length);
        if (src.length > len) {
            System.arraycopy(src, 0, tmpBytes, dest.length, len);
        } else {
            System.arraycopy(src, 0, tmpBytes, dest.length, src.length);
        }

        return tmpBytes;
    }

    public static byte[] add(byte[] dest, byte[] src) {
        return add(dest, src, src.length);
    }

}
