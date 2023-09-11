package com.github.relucent.base.common.convert.impl;

import java.time.Period;
import java.time.temporal.TemporalAmount;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * {@code Period}转换器<br>
 */
public class PeriodConverter implements BasicConverter<Period> {

    public static final PeriodConverter INSTANCE = new PeriodConverter();

    public Period convertInternal(Object source, Class<? extends Period> toType) {
        if (source instanceof Period) {
            return (Period) source;
        }
        if (source instanceof Number) {
            return Period.ofDays(((Number) source).intValue());
        }
        if (source instanceof TemporalAmount) {
            return Period.from((TemporalAmount) source);
        }
        return Period.parse(StringUtil.string(source));
    }
}
