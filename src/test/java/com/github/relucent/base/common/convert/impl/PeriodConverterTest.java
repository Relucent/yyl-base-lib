package com.github.relucent.base.common.convert.impl;

import java.time.Duration;
import java.time.Period;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link PeriodConverter} 单元测试
 */
public class PeriodConverterTest {

    @Test
    public void testConvertFromPeriod() {
        Period source = Period.ofDays(1);
        Assert.assertSame(source, PeriodConverter.INSTANCE.convert(source, Period.class));
    }

    @Test
    public void testConvertFromNumber() {
        Assert.assertEquals(Period.ofDays(10), PeriodConverter.INSTANCE.convert(10, Period.class));
    }

    @Test
    public void testConvertFromIsoText() {
        Assert.assertEquals(Period.ofDays(1), PeriodConverter.INSTANCE.convert("P1D", Period.class));
    }

    @Test
    public void testConvertFromIllegalText() {
        // 非法文本按转换失败处理返回 null（修复前会抛出 DateTimeParseException，绕过默认值）
        Assert.assertNull(PeriodConverter.INSTANCE.convert("abc", Period.class));
    }

    @Test
    public void testConvertFromIncompatibleTemporalAmount() {
        // Duration 无法转换为 Period，返回 null（修复前抛出 UnsupportedTemporalTypeException）
        Assert.assertNull(PeriodConverter.INSTANCE.convert(Duration.ofHours(1), Period.class));
    }

    @Test
    public void testConvertNull() {
        Assert.assertNull(PeriodConverter.INSTANCE.convert(null, Period.class));
    }

    @Test
    public void testConvertWithDefaultValue() {
        // 转换失败时返回默认值
        Assert.assertEquals(Period.ofDays(5), PeriodConverter.INSTANCE.convert("abc", Period.class, Period.ofDays(5)));
        // 转换成功时忽略默认值
        Assert.assertEquals(Period.ofDays(1), PeriodConverter.INSTANCE.convert("P1D", Period.class, Period.ofDays(5)));
    }
}
