package com.github.relucent.base.common.convert.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link DateConverter} 单元测试
 */
public class DateConverterTest {

    @Test
    public void testConvertNull() {
        Assert.assertNull(DateConverter.INSTANCE.convert(null, Date.class));
    }

    @Test
    public void testConvertDate() {
        Date source = new Date(1700000000000L);
        Date result = DateConverter.INSTANCE.convert(source, Date.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(source.getTime(), result.getTime());
        // source 即目标类型时,BasicConverter 走 isInstance 短路,直接返回原对象
        Assert.assertSame(source, result);
    }

    @Test
    public void testConvertNumber() {
        Date result = DateConverter.INSTANCE.convert(0L, Date.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(0L, result.getTime());
    }

    @Test
    public void testConvertNewDatePattern() {
        // "new Date(0)" 模式字符串(修复前 matches()&&find() 组合导致无法匹配)
        Date result = DateConverter.INSTANCE.convert("new Date(0)", Date.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(0L, result.getTime());
    }

    @Test
    public void testConvertNewDatePatternWithMillis() {
        Date result = DateConverter.INSTANCE.convert("new Date(1700000000000)", Date.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(1700000000000L, result.getTime());
    }

    @Test
    public void testConvertCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.setTimeInMillis(0L);
        Date result = DateConverter.INSTANCE.convert(calendar, Date.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(0L, result.getTime());
    }

    @Test
    public void testConvertInstant() {
        Instant instant = Instant.ofEpochMilli(1700000000000L);
        Date result = DateConverter.INSTANCE.convert(instant, Date.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(1700000000000L, result.getTime());
    }

    @Test
    public void testConvertDateString() {
        Date result = DateConverter.INSTANCE.convert("2023-01-01 00:00:00", Date.class);
        Assert.assertNotNull(result);
        // 验证能解析为 2023-01-01
        Calendar cal = Calendar.getInstance();
        cal.setTime(result);
        Assert.assertEquals(2023, cal.get(Calendar.YEAR));
        Assert.assertEquals(0, cal.get(Calendar.MONTH));
        Assert.assertEquals(1, cal.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testConvertInvalidString() {
        Assert.assertNull(DateConverter.INSTANCE.convert("not-a-date", Date.class));
    }

    @Test
    public void testConvertToSqlDate() {
        java.sql.Date result = (java.sql.Date) DateConverter.INSTANCE.convert(0L, java.sql.Date.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(0L, result.getTime());
        Assert.assertTrue(result instanceof java.sql.Date);
    }

    @Test
    public void testConvertToSqlTime() {
        java.sql.Time result = (java.sql.Time) DateConverter.INSTANCE.convert(0L, java.sql.Time.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(0L, result.getTime());
        Assert.assertTrue(result instanceof java.sql.Time);
    }

    @Test
    public void testConvertToTimestamp() {
        Timestamp result = (Timestamp) DateConverter.INSTANCE.convert(0L, Timestamp.class);
        Assert.assertNotNull(result);
        Assert.assertEquals(0L, result.getTime());
        Assert.assertTrue(result instanceof Timestamp);
    }

    @Test
    public void testConvertWithDefault() {
        Assert.assertEquals(java.sql.Date.valueOf("2023-01-01"),
            DateConverter.INSTANCE.convert("invalid", Date.class, java.sql.Date.valueOf("2023-01-01")));
    }
}
