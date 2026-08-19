package com.github.relucent.base.common.time.format;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link FastDateParser} 单元测试<br>
 * 重点验证时区模式（{@code Z} / {@code ZZ} / {@code ZZZ} / {@code X} / {@code XX} / {@code XXX}）的解析结果
 * 与 {@link FastDatePrinter} 的打印结果<b>对称</b>（打印后再解析应还原为同一时刻）。<br>
 * 历史上 {@code RFC_822_TIME_ZONE} 只接受 {@code +0800}，导致打印出的 {@code +08:00} 无法解析，本测试用于固化该修复。
 */
public class FastDateParserTest {

    /** 东八区（无夏令时，偏移固定） */
    private static final TimeZone GMT_PLUS_8 = TimeZone.getTimeZone("GMT+08:00");
    /** 西五区（无夏令时，偏移固定） */
    private static final TimeZone GMT_MINUS_5 = TimeZone.getTimeZone("GMT-05:00");
    /** 零时区 */
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    /** 时间部分格式 */
    private static final String BASE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
    /** 全部受测时区模式 */
    private static final String[] ZONE_PATTERNS = { "Z", "ZZ", "ZZZ", "X", "XX", "XXX" };
    /** 固定时刻：2021-05-03T02:13:20Z */
    private static final long MILLIS = 1620008000000L;

    private static FastDateFormat format(String pattern, TimeZone zone) {
        return FastDateFormat.getInstance(pattern, zone, Locale.ENGLISH);
    }

    /**
     * 打印 -> 解析 往返一致性断言
     */
    private static void assertRoundTrip(String pattern, TimeZone zone, long millis) {
        FastDateFormat format = format(pattern, zone);
        String text = format.format(millis);
        String info = "pattern=" + pattern + ", text=" + text + ", zone=" + zone.getID();

        ParsePosition pos = new ParsePosition(0);
        Date parsed = format.parse(text, pos);
        Assert.assertNotNull("解析结果为空: " + info, parsed);
        Assert.assertEquals("解析未消费完整字符串: " + info, text.length(), pos.getIndex());
        Assert.assertEquals("解析后时刻不一致: " + info, millis, parsed.getTime());
    }

    @Test
    public void testZoneRoundTripInGmtPlus8() {
        for (String zonePattern : ZONE_PATTERNS) {
            assertRoundTrip(BASE_PATTERN + zonePattern, GMT_PLUS_8, MILLIS);
        }
    }

    @Test
    public void testZoneRoundTripInGmtMinus5() {
        for (String zonePattern : ZONE_PATTERNS) {
            assertRoundTrip(BASE_PATTERN + zonePattern, GMT_MINUS_5, MILLIS);
        }
    }

    @Test
    public void testZoneRoundTripInUtc() {
        for (String zonePattern : ZONE_PATTERNS) {
            assertRoundTrip(BASE_PATTERN + zonePattern, UTC, MILLIS);
        }
    }

    @Test
    public void testParseZoneNumberWithoutColon() throws ParseException {
        // Z 宽度1 的打印形式为 "+0800"
        Assert.assertEquals(MILLIS, format(BASE_PATTERN + "Z", GMT_PLUS_8).parse("2021-05-03T10:13:20+0800").getTime());
    }

    @Test
    public void testParseZoneNumberWithColon() throws ParseException {
        // Z 宽度1 也应兼容带冒号的 ISO8601 形式
        Assert.assertEquals(MILLIS, format(BASE_PATTERN + "Z", GMT_PLUS_8).parse("2021-05-03T10:13:20+08:00").getTime());
    }

    @Test
    public void testParseZoneZWidth2WithColon() throws ParseException {
        // ZZ 宽度2 的打印形式为 "+08:00"
        Assert.assertEquals(MILLIS, format(BASE_PATTERN + "ZZ", GMT_PLUS_8).parse("2021-05-03T10:13:20+08:00").getTime());
    }

    @Test
    public void testParseZoneZWidth3WithColon() throws ParseException {
        // ZZZ 宽度3 的打印形式为 "+08:00"，修复前此处解析失败
        Assert.assertEquals(MILLIS, format(BASE_PATTERN + "ZZZ", GMT_PLUS_8).parse("2021-05-03T10:13:20+08:00").getTime());
    }

    @Test
    public void testParseZeroZoneLiteral() throws ParseException {
        // 零时区字面量 Z：宽度1（TimeZoneStrategy）与宽度2/3（ISO8601策略）都应支持
        Assert.assertEquals(MILLIS, format(BASE_PATTERN + "Z", UTC).parse("2021-05-03T02:13:20Z").getTime());
        Assert.assertEquals(MILLIS, format(BASE_PATTERN + "ZZZ", UTC).parse("2021-05-03T02:13:20Z").getTime());
        Assert.assertEquals(MILLIS, format(BASE_PATTERN + "XX", UTC).parse("2021-05-03T02:13:20Z").getTime());
    }

    @Test
    public void testParseIso8601HoursOnly() throws ParseException {
        // X 宽度1 解析 "+08"
        Assert.assertEquals(MILLIS, format(BASE_PATTERN + "X", GMT_PLUS_8).parse("2021-05-03T10:13:20+08").getTime());
    }

    @Test
    public void testParseNegativeOffset() throws ParseException {
        Assert.assertEquals(MILLIS, format(BASE_PATTERN + "Z", GMT_MINUS_5).parse("2021-05-02T21:13:20-0500").getTime());
    }

    @Test
    public void testParseZoneNameText() throws ParseException {
        // z 使用区域/时区文本策略，可解析 GMT 偏移量形式
        FastDateFormat format = format(BASE_PATTERN + " z", GMT_PLUS_8);
        Assert.assertEquals(MILLIS, format.parse("2021-05-03T10:13:20 GMT+08:00").getTime());
    }

    @Test
    public void testParseText() throws ParseException {
        Assert.assertEquals(MILLIS, format("yyyy-MM-dd HH:mm:ss", GMT_PLUS_8).parse("2021-05-03 10:13:20").getTime());
    }

    @Test
    public void testParseWithParsePosition() {
        FastDateFormat format = format("yyyy-MM-dd", GMT_PLUS_8);
        ParsePosition pos = new ParsePosition(0);
        Date date = format.parse("2021-05-03", pos);
        Assert.assertNotNull(date);
        Assert.assertEquals(10, pos.getIndex());
    }

    @Test
    public void testParseObject() {
        FastDateFormat format = format("yyyy-MM-dd", GMT_PLUS_8);
        ParsePosition pos = new ParsePosition(0);
        Object value = format.parseObject("2021-05-03", pos);
        Assert.assertTrue(value instanceof Date);
        Assert.assertEquals(10, pos.getIndex());
    }

    @Test
    public void testParseErrorIndexOnFailure() {
        FastDateFormat format = format("yyyy-MM-dd", GMT_PLUS_8);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertNull(format.parse("not-a-date", pos));
        Assert.assertTrue("解析失败时应标记错误位置", pos.getErrorIndex() >= 0);
    }

    @Test
    public void testParseThrowsOnFailure() {
        try {
            format("yyyy-MM-dd", GMT_PLUS_8).parse("not-a-date");
            Assert.fail("非法输入应当抛出 ParseException");
        } catch (ParseException expected) {
            // 符合预期
        }
    }

    @Test
    public void testParseToleratesTrailingText() {
        // 宽松语义：解析采用前缀匹配（lookingAt），不要求消费完整输入，
        // 与 DateFormat#parse(String, ParsePosition) 的历史行为保持一致
        FastDateFormat format = format(BASE_PATTERN + "Z", GMT_PLUS_8);
        String source = "2021-05-03T10:13:20+0800-extra";
        ParsePosition pos = new ParsePosition(0);
        Date parsed = format.parse(source, pos);
        Assert.assertNotNull(parsed);
        Assert.assertEquals(MILLIS, parsed.getTime());
        // 已消费的字符数 = "2021-05-03T10:13:20+0800" 的长度（24）
        Assert.assertEquals(24, pos.getIndex());
        Assert.assertEquals(-1, pos.getErrorIndex());
    }

    @Test
    public void testAbbreviatedYear() throws ParseException {
        // 两位年份按默认世纪窗口（now-80 年）补齐
        Date date = format("yy-MM-dd", GMT_PLUS_8).parse("21-05-03");
        Calendar calendar = Calendar.getInstance(GMT_PLUS_8, Locale.ENGLISH);
        calendar.setTime(date);
        Assert.assertEquals(2021, calendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.MAY, calendar.get(Calendar.MONTH));
        Assert.assertEquals(3, calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Test
    public void testUnsupportedZoneWidth() {
        try {
            new FastDateParser(BASE_PATTERN + "XXXX", UTC, Locale.ENGLISH, null);
            Assert.fail("不支持的 X 宽度应当抛出 IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // 符合预期
        }
    }
}
