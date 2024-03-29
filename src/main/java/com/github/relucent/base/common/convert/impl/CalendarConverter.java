package com.github.relucent.base.common.convert.impl;

import java.util.Calendar;
import java.util.Date;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.reflect.TypeReference;
import com.github.relucent.base.common.reflect.TypeReferenceCache;
import com.github.relucent.base.common.reflect.internal.ObjectConstructor;
import com.github.relucent.base.common.reflect.internal.ObjectConstructorCache;
import com.github.relucent.base.common.time.CalendarUtil;

/**
 * 日历类型转换器
 */
public class CalendarConverter implements BasicConverter<Calendar> {

    public static final CalendarConverter INSTANCE = new CalendarConverter();

    @Override
    public Calendar convertInternal(Object source, Class<? extends Calendar> toType) {
        Date date = DateConverter.INSTANCE.convert(source, Date.class);

        if (date == null) {
            return null;
        }

        if (Calendar.class.equals(toType)) {
            return CalendarUtil.toCalendar(date);
        }

        TypeReference<? extends Calendar> typeReference = TypeReferenceCache.INSTANCE.get(toType);
        ObjectConstructor<? extends Calendar> constructor = ObjectConstructorCache.INSTANCE.get(typeReference);
        Calendar calendar = constructor.construct();
        calendar.setTime(date);
        return calendar;
    }
}
