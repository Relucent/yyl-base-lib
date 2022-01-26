package com.github.relucent.base.common.identifier;

import java.security.SecureRandom;
import java.util.Random;

/**
 * 一种小巧、安全、URL友好、唯一的 JavaScript 字符串ID生成器。<br>
 * NanoID 与 UUID v4 (基于随机) 相当。 它们在 ID 中有相似数量的随机位 (NanoID 为126，UUID 为122),因此它们的冲突概率相似。<br>
 * 优势：<br>
 * NanoID 比 UUID 更加紧凑，使用更大的字母表（A-Za-z0-9_-）。 因此，ID 大小从36个符号减少到21个符号。<br>
 * NanoID使用URL友好字符（A-Za-z0-9_-）。非常适合web应用程序中的唯一标识符。<br>
 * 劣势：<br>
 * 默认的 NanoID 中包含了字母的大小写，在大小写不敏感的情况下（例如对属性大小写不敏感的数据库），冲突概率会增加，但是可以通过自定义字母表以及增加长度，对该问题进行优化<br>
 * 另外， NanoID生成是无序的，对聚类索引的数据列（B-TREE索引列）并不友好 <br>
 * 实现逻辑参考：<br>
 * @see <a href="https://github.com/ai/nanoid">nanoid</a>
 * @see <a href="https://github.com/aventrix/jnanoid">jnanoid</a>
 * @author YYL
 */
public class NanoId {

    /** 默认随机数生成器 */
    private static final SecureRandom DEFAULT_NUMBER_GENERATOR = new SecureRandom();

    /** 此类使用的默认字母表，使用64个唯一符号创建URL友好的NanoId字符串。 */
    public static final char[] DEFAULT_ALPHABET = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /** 默认长度 */
    public static final int DEFAULT_SIZE = 21;

    /**
     * 生成的长度为{@link #DEFAULT_SIZE}的NanoId字符串，使用加密强伪随机数生成的。
     * @return NanoId字符串
     */
    public static String randomNanoId() {
        return randomNanoId(DEFAULT_NUMBER_GENERATOR, DEFAULT_ALPHABET, DEFAULT_SIZE);
    }

    /**
     * 生成指定长度的NanoId字符串
     * @param size NanoId 字符串长度
     * @return NanoId字符串
     */
    public static String randomNanoId(int size) {
        return randomNanoId(DEFAULT_NUMBER_GENERATOR, DEFAULT_ALPHABET, size);
    }

    /**
     * 使用自定义字母表与指定长度生成NanoId字符串
     * @param alphabet 自定义字母表(长度1~255)
     * @param size NanoId 字符串长度
     * @return NanoId字符串
     */
    public static String randomNanoId(final char[] alphabet, final int size) {
        return randomNanoId(DEFAULT_NUMBER_GENERATOR, alphabet, size);
    }

    /**
     * 使用指定的随机数生成器，自定义字母表与指定长度生成NanoId字符串
     * @param random 随机数生成器
     * @param alphabet 自定义字母表(长度1~255)
     * @param size NanoId 字符串长度
     * @return NanoId字符串
     */
    public static String randomNanoId(final Random random, final char[] alphabet, final int size) {
        if (random == null) {
            throw new IllegalArgumentException("random cannot be null.");
        }
        if (alphabet == null) {
            throw new IllegalArgumentException("alphabet cannot be null.");
        }
        if (alphabet.length == 0 || alphabet.length >= 256) {
            throw new IllegalArgumentException("alphabet must contain between 1 and 255 symbols.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than zero.");
        }
        final int mask = (2 << (int) Math.floor(Math.log(alphabet.length - 1) / Math.log(2))) - 1;
        final int step = (int) Math.ceil(1.6 * mask * size / alphabet.length);
        final StringBuilder idBuilder = new StringBuilder();
        while (true) {
            final byte[] bytes = new byte[step];
            random.nextBytes(bytes);
            for (int i = 0; i < step; i++) {
                final int alphabetIndex = bytes[i] & mask;
                if (alphabetIndex < alphabet.length) {
                    idBuilder.append(alphabet[alphabetIndex]);
                    if (idBuilder.length() == size) {
                        return idBuilder.toString();
                    }
                }
            }
        }
    }
}
