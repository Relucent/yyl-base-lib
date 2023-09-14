package com.github.relucent.base.common.convert.impl;

import java.sql.Timestamp;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.reflect.TypeReference;
import com.github.relucent.base.common.reflect.TypeReferenceCache;
import com.github.relucent.base.common.reflect.internal.ObjectConstructor;
import com.github.relucent.base.common.reflect.internal.ObjectConstructorCache;
import com.github.relucent.base.common.time.DateUtil;
import com.github.relucent.base.common.time.TemporalAccessorUtil;

/**
 * 日期类型转换器
 * @author YYL
 * @version 2012-12-11
 * @see Converter
 */
public class DateConverter implements BasicConverter<Date> {

    public static final DateConverter INSTANCE = new DateConverter();

    private static final Pattern DATE_PATTERN = Pattern.compile("^new Date\\((\\d+)\\)$");

    public Date convertInternal(Object source, Class<? extends Date> toType) {
        try {
            Long mills = toEpochMilli(source);
            return mills == null ? null : wrap(mills, toType);
        } catch (Exception ignore) {
            // Ignore//
        }
        return null;
    }

    private static Long toEpochMilli(final Object source) {
        if (source == null) {
            return null;
        }
        if (source instanceof Date) {
            return ((Date) source).getTime();
        }
        if (source instanceof Number) {
            return ((Number) source).longValue();
        }

        if (source instanceof Calendar) {
            return ((Calendar) source).getTimeInMillis();
        }
        if (source instanceof TemporalAccessor) {
            return TemporalAccessorUtil.toEpochMilli((TemporalAccessor) source);
        }

        final String value = String.valueOf(source);
        Matcher dateMatcher = DATE_PATTERN.matcher(value);
        if (dateMatcher.matches() && dateMatcher.find()) {
            String msel = dateMatcher.group(1);
            return Long.parseLong(msel);
        }

        final TemporalAccessor temporal = TemporalAccessorUtil.parse(value);
        if (temporal != null) {
            return TemporalAccessorUtil.toEpochMilli(temporal);
        }

        final Date date = DateUtil.parseDate(value);
        if (date != null) {
            return date.getTime();
        }

        return null;
    }

    private static Date wrap(final long mills, final Class<?> toType) {
        if (java.util.Date.class.equals(toType)) {
            return new Date(mills);
        }
        if (java.sql.Date.class.equals(toType)) {
            return new java.sql.Date(mills);
        }
        if (java.sql.Time.class.equals(toType)) {
            return new java.sql.Time(mills);
        }
        if (java.sql.Timestamp.class.equals(toType)) {
            return new Timestamp(mills);
        }
        TypeReference<? extends Date> typeReference = TypeReferenceCache.INSTANCE.get(toType);
        ObjectConstructor<? extends Date> constructor = ObjectConstructorCache.INSTANCE.get(typeReference);
        Date date = constructor.construct();
        date.setTime(mills);
        return date;
    }
}
