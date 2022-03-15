package com.github.relucent.base.common.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalField;

/**
 * {@link TemporalAccessor} 工具类
 */
public class TemporalAccessorUtil {

    /**
     * 安全获取时间的某个属性，属性不存在返回默认值
     * @param temporalAccessor 时间对象
     * @param field 需要获取的属性
     * @return 时间的值，如果无法获取则默认值
     */
    public static int get(TemporalAccessor temporalAccessor, TemporalField field) {
        if (temporalAccessor.isSupported(field)) {
            return temporalAccessor.get(field);
        }
        return (int) field.range().getMinimum();
    }

    /**
     * {@link TemporalAccessor}转换为 时间戳（从1970-01-01T00:00:00Z开始的毫秒数）<br>
     * 如果为{@link Month}，调用{@link Month#getValue()}
     * @param temporalAccessor 时间对象
     * @return {@link Instant}对象
     */
    public static long toEpochMilli(TemporalAccessor temporalAccessor) {
        if (temporalAccessor instanceof Month) {
            return ((Month) temporalAccessor).getValue();
        }
        return toInstant(temporalAccessor).toEpochMilli();
    }

    /**
     * {@link TemporalAccessor}转换为 {@link LocalDateTime}对象。<br>
     * 可以处理一些 {@code LocalDateTime.from(TemporalAccessor)}不能返回结果的情况。<br>
     * @param temporalAccessor 时间对象
     * @return 日期时间对象{@code LocalDateTime}
     * @see LocalDateTime#from(TemporalAccessor)
     */
    public static LocalDateTime toLocalDateTime(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }
        return LocalDateTime.of(//
                get(temporalAccessor, ChronoField.YEAR), // 年
                get(temporalAccessor, ChronoField.MONTH_OF_YEAR), // 月
                get(temporalAccessor, ChronoField.DAY_OF_MONTH), // 日
                get(temporalAccessor, ChronoField.HOUR_OF_DAY), // 时
                get(temporalAccessor, ChronoField.MINUTE_OF_HOUR), // 分
                get(temporalAccessor, ChronoField.SECOND_OF_MINUTE), // 秒
                get(temporalAccessor, ChronoField.NANO_OF_SECOND)// 纳秒
        );
    }

    /**
     * {@link TemporalAccessor}转换为 {@link Instant}对象
     * @param temporalAccessor 时间对象
     * @return {@link Instant}对象
     */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        if (temporalAccessor == null) {
            return null;
        }
        if (temporalAccessor instanceof Instant) {
            return (Instant) temporalAccessor;
        }
        if (temporalAccessor instanceof LocalDateTime) {
            return ((LocalDateTime) temporalAccessor).atZone(ZoneId.systemDefault()).toInstant();
        }
        if (temporalAccessor instanceof ZonedDateTime) {
            return ((ZonedDateTime) temporalAccessor).toInstant();
        }
        if (temporalAccessor instanceof OffsetDateTime) {
            return ((OffsetDateTime) temporalAccessor).toInstant();
        }
        if (temporalAccessor instanceof LocalDate) {
            return ((LocalDate) temporalAccessor).atStartOfDay(ZoneId.systemDefault()).toInstant();
        }
        if (temporalAccessor instanceof LocalTime) {
            // 指定本地时间转换 为Instant，取当天日期
            return ((LocalTime) temporalAccessor).atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant();
        }
        if (temporalAccessor instanceof OffsetTime) {
            // 指定本地时间转换 为Instant，取当天日期
            return ((OffsetTime) temporalAccessor).atDate(LocalDate.now()).toInstant();
        }
        // 先转换成 LocalDateTime，从而处理一些 Instant.from(temporalAccessor)不能处理的情况
        return toInstant(toLocalDateTime(temporalAccessor));
    }
}
