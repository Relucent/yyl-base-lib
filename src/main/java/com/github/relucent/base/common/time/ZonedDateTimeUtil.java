package com.github.relucent.base.common.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

import com.github.relucent.base.common.constant.ZoneIdConstant;

/**
 * {@link ZonedDateTime} 工具类封装
 */
public class ZonedDateTimeUtil {

    /**
     * 格式化日期时间
     * @param zonedDateTime 带有时区的日期时间对象
     * @param formatter     日期时间格式器
     * @return 日期时间文本
     */
    public static final String format(ZonedDateTime zonedDateTime, DateTimeFormatter formatter) {
        return zonedDateTime != null ? zonedDateTime.format(formatter) : null;
    }

    /**
     * 格式化日期时间，格式为ISO_OFFSET_DATE_TIME <br>
     * 日期 + 时间 + 时区偏移量 → yyyy-MM-dd'T'HH:mm:ssXXX）
     * @param zonedDateTime 带有时区的日期时间对象
     * @return 日期时间文本
     */
    public static final String formatIsoOffsetDateTime(ZonedDateTime zonedDateTime) {
        return format(zonedDateTime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * 格式化日期时间，格式为ISO_ZONED_DATE_TIME<br>
     * 日期 + 时间 + 时区偏移量 + 时区 ID → yyyy-MM-dd'T'HH:mm:ssXXX'['VV']'
     * @param zonedDateTime 带有时区的日期时间对象
     * @return 日期时间文本
     */
    public static final String formatIsoZonedDateTime(ZonedDateTime zonedDateTime) {
        return format(zonedDateTime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    /**
     * 格式化日期时间，使用的是 ISO-8601 标准格式，以 UTC（0时区）表示时间
     * @param zonedDateTime 带有时区的日期时间对象
     * @return 日期时间文本（UTC零时区）
     */
    public static String formatUtcDateTime(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : DateTimeFormatter.ISO_INSTANT.format(zonedDateTime.toInstant());
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
     * @param zonedDateTime 带有时区的日期时间对象
     * @return 毫秒
     */
    public static Long toEpochMilli(ZonedDateTime zonedDateTime) {
        return zonedDateTime.toInstant().toEpochMilli();
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
     * @param zoneId     时区
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static ZonedDateTime ofEpochMilli(Long epochMilli, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zoneId);
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code ZonedDateTime}
     * @param date   日期类型对象 {@code Date}
     * @param zoneId 时区
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static ZonedDateTime toZonedDateTime(Date date, ZoneId zoneId) {
        return date == null ? null : ZonedDateTime.ofInstant(date.toInstant(), zoneId);
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code ZonedDateTime}，使用系统默认时区
     * @param date 日期类型对象 {@code Date}
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        return toZonedDateTime(date, ZoneUtil.getDefaultZoneId());
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code ZonedDateTime}，使用UTC时区（零时区）
     * @param date 日期类型对象 {@code Date}
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static ZonedDateTime toUtcZonedDateTime(Date date) {
        return toZonedDateTime(date, ZoneIdConstant.UTC);
    }

    /**
     * 将时间对象转换成指定目标时区的日期时间对象
     * @param time   时间对象{@code TemporalAccessor}
     * @param zoneId 时区ID
     * @return 日期时间对象{@code ZonedDateTime}
     */
    public static final ZonedDateTime toZonedDateTime(TemporalAccessor time, ZoneId zoneId) {
        if (time == null) {
            return null;
        }
        if (time instanceof ZonedDateTime) {
            return ((ZonedDateTime) time).withZoneSameInstant(zoneId);
        }
        return TemporalAccessorUtil.toInstant(time).atZone(zoneId);
    }

    /**
     * 日期时间转换，将{@code TemporalAccessor}转换为{@code ZonedDateTime}，使用系统默认时区
     * @param time 时间对象{@code TemporalAccessor}
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     */
    public static final ZonedDateTime toZonedDateTime(TemporalAccessor time) {
        return toZonedDateTime(time, ZoneUtil.getDefaultZoneId());
    }

    /**
     * 日期时间转换，将{@code TemporalAccessor}转换为UTC时区的{@code ZonedDateTime}，使用以 UTC（0时区） 表示时间。
     * @param time 时间对象{@code TemporalAccessor}
     * @return UTC时区的日期时间对象{@code ZonedDateTime}
     */
    public static final ZonedDateTime toUtcZonedDateTime(TemporalAccessor time) {
        return toZonedDateTime(time, ZoneIdConstant.UTC);
    }

    /**
     * 转换日期时间对象为时间戳对象
     * @param zonedDateTime 带有时区的日期时间对象
     * @return 时间戳对象
     */
    public static Instant toInstant(ZonedDateTime zonedDateTime) {
        return zonedDateTime == null ? null : zonedDateTime.toInstant();
    }
}
