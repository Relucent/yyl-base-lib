package com.github.relucent.base.common.time;

/**
 * 时间单位
 */
public enum DateUnit {
    /** 年份 */
    YEAR,
    /** 半年 */
    HALFYEAR,
    /** 季度 */
    QUARTER,
    /** 月份 */
    MONTH,
    /** 日期 */
    DATE,
    /** 小时(0-23) */
    HOUR_OF_DAY,
    /** 分钟(0-59) */
    MINUTE,
    /** 秒钟(0-59) */
    SECOND,
    /**
     * 周<br>
     * 注意：该枚举值追加在末尾而不是按时间粒度插入中间，<br>
     * 因为向枚举中间插入值会改变已有枚举值的 {@code ordinal}，<br>
     * 破坏依赖 {@code ordinal} 的已编译代码（如编译期生成的 switch 映射表）。
     */
    WEEK;
}
