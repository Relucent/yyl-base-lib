package com.github.relucent.base.common.codec;

import com.github.relucent.base.common.constant.ArrayConstant;
import com.github.relucent.base.common.constant.StringConstant;

/**
 * Base64工具类，提供Base64的编码和解码功能。<br>
 * Base64是一种用64个字符来表示任意二进制数据的方法，常用于在URL、Cookie、网页中传输少量二进制数据。<br>
 * Base64要求把每3个8Bit的字节转换为4个6Bit的字节（3*8=4*6=24），然后把6Bit再添两位高位0，组成四个8Bit的字节，转换后的字符串理论上将要比原来增加1/3。<br>
 */
public class Base64 {

    /**
     * 标准 Base64 编码
     * @param data 字节数组
     * @return Base64字符串
     */
    public static String encode(byte[] data) {
        if (data == null || data.length == 0) {
            return StringConstant.EMPTY;
        }
        return java.util.Base64.getEncoder().encodeToString(data);
    }

    /**
     * URL 安全 Base64 编码
     * @param data 字节数组
     * @return Base64字符串
     */
    public static String encodeUrl(byte[] data) {
        if (data == null || data.length == 0) {
            return StringConstant.EMPTY;
        }
        return java.util.Base64.getUrlEncoder().encodeToString(data);
    }

    /**
     * MIME Base64 编码 (每76个字符换行)
     * @param data 字节数组
     * @return Base64字符串
     */
    public static String encodeMime(byte[] data) {
        if (data == null || data.length == 0) {
            return StringConstant.EMPTY;
        }
        return java.util.Base64.getMimeEncoder().encodeToString(data);
    }

    /**
     * 安全标准 Base64 解码
     * @param base64 Base64字符串
     * @return 字节数组
     */
    public static byte[] decode(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return ArrayConstant.EMPTY_BYTE_ARRAY;
        }
        try {
            return java.util.Base64.getDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            return ArrayConstant.EMPTY_BYTE_ARRAY;
        }
    }

    /**
     * 安全 URL Base64 解码
     * @param base64 Base64字符串
     * @return 字节数组
     */
    public static byte[] decodeUrl(String base64) {
        if (base64 == null || base64.isEmpty())
            return ArrayConstant.EMPTY_BYTE_ARRAY;
        try {
            return java.util.Base64.getUrlDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            return ArrayConstant.EMPTY_BYTE_ARRAY;
        }
    }

    /**
     * MIME Base64 解码
     * @param base64 Base64字符串
     * @return 字节数组
     */
    public static byte[] decodeMime(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return ArrayConstant.EMPTY_BYTE_ARRAY;
        }
        try {
            return java.util.Base64.getMimeDecoder().decode(base64);
        } catch (IllegalArgumentException e) {
            return ArrayConstant.EMPTY_BYTE_ARRAY;
        }
    }
}
