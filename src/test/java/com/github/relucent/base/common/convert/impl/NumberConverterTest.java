package com.github.relucent.base.common.convert.impl;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link NumberConverter} 单元测试
 */
public class NumberConverterTest {

    @Test
    public void testConvertNull() {
        Assert.assertNull(NumberConverter.INSTANCE.convert(null, Integer.class));
    }

    @Test
    public void testConvertSameType() {
        // 源对象即目标类型,直接返回
        Integer value = Integer.valueOf(123);
        Assert.assertSame(value, NumberConverter.INSTANCE.convert(value, Integer.class));
    }

    @Test
    public void testConvertNumberToInteger() {
        Assert.assertEquals(Integer.valueOf(1), NumberConverter.INSTANCE.convert(1L, Integer.class));
        Assert.assertEquals(Integer.valueOf(1), NumberConverter.INSTANCE.convert(1.0, Integer.class));
        Assert.assertEquals(Integer.valueOf(1), NumberConverter.INSTANCE.convert(1.9, Integer.class)); // 截断
        Assert.assertEquals(Integer.valueOf(-1), NumberConverter.INSTANCE.convert(-1L, Integer.class));
    }

    @Test
    public void testConvertBooleanToNumber() {
        Assert.assertEquals(Integer.valueOf(1), NumberConverter.INSTANCE.convert(true, Integer.class));
        Assert.assertEquals(Integer.valueOf(0), NumberConverter.INSTANCE.convert(false, Integer.class));
        Assert.assertEquals(Long.valueOf(1L), NumberConverter.INSTANCE.convert(true, Long.class));
        Assert.assertEquals(Double.valueOf(1d), NumberConverter.INSTANCE.convert(true, Double.class));
    }

    @Test
    public void testConvertStringToNumber() {
        Assert.assertEquals(Integer.valueOf(123), NumberConverter.INSTANCE.convert("123", Integer.class));
        Assert.assertEquals(Long.valueOf(123L), NumberConverter.INSTANCE.convert("123", Long.class));
        Assert.assertEquals(Double.valueOf(12.5), NumberConverter.INSTANCE.convert("12.5", Double.class));
        Assert.assertEquals(Float.valueOf(12.5f), NumberConverter.INSTANCE.convert("12.5", Float.class));
    }

    @Test
    public void testConvertStringWithSuffix() {
        // 后缀 D/L/F 应被去除
        Assert.assertEquals(Double.valueOf(123d), NumberConverter.INSTANCE.convert("123D", Double.class));
        Assert.assertEquals(Long.valueOf(123L), NumberConverter.INSTANCE.convert("123L", Long.class));
        Assert.assertEquals(Float.valueOf(123f), NumberConverter.INSTANCE.convert("123F", Float.class));
    }

    @Test
    public void testConvertInvalidString() {
        Assert.assertNull(NumberConverter.INSTANCE.convert("abc", Integer.class));
        Assert.assertNull(NumberConverter.INSTANCE.convert("", Integer.class));
    }

    @Test
    public void testConvertToByteAndShort() {
        Assert.assertEquals(Byte.valueOf((byte) 1), NumberConverter.INSTANCE.convert(1, Byte.class));
        Assert.assertEquals(Short.valueOf((short) 1), NumberConverter.INSTANCE.convert(1, Short.class));
    }

    @Test
    public void testConvertToBigInteger() {
        Assert.assertEquals(BigInteger.valueOf(123L), NumberConverter.INSTANCE.convert(123, BigInteger.class));
        Assert.assertEquals(BigInteger.ONE, NumberConverter.INSTANCE.convert(true, BigInteger.class));
        Assert.assertEquals(BigInteger.ZERO, NumberConverter.INSTANCE.convert(false, BigInteger.class));
        // Date → BigInteger(epochMilli)
        Assert.assertEquals(BigInteger.valueOf(0L), NumberConverter.INSTANCE.convert(new Date(0L), BigInteger.class));
    }

    @Test
    public void testConvertToBigDecimal() {
        BigDecimal result = (BigDecimal) NumberConverter.INSTANCE.convert(123, BigDecimal.class);
        Assert.assertNotNull(result);
        // 用 compareTo 避免 scale 差异(scale 0 vs 1)
        Assert.assertEquals(0, result.compareTo(BigDecimal.valueOf(123)));
        // Date → BigDecimal(epochMilli)
        BigDecimal dateResult = (BigDecimal) NumberConverter.INSTANCE.convert(new Date(0L), BigDecimal.class);
        Assert.assertEquals(0, dateResult.compareTo(BigDecimal.valueOf(0L)));
    }

    @Test
    public void testConvertToAtomicInteger() {
        AtomicInteger result = (AtomicInteger) NumberConverter.INSTANCE.convert(123, AtomicInteger.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(123, result.get());
    }

    @Test
    public void testConvertToAtomicLong() {
        AtomicLong result = (AtomicLong) NumberConverter.INSTANCE.convert(123L, AtomicLong.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(123L, result.get());
    }

    @Test
    public void testConvertToLongAdder() {
        LongAdder result = (LongAdder) NumberConverter.INSTANCE.convert(123L, LongAdder.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(123L, result.sum());
    }

    @Test
    public void testConvertToDoubleAdder() {
        DoubleAdder result = (DoubleAdder) NumberConverter.INSTANCE.convert(1.5, DoubleAdder.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(1.5d, result.sum(), 0.0001);
    }

    @Test
    public void testConvertToNumberClass() {
        // Number.class + Integer 源 → isInstance 短路,直接返回 Integer
        Object result = NumberConverter.INSTANCE.convert(123, Number.class);
        Assert.assertEquals(Integer.valueOf(123), result);
    }
}
