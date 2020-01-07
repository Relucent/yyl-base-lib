package com.github.relucent.base.common.crypto.digest;

/**
 * 摘要算法工具类
 */
public class DigestUtil {

    public static byte[] md5(byte[] input) {
        return Md5.create().digest(input);
    }

    public static byte[] md5(String input) {
        return Md5.create().digest(input);
    }

    public static String md5Hex(String input) {
        return Md5.create().digestHex(input);
    }

    public static String md5Hex(byte[] input) {
        return Md5.create().digestHex(input);
    }
}
