package com.github.relucent.base.common.time;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

public class CalendarTextUtilTest {

    /**
     * 回归测试：季度取值为 1-4，必须映射为「X季度」<br>
     * 历史缺陷：取值范围写成 0-11 且直接以 value 作下标，导致 1 被显示为「二季度」
     */
    @Test
    public void testGetTextQuarter() {
        Assert.assertEquals("一季度", CalendarTextUtil.getText(DateUnit.QUARTER, 1));
        Assert.assertEquals("二季度", CalendarTextUtil.getText(DateUnit.QUARTER, 2));
        Assert.assertEquals("三季度", CalendarTextUtil.getText(DateUnit.QUARTER, 3));
        Assert.assertEquals("四季度", CalendarTextUtil.getText(DateUnit.QUARTER, 4));
    }

    @Test
    public void testGetTextQuarterOutOfRange() {
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.QUARTER, 0));
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.QUARTER, 5));
        // 历史缺陷中 11 会返回「四季度」，现已修正为越界
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.QUARTER, 11));
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.QUARTER, -1));
    }

    @Test
    public void testGetTextYear() {
        Assert.assertEquals("2026年", CalendarTextUtil.getText(DateUnit.YEAR, 2026));
        Assert.assertEquals("0年", CalendarTextUtil.getText(DateUnit.YEAR, 0));
        Assert.assertEquals("-1年", CalendarTextUtil.getText(DateUnit.YEAR, -1));
    }

    @Test
    public void testGetTextHalfYear() {
        Assert.assertEquals("上半年", CalendarTextUtil.getText(DateUnit.HALFYEAR, 0));
        Assert.assertEquals("下半年", CalendarTextUtil.getText(DateUnit.HALFYEAR, 1));
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.HALFYEAR, 2));
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.HALFYEAR, -1));
    }

    @Test
    public void testGetTextMonth() {
        Assert.assertEquals("一月", CalendarTextUtil.getText(DateUnit.MONTH, 0));
        Assert.assertEquals("六月", CalendarTextUtil.getText(DateUnit.MONTH, 5));
        Assert.assertEquals("十二月", CalendarTextUtil.getText(DateUnit.MONTH, 11));
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.MONTH, 12));
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.MONTH, -1));
    }

    @Test
    public void testGetTextDate() {
        Assert.assertEquals("1日", CalendarTextUtil.getText(DateUnit.DATE, 1));
        Assert.assertEquals("11日", CalendarTextUtil.getText(DateUnit.DATE, 11));
        Assert.assertEquals("31日", CalendarTextUtil.getText(DateUnit.DATE, 31));
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.DATE, 0));
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.DATE, 32));
    }

    @Test
    public void testGetTextUnsupportedUnits() {
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.HOUR_OF_DAY, 1));
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.MINUTE, 1));
        Assert.assertNull(CalendarTextUtil.getText(DateUnit.SECOND, 1));
    }

    /** 一致性：getFieldValue 的返回值应可直接作为 getText 的入参 */
    @Test
    public void testConsistentWithGetFieldValue() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2026, Calendar.FEBRUARY, 10);
        Assert.assertEquals("一季度", CalendarTextUtil.getText(DateUnit.QUARTER, CalendarUtil.getFieldValue(calendar, DateUnit.QUARTER)));
        calendar.set(2026, Calendar.NOVEMBER, 10);
        Assert.assertEquals("四季度", CalendarTextUtil.getText(DateUnit.QUARTER, CalendarUtil.getFieldValue(calendar, DateUnit.QUARTER)));
        calendar.set(2026, Calendar.MAY, 10);
        Assert.assertEquals("五月", CalendarTextUtil.getText(DateUnit.MONTH, CalendarUtil.getFieldValue(calendar, DateUnit.MONTH)));
    }
}
