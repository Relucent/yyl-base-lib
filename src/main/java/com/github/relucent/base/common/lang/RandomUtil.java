package com.github.relucent.base.common.lang;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.github.relucent.base.common.constant.StringConstant;

/**
 * 随机工具类，提供{@link Random}相关实用方法。<br>
 */
public class RandomUtil {

    // ==============================Fields===========================================
    /** 数字字符 */
    public static final char[] NUMBER_ALPHABET = "0123456789".toCharArray();
    /** 字母字符表 */
    public static final char[] ENGLISH_ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    /** 字母数字表 */
    public static final char[] DEFAULT_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected RandomUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 获取随机数生成器对象<br>
     * 注意：此方法返回的{@link ThreadLocalRandom} 不应在多线程环境下共享对象，否则有重复随机数问题，应当每次使用时候调用获取。<br>
     * @return {@link ThreadLocalRandom}
     */
    public static ThreadLocalRandom currentRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * 创建{@link SecureRandom}<br>
     * @param seed 自定义随机种子
     * @return {@link SecureRandom}
     */
    public static SecureRandom getSecureRandom(byte[] seed) {
        return (seed == null) ? new SecureRandom() : new SecureRandom(seed);
    }

    /**
     * 获得指定范围内的随机数
     * @param origin 最小值（包含）
     * @param bound 上限值（不包含）
     * @return 随机数
     */
    public static int nextInt(int origin, int bound) {
        return currentRandom().nextInt(origin, bound);
    }

    /**
     * 获得随机数整数
     * @return 随机数
     */
    public static int nextInt() {
        return currentRandom().nextInt();
    }

    /**
     * 获得随机 Boolean 值
     * @return 随机 Boolean 值，{@code true} 或者 {@code false}
     */
    public static boolean nextBoolean() {
        return currentRandom().nextBoolean();
    }

    /**
     * 获得随机汉字（'\u4E00'-'\u9FFF'）
     * @return 随机的汉字字符
     */
    public static char nextChineseChar() {
        return (char) nextInt('\u4E00', '\u9FFF');
    }

    /**
     * 获得指定范围内的随机数
     * @param origin 最小值（包含）
     * @param bound 上限值（不包含）
     * @return 随机数
     */
    public static long nextLong(long origin, long bound) {
        return currentRandom().nextLong(origin, bound);
    }

    /**
     * 获得随机数
     * @return 随机数
     */
    public static long nextLong() {
        return currentRandom().nextLong();
    }

    /**
     * 获得指定范围内的随机数
     * @param origin 最小值（包含）
     * @param bound 上限值（不包含）
     * @return 随机数
     */
    public static double nextDouble(double origin, double bound) {
        return currentRandom().nextDouble(origin, bound);
    }

    /**
     * 获得随机数[0, 1)
     * @return 随机数
     */
    public static double nextDouble() {
        return currentRandom().nextDouble();
    }

    /**
     * 获得随机字符，字符只包含小写字母和数字
     * @return 随机字符
     */
    public static char nextChar() {
        return nextChar(DEFAULT_ALPHABET);
    }

    /**
     * 获得随机字符
     * @param alphabet 自定义字母表
     * @return 随机字符
     */
    public static char nextChar(char[] alphabet) {
        return alphabet[nextInt(0, alphabet.length)];
    }

    /**
     * 随机字节数组
     * @param length 字节数组长度
     * @return 随机字节数组
     */
    public static byte[] nextBytes(int length) {
        byte[] bytes = new byte[length];
        currentRandom().nextBytes(bytes);
        return bytes;
    }

    /**
     * 获得一个随机的字符串
     * @param alphabet 自定义字母表
     * @param length 字符串长度
     * @return 随机字符串
     */
    public static String nextString(char[] alphabet, int length) {
        if (ArrayUtil.isEmpty(alphabet) || length < 1) {
            return StringConstant.EMPTY;
        }
        ThreadLocalRandom random = currentRandom();
        char[] value = new char[length];
        for (int i = 0; i < length; i++) {
            value[i] = alphabet[random.nextInt(alphabet.length)];
        }
        return new String(value);
    }

    /**
     * 获得一个随机的字符串（只包含小写字母和数字）
     * @param length 字符串的长度
     * @return 随机字符串
     */
    public static String nextString(int length) {
        return nextString(DEFAULT_ALPHABET, length);
    }

    /**
     * 获得一个只包含数字的字符串
     * @param length 字符串的长度
     * @return 随机字符串
     */
    public static String nextNumberString(int length) {
        return nextString(NUMBER_ALPHABET, length);
    }
}
