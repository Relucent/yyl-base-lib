package com.github.relucent.base.common.time;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

/**
 * {@link Temporal} 工具类封装
 */
public class TemporalUtil {

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。 <br>
     * @param startTime 起始时刻（包括在内）
     * @param endTime 结束时间（不包含）
     * @return 时间差 {@link Duration}对象
     */
    public static Duration between(Temporal startTime, Temporal endTime) {
        return Duration.between(startTime, endTime);
    }

    /**
     * 获取两个日期的差，如果结束时间早于开始时间，获取结果为负。
     * @param startTime 起始时刻（包括在内）
     * @param endTime 结束时间（不包含）
     * @param unit 计时单位
     * @return 时间差
     */
    public static long between(Temporal startTime, Temporal endTime, ChronoUnit unit) {
        return unit.between(startTime, endTime);
    }

    /**
     * 时间偏移
     * @param <T> 时间对象类型，如{@code LocalDate}或{@code LocalDateTime}
     * @param time {@link Temporal} 时间对象
     * @param amountToAdd 偏移量，正数为向后偏移，负数为向前偏移
     * @param unit 偏移单位，见{@link ChronoUnit}，不能为null
     * @return 偏移后的时间对象
     */
    @SuppressWarnings("unchecked")
    public static <T extends Temporal> T offset(T time, long amountToAdd, TemporalUnit unit) {
        return time == null ? null : (T) time.plus(amountToAdd, unit);
    }

    /**
     * 偏移到指定的周几
     * @param temporal 日期或者日期时间
     * @param dayOfWeek 周几
     * @param <T> 时间对象类型，如{@code LocalDate}或{@code LocalDateTime}
     * @param isPrevious 偏移方向，{@code true}向前偏移，{@code false}向后偏移。
     * @return 偏移后的时间对象
     */
    @SuppressWarnings("unchecked")
    public <T extends Temporal> T offset(T temporal, DayOfWeek dayOfWeek, boolean isPrevious) {
        return (T) temporal.with(isPrevious ? TemporalAdjusters.previous(dayOfWeek) : TemporalAdjusters.next(dayOfWeek));
    }

    /**
     * 将 {@link TimeUnit} 转换为 {@link ChronoUnit}.
     * @param unit 被转换的{@link TimeUnit}单位，如果为{@code null}返回{@code null}
     * @return {@link ChronoUnit}
     */
    public static ChronoUnit toChronoUnit(TimeUnit unit) throws IllegalArgumentException {
        if (unit == null) {
            return null;
        }
        switch (unit) {
        case DAYS:
            return ChronoUnit.DAYS;
        case HOURS:
            return ChronoUnit.HOURS;
        case MINUTES:
            return ChronoUnit.MINUTES;
        case SECONDS:
            return ChronoUnit.SECONDS;
        case MILLISECONDS:
            return ChronoUnit.MILLIS;
        case MICROSECONDS:
            return ChronoUnit.MICROS;
        case NANOSECONDS:
            return ChronoUnit.NANOS;
        default:
            throw new IllegalArgumentException("TimeUnit cannot be converted to ChronoUnit: " + unit);
        }
    }

    /**
     * 转换 {@link ChronoUnit} 到 {@link TimeUnit}.
     * @param unit {@link ChronoUnit}，如果为{@code null}返回{@code null}
     * @return {@link TimeUnit}
     * @throws IllegalArgumentException 如果{@link TimeUnit}没有对应单位抛出
     */
    public static TimeUnit toTimeUnit(ChronoUnit unit) throws IllegalArgumentException {
        if (unit == null) {
            return null;
        }
        switch (unit) {
        case DAYS:
            return TimeUnit.DAYS;
        case HOURS:
            return TimeUnit.HOURS;
        case MINUTES:
            return TimeUnit.MINUTES;
        case SECONDS:
            return TimeUnit.SECONDS;
        case MILLIS:
            return TimeUnit.MILLISECONDS;
        case MICROS:
            return TimeUnit.MICROSECONDS;
        case NANOS:
            return TimeUnit.NANOSECONDS;
        default:
            throw new IllegalArgumentException("ChronoUnit cannot be converted to TimeUnit: " + unit);
        }
    }
}
