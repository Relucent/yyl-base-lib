package com.github.relucent.base.common.lang;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.github.relucent.base.common.constant.StringConstant;

/**
 * 生成随机 {@link String} 的工具类。<br>
 * 注：本类的实现依赖 {@link Random}，它并不是密码学安全的随机数生成器。<br>
 */
public class RandomStringUtil {

    private static ThreadLocalRandom currentRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * 创建一个指定长度的随机字符串。<br>
     * 字符将从所有可用字符集中随机选取。<br>
     * @param count 要生成的随机字符串长度
     * @return 随机字符串
     * @throws IllegalArgumentException 如果 {@code count} 小于 0
     */
    public static String random(final int count) {
        return random(count, false, false);
    }

    /**
     * 创建一个指定长度的随机字符串。<br>
     * 是否包含字母或数字由参数控制。<br>
     * @param count   要生成的随机字符串长度
     * @param letters 是否允许包含字母字符
     * @param numbers 是否允许包含数字字符
     * @return 随机字符串
     * @throws IllegalArgumentException 如果 {@code count} 小于 0
     */
    public static String random(final int count, final boolean letters, final boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    /**
     * 创建一个指定长度的随机字符串。<br>
     * 字符将从指定的字符数组中随机选取。<br>
     * @param count 要生成的随机字符串长度
     * @param chars 用于生成随机字符串的字符数组，可为 null
     * @return 随机字符串
     * @throws IllegalArgumentException 如果 {@code count} 小于 0
     */
    public static String random(final int count, final char... chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, currentRandom());
        }
        return random(count, 0, chars.length, false, false, chars, currentRandom());
    }

    /**
     * 创建一个指定长度的随机字符串。<br>
     * 字符范围和是否包含字母、数字由参数控制。<br>
     * @param count   要生成的随机字符串长度
     * @param start   字符范围起始位置（包含）
     * @param end     字符范围结束位置（不包含）
     * @param letters 是否允许包含字母
     * @param numbers 是否允许包含数字
     * @return 随机字符串
     * @throws IllegalArgumentException 如果 {@code count} 小于 0
     */
    public static String random(final int count, final int start, final int end, final boolean letters,
            final boolean numbers) {
        return random(count, start, end, letters, numbers, null, currentRandom());
    }

    /**
     * 基于多种选项创建随机字符串，使用默认的随机数源。<br>
     * 该方法与 {@link #random(int, int, int, boolean, boolean, char[], Random)} 语义完全一致，只是使用内部默认的 {@link Random} 实例。<br>
     * @param count   要生成的随机字符串长度
     * @param start   字符范围起始位置
     * @param end     字符范围结束位置
     * @param letters 是否允许包含字母
     * @param numbers 是否允许包含数字
     * @param chars   可选字符集，若为 null 则使用所有字符
     * @return 随机字符串
     * @throws ArrayIndexOutOfBoundsException 字符集范围非法
     * @throws IllegalArgumentException       如果 {@code count} 小于 0
     */
    public static String random(final int count, final int start, final int end, final boolean letters,
            final boolean numbers, final char... chars) {
        return random(count, start, end, letters, numbers, chars, currentRandom());
    }

    /**
     * 使用指定的随机数源创建随机字符串。<br>
     * 如果 start 和 end 均为 0，则：<br>
     * 1、若 chars 不为 null，则使用 chars 的范围；<br>
     * 2、若 letters 和 numbers 均为 false，则使用所有 Unicode 字符；<br>
     * 3、否则使用 ASCII 可打印字符（空格到 'z'）。<br>
     * 允许使用外部传入的 {@link Random} 实例。<br>
     * 通过为 Random 设置固定种子，可以重复生成可预测的随机字符串序列。<br>
     * @param count   要生成的随机字符串长度
     * @param start   字符范围起始（包含）
     * @param end     字符范围结束（不包含）
     * @param letters 是否允许包含字母
     * @param numbers 是否允许包含数字
     * @param chars   可选字符集，不可为空数组
     * @param random  随机数源
     * @return 随机字符串
     * @throws ArrayIndexOutOfBoundsException 字符集范围非法
     * @throws IllegalArgumentException       参数非法
     */
    public static String random(int count, int start, int end, final boolean letters, final boolean numbers,
            final char[] chars, final Random random) {
        if (count == 0) {
            return StringConstant.EMPTY;
        }
        if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        if (chars != null && chars.length == 0) {
            throw new IllegalArgumentException("The chars array must not be empty");
        }

        if (start == 0 && end == 0) {
            if (chars != null) {
                end = chars.length;
            } else if (!letters && !numbers) {
                end = Character.MAX_CODE_POINT;
            } else {
                end = 'z' + 1;
                start = ' ';
            }
        } else if (end <= start) {
            throw new IllegalArgumentException(
                    "Parameter end (" + end + ") must be greater than start (" + start + ")");
        }

        final int zeroDigitAscii = 48;
        final int firstLetterAscii = 65;

        if (chars == null && (numbers && end <= zeroDigitAscii || letters && end <= firstLetterAscii)) {
            throw new IllegalArgumentException(
                    "Parameter end (" + end + ") must be greater then (" + zeroDigitAscii + ") for generating digits "
                            + "or greater then (" + firstLetterAscii + ") for generating letters.");
        }

        final StringBuilder builder = new StringBuilder(count);
        final int gap = end - start;

        while (count-- != 0) {
            final int codePoint;
            if (chars == null) {
                codePoint = random.nextInt(gap) + start;

                switch (Character.getType(codePoint)) {
                case Character.UNASSIGNED:
                case Character.PRIVATE_USE:
                case Character.SURROGATE:
                    count++;
                    continue;
                }

            } else {
                codePoint = chars[random.nextInt(gap) + start];
            }

            final int numberOfChars = Character.charCount(codePoint);
            if (count == 0 && numberOfChars > 1) {
                count++;
                continue;
            }

            if (letters && Character.isLetter(codePoint) || numbers && Character.isDigit(codePoint)
                    || !letters && !numbers) {
                builder.appendCodePoint(codePoint);

                if (numberOfChars == 2) {
                    count--;
                }

            } else {
                count++;
            }
        }
        return builder.toString();
    }

    /**
     * 创建一个指定长度的随机字符串。<br>
     * 字符将从指定的字符串中选取。 若为 null，则使用所有字符。<br>
     * @param count 要生成的随机字符串长度
     * @param chars 可选字符字符串，可为 null，但不可为空字符串
     * @return 随机字符串
     * @throws IllegalArgumentException 参数非法
     */
    public static String random(final int count, final String chars) {
        if (chars == null) {
            return random(count, 0, 0, false, false, null, currentRandom());
        }
        return random(count, chars.toCharArray());
    }

    /**
     * 创建一个指定长度的随机字符串。<br>
     * 字符将从拉丁字母字符集中随机选取（a-z, A-Z）。<br>
     * @param count 要生成的随机字符串长度
     * @return 随机字符串
     * @throws IllegalArgumentException 如果 {@code count} 小于 0
     */
    public static String randomAlphabetic(final int count) {
        return random(count, true, false);
    }

    /**
     * 创建一个长度介于最小值（包含）和最大值（不包含）之间的随机字符串。<br>
     * 字符将从拉丁字母字符集中随机选取（a-z, A-Z）。<br>
     * @param minLengthInclusive 生成字符串的最小长度（包含）
     * @param maxLengthExclusive 生成字符串的最大长度（不包含）
     * @return 随机字符串
     */
    public static String randomAlphabetic(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomAlphabetic(RandomUtil.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 创建一个指定长度的随机字符串。<br>
     * 字符将从拉丁字母字符集（a-z, A-Z）以及数字字符（0-9）中随机选取。<br>
     * @param count 要生成的随机字符串长度
     * @return 随机字符串
     * @throws IllegalArgumentException 如果 {@code count} 小于 0
     */
    public static String randomAlphanumeric(final int count) {
        return random(count, true, true);
    }

    /**
     ** 创建一个长度介于最小值（包含）和最大值（不包含）之间的随机字符串。<br>
     * 字符将从拉丁字母字符集（a-z, A-Z）以及数字字符（0-9）中随机选取。<br>
     * @param minLengthInclusive 生成字符串的最小长度（包含）
     * @param maxLengthExclusive 生成字符串的最大长度（不包含）
     * @return 随机字符串
     */
    public static String randomAlphanumeric(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomAlphanumeric(RandomUtil.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 创建仅包含 ASCII 可打印字符（32–126）的随机字符串。
     * @param count 长度
     * @return 随机字符串
     */
    public static String randomAscii(final int count) {
        return random(count, 32, 127, false, false);
    }

    /**
     * 创建一个长度介于最小值（包含）和最大值（不包含）之间的随机字符串。<br>
     * 字符将从 ASCII 值介于 {@code 32} 到 {@code 126}（包含）的字符集中随机选取。<br>
     * @param minLengthInclusive 生成字符串的最小长度（包含）
     * @param maxLengthExclusive 生成字符串的最大长度（不包含）
     * @return 随机字符串
     */
    public static String randomAscii(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomAscii(RandomUtil.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 创建一个指定长度的随机字符串。<br>
     * 字符将从符合 POSIX [:graph:] 正则表达式字符类的字符集中随机选取。<br>
     * 该字符集包含所有可见的 ASCII 字符（即除空格和控制字符之外的字符）。<br>
     * @param count 要生成的随机字符串长度
     * @return 随机字符串
     * @throws IllegalArgumentException 如果 {@code count} 小于 0
     */
    public static String randomGraph(final int count) {
        return random(count, 33, 126, false, false);
    }

    /**
     * 创建一个长度介于最小值（包含）和最大值（不包含）之间的随机字符串。<br>
     * 字符将从 \p{Graph} 字符集中随机选取。<br>
     * @param minLengthInclusive 生成字符串的最小长度（包含）
     * @param maxLengthExclusive 生成字符串的最大长度（不包含）
     * @return 随机字符串
     */
    public static String randomGraph(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomGraph(RandomUtil.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 创建一个指定长度的随机字符串。<br>
     * 字符将仅从数字字符集中随机选取。<br>
     * @param count 要生成的随机字符串长度
     * @return 随机字符串
     * @throws IllegalArgumentException 如果 {@code count} 小于 0
     */
    public static String randomNumeric(final int count) {
        return random(count, false, true);
    }

    /**
     * 创建一个长度介于最小值（包含）和最大值（不包含）之间的随机字符串。<br>
     * 字符将从 \p{Digit}（数字）字符集中随机选取。<br>
     * @param minLengthInclusive 生成字符串的最小长度（包含）
     * @param maxLengthExclusive 生成字符串的最大长度（不包含）
     * @return 随机字符串
     */
    public static String randomNumeric(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomNumeric(RandomUtil.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 创建一个指定长度的随机字符串。<br>
     * 字符将从符合 POSIX [:print:] 正则表达式字符类的字符集中随机选取。<br>
     * 该字符集包含所有可见的 ASCII 字符以及空格 （即除控制字符之外的所有字符）。<br>
     * @param count 要生成的随机字符串长度
     * @return 随机字符串
     * @throws IllegalArgumentException 如果 {@code count} 小于 0
     */
    public static String randomPrint(final int count) {
        return random(count, 32, 126, false, false);
    }

    /**
     * /** 创建一个长度介于最小值（包含）和最大值（不包含）之间的随机字符串。<br>
     * 字符将从 \p{Print} 字符集中随机选取。<br>
     * @param minLengthInclusive 生成字符串的最小长度（包含）
     * @param maxLengthExclusive 生成字符串的最大长度（不包含）
     * @return 随机字符串
     */
    public static String randomPrint(final int minLengthInclusive, final int maxLengthExclusive) {
        return randomPrint(RandomUtil.nextInt(minLengthInclusive, maxLengthExclusive));
    }

    /**
     * 工具类，不建议被实例化使用
     */
    protected RandomStringUtil() {
    }
}
