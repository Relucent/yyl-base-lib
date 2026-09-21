package com.github.relucent.base.common.time;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

import org.junit.Assert;
import org.junit.Test;

import com.github.relucent.base.common.constant.ZoneIdConstant;

public class TemporalAccessorUtilParseTest {

    // ===================== Unix 时间戳字符串 =====================

    @Test
    public void testParseUnixTimestampMillis() {
        ZoneUtil.setDefaultZoneId(ZoneIdConstant.UTC);
        TemporalAccessor ta = TemporalAccessorUtil.parse("1700000000000");
        OffsetDateTime odt = OffsetDateTimeUtil.parse("1700000000000");
        // 13 位视为毫秒
        Assert.assertEquals(1700000000000L, TemporalAccessorUtil.toEpochMilli(ta));
        Assert.assertEquals(1700000000000L, odt.toInstant().toEpochMilli());
    }

    @Test
    public void testParseUnixTimestampSeconds() {
        ZoneUtil.setDefaultZoneId(ZoneIdConstant.UTC);
        TemporalAccessor ta = TemporalAccessorUtil.parse("1700000000");
        // 10 位视为秒 -> 等效毫秒
        Assert.assertEquals(1700000000000L, TemporalAccessorUtil.toEpochMilli(ta));
    }

    @Test
    public void testParseTimestampNegative() {
        ZoneUtil.setDefaultZoneId(ZoneIdConstant.UTC);
        TemporalAccessor ta = TemporalAccessorUtil.parse("-1700000000000");
        Assert.assertEquals(-1700000000000L, TemporalAccessorUtil.toEpochMilli(ta));
    }

    // ===================== trim =====================

    @Test
    public void testParseTrim() {
        TemporalAccessor ta = TemporalAccessorUtil.parse("  2025-11-14  ");
        Assert.assertNotNull(ta);
        Assert.assertEquals(2025, ta.get(ChronoField.YEAR));
        Assert.assertEquals(11, ta.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(14, ta.get(ChronoField.DAY_OF_MONTH));
    }

    // ===================== 扩展格式覆盖 =====================

    @Test
    public void testParseUsStyle() {
        TemporalAccessor ta = TemporalAccessorUtil.parse("11/14/2025");
        Assert.assertEquals(2025, ta.get(ChronoField.YEAR));
        Assert.assertEquals(11, ta.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(14, ta.get(ChronoField.DAY_OF_MONTH));
    }

    @Test
    public void testParseChinese() {
        TemporalAccessor ta = TemporalAccessorUtil.parse("2025年11月14日");
        Assert.assertEquals(2025, ta.get(ChronoField.YEAR));
        Assert.assertEquals(11, ta.get(ChronoField.MONTH_OF_YEAR));
        Assert.assertEquals(14, ta.get(ChronoField.DAY_OF_MONTH));
    }

    @Test
    public void testParseAmPm() {
        TemporalAccessor ta = TemporalAccessorUtil.parse("05:00 PM");
        LocalDateTime ldt = TemporalAccessorUtil.toLocalDateTime(ta);
        Assert.assertEquals(17, ldt.getHour());
        Assert.assertEquals(0, ldt.getMinute());
    }

    @Test
    public void testParseCommaFraction() {
        TemporalAccessor ta = TemporalAccessorUtil.parse("17:00:00,123");
        LocalDateTime ldt = TemporalAccessorUtil.toLocalDateTime(ta);
        Assert.assertEquals(17, ldt.getHour());
        Assert.assertEquals(123_000_000, ldt.getNano());
    }

    @Test
    public void testParseYearMonth() {
        TemporalAccessor ta = TemporalAccessorUtil.parse("2025-11");
        Assert.assertEquals(2025, ta.get(ChronoField.YEAR));
        Assert.assertEquals(11, ta.get(ChronoField.MONTH_OF_YEAR));
    }

    @Test
    public void testParseYearMonthNotConsumeFullDate() {
        // yyyy-MM 不应误吞完整日期
        TemporalAccessor ta = TemporalAccessorUtil.parse("2025-11-14");
        Assert.assertEquals(14, ta.get(ChronoField.DAY_OF_MONTH));
    }

    // ===================== 缺失项补全验证 =====================

    @Test
    public void testOffsetParseBareTimeCompletesToEpoch() {
        ZoneUtil.setDefaultZoneId(ZoneIdConstant.UTC);
        OffsetDateTime odt = OffsetDateTimeUtil.parse("17:00:00");
        Assert.assertEquals(1970, odt.getYear());
        Assert.assertEquals(1, odt.getMonthValue());
        Assert.assertEquals(1, odt.getDayOfMonth());
        Assert.assertEquals(17, odt.getHour());
        Assert.assertEquals(ZoneOffset.UTC, odt.getOffset());
    }

    @Test
    public void testOffsetParseDateOnlyCompletesMidnight() {
        ZoneUtil.setDefaultZoneId(ZoneIdConstant.UTC);
        OffsetDateTime odt = OffsetDateTimeUtil.parse("2025-11-14");
        Assert.assertEquals(2025, odt.getYear());
        Assert.assertEquals(11, odt.getMonthValue());
        Assert.assertEquals(14, odt.getDayOfMonth());
        Assert.assertEquals(0, odt.getHour());
        Assert.assertEquals(ZoneOffset.UTC, odt.getOffset());
    }

    @Test
    public void testZonedParseDateOnlyUsesDefaultZone() {
        ZoneUtil.setDefaultZoneId(ZoneIdConstant.UTC);
        ZonedDateTime zdt = ZonedDateTimeUtil.parse("2025-11-14");
        Assert.assertEquals(2025, zdt.getYear());
        Assert.assertEquals(11, zdt.getMonthValue());
        Assert.assertEquals(14, zdt.getDayOfMonth());
        Assert.assertEquals(ZoneIdConstant.UTC, zdt.getZone());
    }

    @Test
    public void testParseUnparseableReturnsNull() {
        Assert.assertNull(TemporalAccessorUtil.parse("not-a-date"));
        Assert.assertNull(TemporalAccessorUtil.parse(""));
        Assert.assertNull(TemporalAccessorUtil.parse("   "));
    }
}
