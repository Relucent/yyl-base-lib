package com.github.relucent.base.common.convert.impl;

import java.time.Duration;
import java.time.temporal.TemporalAmount;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * {@code Duration}转换器<br>
 */
public class DurationConverter implements BasicConverter<Duration> {

    public static final DurationConverter INSTANCE = new DurationConverter();

    public Duration convertInternal(Object source, Class<? extends Duration> toType) {
        try {
            if (source instanceof Duration) {
                return (Duration) source;
            }
            if (source instanceof Number) {
                return Duration.ofMillis(((Number) source).longValue());
            }
            if (source instanceof TemporalAmount) {
                return Duration.from((TemporalAmount) source);
            }
            return Duration.parse(StringUtil.string(source));
        } catch (Exception ignore) {
            // 无法解析的文本或时间量，按转换失败处理
            return null;
        }
    }
}
