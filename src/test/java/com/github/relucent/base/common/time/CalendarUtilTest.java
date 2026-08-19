package com.github.relucent.base.common.time;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

public class CalendarUtilTest {

    /** 构造指定年月日的日历（时分秒毫秒归零） */
    private static Calendar calendar(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, date, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private static void assertCalendar(Calendar actual, int year, int month, int date, int hour, int minute, int second, int millis) {
        Assert.assertEquals("year", year, actual.get(Calendar.YEAR));
        Assert.assertEquals("month", month, actual.get(Calendar.MONTH));
        Assert.assertEquals("date", date, actual.get(Calendar.DATE));
        Assert.assertEquals("hour", hour, actual.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals("minute", minute, actual.get(Calendar.MINUTE));
        Assert.assertEquals("second", second, actual.get(Calendar.SECOND));
        Assert.assertEquals("millis", millis, actual.get(Calendar.MILLISECOND));
    }

    @Test
    public void testGetBegin() {
        Calendar calendar = Calendar.getInstance();
        CalendarUtil.getBegin(calendar, DateUnit.YEAR);
        CalendarUtil.getBegin(calendar, DateUnit.HALFYEAR);
        CalendarUtil.getBegin(calendar, DateUnit.QUARTER);
        CalendarUtil.getBegin(calendar, DateUnit.MONTH);
        CalendarUtil.getBegin(calendar, DateUnit.DATE);
        CalendarUtil.getBegin(calendar, DateUnit.HOUR_OF_DAY);
        CalendarUtil.getBegin(calendar, DateUnit.MINUTE);
        CalendarUtil.getBegin(calendar, DateUnit.SECOND);
    }

    @Test
    public void testGetEnd() {
        Calendar calendar = Calendar.getInstance();
        CalendarUtil.getEnd(calendar, DateUnit.YEAR);
        CalendarUtil.getEnd(calendar, DateUnit.HALFYEAR);
        CalendarUtil.getEnd(calendar, DateUnit.QUARTER);
        CalendarUtil.getEnd(calendar, DateUnit.MONTH);
        CalendarUtil.getEnd(calendar, DateUnit.DATE);
        CalendarUtil.getEnd(calendar, DateUnit.HOUR_OF_DAY);
        CalendarUtil.getEnd(calendar, DateUnit.MINUTE);
        CalendarUtil.getEnd(calendar, DateUnit.SECOND);
    }

    // ==============================工厂转换==========================================

    @Test
    public void testToCalendar() {
        java.util.Date date = new java.util.Date(1700000000000L);
        Assert.assertEquals(1700000000000L, CalendarUtil.toCalendar(date).getTimeInMillis());
        Assert.assertEquals(1700000000000L, CalendarUtil.toCalendar(1700000000000L).getTimeInMillis());
    }

    @Test
    public void testToCalendarLocalDate() {
        Calendar calendar = CalendarUtil.toCalendar(java.time.LocalDate.of(2026, 9, 11));
        Assert.assertEquals(2026, calendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.SEPTEMBER, calendar.get(Calendar.MONTH));
        Assert.assertEquals(11, calendar.get(Calendar.DATE));
        Assert.assertEquals(0, calendar.get(Calendar.HOUR_OF_DAY));
    }

    @Test
    public void testToCalendarLocalDateTime() {
        Calendar calendar = CalendarUtil.toCalendar(java.time.LocalDateTime.of(2026, 9, 11, 17, 11, 12));
        Assert.assertEquals(2026, calendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.SEPTEMBER, calendar.get(Calendar.MONTH));
        Assert.assertEquals(11, calendar.get(Calendar.DATE));
        Assert.assertEquals(17, calendar.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals(11, calendar.get(Calendar.MINUTE));
        Assert.assertEquals(12, calendar.get(Calendar.SECOND));
    }

    // ==============================getFieldValue==========================================

    /**
     * 回归测试：季度必须返回 1-4。<br>
     * 历史缺陷为 {@code MONTH / 3}，返回 0-3，会让所有季度判断整体偏一个季度。
     */
    @Test
    public void testGetFieldValueQuarter() {
        int[] expectedByMonth = { //
                1, 1, 1, // 1月 2月 3月 → 一季度
                2, 2, 2, // 4月 5月 6月 → 二季度
                3, 3, 3, // 7月 8月 9月 → 三季度
                4, 4, 4, // 10月 11月 12月 → 四季度
        };
        for (int month = Calendar.JANUARY; month <= Calendar.DECEMBER; month++) {
            Calendar calendar = calendar(2026, month, 15);
            int actual = CalendarUtil.getFieldValue(calendar, DateUnit.QUARTER);
            Assert.assertEquals("month=" + (month + 1), expectedByMonth[month], actual);
        }
    }

    /** 季度边界：每个月最后一天与下个月第一天应分属相邻季度 */
    @Test
    public void testGetFieldValueQuarterBoundary() {
        Assert.assertEquals(1, CalendarUtil.getFieldValue(calendar(2026, Calendar.MARCH, 31), DateUnit.QUARTER));
        Assert.assertEquals(2, CalendarUtil.getFieldValue(calendar(2026, Calendar.APRIL, 1), DateUnit.QUARTER));
        Assert.assertEquals(2, CalendarUtil.getFieldValue(calendar(2026, Calendar.JUNE, 30), DateUnit.QUARTER));
        Assert.assertEquals(3, CalendarUtil.getFieldValue(calendar(2026, Calendar.JULY, 1), DateUnit.QUARTER));
        Assert.assertEquals(3, CalendarUtil.getFieldValue(calendar(2026, Calendar.SEPTEMBER, 30), DateUnit.QUARTER));
        Assert.assertEquals(4, CalendarUtil.getFieldValue(calendar(2026, Calendar.OCTOBER, 1), DateUnit.QUARTER));
        Assert.assertEquals(4, CalendarUtil.getFieldValue(calendar(2026, Calendar.DECEMBER, 31), DateUnit.QUARTER));
        Assert.assertEquals(1, CalendarUtil.getFieldValue(calendar(2027, Calendar.JANUARY, 1), DateUnit.QUARTER));
    }

    @Test
    public void testGetFieldValueOtherUnits() {
        Calendar calendar = calendar(2026, Calendar.SEPTEMBER, 20);
        Assert.assertEquals(2026, CalendarUtil.getFieldValue(calendar, DateUnit.YEAR));
        Assert.assertEquals(1, CalendarUtil.getFieldValue(calendar, DateUnit.HALFYEAR));
        Assert.assertEquals(3, CalendarUtil.getFieldValue(calendar, DateUnit.QUARTER));
        Assert.assertEquals(Calendar.SEPTEMBER, CalendarUtil.getFieldValue(calendar, DateUnit.MONTH));
        Assert.assertEquals(20, CalendarUtil.getFieldValue(calendar, DateUnit.DATE));
    }

    /** 半年边界：6月30日为上半年(0)，7月1日为下半年(1) */
    @Test
    public void testGetFieldValueHalfYearBoundary() {
        Assert.assertEquals(0, CalendarUtil.getFieldValue(calendar(2026, Calendar.JANUARY, 1), DateUnit.HALFYEAR));
        Assert.assertEquals(0, CalendarUtil.getFieldValue(calendar(2026, Calendar.JUNE, 30), DateUnit.HALFYEAR));
        Assert.assertEquals(1, CalendarUtil.getFieldValue(calendar(2026, Calendar.JULY, 1), DateUnit.HALFYEAR));
        Assert.assertEquals(1, CalendarUtil.getFieldValue(calendar(2026, Calendar.DECEMBER, 31), DateUnit.HALFYEAR));
    }

    /** 不支持小时/分钟/秒，返回 -1 */
    @Test
    public void testGetFieldValueUnsupportedUnits() {
        Calendar calendar = calendar(2026, Calendar.SEPTEMBER, 20);
        Assert.assertEquals(-1, CalendarUtil.getFieldValue(calendar, DateUnit.HOUR_OF_DAY));
        Assert.assertEquals(-1, CalendarUtil.getFieldValue(calendar, DateUnit.MINUTE));
        Assert.assertEquals(-1, CalendarUtil.getFieldValue(calendar, DateUnit.SECOND));
    }

    // ==============================getBegin / getEnd==========================================

    /**
     * 回归测试：按季度取起始/结束时间必须落在正确月份。<br>
     * 与 {@code getFieldValue(QUARTER)} 的 1-4 语义配套，修复前 getBegin/getEnd 依赖 0-3 计算。
     */
    @Test
    public void testGetBeginEndQuarterAll() {
        // {源月份, 起始月, 结束月, 结束日}
        int[][] cases = { //
                { Calendar.JANUARY, Calendar.JANUARY, Calendar.MARCH, 31 }, //
                { Calendar.APRIL, Calendar.APRIL, Calendar.JUNE, 30 }, //
                { Calendar.JULY, Calendar.JULY, Calendar.SEPTEMBER, 30 }, //
                { Calendar.OCTOBER, Calendar.OCTOBER, Calendar.DECEMBER, 31 }, //
        };
        for (int[] item : cases) {
            Calendar source = calendar(2026, item[0], 15);

            Calendar begin = CalendarUtil.getBegin(source, DateUnit.QUARTER);
            assertCalendar(begin, 2026, item[1], 1, 0, 0, 0, 0);

            Calendar end = CalendarUtil.getEnd(source, DateUnit.QUARTER);
            assertCalendar(end, 2026, item[2], item[3], 23, 59, 59, 999);
        }
    }

    @Test
    public void testGetBeginEndYear() {
        Calendar source = calendar(2026, Calendar.MAY, 20);
        assertCalendar(CalendarUtil.getBegin(source, DateUnit.YEAR), 2026, Calendar.JANUARY, 1, 0, 0, 0, 0);
        assertCalendar(CalendarUtil.getEnd(source, DateUnit.YEAR), 2026, Calendar.DECEMBER, 31, 23, 59, 59, 999);
    }

    @Test
    public void testGetBeginEndHalfYear() {
        // 上半年
        Calendar firstHalf = calendar(2026, Calendar.MAY, 20);
        assertCalendar(CalendarUtil.getBegin(firstHalf, DateUnit.HALFYEAR), 2026, Calendar.JANUARY, 1, 0, 0, 0, 0);
        assertCalendar(CalendarUtil.getEnd(firstHalf, DateUnit.HALFYEAR), 2026, Calendar.JUNE, 30, 23, 59, 59, 999);

        // 下半年
        Calendar secondHalf = calendar(2026, Calendar.AUGUST, 20);
        assertCalendar(CalendarUtil.getBegin(secondHalf, DateUnit.HALFYEAR), 2026, Calendar.JULY, 1, 0, 0, 0, 0);
        assertCalendar(CalendarUtil.getEnd(secondHalf, DateUnit.HALFYEAR), 2026, Calendar.DECEMBER, 31, 23, 59, 59, 999);
    }

    @Test
    public void testGetBeginEndMonth() {
        Calendar source = calendar(2026, Calendar.FEBRUARY, 10);
        assertCalendar(CalendarUtil.getBegin(source, DateUnit.MONTH), 2026, Calendar.FEBRUARY, 1, 0, 0, 0, 0);
        assertCalendar(CalendarUtil.getEnd(source, DateUnit.MONTH), 2026, Calendar.FEBRUARY, 28, 23, 59, 59, 999);
    }

    /** 闰年2月月末为29日 */
    @Test
    public void testGetEndMonthLeapYear() {
        Calendar leap = calendar(2024, Calendar.FEBRUARY, 10);
        assertCalendar(CalendarUtil.getEnd(leap, DateUnit.MONTH), 2024, Calendar.FEBRUARY, 29, 23, 59, 59, 999);
    }

    @Test
    public void testGetBeginEndDate() {
        Calendar source = calendar(2026, Calendar.SEPTEMBER, 11);
        assertCalendar(CalendarUtil.getBegin(source, DateUnit.DATE), 2026, Calendar.SEPTEMBER, 11, 0, 0, 0, 0);
        assertCalendar(CalendarUtil.getEnd(source, DateUnit.DATE), 2026, Calendar.SEPTEMBER, 11, 23, 59, 59, 999);
    }

    @Test
    public void testGetBeginEndHourOfDay() {
        Calendar source = Calendar.getInstance();
        source.set(2026, Calendar.SEPTEMBER, 11, 17, 11, 12);
        assertCalendar(CalendarUtil.getBegin(source, DateUnit.HOUR_OF_DAY), 2026, Calendar.SEPTEMBER, 11, 17, 0, 0, 0);
        assertCalendar(CalendarUtil.getEnd(source, DateUnit.HOUR_OF_DAY), 2026, Calendar.SEPTEMBER, 11, 17, 59, 59, 999);
    }

    @Test
    public void testGetBeginEndMinute() {
        Calendar source = Calendar.getInstance();
        source.set(2026, Calendar.SEPTEMBER, 11, 17, 11, 12);
        assertCalendar(CalendarUtil.getBegin(source, DateUnit.MINUTE), 2026, Calendar.SEPTEMBER, 11, 17, 11, 0, 0);
        assertCalendar(CalendarUtil.getEnd(source, DateUnit.MINUTE), 2026, Calendar.SEPTEMBER, 11, 17, 11, 59, 999);
    }

    @Test
    public void testGetBeginEndSecond() {
        Calendar source = Calendar.getInstance();
        source.set(2026, Calendar.SEPTEMBER, 11, 17, 11, 12);
        source.set(Calendar.MILLISECOND, 500);
        assertCalendar(CalendarUtil.getBegin(source, DateUnit.SECOND), 2026, Calendar.SEPTEMBER, 11, 17, 11, 12, 0);
        assertCalendar(CalendarUtil.getEnd(source, DateUnit.SECOND), 2026, Calendar.SEPTEMBER, 11, 17, 11, 12, 999);
    }

    /** getBegin 不会修改入参日历 */
    @Test
    public void testGetBeginDoesNotMutateSource() {
        Calendar source = calendar(2026, Calendar.MAY, 20);
        long before = source.getTimeInMillis();
        CalendarUtil.getBegin(source, DateUnit.YEAR);
        CalendarUtil.getEnd(source, DateUnit.YEAR);
        Assert.assertEquals(before, source.getTimeInMillis());
    }

    // ==============================WEEK==========================================

    /**
     * WEEK 起始/结束：周一为一周的开始（00:00:00.000），周日为一周的结束（23:59:59.999）<br>
     * 用例覆盖：周一当天、周日当天、周中、跨月的一周（周三在月末/次月初）
     */
    @Test
    public void testGetBeginEndWeek() {
        // 2026-09-07 是周一，2026-09-13 是周日
        assertCalendar(CalendarUtil.getBegin(calendar(2026, Calendar.SEPTEMBER, 9), DateUnit.WEEK), // 周三
                2026, Calendar.SEPTEMBER, 7, 0, 0, 0, 0);
        assertCalendar(CalendarUtil.getEnd(calendar(2026, Calendar.SEPTEMBER, 9), DateUnit.WEEK), //
                2026, Calendar.SEPTEMBER, 13, 23, 59, 59, 999);

        // 周一当天：begin 即当天
        assertCalendar(CalendarUtil.getBegin(calendar(2026, Calendar.SEPTEMBER, 7), DateUnit.WEEK), //
                2026, Calendar.SEPTEMBER, 7, 0, 0, 0, 0);
        // 周日当天：end 即当天
        assertCalendar(CalendarUtil.getEnd(calendar(2026, Calendar.SEPTEMBER, 13), DateUnit.WEEK), //
                2026, Calendar.SEPTEMBER, 13, 23, 59, 59, 999);

        // 跨月的一周：2026-08-31(周一) ~ 2026-09-06(周日)，取 2026-09-01(周二)
        assertCalendar(CalendarUtil.getBegin(calendar(2026, Calendar.SEPTEMBER, 1), DateUnit.WEEK), //
                2026, Calendar.AUGUST, 31, 0, 0, 0, 0);
        assertCalendar(CalendarUtil.getEnd(calendar(2026, Calendar.SEPTEMBER, 1), DateUnit.WEEK), //
                2026, Calendar.SEPTEMBER, 6, 23, 59, 59, 999);

        // 周日取 begin 应回退到上周周一：2026-09-13(周日) → 2026-09-07(周一)
        assertCalendar(CalendarUtil.getBegin(calendar(2026, Calendar.SEPTEMBER, 13), DateUnit.WEEK), //
                2026, Calendar.SEPTEMBER, 7, 0, 0, 0, 0);
        // 周一取 end 应前进到本周周日：2026-09-07(周一) → 2026-09-13(周日)
        assertCalendar(CalendarUtil.getEnd(calendar(2026, Calendar.SEPTEMBER, 7), DateUnit.WEEK), //
                2026, Calendar.SEPTEMBER, 13, 23, 59, 59, 999);
    }

    /** WEEK 字段值：周内天序号 1-7（周一=1 ... 周日=7，与 DayOfWeek#getValue() 一致） */
    @Test
    public void testGetFieldValueWeek() {
        Assert.assertEquals(1, CalendarUtil.getFieldValue(calendar(2026, Calendar.SEPTEMBER, 7), DateUnit.WEEK));// 周一
        Assert.assertEquals(3, CalendarUtil.getFieldValue(calendar(2026, Calendar.SEPTEMBER, 9), DateUnit.WEEK));// 周三
        Assert.assertEquals(5, CalendarUtil.getFieldValue(calendar(2026, Calendar.SEPTEMBER, 11), DateUnit.WEEK));// 周五
        Assert.assertEquals(7, CalendarUtil.getFieldValue(calendar(2026, Calendar.SEPTEMBER, 13), DateUnit.WEEK));// 周日
    }

    /** WEEK 是追加在枚举末尾的新值（保持已有枚举 ordinal 不变，二进制兼容） */
    @Test
    public void testDateUnitWeekAppendedAtEnd() {
        DateUnit[] units = DateUnit.values();
        Assert.assertSame(DateUnit.WEEK, units[units.length - 1]);
        // 原有枚举值的 ordinal 不变
        Assert.assertEquals(0, DateUnit.YEAR.ordinal());
        Assert.assertEquals(3, DateUnit.MONTH.ordinal());
        Assert.assertEquals(4, DateUnit.DATE.ordinal());
        Assert.assertEquals(7, DateUnit.SECOND.ordinal());
    }
}
