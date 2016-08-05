package com.yunsoo.util;

/**
 * Created by:   Lijian
 * Created on:   2015/12/4
 * Descriptions:
 */
public final class HexStringUtils {

    private static final char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String encode(byte[] bytes) {
        char chars[] = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) {
            chars[index++] = HEX[b >> 4 & 0xf];
            chars[index++] = HEX[b & 0xf];
        }
        return new String(chars);
    }

    public static byte[] decode(CharSequence chars) {
        int length = chars.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("Hex-encoded string must have an even number of characters");
        }
        byte[] result = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            int msb = Character.digit(chars.charAt(i), 16);
            int lsb = Character.digit(chars.charAt(i + 1), 16);
            if (msb < 0 || lsb < 0) {
                throw new IllegalArgumentException("Non-hex character in input: " + chars);
            }
            result[i / 2] = (byte) ((msb << 4) | lsb);
        }
        return result;
    }
}
