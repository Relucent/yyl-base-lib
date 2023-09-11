package com.github.relucent.base.common.convert.impl;

import java.time.ZoneId;
import java.util.TimeZone;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * {@code TimeZone}转换器<br>
 */
public class TimeZoneConverter implements BasicConverter<TimeZone> {

    public static final TimeZoneConverter INSTANCE = new TimeZoneConverter();

    public TimeZone convertInternal(Object source, Class<? extends TimeZone> toType) {
        if (source instanceof TimeZone) {
            return (TimeZone) source;
        }
        if (source instanceof ZoneId) {
            return TimeZone.getTimeZone((ZoneId) source);
        }
        return TimeZone.getTimeZone(StringUtil.string(source));
    }
}
