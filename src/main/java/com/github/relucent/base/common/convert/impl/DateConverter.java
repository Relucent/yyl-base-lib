package com.github.relucent.base.common.convert.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.relucent.base.common.convert.Converter;
import com.github.relucent.base.common.time.DateUtil;

/**
 * 日期类型转换器
 * @author YYL
 * @version 2012-12-11
 * @see Converter
 */
public class DateConverter implements Converter<Date> {

    public static final DateConverter INSTANCE = new DateConverter();

    private static final Pattern DATE_PATTERN = Pattern.compile("^new Date\\((\\d+)\\)$");

    public Date convert(Object source, Class<? extends Date> toType, Date vDefault) {
        try {

            Long mills = null;

            if (source == null) {
                mills = null;
            } else if (source instanceof Number) {
                mills = ((Number) source).longValue();
            } else if (source instanceof Date) {
                mills = ((Date) source).getTime();
            } else if (source instanceof Calendar) {
                mills = ((Calendar) source).getTimeInMillis();
            } else {
                final String value = String.valueOf(source);
                Matcher dateMatcher = DATE_PATTERN.matcher(value);
                if (dateMatcher.matches() && dateMatcher.find()) {
                    String msel = dateMatcher.group(1);
                    mills = Long.parseLong(msel);
                } else {
                    Date date = DateUtil.parseDate(value);
                    if (date != null) {
                        mills = date.getTime();
                    }
                }
            }
            if (mills == null) {
                return vDefault;
            }
            if (java.util.Date.class == toType) {
                return new java.util.Date(mills);
            }
            if (java.sql.Date.class == toType) {
                return new java.sql.Date(mills);
            }
            if (java.sql.Time.class == toType) {
                return new java.sql.Time(mills);
            }
            if (java.sql.Timestamp.class == toType) {
                return new java.sql.Timestamp(mills);
            }
        } catch (Exception e) {
            // Ignore//
        }
        return vDefault;
    }

    @Override
    public boolean support(Class<? extends Date> type) {
        return Date.class.isAssignableFrom(type);
    }
}
