package com.github.relucent.base.common.time;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link Times} 统一门面的测试：门面方法均为委托实现，这里验证委托正确性与基本行为
 */
public class TimesTest {

    @Test
    public void testNowAndCurrentMillis() {
        long before = Times.currentMillis();
        Date now = Times.now();
        long after = Times.currentMillis();
        Assert.assertNotNull(now);
        Assert.assertTrue(now.getTime() >= before && now.getTime() <= after);
    }

    @Test
    public void testMinMax() {
        Assert.assertEquals(DateUtil.MIN_MILLIS, Long.valueOf(Times.min().getTime()));
        Assert.assertEquals(DateUtil.MAX_MILLIS, Long.valueOf(Times.max().getTime()));
    }

    @Test
    public void testParseAndFormatRoundTrip() {
        Date date = Times.parseDate("2026-09-11 19:00:00");
        Assert.assertNotNull(date);
        Assert.assertEquals("2026-09-11 19:00:00", Times.formatDateTime(date));
        Assert.assertEquals("2026-09-11", Times.formatDate(date));
        Assert.assertNotNull(Times.format(date));
        Assert.assertEquals("2026/09/11", Times.format(date, "yyyy/MM/dd"));

        // 宽容语义：无法解析返回 null
        Assert.assertNull(Times.parseDate("not-a-date"));
        Assert.assertNull(Times.parseDate("2026-09-11", "HH:mm:ss"));
    }

    @Test
    public void testConvertTypes() {
        Date date = Times.parseDate("2026-09-11 19:00:00");
        Instant instant = Times.toInstant(date);
        Assert.assertEquals(date.getTime(), instant.toEpochMilli());

        LocalDateTime dateTime = Times.toLocalDateTime(date);
        Assert.assertEquals(LocalDateTime.of(2026, 9, 11, 19, 0, 0), dateTime);

        Date back = Times.toDate(dateTime);
        Assert.assertEquals(date.getTime(), back.getTime());

        Calendar calendar = Times.toCalendar(date);
        Assert.assertEquals(date.getTime(), calendar.getTimeInMillis());
    }

    @Test
    public void testGetBeginEndWeek() {
        // 2026-09-11 是周五，本周为 2026-09-07(周一) ~ 2026-09-13(周日)
        Date friday = Times.parseDate("2026-09-11 12:30:45");
        Date begin = Times.getBegin(friday, DateUnit.WEEK);
        Date end = Times.getEnd(friday, DateUnit.WEEK);

        Calendar beginCalendar = Calendar.getInstance();
        beginCalendar.setTime(begin);
        Assert.assertEquals(2026, beginCalendar.get(Calendar.YEAR));
        Assert.assertEquals(Calendar.SEPTEMBER, beginCalendar.get(Calendar.MONTH));
        Assert.assertEquals(7, beginCalendar.get(Calendar.DATE));
        Assert.assertEquals(0, beginCalendar.get(Calendar.HOUR_OF_DAY));

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(end);
        Assert.assertEquals(13, endCalendar.get(Calendar.DATE));
        Assert.assertEquals(23, endCalendar.get(Calendar.HOUR_OF_DAY));

        // 周五 → WEEK 字段值 5
        Assert.assertEquals(5, Times.getFieldValue(friday, DateUnit.WEEK));
    }

    @Test
    public void testBetweenAndOffset() {
        LocalDateTime start = LocalDateTime.of(2026, 9, 11, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(2026, 9, 14, 0, 0, 0);
        Assert.assertEquals(Duration.ofDays(3), Times.between(start, end));
        Assert.assertEquals(3, Times.between(start, end, ChronoUnit.DAYS));
        Assert.assertEquals(72, Times.between(start, end, ChronoUnit.HOURS));

        LocalDateTime shifted = Times.offset(start, 2, ChronoUnit.DAYS);
        Assert.assertEquals(LocalDateTime.of(2026, 9, 13, 0, 0, 0), shifted);
    }

    @Test
    public void testUnitConversion() {
        Assert.assertEquals(ChronoUnit.MILLIS, Times.toChronoUnit(TimeUnit.MILLISECONDS));
        Assert.assertEquals(TimeUnit.MILLISECONDS, Times.toTimeUnit(ChronoUnit.MILLIS));
        Assert.assertNull(Times.toChronoUnit(null));
        Assert.assertNull(Times.toTimeUnit(null));
        Assert.assertEquals(Duration.ofMinutes(5), Times.toDuration(5, TimeUnit.MINUTES));
    }
}
