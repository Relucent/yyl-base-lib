package com.github.relucent.base.common.constant;

import java.time.ZoneId;

/**
 * ZoneId 常量
 */
public class ZoneIdConstant {

    /** 世界统一时间（Universal Time Coordinated） */
    public static final ZoneId UTC = ZoneId.of("UTC");

    /** 格林尼治标准时间 （UTC+0） */
    public static final ZoneId GMT = ZoneId.of("GMT");

    /** 东八时区（UTC+08:00） */
    public static final ZoneId UTC_PLUS_8 = ZoneId.of("+08:00");

    private ZoneIdConstant() {
    }
}
