package com.github.relucent.base.common.time;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.github.relucent.base.common.lang.ObjectUtil;

/**
 * {@link Duration}工具类
 */
public class DurationUtil {

    /**
     * 获取{@link Duration} 的纳秒部分<br>
     * {@link Duration} 对象由于秒和纳秒组成，{@link Duration#getNano()} 获得的是纳秒部分。<br>
     * 但是本方法只获取的是毫秒以下的纳秒部分（不包含毫秒部分）
     * @param duration 持续时间对象
     * @return 纳秒（介于0和999999之间）
     */
    public static int getNanosOfMiili(final Duration duration) {
        return duration.getNano() % 1_000_000;
    }

    /**
     * 判断持续时间是否为正(&gt;0).
     * @param duration 测试的值
     * @return 持续时间是否为正(&gt;0).
     */
    public static boolean isPositive(final Duration duration) {
        return !duration.isNegative() && !duration.isZero();
    }

    /**
     * 将{@link TimeUnit}转换为 {@link ChronoUnit}.
     * @param timeUnit TimeUnit
     * @return 转换后的ChronoUnit
     */
    static ChronoUnit toChronoUnit(final TimeUnit timeUnit) {
        switch (Objects.requireNonNull(timeUnit)) {
        case NANOSECONDS:
            return ChronoUnit.NANOS;
        case MICROSECONDS:
            return ChronoUnit.MICROS;
        case MILLISECONDS:
            return ChronoUnit.MILLIS;
        case SECONDS:
            return ChronoUnit.SECONDS;
        case MINUTES:
            return ChronoUnit.MINUTES;
        case HOURS:
            return ChronoUnit.HOURS;
        case DAYS:
            return ChronoUnit.DAYS;
        default:
            throw new IllegalArgumentException(timeUnit.toString());
        }
    }

    /**
     * 将数量和时间单位转换为持续时间
     * @param amount 持续时间的大小，以单位为单位，正数或负数
     * @param timeUnit 时间单位
     * @return {@link Duration}
     */
    public static Duration toDuration(final long amount, final TimeUnit timeUnit) {
        return Duration.of(amount, toChronoUnit(timeUnit));
    }

    /**
     * 返回时间对象的非null值，如果为null，则返回{@link Duration#ZERO}
     * @param duration 时间对象{@link Duration}
     * @return 给定的时间对象（不为null）
     */
    public static Duration zeroIfNull(final Duration duration) {
        return ObjectUtil.defaultIfNull(duration, Duration.ZERO);
    }

}
