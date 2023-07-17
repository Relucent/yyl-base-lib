package com.github.relucent.base.common.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.relucent.base.common.lang.AssertUtil;

public class LocalDateTimeUtil {

    // ==============================Fields===========================================
    /** 格式化缓存 */
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();
    static {
        // JDK8 分析带毫秒的日期格式“yyyyMMddHHmmssSSS”，需要特殊处理
        // https://bugs.openjdk.java.net/browse/JDK-8031085
        // https://stackoverflow.com/questions/22588051/is-java-time-failing-to-parse-fraction-of-second
        FORMATTER_CACHE.put("yyyyMMddHHmmssSSS", new DateTimeFormatterBuilder()//
                .appendPattern("yyyyMMddHHmmss")//
                .appendValue(ChronoField.MILLI_OF_SECOND, 3)//
                .toFormatter());
    }

    // ==============================Constructors=====================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected LocalDateTimeUtil() {
    }

    // ==============================Methods==========================================

    /**
     * 日期时间转换，将{@code Date}转换为{@code LocalDateTime}
     * @param date 日期类型 {@code Date}
     * @return 日期时间类型{@code LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * {@link TemporalAccessor}转换为 {@link LocalDateTime}对象
     * @param temporalAccessor 时间对象
     * @return 日期时间对象{@code LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }
        return LocalDateTime.of(//
                TemporalAccessorUtil.get(temporalAccessor, ChronoField.YEAR), // 年
                TemporalAccessorUtil.get(temporalAccessor, ChronoField.MONTH_OF_YEAR), // 月
                TemporalAccessorUtil.get(temporalAccessor, ChronoField.DAY_OF_MONTH), // 日
                TemporalAccessorUtil.get(temporalAccessor, ChronoField.HOUR_OF_DAY), // 时
                TemporalAccessorUtil.get(temporalAccessor, ChronoField.MINUTE_OF_HOUR), // 分
                TemporalAccessorUtil.get(temporalAccessor, ChronoField.SECOND_OF_MINUTE), // 秒
                TemporalAccessorUtil.get(temporalAccessor, ChronoField.NANO_OF_SECOND)// 纳秒
        );
    }

    /**
     * 日期时间转换，将{@code LocalDateTime}转换为{@code Date}
     * @param datetime 日期时间类型 {@code LocalDateTime}
     * @return 日期类型{@code Date}
     */
    public static Date toDate(LocalDateTime datetime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = datetime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * 格式化日期对象
     * @param datetime 日期时间
     * @param pattern 日期时间格式
     * @return 日期字符串
     */
    public static String format(LocalDateTime datetime, String pattern) {
        DateTimeFormatter formatter = getFormatter(pattern);
        return formatter.format(datetime);
    }

    /**
     * 解析日期时间字符串
     * @param source 日期时间字符串
     * @param pattern 日期时间格式
     * @return 日期时间
     */
    public static LocalDateTime parse(CharSequence source, String pattern) {
        DateTimeFormatter formatter = getFormatter(pattern);
        return toLocalDateTime(formatter.parse(source));
    }

    // ==============================ToolMethods======================================
    /**
     * 获得 {@code DateTimeFormatter}实例。因为 DateTimeFormatter 是线程安全的所以可以缓存起来，需要的时候引用。
     * @param pattern 日期格式
     * @return {@code DateTimeFormatter}实例
     */
    private static DateTimeFormatter getFormatter(final String pattern) {
        AssertUtil.notNull(pattern, "pattern");
        DateTimeFormatter formatter = FORMATTER_CACHE.get(pattern);
        if (formatter == null) {
            formatter = DateTimeFormatter.ofPattern(pattern);
            final DateTimeFormatter previous = FORMATTER_CACHE.putIfAbsent(pattern, formatter);
            if (previous != null) {
                formatter = previous;
            }
        }
        return formatter;
    }
}
