package com.github.relucent.base.common.time;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import com.github.relucent.base.common.constant.ZoneIdConstant;

/**
 * {@link OffsetDateTime} 工具类封装
 */
public class OffsetDateTimeUtil {

    /**
     * 格式化日期时间
     * @param offsetDateTime 带时区偏移的日期时间对象
     * @param formatter      日期时间格式器
     * @return 日期时间文本
     */
    public static final String format(OffsetDateTime offsetDateTime, DateTimeFormatter formatter) {
        return offsetDateTime != null ? offsetDateTime.format(formatter) : null;
    }

    /**
     * 格式化日期时间，格式为ISO_OFFSET_DATE_TIME <br>
     * 日期 + 时间 + 时区偏移量 → yyyy-MM-dd'T'HH:mm:ssXXX）
     * @param offsetDateTime 带时区偏移的日期时间对象
     * @return 日期时间文本
     */
    public static final String formatIsoOffsetDateTime(OffsetDateTime offsetDateTime) {
        return format(offsetDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * 格式化日期时间，使用的是 ISO-8601 标准格式，以 UTC（0时区）表示时间
     * @param offsetDateTime 带有时区的日期时间对象
     * @return 日期时间文本（UTC零时区）
     */
    public static String formatUtcDateTime(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : DateTimeFormatter.ISO_INSTANT.format(offsetDateTime.toInstant());
    }

    /**
     * 解析日期格式字符串<br>
     * 会通过尝试各种不同时间格式的解析器来解析时间字符串，如果最终依旧无法解析则返回{@code null}
     * @param text 时间文本
     * @return 带偏移的日期时间{@code OffsetDateTime}
     */
    public static OffsetDateTime parse(String text) {
        TemporalAccessor temporal = TemporalAccessorUtil.parse(text);
        if (temporal == null) {
            return null;
        }
        if (temporal instanceof OffsetDateTime) {
            return (OffsetDateTime) temporal;
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toOffsetDateTime();
        }
        try {
            return OffsetDateTime.from(temporal);
        } catch (DateTimeException e) {
            // ignore
        }
        Instant instant = TemporalAccessorUtil.toInstant(temporal);
        return toOffsetDateTime(instant, ZoneUtil.getDefaultZoneId());
    }

    /**
     * 转换日期时间为毫秒
     * @param offsetDateTime 带有时区的日期时间对象
     * @return 毫秒
     */
    public static Long toEpochMilli(OffsetDateTime offsetDateTime) {
        return offsetDateTime.toInstant().toEpochMilli();
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code OffsetDateTime}，使用当前系统时区
     * @param epochMilli 时间的毫秒时间戳
     * @return 带偏移的日期时间{@code OffsetDateTime}
     */
    public static OffsetDateTime ofEpochMilli(Long epochMilli) {
        return ofEpochMilli(epochMilli, ZoneUtil.getDefaultZoneId());
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code OffsetDateTime}
     * @param epochMilli 时间的毫秒时间戳
     * @param zoneId     时区ID
     * @return 带偏移的日期时间{@code OffsetDateTime}
     */
    public static OffsetDateTime ofEpochMilli(Long epochMilli, ZoneId zoneId) {
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zoneId);
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code OffsetDateTime}
     * @param date   日期类型对象 {@code Date}
     * @param zoneId 时区ID
     * @return 带偏移的日期时间{@code OffsetDateTime}
     */
    public static OffsetDateTime toOffsetDateTime(Date date, ZoneId zoneId) {
        return date == null ? null : OffsetDateTime.ofInstant(date.toInstant(), zoneId);
    }

    /**
     * 日期时间转换，将{@code Date}转换为带偏移的日期时间{@code OffsetDateTime}，使用UTC时区（零时区）
     * @param date 日期类型对象 {@code Date}
     * @return 带偏移的日期时间{@code OffsetDateTime}
     */
    public static OffsetDateTime toUtcOffsetDateTime(Date date) {
        return toOffsetDateTime(date, ZoneIdConstant.UTC);
    }

    /**
     * 日期时间转换，将{@code TemporalAccessor}转换为带偏移的日期时间{@code OffsetDateTime}
     * @param time   时间对象{@code TemporalAccessor}
     * @param zoneId 时区ID
     * @return 带偏移的日期时间{@code OffsetDateTime}
     */
    public static final OffsetDateTime toOffsetDateTime(TemporalAccessor time, ZoneId zoneId) {
        return time == null ? null : OffsetDateTime.ofInstant(TemporalAccessorUtil.toInstant(time), zoneId);
    }

    /**
     * 日期时间转换，将{@code TemporalAccessor}转换为带偏移的日期时间{@code OffsetDateTime}，使用UTC时区（零时区）
     * @param time 时间对象{@code TemporalAccessor}
     * @return UTC时区的带偏移的日期时间{@code OffsetDateTime}
     */
    public static final OffsetDateTime toUtcOffsetDateTime(TemporalAccessor time) {
        return toOffsetDateTime(time, ZoneOffset.UTC);
    }

    /**
     * 转换日期时间对象为时间戳对象
     * @param offsetDateTime 带时区偏移的日期时间对象
     * @return 时间戳对象
     */
    public static Instant toInstant(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toInstant();
    }
}
