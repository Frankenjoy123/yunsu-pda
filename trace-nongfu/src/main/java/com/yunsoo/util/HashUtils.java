package com.yunsoo.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by  : Lijian
 * Created on  : 2015/4/21
 * Descriptions:
 */
public final class HashUtils {

    public static byte[] md5(byte[] src) {
        return hash(src, "MD5");
    }

    public static byte[] sha1(byte[] src) {
        return hash(src, "SHA-1");
    }

    public static byte[] sha256(byte[] src) {
        return hash(src, "SHA-256");
    }


    public static byte[] md5(String src) {
        return hash(src, "MD5");
    }

    public static byte[] sha1(String src) {
        return hash(src, "SHA-1");
    }

    public static byte[] sha256(String src) {
        return hash(src, "SHA-256");
    }

    public static byte[] md5(String src, String charset) throws UnsupportedEncodingException {
        return hash(src, charset, "MD5");
    }


    public static String md5HexString(String src) {
        return toHexString(md5(src));
    }

    public static String sha1HexString(String src) {
        return toHexString(sha1(src));
    }

    public static String sha256HexString(String src) {
        return toHexString(sha256(src));
    }

    public static String md5HexString(String src, String charset) throws UnsupportedEncodingException {
        return toHexString(md5(src, charset));
    }


    public static byte[] hash(String src, String algorithm) {
        return hash(src.getBytes(), algorithm);
    }

    public static byte[] hash(byte[] src, String algorithm) {
        MessageDigest mdInstance;
        try {
            mdInstance = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("hash algorithm not found: " + algorithm);
        }
        return mdInstance.digest(src);
    }

    public static byte[] hash(String src, String charset, String algorithm) throws UnsupportedEncodingException {
        return hash(src.getBytes(charset), algorithm);
    }


    private static String toHexString(byte[] bytes) {
        return HexStringUtils.encode(bytes);
    }
}
