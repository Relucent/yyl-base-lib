package com.github.relucent.base.common.crypto.digest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;

import com.github.relucent.base.common.codec.Hex;
import com.github.relucent.base.common.crypto.ProviderFactory;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * 摘要算法工具类
 */
public class DigestUtil {

    // =================================Fields==================================================
    private static final int STREAM_BUFFER_LENGTH = 4096;

    // =================================Constructors============================================
    /** 工具类，私有构造 */
    protected DigestUtil() {
    }

    // =================================Digest=================================================
    /**
     * 使用指定的信息摘要算法计算数据摘要
     * @param algorithm 消息摘要算法
     * @param input     需要计算摘要的字节数组
     * @return 数据摘要
     */
    public static byte[] digest(final DigestAlgorithm algorithm, final byte[] input) {
        return getDigest(algorithm).digest(input);
    }

    /**
     * 使用指定的信息摘要算法计算数据摘要
     * @param algorithm 消息摘要算法
     * @param input     需要计算摘要的字符串
     * @return 数据摘要
     */
    public static byte[] digest(final DigestAlgorithm algorithm, final String input) {
        return digest(algorithm, StringUtil.getBytes(input));
    }

    /**
     * 使用指定的信息摘要算法计算数据摘要（16进制字符串表示）
     * @param algorithm 消息摘要算法
     * @param input     需要计算摘要的字节数组
     * @return 数据摘要（16进制字符串表示）
     */
    public static String digestHex(final DigestAlgorithm algorithm, final byte[] input) {
        return Hex.encodeHexString(digest(algorithm, input));
    }

    /**
     * 使用指定的信息摘要算法计算数据摘要（16进制字符串表示）
     * @param algorithm 消息摘要算法
     * @param input     需要计算摘要的字符串
     * @return 数据摘要（16进制字符串表示）
     */
    public static String digestHex(final DigestAlgorithm algorithm, final String input) {
        return digestHex(algorithm, StringUtil.getBytes(input));
    }

    /**
     * 读取字节数组并返回数据摘要
     * @param messageDigest 使用的摘要算法工具类
     * @param input         字节数组
     * @return 数据摘要
     */
    public static byte[] digest(final MessageDigest messageDigest, final byte[] input) {
        return messageDigest.digest(input);
    }

    /**
     * 读取字节缓冲（ByteBuffer）并返回数据摘要
     * @param messageDigest 使用的摘要算法工具类
     * @param input         需要计算摘要的字节缓冲
     * @return 数据摘要
     */
    public static byte[] digest(final MessageDigest messageDigest, final ByteBuffer input) {
        messageDigest.update(input);
        return messageDigest.digest();
    }

    /**
     * 读取文件并计算数据摘要
     * @param messageDigest 使用的摘要算法工具类
     * @param file          待计算摘要文件
     * @return 数据摘要
     * @throws IOException 读取流出现异常
     */
    public static byte[] digest(final MessageDigest messageDigest, final File file) throws IOException {
        return updateDigest(messageDigest, file).digest();
    }

    /**
     * 读取输入流（InputStream）并返回数据摘要
     * @param messageDigest 使用的摘要算法工具类
     * @param input         输入流
     * @return 数据摘要
     * @throws IOException 读取流出现异常
     */
    public static byte[] digest(final MessageDigest messageDigest, final InputStream input) throws IOException {
        return updateDigest(messageDigest, input).digest();
    }

    /**
     * 读取文件并更新数据摘要
     * @param messageDigest 使用的摘要算法工具类
     * @param file          待计算摘要文件
     * @return 消息摘要工具类
     * @throws IOException 读取流出现异常
     */
    public static MessageDigest updateDigest(final MessageDigest messageDigest, final File file) throws IOException {
        try (final BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
            return updateDigest(messageDigest, inputStream);
        }
    }

    /**
     * 读取数据流（InputStream）并更新数据摘要
     * @param messageDigest 使用的摘要算法工具类
     * @param inputStream   输入流
     * @return 消息摘要工具类
     * @throws IOException 流读取中出现异常
     */
    public static MessageDigest updateDigest(final MessageDigest messageDigest, final InputStream inputStream)
            throws IOException {
        final byte[] buffer = new byte[STREAM_BUFFER_LENGTH];
        int read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH);

        while (read > -1) {
            messageDigest.update(buffer, 0, read);
            read = inputStream.read(buffer, 0, STREAM_BUFFER_LENGTH);
        }

        return messageDigest;
    }

    // =================================MD5====================================================
    public static byte[] md5(byte[] input) {
        return digest(DigestAlgorithm.MD5, input);
    }

    public static byte[] md5(String input) {
        return digest(DigestAlgorithm.MD5, input);
    }

    public static String md5Hex(String input) {
        return digestHex(DigestAlgorithm.MD5, input);
    }

    public static String md5Hex(byte[] input) {
        return digestHex(DigestAlgorithm.MD5, input);
    }

    // =================================SHA256=================================================
    public static byte[] sha256(byte[] input) {
        return digest(DigestAlgorithm.SHA_256, input);
    }

    public static byte[] sha256(String input) {
        return digest(DigestAlgorithm.SHA_256, input);
    }

    public static String sha256Hex(byte[] input) {
        return digestHex(DigestAlgorithm.SHA_256, input);
    }

    public static String sha256Hex(final String input) {
        return digestHex(DigestAlgorithm.SHA_256, input);
    }

    // =================================SM3====================================================
    public static byte[] sm3(byte[] input) {
        return digest(DigestAlgorithm.SM3, input);
    }

    public static byte[] sm3(String input) {
        return digest(DigestAlgorithm.SM3, input);
    }

    public static String sm3Hex(String input) {
        return digestHex(DigestAlgorithm.SM3, input);
    }

    public static String sm3Hex(byte[] input) {
        return digestHex(DigestAlgorithm.SM3, input);
    }

    // =================================MessageDigest==========================================
    /**
     * 获得指定的{@code MessageDigest}
     * @param algorithm 消息摘要算法
     * @return 消息摘要处理器
     * @see MessageDigest#getInstance(String)
     * @throws IllegalArgumentException 如果不存在该算法的实现
     */
    public static MessageDigest getDigest(DigestAlgorithm algorithm) {
        return getDigest(algorithm.getValue());
    }

    /**
     * 获得指定的{@code MessageDigest}
     * @param algorithm 消息摘要算法
     * @return 消息摘要处理器
     * @see MessageDigest#getInstance(String)
     * @throws IllegalArgumentException 如果不存在该算法的实现
     */
    public static MessageDigest getDigest(String algorithm) {
        try {
            Provider provider = ProviderFactory.getProvider();
            if (provider == null) {
                return MessageDigest.getInstance(algorithm);
            } else {
                return MessageDigest.getInstance(algorithm, provider);
            }
        } catch (final NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
