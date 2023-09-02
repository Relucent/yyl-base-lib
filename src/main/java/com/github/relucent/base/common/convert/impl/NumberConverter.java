package com.github.relucent.base.common.convert.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.lang.NumberUtil;
import com.github.relucent.base.common.lang.StringUtil;
import com.github.relucent.base.common.time.TemporalAccessorUtil;

/**
 * 数值类型转换器
 * @see java.lang.Byte
 * @see java.lang.Short
 * @see java.lang.Integer
 * @see java.lang.Long
 * @see java.lang.Float
 * @see java.lang.Double
 * @see java.lang.Number
 * @see java.math.BigInteger
 * @see java.math.BigDecimal
 * @see java.util.concurrent.atomic.AtomicInteger
 * @see java.util.concurrent.atomic.AtomicLong
 * @see java.util.concurrent.atomic.LongAdder
 * @see java.util.concurrent.atomic.DoubleAdder
 * @author YYL
 */
public class NumberConverter implements Converter<Number> {

    public static final NumberConverter INSTANCE = new NumberConverter();

    @Override
    public Number convert(Object source, Class<? extends Number> toType) {

        if (source == null) {
            return null;
        }

        if (toType.isInstance(source)) {
            return toType.cast(source);
        }

        if (Byte.class.equals(toType)) {
            return toByte(source);
        }

        if (Short.class.equals(toType)) {
            return toShort(source);
        }

        if (Integer.class.equals(toType)) {
            return toInteger(source);
        }

        if (Long.class.equals(toType)) {
            return toLong(source);
        }

        if (Float.class.equals(toType)) {
            return toFloat(source);
        }

        if (Double.class.equals(toType)) {
            return toDouble(source);
        }

        if (Number.class.equals(toType)) {
            return toBigDecimal(source);
        }

        if (BigInteger.class.equals(toType)) {
            return toBigInteger(source);
        }

        if (BigDecimal.class.equals(toType)) {
            return toBigDecimal(source);
        }

        if (AtomicInteger.class.equals(toType)) {
            Integer value = toInteger(source);
            return value == null ? null : new AtomicInteger(value);
        }

        if (AtomicLong.class.equals(toType)) {
            Long value = toLong(source);
            return value == null ? null : new AtomicLong(value);

        }

        if (LongAdder.class.equals(toType)) {
            Long value = toLong(source);
            if (value != null) {
                LongAdder adder = new LongAdder();
                adder.add(value);
                return adder;
            }
            return null;
        }

        if (DoubleAdder.class.equals(toType)) {
            Double value = toDouble(source);
            if (value != null) {
                DoubleAdder adder = new DoubleAdder();
                adder.add(value);
                return adder;
            }
            return null;
        }

        return null;
    }

    /**
     * 类型转换，将对象转换为Byte<br>
     * @param source 初始对象
     * @return 类型转换后的Byte对象
     */
    protected static Byte toByte(Object source) {
        if (source instanceof Number) {
            return Byte.valueOf(((Number) source).byteValue());
        }
        if (source instanceof Boolean) {
            return Byte.valueOf(((Boolean) source).booleanValue() ? (byte) 1 : (byte) 0);
        }
        try {
            return Byte.valueOf(sourceToString(source));
        } catch (final NumberFormatException e) {
            // ignore
        }
        BigDecimal decimal = toBigDecimal(source);
        return decimal == null ? null : Byte.valueOf(decimal.byteValue());
    }

    /**
     * 类型转换，将对象转换为Short<br>
     * @param source 初始对象
     * @return 类型转换后的Short对象
     */
    protected static Short toShort(Object source) {
        if (source instanceof Number) {
            return Short.valueOf(((Number) source).shortValue());
        }
        if (source instanceof Boolean) {
            return Short.valueOf(((Boolean) source).booleanValue() ? (short) 1 : (short) 0);
        }
        try {
            return Short.valueOf(sourceToString(source));
        } catch (final NumberFormatException e) {
            // ignore
        }
        BigDecimal decimal = toBigDecimal(source);
        return decimal == null ? null : Short.valueOf(decimal.shortValue());
    }

    /**
     * 类型转换，将对象转换为Integer<br>
     * @param source 初始对象
     * @return 类型转换后的Integer对象
     */
    protected static Integer toInteger(Object source) {
        if (source instanceof Number) {
            return Integer.valueOf(((Number) source).intValue());
        }
        if (source instanceof Boolean) {
            return Integer.valueOf(((Boolean) source).booleanValue() ? 1 : 0);
        }
        try {
            return Integer.valueOf(sourceToString(source));
        } catch (final NumberFormatException e) {
            // ignore
        }
        BigDecimal decimal = toBigDecimal(source);
        return decimal == null ? null : Integer.valueOf(decimal.intValue());
    }

    /**
     * 类型转换，将对象转换为Long<br>
     * @param source 初始对象
     * @return 类型转换后的Long对象
     */
    protected static Long toLong(Object source) {
        if (source instanceof Number) {
            return Long.valueOf(((Number) source).longValue());
        }
        if (source instanceof Boolean) {
            return Long.valueOf(((Boolean) source).booleanValue() ? 1 : 0);
        }
        try {
            return Long.valueOf(sourceToString(source));
        } catch (final NumberFormatException e) {
            // ignore
        }
        BigDecimal decimal = toBigDecimal(source);
        return decimal == null ? null : Long.valueOf(decimal.longValue());
    }

    /**
     * 类型转换，将对象转换为Float<br>
     * @param source 初始对象
     * @return 类型转换后的Float对象
     */
    protected static Float toFloat(Object source) {
        if (source instanceof Number) {
            return Float.valueOf(((Number) source).floatValue());
        }
        if (source instanceof Boolean) {
            return Float.valueOf(((Boolean) source).booleanValue() ? 1f : 0f);
        }
        try {
            return Float.valueOf(sourceToString(source));
        } catch (final NumberFormatException e) {
            // ignore
        }
        BigDecimal decimal = toBigDecimal(source);
        return decimal == null ? null : Float.valueOf(decimal.floatValue());
    }

    /**
     * 类型转换，将对象转换为Double<br>
     * @param source 初始对象
     * @return 类型转换后的Double对象
     */
    protected static Double toDouble(Object source) {
        if (source instanceof Number) {
            return Double.valueOf(((Number) source).doubleValue());
        }
        if (source instanceof Boolean) {
            return Double.valueOf(((Boolean) source).booleanValue() ? 1d : 0d);
        }
        try {
            return Double.valueOf(sourceToString(source));
        } catch (final NumberFormatException e) {
            // ignore
            return Double.valueOf(toBigDecimal(source).doubleValue());
        }
    }

    /**
     * 类型转换，将对象转换为BigInteger<br>
     * @param source 初始对象
     * @return 类型转换后的BigInteger对象
     */
    protected static BigInteger toBigInteger(Object source) {
        if (source instanceof Number) {
            return NumberUtil.toBigInteger((Number) source);
        }
        if (source instanceof Boolean) {
            return ((Boolean) source).booleanValue() ? BigInteger.ONE : BigInteger.ZERO;
        }
        if (source instanceof Date) {
            return BigInteger.valueOf(((Date) source).getTime());
        }
        if (source instanceof Calendar) {
            return BigInteger.valueOf(((Calendar) source).getTimeInMillis());
        }
        if (source instanceof TemporalAccessor) {
            return BigInteger.valueOf(TemporalAccessorUtil.toInstant((TemporalAccessor) source).toEpochMilli());
        }
        try {
            return NumberUtil.toBigInteger(sourceToString(source));
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * 类型转换，将对象转换为BigDecimal<br>
     * @param source 初始对象
     * @return 类型转换后的BigDecimal对象
     */
    protected static BigDecimal toBigDecimal(Object source) {
        if (source instanceof Number) {
            return NumberUtil.toBigDecimal((Number) source);
        }
        if (source instanceof Boolean) {
            return ((Boolean) source).booleanValue() ? BigDecimal.ONE : BigDecimal.ZERO;
        }
        if (source instanceof Date) {
            return BigDecimal.valueOf(((Date) source).getTime());
        }
        if (source instanceof Calendar) {
            return BigDecimal.valueOf(((Calendar) source).getTimeInMillis());
        }
        if (source instanceof TemporalAccessor) {
            return BigDecimal.valueOf(TemporalAccessorUtil.toInstant((TemporalAccessor) source).toEpochMilli());
        }
        try {
            return NumberUtil.toBigDecimal(sourceToString(source));
        } catch (Exception ignore) {
            return null;
        }
    }

    /**
     * 特殊处理解决浮点类型的后缀
     * @param source 处理的对象
     * @return 处理后的字符串
     */
    protected static String sourceToString(Object source) {
        final String string = StringUtil.trim(StringUtil.string(source));
        if (StringUtil.isNotEmpty(string)) {
            final char c = Character.toUpperCase(string.charAt(string.length() - 1));
            // 类型标识形式（例如123.45D）
            if (c == 'D' || c == 'L' || c == 'F') {
                return string.substring(0, string.length() - 1);
            }
        }
        return string;
    }
}
