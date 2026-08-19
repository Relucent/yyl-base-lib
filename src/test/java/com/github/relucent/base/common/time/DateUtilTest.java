package com.github.relucent.base.common.time;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DateUtilTest {

    private Map<String, Date> sample = new LinkedHashMap<>();

    @Before
    public void before() throws ParseException {
        Date now = DateUtil.now();
        for (String pattern : Arrays.asList( //
                "yyyy-MM-dd'T'HH:mm:ss.SSSZZ", //
                "yyyy-MM-dd'T'HH:mm:ss.SSS", //
                "yyyy-MM-dd'T'HH:mm:ss", //
                "yyyy-MM-dd HH:mm:ss", //
                "yyyy-MM-dd HH:mm:ss.SSS", //
                "yyyy-MM-dd HH:mm", //
                "yyyy-MM-dd HH", //
                "yyyy-MM-dd", //
                "yyyy-MM", //
                "EEE MMM dd HH:mm:ss zzz yyyy", //
                "MMM d, yyyy HH:mm", //
                "d MMM yyyy h:m a", //
                "MMM d, yyyy", //
                "MM/dd/yyyy", //
                "yyyyMMdd", //
                "yyyyMM", //
                "yyyy")) {
            DateFormat formater = new SimpleDateFormat(pattern);
            String source = formater.format(now);
            Date date = formater.parse(source);
            sample.put(source, date);
        }
    }

    @After
    public void after() {
        // DateUtil 依赖 ZoneUtil 的全局默认时区，测试后还原
        ZoneUtil.resetDefaultZoneId();
    }

    @Test
    public void testParse() {
        for (Map.Entry<String, Date> entry : sample.entrySet()) {
            Assert.assertEquals(DateUtil.parseDate(entry.getKey()), entry.getValue());
        }
    }

    @Test
    public void testParseDateBlank() {
        Assert.assertNull(DateUtil.parseDate(null));
        Assert.assertNull(DateUtil.parseDate(""));
        Assert.assertNull(DateUtil.parseDate("   "));
    }

    @Test
    public void testParseDateInvalid() {
        Assert.assertNull(DateUtil.parseDate("not-a-date"));
    }

    @Test
    public void testParseDateWithZoneId() {
        ZoneId zoneId = ZoneId.of("Asia/Shanghai");
        Date date = DateUtil.parseDate("2021-09-11 17:30:00", zoneId);
        Assert.assertNotNull(date);
        Assert.assertEquals("2021-09-11 17:30:00", DateUtil.format(date, DateUtil.DATETIME_FORMAT, zoneId));
    }

    @Test
    public void testParseDateNullZoneIdFallsBackToDefault() {
        // 回归：zoneId 为 null 时不得抛 NPE，而应回退到默认时区
        ZoneUtil.setDefaultZoneId(ZoneId.of("Asia/Shanghai"));
        Date date = DateUtil.parseDate("2021-09-11 17:30:00", (ZoneId) null);
        Assert.assertNotNull(date);
        Assert.assertEquals(1631352600000L, date.getTime());
    }

    @Test
    public void testParseDateWithPatternNullZoneIdFallsBackToDefault() {
        // 回归：zoneId 为 null 时不得抛 NPE
        ZoneUtil.setDefaultZoneId(ZoneId.of("Asia/Shanghai"));
        Date date = DateUtil.parseDate("2021/09/11", "yyyy/MM/dd", null);
        Assert.assertNotNull(date);
        Assert.assertEquals("2021-09-11", DateUtil.format(date, DateUtil.DATE_FORMAT, ZoneId.of("Asia/Shanghai")));
    }

    @Test
    public void testParseDateWithPatternInvalidPattern() {
        // 回归：非法 pattern 触发 RuntimeException 时应被捕获并返回 null
        Assert.assertNull(DateUtil.parseDate("2021-09-11", "yyyy-MM-dd'T'"));
    }

    @Test
    public void testParseDateIsoZeroZone() {
        // JSON.stringify 的零时区格式（24 字符）
        Date date = DateUtil.parseDate("2021-09-11T09:30:00.000Z", ZoneId.of("Asia/Shanghai"));
        Assert.assertNotNull(date);
        Assert.assertEquals("2021-09-11T17:30:00", DateUtil.format(date, DateUtil.ISO8601_FORMAT, ZoneId.of("Asia/Shanghai")));
    }

    @Test
    public void testFormatWithZoneId() {
        Date date = new Date(1631352600000L);// 2021-09-11T09:30:00Z
        Assert.assertEquals("2021-09-11 17:30:00", DateUtil.format(date, DateUtil.DATETIME_FORMAT, ZoneId.of("Asia/Shanghai")));
        Assert.assertEquals("2021-09-11 09:30:00", DateUtil.format(date, DateUtil.DATETIME_FORMAT, ZoneId.of("UTC")));
    }

    @Test
    public void testFormatNullZoneIdFallsBackToDefault() {
        // 回归：zoneId 为 null 时不得抛 NPE
        ZoneUtil.setDefaultZoneId(ZoneId.of("Asia/Shanghai"));
        Date date = new Date(1631352600000L);
        Assert.assertEquals("2021-09-11 17:30:00", DateUtil.format(date, DateUtil.DATETIME_FORMAT, null));
    }

    @Test
    public void testFormatNullDate() {
        Assert.assertNull(DateUtil.format((Date) null));
        Assert.assertNull(DateUtil.format(null, DateUtil.DATE_FORMAT));
        Assert.assertNull(DateUtil.format(null, DateUtil.DATE_FORMAT, ZoneId.of("UTC")));
        Assert.assertNull(DateUtil.formatDateTime(null));
        Assert.assertNull(DateUtil.formatDate(null));
    }

    @Test
    public void testFormatShortcuts() {
        Date date = new Date(1631352600000L);
        ZoneUtil.setDefaultZoneId(ZoneId.of("Asia/Shanghai"));
        Assert.assertEquals("2021-09-11T17:30:00", DateUtil.format(date));
        Assert.assertEquals("2021-09-11 17:30:00", DateUtil.formatDateTime(date));
        Assert.assertEquals("2021-09-11", DateUtil.formatDate(date));
    }

    @Test
    public void testMinMax() {
        Date a = new Date(1000L);
        Date b = new Date(2000L);
        Assert.assertEquals(a, DateUtil.min(a, b));
        Assert.assertEquals(b, DateUtil.max(a, b));
        Assert.assertEquals(a, DateUtil.min(b, a));
        Assert.assertEquals(b, DateUtil.max(b, a));
        Assert.assertEquals(a, DateUtil.min(a, a));
        Assert.assertEquals(a, DateUtil.max(a, a));
    }

    @Test
    public void testMinMaxConstants() {
        Assert.assertTrue(DateUtil.min().before(DateUtil.max()));
        Assert.assertEquals(Long.valueOf(DateUtil.MIN_MILLIS), Long.valueOf(DateUtil.min().getTime()));
        Assert.assertEquals(Long.valueOf(DateUtil.MAX_MILLIS), Long.valueOf(DateUtil.max().getTime()));
        Assert.assertTrue(DateUtil.now().after(DateUtil.min()));
        Assert.assertTrue(DateUtil.now().before(DateUtil.max()));
    }

    @Test
    public void testToDate() {
        ZoneUtil.setDefaultZoneId(ZoneId.of("Asia/Shanghai"));
        Assert.assertEquals(new Date(1631352600000L), DateUtil.toDate(LocalDateTime.of(2021, 9, 11, 17, 30, 0)));
        Assert.assertEquals(new Date(1631289600000L), DateUtil.toDate(LocalDate.of(2021, 9, 11)));
    }

    @Test
    public void testGetFieldValue() {
        Date date = DateUtil.toDate(LocalDate.of(2021, 5, 3));
        Assert.assertEquals(2021, DateUtil.getFieldValue(date, DateUnit.YEAR));
        // 5 月属于上半年，HALFYEAR 取值为 0
        Assert.assertEquals(0, DateUtil.getFieldValue(date, DateUnit.HALFYEAR));
        // 修复后 QUARTER 取值为 1-4
        Assert.assertEquals(2, DateUtil.getFieldValue(date, DateUnit.QUARTER));
        // MONTH 取值与 Calendar.MONTH 一致，从 0 开始
        Assert.assertEquals(Calendar.MAY, DateUtil.getFieldValue(date, DateUnit.MONTH));
        Assert.assertEquals(3, DateUtil.getFieldValue(date, DateUnit.DATE));
        Assert.assertEquals(-1, DateUtil.getFieldValue(date, DateUnit.HOUR_OF_DAY));
        Assert.assertEquals(-1, DateUtil.getFieldValue(date, DateUnit.MINUTE));
        Assert.assertEquals(-1, DateUtil.getFieldValue(date, DateUnit.SECOND));
    }

    @Test
    public void testGetFieldValueHalfYearBoundary() {
        // 6 月末属于上半年，7 月初进入下半年
        Date june = DateUtil.toDate(LocalDate.of(2021, 6, 30));
        Date july = DateUtil.toDate(LocalDate.of(2021, 7, 1));
        Assert.assertEquals(0, DateUtil.getFieldValue(june, DateUnit.HALFYEAR));
        Assert.assertEquals(1, DateUtil.getFieldValue(july, DateUnit.HALFYEAR));
        // 季度取值随月份推进：第二季度末 -> 第三季度初
        Assert.assertEquals(2, DateUtil.getFieldValue(june, DateUnit.QUARTER));
        Assert.assertEquals(3, DateUtil.getFieldValue(july, DateUnit.QUARTER));
    }

    @Test
    public void testGetBeginEnd() {
        Date date = DateUtil.toDate(LocalDateTime.of(2021, 5, 3, 10, 13, 20));
        // 以字段方式断言，避免依赖运行环境的默认时区
        assertFields(DateUtil.getBegin(date, DateUnit.DATE), 2021, Calendar.MAY, 3, 0, 0, 0);
        assertFields(DateUtil.getEnd(date, DateUnit.DATE), 2021, Calendar.MAY, 3, 23, 59, 59);
        // 第二季度：4月1日 - 6月30日
        assertFields(DateUtil.getBegin(date, DateUnit.QUARTER), 2021, Calendar.APRIL, 1, 0, 0, 0);
        assertFields(DateUtil.getEnd(date, DateUnit.QUARTER), 2021, Calendar.JUNE, 30, 23, 59, 59);
        // 上半年：1月1日 - 6月30日
        assertFields(DateUtil.getBegin(date, DateUnit.HALFYEAR), 2021, Calendar.JANUARY, 1, 0, 0, 0);
        assertFields(DateUtil.getEnd(date, DateUnit.HALFYEAR), 2021, Calendar.JUNE, 30, 23, 59, 59);
        // 年：1月1日 - 12月31日
        assertFields(DateUtil.getBegin(date, DateUnit.YEAR), 2021, Calendar.JANUARY, 1, 0, 0, 0);
        assertFields(DateUtil.getEnd(date, DateUnit.YEAR), 2021, Calendar.DECEMBER, 31, 23, 59, 59);
    }

    @Test
    public void testGetBeginEndNull() {
        Assert.assertNull(DateUtil.getBegin(null, DateUnit.DATE));
        Assert.assertNull(DateUtil.getEnd(null, DateUnit.DATE));
    }

    /** 以日历字段方式断言时间，避免测试依赖运行环境的默认时区 */
    private static void assertFields(Date date, int year, int month, int day, int hour, int minute, int second) {
        Assert.assertNotNull(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String info = "实际=" + date;
        Assert.assertEquals(info, year, calendar.get(Calendar.YEAR));
        Assert.assertEquals(info, month, calendar.get(Calendar.MONTH));
        Assert.assertEquals(info, day, calendar.get(Calendar.DAY_OF_MONTH));
        Assert.assertEquals(info, hour, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(info, minute, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(info, second, calendar.get(Calendar.SECOND));
    }
}
