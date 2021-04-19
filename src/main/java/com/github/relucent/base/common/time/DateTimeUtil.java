package com.github.relucent.base.common.time;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeUtil {

    /**
     * 工具类方法，实例不应在标准编程中构造。
     */
    protected DateTimeUtil() {
    }

    /**
     * 日期时间转换，将{@code Date}转换为{@code LocalDateTime}
     * @param date 日期类型 {@code Date}
     * @return 日期时间类型{@code LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
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
}
