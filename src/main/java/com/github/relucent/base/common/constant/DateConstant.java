package com.github.relucent.base.common.constant;

/**
 * 时间常量
 */
public class DateConstant {

    /**
     * 一个标准秒的毫秒数
     */
    public static final long MILLIS_PER_SECOND = 1000;
    /**
     * 一个标准分钟的毫秒数
     */
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;
    /**
     * 一个标准小时的毫秒数
     */
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;
    /**
     * 一个标准日的毫秒数
     */
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    private DateConstant() {
    }
}
