package com.github.relucent.base.common.convert.impl;

import java.time.ZoneId;
import java.util.TimeZone;

import com.github.relucent.base.common.convert.BasicConverter;
import com.github.relucent.base.common.lang.StringUtil;

/**
 * {@code ZoneId}转换器<br>
 */
public class ZoneIdConverter implements BasicConverter<ZoneId> {

    public static final ZoneIdConverter INSTANCE = new ZoneIdConverter();

    public ZoneId convertInternal(Object source, Class<? extends ZoneId> toType) {
        if (source instanceof ZoneId) {
            return (ZoneId) source;
        }
        if (source instanceof TimeZone) {
            return ((TimeZone) source).toZoneId();
        }
        try {
            return ZoneId.of(StringUtil.string(source));
        } catch (Exception ignore) {
            // 无效的时区 ID，按转换失败处理
            return null;
        }
    }
}
