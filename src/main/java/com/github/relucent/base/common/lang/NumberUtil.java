package com.github.relucent.base.common.lang;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.relucent.base.common.constant.NumberConstant;

public class NumberUtil {

    // ==============================Fields===========================================

    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected NumberUtil() {
    }

    // ==============================Methods==========================================
    /**
     * 对{@code BigDecimal}进行转换，四舍五入保留两位小数
     * @param value 需要转换的 {@code BigDecimal}，可以为 null.
     * @return 转换后的结果{@code BigDecimal}
     */
    public static BigDecimal toScaledBigDecimal(final BigDecimal value) {
        return toScaledBigDecimal(value, NumberConstant.INTEGER_TWO, RoundingMode.HALF_EVEN);
    }

    /**
     * 对{@code BigDecimal}进行转换，使用指定的舍入模式{@code RoundingMode}保留指定的小数位，如果输入{@code value}是{@code null}，则返回{@code BigDecimal.ZERO}。
     * @param value 需要转换的 {@code BigDecimal}，可以为 null
     * @param scale 小数点右边的位数
     * @param roundingMode 舍入模式：数值运算的舍入行为，如果为null，则使用四舍五入
     * @return 转换后的结果{@code BigDecimal}
     */
    public static BigDecimal toScaledBigDecimal(final BigDecimal value, final int scale, final RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return value.setScale(scale, (roundingMode == null) ? RoundingMode.HALF_EVEN : roundingMode);
    }

    /**
     * 将{@code Float}转换为{@code BigDecimal}，四舍五入保留两位小数
     * @param value 需要转换的 {@code Float}，可以为 null
     * @return 处理后的数值
     */
    public static BigDecimal toScaledBigDecimal(final Float value) {
        return toScaledBigDecimal(value, NumberConstant.INTEGER_TWO, RoundingMode.HALF_EVEN);
    }

    /**
     * 将{@code Float}转换为{@code BigDecimal}，使用指定的舍入模式{@code RoundingMode}保留指定的小数位，如果输入{@code value}是{@code null}，则返回{@code BigDecimal.ZERO}。
     * @param value 需要转换的 {@code Float}，可以为 null
     * @param scale 小数点右边的位数
     * @param roundingMode 舍入模式：数值运算的舍入行为，如果为null，则使用四舍五入
     * @return 转换后的结果{@code BigDecimal}
     */
    public static BigDecimal toScaledBigDecimal(final Float value, final int scale, final RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return toScaledBigDecimal(BigDecimal.valueOf(value), scale, roundingMode);
    }

    /**
     * 将{@code Double}转换为{@code BigDecimal}，四舍五入保留两位小数
     * @param value 需要转换的 {@code Float}，可以为 null
     * @return 处理后的数值
     */
    public static BigDecimal toScaledBigDecimal(final Double value) {
        return toScaledBigDecimal(value, NumberConstant.INTEGER_TWO, RoundingMode.HALF_EVEN);
    }

    /**
     * 将{@code Double}转换为{@code BigDecimal}，使用指定的舍入模式{@code RoundingMode}保留指定的小数位，如果输入{@code value}是{@code null}，则返回{@code BigDecimal.ZERO}。
     * @param value 需要转换的 {@code Double}，可以为 null
     * @param scale 小数点右边的位数
     * @param roundingMode 舍入模式：数值运算的舍入行为，如果为null，则使用四舍五入
     * @return 转换后的结果{@code BigDecimal}
     */
    public static BigDecimal toScaledBigDecimal(final Double value, final int scale, final RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return toScaledBigDecimal(BigDecimal.valueOf(value), scale, roundingMode);
    }

    /**
     * 将{@code String}转换为{@code BigDecimal}，四舍五入保留两位小数
     * @param value 需要转换的 {@code String}，可以为 null
     * @return 处理后的数值
     */
    public static BigDecimal toScaledBigDecimal(final String value) {
        return toScaledBigDecimal(value, NumberConstant.INTEGER_TWO, RoundingMode.HALF_EVEN);
    }

    /**
     * 将{@code String}转换为{@code BigDecimal}，使用指定的舍入模式{@code RoundingMode}保留指定的小数位，如果输入{@code value}是{@code null}，则返回{@code BigDecimal.ZERO}。
     * @param value 需要转换的 {@code String}，可以为 null
     * @param scale 小数点右边的位数
     * @param roundingMode 舍入模式：数值运算的舍入行为，如果为null，则使用四舍五入
     * @return 转换后的结果{@code BigDecimal}
     */
    public static BigDecimal toScaledBigDecimal(final String value, final int scale, final RoundingMode roundingMode) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        return toScaledBigDecimal(toBigDecimal(value), scale, roundingMode);
    }

    /**
     * 将{@code String}转换为{@code BigDecimal}，如果参数为空则返回{@code null}
     * @param value 需要转换的 {@code String}，可以为 null
     * @return 转换后的结果{@code BigDecimal} (如果参数为空则返回 null )
     * @throws NumberFormatException 无法转换该值
     */
    public static BigDecimal toBigDecimal(final String value) {
        if (value == null) {
            return null;
        }
        if (StringUtil.isBlank(value)) {
            throw new NumberFormatException("A blank string is not a valid number");
        }
        return new BigDecimal(value);
    }

    // // --------------------------------------------------------------------
    /**
     * 返回数组中的最小值
     * @param array 数字数组
     * @return 数组中的最小值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static byte min(final byte... array) {
        validateArray(array);
        byte min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    /**
     * 返回数组中的最小值
     * @param array 数字数组
     * @return 数组中的最小值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static short min(final short... array) {
        validateArray(array);
        short min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    /**
     * 返回数组中的最小值
     * @param array 数字数组
     * @return 数组中的最小值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static int min(final int... array) {
        validateArray(array);
        int min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    /**
     * 返回数组中的最小值
     * @param array 数字数组
     * @return 数组中的最小值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static long min(final long... array) {
        validateArray(array);
        long min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    /**
     * 返回数组中的最小值
     * @param array 数字数组
     * @return 数组中的最小值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static float min(final float... array) {
        validateArray(array);
        float min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    /**
     * 返回数组中的最小值
     * @param array 数字数组
     * @return 数组中的最小值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static double min(final double... array) {
        validateArray(array);
        double min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }

    // // --------------------------------------------------------------------
    /**
     * 返回数组中的最大值
     * @param array 数字数组
     * @return 数组中的最大值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static byte max(final byte... array) {
        validateArray(array);
        byte max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    /**
     * 返回数组中的最大值
     * @param array 数字数组
     * @return 数组中的最大值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static short max(final short... array) {
        validateArray(array);
        short max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    /**
     * 返回数组中的最大值
     * @param array 数字数组
     * @return 数组中的最大值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static int max(final int... array) {
        validateArray(array);
        int max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    /**
     * 返回数组中的最大值
     * @param array 数字数组
     * @return 数组中的最大值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static long max(final long... array) {
        validateArray(array);
        long max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    /**
     * 返回数组中的最大值
     * @param array 数字数组
     * @return 数组中的最大值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static float max(final float... array) {
        validateArray(array);
        float max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    /**
     * 返回数组中的最大值
     * @param array 数字数组
     * @return 数组中的最大值
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    public static double max(final double... array) {
        validateArray(array);
        double max = array[0];
        for (int j = 1; j < array.length; j++) {
            if (array[j] > max) {
                max = array[j];
            }
        }
        return max;
    }

    /**
     * 检查指定的数组是否为空的，如果数组为空抛出异常
     * @param array 检查的数组
     * @throws IllegalArgumentException 如果数组{@code array} 为{@code null} 或者空
     */
    private static void validateArray(final Object array) {
        Assert.notNull(array, "The Array must not be null");
        Assert.isTrue(Array.getLength(array) != 0, "Array cannot be empty.");
    }
    // // -----------------------------------------------------------------------

    /**
     * 判断字符串是否只包含数字
     * @param string 检查的字符串
     * @return 如果字符串只包含数字字符，则返回{@code true} //
     */
    public static boolean isDigits(final String string) {
        return StringUtil.isDigits(string);
    }

    /**
     * 判断字符串是否可以转换为数字<br>
     * 有效数字包括：
     * 
     * <pre>
     * 1、十六进制，使用{@code 0x}或{@code 0X}限定符前缀
     * 2、八进制数，以前缀0开头的非十六进制字符串
     * 3、科学记数法，例如：1E3
     * 4、包含类型限定符的数字字符串，例如：123L，0.12D
     * </pre>
     * 
     * 以前缀0开头的非十六进制字符串是被视为八进制值。因此，字符串{@code 08}将返回{@code false}，因为{@code 8}不是有效的八进制值。但是，以{@code 0.}开头的数字被视为十进制。<br>
     * 空字符串将返回{@code false}，因为这不是有效数字<br>
     * @param string 检查的字符串
     * @return 如果字符序列是一个数字，则返回{@code true} //
     */
    public static boolean isNumber(final String string) {
        if (StringUtil.isEmpty(string)) {
            return false;
        }
        final char[] chars = string.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        // deal with any possible sign up front
        final int start = chars[0] == '-' || chars[0] == '+' ? 1 : 0;
        // leading 0, skip if is a decimal number
        if (sz > start + 1 && chars[start] == '0' && string.indexOf('.') == -1) {
            // leading 0x/0X
            if (chars[start + 1] == 'x' || chars[start + 1] == 'X') {
                int i = start + 2;
                if (i == sz) {
                    return false; // str == "0x"
                }
                // checking hex (it can't be anything else)
                for (; i < chars.length; i++) {
                    if ((chars[i] < '0' || chars[i] > '9') && (chars[i] < 'a' || chars[i] > 'f') && (chars[i] < 'A' || chars[i] > 'F')) {
                        return false;
                    }
                }
                return true;
            } else if (Character.isDigit(chars[start + 1])) {
                // leading 0, but not hex, must be octal
                int i = start + 1;
                for (; i < chars.length; i++) {
                    if (chars[i] < '0' || chars[i] > '7') {
                        return false;
                    }
                }
                return true;
            }
        }
        sz--; // don't want to loop to the last char, check it afterwords
              // for type qualifiers
        int i = start;
        // loop to the next to last char or to the last char if we need another digit to
        // make a valid number (e.g. chars[0..5] = "1234E")
        while (i < sz || i < sz + 1 && allowSigns && !foundDigit) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;

            } else if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                hasDecPoint = true;
            } else if (chars[i] == 'e' || chars[i] == 'E') {
                // we've already taken care of hex.
                if (hasExp) {
                    // two E's
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
            } else if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false; // we need a digit after the E
            } else {
                return false;
            }
            i++;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                // no type qualifier, OK
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                // can't have an E at the last byte
                return false;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    // two decimal points or dec in exponent
                    return false;
                }
                // single trailing decimal point after non-exponent is ok
                return foundDigit;
            }
            if (!allowSigns && (chars[i] == 'd' || chars[i] == 'D' || chars[i] == 'f' || chars[i] == 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                // not allowing L with an exponent or decimal point
                return foundDigit && !hasExp && !hasDecPoint;
            }
            // last character is illegal
            return false;
        }
        // allowSigns is true iff the val ends in 'E'
        // found digit it to make sure weird stuff like '.' and '1E-' doesn't pass
        return !allowSigns && foundDigit;
    }
}