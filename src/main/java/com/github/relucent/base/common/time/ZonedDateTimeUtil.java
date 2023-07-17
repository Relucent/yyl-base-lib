package com.github.relucent.base.common.time;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * {@link ZonedDateTime} 工具类封装
 */
public class ZonedDateTimeUtil {

    /**
     * 格式化日期时间
     * @param datetime 日期时间
     * @param formatter 字符串格式
     * @return 日期时间字符串
     */
    public static final String format(ZonedDateTime datetime, DateTimeFormatter formatter) {
        return datetime != null ? datetime.format(formatter) : null;
    }

    /**
     * 格式化日期时间，格式为ISO_OFFSET_DATE_TIME
     * @param datetime 日期时间
     * @return 日期时间字符串
     */
    public static final String formatIsoOffsetDateTime(ZonedDateTime datetime) {
        return format(datetime, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * 格式化日期时间，格式为ISO_ZONED_DATE_TIME
     * @param datetime 日期时间
     * @return 日期时间字符串
     */
    public static final String formatIsoZonedDateTime(ZonedDateTime datetime) {
        return format(datetime, DateTimeFormatter.ISO_ZONED_DATE_TIME);
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
     * @return 日期时间类型{@code ZonedDateTime}
     */
    public static ZonedDateTime ofEpochMilli(Long epochMilli) {
        return ofEpochMilli(epochMilli, ZoneId.systemDefault());
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code ZonedDateTime}
     * @param epochMilli 时间的毫秒时间戳
     * @param zoneId 时区
     * @return 日期时间类型{@code ZonedDateTime}
     */
    public static ZonedDateTime ofEpochMilli(Long epochMilli, ZoneId zoneId) {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), zoneId);
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code ZonedDateTime}
     * @param date 日期类型 {@code Date}
     * @return 日期时间类型{@code ZonedDateTime}
     */
    public static ZonedDateTime toZonedDateTime(Date date) {
        return toZonedDateTime(date, ZoneId.systemDefault());
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code ZonedDateTime}
     * @param date 日期类型 {@code Date}
     * @param zoneId 时区
     * @return 日期时间类型{@code ZonedDateTime}
     */
    public static ZonedDateTime toZonedDateTime(Date date, ZoneId zoneId) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        return ZonedDateTime.ofInstant(instant, zoneId);
    }
}
