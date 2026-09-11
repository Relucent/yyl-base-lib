package com.github.relucent.base.common.convert.impl;

import java.time.Duration;
import java.time.Period;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link DurationConverter} 单元测试
 */
public class DurationConverterTest {

    @Test
    public void testConvertFromDuration() {
        Duration source = Duration.ofHours(1);
        Assert.assertSame(source, DurationConverter.INSTANCE.convert(source, Duration.class));
    }

    @Test
    public void testConvertFromNumber() {
        Assert.assertEquals(Duration.ofSeconds(1), DurationConverter.INSTANCE.convert(1000L, Duration.class));
    }

    @Test
    public void testConvertFromIsoText() {
        Assert.assertEquals(Duration.ofHours(1), DurationConverter.INSTANCE.convert("PT1H", Duration.class));
    }

    @Test
    public void testConvertFromIllegalText() {
        // 非法文本按转换失败处理返回 null（修复前会抛出 DateTimeParseException，绕过默认值）
        Assert.assertNull(DurationConverter.INSTANCE.convert("abc", Duration.class));
    }

    @Test
    public void testConvertFromIncompatibleTemporalAmount() {
        // Period 无法转换为 Duration，返回 null（修复前抛出 UnsupportedTemporalTypeException）
        Assert.assertNull(DurationConverter.INSTANCE.convert(Period.ofDays(1), Duration.class));
    }

    @Test
    public void testConvertNull() {
        Assert.assertNull(DurationConverter.INSTANCE.convert(null, Duration.class));
    }

    @Test
    public void testConvertWithDefaultValue() {
        // 转换失败时返回默认值
        Assert.assertEquals(Duration.ofHours(1), DurationConverter.INSTANCE.convert("abc", Duration.class, Duration.ofHours(1)));
        // 转换成功时忽略默认值
        Assert.assertEquals(Duration.ofSeconds(1), DurationConverter.INSTANCE.convert(1000L, Duration.class, Duration.ofHours(1)));
    }
}
