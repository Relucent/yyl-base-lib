package com.github.relucent.base.common.convert.impl;

import java.util.Calendar;
import java.util.Date;

import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.time.CalendarUtil;

/**
 * 日历类型转换器
 */
public class CalendarConverter implements Converter<Calendar> {

    public static final CalendarConverter INSTANCE = new CalendarConverter();

    @Override
    public Calendar convert(Object source, Class<? extends Calendar> toType) {
        Date date = DateConverter.INSTANCE.convert(source, Date.class);
        return date == null ? null : CalendarUtil.toCalendar(date);
    }
}
