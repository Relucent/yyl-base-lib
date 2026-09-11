package com.github.relucent.base.common.convert.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link CalendarConverter} 单元测试
 */
public class CalendarConverterTest {

    @Test
    public void testConvertFromEpochMilli() {
        Calendar calendar = CalendarConverter.INSTANCE.convert(0L, Calendar.class);
        Assert.assertNotNull(calendar);
        Assert.assertEquals(0L, calendar.getTimeInMillis());
    }

    @Test
    public void testConvertFromDate() {
        Date date = new Date(0L);
        Calendar calendar = CalendarConverter.INSTANCE.convert(date, Calendar.class);
        Assert.assertNotNull(calendar);
        Assert.assertEquals(date.getTime(), calendar.getTimeInMillis());
    }

    @Test
    public void testConvertFromCalendar() {
        Calendar source = Calendar.getInstance();
        Assert.assertSame(source, CalendarConverter.INSTANCE.convert(source, Calendar.class));
    }

    @Test
    public void testConvertToConcreteType() {
        // 目标类型为具体实现类时，构造对应类型的实例并设置时间
        Calendar calendar = CalendarConverter.INSTANCE.convert(0L, GregorianCalendar.class);
        Assert.assertTrue(calendar instanceof GregorianCalendar);
        Assert.assertEquals(0L, calendar.getTimeInMillis());
    }

    @Test
    public void testConvertFromIllegalText() {
        // 无法解析的文本，按转换失败处理返回 null
        Assert.assertNull(CalendarConverter.INSTANCE.convert("not-a-date", Calendar.class));
    }

    @Test
    public void testConvertNull() {
        Assert.assertNull(CalendarConverter.INSTANCE.convert(null, Calendar.class));
    }
}
