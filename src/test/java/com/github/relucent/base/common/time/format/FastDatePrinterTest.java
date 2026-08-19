package com.github.relucent.base.common.time.format;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link FastDatePrinter} 单元测试<br>
 * 重点固化时区模式（{@code Z} / {@code ZZ} / {@code ZZZ} / {@code X} / {@code XX} / {@code XXX}）的打印规则
 */
public class FastDatePrinterTest {

    /** 东八区（无夏令时，偏移固定） */
    private static final TimeZone GMT_PLUS_8 = TimeZone.getTimeZone("GMT+08:00");
    /** 西五区（无夏令时，偏移固定） */
    private static final TimeZone GMT_MINUS_5 = TimeZone.getTimeZone("GMT-05:00");
    /** 零时区 */
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    /** 测试用固定时刻：2021-05-03T02:13:20Z */
    private static final long MILLIS = 1620008000000L;

    private static FastDatePrinter printer(String pattern, TimeZone zone) {
        return new FastDatePrinter(pattern, zone, Locale.ENGLISH);
    }

    @Test
    public void testFormatZoneNumber() {
        FastDatePrinter printer = printer("Z", GMT_PLUS_8);
        Assert.assertEquals("+0800", printer.format(MILLIS));
    }

    @Test
    public void testFormatZoneZWidth2() {
        FastDatePrinter printer = printer("ZZ", GMT_PLUS_8);
        // 宽度2 输出带冒号的 ISO8601 形式
        Assert.assertEquals("+08:00", printer.format(MILLIS));
    }

    @Test
    public void testFormatZoneZWidth3() {
        FastDatePrinter printer = printer("ZZZ", GMT_PLUS_8);
        // 宽度>=3 输出带冒号形式
        Assert.assertEquals("+08:00", printer.format(MILLIS));
    }

    @Test
    public void testFormatZoneIso8601X() {
        Assert.assertEquals("+08", printer("X", GMT_PLUS_8).format(MILLIS));
        Assert.assertEquals("+0800", printer("XX", GMT_PLUS_8).format(MILLIS));
        Assert.assertEquals("+08:00", printer("XXX", GMT_PLUS_8).format(MILLIS));
    }

    @Test
    public void testFormatZoneIso8601XNegative() {
        Assert.assertEquals("-05", printer("X", GMT_MINUS_5).format(MILLIS));
        Assert.assertEquals("-0500", printer("XX", GMT_MINUS_5).format(MILLIS));
        Assert.assertEquals("-05:00", printer("XXX", GMT_MINUS_5).format(MILLIS));
    }

    @Test
    public void testFormatZoneAtZeroOffset() {
        // 零偏移时：X 系列输出 "Z"，而 Z 系列输出数字形式
        Assert.assertEquals("+0000", printer("Z", UTC).format(MILLIS));
        Assert.assertEquals("Z", printer("ZZ", UTC).format(MILLIS));
        Assert.assertEquals("+00:00", printer("ZZZ", UTC).format(MILLIS));
        Assert.assertEquals("Z", printer("X", UTC).format(MILLIS));
        Assert.assertEquals("Z", printer("XX", UTC).format(MILLIS));
        Assert.assertEquals("Z", printer("XXX", UTC).format(MILLIS));
    }

    @Test
    public void testFormatDateAndLongConsistent() {
        FastDatePrinter printer = printer("yyyy-MM-dd'T'HH:mm:ss", GMT_PLUS_8);
        String fromLong = printer.format(MILLIS);
        String fromDate = printer.format(new Date(MILLIS));
        Assert.assertEquals("2021-05-03T10:13:20", fromLong);
        Assert.assertEquals(fromLong, fromDate);
    }

    @Test
    public void testFormatCalendar() {
        FastDatePrinter printer = printer("yyyy-MM-dd'T'HH:mm:ss", GMT_PLUS_8);
        Calendar calendar = new GregorianCalendar(GMT_PLUS_8, Locale.ENGLISH);
        calendar.setTimeInMillis(MILLIS);
        Assert.assertEquals("2021-05-03T10:13:20", printer.format(calendar));
    }

    @Test
    public void testFormatAppendable() {
        FastDatePrinter printer = printer("yyyy-MM-dd", GMT_PLUS_8);
        StringBuilder buffer = new StringBuilder("prefix:");
        StringBuilder result = printer.format(MILLIS, buffer);
        Assert.assertSame(buffer, result);
        Assert.assertEquals("prefix:2021-05-03", result.toString());
    }

    @Test
    public void testFormatObject() {
        FastDatePrinter printer = printer("yyyy-MM-dd", GMT_PLUS_8);
        Assert.assertEquals("2021-05-03", printer.format((Object) new Date(MILLIS)));
        Assert.assertEquals("2021-05-03", printer.format((Object) Long.valueOf(MILLIS)));
    }

    @Test
    public void testLiteralQuote() {
        FastDatePrinter printer = printer("yyyy-MM-dd'T'HH:mm:ss'Z'", UTC);
        // 单引号包裹的 Z 是字面量，不会被当作时区
        Assert.assertEquals("2021-05-03T02:13:20Z", printer.format(MILLIS));
    }

    @Test
    public void testSimpleEscapeQuote() {
        FastDatePrinter printer = printer("yyyy'-'MM", GMT_PLUS_8);
        Assert.assertEquals("2021-05", printer.format(MILLIS));
    }

    @Test
    public void testAccessors() {
        FastDatePrinter printer = printer("yyyy-MM-dd", GMT_PLUS_8);
        Assert.assertEquals("yyyy-MM-dd", printer.getPattern());
        Assert.assertEquals(GMT_PLUS_8, printer.getTimeZone());
        Assert.assertEquals(Locale.ENGLISH, printer.getLocale());
        Assert.assertTrue(printer.getMaxLengthEstimate() >= "2021-05-03".length());
    }

    @Test
    public void testEqualsAndHashCode() {
        FastDatePrinter a = printer("yyyy-MM-dd", GMT_PLUS_8);
        FastDatePrinter b = printer("yyyy-MM-dd", GMT_PLUS_8);
        FastDatePrinter c = printer("yyyy-MM-dd", UTC);
        Assert.assertNotSame(a, b);
        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());
        Assert.assertNotEquals(a, c);
        Assert.assertFalse(a.equals(null));
        Assert.assertFalse(a.equals("yyyy-MM-dd"));
    }

    @Test
    public void testToString() {
        FastDatePrinter printer = printer("yyyy-MM-dd", GMT_PLUS_8);
        String text = printer.toString();
        Assert.assertTrue(text.contains("yyyy-MM-dd"));
        Assert.assertTrue(text.contains("GMT+08:00"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidTimezoneWidth() {
        // X 的宽度只支持 1-3，宽度4应抛出异常
        printer("XXXX", UTC);
    }
}
