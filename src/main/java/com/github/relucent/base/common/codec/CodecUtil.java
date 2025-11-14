package com.github.relucent.base.common.codec;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 编码解码工具类
 */
public class CodecUtil {

    private static final String DEFAULT_URI_ENCODING = "UTF-8";

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected CodecUtil() {
    }

    /**
     * 将字节数组转换为十六进制值字符串
     * @param data 字节数组
     * @return 十六进制字符字符串
     */
    public static String encodeHexString(byte[] data) {
        return Hex.encodeHexString(data);
    }

    /**
     * 将十六进制字符数组转换为字符串
     * @param data 包含十六进制数字的字符串
     * @return 字节数组，包含从所提供的字符串中解码的二进制数据
     */
    public static byte[] decodeHex(String data) {
        return Hex.decodeHex(data);
    }

    /**
     * 将字节数组编码成Base64字符串
     * @param data 字节数组
     * @return Base64字符串
     */
    public static String encodeBase64(byte[] data) {
        return Base64.encode(data);
    }

    /**
     * 将字节数组编码成 URL安全Base64字符串
     * @param data 字节数组
     * @return Base64字符串
     */
    public static String encodeUrlBase64(byte[] data) {
        return Base64.encodeUrl(data);
    }

    /**
     * 将字节数组编码成 MIME Base64字符串
     * @param data 字节数组
     * @return Base64字符串
     */
    public static String encodeMimeBase64(byte[] data) {
        return Base64.encodeMime(data);
    }

    /**
     * 将Base64字符串解码成字节数组
     * @param base64 Base64字符串
     * @return 字节数组
     */
    public static byte[] decodeBase64(String base64) {
        return Base64.decode(base64);
    }

    /**
     * 将MIME Base64字符串解码成字节数组
     * @param base64 MIME Base64字符串
     * @return 字节数组
     */
    public static byte[] decodeMimeBase64(String base64) {
        return Base64.decodeMime(base64);
    }

    /**
     * 将URL安全Base64字符串解码成字节数组
     * @param base64 Base64字符串
     * @return 字节数组
     */
    public static byte[] decodeUrlBase64(String base64) {
        return Base64.decodeUrl(base64);
    }

    /**
     * URL 编码, Encode默认为UTF-8.
     * @param input 需要编码的字符串
     * @return 编码后字符串
     */
    public static String encodeURI(String input) {
        try {
            return URLEncoder.encode(input, DEFAULT_URI_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }

    /**
     * URL 解码, Encode默认为UTF-8.
     * @param input 需要解码的字符串
     * @return 解码后字符串
     */
    public static String decodeURI(String input) {
        try {
            return URLDecoder.decode(input, DEFAULT_URI_ENCODING);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("Unsupported Encoding Exception", e);
        }
    }

    /**
     * 将字符串解码成字节数组，可支持的编码如下： <br>
     * 1. Hex（16进制）编码 <br>
     * 2. Base64编码 <br>
     * @param key 被解码的密钥字符串
     * @return 密钥
     */
    public static byte[] decodeByteArray(String key) {
        return Hex.isHex(key) ? Hex.decodeHex(key) : Base64.decode(key);
    }
}
