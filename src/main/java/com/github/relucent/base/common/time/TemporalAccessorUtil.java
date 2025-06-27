package com.github.relucent.base.common.time;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.Era;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;
import java.time.temporal.UnsupportedTemporalTypeException;

import com.github.relucent.base.common.lang.StringUtil;

/**
 * {@link TemporalAccessor} 工具类
 */
public class TemporalAccessorUtil {

    // =================================Fields=================================================
    /** 默认初始日期（Unix Epoch 时间零点） */
    private static final LocalDate DEFAULT_EPOCH_DATE = LocalDate.of(1970, 1, 1);

    /**
     * 常用的时间解析器列表<br>
     */
    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = { //
            DateTimeFormatter.BASIC_ISO_DATE, //
            DateTimeFormatter.ISO_INSTANT, //
            DateTimeFormatter.ISO_DATE, //
            DateTimeFormatter.ISO_TIME, //
            DateTimeFormatter.ISO_DATE_TIME, //
            DateTimeFormatter.ISO_LOCAL_DATE, //
            DateTimeFormatter.ISO_LOCAL_TIME, //
            DateTimeFormatter.ISO_LOCAL_DATE_TIME, //
            DateTimeFormatter.ISO_OFFSET_DATE, //
            DateTimeFormatter.ISO_OFFSET_TIME, //
            DateTimeFormatter.ISO_OFFSET_DATE_TIME, //
            DateTimeFormatter.ISO_ORDINAL_DATE, //
            DateTimeFormatter.ISO_ZONED_DATE_TIME, //
            DateTimeFormatter.RFC_1123_DATE_TIME, //

    };

    // =================================Constructors===========================================
    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected TemporalAccessorUtil() {

    }

    // =================================Methods================================================
    /**
     * 安全获取时间的某个属性，属性不存在返回默认值
     * @param temporal 时间对象
     * @param field    需要获取的属性
     * @return 时间的值，如果无法获取则默认值
     */
    public static int get(TemporalAccessor temporal, TemporalField field) {
        if (temporal.isSupported(field)) {
            return temporal.get(field);
        }
        return (int) field.range().getMinimum();
    }

    /**
     * {@link TemporalAccessor}转换为 时间戳（从1970-01-01T00:00:00Z开始的毫秒数）<br>
     * 如果为{@link Month}，调用{@link Month#getValue()}
     * @param temporal 时间对象
     * @return {@link Instant}对象
     */
    public static long toEpochMilli(TemporalAccessor temporal) {
        if (temporal instanceof Month) {
            return ((Month) temporal).getValue();
        }
        return toInstant(temporal).toEpochMilli();
    }

    /**
     * 将{@link TemporalAccessor}转换为 {@link LocalDateTime}对象。<br>
     * 可以处理一些 {@code LocalDateTime.from(TemporalAccessor)}不能返回结果的情况。<br>
     * @param temporal 时间对象
     * @return 日期时间对象{@code LocalDateTime}
     * @see LocalDateTime#from(TemporalAccessor)
     */
    public static LocalDateTime toLocalDateTime(TemporalAccessor temporal) {
        if (temporal == null) {
            return null;
        }

        if (temporal instanceof Instant) {
            return LocalDateTime.ofInstant((Instant) temporal, ZoneUtil.getDefaultZoneId());
        }

        if (temporal instanceof LocalDate) {
            return ((LocalDate) temporal).atStartOfDay();
        }

        try {
            return LocalDateTime.from(temporal);
        } catch (DateTimeException e) {
            // ignore
        }

        try {
            return ZonedDateTime.from(temporal).toLocalDateTime();
        } catch (final Exception ignore) {
            // ignore
        }

        try {
            return LocalDateTime.ofInstant(Instant.from(temporal), ZoneUtil.getDefaultZoneId());
        } catch (DateTimeException e) {
            // ignore
        }

        return LocalDateTime.of(//
                get(temporal, ChronoField.YEAR), // 年
                get(temporal, ChronoField.MONTH_OF_YEAR), // 月
                get(temporal, ChronoField.DAY_OF_MONTH), // 日
                get(temporal, ChronoField.HOUR_OF_DAY), // 时
                get(temporal, ChronoField.MINUTE_OF_HOUR), // 分
                get(temporal, ChronoField.SECOND_OF_MINUTE), // 秒
                get(temporal, ChronoField.NANO_OF_SECOND)// 纳秒
        );
    }

    /**
     * 将{@link TemporalAccessor}转换为 {@link ZonedDateTime}对象。<br>
     * 可以处理一些 {@code ZonedDateTime.from(TemporalAccessor)}不能返回结果的情况。<br>
     * @param temporal 时间对象
     * @return 带时区的日期时间对象{@code ZonedDateTime}
     * @see ZonedDateTime#from(TemporalAccessor)
     */
    public static ZonedDateTime toZonedDateTime(TemporalAccessor temporal) {
        if (temporal == null) {
            return null;
        }

        if (temporal instanceof Instant) {
            return ZonedDateTime.ofInstant((Instant) temporal, ZoneUtil.getDefaultZoneId());
        }
        if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).atZone(ZoneUtil.getDefaultZoneId());
        }

        try {
            return ZonedDateTime.from(temporal);
        } catch (DateTimeException e) {
            // ignore
        }
        try {
            return ZonedDateTime.ofInstant(Instant.from(temporal), ZoneUtil.getDefaultZoneId());
        } catch (DateTimeException e2) {
            // ignore
        }

        return ZonedDateTime.of(//
                get(temporal, ChronoField.YEAR), // 年
                get(temporal, ChronoField.MONTH_OF_YEAR), // 月
                get(temporal, ChronoField.DAY_OF_MONTH), // 日
                get(temporal, ChronoField.HOUR_OF_DAY), // 时
                get(temporal, ChronoField.MINUTE_OF_HOUR), // 分
                get(temporal, ChronoField.SECOND_OF_MINUTE), // 秒
                get(temporal, ChronoField.NANO_OF_SECOND), // 纳秒
                ZoneUtil.getDefaultZoneId() // 时区
        );
    }

    /**
     * {@link TemporalAccessor}转换为 {@link Instant}对象<br>
     * 此方法会最大限度进行转化处理，尽量不抛出发异常：<br>
     * 如果入参为空，则会直接返回null。<br>
     * 如果TemporalAccessor缺失时区信息，则会使用默认的时区。<br>
     * 如果TemporalAccessor缺失日期信息，则会补偿默认的日期“1970-01-01”。<br>
     * 如果TemporalAccessor缺失时间信息，则会补偿默认的时间“00:00:00”。<br>
     * 但是如果所有关键信息都缺失，则会抛出 DateTimeException 异常<br>
     * @param temporal 时间对象
     * @return {@link Instant}对象
     */
    public static Instant toInstant(TemporalAccessor temporal) {
        if (temporal == null) {
            return null;
        }
        if (temporal instanceof Instant) {
            return (Instant) temporal;
        }
        if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).atZone(ZoneUtil.getDefaultZoneId()).toInstant();
        }
        if (temporal instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporal).toInstant();
        }
        if (temporal instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporal).toInstant();
        }
        if (temporal instanceof LocalDate) {
            return ((LocalDate) temporal).atStartOfDay(ZoneUtil.getDefaultZoneId()).toInstant();
        }
        if (temporal instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            return ((LocalTime) temporal).atDate(LocalDate.now()).atZone(ZoneUtil.getDefaultZoneId()).toInstant();
        }
        if (temporal instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            return ((OffsetTime) temporal).atDate(LocalDate.now()).toInstant();
        }
        // Instant
        try {
            return Instant.from(temporal);
        } catch (Exception e) {
            ZoneId defaultZone = ZoneUtil.getDefaultZoneId();
            // 包含日期部分
            boolean hasDate = temporal.isSupported(ChronoField.YEAR) //
                    && temporal.isSupported(ChronoField.MONTH_OF_YEAR)//
                    && temporal.isSupported(ChronoField.DAY_OF_MONTH);
            // 包含时间部分
            boolean hasTime = temporal.isSupported(ChronoField.HOUR_OF_DAY)//
                    && temporal.isSupported(ChronoField.MINUTE_OF_HOUR);
            // 包含时间偏移部分
            boolean hasOffset = temporal.isSupported(ChronoField.OFFSET_SECONDS);

            // 包含：日期、时间、偏移
            if (hasDate && hasTime && hasOffset) {
                return OffsetDateTime.from(temporal).toInstant();
            }
            // 包含：日期、时间
            else if (hasDate && hasTime) {
                return LocalDateTime.from(temporal).atZone(defaultZone).toInstant();
            }
            // 包含：日期
            else if (hasDate) {
                return LocalDate.from(temporal).atStartOfDay(defaultZone).toInstant();
            }
            // 包含：时间、偏移
            else if (hasTime && hasOffset) {
                // 需要补偿日期部分（为了统一处理，补偿日期为DEFAULT_DATE）
                return OffsetTime.from(temporal).atDate(DEFAULT_EPOCH_DATE).toInstant();
            }
            // 包含：时间
            else if (hasTime) {
                // 只有时间，无偏移也无日期：补默认日期 + 默认时区
                LocalTime time = LocalTime.from(temporal);
                LocalDateTime ldt = LocalDateTime.of(DEFAULT_EPOCH_DATE, time);
                return ldt.atZone(defaultZone).toInstant();
            }
            // 实在无法处理了，只能抛出异常了
            else {
                throw new DateTimeException("Unsupported TemporalAccessor: missing required fields");
            }
        }
    }

    /**
     * 格式化日期时间为指定格式<br>
     * 如果为{@link Month}，调用{@link Month#toString()}
     * @param temporal  {@link TemporalAccessor}
     * @param formatter 日期格式化器，预定义的格式见：{@link DateTimeFormatter}
     * @return 格式化后的字符串
     */
    public static String format(TemporalAccessor temporal, DateTimeFormatter formatter) {

        if (temporal == null) {
            return null;
        }

        if (temporal instanceof DayOfWeek || temporal instanceof Month || temporal instanceof Era
                || temporal instanceof MonthDay) {
            return temporal.toString();
        }

        if (formatter == null) {
            formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }

        try {
            return formatter.format(temporal);
        } catch (UnsupportedTemporalTypeException e) {
            if (temporal instanceof LocalDate && e.getMessage().contains("HourOfDay")) {
                // 用户传入LocalDate，但是要求格式化带有时间部分，转换为LocalDateTime重试
                return formatter.format(((LocalDate) temporal).atStartOfDay());
            } else if (temporal instanceof LocalTime && e.getMessage().contains("YearOfEra")) {
                // 用户传入LocalTime，但是要求格式化带有日期部分，转换为LocalDateTime重试
                return formatter.format(((LocalTime) temporal).atDate(LocalDate.now()));
            } else if (temporal instanceof Instant) {
                // 时间戳没有时区信息，赋予默认时区
                return formatter.format(((Instant) temporal).atZone(ZoneUtil.getDefaultZoneId()));
            }
            throw e;
        }
    }

    /**
     * 使用指定的 时间格式解析时间格式字符串
     * @param text    时间文本
     * @param pattern 时间格式
     * @return 时间对象{@code TemporalAccessor}
     */
    public static final TemporalAccessor parse(String text, String pattern) {
        return StringUtil.isEmpty(text) ? null : DateTimeFormatter.ofPattern(pattern).parse(text);
    }

    /**
     * 使用指定的格式化器解析时间格式字符串
     * @param text      时间文本
     * @param formatter 时间格式化器
     * @return 时间对象{@code TemporalAccessor}
     */
    public static final TemporalAccessor parse(String text, DateTimeFormatter formatter) {
        return StringUtil.isEmpty(text) ? null : formatter.parse(text);
    }

    /**
     * 解析日期格式字符串<br>
     * 会通过尝试各种不同时间格式的解析器来解析时间字符串，如果最终依旧无法解析则返回{@code null}
     * @param text 时间文本
     * @return 时间对象{@code TemporalAccessor}
     */
    public static TemporalAccessor parse(String text) {
        if (StringUtil.isBlank(text)) {
            return null;
        }
        for (DateTimeFormatter formatter : DATE_TIME_FORMATTERS) {
            try {
                return formatter.parse(text);
            } catch (Exception e) {
                // ignore
            }
        }
        return null;
    }
}
