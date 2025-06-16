package com.github.relucent.base.common.crypto.mac;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.github.relucent.base.common.codec.Base64;
import com.github.relucent.base.common.codec.Hex;
import com.github.relucent.base.common.crypto.ProviderFactory;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * 认证算法（MAC）工具类，支持多种 HMAC 算法，包括国密 SM3。<br>
 * 提供返回字节数组、十六进制字符串和 Base64 字符串的通用方法。<br>
 */
public class MacUtil {

    // =================================Methods================================================
    /**
     * 使用指定算法和密钥对数据进行 HMAC 运算，返回原始字节数组。
     * @param data      要签名的消息
     * @param key       密钥（对称）
     * @param algorithm 使用的 HMAC 算法
     * @return HMAC 值的字节数组
     */
    public static byte[] hmac(byte[] data, byte[] key, HmacAlgorithm algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm.getAlgorithm(), ProviderFactory.getProvider());
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, algorithm.getAlgorithm());
            mac.init(secretKeySpec);
            return mac.doFinal(data);
        } catch (Exception e) {
            throw new RuntimeException("HMAC calculation failed", e);
        }
    }

    /**
     * 使用指定算法和密钥对数据进行 HMAC 运算，返回原始字节数组。
     * @param data      要签名的消息
     * @param key       密钥（对称）
     * @param algorithm 使用的 HMAC 算法
     * @return HMAC 值的字节数组
     */
    public static byte[] hmac(String data, String key, HmacAlgorithm algorithm) {
        return hmac(StringUtil.getBytes(data), StringUtil.getBytes(key), algorithm);
    }

    /**
     * 返回十六进制格式的 HMAC 字符串，常用于日志或调试输出。
     * @param data      要签名的消息
     * @param key       密钥
     * @param algorithm 使用的 HMAC 算法
     * @return HMAC 值的十六进制表示
     */
    public static String hmacHex(String data, String key, HmacAlgorithm algorithm) {
        byte[] result = hmac(data, key, algorithm);
        return Hex.encodeHexString(result);
    }

    /**
     * 返回 Base64 格式的 HMAC 字符串，适合 Web、JWT 等需要可打印编码的场景。
     * @param data      要签名的消息
     * @param key       密钥
     * @param algorithm 使用的 HMAC 算法
     * @return HMAC 值的 Base64 编码
     */
    public static String hmacBase64(String data, String key, HmacAlgorithm algorithm) {
        byte[] result = hmac(data, key, algorithm);
        return Base64.encode(result);
    }

    // =================================Constructors===========================================
    /**
     * 工具类。私有构造函数
     */
    public MacUtil() {
    }
}
