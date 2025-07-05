package com.github.relucent.base.common.constant;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * 通用时间单位常量定义<br>
 * @see ChronoUnit
 */
public class TemporalUnitConstant {

    /** 秒 */
    public static final TemporalUnit SECONDS = ChronoUnit.SECONDS;
    /** 分 */
    public static final TemporalUnit MINUTES = ChronoUnit.MINUTES;
    /** 时 */
    public static final TemporalUnit HOURS = ChronoUnit.HOURS;
    /** 天 */
    public static final TemporalUnit DAYS = ChronoUnit.DAYS;
    /** 周 */
    public static final TemporalUnit WEEKS = ChronoUnit.WEEKS;
    /** 月 */
    public static final TemporalUnit MONTHS = ChronoUnit.MONTHS;
    /** 年 */
    public static final TemporalUnit YEARS = ChronoUnit.YEARS;

    private TemporalUnitConstant() {
    }
}
