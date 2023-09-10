package com.github.relucent.base.common.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * {@link ZonedDateTime} 工具类封装
 */
public class ZonedDateTimeUtil {

    /**
     * 格式化日期时间
     * @param datetime 日期时间
     * @param formatter 日期时间格式器
     * @return 日期时间文本
     */
    public static final String format(ZonedDateTime datetime, DateTimeFormatter formatter) {
        return datetime != null ? datetime.format(formatter) : null;
    }

    /**
     * 格式化日期时间，格式为ISO_OFFSET_DATE_TIME
     * @param datetime 日期时间
     * @return 日期时间文本
     */
    public static final String formatIsoOffsetDateTime(ZonedDateTime datetime) {
        return format(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * 格式化日期时间，格式为ISO_ZONED_DATE_TIME
     * @param datetime 日期时间
     * @return 日期时间文本
     */
    public static final String formatIsoZonedDateTime(ZonedDateTime datetime) {
        return format(datetime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    /**
     * 解析日期格式字符串<br>
     * 会通过尝试各种不同时间格式的解析器来解析时间字符串，如果最终依旧无法解析则返回{@code null}
     * @param text 时间文本
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static ZonedDateTime parse(String text) {
        return TemporalAccessorUtil.toZonedDateTime(TemporalAccessorUtil.parse(text));
    }

    /**
     * 转换日期时间为毫秒
     * @param datetime 日期时间
     * @return 毫秒
     */
    public static Long toEpochMilli(ZonedDateTime datetime) {
        return datetime.toInstant().toEpochMilli();
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code ZonedDateTime}，使用当前系统时区
     * @param epochMilli 时间的毫秒时间戳
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static ZonedDateTime ofEpochMilli(Long epochMilli) {
        return ofEpochMilli(epochMilli, ZoneUtil.getDefaultZoneId());
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code ZonedDateTime}
     * @param epochMilli 时间的毫秒时间戳
     * @param zoneId 时区
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static ZonedDateTime ofEpochMilli(Long epochMilli, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zoneId);
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code ZonedDateTime}
     * @param date 日期类型对象 {@code Date}
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        return toZonedDateTime(date, ZoneUtil.getDefaultZoneId());
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code ZonedDateTime}
     * @param date 日期类型对象 {@code Date}
     * @param zoneId 时区
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static ZonedDateTime toZonedDateTime(Date date, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        return ZonedDateTime.ofInstant(instant, zoneId);
    }

    /**
     * 日期时间转换，将{@code TemporalAccessor}转换为{@code ZonedDateTime}
     * @param time 时间对象{@code TemporalAccessor}
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static final ZonedDateTime toZonedDateTime(TemporalAccessor time) {
        if (time == null) {
            return null;
        }
        if (time instanceof ZonedDateTime) {
            return (ZonedDateTime) time;
        }
        return TemporalAccessorUtil.toInstant(time).atZone(ZoneUtil.getDefaultZoneId());
    }
}
